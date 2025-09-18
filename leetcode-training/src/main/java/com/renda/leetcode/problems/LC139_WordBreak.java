package com.renda.leetcode.problems;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 139 - Word Break
 *
 * Runtime 1 ms Beats 99.47%
 * Memory 41.59 MB Beats 97.36%
 *
 * 总用时：1 hour 5 min
 * 阅读 4 min 30 s
 * 思考 47 min 26 s
 * 编码 12 min 43 s
 * Debug 0
 */
public class LC139_WordBreak implements LeetCodeProblem {

    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> dict = new HashSet<>(wordDict);
        int minLen = Integer.MAX_VALUE, maxLen = 0;
        for (String e : dict) {
            int len = e.length();
            if (len > maxLen) maxLen = len;
            if (len < minLen) minLen = len;
        }
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        for (int i = 1; i <= n; i++) {
            int start = Math.max(0, i - maxLen);
            int end = i - minLen;
            for (int j = end; j >= start; j--) {
                if (dp[j] && dict.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n];
    }

    @Override
    public String problemNumber() {
        return "139";
    }

    @Override
    public void run() {
        System.out.println("LC139 Word Break: ");
        System.out.println(wordBreak("leetcode", List.of("leet", "code")));
        System.out.println(wordBreak("applepenapple", List.of("apple", "pen")));
        System.out.println(wordBreak("catsandog", List.of("cats", "dog", "sand", "and", "cat")));
    }

}
