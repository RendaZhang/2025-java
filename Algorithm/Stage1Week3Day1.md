# Stage1 - Week3 - Day 1: Dynamic Programming

______________________________________________________________________

## Fibonacci - LC 509

### Time:

Thinking 2min26s, Coding 2min3s, Debugging 10min49s.

### Code:

```java
// Time: O(N), Space: O(1)
class Solution {
    public int fib(int n) {
        if (n == 0) {return 0;}
        if (n == 1) {return 1;}
        int prev = 0;
        int curr = 1;
        for (int i = 2; i <= n; i++) {
            int temp = curr;
            curr = prev + curr;
            prev = temp;
        }
        return curr;
    }
}
```

### Result:

Accepted

31 / 31 testcases passed

Runtime: 0ms, Beats 100%; Memory: 40.22MB, Beats 80.55%

______________________________________________________________________

## Climbing Stairs - LC 70

### Time:

Thinking 9min0s, Coding 2min30s, Debugging 0min10s.

### Code:

```java
// Time: O(N), Space: O(1)
class Solution {
    public int climbStairs(int n) {
        if (n == 1) return 1;
        int prev = 0;
        int curr = 1;
        for (int i = 1; i <= n; i++) {
            int temp = curr;
            curr = prev + curr;
            prev = temp;
        }
        return curr;
    }
}
```

### Result:

Accepted

45 / 45 testcases passed

Runtime: 0ms, Beats 100%; Memory: 40.38MB, Beats 65.51%

______________________________________________________________________

## Memoized Recursion

### Fibonacci - LC 509

```java
// Time: O(N), Space: O(N)
class Solution {
    HashMap<Integer, Integer> memo = new HashMap<>();
    public int fib(int n) {
        if (n < 2) return n;
        if (memo.containsKey(n)) return memo.get(n);
        int result = fib(n - 1) + fib(n - 2);
        memo.put(n, result);
        return result;
    }
}
```

Accepted

31 / 31 testcases passed

Runtime: 0 ms Beats 100.00%; Memory: 40.77 MB Beats 18.26%

### Climbing Stairs - LC 70

```java
// Time: O(N), Space: O(N)
class Solution {
    HashMap<Integer, Integer> memo = new HashMap<>();
    public int climbStairs(int n) {
        if (n < 3) return n;
        if (memo.containsKey(n)) return memo.get(n);
        int result = climbStairs(n - 2) + climbStairs(n - 1);
        memo.put(n, result);
        return result;
    }
}
```

______________________________________________________________________

## Compare & Reflect

Q: Are these two problems essentially the same? If yes, why does LeetCode separate them?

A:
Yes, the transition equations of the two problems are essentially identical: f(n) = f(n-1) + f(n-2);
The reason LeetCode includes both is to guide you from "specific scenarios" to "abstract patterns";
LC 70 leans more toward "path counting problems," helping you understand DP applications;
LC 509 leans more toward "mathematical induction problems," suitable for introducing optimization techniques
(e.g., matrix exponentiation, Binet’s formula).

______________________________________________________________________

## LC 416 “Partition Equal Subset Sum”

### DFS + Memoization (Top-down)

```java
// Time: O(n*target), Space: O(n*target)
class Solution {
    public boolean canPartition(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum % 2 != 0) return false;
        int target = sum / 2;

        // -1 means uncomputed, 0 means false, 1 means true
        int[][] memo = new int[nums.length][target + 1];
        return dfs(nums, 0, 0, target, memo);
    }

    private boolean dfs(int[] nums, int index, int currSum, int target, int[][] memo) {
        if (currSum == target) return true;
        if (currSum > target || index >= nums.length) return false;
        if (memo[index][currSum] != 0) return memo[index][currSum] == 1;

        boolean found = dfs(nums, index + 1, currSum + nums[index], target, memo)
                || dfs(nums, index + 1, currSum, target, memo);

        memo[index][currSum] = found ? 1 : -1;
        return found;
    }
}
```

### 2D Dynamic Programming (Bottom-up)

```java
// Time: O(n*target), Space: O(n*target)
class Solution {
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int num : nums) sum += num;
        if (sum % 2 != 0) return false;

        int target = sum / 2;
        int n = nums.length;
        boolean[][] dp = new boolean[n + 1][target + 1];
        dp[0][0] = true; // Initial state

        for (int i = 1; i <= n; i++) {
            int num = nums[i - 1];
            for (int j = 0; j <= target; j++) {
                dp[i][j] = dp[i - 1][j]; // Do not select the current number
                if (j >= num) {
                    dp[i][j] |= dp[i - 1][j - num]; // Select the current number
                }
            }
        }

        return dp[n][target];
    }
}
```

### 1D Dynamic Programming (Space-Optimized)

```java
// Time: O(n*target), Space: O(target)
class Solution {
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int num : nums) sum += num;
        if (sum % 2 != 0) return false;

        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true; // 0 can be summed

        for (int num : nums) {
            for (int j = target; j >= num; j--) {
                dp[j] = dp[j] || dp[j - num];
            }
        }

        return dp[target];
    }
}
```

