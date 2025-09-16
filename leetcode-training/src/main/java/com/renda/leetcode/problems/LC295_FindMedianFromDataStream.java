package com.renda.leetcode.problems;

import java.util.Comparator;
import java.util.PriorityQueue;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 295 - Find Median from Data Stream
 *
 * 双堆。
 * 左边**最大堆**存较小的一半，右边**最小堆**存较大的一半；
 * 保证两堆大小差 ≤1，左堆允许多一个。
 *
 * Runtime 102 ms Beats 75.43%
 * Memory 63.92 MB Beats 29.95%
 *
 * 总用时：38 min
 * 阅读 2 min 15 s
 * 思考 13 min 54 s
 * 编码 18 min 46 s
 * Debug 3 min 40 s
 */
public class LC295_FindMedianFromDataStream implements LeetCodeProblem {

    private class MedianFinder {
        private PriorityQueue<Integer> LQ;
        private PriorityQueue<Integer> RQ;

        public MedianFinder() {
            LQ = new PriorityQueue<>(Comparator.reverseOrder());
            RQ = new PriorityQueue<>();
        }

        public void addNum(int num) {
            if (LQ.isEmpty() || num <= LQ.peek()) LQ.offer(num);
            else RQ.offer(num);
            if (LQ.size() < RQ.size()) LQ.offer(RQ.poll());
            else if ((RQ.size() + 1) < LQ.size()) RQ.offer(LQ.poll());
        }

        public double findMedian() {
            if (LQ.size() == RQ.size()) return (LQ.peek() + RQ.peek()) / 2.0;
            else return LQ.peek().doubleValue();
        }
    }

    @Override
    public String problemNumber() {
        return "295";
    }

    @Override
    public void run() {
        System.out.println("LC295 Find Median from Data Stream: ");
        MedianFinder obj = new MedianFinder();
        obj.addNum(1);
        System.out.println(obj.findMedian());
        obj.addNum(2);
        System.out.println(obj.findMedian());
        obj.addNum(3);
        System.out.println(obj.findMedian());
        obj.addNum(4);
        System.out.println(obj.findMedian());
        obj.addNum(5);
        System.out.println(obj.findMedian());
    }

}
