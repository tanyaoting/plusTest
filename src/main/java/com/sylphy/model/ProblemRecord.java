package com.sylphy.model;

import com.sylphy.strategy.ArithmeticProblemStrategy;

import java.util.Objects;

/**
 * CSV 中的一道口算题记录。
 * @author apple
 */
public record ProblemRecord(int index, int left, char operator, int right, ArithmeticProblemStrategy strategy) {
    public ProblemRecord {
        Objects.requireNonNull(strategy, "strategy must not be null");
        if (index <= 0) {
            throw new IllegalArgumentException("problem index must be greater than 0");
        }
        if (strategy.operator() != operator) {
            throw new IllegalArgumentException("strategy operator does not match problem operator: " + operator);
        }
        if (operator == '-' && left < right) {
            throw new IllegalArgumentException("subtraction answer must not be negative");
        }
    }

    public String expression() {
        return left + " " + operator + " " + right + " = ";
    }

    public int answer() {
        return strategy.create(left, right).answer();
    }
}
