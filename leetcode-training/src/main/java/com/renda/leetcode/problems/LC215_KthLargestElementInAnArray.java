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
        if (nums.length == 1) {
            return nums[0];
        }
        LinkedList<Integer> topKLinkedList = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            topKLinkedList.add(Integer.MIN_VALUE);
        }
        for (int i = 0; i < nums.length; i++) {
            ListIterator<Integer> iterator1 = topKLinkedList.listIterator();
            ListIterator<Integer> iterator2 = topKLinkedList.listIterator(k);
            if (nums[i] <= topKLinkedList.getLast()) {
                continue;
            }
            while (iterator1.hasNext() && iterator2.hasPrevious()) {
                Integer element1 = iterator1.next();
                Integer element2 = iterator2.previous();
                if (nums[i] <= element2) {
                    iterator2.next();
                    iterator2.add(nums[i]);
                    topKLinkedList.removeLast();
                    break;
                } else {
                    if (nums[i] >= element1) {
                        iterator1.previous();
                        iterator1.add(nums[i]);
                        topKLinkedList.removeLast();
                        break;
                    }
                }
                if (iterator1.nextIndex() > iterator2.nextIndex()) break;
            }
        }
        return topKLinkedList.getLast();
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
