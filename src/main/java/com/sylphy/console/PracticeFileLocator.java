package com.sylphy.console;

import com.sylphy.config.GeneratorConfig;

import java.nio.file.Path;

/**
 * 练习文件命名和默认路径规则，避免路径规则分散在菜单流程中。
 *
 * @author apple
 */
public final class PracticeFileLocator {
    public Path defaultBatchDirectory(GeneratorConfig config) {
        return defaultOutputDirectory(config).resolve("practices");
    }

    public Path firstBatchProblemPath(GeneratorConfig config) {
        return batchProblemPath(defaultBatchDirectory(config), 1);
    }

    public Path defaultSelectedProblemPath(GeneratorConfig config) {
        return defaultOutputDirectory(config).resolve("selected-problems.csv");
    }

    public Path defaultSelectedAnswerPath(GeneratorConfig config) {
        return defaultOutputDirectory(config).resolve("selected-answers.csv");
    }

    public Path defaultStudentAnswerPath(GeneratorConfig config) {
        return defaultOutputDirectory(config).resolve("student-answers.csv");
    }

    public Path defaultResultPath(GeneratorConfig config) {
        return defaultOutputDirectory(config).resolve("results.csv");
    }

    public Path batchProblemPath(Path outputDirectory, int practiceNumber) {
        return outputDirectory.resolve("practice-" + formatPracticeNumber(practiceNumber) + "-problems.csv");
    }

    public Path batchAnswerPath(Path outputDirectory, int practiceNumber) {
        return outputDirectory.resolve("practice-" + formatPracticeNumber(practiceNumber) + "-answers.csv");
    }

    public Path batchStudentAnswerPath(Path problemPath) {
        String fileName = problemPath.getFileName().toString();
        return problemPath.resolveSibling(fileName.replace("-problems.csv", "-student-answers.csv"));
    }

    public Path batchResultPath(Path problemPath) {
        String fileName = problemPath.getFileName().toString();
        return problemPath.resolveSibling(fileName.replace("-problems.csv", "-results.csv"));
    }

    private Path defaultOutputDirectory(GeneratorConfig config) {
        Path parent = config.outputPath().getParent();
        if (parent == null) {
            return Path.of(".");
        }
        return parent;
    }

    private String formatPracticeNumber(int practiceNumber) {
        return String.format("%03d", practiceNumber);
    }
}
