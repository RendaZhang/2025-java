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

* **State Transition Diagram (ASCII)**:

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

| i | x  | cur               | best           |
| - | -- | ----------------- | -------------- |
| 0 | -2 | -2                | -2             |
| 1 | 1  | max(1, -2+1) = 1  | max(-2, 1) = 1 |
| 2 | -3 | max(-3, 1-3) = -2 | max(1, -2) = 1 |
| 3 | 4  | max(4, -2+4) = 4  | max(1, 4) = 4  |
| 4 | -1 | max(-1, 4-1) = 3  | max(4, 3) = 4  |
| 5 | 2  | max(2, 3+2) = 5   | max(4, 5) = 5  |
| 6 | 1  | max(1, 5+1) = 6   | max(5, 6) = 6  |
| 7 | -5 | max(-5, 6-5) = 1  | max(6, 1) = 6  |
| 8 | 4  | max(4, 1+4) = 5   | max(6, 5) = 6  |

✔️ Final Answer: 6

### 4️⃣ 2D Extension (Maximum Submatrix Sum)

> Fix the upper and lower bounds, compress each column into column sums → Apply Kadane’s Algorithm on the compressed 1D array to find the maximum subarray sum.

📌 **One-Liner Summary**:

> Enumerate upper/lower bounds, compress into column sums, then apply Kadane’s Algorithm on the sums.

Time Complexity: `O(n² * m)`, Space Complexity: O(m)

### 🧠 Interview Bonus Explanation

> You can interpret `cur` as the "holding state" and `best` as the "maximum profit after closing the position." `cur = max(current open position, holding + price change)`. Introducing this "financial trading perspective" can often impress the interviewer ✨

✅ If you want to extend the solution to output the start and end indices `(start, end)` of the subarray, you can add two index variables:

* When `cur = nums[i]`, update `start = i`;
* When updating `best`, also record `end = i`.

---

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

---

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

---

## 🧮 Additional: DP Table Structure Explanation

Taking LC 1312 as an example, for the string `"abcba"`, the `dp[i][j]` table is constructed as follows:

| i \ j | 0 | 1 | 2 | 3 | 4 |
| ----- | - | - | - | - | - |
| 0     | 0 | 1 | 2 | 1 | 0 |
| 1     |   | 0 | 1 | 0 | 1 |
| 2     |   |   | 0 | 1 | 2 |
| 3     |   |   |   | 0 | 1 |
| 4     |   |   |   |   | 0 |

Each cell represents the minimum number of insertions required to make `s[i..j]` a palindrome.

---

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

* When `bucketSum == target`, recursively move to the next bucket to avoid redundant searches from the beginning;
* `if (bucketSum == 0) break;` is a clever **same-level pruning** technique;
* `Arrays.sort(nums)` is a key optimization, ensuring larger numbers are placed first to prune invalid paths early;
* Although the time complexity is exponential, **it performs stably under the constraint of `n ≤ 16`**.

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

---

## LC 354 Russian Doll Envelopes

Thinking mins, Coding mins, Debugging mins.

**Algorithm Type**:
**Time Complexity**:
**Space Complexity**:

```java

```

---

## LC 464 Can I Win

Thinking mins, Coding mins, Debugging mins.

**Algorithm Type**:
**Time Complexity**:
**Space Complexity**:

```java

```