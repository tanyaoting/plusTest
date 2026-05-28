package com.sylphy.model;

import com.sylphy.model.arithmericproblem.ArithmeticProblem;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 题目批次抽象数据类型，封装不可变题目集合，并通过迭代器暴露顺序遍历能力。
 * @author apple
 */
public record ProblemBatch(List<ArithmeticProblem> problems) implements Iterable<ArithmeticProblem> {
    public ProblemBatch {
        problems = List.copyOf(Objects.requireNonNull(problems, "problems must not be null"));
    }

    public int size() {
        return problems.size();
    }

    public List<ArithmeticProblem> asList() {
        return problems;
    }

    @Override
    public Iterator<ArithmeticProblem> iterator() {
        return problems.iterator();
    }
}
