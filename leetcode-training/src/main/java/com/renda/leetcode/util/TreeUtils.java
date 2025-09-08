package com.renda.leetcode.util;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 二叉树辅助工具类，提供构建树等常用方法。
 */
public class TreeUtils {

    /**
     * 根据层序遍历数组构建二叉树。
     */
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
