package com.renda.leetcode;

import java.util.List;

public class LC120_Triangle {

    public int minimumTotal(List<List<Integer>> triangle) {
        int[] dp = new int[triangle.getLast().size()];
        dp[0] = triangle.getFirst().getFirst();
        for (int i = 1; i < triangle.size(); i++) {
            int end = triangle.get(i).size() - 1;
            dp[end] = dp[end - 1] + triangle.get(i).get(end);
            for (int j = end - 1; j > 0; j--) {
                dp[j] = Math.min(dp[j], dp[j - 1]) + triangle.get(i).get(j);
            }
            dp[0] = dp[0] + triangle.get(i).getFirst();
        }
        int min_sum = Integer.MAX_VALUE;
        for (int j : dp) if (j < min_sum) min_sum = j;
        return min_sum;
    }

}
