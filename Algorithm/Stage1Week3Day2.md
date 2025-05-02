# Stage1 - Week3 - Day 2: Dynamic Programming Advanced

---

## LC 300 - Longest Increasing Subsequence

###  O(n²) DP

Thinking 4min55s, Coding 7min13s, Debugging 5min34s.

```java
// O(n²) -> 43ms
class Solution {
    public int lengthOfLIS(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        int ans = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            ans = Math.max(ans, dp[i]);
        }
        return ans;
    }
}
```

### O(n log n) Binary Search Optimization

Thinking 7min15s, Coding 10min44s, Debugging 10min58s.

```java
// O(n log n) -> 2ms
class Solution {
    public int lengthOfLIS(int[] nums) {
        int n = nums.length;
        int[] tails = new int[n];
        tails[0] = nums[0];
        int num, size = 1;
        for (int i = 1; i < n; i++) {
            num = nums[i];
            int left = 0, right = size, mid;
            while (left != right) {
                mid = (left + right) / 2;
                if (num <= tails[mid]) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            if (left >= size) size++;
            tails[left] = num;
        }
        return size;
    }
}
```

---