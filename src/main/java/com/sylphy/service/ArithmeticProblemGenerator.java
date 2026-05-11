package com.sylphy.service;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.iterator.ArithmeticProblemIterator;
import com.sylphy.model.ArithmeticProblem;
import com.sylphy.model.ProblemBatch;
import com.sylphy.strategy.AdditionProblemStrategy;
import com.sylphy.strategy.ArithmeticProblemStrategy;
import com.sylphy.strategy.SubtractionProblemStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 算术题生成服务，负责组合配置、随机数、策略和迭代器生成完整题目批次。
 *
 * @author apple
 */
public class ArithmeticProblemGenerator {
    private final Random random;
    private final List<ArithmeticProblemStrategy> strategies;

    private ArithmeticProblemGenerator(Random random, List<ArithmeticProblemStrategy> strategies) {
        this.random = Objects.requireNonNull(random, "random must not be null");
        this.strategies = List.copyOf(Objects.requireNonNull(strategies, "strategies must not be null"));
        if (this.strategies.isEmpty()) {
            throw new IllegalArgumentException("strategies must not be empty");
        }
    }

    public ProblemBatch generate(GeneratorConfig config) {
        ArithmeticProblemIterator iterator = new ArithmeticProblemIterator(config, random, strategies);
        List<ArithmeticProblem> problems = new ArrayList<>(config.questionCount());
        while (iterator.hasNext()) {
            problems.add(iterator.next());
        }
        return new ProblemBatch(problems);
    }

    /**
     * 算术题生成器工厂，统一装配默认随机源和默认策略，屏蔽生成器构造细节。
     *
     * @author apple
     */
    public static final class Factory {
        private Factory() {
        }

        public static ArithmeticProblemGenerator createDefault() {
            return create(new Random());
        }

        public static ArithmeticProblemGenerator create(Random random) {
            return create(random, defaultStrategies());
        }

        public static ArithmeticProblemGenerator create(Random random, List<ArithmeticProblemStrategy> strategies) {
            Objects.requireNonNull(random, "random must not be null");
            Objects.requireNonNull(strategies, "strategies must not be null");
            return new ArithmeticProblemGenerator(random, strategies);
        }

        private static List<ArithmeticProblemStrategy> defaultStrategies() {
            return List.of(new AdditionProblemStrategy(), new SubtractionProblemStrategy());
        }
    }
}
