package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 221 - Maximal Square
 *
 * 二维 DP。
 *
 * 如果 `matrix[i-1][j-1] == '1'`，
 * 则当前以该格为右下角的最大正方形边长
 * `dp[i][j] = 1 + min(dp[i-1][j] , dp[i][j-1], dp[i-1][j-1])`；
 * 否则为 0。
 *
 * Runtime 8 ms Beats 70.36%
 * Memory 60.12 MB Beats 75.45%
 *
 * 总用时：54 min 9 s
 * 阅读 51 s
 * 思考 42 min 5 s
 * 编码 9 min 38 s
 * Debug 1 min 33 s
 */
public class LC221_MaximalSquare implements LeetCodeProblem {

    public int maximalSquare(char[][] matrix) {
        int n = matrix.length;
        if (n == 0) return 0;
        int m = matrix[0].length;
        int[][] dp = new int[n+1][m+1];
        int max_result = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int pre_i = i - 1, pre_j = j - 1;
                if (matrix[pre_i][pre_j] == '1') {
                    dp[i][j] = (Math.min(Math.min(dp[pre_i][pre_j], dp[pre_i][j]), dp[i][pre_j])) + 1;
                    max_result = Math.max(max_result, dp[i][j]);
                } else {
                    dp[i][j] = 0;
                }
            }
        }
        return max_result * max_result;
    }

    @Override
    public String problemNumber() {
        return("221");
    }

    @Override
    public void run() {
        System.out.println("LC221 Maximal Square: ");
        System.out.println(maximalSquare(new char[][]{
            new char[]{'1','0','1','0','0'},
            new char[]{'1','0','1','1','1'},
            new char[]{'1','1','1','1','1'},
            new char[]{'1','0','0','1','0'}
        }));
        System.out.println(maximalSquare(new char[][]{
            new char[]{'0','1'},
            new char[]{'1','0'}
        }));
        System.out.println(maximalSquare(new char[][]{
            new char[]{'0'}
        }));
        System.out.println(maximalSquare(new char[][]{
            new char[]{'1','1','1','1','1'},
            new char[]{'1','1','1','1','1'},
            new char[]{'1','1','1','1','1'},
            new char[]{'1','1','1','1','1'},
            new char[]{'1','1','1','1','0'}
        }));
    }

}
