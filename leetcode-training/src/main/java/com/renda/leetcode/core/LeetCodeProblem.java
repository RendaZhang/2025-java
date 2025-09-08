package com.renda.leetcode.core;

/**
 * 基础接口：所有 LeetCode 练习题需实现该接口。
 * <p>
 * 使用方式：
 * <ol>
 *   <li>实现 {@code run} 方法，编写题目的示例调用。</li>
 *   <li>实现 {@code problemNumber} 方法，返回题目在 LeetCode 上的编号。</li>
 *   <li>在 {@link ProblemRegistry} 中注册即可通过 {@link com.renda.leetcode.Main} 运行。</li>
 * </ol>
 */
public interface LeetCodeProblem {
    /**
     * @return 题目的编号，如 "1"、"200" 等。
     */
    String problemNumber();

    /**
     * 运行示例代码以演示算法。
     */
    void run();
}
