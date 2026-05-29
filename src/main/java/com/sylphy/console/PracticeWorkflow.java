package com.sylphy.console;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.csv.CsvFile;
import com.sylphy.model.GradingReport;
import com.sylphy.model.ProblemBatch;
import com.sylphy.model.ProblemRecord;
import com.sylphy.model.StudentAnswerRecord;
import com.sylphy.model.arithmericproblem.ArithmeticProblem;
import com.sylphy.reader.ProblemCsvReader;
import com.sylphy.reader.StudentAnswerCsvReader;
import com.sylphy.service.ArithmeticProblemGenerator;
import com.sylphy.service.GradingService;
import com.sylphy.writer.GradingReportWriter;
import com.sylphy.writer.ProblemFileWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 控制台菜单背后的业务流程编排。
 *
 * @author apple
 */
public final class PracticeWorkflow {
    private static final int DEFAULT_INTERACTIVE_COUNT = 10;

    private final ConsoleIo io;
    private final ArithmeticProblemGenerator generator;
    private final ProblemFileWriter problemWriter;
    private final GradingReportWriter reportWriter;
    private final GradingService gradingService;
    private final ConfigLoader configLoader;
    private final PracticeFileLocator fileLocator;

    public PracticeWorkflow(
            ConsoleIo io,
            ArithmeticProblemGenerator generator,
            ProblemFileWriter problemWriter,
            GradingReportWriter reportWriter,
            GradingService gradingService,
            ConfigLoader configLoader,
            PracticeFileLocator fileLocator
    ) {
        this.io = Objects.requireNonNull(io, "io must not be null");
        this.generator = Objects.requireNonNull(generator, "generator must not be null");
        this.problemWriter = Objects.requireNonNull(problemWriter, "problemWriter must not be null");
        this.reportWriter = Objects.requireNonNull(reportWriter, "reportWriter must not be null");
        this.gradingService = Objects.requireNonNull(gradingService, "gradingService must not be null");
        this.configLoader = Objects.requireNonNull(configLoader, "configLoader must not be null");
        this.fileLocator = Objects.requireNonNull(fileLocator, "fileLocator must not be null");
    }

    public void generateBatchProblems() throws IOException {
        GeneratorConfig config = configLoader.load();
        int practiceCount = io.readOptionalPositiveInt("请输入练习套数（直接回车默认 3）：", 3);
        int questionCount = io.readOptionalPositiveInt("请输入题目数量（直接回车使用配置值 "
                + config.questionCount() + "）：", config.questionCount());
        Path outputDirectory = io.readPath("请输入批量练习保存目录", fileLocator.defaultBatchDirectory(config));
        GeneratorConfig actualConfig = withQuestionCount(config, questionCount);
        for (int practiceNumber = 1; practiceNumber <= practiceCount; practiceNumber++) {
            ProblemBatch problems = generator.generate(actualConfig);
            Path problemPath = fileLocator.batchProblemPath(outputDirectory, practiceNumber);
            Path answerPath = fileLocator.batchAnswerPath(outputDirectory, practiceNumber);
            problemWriter.write(problems, problemPath, answerPath);
            io.println("已生成第 " + practiceNumber + " 套练习：" + problemPath);
            io.println("标准答案文件：" + answerPath);
        }
        io.println("共生成 " + practiceCount + " 套练习，每套 " + questionCount + " 道题。");
    }

    public void selectAndPrintProblems() throws IOException {
        GeneratorConfig config = configLoader.load();
        Path sourcePath = io.readPath("请输入题目文件路径", fileLocator.firstBatchProblemPath(config));
        List<ProblemRecord> records = readProblems(sourcePath, config);
        int startIndex = io.readOptionalPositiveInt("请输入起始题号（直接回车默认 1）：", 1);
        int count = io.readOptionalPositiveInt("请输入选择题目数量（直接回车默认 "
                + records.size() + "）：", records.size());
        List<ProblemRecord> selectedRecords = selectRecords(records, startIndex, count);
        ProblemBatch selectedProblems = toProblemBatch(selectedRecords);

        Path problemPath = io.readPath("请输入保存选择题目的路径", fileLocator.defaultSelectedProblemPath(config));
        Path answerPath = io.readPath("请输入保存选择题目答案的路径", fileLocator.defaultSelectedAnswerPath(config));
        problemWriter.write(selectedProblems, problemPath, answerPath);

        io.println("已选择并保存 " + selectedProblems.size() + " 道题。");
        io.println("可打印题目文件：" + problemPath);
        for (ProblemRecord record : selectedRecords) {
            io.println(record.index() + ". " + record.expression());
        }
    }

    public void editStudentAnswers() throws IOException {
        GeneratorConfig config = configLoader.load();
        Path problemPath = io.readPath("请输入题目文件路径", fileLocator.defaultSelectedProblemPath(config));
        List<ProblemRecord> problems = readProblems(problemPath, config);
        List<List<String>> rows = new ArrayList<>(problems.size() + 1);
        rows.add(List.of("index", "studentAnswer"));
        for (ProblemRecord problem : problems) {
            int answer = io.readInteger(problem.index() + ". " + problem.expression());
            rows.add(List.of(String.valueOf(problem.index()), String.valueOf(answer)));
        }

        Path answerPath = io.readPath("请输入保存答题结果的路径", fileLocator.defaultStudentAnswerPath(config));
        CsvFile.writeRows(answerPath, rows);
        io.println("答题结果已保存到：" + answerPath);
    }

