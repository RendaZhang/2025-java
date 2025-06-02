package com.renda.leetcode;

public class LC122_BestTimeToBuyAndSellStockII {

    public int maxProfit(int[] prices) {
        int n = prices.length;
        int profit = 0;
        int tmp = prices[0];
        for (int i = 1; i < n; i++) {
            if (prices[i] > tmp) profit += prices[i] - tmp;
            tmp = prices[i];
        }
        return profit;
    }

}
