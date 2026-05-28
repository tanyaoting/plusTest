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
        System.out.println("Generated " + problems.size() + " problems to " + config.outputPath());
        System.out.println("Generated answers to " + config.answerOutputPath());
    }

    private static void grade(Path problemPath, Path studentAnswerPath, Path resultPath) throws IOException {
        GeneratorConfig config = GeneratorConfig.loadDefault();
        List<ProblemRecord> problems = new ProblemCsvReader(config.strategies()).read(problemPath);
        List<StudentAnswerRecord> answers = new StudentAnswerCsvReader().read(studentAnswerPath);
        GradingReport report = new GradingService().grade(problems, answers);

        new GradingReportWriter().write(report, resultPath);
        System.out.println("Graded " + report.totalCount() + " problems.");
        System.out.println("Correct: " + report.correctCount() + "/" + report.totalCount());
        System.out.println("Score: " + report.score());
        System.out.println("Saved results to " + resultPath);
    }

    private static Integer parseQuestionCount(String value) {
        try {
            int questionCount = Integer.parseInt(value);
            if (questionCount <= 0) {
                System.out.println("Question count must be greater than 0.");
                printUsage();
                return null;
            }
            return questionCount;
        } catch (NumberFormatException exception) {
            System.out.println("Question count must be an integer: " + value);
            printUsage();
            return null;
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java com.sylphy.ArithmeticGenerator");
        System.out.println("  java com.sylphy.ArithmeticGenerator <question-count>");
        System.out.println("  java com.sylphy.ArithmeticGenerator grade <problems.csv> <student-answers.csv> <results.csv>");
    }
}
