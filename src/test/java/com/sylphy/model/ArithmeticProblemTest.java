package com.sylphy.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArithmeticProblemTest {
    @Test
    void formatsAdditionProblem() {
        ArithmeticProblem problem = new ArithmeticProblem(3, '+', 5);

        assertEquals("3 + 5 = ", problem.format());
    }

    @Test
    void formatsSubtractionProblem() {
        ArithmeticProblem problem = new ArithmeticProblem(8, '-', 2);

        assertEquals("8 - 2 = ", problem.format());
    }

    @Test
    void calculatesAnswer() {
        assertEquals(8, new ArithmeticProblem(3, '+', 5).answer());
        assertEquals(6, new ArithmeticProblem(8, '-', 2).answer());
    }

    @Test
    void rejectsUnsupportedOperator() {
        assertThrows(IllegalArgumentException.class, () -> new ArithmeticProblem(3, '*', 5));
    }
}
