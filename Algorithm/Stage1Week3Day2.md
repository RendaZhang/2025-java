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

## LC 1039 Minimum Score Triangulation of Polygon

Thinking mins, Coding mins, Debugging mins.

```java

```

---

## LC 1312 Minimum Insertion Steps to Make a String Palindrome

Thinking mins, Coding mins, Debugging mins.

```java

```

---