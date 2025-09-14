package com.renda.leetcode.problems;

import java.util.LinkedList;
import java.util.ListIterator;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 215 - Kth Largest Element in an Array
 *
 * Time Limit Exceeded
 * 38 / 44 testcases passed
 */
public class LC215_KthLargestElementInAnArray implements LeetCodeProblem {

    public int findKthLargest(int[] nums, int k) {
        LinkedList<Integer> topKLinkedList = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            topKLinkedList.add(Integer.MIN_VALUE);
        }
        for (int i = 0; i < nums.length; i++) {
            ListIterator<Integer> iterator = topKLinkedList.listIterator();
            while (iterator.hasNext()) {
                Integer element = iterator.next();
                if (nums[i] > element) {
                    iterator.previous();
                    iterator.add(nums[i]);
                    topKLinkedList.removeLast();
                    break;
                }
            }
        }
        return topKLinkedList.get(k-1);
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
        // Expected Output:
        // 5
        // 4
    }

}
