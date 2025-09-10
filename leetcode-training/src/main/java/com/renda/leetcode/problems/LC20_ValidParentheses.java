package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

/**
 * LeetCode 20 - Valid Parentheses.
 *
 * 遇到左括号入栈；遇到右括号，检查栈顶能否匹配；结束时栈必须空。
 *
 * Runtime 1 ms Beats 99.54%
 * Memory 41.68 MB Beats 86.27%
 */
public class LC20_ValidParentheses implements LeetCodeProblem {

    public boolean isValid(String s) {
        char[] inputArray = s.toCharArray();
        int len = inputArray.length;

        char[] stack = new char[len];
        int top = -1;
        for (int i = 0; i < len; i++) {
            if (top > -1 && isMatched(stack[top], inputArray[i])) {
                top--;
            } else {
                top++;
                stack[top] = inputArray[i];
            }
        }

        return top == -1;
    }

    private boolean isMatched(char a, char b) {
        if (a == '(' && b == ')') return true;
        if (a == '[' && b == ']') return true;
        return a == '{' && b == '}';
    }

    @Override
    public String problemNumber() {
        return "20";
    }

    @Override
    public void run() {
        System.out.println("LC20 Valid Parentheses: " + isValid("()[]{}"));
    }
}
