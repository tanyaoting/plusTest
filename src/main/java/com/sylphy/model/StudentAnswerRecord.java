package com.sylphy.model;

/**
 * 小明作答 CSV 中的一条答案记录。
 * @author apple
 */
public record StudentAnswerRecord(int index, int answer) {
    public StudentAnswerRecord {
        if (index <= 0) {
            throw new IllegalArgumentException("answer index must be greater than 0");
        }
    }
}
