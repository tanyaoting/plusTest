package com.sylphy.model.arithmericproblem;

/**
 * 算术题目的公共接口，定义题目展示、答案计算和操作数访问能力。
 * @author apple
 */
public interface ArithmeticProblem {
    int left();

    int right();

    char operator();

    String format();

    int answer();
}
