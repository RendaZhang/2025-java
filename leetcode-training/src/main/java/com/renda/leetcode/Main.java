package com.renda.leetcode;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.core.ProblemRegistry;

/**
 * 程序入口，可通过命令行指定题号运行对应算法。
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("请输入要运行的题号，例如: java Main 1");
            System.out.println("可用题号: " + ProblemRegistry.availableProblems());
            return;
        }
        LeetCodeProblem problem = ProblemRegistry.getProblem(args[0]);
        if (problem == null) {
            System.out.println("未找到题号: " + args[0]);
            System.out.println("可用题号: " + ProblemRegistry.availableProblems());
            return;
        }
        problem.run();
    }
}
