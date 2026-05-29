package com.sylphy;

import java.io.IOException;

/**
 * 应用程序唯一入口，负责启动控制台菜单界面。
 * @author apple
 */
public class ArithmeticGenerator {
    public static void main(String[] args) throws IOException {
        new ConsoleApplication(System.in, System.out).run();
    }
}
