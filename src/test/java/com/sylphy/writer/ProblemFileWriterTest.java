package com.sylphy.writer;

import com.sylphy.model.arithmericproblem.AdditionProblem;
import com.sylphy.model.ProblemBatch;
import com.sylphy.model.arithmericproblem.SubtractionProblem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ProblemFileWriter 的单元测试，验证目录创建、题目 CSV 输出和答案 CSV 输出内容。
 */
class ProblemFileWriterTest {
    @TempDir
    Path tempDir;

    @Test
    void createsDirectoryAndWritesFormattedProblems() throws Exception {
        ProblemFileWriter writer = new ProblemFileWriter();
        Path outputPath = tempDir.resolve("nested/math-problems.csv");
        Path answerOutputPath = tempDir.resolve("nested/math-answers.csv");
        ProblemBatch problems = new ProblemBatch(List.of(
                new AdditionProblem(3, 5),
                new SubtractionProblem(8, 2)
        ));

        writer.write(problems, outputPath, answerOutputPath);

        assertTrue(Files.exists(outputPath));
        assertTrue(Files.exists(answerOutputPath));
        assertEquals("index,left,operator,right,expression" + System.lineSeparator()
                + "1,3,+,5,3 + 5 = " + System.lineSeparator()
                + "2,8,-,2,8 - 2 = " + System.lineSeparator(), Files.readString(outputPath));
        assertEquals("index,answer" + System.lineSeparator()
                + "1,8" + System.lineSeparator()
                + "2,6" + System.lineSeparator(), Files.readString(answerOutputPath));
    }
}
