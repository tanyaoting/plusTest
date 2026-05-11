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
    
    public ArithmeticProblemGenerator() {
        this(new Random());
    }
    
    public ArithmeticProblemGenerator(Random random) {
        this(random, defaultStrategies());
    }
    
    public ArithmeticProblemGenerator(List<ArithmeticProblemStrategy> strategies) {
        this(new Random(), strategies);
    }
    
    public ArithmeticProblemGenerator(Random random, List<ArithmeticProblemStrategy> strategies) {
        this.random = Objects.requireNonNull(random, "random must not be null");
        this.strategies = List.copyOf(Objects.requireNonNull(strategies, "strategies must not be null"));
    }
    
    public ProblemBatch generate(GeneratorConfig config) {
        ArithmeticProblemIterator iterator = new ArithmeticProblemIterator(config, random, strategies);
        List<ArithmeticProblem> problems = new ArrayList<>(config.questionCount());
        while (iterator.hasNext()) {
            problems.add(iterator.next());
        }
        return new ProblemBatch(problems);
    }
    
    private static List<ArithmeticProblemStrategy> defaultStrategies() {
        return List.of(new AdditionProblemStrategy(), new SubtractionProblemStrategy());
    }
}
