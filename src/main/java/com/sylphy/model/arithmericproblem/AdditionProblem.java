package com.sylphy.model.arithmericproblem;

/**
 * 加法题目实体，负责表达加法题并计算加法答案。
 * @author apple
 */
public final class AdditionProblem extends AbstractBinaryArithmeticProblem {
    public AdditionProblem(int left, int right) {
        super(left, right, '+');
    }

    @Override
    public int answer() {
        return left() + right();
    }
}
