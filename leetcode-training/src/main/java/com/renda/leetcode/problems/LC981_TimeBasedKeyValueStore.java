package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 981 - Time Base Key Value Store
 *
 * <p>ArrayList + 二分：
 * 利用题目保证 set 的时间戳严格递增（全局递增 ⇒ 某个 key 的时间戳自然也递增），
 * 对每个 key 维护一个按时间戳递增的数组，get 时做“找最后一个 ≤ timestamp”的二分。
 */
public class LC981_TimeBasedKeyValueStore implements LeetCodeProblem {

    static class TimeMap {

        static class Pair {
            int ts;
            String val;
            Pair(int timestamp, String value) {
                this.ts = timestamp;
                this.val = value;
            }
        }

        final Map<String, ArrayList<Pair>> map;

        public TimeMap() {
            map = new HashMap<>();
        }

        public void set(String key, String value, int timestamp) {
            map.computeIfAbsent(key, a -> new ArrayList<>()).add(new Pair(timestamp, value));
        }

        public String get(String key, int timestamp) {
            ArrayList<Pair> list = map.get(key);
            if (list == null || list.isEmpty()) return "";
            int lo = 0, hi = list.size() - 1, idx = -1;
            while (lo <= hi) {
                int mid = (lo + hi) >>> 1;
                Pair p = list.get(mid);
                if (p.ts <= timestamp) {
                    idx = mid;
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            return idx == -1 ? "" : list.get(idx).val;
        }
    }

    @Override
    public String problemNumber() {
        return "981";
    }

    @Override
    public void run() {
        System.out.println("LC981 Time Base Key Value Store: ");
        TimeMap obj = new TimeMap();
        obj.set("1", "a", 1);
        obj.set("2", "b", 2);
        obj.set("3", "c", 3);
        obj.set("4", "d", 4);
        obj.set("5", "e", 5);
        obj.set("6", "f", 6);
        System.out.println(obj.get("1", 3));
        TimeMap timeMap = new TimeMap();
        timeMap.set("foo", "bar", 1);  // store the key "foo" and value "bar" along with timestamp = 1.
        System.out.println(timeMap.get("foo", 1));         // return "bar"
        System.out.println(timeMap.get("foo", 3));         // return "bar", since there is no value corresponding to foo at timestamp 3 and timestamp 2, then the only value is at timestamp 1 is "bar".
        timeMap.set("foo", "bar2", 4); // store the key "foo" and value "bar2" along with timestamp = 4.
        System.out.println(timeMap.get("foo", 4));         // return "bar2"
        System.out.println(timeMap.get("foo", 5));         // return "bar2"
    }

}
