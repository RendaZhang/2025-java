package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

import java.util.Arrays;

public class LC27_RemoveElement implements LeetCodeProblem {

    public int removeElement(int[] nums, int val) {
        int k = nums.length - 1;
        int i = 0;
        while (i <= k) {
            if (nums[i] == val) nums[i] = nums[k--];
            else i++;
        }
        return k + 1;
    }

    @Override
    public String problemNumber() {
        return "27";
    }

    @Override
    public void run() {
        int[] nums1 = new int[]{3,2,2,3};
        int k1 = removeElement(nums1, 3);
        System.out.println(k1);
        System.out.println(Arrays.toString(nums1));
        int[] nums2 = new int[]{0,1,2,2,3,0,4,2};
        int k2 = removeElement(nums2, 2);
        System.out.println(k2);
        System.out.println(Arrays.toString(nums2));
    }

}
