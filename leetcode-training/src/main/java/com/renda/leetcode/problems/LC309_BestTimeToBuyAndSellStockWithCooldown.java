package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 309 - Best Time to Buy and Sell Stock with Cooldown
 *
 * 状态机 DP：
 * 每天结束时，你只可能处在三种“状态”之一：
 * - 持有股票 hold
 * - 今天刚卖出 sold（明天会被冷冻一天，不能立刻买）
 * - 空仓且不在冷冻期 rest（可以随时买）
 * 把它当成一个三格“状态机”，每天在三格之间做最优转移：
 * - 继续持有或今天买入：
 *   - hold[i] = max(hold[i-1], rest[i-1] - price[i])
 *   - 解释：要么沿用昨天的持仓，要么昨天是空仓可买，今天花钱买入。
 * - 今天卖出：
 *   - sold[i] = hold[i-1] + price[i]
 *   - 解释：只有昨天手里有股，今天才能卖。
 * - 空仓可买（不在冷冻期）
 *   - rest[i] = max(rest[i-1], sold[i-1])
 *   - 解释：要么一直空仓，要么昨天刚卖出，今天冷冻结束，回到可买状态。
 * 答案是 max(sold[n-1], rest[n-1])，因为最后一天手上有股不如把它卖掉或保持空仓更优。
 *
 * Runtime 0 ms Beats 100.00%
 * Memory 42.13 MB Beats 25.07%
 */
public class LC309_BestTimeToBuyAndSellStockWithCooldown implements LeetCodeProblem {

    public int maxProfit(int[] prices) {
        int INF = Integer.MIN_VALUE / 2; // 防溢出
        int sold = INF, hold = INF, rest =  0;
        for (int price : prices) {
            int prevRest = rest;
            // 维持空仓 or 冷冻结束
            rest = Math.max(rest, sold);
            // 今天卖出（只能从持有来）
            sold = hold + price;
            // 继续持有 or 今天买入
            hold = Math.max(hold, prevRest - price);
        }
        rest = Math.max(rest, sold);
        return rest;
    }

    @Override
    public String problemNumber() {
        return "309";
    }

    @Override
    public void run() {
        System.out.println("LC309 Best Time To Buy And Sell Stock With Cooldown: ");
        System.out.println(maxProfit(new int[]{1001,1002,1003,500,1002}));
        System.out.println(maxProfit(new int[]{1,2,3,0,2}));
        System.out.println(maxProfit(new int[]{1}));
    }

}
