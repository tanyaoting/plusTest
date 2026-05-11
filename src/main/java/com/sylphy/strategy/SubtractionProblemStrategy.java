package com.sylphy.strategy;

import com.sylphy.model.ArithmeticProblem;
import com.sylphy.model.SubtractionProblem;

/**
 * 减法题生成策略，负责调整操作数顺序，确保生成的减法题结果非负。
 * @author apple
 */
public final class SubtractionProblemStrategy implements ArithmeticProblemStrategy {
    @Override
    public char operator() {
        return '-';
    }

    @Override
    public ArithmeticProblem create(int left, int right) {
        if (left < right) {
            int larger = right;
            right = left;
            left = larger;
        }
        return new SubtractionProblem(left, right);
    }
}