### Complexity Comparison Summary

| Method Type | Time Complexity | Space Complexity | Characteristics |
|---------------------|----------------------|-------------------------|-----------------------------------------|
| DFS + Memoization | `O(n × target)` | `O(n × target)` | Easy to understand, risk of TLE, stack overflow risk |
| 2D DP | `O(n × target)` | `O(n × target)` | Clear and intuitive, high space usage |
| 1D DP (Compressed) | `O(n × target)` | `O(target)` | Most recommended, memory efficient |

✅ Recommendations

- **For beginners or teaching purposes**: Use **2D DP** to understand state transitions.
- **For interviews or practical submissions**: Directly use the **1D DP compressed version**.
- **For small data debugging or brute-force validation**: Start with **Memoized DFS**.

______________________________________________________________________

## LC 62 “Unique Paths”

For the mxn grid, the robot need to Move to the Right n-1 times and to the bottom m-1 times.
The output should be the combination of these Right and bottom moves.

### 2D Dynamic Programming (Bottom-up)

#### Time

Thinking 40min3s, Coding 11min47s, Debugging 0min0s.

#### State Definition

`f[i][j]` = The number of unique paths that the robot can move to position (i, j)

#### Transition

`f[i][j] = f[i-1][j] + f[i][j-1]`

#### Initialization & Order

`f[0][0] = 1, f[0...m][n] = 1, f[0][0...n] = 1`

#### Complexity

Time O(m*n), Space O(m*n)

#### Code

```java
class Solution {
    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) dp[i][0] = 1;
        for (int j = 0; j < n; j++) dp[0][j] = 1;

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i-1][j] + dp[i][j-1];
            }
        }

        return dp[m-1][n-1];
    }
}
```

### 1D Dynamic Programming (Space-Optimized)

#### Time

Thinking 11min22s, Coding 15min50s, Debugging 7min9s.

#### State Definition

`f[i]` = the Number of the path that the robot can move to the column position j of the current row or previous row.

#### Transition

`f[i] = f[i - 1] + prev(f[i])`

#### Initialization & Order

`f[0...n] = 1`

`f[0] = 1` for every beginning of new row

#### Complexity

Time O(m\*n), Space O(n)

#### Code

```java
import java.util.Arrays;

class Solution {
    public int uniquePaths(int m, int n) {
        int[] dp = new int[n];

        Arrays.fill(dp, 1);

        for (int i = 1; i < m; i++) {
            dp[0] = 1;
            for (int j = 1; j < n; j++) {
                dp[j] = dp[j] + dp[j-1];
            }
        }

        return dp[n-1];
    }
}
```

______________________________________________________________________

## LC 1143 “Longest Common Subsequence”

A common substring of two String is a subsequence that is common to both string.
Input text consist of only lowercase English letter.

### 2D Dynamic Programming (Bottom-up)

#### State Definition

`f[i][j]` = the length of the longest common subsequence for the two substrings - `text1[0...i]` and `text2[0...j]`.

#### Transition

```text
if (text1[i] == text2[j]), f[i][j] = f[i-1][j-1] + 1;
otherwise, f[i][j] = max(f[i-1][j], f[i][j-1]).
```

#### Initialization & Order

Init `f[0][0...n]` and `f[0...m][0]`

#### Complexity

Time O(m*n), Space O(m*n)

#### Code

```java
class Solution {
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            char c1 = text1.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                char c2 = text2.charAt(j - 1);
                if (c1 == c2) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }
}

```

### Two-Row Rolling Array DP (Space-Optimized)

#### State Definition

`f[i]` → Optimized into a two-row rolling array format

`f[2][j]` = The length of the longest common subsequence between the `i-th` row of text1 and the first j characters of text2

Use `f[curr][j]` to represent the current row being processed (i.e., the `i-th` character of text1);

Use `f[prev][j]` to represent the previous row (i.e., the `(i-1)-th` character of text1.

#### Transition

When two characters match → Take the value from the diagonal previous state + 1;

When they don’t match → Take the maximum value from the left or top;

`prev = (i - 1) % 2`, `curr = i % 2` to implement the rolling alternation.

#### Initialization & Order

```pseudo
f[0][0...n] = 0  // Initialize the first row (i=0) to 0
```

Traversal order:

```pseudo
for (i = 1 ... m)
  for (j = 1 ... n)
```

#### Complexity

Time O(m\*n), Space O(n)

#### Code

```java
class Solution {
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[2][n + 1];

        for (int i = 1; i <= m; i++) {
            int curr = i % 2, prev = (i - 1) % 2;
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[curr][j] = dp[prev][j - 1] + 1;
                } else {
                    dp[curr][j] = Math.max(dp[prev][j], dp[curr][j - 1]);
                }
            }
        }

        return dp[m % 2][n];
    }
}
```

______________________________________________________________________
