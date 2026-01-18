package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.util.ListNode;
import com.renda.leetcode.util.ListUtils;

public class LC21_MergeTwoSortedLists implements LeetCodeProblem {

    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        ListNode p1 = list1, p2 = list2;
        while (p1 != null && p2 != null) {
            if (p1.val <= p2.val) {
                tail.next = p1;
                p1 = p1.next;
            } else {
                tail.next = p2;
                p2 = p2.next;
            }
            tail = tail.next;
        }
        tail.next = (p1 != null) ? p1 : p2;
        return dummy.next;
    }

    @Override
    public String problemNumber() {
        return "21";
    }

    @Override
    public void run() {
        System.out.println(mergeTwoLists(ListUtils.buildList(new int[]{1,2,4}), ListUtils.buildList(new int[]{1,3,4})));
        System.out.println(mergeTwoLists(ListUtils.buildList(new int[]{}), ListUtils.buildList(new int[]{})));
        System.out.println(mergeTwoLists(ListUtils.buildList(new int[]{}), ListUtils.buildList(new int[]{0})));
        System.out.println(mergeTwoLists(ListUtils.buildList(new int[]{1,2,3}), ListUtils.buildList(new int[]{4,5,6})));
    }

}
