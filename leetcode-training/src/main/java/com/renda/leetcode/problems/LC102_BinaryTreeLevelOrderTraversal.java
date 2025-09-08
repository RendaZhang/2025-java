package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;
import com.renda.leetcode.util.TreeNode;
import com.renda.leetcode.util.TreeUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * LeetCode 102 - Binary Tree Level Order Traversal.
 */
public class LC102_BinaryTreeLevelOrderTraversal implements LeetCodeProblem {

    public List<List<Integer>> levelOrder(TreeNode root) {
        if (root == null) return List.of();
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        List<List<Integer>> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node == null) continue;
                level.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            if (!level.isEmpty()) result.add(level);
        }
        return result;
    }

    @Override
    public String problemNumber() {
        return "102";
    }

    @Override
    public void run() {
        TreeNode root = TreeUtils.buildTree(new Integer[]{3,9,20,null,null,15,7});
        System.out.println("LC102 Binary Tree Level Order Traversal: " + levelOrder(root));
    }
}
