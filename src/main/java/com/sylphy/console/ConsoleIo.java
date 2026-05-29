package com.sylphy.console;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

/**
 * 控制台输入输出封装，集中处理提示语、默认值和整数校验。
 *
 * @author apple
 */
public final class ConsoleIo {
    private final Scanner input;
    private final PrintStream output;

    public ConsoleIo(InputStream inputStream, PrintStream output) {
        this(new Scanner(Objects.requireNonNull(inputStream, "inputStream must not be null")), output);
    }

    public ConsoleIo(Scanner input, PrintStream output) {
        this.input = Objects.requireNonNull(input, "input must not be null");
        this.output = Objects.requireNonNull(output, "output must not be null");
    }

    public void print(String message) {
        output.print(message);
    }

    public void println() {
        output.println();
    }

    public void println(String message) {
        output.println(message);
    }

    public Path readPath(String prompt, Path defaultPath) {
        String line = readLine(prompt + "（直接回车默认 " + defaultPath + "）：");
        if (line.isBlank()) {
            return defaultPath;
        }
        return Path.of(line);
    }

    public int readOptionalPositiveInt(String prompt, int defaultValue) {
        String line = readLine(prompt);
        if (line.isBlank()) {
            return defaultValue;
        }
        Integer value = parseInteger(line);
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("请输入大于 0 的整数。");
        }
        return value;
    }

    public int readInteger(String prompt) {
        String line = readLine(prompt);
        Integer value = parseInteger(line);
        if (value == null) {
            throw new IllegalArgumentException("请输入整数。");
        }
        return value;
    }

    public String readLine(String prompt) {
        print(prompt);
        if (!input.hasNextLine()) {
            return "";
        }
        return input.nextLine().trim();
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
