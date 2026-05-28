package com.sylphy.model;

import java.util.Objects;

/**
 * 单道题的批改结果。
 * @author apple
 */
public record GradingResult(ProblemRecord problem, int studentAnswer) {
    public GradingResult {
        Objects.requireNonNull(problem, "problem must not be null");
    }

    public boolean correct() {
        return studentAnswer == problem.answer();
    }
}
