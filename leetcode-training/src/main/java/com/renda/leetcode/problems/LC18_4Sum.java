package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LC18_4Sum implements LeetCodeProblem {

    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>(4);
        if (nums.length < 4) return result;
        Arrays.sort(nums);
        long t = target;
        if (nums[0] > 0 && t < 0) return result;
        int end_i = nums.length - 3;
        int end_j = nums.length - 2;
        int end_l = nums.length - 1;
        for (int i = 0; i < end_i; i++) {
            if (i > 0 && nums[i] == nums[i-1]) continue;
            long min1 = (long) nums[i] + nums[i+1] + nums[i+2] + nums[i+3];
            if (min1 > t) break;
            long max1 = (long) nums[i] + nums[end_i] + nums[end_j] + nums[end_l];
            if (max1 < t) continue;
            int i1 = i + 1;
            for (int j = i1; j < end_j; j++) {
                if (j > i1 && nums[j] == nums[j-1]) continue;
                long min2 = (long) nums[i] + nums[j] + nums[j+1] + nums[j+2];
                if (min2 > t) break;
                long max2 = (long) nums[i] + nums[j] + nums[end_j] + nums[end_l];
                if (max2 < t) continue;
                int k = j + 1;
                int l = end_l;
                while (k < l) {
                    long sum = (long) nums[i] + nums[j] + nums[k] + nums[l];
                    if (sum == t) {
                        result.add(List.of(nums[i], nums[j], nums[k], nums[l]));
                        int num_k = nums[k];
                        int num_l = nums[l];
                        while (k < l && num_k == nums[k]) k++;
                        while (k < l && num_l == nums[l]) l--;
                    } else if (sum < t) {
                        int num_k = nums[k];
                        while (k < l && num_k == nums[k]) k++;
                    } else {
                        int num_l = nums[l];
                        while (k < l && num_l == nums[l]) l--;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String problemNumber() {
        return "18";
    }

    @Override
    public void run() {
        System.out.println(fourSum(new int[]{0,0,0,1000000000,1000000000,1000000000,1000000000}, 1000000000));
        System.out.println(fourSum(new int[]{0,0,0,0}, 1));
        System.out.println(fourSum(new int[]{1,-2,-5,-4,-3,3,3,5}, -11));
        System.out.println(fourSum(new int[]{1000000000,1000000000,1000000000,1000000000}, -294967296));
        System.out.println(fourSum(new int[]{1,0,-1,0,-2,2}, 0));
        System.out.println(fourSum(new int[]{2,2,2,2,2}, 8));
        System.out.println(fourSum(new int[]{-2,0,1,-3,2,0,-5,4}, 0));
    }
}
