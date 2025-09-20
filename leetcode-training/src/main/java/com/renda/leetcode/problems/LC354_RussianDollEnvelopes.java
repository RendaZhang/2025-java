package com.renda.leetcode.problems;

import java.util.Arrays;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode - 354 Russian Doll Envelopes
 *
 * Runtime 37 ms Beats 97.86%
 * Memory 104.62 MB Beats 23.96%
 *
 * 总用时：3 hour 27 min
 * 阅读 8 min 3 s
 * 思考 2 hour 36 min 8 s
 * 编码 42 min 59 s
 * Debug 0
 */
public class LC354_RussianDollEnvelopes implements LeetCodeProblem {

    public int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, (a,b)->{
            if (a[0] == b[0]) return b[1] - a[1];
            else return a[0] - b[0];
        });
        int len = 0;
        int[] list = new int[envelopes.length];
        for (int[] envelope : envelopes) {
            int h = envelope[1];
            int i = binarySearch(list, 0, len, h);
            list[i] = h;
            if (i == len) len++;
        }
        return len;
    }

    private int binarySearch(int[] a, int i, int len, int val) {
        while (len > 0) {
            int len2 = len >> 1;
            int mid = i + len2;
            if (a[mid] == val) return mid;
            else if (a[mid] > val) len = len2;
            else {
                i = mid + 1;
                len = len - len2 - 1;
            }
        }
        return i;
    }

    @Override
    public String problemNumber() {
        return "354";
    }

    @Override
    public void run() {
        System.out.println("LC354 Russian Doll Envelopes: ");
        System.out.println(maxEnvelopes(new int[][]{
            new int[]{5,4},
            new int[]{6,4},
            new int[]{6,7},
            new int[]{2,3}
        }));
        System.out.println(maxEnvelopes(new int[][]{
            new int[]{1,1},
            new int[]{1,1},
            new int[]{1,1}
        }));
        System.out.println(maxEnvelopes(new int[][]{
            new int[]{1,10},
            new int[]{2,2},
            new int[]{3,5},
            new int[]{4,2},
            new int[]{5,10},
            new int[]{5,5}
        }));
    }

}
