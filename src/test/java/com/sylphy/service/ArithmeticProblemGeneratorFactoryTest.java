package com.sylphy.service;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.model.ProblemBatch;
import com.sylphy.strategy.AdditionProblemStrategy;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ArithmeticProblemGenerator.Factory 的单元测试，验证默认创建和自定义策略创建。
 */
class ArithmeticProblemGeneratorFactoryTest {
    @Test
    void createsGeneratorWithDefaultStrategies() {
        GeneratorConfig config = new GeneratorConfig(40, 1, 10, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = ArithmeticProblemGenerator.Factory.create(new Random(3));

        ProblemBatch problems = generator.generate(config);

        assertTrue(problems.asList().stream()
                .allMatch(problem -> problem.operator() == '+' || problem.operator() == '-'));
    }

    @Test
    void createsGeneratorWithCustomStrategies() {
        GeneratorConfig config = new GeneratorConfig(10, 1, 10, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = ArithmeticProblemGenerator.Factory.create(
                new Random(5),
                List.of(new AdditionProblemStrategy())
        );

        ProblemBatch problems = generator.generate(config);

        assertTrue(problems.asList().stream().allMatch(problem -> problem.operator() == '+'));
    }

    @Test
    void rejectsEmptyStrategies() {
        assertThrows(IllegalArgumentException.class, () -> ArithmeticProblemGenerator.Factory.create(new Random(1), List.of()));
    }
}
