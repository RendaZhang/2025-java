package com.renda.leetcode.problems;

import java.util.Arrays;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 322 - Coin Change
 *
 * 经典一维 DP / 完全背包
 *
 * Runtime 16 ms Beats 56.12%
 * Memory 44.16 MB Beats 90.36%
 *
 * 总用时：59 min 49 s
 * 阅读 4 min 36 s
 * 思考 40 min 47 s
 * 编码 10 min 35 s
 * Debug 3 min 50 s
 */
public class LC322_CoinChange implements LeetCodeProblem {

    public int coinChange(int[] coins, int amount) {
        if (amount == 0) return 0;
        int n = amount + 1;
        int[] dp = new int[n];
        Arrays.fill(dp, n);
        dp[0] = 0;
        for (int i = 1; i < n; i++) {
            for (int c : coins) {
                if (c <= i) {
                    int t = i - c;
                    if (dp[t] != n) {
                        dp[i] = Math.min(dp[i], dp[t] + 1);
                    }
                }
            }
        }
        return dp[amount] == n ? -1 : dp[amount];
    }

    @Override
    public String problemNumber() {
        return "322";
    }

    @Override
    public void run() {
        System.out.println("LC322 Coin Change: ");
        System.out.println(coinChange(new int[]{1,2,5}, 11));
        System.out.println(coinChange(new int[]{2}, 3));
        System.out.println(coinChange(new int[]{1}, 0));
    }

}
