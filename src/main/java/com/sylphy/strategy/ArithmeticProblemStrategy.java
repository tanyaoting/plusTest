package com.sylphy.strategy;

import com.sylphy.model.arithmericproblem.ArithmeticProblem;

/**
 * 算术题生成策略接口，用于解耦题目生成流程和具体题型创建逻辑。
 * @author apple
 */
public interface ArithmeticProblemStrategy {
    /**
     * 获取配置中的策略标识
     * @return
     */
    String key();

    /**
     * 获取操作符
     * @return
     */
    char operator();
    
    /**
     * 创建算术题
     * @param left
     * @param right
     * @return
     */
    ArithmeticProblem create(int left, int right);
}
