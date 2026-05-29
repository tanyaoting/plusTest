package com.sylphy.strategy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * StrategyRegistry 的单元测试，验证策略实现类可以通过反射扫描发现。
 * @author apple
 */
class StrategyRegistryTest {
    @Test
    void discoversArithmeticProblemStrategies() {
        List<String> keys = StrategyRegistry.availableStrategies().stream()
                .map(ArithmeticProblemStrategy::key)
                .toList();

        assertEquals(List.of("addition", "subtraction"), keys);
    }
}
