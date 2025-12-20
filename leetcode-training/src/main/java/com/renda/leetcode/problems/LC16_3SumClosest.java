package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

import java.util.Arrays;

public class LC16_3SumClosest implements LeetCodeProblem {

    public int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int lo = 0, hi = nums.length - 1;
        int result = 0, delta = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int j = i + 1, k = hi;
            while (j < k) {
                int sum = nums[i] + nums[j] + nums[k];
                if (sum < target) {
                    int delta_tmp = target - sum;
                    if (delta_tmp < delta) {
                        delta = delta_tmp;
                        result = sum;
                    }
                    int num_j = nums[j];
                    while (j < k && nums[j] == num_j) j++;
                } else if (sum > target) {
                    int delta_tmp = sum - target;
                    if (delta_tmp < delta) {
                        delta = delta_tmp;
                        result = sum;
                    }
                    int num_k = nums[k];
                    while (j < k && nums[k] == num_k) k--;
                } else {
                    return target;
                }
            }
        }
        return result;
    }

    @Override
    public String problemNumber() {
        return "16";
    }

    @Override
    public void run() {
        // You may assume that each input may have exactly one solution.
        //System.out.println("The sum that is closest to the target is " + threeSumClosest(new int[]{-1, 2, 1, -4}, 1));
        //System.out.println("The sum that is closest to the target is " + threeSumClosest(new int[]{0, 0, 0}, 1));
        System.out.println("The sum that is closest to the target is " + threeSumClosest(new int[]{-100,-98,-2,-1}, -101));
    }
}
