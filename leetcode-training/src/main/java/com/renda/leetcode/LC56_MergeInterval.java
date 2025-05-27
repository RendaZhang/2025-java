package com.renda.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LC56_MergeInterval {

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

}
