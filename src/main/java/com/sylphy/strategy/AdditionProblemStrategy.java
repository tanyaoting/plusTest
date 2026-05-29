package com.sylphy.strategy;

import com.sylphy.model.arithmericproblem.AdditionProblem;
import com.sylphy.model.arithmericproblem.ArithmeticProblem;

/**
 * 加法题生成策略，负责根据左右操作数创建加法题。
 * @author apple
 */
public final class AdditionProblemStrategy implements ArithmeticProblemStrategy {
    @Override
    public String key() {
        return "addition";
    }

    @Override
    public char operator() {
        return '+';
    }

    @Override
    public ArithmeticProblem create(int left, int right) {
        return new AdditionProblem(left, right);
    }
}
