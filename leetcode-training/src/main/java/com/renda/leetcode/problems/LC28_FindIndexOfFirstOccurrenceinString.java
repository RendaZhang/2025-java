package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

public class LC28_FindIndexOfFirstOccurrenceinString implements LeetCodeProblem {

    public int strStr(String haystack, String needle) {
        char[] str = haystack.toCharArray();
        char[] sub = needle.toCharArray();
        if (sub.length > str.length) return -1;
        if (sub.length == 0) return 0;
        int k = str.length - sub.length;
        for (int i = 0; i <= k; i++) {
            int j = 0;
            while (j < sub.length && str[i+j] == sub[j]) j++;
            if (j == sub.length) return i;
        }
        return -1;
    }

    @Override
    public String problemNumber() {
        return "28";
    }

    @Override
    public void run() {
        System.out.println(strStr("sadbutsad", "sad"));
        System.out.println(strStr("leetcode", "leeto"));
    }

}
