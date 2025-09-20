package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 72 - Edit Distance
 *
 * 二维 DP：
 * - 设 `dp[i][j]` 为 `word1[0..i)` 到 `word2[0..j)` 的最少操作数。
 * - 初始化：`dp[i][0]=i`，`dp[0][j]=j`。
 * - 转移：
 *   - 若 `w1[i-1]==w2[j-1]`，`dp[i][j]=dp[i-1][j-1]`；
 *   - 否则 `dp[i][j] = 1 + min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])`。
 *     - dp[i-1][j] + 1：删掉 word1[i-1]（删除）
 *     - dp[i][j-1] + 1：给 word1 末尾插入 word2[j-1]（插入）
 *     - dp[i-1][j-1] + 1：把 word1[i-1] 替换成 word2[j-1]（替换）
 * - 用一维数组 `dp[j]` 滚动行，变量 `prev` 保存左上角 `dp[i-1][j-1]`。
 *
 * Runtime 4 ms Beats 72.34%
 * Memory 44.99 MB Beats 79.07%
 *
 * 总用时：1 hour 20 min
 * 阅读 1 min 6 s
 * 思考 1 hour 4 min 33 s
 * 编码 14 min 41 s
 * Debug 0
 */
public class LC72_EditDistance implements LeetCodeProblem {

    public int minDistance(String word1, String word2) {
        int n = word1.length(), m = word2.length();
        char[] l1 = word1.toCharArray(), l2 = word2.toCharArray();
        int[][] dp = new int[n+1][m+1];
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        int prev_i = 0;
        for (int i = 1; i <= n; i++) {
            int prev_j = 0;
            for (int j = 1; j <= m; j++) {
                if (l1[prev_i] == l2[prev_j]) {
                    dp[i][j] = dp[prev_i][prev_j];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[prev_i][prev_j], dp[prev_i][j]), dp[i][prev_j]);
                }
                prev_j = j;
            }
            prev_i = i;
        }
        return dp[n][m];
    }

    @Override
    public String problemNumber() {
        return "72";
    }

    @Override
    public void run() {
        System.out.println("LC72 Edit Distance: ");
        System.out.println(minDistance("horse", "ros"));
        System.out.println(minDistance("intention", "execution"));
    }

}
