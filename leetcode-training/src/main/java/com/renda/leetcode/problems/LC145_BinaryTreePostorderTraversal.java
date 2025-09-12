package com.renda.leetcode.problems;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.util.TreeNode;
import com.renda.leetcode.util.TreeUtils;

/**
 * LeetCode 145 - Binary Tree Postorder Traversal
 *
 * 双栈法
 *
 * Runtime 1 ms Beats 6.81%
 * Memory 41.65 MB Beats 67.31%
 *
 */
public class LC145_BinaryTreePostorderTraversal implements LeetCodeProblem {

    public List<Integer> postorderTraversal(TreeNode root) {
        ArrayDeque<TreeNode> stack1 = new ArrayDeque<>();
        ArrayDeque<TreeNode> stack2 = new ArrayDeque<>();
        LinkedList<Integer> resultList = new LinkedList<>();
        if (root == null) return resultList;
        if (root.left == null && root.right == null) {
            resultList.add(root.val);
            return resultList;
        }
        while (root != null || !stack1.isEmpty()) {
            while (root != null) {
                stack1.push(root);
                if (root.right != null) stack1.push(root.right);
                root = root.left;
            }
            root = stack1.pop();
            if (root.right == null && root.left == null) {
                resultList.addLast(root.val);
                stack2.push(root);
                if (!stack1.isEmpty()) root = stack1.pop();
            }
            while (!stack2.isEmpty() && (root.right == stack2.peek() || root.left == stack2.peek())) {
                if (!stack2.isEmpty() && root.right == stack2.peek()) stack2.pop();
                if (!stack2.isEmpty() && root.left == stack2.peek()) stack2.pop();
                resultList.addLast(root.val);
                stack2.push(root);
                if (stack1.isEmpty()) return resultList;
                root = stack1.pop();
            }
        }
        return resultList;
    }

    @Override
    public String problemNumber() {
        return "145";
    }

    @Override
    public void run() {
        System.out.println("LC145 Binary Tree Postorder Traversal: ");
        System.out.println(postorderTraversal(TreeUtils.buildTree(new Integer[]{1,null,2,3})));
        System.out.println(postorderTraversal(TreeUtils.buildTree(new Integer[]{1,2,3,4,5,null,8,null,null,6,7,9})));
        System.out.println(postorderTraversal(TreeUtils.buildTree(new Integer[]{})));
        System.out.println(postorderTraversal(TreeUtils.buildTree(new Integer[]{1})));
    }

}
