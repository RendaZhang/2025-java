package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;
import java.util.Arrays;

/**
 * LeetCode 1 - Two Sum.
 */
public class LC1_TwoSum implements LeetCodeProblem {

    public int[] twoSum(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{};
    }

    @Override
    public String problemNumber() {
        return "1";
    }

    @Override
    public void run() {
        int[] result = twoSum(new int[]{2, 7, 11, 15}, 9);
        System.out.println("LC1 Two Sum: " + Arrays.toString(result));
    }
}
