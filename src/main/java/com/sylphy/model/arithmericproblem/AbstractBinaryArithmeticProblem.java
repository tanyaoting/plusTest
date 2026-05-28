package com.sylphy.model.arithmericproblem;

import java.util.Objects;

/**
 * 二元算术题的抽象基类，封装左右操作数、运算符、格式化和相等性判断等通用行为。
 * @author apple
 */
public abstract class AbstractBinaryArithmeticProblem implements ArithmeticProblem {
    private final int left;
    private final int right;
    private final char operator;

    protected AbstractBinaryArithmeticProblem(int left, int right, char operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public final int left() {
        return left;
    }

    @Override
    public final int right() {
        return right;
    }

    @Override
    public final char operator() {
        return operator;
    }

    @Override
    public final String format() {
        return left + " " + operator + " " + right + " = ";
    }

    @Override
    public final String toString() {
        return format();
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        AbstractBinaryArithmeticProblem that = (AbstractBinaryArithmeticProblem) object;
        return left == that.left && right == that.right && operator == that.operator;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), left, right, operator);
    }
}
