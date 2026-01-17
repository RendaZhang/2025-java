package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.util.ListNode;
import com.renda.leetcode.util.ListUtils;

public class LC19_RemoveNthNodeFromEndOfList implements LeetCodeProblem {

    public ListNode removeNthFromEnd(ListNode head, int n) {
        if (head == null) return null;
        ListNode tempNode = head;
        int count = 0;
        while (tempNode != null) {
            count++;
            tempNode = tempNode.next;
        }
        int m = count - n;
        if (m <= 0) return head.next;
        ListNode currNode = head;
        ListNode prevNode = currNode;
        for (int i = 0; i < m; i++) {
            prevNode = currNode;
            currNode = currNode.next;
        }
        prevNode.next = currNode.next;
        return head;
    }

    @Override
    public String problemNumber() {
        return "19";
    }

    @Override
    public void run() {
        System.out.println(removeNthFromEnd(ListUtils.buildList(new int[]{1, 2, 3, 4, 5}), 2));
        System.out.println(removeNthFromEnd(ListUtils.buildList(new int[]{1}), 1));
        System.out.println(removeNthFromEnd(ListUtils.buildList(new int[]{1, 2}), 1));
    }
}
