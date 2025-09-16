package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 703 - Kth Largest Element in a Stream
 *
 * 维护**大小为 k 的最小堆**；堆内始终是“当前最大的 k 个数”，堆顶即第 k 大。
 *
 * Runtime 16 ms Beats 99.89%
 * Memory 50.38 MB Beats 31.48%
 *
 * 总用时：1 hour 12 min
 * 阅读 + 思考 16 min 45 s
 * 编码 37 min 5 s
 * Debug 18 min 47 s
 */
public class LC703_KthLargestElementInAStream implements LeetCodeProblem {

    private class KthLargest {
        private static int[] minHeap;
        private static int n;
        private static boolean isFull;
        private static int size;

        public KthLargest(int k, int[] nums) {
            minHeap = new int[k];
            size = k;
            int len = nums.length;
            if (len < k) {
                for (int i = 0; i < len; i++) {
                    minHeap[i] = nums[i];
                    shiftUp(i);
                }
                n = len;
                isFull = false;
            } else {
                for (int i = 0; i < k; i++) {
                    minHeap[i] = nums[i];
                    shiftUp(i);
                }
                n = k;
                for (int i = k; i < len; i++) {
                    if (nums[i] > peek()) {
                        minHeap[0] = nums[i];
                        shiftDown(0);
                    }
                }
                isFull = true;
            }
        }

        public int add(int val) {
            if (isFull) {
                if (val > peek()) {
                    minHeap[0] = val;
                    shiftDown(0);
                }
            } else {
                minHeap[n] = val;
                shiftUp(n++);
                if (n == size) isFull = true;
            }
            return peek();
        }

        private int peek() { return v(0); }

        private void shiftUp(int i) {
            while (true) {
                int p = pi(i);
                if (p<0) return;
                if (v(i)>=v(p)) return;
                swap(i, p);
                i = p;
            }
        }

        private void shiftDown(int i) {
            while (true) {
                int l = lci(i);
                if (l>=n) return;
                int r = l+1;
                int t = (r<n)&&(v(r)<v(l))?r:l;
                if (v(t)>=v(i)) return;
                swap(i, t);
                i = t;
            }
        }

        private int pi(int i) {return (i-1)/2;}
        private int lci(int i) {return (i*2)+1;}
        // private int rci(int i) {return (i*2)+2;}
        private void swap(int i, int j) {
            int tmp = minHeap[i];
            minHeap[i] = minHeap[j];
            minHeap[j] = tmp;
        }
        private int v(int i) {return minHeap[i];}
    }

    @Override
    public String problemNumber() {
        return "703";
    }

    /**
     * Your KthLargest object will be instantiated and called as such:
     * KthLargest obj = new KthLargest(k, nums);
     * int param_1 = obj.add(val);
     */
    @Override
    public void run() {
        System.out.println("LC703 Kth Largest Element In A Stream: ");
        System.out.println("Example 1: ");
        KthLargest obj = new KthLargest(3, new int[]{4, 5, 8, 2});
        System.out.println(obj.add(3));   // return 4
        System.out.println(obj.add(5));   // return 5
        System.out.println(obj.add(10));  // return 5
        System.out.println(obj.add(9));   // return 8
        System.out.println(obj.add(4));   // return 8
        System.out.println("Example 2: ");
        KthLargest kthLargest = new KthLargest(4, new int[]{7, 7, 7, 7, 8, 3});
        System.out.println(kthLargest.add(2)); // return 7
        System.out.println(kthLargest.add(10)); // return 7
        System.out.println(kthLargest.add(9)); // return 7
        System.out.println(kthLargest.add(9)); // return 8
    }

}
