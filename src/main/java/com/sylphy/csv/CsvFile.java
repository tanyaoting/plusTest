package com.sylphy.csv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * CSV 文件工具，负责基础 CSV 转义、解析和按表头读取。
 * @author apple
 */
public final class CsvFile {
    private CsvFile() {
    }

    public static void writeRows(Path path, List<List<String>> rows) throws IOException {
        Objects.requireNonNull(path, "path must not be null");
        Objects.requireNonNull(rows, "rows must not be null");

        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = new ArrayList<>(rows.size());
        for (List<String> row : rows) {
            lines.add(toLine(row));
        }
        Files.writeString(path, String.join(System.lineSeparator(), lines) + System.lineSeparator(), StandardCharsets.UTF_8);
    }

    public static List<Map<String, String>> readTable(Path path) throws IOException {
        Objects.requireNonNull(path, "path must not be null");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty: " + path);
        }

        List<String> headers = parseLine(lines.getFirst());
        validateHeaders(headers, path);

        List<Map<String, String>> rows = new ArrayList<>();
        for (int lineNumber = 2; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);
            if (line.isBlank()) {
                continue;
            }
            List<String> values = parseLine(line);
            if (values.size() != headers.size()) {
                throw new IllegalArgumentException("CSV column count mismatch at line " + lineNumber + ": " + path);
            }
            Map<String, String> row = new LinkedHashMap<>();
            for (int index = 0; index < headers.size(); index++) {
                row.put(headers.get(index), values.get(index));
            }
            rows.add(row);
        }
        return rows;
    }

    private static String toLine(List<String> row) {
        Objects.requireNonNull(row, "row must not be null");
        List<String> values = new ArrayList<>(row.size());
        for (String value : row) {
            values.add(escape(Objects.requireNonNull(value, "CSV value must not be null")));
        }
        return String.join(",", values);
    }

    private static String escape(String value) {
        if (value.contains("\"") || value.contains(",") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static List<String> parseLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                if (inQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (character == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException("CSV quoted field is not closed: " + line);
        }

        values.add(current.toString());
        return values;
    }

    private static void validateHeaders(List<String> headers, Path path) {
        Set<String> seenHeaders = new HashSet<>();
        for (String header : headers) {
            if (header.isBlank()) {
                throw new IllegalArgumentException("CSV header must not be blank: " + path);
            }
            if (!seenHeaders.add(header)) {
                throw new IllegalArgumentException("Duplicate CSV header '" + header + "': " + path);
            }
        }
    }
}
