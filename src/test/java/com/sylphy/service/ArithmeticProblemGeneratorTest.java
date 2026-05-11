package com.sylphy.service;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.factory.ArithmeticProblemGeneratorFactory;
import com.sylphy.model.ProblemBatch;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ArithmeticProblemGenerator 的单元测试，验证生成数量、操作数范围和题目约束。
 */
class ArithmeticProblemGeneratorTest {
    @Test
    void generatesConfiguredProblemCount() {
        GeneratorConfig config = new GeneratorConfig(5, 1, 10, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = ArithmeticProblemGeneratorFactory.create(new Random(1));

        ProblemBatch problems = generator.generate(config);

        assertEquals(5, problems.size());
    }

    @Test
    void generatesOperandsInsideConfiguredRange() {
        GeneratorConfig config = new GeneratorConfig(50, 3, 7, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = ArithmeticProblemGeneratorFactory.create(new Random(2));

        ProblemBatch problems = generator.generate(config);

        assertTrue(problems.asList().stream().allMatch(problem -> problem.left() >= 3 && problem.left() <= 7));
        assertTrue(problems.asList().stream().allMatch(problem -> problem.right() >= 3 && problem.right() <= 7));
    }

    @Test
    void generatesOnlySupportedOperators() {
        GeneratorConfig config = new GeneratorConfig(50, 1, 10, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = ArithmeticProblemGeneratorFactory.create(new Random(3));

        ProblemBatch problems = generator.generate(config);

        assertTrue(problems.asList().stream().allMatch(problem -> problem.operator() == '+' || problem.operator() == '-'));
    }

    @Test
    void preventsNegativeSubtractionAnswers() {
        GeneratorConfig config = new GeneratorConfig(50, 1, 10, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = ArithmeticProblemGeneratorFactory.create(new Random(4));

        ProblemBatch problems = generator.generate(config);

        assertTrue(problems.asList().stream()
                .filter(problem -> problem.operator() == '-')
                .allMatch(problem -> problem.answer() >= 0));
    }
}
