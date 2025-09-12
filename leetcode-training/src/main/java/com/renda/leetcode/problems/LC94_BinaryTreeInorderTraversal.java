package com.renda.leetcode.problems;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.util.TreeNode;
import com.renda.leetcode.util.TreeUtils;

/**
 * LeetCode 94 - Binary Tree Inorder Traversal
 *
 * 迭代中序 + 栈
 *
 * Runtime 0 ms Beats 100.00%
 * Memory 41.65 MB Beats 66.85% Analyze
 *
 * 总用时：41 min 54 s
 * 阅读 + 思考 9 min 8 s
 * 编码 13 min 50 s
 * Debug 18 min 50 s
 */
public class LC94_BinaryTreeInorderTraversal implements LeetCodeProblem {

    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> resultList = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        while (root != null) {
            if (root.left != null) {
                TreeNode temp  = root.left;
                root.left = null;
                stack.push(root);
                root = temp;
            } else {
                resultList.addLast(root.val);
                root = root.right == null ? (stack.isEmpty() ? null : stack.pop()) : root.right;
            }
        }
        return resultList;
    }

    @Override
    public String problemNumber() {
        return "94";
    }

    @Override
    public void run() {
        System.out.println("LC94 Binary Tree Inorder Traversal: ");
        System.out.println(inorderTraversal(TreeUtils.buildTree(new Integer[]{1,null,2,3})));
        System.out.println(inorderTraversal(TreeUtils.buildTree(new Integer[]{1,2,3,4,5,null,8,null,null,6,7,9})));
        System.out.println(inorderTraversal(TreeUtils.buildTree(new Integer[]{})));
        System.out.println(inorderTraversal(TreeUtils.buildTree(new Integer[]{1})));
    }

}
