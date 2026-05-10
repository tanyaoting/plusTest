package com.sylphy.service;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.model.ArithmeticProblem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ArithmeticProblemGenerator {
    private final Random random;

    public ArithmeticProblemGenerator() {
        this(new Random());
    }

    public ArithmeticProblemGenerator(Random random) {
        this.random = Objects.requireNonNull(random, "random must not be null");
    }

    public List<ArithmeticProblem> generate(GeneratorConfig config) {
        List<ArithmeticProblem> problems = new ArrayList<>(config.questionCount());
        int bound = config.maxValue() - config.minValue() + 1;

        for (int i = 0; i < config.questionCount(); i++) {
            boolean isAddition = random.nextBoolean();
            int left = nextValue(bound, config.minValue());
            int right = nextValue(bound, config.minValue());

            if (!isAddition && left < right) {
                int larger = right;
                right = left;
                left = larger;
            }

            problems.add(new ArithmeticProblem(left, isAddition ? '+' : '-', right));
        }

        return List.copyOf(problems);
    }

    private int nextValue(int bound, int minValue) {
        return random.nextInt(bound) + minValue;
    }
}
