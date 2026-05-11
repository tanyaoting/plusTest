package com.sylphy.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 算术题模型的单元测试，验证格式化、答案计算、相等性和模型不变量。
 */
class ArithmeticProblemTest {
    @Test
    void formatsAdditionProblem() {
        ArithmeticProblem problem = new AdditionProblem(3, 5);

        assertEquals("3 + 5 = ", problem.format());
    }

    @Test
    void formatsSubtractionProblem() {
        ArithmeticProblem problem = new SubtractionProblem(8, 2);

        assertEquals("8 - 2 = ", problem.format());
    }

    @Test
    void calculatesAnswer() {
        assertEquals(8, new AdditionProblem(3, 5).answer());
        assertEquals(6, new SubtractionProblem(8, 2).answer());
    }

    @Test
    void comparesSameProblemTypeByValue() {
        assertEquals(new AdditionProblem(3, 5), new AdditionProblem(3, 5));
        assertEquals(new SubtractionProblem(8, 2), new SubtractionProblem(8, 2));
    }

    @Test
    void doesNotTreatDifferentProblemTypesAsEqual() {
        assertNotEquals(new AdditionProblem(3, 5), new SubtractionProblem(5, 3));
    }

    @Test
    void rejectsSubtractionProblemWithNegativeAnswer() {
        assertThrows(IllegalArgumentException.class, () -> new SubtractionProblem(2, 5));
    }
}
