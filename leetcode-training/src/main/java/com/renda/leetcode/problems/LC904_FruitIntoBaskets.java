package com.renda.leetcode.problems;

import java.util.HashSet;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 904 - Fruit Into Baskets.
 *
 * 使用滑动窗口思路，维护了一个“最多包含两种元素”的窗口。
 * 使用了 HashSet 数据结构配合 curr_max 来模拟 Basket。
 *
 * 优化技巧：
 * 在集合的容量变化不大的情况下，
 * 循环中清空集合,比起每次创建新集合，
 * 可以减少内存分配开销和减少垃圾回收压力。
 *
 * Runtime 19 ms Beats 83.75%
 * Memory 59.20 MB Beats 5.76%
 *
 * 总用时：1 hour 42 min
 * 阅读 5 min 15 s
 * 思考 14 min 23 s
 * 编码 1 hour 5 min 15 s
 * Debug 17 min 39 s
 */
public class LC904_FruitIntoBaskets implements LeetCodeProblem {

    public int totalFruit(int[] fruits) {
        int len = fruits.length;
        if (len < 3) return len;
        HashSet<Integer> basket = new HashSet<>(2);
        basket.add(fruits[0]);
        int result = 1, curr_max = 1, start = 0, next_start = 0, curr_type = fruits[0];
        for (int i = 1; i < len; i++) {
            if (basket.contains(fruits[i])) {
                curr_max++;
            } else {
                if (basket.size() < 2) {
                    basket.add(fruits[i]);
                    curr_max++;
                } else {
                    result = Math.max(curr_max, result);
                    curr_max = curr_max - (next_start - start) + 1;
                    start = next_start;
                    basket.clear();
                    basket.add(curr_type);
                    basket.add(fruits[i]);
                }
            }
            if (fruits[i] != curr_type) {
                curr_type = fruits[i];
                next_start = i;
            }
        }
        return Math.max(curr_max, result);
    }

    @Override
    public String problemNumber() {
        return "904";
    }

    @Override
    public void run() {
        System.out.println("LC904 Fruit Into Baskets: ");
        System.out.println(totalFruit(new int[]{1,2,1}));
        System.out.println(totalFruit(new int[]{0,1,2,2}));
        System.out.println(totalFruit(new int[]{1,2,3,2,2}));
        System.out.println(totalFruit(new int[]{1,0,3,4,3}));
        // expected:
        // 3
        // 3
        // 4
        // 3
    }

}