    public void gradeSelectedPractice() throws IOException {
        GeneratorConfig config = configLoader.load();
        Path problemPath = io.readPath("请输入要批改的练习题文件路径", fileLocator.defaultSelectedProblemPath(config));
        Path answerPath = io.readPath("请输入学生答案文件路径", fileLocator.defaultStudentAnswerPath(config));
        Path resultPath = io.readPath("请输入批改结果保存路径", fileLocator.defaultResultPath(config));
        grade(problemPath, answerPath, resultPath, config);
    }

    public void gradeBatchPractice() throws IOException {
        GeneratorConfig config = configLoader.load();
        Path batchDirectory = io.readPath("请输入批量练习目录", fileLocator.defaultBatchDirectory(config));
        List<Path> problemPaths = listBatchProblemPaths(batchDirectory);
        int gradedCount = 0;
        for (Path problemPath : problemPaths) {
            Path answerPath = fileLocator.batchStudentAnswerPath(problemPath);
            if (!Files.exists(answerPath)) {
                io.println("跳过 " + problemPath + "，未找到学生答案文件：" + answerPath);
                continue;
            }
            Path resultPath = fileLocator.batchResultPath(problemPath);
            grade(problemPath, answerPath, resultPath, config);
            gradedCount++;
        }
        io.println("批量批改完成，共批改 " + gradedCount + " 套练习。");
    }

    public void completePracticeOnComputer() throws IOException {
        GeneratorConfig config = configLoader.load();
        Path problemPath = io.readPath("请输入机器练习题目文件路径", fileLocator.defaultSelectedProblemPath(config));
        List<ProblemRecord> problems = readProblems(problemPath, config);
        int count = io.readOptionalPositiveInt("请输入机器练习题目数量（直接回车默认 "
                + Math.min(DEFAULT_INTERACTIVE_COUNT, problems.size()) + "）：",
                Math.min(DEFAULT_INTERACTIVE_COUNT, problems.size()));
        List<ProblemRecord> selectedProblems = problems.stream().limit(count).toList();
        List<StudentAnswerRecord> answers = new ArrayList<>(selectedProblems.size());

        io.println("开始练习。");
        for (ProblemRecord problem : selectedProblems) {
            int answer = io.readInteger(problem.index() + ". " + problem.expression());
            answers.add(new StudentAnswerRecord(problem.index(), answer));
        }

        GradingReport report = gradingService.grade(selectedProblems, answers);
        Path resultPath = io.readPath("请输入机器练习结果保存路径", fileLocator.defaultResultPath(config));
        reportWriter.write(report, resultPath);
        printReport(report, resultPath);
    }

    private void grade(Path problemPath, Path answerPath, Path resultPath, GeneratorConfig config) throws IOException {
        List<ProblemRecord> problems = readProblems(problemPath, config);
        List<StudentAnswerRecord> answers = new StudentAnswerCsvReader().read(answerPath);
        GradingReport report = gradingService.grade(problems, answers);
        reportWriter.write(report, resultPath);
        printReport(report, resultPath);
    }

    private void printReport(GradingReport report, Path resultPath) {
        io.println("已批改 " + report.totalCount() + " 道题。");
        io.println("正确数量：" + report.correctCount() + "/" + report.totalCount());
        io.println("得分：" + report.score());
        io.println("错题数量：" + report.wrongResults().size());
        io.println("批改结果已保存到：" + resultPath);
    }

    private List<ProblemRecord> readProblems(Path path, GeneratorConfig config) throws IOException {
        return new ProblemCsvReader(config.strategies()).read(path);
    }

    private List<Path> listBatchProblemPaths(Path batchDirectory) throws IOException {
        if (!Files.isDirectory(batchDirectory)) {
            throw new IllegalArgumentException("批量练习目录不存在：" + batchDirectory);
        }
        try (Stream<Path> paths = Files.list(batchDirectory)) {
            List<Path> problemPaths = paths
                    .filter(path -> path.getFileName().toString().endsWith("-problems.csv"))
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
            if (problemPaths.isEmpty()) {
                throw new IllegalArgumentException("批量练习目录中没有找到题目文件。");
            }
            return problemPaths;
        }
    }

    private ProblemBatch toProblemBatch(List<ProblemRecord> records) {
        List<ArithmeticProblem> problems = new ArrayList<>(records.size());
        for (ProblemRecord record : records) {
            problems.add(record.strategy().create(record.left(), record.right()));
        }
        return new ProblemBatch(problems);
    }

    private List<ProblemRecord> selectRecords(List<ProblemRecord> records, int startIndex, int count) {
        List<ProblemRecord> selectedRecords = records.stream()
                .filter(record -> record.index() >= startIndex)
                .limit(count)
                .toList();
        if (selectedRecords.isEmpty()) {
            throw new IllegalArgumentException("没有找到可选择的题目。");
        }
        return selectedRecords;
    }

    private GeneratorConfig withQuestionCount(GeneratorConfig config, int questionCount) {
        return new GeneratorConfig(
                questionCount,
                config.minValue(),
                config.maxValue(),
                config.outputPath(),
                config.answerOutputPath(),
                config.strategies()
        );
    }
}
