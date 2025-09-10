package com.renda.leetcode.problems;

import java.util.HashSet;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.util.ListNode;
import com.renda.leetcode.util.ListUtils;

/**
 * LeetCode 141 - Linked List Cycle
 *
 * Runtime 4 ms Beats 10.74%
 * Memory 45.13 MB Beats 7.01%
 *
 * 总用时：15 min 16 s
 * 阅读 5 min 53 s
 * 思考 1 min 55 s
 * 编码 6 min 3 s
 * Debug 1 min 23 s
 */
public class LC141_LinkedListCycle implements LeetCodeProblem {

    public boolean hasCycle(ListNode head) {
        HashSet<ListNode> set = new HashSet<>();
        while (head != null) {
            if (set.contains(head)) return true;
            set.add(head);
            head = head.next;
        }
        return false;
    }

    @Override
    public String problemNumber() {
        return "141";
    }

    @Override
    public void run() {
        System.out.println("LC141 Linked List Cycle: ");
        System.out.println(hasCycle(ListUtils.buildList(new int[]{3,2,0,-4}, 1)));
        System.out.println(hasCycle(ListUtils.buildList(new int[]{1,2}, 0)));
        System.out.println(hasCycle(ListUtils.buildList(new int[]{1}, -1)));
        // Expected Output:
        // true
        // true
        // false
    }

}
