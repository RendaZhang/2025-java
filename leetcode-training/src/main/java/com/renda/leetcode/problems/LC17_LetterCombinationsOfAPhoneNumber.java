package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LC17_LetterCombinationsOfAPhoneNumber implements LeetCodeProblem {

    private static final List<List<Character>> PHONE_KEYPAD = List.of(
            List.of(),                    // 0
            List.of(),                    // 1
            List.of('a','b','c'),         // 2
            List.of('d','e','f'),         // 3
            List.of('g','h','i'),         // 4
            List.of('j','k','l'),         // 5
            List.of('m','n','o'),         // 6
            List.of('p','q','r','s'),     // 7
            List.of('t','u','v'),         // 8
            List.of('w','x','y','z')      // 9
    );

    public List<String> letterCombinations(String digits) {
        if (digits == null || digits.isEmpty()) return new ArrayList<>();
        char[] digitsCharArray = digits.toCharArray();
        List<String> result = new ArrayList<>();
        result.add("");
        for (char c : digitsCharArray) {
            int d = c - '0';
            List<Character> letters = PHONE_KEYPAD.get(d);
            if (letters.isEmpty()) continue;
            List<String> next = new ArrayList<>(result.size() * letters.size());
            for (String prefix : result) {
                for (Character letter : letters) {
                    next.add(prefix + letter);
                }
            }
            result = next;
        }
        return result;
    }

    @Override
    public String problemNumber() {
        return "17";
    }

    @Override
    public void run() {
        // Given a string containing digits from 2-9 inclusive,
        // return all possible letter combinations that the number could represent.
        // Return the answer in any order.
        System.out.println(letterCombinations("23"));
        System.out.println(letterCombinations("2"));
    }

}
