package com.sylphy;

import com.sylphy.config.GeneratorConfig;
import com.sylphy.console.ConfigLoader;
import com.sylphy.console.ConsoleIo;
import com.sylphy.console.PracticeFileLocator;
import com.sylphy.console.PracticeWorkflow;
import com.sylphy.service.ArithmeticProblemGenerator;
import com.sylphy.service.GradingService;
import com.sylphy.writer.GradingReportWriter;
import com.sylphy.writer.ProblemFileWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

/**
 * 控制台菜单应用，整合练习生成、录入、批改和机器练习功能。
 * @author apple
 */
public final class ConsoleApplication {
    private final ConsoleIo io;
    private final PracticeWorkflow workflow;

    public ConsoleApplication(InputStream inputStream, PrintStream output) {
        this(
                new ConsoleIo(inputStream, output),
                ArithmeticProblemGenerator.Factory.createDefault(),
                new ProblemFileWriter(),
                new GradingReportWriter(),
                new GradingService(),
                GeneratorConfig::loadDefault
        );
    }

    ConsoleApplication(
            Scanner input,
            PrintStream output,
            ArithmeticProblemGenerator generator,
            ProblemFileWriter problemWriter,
            GradingReportWriter reportWriter,
            GradingService gradingService,
            ConfigLoader configLoader
    ) {
        this(
                new ConsoleIo(input, output),
                generator,
                problemWriter,
                reportWriter,
                gradingService,
                configLoader
        );
    }

    private ConsoleApplication(
            ConsoleIo io,
            ArithmeticProblemGenerator generator,
            ProblemFileWriter problemWriter,
            GradingReportWriter reportWriter,
            GradingService gradingService,
            ConfigLoader configLoader
    ) {
        this.io = Objects.requireNonNull(io, "io must not be null");
        this.workflow = new PracticeWorkflow(
                io,
                generator,
                problemWriter,
                reportWriter,
                gradingService,
                configLoader,
                new PracticeFileLocator()
        );
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = io.readLine("请选择......");
            try {
                switch (choice) {
                    case "1" -> workflow.generateBatchProblems();
                    case "2" -> workflow.selectAndPrintProblems();
                    case "3" -> workflow.editStudentAnswers();
                    case "4" -> workflow.gradeSelectedPractice();
                    case "5" -> workflow.gradeBatchPractice();
                    case "6" -> workflow.completePracticeOnComputer();
                    case "0" -> {
                        io.println("已退出。");
                        running = false;
                    }
                    default -> io.println("无效选择，请输入菜单前面的数字。");
                }
            } catch (IOException | RuntimeException exception) {
                io.println("执行失败：" + exception.getMessage());
            }
            io.println();
        }
    }

    private void printMenu() {
        io.println("100以内的口算练习程序");
        io.println("============================================================");
        io.println("功能列表（请输入功能前面对应的数字，按回车键执行）：");
        io.println("------------------------------------------------------------");
        io.println("1. 批量生成练习套卷");
        io.println("2. 选择题目并生成打印版");
        io.println("3. 录入纸面练习答案");
        io.println("4. 批改单套练习");
        io.println("5. 批量批改多套练习");
        io.println("6. 小明电脑练习并批改");
        io.println("0. 退出");
        io.println("============================================================");
    }
}
