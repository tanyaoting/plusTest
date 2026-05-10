package com.sylphy.config;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeneratorConfigTest {
    @Test
    void loadsConfigFromClasspath() throws Exception {
        GeneratorConfig config = GeneratorConfig.loadFromClasspath("test-application.properties");

        assertEquals(5, config.questionCount());
        assertEquals(1, config.minValue());
        assertEquals(10, config.maxValue());
        assertEquals(Path.of("target/test-output/math-problems.txt"), config.outputPath());
    }

    @Test
    void usesDefaultsWhenPropertiesAreMissing() {
        GeneratorConfig config = GeneratorConfig.fromProperties(new Properties());

        assertEquals(100, config.questionCount());
        assertEquals(0, config.minValue());
        assertEquals(100, config.maxValue());
        assertEquals(Path.of("output", "math-problems.txt"), config.outputPath());
    }

    @Test
    void rejectsInvalidQuestionCount() {
        Properties properties = new Properties();
        properties.setProperty("question.count", "0");

        assertThrows(IllegalArgumentException.class, () -> GeneratorConfig.fromProperties(properties));
    }

    @Test
    void rejectsNegativeMinValue() {
        Properties properties = new Properties();
        properties.setProperty("value.min", "-1");

        assertThrows(IllegalArgumentException.class, () -> GeneratorConfig.fromProperties(properties));
    }

    @Test
    void rejectsInvalidRange() {
        Properties properties = new Properties();
        properties.setProperty("value.min", "10");
        properties.setProperty("value.max", "1");

        assertThrows(IllegalArgumentException.class, () -> GeneratorConfig.fromProperties(properties));
    }

    @Test
    void rejectsTooLargeRange() {
        Properties properties = new Properties();
        properties.setProperty("value.min", "0");
        properties.setProperty("value.max", String.valueOf(Integer.MAX_VALUE));

        assertThrows(IllegalArgumentException.class, () -> GeneratorConfig.fromProperties(properties));
    }
}
