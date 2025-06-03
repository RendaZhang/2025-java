package com.renda.leetcode;

import java.util.*;

public class LC347_TopKFrequentElements {

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

}
