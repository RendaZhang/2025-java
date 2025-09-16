package com.renda.leetcode.core;

import com.renda.leetcode.problems.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 题目注册中心，统一管理所有可运行的 LeetCode 练习题。
 * <p>
 * 扩展方式：实现 {@link LeetCodeProblem} 并在静态代码块中注册即可。
 */
public final class ProblemRegistry {

    private static final Map<String, LeetCodeProblem> PROBLEMS = new HashMap<>();

    static {
        // 在此处注册题目
        register(new LC1_TwoSum());
        register(new LC3_LongestSubstringWithoutRepeatingCharacters());
        register(new LC20_ValidParentheses());
        register(new LC56_MergeInterval());
        register(new LC79_WordSearch());
        register(new LC102_BinaryTreeLevelOrderTraversal());
        register(new LC104_MaximumDepthOfBinaryTree());
        register(new LC120_Triangle());
        register(new LC122_BestTimeToBuyAndSellStockII());
        register(new LC200_NumberOfIslands());
        register(new LC347_TopKFrequentElements());
        register(new LC904_FruitIntoBaskets());
        register(new LC167_TwoSumII_InputArrayIsSorted());
        register(new LC209_MinimumSizeSubarraySum());
        register(new LC739_DailyTemperatures());
        register(new LC141_LinkedListCycle());
        register(new LC94_BinaryTreeInorderTraversal());
        register(new LC145_BinaryTreePostorderTraversal());
        register(new LC215_KthLargestElementInAnArray());
        register(new LC23_MergeKSortedLists());
        register(new LC703_KthLargestElementInAStream());
        register(new LC378_KthSmallestElementInASortedMatrix());
        register(new LC373_FindKPairswithSmallestSums());
        register(new LC295_FindMedianFromDataStream());
    }

    private ProblemRegistry() {
    }

    private static void register(LeetCodeProblem problem) {
        PROBLEMS.put(problem.problemNumber(), problem);
    }

    /**
     * 根据题号获取对应的算法实例。
     *
     * @param number LeetCode 题号
     * @return 算法实例，如果不存在则返回 {@code null}
     */
    public static LeetCodeProblem getProblem(String number) {
        return PROBLEMS.get(number);
    }

    /**
     * @return 已注册的所有题号。
     */
    public static java.util.Set<String> availableProblems() {
        return PROBLEMS.keySet();
    }
}
