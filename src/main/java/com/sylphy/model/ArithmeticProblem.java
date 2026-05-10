package com.sylphy.model;

/**
 * @author apple
 */
public record ArithmeticProblem(int left, char operator, int right) {
    public ArithmeticProblem {
        if (operator != '+' && operator != '-') {
            throw new IllegalArgumentException("operator must be + or -");
        }
    }

    public String format() {
        return left + " " + operator + " " + right + " = ";
    }

    public int answer() {
        return switch (operator) {
            case '+' -> left + right;
            case '-' -> left - right;
            default -> throw new IllegalStateException("Unsupported operator: " + operator);
        };
    }
}
