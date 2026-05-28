package com.sylphy.csv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * CsvFile 的单元测试，验证 CSV 转义、读取和格式错误处理。
 */
class CsvFileTest {
    @TempDir
    Path tempDir;

    @Test
    void writesAndReadsEscapedValues() throws Exception {
        Path path = tempDir.resolve("data.csv");

        CsvFile.writeRows(path, List.of(
                List.of("name", "value"),
                List.of("expression", "1, \"2\"")
        ));

        List<Map<String, String>> rows = CsvFile.readTable(path);

        assertEquals("expression", rows.getFirst().get("name"));
        assertEquals("1, \"2\"", rows.getFirst().get("value"));
    }

    @Test
    void rejectsUnclosedQuotedField() throws Exception {
        Path path = tempDir.resolve("broken.csv");
        CsvFile.writeRows(path, List.of(List.of("name", "value")));
        java.nio.file.Files.writeString(path, "name,value" + System.lineSeparator() + "a,\"b");

        assertThrows(IllegalArgumentException.class, () -> CsvFile.readTable(path));
    }
}
