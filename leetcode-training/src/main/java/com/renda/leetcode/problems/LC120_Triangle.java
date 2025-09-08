package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 120 - Triangle.
 */
public class LC120_Triangle implements LeetCodeProblem {

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

    @Override
    public String problemNumber() {
        return "120";
    }

    @Override
    public void run() {
        List<List<Integer>> triangle = Arrays.asList(
                List.of(2),
                Arrays.asList(3,4),
                Arrays.asList(6,5,7),
                Arrays.asList(4,1,8,3)
        );
        System.out.println("LC120 Triangle: " + minimumTotal(triangle));
    }
}
