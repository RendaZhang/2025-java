package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 378 - Kth Smallest Element in a Sorted Matrix
 *
 * 矩阵每行、每列递增。
 * 设值域 `[lo, hi] = [matrix[0][0], matrix[n-1][n-1]]`；
 * 二分 `mid`，用“**从左下角/右上角起数 ≤mid 的元素个数**”来决定收缩区间。
 *
 * Runtime 0 ms Beats 100.00%
 * Memory 49.77 MB Beats 39.25%
 */
public class LC378_KthSmallestElementInASortedMatrix implements LeetCodeProblem {

    public int kthSmallest(int[][] matrix, int k) {
        int n = matrix.length, m = n - 1, lo = matrix[0][0], hi = matrix[m][m], mid;
        while (lo < hi) {
            mid = lo + ((hi - lo) >> 1);
            if (countLE(matrix, m, mid) >= k) hi = mid;
            else lo = mid + 1;
        }
        return lo;
    }

    private int countLE(int[][] a, int m, int target) {
        int n = a.length, i = m, j = 0, count = 0;
        while (i >= 0 && j < n) {
            if (a[i][j] <= target) { j++; count += i + 1; }
            else i--;
        }
        return count;
    }

    @Override
    public String problemNumber() {
        return "378";
    }

    @Override
    public void run() {
        System.out.println("LC378 Kth Smallest Element in a Sorted Matrix: ");
        System.out.println(kthSmallest(new int[][]{{1, 2}, {1, 3}}, 3));
        System.out.println(kthSmallest(new int[][]{{1, 5, 9}, {10, 11, 13}, {12, 13, 15}}, 8));
        System.out.println(kthSmallest(new int[][]{{-5}}, 1));
    }

}
