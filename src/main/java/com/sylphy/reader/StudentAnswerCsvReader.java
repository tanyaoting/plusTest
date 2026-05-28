package com.sylphy.reader;

import com.sylphy.csv.CsvFile;
import com.sylphy.model.StudentAnswerRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * 从小明作答 CSV 读取答案记录。
 * @author apple
 */
public final class StudentAnswerCsvReader {
    public List<StudentAnswerRecord> read(Path path) throws IOException {
        return CsvFile.readTable(path).stream()
                .map(this::toStudentAnswerRecord)
                .toList();
    }

    private StudentAnswerRecord toStudentAnswerRecord(Map<String, String> row) {
        return new StudentAnswerRecord(readInt(row, "index"), readInt(row, answerHeader(row)));
    }

    private String answerHeader(Map<String, String> row) {
        if (row.containsKey("studentAnswer")) {
            return "studentAnswer";
        }
        if (row.containsKey("answer")) {
            return "answer";
        }
        throw new IllegalArgumentException("Missing required CSV field: studentAnswer");
    }

    private int readInt(Map<String, String> row, String header) {
        String value = row.get(header);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required CSV field: " + header);
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("CSV field '" + header + "' must be an integer: " + value, exception);
        }
    }
}
