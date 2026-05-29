package com.sylphy;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.service.ArithmeticProblemGenerator;
import com.sylphy.service.GradingService;
import com.sylphy.writer.GradingReportWriter;
import com.sylphy.writer.ProblemFileWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ConsoleApplication 的单元测试，验证故事 6 的菜单导航和主要功能流程。
 * @author apple
 */
class ConsoleApplicationTest {
    @TempDir
    Path tempDir;

    @Test
    void menuCanGenerateBatchProblems() {
        Path batchDirectory = tempDir.resolve("practices");
        String input = String.join(System.lineSeparator(), "1", "2", "1", batchDirectory.toString(), "0")
                + System.lineSeparator();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        createApplication(input, output, tempDir.resolve("problems.csv"), tempDir.resolve("answers.csv")).run();

        assertTrue(Files.exists(batchDirectory.resolve("practice-001-problems.csv")));
        assertTrue(Files.exists(batchDirectory.resolve("practice-001-answers.csv")));
        assertTrue(Files.exists(batchDirectory.resolve("practice-002-problems.csv")));
        assertTrue(Files.exists(batchDirectory.resolve("practice-002-answers.csv")));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("共生成 2 套练习，每套 1 道题。"));
    }

    @Test
    void defaultInputsCanRunPaperPracticeFlow() {
        String input = String.join(System.lineSeparator(),
                "1", "", "", "",
                "2", "", "", "", "", "",
                "3", "", "0", "",
                "4", "", "", "",
                "0"
        ) + System.lineSeparator();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        createApplication(input, output, tempDir.resolve("problems.csv"), tempDir.resolve("answers.csv")).run();

        assertTrue(Files.exists(tempDir.resolve("practices/practice-001-problems.csv")));
        assertTrue(Files.exists(tempDir.resolve("selected-problems.csv")));
        assertTrue(Files.exists(tempDir.resolve("student-answers.csv")));
        assertTrue(Files.exists(tempDir.resolve("results.csv")));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("批改结果已保存到："));
    }

    @Test
    void menuCanGradeSelectedPractice() throws Exception {
        Path problemPath = tempDir.resolve("problems.csv");
        Path answerPath = tempDir.resolve("student-answers.csv");
        Path resultPath = tempDir.resolve("results.csv");
        Files.writeString(problemPath, "index,left,operator,right,expression" + System.lineSeparator()
                + "1,3,+,5,3 + 5 = " + System.lineSeparator());
        Files.writeString(answerPath, "index,studentAnswer" + System.lineSeparator()
                + "1,8" + System.lineSeparator());
        String input = String.join(System.lineSeparator(), "4", problemPath.toString(), answerPath.toString(),
                resultPath.toString(), "0") + System.lineSeparator();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        createApplication(input, output, problemPath, tempDir.resolve("answers.csv")).run();

        assertTrue(Files.exists(resultPath));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("得分：100"));
    }

    @Test
    void menuCanGradeAllBatchPractices() throws Exception {
        Path batchDirectory = tempDir.resolve("practices");
        Files.createDirectories(batchDirectory);
        Files.writeString(batchDirectory.resolve("practice-001-problems.csv"),
                "index,left,operator,right,expression" + System.lineSeparator()
                        + "1,3,+,5,3 + 5 = " + System.lineSeparator());
        Files.writeString(batchDirectory.resolve("practice-001-student-answers.csv"),
                "index,studentAnswer" + System.lineSeparator()
                        + "1,8" + System.lineSeparator());
        Files.writeString(batchDirectory.resolve("practice-002-problems.csv"),
                "index,left,operator,right,expression" + System.lineSeparator()
                        + "1,9,-,4,9 - 4 = " + System.lineSeparator());
        Files.writeString(batchDirectory.resolve("practice-002-student-answers.csv"),
                "index,studentAnswer" + System.lineSeparator()
                        + "1,5" + System.lineSeparator());
        String input = String.join(System.lineSeparator(), "5", batchDirectory.toString(), "0")
                + System.lineSeparator();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        createApplication(input, output, tempDir.resolve("problems.csv"), tempDir.resolve("answers.csv")).run();

        assertTrue(Files.exists(batchDirectory.resolve("practice-001-results.csv")));
        assertTrue(Files.exists(batchDirectory.resolve("practice-002-results.csv")));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("批量批改完成，共批改 2 套练习。"));
    }

    @Test
    void menuCanCompletePracticeOnComputer() throws Exception {
        Path problemPath = tempDir.resolve("selected-problems.csv");
        Path resultPath = tempDir.resolve("computer-results.csv");
        Files.writeString(problemPath, "index,left,operator,right,expression" + System.lineSeparator()
                + "1,3,+,5,3 + 5 = " + System.lineSeparator());
        String input = String.join(System.lineSeparator(), "6", problemPath.toString(), "1", "8",
                resultPath.toString(), "0") + System.lineSeparator();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        createApplication(input, output, tempDir.resolve("problems.csv"), tempDir.resolve("answers.csv")).run();

        assertTrue(Files.exists(resultPath));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("正确数量：1/1"));
    }

    private ConsoleApplication createApplication(String input, ByteArrayOutputStream output,
                                                 Path problemPath, Path answerPath) {
        return new ConsoleApplication(
                new Scanner(input),
                new PrintStream(output, true, StandardCharsets.UTF_8),
                ArithmeticProblemGenerator.Factory.create(new Random(1)),
                new ProblemFileWriter(),
                new GradingReportWriter(),
                new GradingService(),
                () -> new GeneratorConfig(1, 0, 100, problemPath, answerPath)
        );
    }
}
