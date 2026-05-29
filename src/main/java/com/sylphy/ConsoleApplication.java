package com.sylphy;

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
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * 控制台菜单应用，整合练习生成、录入、批改和机器练习功能。
 * @author apple
 */
public final class ConsoleApplication {
    private static final Path DEFAULT_SELECTED_PROBLEM_PATH = Path.of("output", "selected-problems.csv");
    private static final Path DEFAULT_SELECTED_ANSWER_PATH = Path.of("output", "selected-answers.csv");
    private static final Path DEFAULT_STUDENT_ANSWER_PATH = Path.of("output", "student-answers.csv");
    private static final Path DEFAULT_RESULT_PATH = Path.of("output", "results.csv");
    private static final int DEFAULT_INTERACTIVE_COUNT = 10;

    private final Scanner input;
    private final PrintStream output;
    private final ArithmeticProblemGenerator generator;
    private final ProblemFileWriter problemWriter;
    private final GradingReportWriter reportWriter;
    private final GradingService gradingService;
    private final ConfigLoader configLoader;

    public ConsoleApplication(InputStream inputStream, PrintStream output) {
        this(
                new Scanner(Objects.requireNonNull(inputStream, "inputStream must not be null")),
                output,
                ArithmeticProblemGenerator.Factory.createDefault(),
                new ProblemFileWriter(),
                new GradingReportWriter(),
                new GradingService(),
                GeneratorConfig::loadDefault
        );
    }

    ConsoleApplication(
            Scanner input,
            PrintStream output,
            ArithmeticProblemGenerator generator,
            ProblemFileWriter problemWriter,
            GradingReportWriter reportWriter,
            GradingService gradingService,
            ConfigLoader configLoader
    ) {
        this.input = Objects.requireNonNull(input, "input must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
        this.generator = Objects.requireNonNull(generator, "generator must not be null");
        this.problemWriter = Objects.requireNonNull(problemWriter, "problemWriter must not be null");
        this.reportWriter = Objects.requireNonNull(reportWriter, "reportWriter must not be null");
        this.gradingService = Objects.requireNonNull(gradingService, "gradingService must not be null");
        this.configLoader = Objects.requireNonNull(configLoader, "configLoader must not be null");
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine("请选择......");
            try {
                switch (choice) {
                    case "1" -> generateBatchProblems();
                    case "2" -> selectAndPrintProblems();
                    case "3" -> editStudentAnswers();
                    case "4" -> gradeSelectedPractice();
                    case "5" -> gradeBatchPractice();
                    case "6" -> completePracticeOnComputer();
                    case "0" -> {
                        output.println("已退出。");
                        running = false;
                    }
                    default -> output.println("无效选择，请输入菜单前面的数字。");
                }
            } catch (IOException | RuntimeException exception) {
                output.println("执行失败：" + exception.getMessage());
            }
            output.println();
        }
    }

    private void printMenu() {
        output.println("100以内的口算练习程序");
        output.println("============================================================");
        output.println("功能列表（请输入功能前面对应的数字，按回车键执行）：");
        output.println("------------------------------------------------------------");
        output.println("1. 批量产生练习题");
        output.println("2. 选择并打印练习题");
        output.println("3. 编辑答题结果并保存");
        output.println("4. 选择一次练习并批改");
        output.println("5. 批量批改所有的题目");
        output.println("6. 选择一套练习并在机器完成");
        output.println("0. 退出");
        output.println("============================================================");
    }

    private void generateBatchProblems() throws IOException {
        GeneratorConfig config = configLoader.load();
        int questionCount = readOptionalPositiveInt("请输入题目数量（直接回车使用配置值 "
                + config.questionCount() + "）：", config.questionCount());
        GeneratorConfig actualConfig = withQuestionCount(config, questionCount);
        ProblemBatch problems = generator.generate(actualConfig);
        problemWriter.write(problems, actualConfig.outputPath(), actualConfig.answerOutputPath());
        output.println("已生成 " + problems.size() + " 道题。");
        output.println("题目文件：" + actualConfig.outputPath());
        output.println("标准答案文件：" + actualConfig.answerOutputPath());
    }

    private void selectAndPrintProblems() throws IOException {
        GeneratorConfig config = configLoader.load();
        Path sourcePath = readPath("请输入题目文件路径", config.outputPath());
        List<ProblemRecord> records = readProblems(sourcePath, config);
        int startIndex = readPositiveInt("请输入起始题号：");
        int count = readPositiveInt("请输入选择题目数量：");
        List<ProblemRecord> selectedRecords = selectRecords(records, startIndex, count);
        ProblemBatch selectedProblems = toProblemBatch(selectedRecords);

        Path problemPath = readPath("请输入保存选择题目的路径", DEFAULT_SELECTED_PROBLEM_PATH);
        Path answerPath = readPath("请输入保存选择题目答案的路径", DEFAULT_SELECTED_ANSWER_PATH);
        problemWriter.write(selectedProblems, problemPath, answerPath);

        output.println("已选择并保存 " + selectedProblems.size() + " 道题。");
        output.println("可打印题目文件：" + problemPath);
        for (ProblemRecord record : selectedRecords) {
            output.println(record.index() + ". " + record.expression());
        }
    }

