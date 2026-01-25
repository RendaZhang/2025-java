package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

public class LC26_RemoveDuplicatesFromSortedArray implements LeetCodeProblem {

    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;
        int k = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[k - 1]) {
                nums[k++] = nums[i];
            }
        }
        return k;
    }

    @Override
    public String problemNumber() {
        return "26";
    }

    @Override
    public void run() {
        test(new int[]{1,1,2}, new int[]{1,2});
        test(new int[]{0,0,1,1,1,2,2,3,3,4}, new int[]{0,1,2,3,4});
    }

    void test(int[] nums, int[] expectedNums) {
        int k = removeDuplicates(nums);
        if (k != expectedNums.length) {
            System.out.println("Array length is incorrect");
            return;
        }
        for (int i = 0; i < k; i++) {
            if (nums[i] != expectedNums[i]) {
                System.out.println("Incorrect Answer");
                return;
            }
        }
        System.out.println("Correct");
    }
}
