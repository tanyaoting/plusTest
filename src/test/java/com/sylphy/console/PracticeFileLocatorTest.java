package com.sylphy.console;

import com.sylphy.config.GeneratorConfig;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PracticeFileLocator 的单元测试，锁定故事 7 重构后的文件命名规则。
 *
 * @author apple
 */
class PracticeFileLocatorTest {
    private final PracticeFileLocator locator = new PracticeFileLocator();

    @Test
    void defaultPathsStayUnderConfiguredOutputDirectory() {
        GeneratorConfig config = new GeneratorConfig(
                10,
                0,
                100,
                Path.of("output", "math-problems.csv"),
                Path.of("output", "math-answers.csv")
        );

        assertEquals(Path.of("output", "practices"), locator.defaultBatchDirectory(config));
        assertEquals(Path.of("output", "selected-problems.csv"), locator.defaultSelectedProblemPath(config));
        assertEquals(Path.of("output", "student-answers.csv"), locator.defaultStudentAnswerPath(config));
        assertEquals(Path.of("output", "results.csv"), locator.defaultResultPath(config));
    }

    @Test
    void batchFileNamesUseSamePracticeNumber() {
        Path directory = Path.of("output", "practices");
        Path problemPath = locator.batchProblemPath(directory, 7);

        assertEquals(Path.of("output", "practices", "practice-007-problems.csv"), problemPath);
        assertEquals(Path.of("output", "practices", "practice-007-answers.csv"),
                locator.batchAnswerPath(directory, 7));
        assertEquals(Path.of("output", "practices", "practice-007-student-answers.csv"),
                locator.batchStudentAnswerPath(problemPath));
        assertEquals(Path.of("output", "practices", "practice-007-results.csv"),
                locator.batchResultPath(problemPath));
    }
}
