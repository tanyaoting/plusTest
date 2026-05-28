package com.sylphy.service;

import com.sylphy.model.GradingReport;
import com.sylphy.model.GradingResult;
import com.sylphy.model.ProblemRecord;
import com.sylphy.model.StudentAnswerRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 练习批改服务，负责对题目记录和学生答案记录进行匹配、判题和打分。
 * @author apple
 */
public final class GradingService {
    public GradingReport grade(List<ProblemRecord> problems, List<StudentAnswerRecord> answers) {
        Objects.requireNonNull(problems, "problems must not be null");
        Objects.requireNonNull(answers, "answers must not be null");
        if (problems.isEmpty()) {
            throw new IllegalArgumentException("problems must not be empty");
        }

        Map<Integer, Integer> answerByIndex = toAnswerMap(answers);
        Set<Integer> problemIndexes = new HashSet<>();
        List<GradingResult> results = new ArrayList<>(problems.size());
        for (ProblemRecord problem : problems) {
            if (!problemIndexes.add(problem.index())) {
                throw new IllegalArgumentException("Duplicate problem index: " + problem.index());
            }
            Integer studentAnswer = answerByIndex.get(problem.index());
            if (studentAnswer == null) {
                throw new IllegalArgumentException("Missing student answer for problem index: " + problem.index());
            }
            results.add(new GradingResult(problem, studentAnswer));
        }

        for (Integer answerIndex : answerByIndex.keySet()) {
            if (!problemIndexes.contains(answerIndex)) {
                throw new IllegalArgumentException("Student answer has no matching problem index: " + answerIndex);
            }
        }

        return new GradingReport(results);
    }

    private Map<Integer, Integer> toAnswerMap(List<StudentAnswerRecord> answers) {
        Map<Integer, Integer> answerByIndex = new LinkedHashMap<>();
        for (StudentAnswerRecord answer : answers) {
            Integer previous = answerByIndex.put(answer.index(), answer.answer());
            if (previous != null) {
                throw new IllegalArgumentException("Duplicate student answer index: " + answer.index());
            }
        }
        return answerByIndex;
    }
}
