package com.sylphy.factory;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.model.ProblemBatch;
import com.sylphy.service.ArithmeticProblemGenerator;
import com.sylphy.strategy.AdditionProblemStrategy;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ArithmeticProblemGeneratorFactory 的单元测试，验证默认创建和自定义策略创建。
 */
class ArithmeticProblemGeneratorFactoryTest {
    @Test
    void createsGeneratorWithDefaultStrategies() {
        GeneratorConfig config = new GeneratorConfig(40, 1, 10, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = ArithmeticProblemGeneratorFactory.create(new Random(3));

        ProblemBatch problems = generator.generate(config);

        assertTrue(problems.asList().stream()
                .allMatch(problem -> problem.operator() == '+' || problem.operator() == '-'));
    }

    @Test
    void createsGeneratorWithCustomStrategies() {
        GeneratorConfig config = new GeneratorConfig(10, 1, 10, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = ArithmeticProblemGeneratorFactory.create(
                new Random(5),
                List.of(new AdditionProblemStrategy())
        );

        ProblemBatch problems = generator.generate(config);

        assertTrue(problems.asList().stream().allMatch(problem -> problem.operator() == '+'));
    }

    @Test
    void rejectsEmptyStrategies() {
        assertThrows(IllegalArgumentException.class, () -> ArithmeticProblemGeneratorFactory.create(new Random(1), List.of()));
    }
}
