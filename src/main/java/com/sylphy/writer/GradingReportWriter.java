package com.sylphy.writer;

import com.sylphy.csv.CsvFile;
import com.sylphy.model.GradingReport;
import com.sylphy.model.GradingResult;
import com.sylphy.model.ProblemRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 批改结果写入器，负责将一次练习结果保存为 CSV。
 * @author apple
 */
public final class GradingReportWriter {
    public void write(GradingReport report, Path path) throws IOException {
        List<List<String>> rows = new ArrayList<>(report.totalCount() + 1);
        rows.add(List.of(
                "index",
                "left",
                "operator",
                "right",
                "expression",
                "correctAnswer",
                "studentAnswer",
                "correct"
        ));

        for (GradingResult result : report.results()) {
            ProblemRecord problem = result.problem();
            rows.add(List.of(
                    String.valueOf(problem.index()),
                    String.valueOf(problem.left()),
                    String.valueOf(problem.operator()),
                    String.valueOf(problem.right()),
                    problem.expression(),
                    String.valueOf(problem.answer()),
                    String.valueOf(result.studentAnswer()),
                    String.valueOf(result.correct())
            ));
        }

        CsvFile.writeRows(path, rows);
    }
}
