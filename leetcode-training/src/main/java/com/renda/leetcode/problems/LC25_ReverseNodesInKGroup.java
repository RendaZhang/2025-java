package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.util.ListNode;
import com.renda.leetcode.util.ListUtils;

public class LC25_ReverseNodesInKGroup implements LeetCodeProblem {

    public ListNode reverseKGroup(ListNode head, int k) {
        if (k <= 1 || head.next == null) return head;
        ListNode dummyNode = new ListNode(0, head);
        ListNode pNode = dummyNode;
        ListNode eNode = pNode.next;
        ListNode sNode = eNode;
        for (int i = 1; i < k; i++) {
            sNode = sNode != null ? sNode.next : null;
        }
        while (sNode != null) {
            ListNode prev = eNode;
            ListNode curr = eNode.next;
            while (curr != sNode) {
                ListNode temp = curr.next;
                curr.next = prev;
                prev = curr;
                curr = temp;
            }
            // pNode -> sNode -> eNode
            eNode.next = sNode.next;
            sNode.next = prev;
            pNode.next = sNode;
            pNode = eNode;
            eNode = pNode.next;
            sNode = eNode;
            for (int i = 1; i < k; i++) {
                sNode = sNode != null ? sNode.next : null;
            }
        }
        return dummyNode.next;
    }

    @Override
    public String problemNumber() {
        return "25";
    }

    @Override
    public void run() {
        System.out.println(reverseKGroup(ListUtils.buildList(new int[]{1,2,3,4,5}), 2));
        System.out.println(reverseKGroup(ListUtils.buildList(new int[]{1,2,3,4,5}), 3));
        System.out.println(reverseKGroup(ListUtils.buildList(new int[]{1,2,3,4}), 4));
    }

}
