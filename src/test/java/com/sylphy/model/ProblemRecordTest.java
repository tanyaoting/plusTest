package com.sylphy.model;

import org.junit.jupiter.api.Test;
import com.sylphy.strategy.AdditionProblemStrategy;
import com.sylphy.strategy.SubtractionProblemStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ProblemRecord 的单元测试，验证 CSV 题目记录通过策略表计算答案。
 */
class ProblemRecordTest {
    @Test
    void calculatesAnswerThroughStrategyTable() {
        assertEquals(8, new ProblemRecord(1, 3, '+', 5, new AdditionProblemStrategy()).answer());
        assertEquals(6, new ProblemRecord(2, 8, '-', 2, new SubtractionProblemStrategy()).answer());
    }

    @Test
    void rejectsUnsupportedOperator() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProblemRecord(1, 3, '*', 5, new AdditionProblemStrategy()));
    }
}
