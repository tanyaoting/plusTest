package com.sylphy.iterator;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.model.arithmericproblem.ArithmeticProblem;
import com.sylphy.strategy.SubtractionProblemStrategy;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ArithmeticProblemIterator 的单元测试，验证迭代数量、边界范围和结束状态。
 */
class ArithmeticProblemIteratorTest {
    @Test
    void iteratesConfiguredNumberOfProblems() {
        GeneratorConfig config = new GeneratorConfig(4, 1, 9, Path.of("output.txt"), Path.of("answers.txt"));
        ArithmeticProblemIterator iterator = new ArithmeticProblemIterator(
                config,
                new Random(7),
                List.of(new SubtractionProblemStrategy())
        );

        int count = 0;
        while (iterator.hasNext()) {
            ArithmeticProblem problem = iterator.next();
            assertEquals('-', problem.operator());
            assertTrue(problem.left() >= 1 && problem.left() <= 9);
            assertTrue(problem.right() >= 1 && problem.right() <= 9);
            assertTrue(problem.answer() >= 0);
            count++;
        }

        assertEquals(4, count);
        assertFalse(iterator.hasNext());
        assertThrows(java.util.NoSuchElementException.class, iterator::next);
    }
}
