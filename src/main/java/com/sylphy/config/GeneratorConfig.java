package com.sylphy.config;

import com.sylphy.strategy.ArithmeticProblemStrategy;
import com.sylphy.strategy.StrategyRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * 题目生成配置的抽象数据类型，集中保存题目数量、取值范围和输出路径，并负责配置校验。
 * @author apple
 */
public record GeneratorConfig(int questionCount, int minValue, int maxValue, Path outputPath, Path answerOutputPath,
                              List<ArithmeticProblemStrategy> strategies) {
    private static final String DEFAULT_CONFIG_FILE = "application.properties";
    private static final int DEFAULT_QUESTION_COUNT = 100;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final Path DEFAULT_OUTPUT_PATH = Path.of("output", "math-problems.csv");
    private static final Path DEFAULT_ANSWER_OUTPUT_PATH = Path.of("output", "math-answers.csv");
    private static final String STRATEGY_NAMES_KEY = "problem.strategies";

    public GeneratorConfig(int questionCount, int minValue, int maxValue, Path outputPath, Path answerOutputPath) {
        this(questionCount, minValue, maxValue, outputPath, answerOutputPath, defaultStrategies());
    }

    public GeneratorConfig {
        Objects.requireNonNull(outputPath, "outputPath must not be null");
        Objects.requireNonNull(answerOutputPath, "answerOutputPath must not be null");
        strategies = List.copyOf(Objects.requireNonNull(strategies, "strategies must not be null"));

        if (questionCount <= 0) {
            throw new IllegalArgumentException("questionCount must be greater than 0");
        }
        if (minValue < 0) {
            throw new IllegalArgumentException("minValue must be greater than or equal to 0");
        }
        if (maxValue < minValue) {
            throw new IllegalArgumentException("maxValue must be greater than or equal to minValue");
        }
        if ((long) maxValue - minValue + 1 > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("configured range is too large");
        }
        validateStrategies(strategies);
    }

    public static GeneratorConfig loadDefault() throws IOException {
        return loadFromClasspath(DEFAULT_CONFIG_FILE);
    }

    public static GeneratorConfig loadFromClasspath(String resourceName) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = GeneratorConfig.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        }
        return fromProperties(properties);
    }

    public static GeneratorConfig fromProperties(Properties properties) {
        return new GeneratorConfig(
                readInt(properties, "question.count", DEFAULT_QUESTION_COUNT),
                readInt(properties, "value.min", DEFAULT_MIN_VALUE),
                readInt(properties, "value.max", DEFAULT_MAX_VALUE),
                Path.of(properties.getProperty("output.path", DEFAULT_OUTPUT_PATH.toString())),
                Path.of(properties.getProperty("answer.output.path", DEFAULT_ANSWER_OUTPUT_PATH.toString())),
                readStrategies(properties)
        );
    }

    private static int readInt(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    public static List<ArithmeticProblemStrategy> defaultStrategies() {
        return StrategyRegistry.availableStrategies();
    }

    private static List<ArithmeticProblemStrategy> readStrategies(Properties properties) {
        String value = properties.getProperty(STRATEGY_NAMES_KEY);
        if (value == null || value.isBlank()) {
            return defaultStrategies();
        }
        return List.of(value.split(",")).stream()
                .map(String::trim)
                .filter(strategyKey -> !strategyKey.isBlank())
                .map(GeneratorConfig::findStrategy)
                .toList();
    }

    private static ArithmeticProblemStrategy findStrategy(String strategyKey) {
        for (ArithmeticProblemStrategy strategy : StrategyRegistry.availableStrategies()) {
            if (strategy.key().equalsIgnoreCase(strategyKey)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("unsupported problem strategy: " + strategyKey);
    }

    private static void validateStrategies(List<ArithmeticProblemStrategy> strategies) {
        if (strategies.isEmpty()) {
            throw new IllegalArgumentException("strategies must not be empty");
        }
        Set<Character> operators = new HashSet<>();
        for (ArithmeticProblemStrategy strategy : strategies) {
            Objects.requireNonNull(strategy, "strategy must not be null");
            if (!operators.add(strategy.operator())) {
                throw new IllegalArgumentException("duplicate strategy operator: " + strategy.operator());
            }
        }
    }
}
