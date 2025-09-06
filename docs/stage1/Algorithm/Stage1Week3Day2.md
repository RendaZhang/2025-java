<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Stage1 - Week3 - Day 2: Dynamic Programming Advanced](#stage1---week3---day-2-dynamic-programming-advanced)
  - [LC 300 - Longest Increasing Subsequence](#lc-300---longest-increasing-subsequence)
    - [O(n²) DP](#on%C2%B2-dp)
    - [O(n log n) Binary Search Optimization](#on-log-n-binary-search-optimization)
  - [LC 53 - Maximum Subarray (Kadane’s Algorithm)](#lc-53---maximum-subarray-kadanes-algorithm)
    - [1️⃣ State Machine Diagram](#-state-machine-diagram)
    - [2️⃣ Code Implementation (Core ≤ 5 Lines)](#-code-implementation-core-%E2%89%A4-5-lines)
    - [3️⃣ DP Evolution Table (Example Input: `[-2,1,-3,4,-1,2,1,-5,4]`)](#-dp-evolution-table-example-input--21-34-121-54)
    - [4️⃣ 2D Extension (Maximum Submatrix Sum)](#-2d-extension-maximum-submatrix-sum)
    - [🧠 Interview Bonus Explanation](#-interview-bonus-explanation)
  - [🧠 LC 1039 - Minimum Score Triangulation of Polygon](#-lc-1039---minimum-score-triangulation-of-polygon)
    - [Example Validation: `[1,3,1,4,1,5]`](#example-validation-131415)
  - [🧠 LC 1312 - Minimum Insertion Steps to Make a String Palindrome](#-lc-1312---minimum-insertion-steps-to-make-a-string-palindrome)
    - [Example Validation: `"abcba"`](#example-validation-abcba)
  - [🧮 Additional: DP Table Structure Explanation](#-additional-dp-table-structure-explanation)
  - [LC 698 Partition to K Equal Sum Subsets](#lc-698-partition-to-k-equal-sum-subsets)
    - [**Algorithm Type**:](#algorithm-type)
    - [**Time Complexity**:](#time-complexity)
    - [**Space Complexity**:](#space-complexity)
    - [**Code Review Tips**:](#code-review-tips)
    - [Code:](#code)
  - [LC 464 Can I Win](#lc-464-can-i-win)
  - [LC 464 - Can I Win](#lc-464---can-i-win)
    - [**Algorithm Type**:](#algorithm-type-1)
    - [**Time Complexity**:](#time-complexity-1)
    - [**Space Complexity**:](#space-complexity-1)
    - [✅ Key Points Explained](#-key-points-explained)
    - [Code](#code)
    - [🧠 Additional Suggestions:](#-additional-suggestions)
  - [LC 354 Russian Doll Envelopes](#lc-354-russian-doll-envelopes)
    - [**Algorithm Type**:](#algorithm-type-2)
    - [**Time Complexity**:](#time-complexity-2)
    - [**Space Complexity**:](#space-complexity-2)
  - [🧠 Key Insights:](#-key-insights)
    - [🚩 1. Cannot directly apply LIS to 2D arrays](#-1-cannot-directly-apply-lis-to-2d-arrays)
    - [🚩 2. Special Sorting Technique (Core Trick ⚠️):](#-2-special-sorting-technique-core-trick-)
    - [🚩 3. Binary Search-Optimized LIS (Explanation of `tails`)](#-3-binary-search-optimized-lis-explanation-of-tails)
    - [✨ Example](#-example)
    - [Code](#code-1)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Stage1 - Week3 - Day 2: Dynamic Programming Advanced

______________________________________________________________________

## LC 300 - Longest Increasing Subsequence

### O(n²) DP

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

______________________________________________________________________

## LC 53 - Maximum Subarray (Kadane’s Algorithm)

Thinking 5min58s, Coding 8min0s, Debugging 0min58s.

### 1️⃣ State Machine Diagram

- **State Definitions**:

  - `cur`: Maximum subarray sum ending at `nums[i]`
  - `best`: Global maximum subarray sum so far

- **Transition Formulas**:

```text
cur  →  max(nums[i], cur + nums[i])
best →  max(best, cur)
```

- **State Transition Diagram (ASCII)**:

```
        +------------+
        |   cur      |
        | max(x, x+cur) ←——
        +------------+     |
               ↓           |
        +------------+     |
        |  best      | ←———
        | max(best,cur)
        +------------+
```

### 2️⃣ Code Implementation (Core ≤ 5 Lines)

```java
// Time: O(n), Space: O(1)
class Solution {
    public int maxSubArray(int[] nums) {
        int cur = nums[0], best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            cur = Math.max(nums[i], cur + nums[i]);
            best = Math.max(best, cur);
        }
        return best;
    }
}
```

### 3️⃣ DP Evolution Table (Example Input: `[-2,1,-3,4,-1,2,1,-5,4]`)

| i | x | cur | best |
| - | -- | ----------------- | -------------- |
| 0 | -2 | -2 | -2 |
| 1 | 1 | max(1, -2+1) = 1 | max(-2, 1) = 1 |
| 2 | -3 | max(-3, 1-3) = -2 | max(1, -2) = 1 |
| 3 | 4 | max(4, -2+4) = 4 | max(1, 4) = 4 |
| 4 | -1 | max(-1, 4-1) = 3 | max(4, 3) = 4 |
| 5 | 2 | max(2, 3+2) = 5 | max(4, 5) = 5 |
| 6 | 1 | max(1, 5+1) = 6 | max(5, 6) = 6 |
| 7 | -5 | max(-5, 6-5) = 1 | max(6, 1) = 6 |
| 8 | 4 | max(4, 1+4) = 5 | max(6, 5) = 6 |

✔️ Final Answer: 6

### 4️⃣ 2D Extension (Maximum Submatrix Sum)

> Fix the upper and lower bounds, compress each column into column sums → Apply Kadane’s Algorithm on the compressed 1D array to find the maximum subarray sum.

📌 **One-Liner Summary**:

> Enumerate upper/lower bounds, compress into column sums, then apply Kadane’s Algorithm on the sums.

Time Complexity: `O(n² * m)`, Space Complexity: O(m)

### 🧠 Interview Bonus Explanation

> You can interpret `cur` as the "holding state" and `best` as the "maximum profit after closing the position." `cur = max(current open position, holding + price change)`. Introducing this "financial trading perspective" can often impress the interviewer ✨

✅ If you want to extend the solution to output the start and end indices `(start, end)` of the subarray, you can add two index variables:

- When `cur = nums[i]`, update `start = i`;
- When updating `best`, also record `end = i`.

______________________________________________________________________

## 🧠 LC 1039 - Minimum Score Triangulation of Polygon

**Algorithm Type**: Interval DP
**Time Complexity**: O(n³)
**Space Complexity**: O(n²)

```java
class Solution {
    public int minScoreTriangulation(int[] values) {
        int n = values.length;
        int[][] dp = new int[n][n];

        for (int j = 2; j < n; j++) {
            for (int i = j - 2; i >= 0; i--) {
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i + 1; k < j; k++) {
                    dp[i][j] = Math.min(dp[i][j],
                        dp[i][k] + dp[k][j] + values[i]*values[j]*values[k]);
                }
            }
        }

        return dp[0][n-1];
    }
}
```

### Example Validation: `[1,3,1,4,1,5]`

Minimum Triangulation Score: `13` ✅

______________________________________________________________________

## 🧠 LC 1312 - Minimum Insertion Steps to Make a String Palindrome

**Algorithm Type**: Interval DP
**Time Complexity**: O(n²)
**Space Complexity**: O(n²)

```java
class Solution {
    public int minInsertions(String s) {
        char[] charArray = s.toCharArray();
        int len = charArray.length;
        int[][] dp = new int[len][len];
        for (int j = 1; j < len; j++) {
            for (int i = j - 1; i >= 0; i--) {
                if (charArray[i] != charArray[j]) {
                    dp[i][j] = Math.min(dp[i+1][j], dp[i][j-1]) + 1;
                } else {
                    dp[i][j] = dp[i+1][j-1];
                }
            }
        }
        return dp[0][len-1];
    }
}
```

### Example Validation: `"abcba"`

Output: `0` (Already a palindrome) ✅

______________________________________________________________________

## 🧮 Additional: DP Table Structure Explanation

Taking LC 1312 as an example, for the string `"abcba"`, the `dp[i][j]` table is constructed as follows:

| i \\ j | 0 | 1 | 2 | 3 | 4 |
| ----- | - | - | - | - | - |
| 0 | 0 | 1 | 2 | 1 | 0 |
| 1 | | 0 | 1 | 0 | 1 |
| 2 | | | 0 | 1 | 2 |
| 3 | | | | 0 | 1 |
| 4 | | | | | 0 |

Each cell represents the minimum number of insertions required to make `s[i..j]` a palindrome.

______________________________________________________________________

## LC 698 Partition to K Equal Sum Subsets

Thinking mins, Coding mins, Debugging mins.

### **Algorithm Type**:

Backtracking (DFS with Pruning) + Greedy Preprocessing (Sorting)

### **Time Complexity**:

Worst-case O(k × 2ⁿ)

n is the number of elements in nums

Each element can either be used or not → 2ⁿ states

Pruning (bucketSum > target & sorting) greatly reduces branches in practice

### **Space Complexity**:

O(n + k)

boolean[] used → O(n)

Call stack recursion depth up to n levels → O(n)

No extra DP cache used in this version

### **Code Review Tips**:

- When `bucketSum == target`, recursively move to the next bucket to avoid redundant searches from the beginning;
- `if (bucketSum == 0) break;` is a clever **same-level pruning** technique;
- `Arrays.sort(nums)` is a key optimization, ensuring larger numbers are placed first to prune invalid paths early;
- Although the time complexity is exponential, **it performs stably under the constraint of `n ≤ 16`**.

### Code:

```java
class Solution {
    public boolean canPartitionKSubsets(int[] nums, int k) {
        if (k == 1) return true;
        int sum = Arrays.stream(nums).sum();
        if (sum % k != 0) return false;
        int len = nums.length;
        int target = sum / k;
        // Sorting Pruning
        Arrays.sort(nums);
        if (nums[len - 1] > target) return false;
        return dfs(nums, target, k, 0, 0, new boolean[len]);
    }

    boolean dfs(int[] nums, int target, int k, int s_i, int bucketSum, boolean[] used) {
        if (k == 0) return true;
        if (target == bucketSum) {
            // Current bucket is full, move to the next bucket
            return dfs(nums, target, k - 1, 0, 0, used);
        }

        for (int i = s_i; i < nums.length; i++) {
            if (used[i]) continue;
            int currSum = bucketSum + nums[i];
            // Pruning: If adding nums[i] exceeds the target, skip
            if (currSum > target) continue;
            used[i] = true;
            if(dfs(nums, target, k, i + 1, currSum, used)) return true;
            used[i] = false;
            // Pruning: Break on the first failure at the same level
            if (bucketSum == 0) break;
        }

        return false;
    }
}
```

______________________________________________________________________

## LC 464 Can I Win

Fantastic! Below is the complete algorithm record for **LC 464 - Can I Win**, including thinking and implementation details, time and space complexity analysis, and more:

______________________________________________________________________

## LC 464 - Can I Win

### **Algorithm Type**:

🎲 **Minimax + Memoized DFS + State Compression** (Bitmask Approach)

### **Time Complexity**:

**O(2ⁿ × n)**

- There are at most `2ⁿ` states (each number is either chosen or not).
- For each state, enumerate `n` possible next numbers.

> The actual complexity is much smaller than `2ⁿ` because many states are pruned early (e.g., winning immediately upon selection).

### **Space Complexity**:

**O(2ⁿ + n)**

- Memoization table `memo[2^n]` space.
- Recursion call stack depth up to `n` (i.e., a total of `n` numbers can be chosen).
- Additional `boolean[] used` and `int[] pow2`, both O(n).

### ✅ Key Points Explained

| Element | Description |
| ---------------- | --------------------------------------------------------------------------- |
| **Pruning 1** | If `desiredTotal <= 0`, win immediately (initial condition). |
| **Pruning 2** | If `1+2+…+maxChoosable < desiredTotal`, even selecting all numbers will lose.|
| **State Representation** | Use `int mask` to represent which numbers are currently chosen (up to 20 bits). |
| **Memoization Table** | `Boolean[] memo = new Boolean[2^(max+1)]` records whether the current state is a winning position for the first player. |
| **Search Logic** | The current player enumerates a number `i`. If `i >= remain`, win immediately; otherwise, pass the problem to the opponent recursively. |
| **Winning Strategy** | If there exists a number that forces the opponent into a losing state ⇒ the current player is in a winning position. |

### Code

```java
class Solution {

  private int max;          // 最大可选数
  private int[] pow2;       // pow2[i] = 2^(i-1)，不用位移
  private Boolean[] memo;   // 记忆化表：mask → 当前轮是否必胜

  public boolean canIWin(int maxChoosableInteger, int desiredTotal) {

    // 0. 目标 <= 0，先手直接赢
    if (desiredTotal <= 0) return true;

    // 1. 下界剪枝：最大总和仍达不到目标，必输
    int maxSum = (maxChoosableInteger + 1) * maxChoosableInteger / 2;
    if (maxSum < desiredTotal) return false;

    // 2. 预处理
    this.max = maxChoosableInteger;
    buildPow2();                              // 填充 pow2[]
    memo = new Boolean[2*(1 << max)];            // 2*2^max 个状态

    boolean[] used = new boolean[max + 1];    // 1‑based
    return dfs(desiredTotal, used, 0);
  }

  /** 递归判断“当前局面（remain, used, mask）”先手是否必胜 */
  private boolean dfs(int remain, boolean[] used, int mask) {
    // 3. 记忆化
    if (memo[mask] != null) return memo[mask];

    // 4. 枚举可选数字：从大到小能更快触发“直接赢”剪枝
    for (int i = max; i >= 1; i--) {
      if (used[i]) continue;

      // 4‑1. 选 i 就能凑够或超出目标 ⇒ 当前玩家立刻获胜
      if (i >= remain) {
        return memo[mask] = true;
      }

      // 4‑2. 尝试选 i，递归让对手去玩剩下的局面
      used[i] = true;
      boolean opponentWin = dfs(remain - i, used, mask + pow2[i]);
      used[i] = false;          // 回溯

      // 只要有一种选择让对手必败，当前必胜
      if (!opponentWin) return memo[mask] = true;
    }

    // 5. 遍历完仍没有必胜手段 ⇒ 当前必败
    return memo[mask] = false;
  }

  /** 构造 pow2[]：pow2[i] = 2^(i-1)，全部用乘法，无位移 */
  private void buildPow2() {
    pow2 = new int[max + 1];   // 下标 0..max
    pow2[0] = 1;               // 2^0
    for (int i = 1; i <= max; i++) {
      pow2[i] = pow2[i - 1] * 2;   // 连乘得到 2^(i-1)
    }
  }
}
```

### 🧠 Additional Suggestions:

Your code is very well-structured, and the use of `pow2[i]` instead of `(1 << (i-1))`, though slightly more verbose, effectively avoids bit-shifting pitfalls 👏.

For further space optimization, you can:

- Replace `pow2[i]` with `1 << (i-1)`;
- Use `memo = new Boolean[1 << max]` to avoid wasting space on unused states;
- Replace `used[]` with a dynamically generated mask instead of a separate array.

______________________________________________________________________

## LC 354 Russian Doll Envelopes

### **Algorithm Type**:

📦 **2D to 1D Transformation + Longest Increasing Subsequence (LIS) + Binary Search Optimization**

### **Time Complexity**:

**O(n × log n)**

- Sorting takes O(n × log n).
- LIS with binary search maintains the `tails` array, each operation takes O(log n), performed n times → O(n × log n).

### **Space Complexity**:

**O(n)**

- The `tails` array is used to record the smallest possible tail elements of all increasing subsequences.

## 🧠 Key Insights:

### 🚩 1. Cannot directly apply LIS to 2D arrays

Since both dimensions (width `w` and height `h`) need to satisfy the increasing condition, direct sorting and 2D comparison are not feasible.

### 🚩 2. Special Sorting Technique (Core Trick ⚠️):

```java
Arrays.sort(envelopes, (a, b) ->
    a[0] == b[0] ? Integer.compare(b[1], a[1]) : Integer.compare(a[0], b[0]));
```

| Sorting Logic | Purpose |
| ---------------------------------- | ----------------------------------------- |
| Sort by `width` in ascending order | Ensures that later envelopes are "at least not smaller". |
| If `width` is equal, sort by `height` in descending order | Prevents misjudgment of nesting for `[w, h1], [w, h2]` (which cannot be nested). |

This reduces the problem to 1D, allowing us to apply LIS only on the **`height`** dimension.

### 🚩 3. Binary Search-Optimized LIS (Explanation of `tails`)

- `tails[i]` represents the smallest possible tail element of all increasing subsequences of length `i+1`.
- Each `height` is inserted into the `tails` array using binary search.
- If an old value can be replaced, replace it (to maintain the optimal tail); otherwise, append it.
- The length of `tails` at the end is the maximum number of nested envelopes.

### ✨ Example

```java
Input: envelopes = [[5,4],[6,4],[6,7],[2,3]]
Output: 3
Explanation: The sequence [2,3] => [5,4] => [6,7]
```

### Code

```java
class Solution {
    // Constraints: 1 <= envelopes.length <= 105, envelopes[i].length == 2, 1 <= wi, hi <= 105
    public int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, (a, b) -> a[0] == b[0] ? Integer.compare(b[1], a[1]) : Integer.compare(a[0], b[0]));

        int[] tails = new int[envelopes.length];
        int size = 0;
        for (int[] env : envelopes) {
            int h = env[1];
            int l = 0, r = size;
            while (l < r) {
                int m = (l + r) >>> 1;
                if (tails[m] < h) l = m + 1;
                else r = m;
            }
            tails[l] = h;
            if (l == size) size++;
        }
        return size;
    }
}
```

______________________________________________________________________
