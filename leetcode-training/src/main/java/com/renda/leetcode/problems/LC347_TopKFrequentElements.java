package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;
import java.util.*;

/**
 * LeetCode 347 - Top K Frequent Elements.
 *
 * 哈希计数 + 最大堆
 *
 * Runtime 13 ms Beats 75.63%
 * Memory 48.79 MB Beats 54.68%
 */
public class LC347_TopKFrequentElements implements LeetCodeProblem {

    public int[] topKFrequent(int[] nums, int k) {
        int n = nums.length;
        if (n == k) return nums;
        int[] result = new int[k];
        HashMap<Integer, Integer> heap = new HashMap<>();
        for (int i = 0; i < n; i++) heap.put(nums[i], heap.getOrDefault(nums[i], 0) + 1);
        PriorityQueue<Map.Entry<Integer, Integer>> priorityQueue = new PriorityQueue<>(heap.size(), (a, b) -> (b.getValue() - a.getValue()));
        priorityQueue.addAll(heap.entrySet());
        for (int i = 0; i < k; i++) {
            result[i] = priorityQueue.poll().getKey();
        }
        return result;
    }

    @Override
    public String problemNumber() {
        return "347";
    }

    @Override
    public void run() {
        System.out.println("LC347 Top K Frequent Elements: " + Arrays.toString(topKFrequent(new int[]{1,1,1,2,2,3}, 2)));
    }
}
