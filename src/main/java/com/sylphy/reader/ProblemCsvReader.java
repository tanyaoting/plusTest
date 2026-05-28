package com.sylphy.reader;

import com.sylphy.csv.CsvFile;
import com.sylphy.model.ProblemRecord;
import com.sylphy.strategy.ArithmeticProblemStrategy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 从练习题 CSV 读取题目记录。
 * @author apple
 */
public final class ProblemCsvReader {
    private final Map<Character, ArithmeticProblemStrategy> strategyTable;

    public ProblemCsvReader(List<ArithmeticProblemStrategy> strategies) {
        this.strategyTable = strategyTable(strategies);
    }

    public List<ProblemRecord> read(Path path) throws IOException {
        return CsvFile.readTable(path).stream()
                .map(this::toProblemRecord)
                .toList();
    }

    private ProblemRecord toProblemRecord(Map<String, String> row) {
        char operator = readOperator(row);
        return new ProblemRecord(
                readInt(row, "index"),
                readInt(row, "left"),
                operator,
                readInt(row, "right"),
                readStrategy(operator)
        );
    }

    private int readInt(Map<String, String> row, String header) {
        String value = readRequired(row, header);
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("CSV field '" + header + "' must be an integer: " + value, exception);
        }
    }

    private char readOperator(Map<String, String> row) {
        String value = readRequired(row, "operator").trim();
        if (value.length() != 1) {
            throw new IllegalArgumentException("CSV field 'operator' must be a single character: " + value);
        }
        return value.charAt(0);
    }

    private ArithmeticProblemStrategy readStrategy(char operator) {
        ArithmeticProblemStrategy strategy = strategyTable.get(operator);
        if (strategy == null) {
            throw new IllegalArgumentException("unsupported operator: " + operator);
        }
        return strategy;
    }

    private String readRequired(Map<String, String> row, String header) {
        String value = row.get(header);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required CSV field: " + header);
        }
        return value;
    }

    private Map<Character, ArithmeticProblemStrategy> strategyTable(List<ArithmeticProblemStrategy> strategies) {
        Objects.requireNonNull(strategies, "strategies must not be null");
        Map<Character, ArithmeticProblemStrategy> table = new LinkedHashMap<>();
        for (ArithmeticProblemStrategy strategy : strategies) {
            Objects.requireNonNull(strategy, "strategy must not be null");
            ArithmeticProblemStrategy previous = table.put(strategy.operator(), strategy);
            if (previous != null) {
                throw new IllegalArgumentException("duplicate strategy operator: " + strategy.operator());
            }
        }
        if (table.isEmpty()) {
            throw new IllegalArgumentException("strategies must not be empty");
        }
        return Map.copyOf(table);
    }
}
