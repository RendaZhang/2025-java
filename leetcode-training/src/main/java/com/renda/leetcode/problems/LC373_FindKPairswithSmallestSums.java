package com.renda.leetcode.problems;



import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 373 - Find K Pairs with Smallest Sums
 *
 * 最小堆。
 * 把 `(i,0)`（固定 `A[i]`，配 `B[0]`）入 **小顶堆**（按 `A[i]+B[j]` 排序），
 * 每次弹 `(i,j)` 后把 `(i, j+1)` 入堆。
 *
 * Runtime 25 ms Beats 97.87%
 * Memory 60.20 MB Beats 48.33%
 *
 * 总用时：55 min
 * 阅读 3 min 12 s
 * 思考 32 min 43 s
 * 编码 18 min 3 s
 * Debug 1 min 13 s
 */
public class LC373_FindKPairswithSmallestSums implements LeetCodeProblem {

    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> resultList = new ArrayList<List<Integer>>();
        if (k <= 0 || nums1.length == 0 || nums2.length == 0) return resultList;
        PriorityQueue<int[]> queue = new PriorityQueue<>((a,b)->((nums1[a[0]]+nums2[a[1]])-(nums1[b[0]]+nums2[b[1]])));
        int m = Math.min(k, nums1.length);
        for (int i = 0; i < m; i++) queue.add(new int[]{i,0});
        while (k-- > 0 && !queue.isEmpty()) {
            int[] t = queue.poll();
            resultList.add(List.of(nums1[t[0]],nums2[t[1]]));
            int j = t[1]+1;
            if (j < nums2.length) queue.add(new int[]{t[0],j});
        }
        return resultList;
    }

    @Override
    public String problemNumber() {
        return "373";
    }

    @Override
    public void run() {
        System.out.println("LC373 Find K Pairs with Smallest Sums: ");
        System.out.println(kSmallestPairs(new int[]{1, 7}, new int[]{3, 4}, 2));
        System.out.println(kSmallestPairs(new int[]{1, 1, 2}, new int[]{1, 2, 3}, 2));
        System.out.println(kSmallestPairs(new int[]{1, 2}, new int[]{3}, 3));
    }

}
