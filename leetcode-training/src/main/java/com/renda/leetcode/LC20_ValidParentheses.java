package com.renda.leetcode;

public class LC20_ValidParentheses {

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

}
