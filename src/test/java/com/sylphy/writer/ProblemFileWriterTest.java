package com.sylphy.writer;

import com.sylphy.model.ArithmeticProblem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProblemFileWriterTest {
    @TempDir
    Path tempDir;

    @Test
    void createsDirectoryAndWritesFormattedProblems() throws Exception {
        ProblemFileWriter writer = new ProblemFileWriter();
        Path outputPath = tempDir.resolve("nested/math-problems.txt");
        Path answerOutputPath = tempDir.resolve("nested/math-answers.txt");
        List<ArithmeticProblem> problems = List.of(
                new ArithmeticProblem(3, '+', 5),
                new ArithmeticProblem(8, '-', 2)
        );

        writer.write(problems, outputPath, answerOutputPath);

        assertTrue(Files.exists(outputPath));
        assertTrue(Files.exists(answerOutputPath));
        assertEquals("1. 3 + 5 = " + System.lineSeparator()
                + "2. 8 - 2 = " + System.lineSeparator(), Files.readString(outputPath));
        assertEquals("1. 8" + System.lineSeparator()
                + "2. 6" + System.lineSeparator(), Files.readString(answerOutputPath));
    }
}
