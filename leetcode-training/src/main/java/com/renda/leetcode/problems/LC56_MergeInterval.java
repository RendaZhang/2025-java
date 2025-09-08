package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * LeetCode 56 - Merge Intervals.
 */
public class LC56_MergeInterval implements LeetCodeProblem {

    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        List<int[]> output = new ArrayList<>();
        output.add(intervals[0]);
        for (int[] interval : intervals) {
            int[] last = output.getLast();
            if (last[1] >= interval[0]) {
                last[1] = Math.max(last[1], interval[1]);
                last[0] = Math.min(last[0], interval[0]);
            } else {
                output.addLast(interval);
            }
        }
        return output.toArray(new int[output.size()][]);
    }

    @Override
    public String problemNumber() {
        return "56";
    }

    @Override
    public void run() {
        int[][] merged = merge(new int[][]{{2,3},{4,5},{6,7},{8,9},{1,10}});
        System.out.println("LC56 Merge Interval: " + Arrays.deepToString(merged));
    }
}
