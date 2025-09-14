package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.util.ListNode;
import com.renda.leetcode.util.ListUtils;

/**
 * LeetCode 23 - Merge K Sorted Lists
 *
 * Wrong Answer 71 / 134 testcases passed
 * Input: [[-8,-7,-7,-5,1,1,3,4],[-2],[-10,-10,-7,0,1,3],[2]]
 * Output: [-10,-10,-7,-2,0,-8,-7,-7,-5,1,1,1,2,3,3,4]
 * Expected: [-10,-10,-8,-7,-7,-7,-5,-2,0,1,1,1,2,
 *
 */
public class LC23_MergeKSortedLists implements LeetCodeProblem {

    public ListNode mergeKLists(ListNode[] lists) {
        int len = lists.length;
        ListNode[] minHeap = new ListNode[len];
        int heapSize = 0;
        for (int i = 0; i < len; i++) {
            if (lists[i] != null) {
                insert(minHeap, heapSize, lists[i]);
                heapSize++;
            }
        }
        ListNode dummyNode = new ListNode(0);
        ListNode tailNode = dummyNode;
        while (heapSize > 0) {
            tailNode = tailNode.next = minHeap[0];
            heapSize = removeMin(minHeap, heapSize);
            if (tailNode.next != null) insert(minHeap, heapSize++, tailNode.next);
        }
        return dummyNode.next;
    }

    private void insert(ListNode[] a, int i, ListNode v) {
        a[i] = v;
        shiftUp(a, i);
    }

    private int removeMin(ListNode[] a, int n) {
        if (n <= 0) return n;
        n--;
        a[0] = a[n];
        a[n] = null;
        shiftDown(a, 0, n);
        return n;
    }

    private void shiftUp(ListNode[] a, int i) {
        while (true) {
            int p = pIndex(i);
            if (p < 0) return;
            if (a[i].val >= a[p].val) return;
            swap(a, i, p);
            i = p;
        }
    }

    private void shiftDown(ListNode[] a, int i, int n) {
        while (true) {
            int l = lcIndex(i);
            if (l >= n) return;
            int r = l + 1;
            int smallest = (r < n && a[r].val < a[l].val) ? r : l;
            if (a[i].val <= a[smallest].val) return;
            swap(a, i, smallest);
            i = smallest;
        }
    }

    private int lcIndex(int i) { return i << 1 + 1; }

    private int pIndex(int i) { return (i - 1) / 2; }

    private void swap(ListNode[] a, int i, int j) {
        ListNode t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    @Override
    public String problemNumber() {
        return "23";
    }

    @Override
    public void run() {
        ListNode[] list1 = new ListNode[]{
            ListUtils.buildList(new int[]{1,4,5}),
            ListUtils.buildList(new int[]{1,3,4}),
            ListUtils.buildList(new int[]{2,6})
        };
        ListNode[] list2 = new ListNode[]{};
        ListNode[] list3 = new ListNode[]{
            ListUtils.buildList(new int[]{})
        };
        ListNode[] list4 = new ListNode[]{
            ListUtils.buildList(new int[]{}),
            ListUtils.buildList(new int[]{1})
        };
        System.out.println("LC23 Merge K Sorted Lists: ");
        System.out.println(mergeKLists(list1));
        System.out.println(mergeKLists(list2));
        System.out.println(mergeKLists(list3));
        System.out.println(mergeKLists(list4));
        // Expected Output:
        // [1,1,2,3,4,4,5,6]
        // []
        // []
    }

}
