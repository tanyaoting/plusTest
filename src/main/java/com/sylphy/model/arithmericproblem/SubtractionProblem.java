package com.sylphy.model.arithmericproblem;

/**
 * 减法题目实体，负责表达非负结果的减法题并计算减法答案。
 * @author apple
 */
public final class SubtractionProblem extends AbstractBinaryArithmeticProblem {
    public SubtractionProblem(int left, int right) {
        super(left, right, '-');
        if (left < right) {
            throw new IllegalArgumentException("subtraction problem answer must not be negative");
        }
    }

    @Override
    public int answer() {
        return left() - right();
    }
}
