package com.sylphy.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * GeneratorConfig 的单元测试，验证配置加载、默认值和非法配置校验。
 */
class GeneratorConfigTest {
    private static final Pattern CONFIG_CASE_PATTERN = Pattern.compile("\\{([^{}]+)}");

    @Test
    void loadsConfigCasesFromJsonTestData() throws Exception {
        List<ConfigCase> cases = loadConfigCases();

        assertEquals(6, cases.size());
        assertConfigCase(cases.getFirst(), 5, 1, 10,
                Path.of("target/test-output/math-problems.csv"),
                Path.of("target/test-output/math-answers.csv"));
        assertConfigCase(cases.get(1), 1, 0, 0,
                Path.of("target/test-output/single-problem.csv"),
                Path.of("target/test-output/single-answer.csv"));
        assertConfigCase(cases.get(2), 100, 0, 100,
                Path.of("target/test-output/default-problems.csv"),
                Path.of("target/test-output/default-answers.csv"));
        assertConfigCase(cases.get(3), 200, 3, 5,
                Path.of("target/test-output/large-count-problems.csv"),
                Path.of("target/test-output/large-count-answers.csv"));
        assertConfigCase(cases.get(4), 20, 90, 100,
                Path.of("target/test-output/upper-bound-problems.csv"),
                Path.of("target/test-output/upper-bound-answers.csv"));
        assertConfigCase(cases.get(5), 12, 2, 30,
                Path.of("target/test-output/custom/nested/math-problems.csv"),
                Path.of("target/test-output/custom/nested/math-answers.csv"));
    }

    @Test
    void usesDefaultsWhenPropertiesAreMissing() {
        GeneratorConfig config = GeneratorConfig.fromProperties(new Properties());

        assertEquals(100, config.questionCount());
        assertEquals(0, config.minValue());
        assertEquals(100, config.maxValue());
        assertEquals(Path.of("output", "math-problems.csv"), config.outputPath());
        assertEquals(Path.of("output", "math-answers.csv"), config.answerOutputPath());
        assertEquals(List.of('+', '-'), config.strategies().stream()
                .map(strategy -> strategy.operator())
                .toList());
    }

    @Test
    void readsStrategiesFromProperties() {
        Properties properties = new Properties();
        properties.setProperty("problem.strategies", "addition,subtraction");

        GeneratorConfig config = GeneratorConfig.fromProperties(properties);

        assertEquals(List.of('+', '-'), config.strategies().stream()
                .map(strategy -> strategy.operator())
                .toList());
    }

    @Test
    void readsSingleStrategyFromProperties() {
        Properties properties = new Properties();
        properties.setProperty("problem.strategies", "addition");

        GeneratorConfig config = GeneratorConfig.fromProperties(properties);

        assertEquals(List.of('+'), config.strategies().stream()
                .map(strategy -> strategy.operator())
                .toList());
    }

    @Test
    void rejectsUnsupportedStrategyName() {
        Properties properties = new Properties();
        properties.setProperty("problem.strategies", "multiplication");

        assertThrows(IllegalArgumentException.class, () -> GeneratorConfig.fromProperties(properties));
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

    private static List<ConfigCase> loadConfigCases() throws IOException {
        try (InputStream inputStream = GeneratorConfigTest.class.getClassLoader()
                .getResourceAsStream("generator-config-cases.json")) {
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return CONFIG_CASE_PATTERN.matcher(json).results()
                    .map(matchResult -> toConfigCase(matchResult.group(1)))
                    .toList();
        }
    }

    private static ConfigCase toConfigCase(String objectBody) {
        return new ConfigCase(
                readString(objectBody, "name"),
                readInt(objectBody, "questionCount"),
                readInt(objectBody, "minValue"),
                readInt(objectBody, "maxValue"),
                Path.of(readString(objectBody, "outputPath")),
                Path.of(readString(objectBody, "answerOutputPath"))
        );
    }

    private static void assertConfigCase(ConfigCase configCase, int questionCount, int minValue, int maxValue,
                                         Path outputPath, Path answerOutputPath) {
        GeneratorConfig config = configCase.toConfig();

        assertEquals(questionCount, config.questionCount());
        assertEquals(minValue, config.minValue());
        assertEquals(maxValue, config.maxValue());
        assertEquals(outputPath, config.outputPath());
        assertEquals(answerOutputPath, config.answerOutputPath());
    }

    private static int readInt(String objectBody, String key) {
        return Integer.parseInt(readValue(objectBody, key));
    }

    private static String readString(String objectBody, String key) {
        return readValue(objectBody, key).replaceAll("^\\\"|\\\"$", "");
    }

    private static String readValue(String objectBody, String key) {
        Matcher matcher = Pattern.compile("\\\"" + key + "\\\"\\s*:\\s*(\\\"[^\\\"]*\\\"|\\d+)").matcher(objectBody);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing JSON key: " + key);
        }
        return matcher.group(1);
    }

    /**
     * JSON 测试数据中的单个配置用例，用于转换为 GeneratorConfig 进行断言。
     */
    private record ConfigCase(String name, int questionCount, int minValue, int maxValue,
                              Path outputPath, Path answerOutputPath) {
        private GeneratorConfig toConfig() {
            return new GeneratorConfig(questionCount, minValue, maxValue, outputPath, answerOutputPath);
        }
    }
}
