package com.renda.leetcode.problems;

import java.util.Arrays;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 167 Two Sum II - Input Array Is Sorted.
 *
 * 使用了双指针的思路。
 *
 * Runtime 2 ms Beats 92.60%
 * Memory 47.27 MB Beats 52.29%
 *
 * 总用时：33 min 17 s
 * 阅读 2 min 15 s
 * 思考 17 min 33 s
 * 编码 12 min 57 s
 * Debug 0
 */
public class LC167_TwoSumII_InputArrayIsSorted implements LeetCodeProblem {

    public int[] twoSum(int[] numbers, int target) {
        int len = numbers.length;
        int left = 0, right = len - 1;
        int sum = numbers[left] + numbers[right];
        while (sum != target && left < right) {
            if (sum > target) {
                right--;
            } else {
                left++;
            }
            sum = numbers[left] + numbers[right];
        }
        return new int[]{left+1, right+1};
    }

    @Override
    public String problemNumber() {
        return "167";
    }

    @Override
    public void run() {
        System.out.println("LC167 Two Sum II - Input Array Is Sorted: ");
        System.out.println(Arrays.toString(twoSum(new int[]{2,7,11,15}, 9)));
        System.out.println(Arrays.toString(twoSum(new int[]{2,3,4}, 6)));
        System.out.println(Arrays.toString(twoSum(new int[]{-1,0}, -1)));
        System.out.println(Arrays.toString(twoSum(new int[]{2,7,11,15,16,17,20}, 24)));
        // Expected Output:
        // [1,2]
        // [1,3]
        // [1,2]
    }

}
