package com.renda.leetcode.util;

import java.util.ArrayDeque;
import java.util.Queue;

public class TreeUtils {

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

}
