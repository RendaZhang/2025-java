package com.renda.leetcode;

import java.util.*;

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 * int val;
 * TreeNode left;
 * TreeNode right;
 * TreeNode() {}
 * TreeNode(int val) { this.val = val; }
 * TreeNode(int val, TreeNode left, TreeNode right) {
 * this.val = val;
 * this.left = left;
 * this.right = right;
 * }
 * }
 */
public class LC104_MaximumDepthOfBinaryTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }

    }

    public static TreeNode buildTree(Integer[] integers) {
        if (integers.length == 0 || integers[0] == null) return null;
        TreeNode root = new TreeNode(integers[0]);
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        int i = 1;
        while (i < integers.length) {
            TreeNode node = queue.poll();
            if (node == null) continue;
            if (integers[i] != null) {
                node.left = new TreeNode(integers[i]);
                queue.offer(node.left);
            }
            i++;
            if (i >= integers.length) break;
            if (integers[i] != null) {
                node.right = new TreeNode(integers[i]);
                queue.offer(node.right);
            }
            i++;
        }
        return root;
    }

    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        int count = 0;
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            count++;
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node == null) break;
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        return count;
    }
}
