package com.renda.leetcode;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.core.ProblemRegistry;

/**
 * 程序入口，可通过命令行指定题号运行对应算法。
 */
public class Main {

    public static void main(String[] args) {
        String problemNumber = resolveProblemNumber(args);

        if (problemNumber == null || problemNumber.isEmpty()) {
            System.out.println("未输入有效的题号，程序已退出。");
            return;
        }

        LeetCodeProblem problem = ProblemRegistry.getProblem(problemNumber);
        if (problem == null) {
            System.out.println("未找到题号: " + problemNumber);
            System.out.println("可用题号: " + ProblemRegistry.availableProblems());
            return;
        }
        problem.run();
    }

    private static String resolveProblemNumber(String[] args) {
        if (args.length > 0 && args[0] != null && !args[0].isBlank()) {
            return args[0].trim();
        }

        System.out.println("请输入要运行的题号，例如: java Main 1");
        System.out.println("可用题号: " + ProblemRegistry.availableProblems());
        System.out.print("在 IntelliJ IDEA 中直接运行 Main.main 后输入题号并回车: ");

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input != null) {
                return input.trim();
            }
        }
        return null;
    }
}
