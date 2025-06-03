package com.renda.leetcode;

import java.util.Arrays;

import static com.renda.leetcode.util.TreeUtils.buildTree;

public class Main {

    public static void main(String[] args) {
//         LC1_TwoSum lc1 = new LC1_TwoSum();
//         System.out.println("LC1 Two Sum: " + Arrays.toString(lc1.twoSum(new int[]{2, 7, 11, 15}, 9)));
//
//         LC20_ValidParentheses lc20 = new LC20_ValidParentheses();
//         System.out.println("LC20 Valid Parentheses: " + lc20.isValid("()[]{}"));
//
//        LC56_MergeInterval lc56 = new LC56_MergeInterval();
//        System.out.println("LC56 Merge Interval: " + Arrays.deepToString(lc56.merge(new int[][]{{2, 3}, {4, 5}, {6, 7}, {8, 9}, {1, 10}})));
//
//        LC104_MaximumDepthOfBinaryTree lc104 = new LC104_MaximumDepthOfBinaryTree();
//        System.out.println("LC104 Maximum Depth of Binary Tree: " + lc104.maxDepth(buildTree(new Integer[]{3, 9, 20, null, null, 15, 7})));
//
//        LC3_LongestSubstringWithoutRepeatingCharacters lc3 = new LC3_LongestSubstringWithoutRepeatingCharacters();
//        System.out.println("LC3 Longest Substring Without Repeating Characters: " + lc3.lengthOfLongestSubstring("abc"));
//
//        LC102_BinaryTreeLevelOrderTraversal lc102 = new LC102_BinaryTreeLevelOrderTraversal();
//        System.out.println("LC102 Binary Tree Level Order Traversal: " + lc102.levelOrder(buildTree(new Integer[]{3, 9, 20, null, null, 15, 7})));
//
//        LC122_BestTimeToBuyAndSellStockII lc122 = new LC122_BestTimeToBuyAndSellStockII();
//        System.out.println("LC122 Best Time to Buy and Sell Stock II: " + lc122.maxProfit(new int[]{7, 1, 5, 3, 6, 4}));

        LC200_NumberOfIslands lc200 = new LC200_NumberOfIslands();
        System.out.println("LC200 Number of Islands: " + lc200.numIslands(new char[][]{{'1', '1', '0', '0', '0'}, {'1', '1', '0', '0', '0'}, {'0', '0', '1', '0', '0'}, {'0', '0', '0', '1', '1'}}));
    }

}
