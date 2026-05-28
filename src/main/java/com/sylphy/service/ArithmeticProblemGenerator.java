package com.sylphy.service;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.iterator.ArithmeticProblemIterator;
import com.sylphy.model.arithmericproblem.ArithmeticProblem;
import com.sylphy.model.ProblemBatch;

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

    private ArithmeticProblemGenerator(Random random) {
        this.random = Objects.requireNonNull(random, "random must not be null");
    }

    public ProblemBatch generate(GeneratorConfig config) {
        ArithmeticProblemIterator iterator = new ArithmeticProblemIterator(config, random, config.strategies());
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
            Objects.requireNonNull(random, "random must not be null");
            return new ArithmeticProblemGenerator(random);
        }

    }
}
