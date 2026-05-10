package com.sylphy;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.model.ArithmeticProblem;
import com.sylphy.service.ArithmeticProblemGenerator;
import com.sylphy.writer.ProblemFileWriter;

import java.io.IOException;
import java.util.List;

public class ArithmeticGenerator {
    public static void main(String[] args) throws IOException {
        GeneratorConfig config = GeneratorConfig.loadDefault();
        ArithmeticProblemGenerator generator = new ArithmeticProblemGenerator();
        List<ArithmeticProblem> problems = generator.generate(config);

        new ProblemFileWriter().write(problems, config.outputPath(), config.answerOutputPath());
        System.out.println("Generated " + problems.size() + " problems to " + config.outputPath());
        System.out.println("Generated answers to " + config.answerOutputPath());
    }
}
