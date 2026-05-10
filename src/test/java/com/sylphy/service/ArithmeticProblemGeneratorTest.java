package com.sylphy.service;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.model.ArithmeticProblem;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArithmeticProblemGeneratorTest {
    @Test
    void generatesConfiguredProblemCount() {
        GeneratorConfig config = new GeneratorConfig(5, 1, 10, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = new ArithmeticProblemGenerator(new Random(1));

        List<ArithmeticProblem> problems = generator.generate(config);

        assertEquals(5, problems.size());
    }

    @Test
    void generatesOperandsInsideConfiguredRange() {
        GeneratorConfig config = new GeneratorConfig(50, 3, 7, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = new ArithmeticProblemGenerator(new Random(2));

        List<ArithmeticProblem> problems = generator.generate(config);

        assertTrue(problems.stream().allMatch(problem -> problem.left() >= 3 && problem.left() <= 7));
        assertTrue(problems.stream().allMatch(problem -> problem.right() >= 3 && problem.right() <= 7));
    }

    @Test
    void generatesOnlySupportedOperators() {
        GeneratorConfig config = new GeneratorConfig(50, 1, 10, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = new ArithmeticProblemGenerator(new Random(3));

        List<ArithmeticProblem> problems = generator.generate(config);

        assertTrue(problems.stream().allMatch(problem -> problem.operator() == '+' || problem.operator() == '-'));
    }

    @Test
    void preventsNegativeSubtractionAnswers() {
        GeneratorConfig config = new GeneratorConfig(50, 1, 10, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemGenerator generator = new ArithmeticProblemGenerator(new Random(4));

        List<ArithmeticProblem> problems = generator.generate(config);

        assertTrue(problems.stream()
                .filter(problem -> problem.operator() == '-')
                .allMatch(problem -> problem.answer() >= 0));
    }
}
