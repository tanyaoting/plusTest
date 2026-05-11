package com.sylphy.factory;

import com.sylphy.service.ArithmeticProblemGenerator;
import com.sylphy.strategy.AdditionProblemStrategy;
import com.sylphy.strategy.ArithmeticProblemStrategy;
import com.sylphy.strategy.SubtractionProblemStrategy;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 算术题生成器工厂，统一装配默认随机源和默认策略，屏蔽生成器构造细节。
 */
public final class ArithmeticProblemGeneratorFactory {
    private ArithmeticProblemGeneratorFactory() {
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
