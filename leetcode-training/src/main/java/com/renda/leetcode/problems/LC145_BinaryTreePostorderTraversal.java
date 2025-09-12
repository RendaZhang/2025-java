package com.renda.leetcode.problems;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.util.TreeNode;
import com.renda.leetcode.util.TreeUtils;

/**
 * LeetCode 145 - Binary Tree Postorder Traversal
 *
 * 单栈 + prev 指针
 *
 * Runtime 1 ms Beats 6.81%
 * Memory 41.65 MB Beats 67.31%
 *
 */
public class LC145_BinaryTreePostorderTraversal implements LeetCodeProblem {

    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root, prev = null;
        while (cur != null || !stack.isEmpty()) {
            // 一路向左
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            TreeNode node = stack.peek();
            // 右子树不存在，或刚刚处理过右子树 => 可以输出当前节点
            if (node.right == null || node.right == prev) {
                res.add(node.val);
                stack.pop();
                prev = node;          // 记录“上次输出的是谁”
            } else {
                cur = node.right;     // 否则先去处理右子树
            }
        }
        return res;
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
