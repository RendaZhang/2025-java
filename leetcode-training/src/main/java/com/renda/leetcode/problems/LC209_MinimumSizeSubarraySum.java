package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 209 - Minimum Size Subarray Sum.
 *
 * 使用滑动窗口思路。
 * 正整数数组；右指针扩张累加，`sum >= target` 时左指针尽可能收缩并更新最短长度。
 *
 * Runtime 2 ms Beats 15.07%
 * Memory 57.92 MB Beats 81.49%
 *
 * 总用时：40 min 14 s
 * 阅读 2 min 22 s
 * 思考 13 min 44 s
 * 编码 23 min 38 s
 * Debug 0
 */
public class LC209_MinimumSizeSubarraySum implements LeetCodeProblem {

    public int minSubArrayLen(int target, int[] nums) {
        int min_delta = Integer.MAX_VALUE, start = 0, len = nums.length;
        int sum = nums[0];
        if (sum >= target) return 1;
        for (int i = 1; i < len; i++) {
            sum += nums[i];
            while (sum >= target) {
                min_delta = Math.min(min_delta, i - start);
                sum -= nums[start];
                start++;
            }
        }
        return min_delta == Integer.MAX_VALUE ? 0 : min_delta + 1;
    }

    @Override
    public String problemNumber() {
        return "209";
    }

    @Override
    public void run() {
        System.out.println("LC209 Minimum Size Subarray Sum: ");
        System.out.println(minSubArrayLen(7, new int[]{2,3,1,2,4,3}));
        System.out.println(minSubArrayLen(4, new int[]{1,4,4}));
        System.out.println(minSubArrayLen(11, new int[]{1,1,1,1,1,1,1,1}));
        // Expected Output:
        // 2
        // 1
        // 0
    }

}
