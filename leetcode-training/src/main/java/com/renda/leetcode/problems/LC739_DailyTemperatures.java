package com.renda.leetcode.problems;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 739 - Daily Temperatures
 *
 * 使用单调栈的思路，维护了一个递减栈（存索引）
 *
 * Runtime 23 ms Beats 91.85%
 * Memory 60.35 MB Beats 63.84%
 *
 * 总用时：44 min 39 s
 * 阅读 1 min 7 s
 * 思考 5 min 10 s
 * 编码 33 min 26 s
 * Debug 6 min 10 s
 */
public class LC739_DailyTemperatures implements LeetCodeProblem{

    public int[] dailyTemperatures(int[] temperatures) {
        int len = temperatures.length;
        int[] result = new int[len];
        int index = len - 1;
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(index);
        result[index--] = 0;
        while (index >= 0) {
            while (!stack.isEmpty() && temperatures[index] >= temperatures[stack.peek()]) {
                stack.pop();
            }
            Integer latest_i = stack.peek();
            result[index] = latest_i == null ? 0 : latest_i - index;
            stack.push(index);
            index--;
        }
        return result;
    }

    @Override
    public String problemNumber() {
        return "739";
    }

    @Override
    public void run() {
        System.out.println("LC739 Daily Temperatures: ");
        System.out.println(Arrays.toString(dailyTemperatures(new int[]{73,74,75,71,69,72,76,73})));
        System.out.println(Arrays.toString(dailyTemperatures(new int[]{30,40,50,60})));
        System.out.println(Arrays.toString(dailyTemperatures(new int[]{30,60,90})));
        System.out.println(Arrays.toString(dailyTemperatures(new int[]{89,62,70,58,47,47,46,76,100,70})));
        // Expected Output:
        // [1,1,4,2,1,1,0,0]
        // [1,1,1,0]
        // [1,1,0]
        // [8,1,5,4,3,2,1,1,0,0]
    }

}
