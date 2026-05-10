package com.sylphy.writer;

import com.sylphy.model.ArithmeticProblem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ProblemFileWriter {
    public void write(List<ArithmeticProblem> problems, Path outputPath, Path answerOutputPath) throws IOException {
        writeProblems(problems, outputPath);
        writeAnswers(problems, answerOutputPath);
    }

    private void writeProblems(List<ArithmeticProblem> problems, Path outputPath) throws IOException {
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < problems.size(); i++) {
            content.append(i + 1)
                    .append(". ")
                    .append(problems.get(i).format())
                    .append(System.lineSeparator());
        }

        Files.writeString(outputPath, content.toString(), StandardCharsets.UTF_8);
    }

    private void writeAnswers(List<ArithmeticProblem> problems, Path answerOutputPath) throws IOException {
        Path parent = answerOutputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < problems.size(); i++) {
            content.append(i + 1)
                    .append(". ")
                    .append(problems.get(i).answer())
                    .append(System.lineSeparator());
        }

        Files.writeString(answerOutputPath, content.toString(), StandardCharsets.UTF_8);
    }
}