    private void editStudentAnswers() throws IOException {
        GeneratorConfig config = configLoader.load();
        Path problemPath = readPath("请输入题目文件路径", config.outputPath());
        List<ProblemRecord> problems = readProblems(problemPath, config);
        List<List<String>> rows = new ArrayList<>(problems.size() + 1);
        rows.add(List.of("index", "studentAnswer"));
        for (ProblemRecord problem : problems) {
            int answer = readInteger(problem.index() + ". " + problem.expression());
            rows.add(List.of(String.valueOf(problem.index()), String.valueOf(answer)));
        }

        Path answerPath = readPath("请输入保存答题结果的路径", DEFAULT_STUDENT_ANSWER_PATH);
        CsvFile.writeRows(answerPath, rows);
        output.println("答题结果已保存到：" + answerPath);
    }

    private void gradeSelectedPractice() throws IOException {
        GeneratorConfig config = configLoader.load();
        Path problemPath = readPath("请输入要批改的练习题文件路径", DEFAULT_SELECTED_PROBLEM_PATH);
        Path answerPath = readPath("请输入学生答案文件路径", DEFAULT_STUDENT_ANSWER_PATH);
        Path resultPath = readPath("请输入批改结果保存路径", DEFAULT_RESULT_PATH);
        grade(problemPath, answerPath, resultPath, config);
    }

    private void gradeBatchPractice() throws IOException {
        GeneratorConfig config = configLoader.load();
        Path problemPath = readPath("请输入批量题目文件路径", config.outputPath());
        Path answerPath = readPath("请输入批量学生答案文件路径", DEFAULT_STUDENT_ANSWER_PATH);
        Path resultPath = readPath("请输入批量批改结果保存路径", DEFAULT_RESULT_PATH);
        grade(problemPath, answerPath, resultPath, config);
    }

    private void completePracticeOnComputer() throws IOException {
        GeneratorConfig config = configLoader.load();
        Path problemPath = readPath("请输入机器练习题目文件路径", DEFAULT_SELECTED_PROBLEM_PATH);
        List<ProblemRecord> problems = readProblems(problemPath, config);
        int count = readOptionalPositiveInt("请输入机器练习题目数量（直接回车默认 "
                + Math.min(DEFAULT_INTERACTIVE_COUNT, problems.size()) + "）：",
                Math.min(DEFAULT_INTERACTIVE_COUNT, problems.size()));
        List<ProblemRecord> selectedProblems = problems.stream().limit(count).toList();
        List<StudentAnswerRecord> answers = new ArrayList<>(selectedProblems.size());

        output.println("开始练习。");
        for (ProblemRecord problem : selectedProblems) {
            int answer = readInteger(problem.index() + ". " + problem.expression());
            answers.add(new StudentAnswerRecord(problem.index(), answer));
        }

        GradingReport report = gradingService.grade(selectedProblems, answers);
        Path resultPath = readPath("请输入机器练习结果保存路径", DEFAULT_RESULT_PATH);
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
        output.println("已批改 " + report.totalCount() + " 道题。");
        output.println("正确数量：" + report.correctCount() + "/" + report.totalCount());
        output.println("得分：" + report.score());
        output.println("错题数量：" + report.wrongResults().size());
        output.println("批改结果已保存到：" + resultPath);
    }

    private List<ProblemRecord> readProblems(Path path, GeneratorConfig config) throws IOException {
        return new ProblemCsvReader(config.strategies()).read(path);
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

    private Path readPath(String prompt, Path defaultPath) {
        String line = readLine(prompt + "（直接回车默认 " + defaultPath + "）：");
        if (line.isBlank()) {
            return defaultPath;
        }
        return Path.of(line);
    }

    private int readOptionalPositiveInt(String prompt, int defaultValue) {
        String line = readLine(prompt);
        if (line.isBlank()) {
            return defaultValue;
        }
        Integer value = parseInteger(line);
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("请输入大于 0 的整数。");
        }
        return value;
    }

    private int readPositiveInt(String prompt) {
        int value = readInteger(prompt);
        if (value <= 0) {
            throw new IllegalArgumentException("请输入大于 0 的整数。");
        }
        return value;
    }

    private int readInteger(String prompt) {
        String line = readLine(prompt);
        Integer value = parseInteger(line);
        if (value == null) {
            throw new IllegalArgumentException("请输入整数。");
        }
        return value;
    }

    private String readLine(String prompt) {
        output.print(prompt);
        if (!input.hasNextLine()) {
            return "";
        }
        return input.nextLine().trim();
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @FunctionalInterface
    interface ConfigLoader {
        GeneratorConfig load() throws IOException;
    }
}
