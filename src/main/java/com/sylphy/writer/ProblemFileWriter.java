package com.sylphy.writer;

import com.sylphy.model.ArithmeticProblem;
import com.sylphy.model.ProblemBatch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 题目文件写入器，负责将题目批次分别输出为题目文件和答案文件。
 */
public class ProblemFileWriter {
    public void write(ProblemBatch problems, Path outputPath, Path answerOutputPath) throws IOException {
        writeProblems(problems, outputPath);
        writeAnswers(problems, answerOutputPath);
    }

    private void writeProblems(ProblemBatch problems, Path outputPath) throws IOException {
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(outputPath, toIndexedLines(problems, ArithmeticProblem::format), StandardCharsets.UTF_8);
    }

    private void writeAnswers(ProblemBatch problems, Path answerOutputPath) throws IOException {
        Path parent = answerOutputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(answerOutputPath, toIndexedLines(problems, problem -> String.valueOf(problem.answer())), StandardCharsets.UTF_8);
    }

    private String toIndexedLines(ProblemBatch problems, Function<ArithmeticProblem, String> valueFormatter) {
        List<String> lines = new ArrayList<>(problems.size());
        int index = 1;
        for (ArithmeticProblem problem : problems) {
            lines.add(index++ + ". " + valueFormatter.apply(problem));
        }
        return String.join(System.lineSeparator(), lines) + System.lineSeparator();
    }
}
