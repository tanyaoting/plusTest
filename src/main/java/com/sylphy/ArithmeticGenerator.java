package com.sylphy;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.model.ProblemBatch;
import com.sylphy.service.ArithmeticProblemGenerator;
import com.sylphy.strategy.AdditionProblemStrategy;
import com.sylphy.strategy.SubtractionProblemStrategy;
import com.sylphy.writer.ProblemFileWriter;

import java.io.IOException;
import java.util.List;

/**
 * 应用程序入口，负责加载默认配置、生成题目并将题目和答案写入文件。
 * @author apple
 */
public class ArithmeticGenerator {
    public static void main(String[] args) throws IOException {
        GeneratorConfig config = GeneratorConfig.loadDefault();
        ArithmeticProblemGenerator generator = new ArithmeticProblemGenerator(List.of(new AdditionProblemStrategy(), new SubtractionProblemStrategy()));
        ProblemBatch problems = generator.generate(config);

        new ProblemFileWriter().write(problems, config.outputPath(), config.answerOutputPath());
        System.out.println("Generated " + problems.size() + " problems to " + config.outputPath());
        System.out.println("Generated answers to " + config.answerOutputPath());
    }
}
