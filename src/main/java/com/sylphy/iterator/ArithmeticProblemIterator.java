package com.sylphy.iterator;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.model.ArithmeticProblem;
import com.sylphy.strategy.ArithmeticProblemStrategy;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;

/**
 * 算术题生成迭代器，按照配置数量逐个生成题目，并通过策略选择具体题型。
 * @author apple
 */
public final class ArithmeticProblemIterator implements Iterator<ArithmeticProblem> {
    private final GeneratorConfig config;
    private final Random random;
    private final List<ArithmeticProblemStrategy> strategies;
    private final int bound;
    private int generatedCount;

    public ArithmeticProblemIterator(GeneratorConfig config, Random random, List<ArithmeticProblemStrategy> strategies) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.random = Objects.requireNonNull(random, "random must not be null");
        this.strategies = List.copyOf(Objects.requireNonNull(strategies, "strategies must not be null"));
        if (this.strategies.isEmpty()) {
            throw new IllegalArgumentException("strategies must not be empty");
        }
        this.bound = config.maxValue() - config.minValue() + 1;
    }

    @Override
    public boolean hasNext() {
        return generatedCount < config.questionCount();
    }

    @Override
    public ArithmeticProblem next() {
        if (!hasNext()) {
            throw new NoSuchElementException("no more arithmetic problems available");
        }

        ArithmeticProblemStrategy strategy = strategies.get(random.nextInt(strategies.size()));
        int left = nextValue();
        int right = nextValue();
        generatedCount++;
        return strategy.create(left, right);
    }

    private int nextValue() {
        return random.nextInt(bound) + config.minValue();
    }
}
