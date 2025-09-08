package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 3 - Longest Substring Without Repeating Characters.
 */
public class LC3_LongestSubstringWithoutRepeatingCharacters implements LeetCodeProblem {

    public int lengthOfLongestSubstring(String s) {
        int n = s.length();
        int maxLength = 0;
        int left = 0;
        int[] charIndex = new int[128]; // ASCII character set

        for (int right = 0; right < n; right++) {
            char currentChar = s.charAt(right);
            if (charIndex[currentChar] > left) {
                left = charIndex[currentChar];
            }
            charIndex[currentChar] = right + 1; // Store the next index after the current character
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    @Override
    public String problemNumber() {
        return "3";
    }

    @Override
    public void run() {
        System.out.println("LC3 Longest Substring Without Repeating Characters: " + lengthOfLongestSubstring("abcabcbb"));
    }
}
