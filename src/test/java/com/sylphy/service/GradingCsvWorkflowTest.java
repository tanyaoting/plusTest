package com.sylphy.service;

import com.sylphy.model.GradingReport;
import com.sylphy.model.ProblemRecord;
import com.sylphy.model.StudentAnswerRecord;
import com.sylphy.reader.ProblemCsvReader;
import com.sylphy.reader.StudentAnswerCsvReader;
import com.sylphy.writer.GradingReportWriter;
import com.sylphy.config.GeneratorConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 批改 CSV 工作流测试，验证读取练习、读取作答、判题和保存结果。
 */
class GradingCsvWorkflowTest {
    @TempDir
    Path tempDir;

    @Test
    void readsCsvGradesAndWritesResultCsv() throws Exception {
        Path problemPath = tempDir.resolve("problems.csv");
        Path answerPath = tempDir.resolve("student-answers.csv");
        Path resultPath = tempDir.resolve("results.csv");
        Files.writeString(problemPath, "index,left,operator,right,expression" + System.lineSeparator()
                + "1,3,+,5,3 + 5 = " + System.lineSeparator()
                + "2,8,-,2,8 - 2 = " + System.lineSeparator());
        Files.writeString(answerPath, "index,studentAnswer" + System.lineSeparator()
                + "1,8" + System.lineSeparator()
                + "2,5" + System.lineSeparator());

        List<ProblemRecord> problems = new ProblemCsvReader(GeneratorConfig.defaultStrategies()).read(problemPath);
        List<StudentAnswerRecord> answers = new StudentAnswerCsvReader().read(answerPath);
        GradingReport report = new GradingService().grade(problems, answers);
        new GradingReportWriter().write(report, resultPath);

        assertEquals(50, report.score());
        assertTrue(Files.readString(resultPath).contains("2,8,-,2,8 - 2 = ,6,5,false"));
    }
}
