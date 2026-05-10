package com.sylphy.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/**
 * @author apple
 */
public record GeneratorConfig(int questionCount, int minValue, int maxValue, Path outputPath) {
    private static final String DEFAULT_CONFIG_FILE = "application.properties";
    private static final int DEFAULT_QUESTION_COUNT = 100;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final Path DEFAULT_OUTPUT_PATH = Path.of("output", "math-problems.txt");

    public GeneratorConfig {
        Objects.requireNonNull(outputPath, "outputPath must not be null");

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
                Path.of(properties.getProperty("output.path", DEFAULT_OUTPUT_PATH.toString()))
        );
    }

    private static int readInt(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }
}
