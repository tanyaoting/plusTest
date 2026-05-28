package com.sylphy.service;

import com.sylphy.model.GradingReport;
import com.sylphy.model.ProblemRecord;
import com.sylphy.model.StudentAnswerRecord;
import com.sylphy.strategy.AdditionProblemStrategy;
import com.sylphy.strategy.SubtractionProblemStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * GradingService 的单元测试，验证判题、打分和作答数据校验。
 */
class GradingServiceTest {
    @Test
    void gradesAnswersAndCalculatesScore() {
        GradingService service = new GradingService();
        List<ProblemRecord> problems = List.of(
                new ProblemRecord(1, 3, '+', 5, new AdditionProblemStrategy()),
                new ProblemRecord(2, 8, '-', 2, new SubtractionProblemStrategy())
        );
        List<StudentAnswerRecord> answers = List.of(
                new StudentAnswerRecord(1, 8),
                new StudentAnswerRecord(2, 5)
        );

        GradingReport report = service.grade(problems, answers);

        assertEquals(2, report.totalCount());
        assertEquals(1, report.correctCount());
        assertEquals(50, report.score());
        assertEquals(1, report.wrongResults().size());
    }

    @Test
    void rejectsMissingStudentAnswer() {
        GradingService service = new GradingService();
        List<ProblemRecord> problems = List.of(new ProblemRecord(1, 3, '+', 5, new AdditionProblemStrategy()));
        List<StudentAnswerRecord> answers = List.of();

        assertThrows(IllegalArgumentException.class, () -> service.grade(problems, answers));
    }
}
