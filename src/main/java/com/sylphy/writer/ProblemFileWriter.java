package com.sylphy.writer;

import com.sylphy.csv.CsvFile;
import com.sylphy.model.arithmericproblem.ArithmeticProblem;
import com.sylphy.model.ProblemBatch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 题目文件写入器，负责将题目批次分别输出为 CSV 题目文件和 CSV 答案文件。
 * @author apple
 */
public class ProblemFileWriter {
    public void write(ProblemBatch problems, Path outputPath, Path answerOutputPath) throws IOException {
        writeProblems(problems, outputPath);
        writeAnswers(problems, answerOutputPath);
    }

    private void writeProblems(ProblemBatch problems, Path outputPath) throws IOException {
        List<List<String>> rows = new ArrayList<>(problems.size() + 1);
        rows.add(List.of("index", "left", "operator", "right", "expression"));
        int index = 1;
        for (ArithmeticProblem problem : problems) {
            rows.add(List.of(
                    String.valueOf(index++),
                    String.valueOf(problem.left()),
                    String.valueOf(problem.operator()),
                    String.valueOf(problem.right()),
                    problem.format()
            ));
        }

        CsvFile.writeRows(outputPath, rows);
    }

    private void writeAnswers(ProblemBatch problems, Path answerOutputPath) throws IOException {
        List<List<String>> rows = new ArrayList<>(problems.size() + 1);
        rows.add(List.of("index", "answer"));
        int index = 1;
        for (ArithmeticProblem problem : problems) {
            rows.add(List.of(String.valueOf(index++), String.valueOf(problem.answer())));
        }

        CsvFile.writeRows(answerOutputPath, rows);
    }
}
