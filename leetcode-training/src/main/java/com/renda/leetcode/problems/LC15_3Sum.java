package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LC15_3Sum implements LeetCodeProblem {

    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;
        if (n < 3) return result;
        int end = n - 2;
        int last_i = n - 1;
        int prev_i = -1;
        Arrays.sort(nums);
        for (int i = 0; i < end; i++) {
            int num0 = nums[i];
            if (num0 > 0) break;
            if (prev_i >= 0 && nums[prev_i] == nums[i]) continue;
            prev_i = i;
            int k1 = i + 1, k2 = last_i;
            while (k1 < k2) {
                int sum = num0 + nums[k1] + nums[k2];
                if (sum == 0) {
                    result.add(Arrays.asList(num0, nums[k1], nums[k2]));
                    k1++;
                    k2--;
                    while (k1 < k2 && nums[k1] == nums[k1-1]) {k1++;}
                    while (k1 < k2 && nums[k2] == nums[k2+1]) {k2--;}
                } else if (sum > 0) {
                    k2--;
                } else {
                    k1++;
                }
            }
        }
        return result;
    }

    @Override
    public String problemNumber() {
        return "15";
    }

    @Override
    public void run() {
        System.out.println(threeSum(new int[]{0,0,0,0}));
        System.out.println(threeSum(new int[]{-1, 0, 1, 2, -1, -4}));
    }

}
