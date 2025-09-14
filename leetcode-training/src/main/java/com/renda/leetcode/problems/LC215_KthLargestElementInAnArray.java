package com.renda.leetcode.problems;

import java.util.PriorityQueue;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 215 - Kth Largest Element in an Array
 *
 * 堆
 *
 * 维持一个大小为 k 的最小堆
 *
 * Runtime 43 ms Beats 62.54%
 * Memory 57.61 MB Beats 68.32%
 */
public class LC215_KthLargestElementInAnArray implements LeetCodeProblem {

    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        for (int n : nums) {
            if (queue.size() < k) {
                queue.offer(n);
            } else if (n > queue.peek()) {
                queue.poll();
                queue.offer(n);
            }
        }
        return queue.peek();
    }

    @Override
    public String problemNumber() {
        return "215";
    }

    @Override
    public void run() {
        System.out.println("LC215 Kth Largest Element in an Array: ");
        System.out.println(findKthLargest(new int[]{3,2,1,5,6,4}, 2));
        System.out.println(findKthLargest(new int[]{3,2,3,1,2,4,5,5,6}, 4));
        System.out.println(findKthLargest(new int[]{1}, 1));
        System.out.println(findKthLargest(new int[]{2,1}, 2));
        // Expected Output:
        // 5
        // 4
        // 1
        // 1
    }

}
