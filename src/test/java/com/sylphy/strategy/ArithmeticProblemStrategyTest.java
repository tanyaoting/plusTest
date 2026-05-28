package com.sylphy.strategy;

import com.sylphy.model.arithmericproblem.AdditionProblem;
import com.sylphy.model.arithmericproblem.ArithmeticProblem;
import com.sylphy.model.arithmericproblem.SubtractionProblem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * 算术题策略的单元测试，验证不同策略创建的题型和特定生成规则。
 */
class ArithmeticProblemStrategyTest {
    @Test
    void additionStrategyCreatesAdditionProblem() {
        ArithmeticProblemStrategy strategy = new AdditionProblemStrategy();

        ArithmeticProblem problem = strategy.create(3, 5);

        assertEquals('+', strategy.operator());
        assertInstanceOf(AdditionProblem.class, problem);
        assertEquals(8, problem.answer());
    }

    @Test
    void subtractionStrategyNormalizesOperands() {
        ArithmeticProblemStrategy strategy = new SubtractionProblemStrategy();

        ArithmeticProblem problem = strategy.create(2, 5);

        assertEquals('-', strategy.operator());
        assertInstanceOf(SubtractionProblem.class, problem);
        assertEquals(5, problem.left());
        assertEquals(2, problem.right());
        assertEquals(3, problem.answer());
    }
}
