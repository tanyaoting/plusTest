package com.sylphy.model;

import java.util.List;
import java.util.Objects;

/**
 * 一次练习的批改报告。
 * @author apple
 */
public record GradingReport(List<GradingResult> results) {
    public GradingReport {
        results = List.copyOf(Objects.requireNonNull(results, "results must not be null"));
        if (results.isEmpty()) {
            throw new IllegalArgumentException("grading results must not be empty");
        }
    }

    public int totalCount() {
        return results.size();
    }

    public long correctCount() {
        return results.stream().filter(GradingResult::correct).count();
    }

    public int score() {
        return Math.round(correctCount() * 100.0f / totalCount());
    }

    public List<GradingResult> wrongResults() {
        return results.stream()
                .filter(result -> !result.correct())
                .toList();
    }
}
