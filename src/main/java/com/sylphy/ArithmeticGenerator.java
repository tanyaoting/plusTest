package com.sylphy;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.model.GradingReport;
import com.sylphy.model.ProblemBatch;
import com.sylphy.model.ProblemRecord;
import com.sylphy.model.StudentAnswerRecord;
import com.sylphy.reader.ProblemCsvReader;
import com.sylphy.reader.StudentAnswerCsvReader;
import com.sylphy.service.ArithmeticProblemGenerator;
import com.sylphy.service.GradingService;
import com.sylphy.writer.GradingReportWriter;
import com.sylphy.writer.ProblemFileWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * 应用程序入口，负责生成练习 CSV，或读取练习和作答 CSV 完成自动批改。
 * @author apple
 */
public class ArithmeticGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            generateProblems(null);
            return;
        }
        if (args.length == 4 && "grade".equals(args[0])) {
            grade(Path.of(args[1]), Path.of(args[2]), Path.of(args[3]));
            return;
        }
        if (args.length == 1 && !"grade".equals(args[0])) {
            Integer questionCount = parseQuestionCount(args[0]);
            if (questionCount != null) {
                generateProblems(questionCount);
            }
            return;
        }
        printUsage();
    }

    private static void generateProblems(Integer questionCount) throws IOException {
        GeneratorConfig config = GeneratorConfig.loadDefault();
        if (questionCount != null) {
            config = new GeneratorConfig(
                    questionCount,
                    config.minValue(),
                    config.maxValue(),
                    config.outputPath(),
                    config.answerOutputPath(),
                    config.strategies()
            );
        }
        ArithmeticProblemGenerator generator = ArithmeticProblemGenerator.Factory.createDefault();
        ProblemBatch problems = generator.generate(config);

        new ProblemFileWriter().write(problems, config.outputPath(), config.answerOutputPath());
        System.out.println("已生成 " + problems.size() + " 道题，题目文件：" + config.outputPath());
        System.out.println("已生成标准答案文件：" + config.answerOutputPath());
    }

    private static void grade(Path problemPath, Path studentAnswerPath, Path resultPath) throws IOException {
        GeneratorConfig config = GeneratorConfig.loadDefault();
        List<ProblemRecord> problems = new ProblemCsvReader(config.strategies()).read(problemPath);
        List<StudentAnswerRecord> answers = new StudentAnswerCsvReader().read(studentAnswerPath);
        GradingReport report = new GradingService().grade(problems, answers);

        new GradingReportWriter().write(report, resultPath);
        System.out.println("已批改 " + report.totalCount() + " 道题。");
        System.out.println("正确数量：" + report.correctCount() + "/" + report.totalCount());
        System.out.println("得分：" + report.score());
        System.out.println("批改结果已保存到：" + resultPath);
    }

    private static Integer parseQuestionCount(String value) {
        try {
            int questionCount = Integer.parseInt(value);
            if (questionCount <= 0) {
                System.out.println("题目数量必须大于 0。");
                printUsage();
                return null;
            }
            return questionCount;
        } catch (NumberFormatException exception) {
            System.out.println("题目数量必须是整数：" + value);
            printUsage();
            return null;
        }
    }

    private static void printUsage() {
        System.out.println("用法：");
        System.out.println("  java com.sylphy.ArithmeticGenerator");
        System.out.println("  java com.sylphy.ArithmeticGenerator <题目数量>");
        System.out.println("  java com.sylphy.ArithmeticGenerator grade <题目文件.csv> <学生答案.csv> <批改结果.csv>");
    }
}
