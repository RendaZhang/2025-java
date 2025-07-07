# Stage1 - Week3 - Day 3: Backtracking & Greedy Algorithms

______________________________________________________________________

## LC 77 - Combinations

### ✅ Code

```java
class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        dfs(result, new ArrayList<Integer>(), 1, n, k);
        return result;

    }

    void dfs(List<List<Integer>> result, List<Integer> curr, int start, int n, int k) {
        // When the length equals k, a combination is found
        if (curr.size() == k) {
            result.addLast(new ArrayList<>(curr));
            return;
        }

        for (int i = start; i <= n; i++) {
            // Pruning optimization: If the remaining elements are insufficient to fill the combination, exit early
            if (curr.size() + (n - i + 1) < k) break;
            curr.addLast(i);
            dfs(result, curr, i+1, n, k);
            curr.removeLast();
        }
    }
}
```

### ⏱ Time Complexity: `O(C(n, k) * k)`

- `C(n, k)` is the number of combinations, i.e., `n! / (k! * (n - k)!)`;
- Each combination requires traversing `k` times to construct the `ArrayList`;
- Therefore, the overall time complexity is `O(C(n, k) * k)`.

### 🗂 Space Complexity: `O(k)`

- The maximum depth of the recursion stack is `k`, which is the depth of the path;
- Additionally, we use `curr` to temporarily store the path;
- The overall space complexity is `O(k)`, excluding the storage for the returned results.

### 🔁 Number of Recursive Calls

The number of recursive calls is equal to the total number of combinations:

- For `n = 5`, `k = 3`:

  - The total number of combinations is: `5! / (3! * 2!) = 10`
  - The number of recursive calls is `10`.

In general, it’s `C(n, k)`.

### ✅ Example Walkthrough

Let’s take an example to illustrate:

```text
n = 4, k = 2

Recursive path:
[1] → [1, 2] → backtrack → [1, 3] → backtrack → [1, 4] → backtrack
[2] → [2, 3] → backtrack → [2, 4] → backtrack
[3] → [3, 4] → backtrack
[4] → end
```

______________________________________________________________________

## LC 46 - Permutations

### ✅ **Visited Array Version**

#### 🌟 Core Idea

- Use a `boolean[] used` array to mark which elements have been selected for the current path.
- Traverse each unused element and add it to the current path.
- Restore the state during backtracking.

#### ⏱ **Time Complexity**

- Each recursion traverses unused elements, and there are `n!` total permutations.
- Each recursion involves traversing `n` elements → Time complexity is **O(n × n!)**.

#### 🗂 **Space Complexity**

- Mainly from the recursion stack depth: `O(n)`.
- `boolean[] used` occupies `O(n)`.
- The combination path `curr` also occupies `O(n)`.
- **Total space complexity**: `O(n + n + n) = O(n)` (excluding result storage).

#### Code

```java
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        dfs(result, new ArrayList<>(), nums, new boolean[nums.length]);
        return result;
    }

    void dfs(List<List<Integer>> result, List<Integer> curr, int[] nums, boolean[] used) {
        if (curr.size() == nums.length) {
            result.addLast(new ArrayList<Integer>(curr));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            curr.addLast(nums[i]);
            used[i] = true;
            dfs(result, curr, nums, used);
            curr.removeLast();
            used[i] = false;
        }
    }

}
```

#### 🔄 **Number of Recursive Calls**

- The number of recursive calls for permutations is `n!`, as each level selects the next element.
- For example, if `n = 4`:
  - `4! = 24` recursive calls.

### ✅ **Swap Version**

#### 🌟 Core Idea

- Avoid using `boolean[] used` by swapping elements within the array.
- Maintain the permutation order during recursion:
  - Use the current `start` position as an anchor.
  - Swap `i` with `start`, and restore after recursion.

#### ⏱ **Time Complexity**

- Similarly, there are `n!` permutations, and each recursion involves swapping and backtracking → **O(n × n!)**.

#### 🗂 **Space Complexity**

- **Recursion stack**: `O(n)`.
- No `boolean[]`, **saving O(n) space**.
- **Total space complexity**: `O(n)`.

#### Code

```java
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        dfs(result, nums, 0);
        return result;
    }

    void dfs(List<List<Integer>> result, int[] nums, int start) {
        if (start >= nums.length) {
            result.addLast(toArrayList(nums));
            return;
        }
        for (int i = start; i < nums.length; i++) {
            swap(nums, i, start);
            dfs(result, nums, start+1);
            swap(nums, i, start);
        }
    }

    List<Integer> toArrayList(int[] nums) {
        List<Integer> tmp = new ArrayList<>(nums.length);
        for (int i = 0; i < nums.length; i++) {
            tmp.add(nums[i]);
        }
        return tmp;
    }

    void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }

}
```

#### 🔄 **Number of Recursive Calls**

- Same as the Visited Array Version, `n!` calls.
- For `n = 4`: `4! = 24` recursive calls.

### 🧐 **Comparison Summary**

| Approach | Time Complexity | Space Complexity | Recursive Calls | Memory Usage | Readability |
| ------------------- | --------------- | ---------------- | ---------------- | ------------ | ----------- |
| **Visited Array** | `O(n × n!)` | `O(n)` | `n!` | **Higher** | More intuitive |
| **Swap Version** | `O(n × n!)` | `O(n)` | `n!` | **Lower** | Harder to understand |

### 🏷 **Visited Array vs Swap Version**

| Comparison Point | **Visited Array** | **Swap Version** |
| ---------------- | -------------------------------- | ------------------------------ |
| Memory Usage | Higher, requires extra `boolean[]` | Lower, uses swapping to track state |
| Readability | Very intuitive, `used[]` shows selected state | Slightly harder, swapping represents "selected state" |
| Modifies Input | Does not modify the input array | Modifies the input array but restores it after recursion |
| Extensibility | Easier to extend to permutations with duplicates | Requires additional handling for duplicates |
| Pruning Efficiency | Can add `if` pruning | Manual handling for duplicate elements |

- If only calculating `n!` permutations, I recommend the **Swap Version** for better space efficiency.
- If extending to **Permutations II** (with duplicate elements), the **Visited Array** is better for adding duplicate-handling logic.

______________________________________________________________________

## LC 39 - Combination Sum

### 💻 DFS with Pruning Version

```java
class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates); // Pre-sorting
        List<List<Integer>> result = new ArrayList<>();
        dfs(result, new ArrayList<>(), candidates, 0, target, 0);
        return result;
    }

    void dfs(List<List<Integer>> result, List<Integer> curr, int[] candidates, int sum, int target, int start) {
        if (sum == target) {
            result.addLast(new ArrayList<Integer>(curr));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            // Pruning: If the current sum plus the candidate number already exceeds the target value, break out of the loop directly.
            if (sum + candidates[i] > target) break;
            curr.addLast(candidates[i]);
            dfs(result, curr, candidates, sum + candidates[i], target, i);
            curr.removeLast();
        }
    }
}
```

#### 🌟 **Algorithm Idea**

- Use **Depth-First Search (DFS)** for recursive solving.
- Each number can be reused, so the current index remains unchanged during recursive calls.
- When the sum equals the target value, add the current combination to the result.
- If the sum exceeds the target value, return immediately.

#### ⏱ **Time Complexity**

- **Worst case**: Generate all possible combinations, similar to the **unbounded knapsack problem**:
  - **Time complexity**: `O(2^t * k)`
    - `t` is the target value, `k` is the length of the combination.
    - Exponential, as each number can be selected repeatedly.
- **Space complexity**: `O(target / min(candidates))`
  - The maximum recursion depth is `target / min(candidates)`.

#### 📊 **Pruning Effect Statistics**

Test Case:

```text
candidates = [2, 3, 6, 7], target = 7
```

**Output:**

```
Nodes before pruning: 16
Nodes after pruning: 8
```

🚦 **Pruning Effectiveness Calculation**

```text
Pruning effectiveness percentage = (1 - (cntAfter / cntBefore)) * 100%
Pruning effectiveness = (1 - (8 / 16)) * 100% = 50%
```

#### ✅ **Comparison: Before Pruning vs After Pruning**

| Version | Node Count (cntBefore) | Node Count After Pruning (cntAfter) | Pruning Effectiveness |
| ------------ | ---------------------- | ----------------------------------- | --------------------- |
| No Pruning | 16 | 16 | 0% |
| With Pruning | 16 | 8 | 50% |

### 💻 DP Version

```java
class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        // Initialize DP array
        List<List<Integer>>[] dp = new ArrayList[target + 1];
        for (int i = 0; i <= target; i++) {
            dp[i] = new ArrayList<>();
        }
        dp[0].add(new ArrayList<>()); // Base case: empty set when sum is 0

        // Iterate through each candidate number
        for (int num : candidates) {
            for (int j = num; j <= target; j++) {
                // Iterate through all prefix combinations and add the current num
                for (List<Integer> sublist : dp[j - num]) {
                    List<Integer> newList = new ArrayList<>(sublist);
                    newList.add(num);
                    dp[j].add(newList);
                }
            }
        }

        return dp[target];
    }
}
```

### Problem Classification:

- This is an **unbounded knapsack problem**, where the same element can be repeatedly selected to achieve the target sum `target`.
- It is a combination problem, so the order does not matter. For example, `[2, 2, 3]` and `[3, 2, 2]` are considered the same solution.

### State Definition:

- `dp[i]` represents **all combinations with a sum of `i`**.
- Since it records all combinations, `dp[i]` is a `List<List<Integer>>` rather than a single number.

### State Transition:

- Iterate through all `candidates` and all `j`, updating each `dp[j]`:

  ```text
  dp[j] = dp[j] + dp[j - candidates[i]] + candidates[i]
  ```

- If `dp[j - candidates[i]]` is non-empty, it means we can **append the current element** to all these combinations to achieve `j`.

### Initialization:

- `dp[0] = [[]]`, meaning there is an empty combination when the sum is 0 (this is the recursive base case).

#### 🔍 **Time Complexity Analysis**

- **Time complexity**: `O(n * target * k)`

  - `n` is the number of `candidates`;
  - `target` is the target value;
  - `k` is the average length of intermediate combinations;
  - In the worst case, each subset may generate new subsets.

- **Space complexity**: `O(target * m)`

  - `m` is the average size of combinations, and `dp[i]` records all valid combinations.

#### ✅ **DFS vs DP Comparison**

| Comparison Item | DFS Backtracking | DP Unbounded Knapsack |
| --------------- | ----------------------- | -------------------------- |
| **Time Complexity** | Exponential: `O(2^n * k)` | `O(n * target * k)` |
| **Space Complexity** | Recursion depth + extra arrays | `O(target * k)` |
| **Duplication Handling** | Naturally avoids duplicates in recursion tree | Ensures no duplicates through traversal order |
| **Pruning Efficiency** | Requires manual implementation | Naturally prunes (no state conflicts) |
| **Parallelism** | Poor (depth-first single path) | High (each state is computed independently) |
| **Implementation Difficulty** | Relatively simple, clear logic | State transition is slightly complex |
| **Applicability** | Small-scale combinations, focused on output paths | Large-scale path statistics |

#### 🎯 **When to Use DFS vs DP?**

- If you only need to find one combination or output all paths, `DFS` is more intuitive.
- If you want to count the number of combinations or optimize complexity, `DP` is a better choice.
- If the problem constraints involve a large `target` (e.g., 10^4 level), `DP` is more efficient.

______________________________________________________________________

## LC 435 - Non-overlapping Intervals

### CODE

```java
class Solution {
  public int eraseOverlapIntervals(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
    int n = intervals.length;
    int result = 0;
    int max = intervals[0][1];
    for (int i = 1; i < n; i++) {
      if (intervals[i][0] < max) {
        result++;
      } else {
        max = intervals[i][1];
      }
    }
    return result;
  }
}
```

### ✅ **Reason for Local Optimality**

In the sorted intervals, **selecting the interval with the earliest end time** ensures that there is more space available for subsequent intervals. This is a **greedy strategy** that guarantees local optimality, leading to global optimality.

### ✅ **Proof of Correctness**

1. **Exchange Argument**

- Suppose at a certain step, we do not choose the interval with the earliest end time but instead choose one with a later end time.
- Since it ends later, it occupies more time, reducing the number of available intervals afterward.
- If we swap it with the interval that ends the earliest, the available space afterward will not decrease and may even increase.
- This proves that **choosing the earliest ending interval** is not worse than any other choice.

2. **Contradiction Argument**

- Assume there exists an optimal solution that does not choose the interval with the earliest end time.
- Replacing it with the interval that ends the earliest does not reduce the total number of intervals that can be accommodated and may even increase it.
- This contradicts the assumption of "optimality," so choosing the earliest ending interval is the optimal local strategy.

### ✅ **Time Complexity**

- **Sorting**: `O(n log n)`
- **Traversal and Comparison**: `O(n)`

**Total Complexity**: `O(n log n)`

> Sorting is the main time-consuming step, while traversal is linear, so the overall complexity is **O(n log n)**.

### ✅ **Space Complexity**

- Space complexity of sorting: `O(log n)` (stack space used by JDK's optimized TimSort)
- Additional variables: `max`, `result` → `O(1)`

**Total Space Complexity**: `O(log n)`

### ✅ **Summary**

| Item | Content |
| ------------------ | ------------------------------------ |
| **Local Optimal Strategy** | Select the interval with the earliest end time to ensure more space for subsequent intervals |
| **Proof of Correctness** | Exchange Argument + Contradiction Argument: Choosing the earliest end is not worse than others |
| **Time Complexity** | `O(n log n)` (sorting + traversal) |
| **Space Complexity** | `O(log n)` (stack space for sorting) |

______________________________________________________________________

## LC 452 - Minimum Number of Arrows to Burst Balloons

```java
class Solution {
  public int findMinArrowShots(int[][] points) {
    Arrays.sort(points, (a, b) -> {
      if (a[1] > b[1]) return 1;
      else if (a[1] < b[1]) return -1;
      else return 0;
    });
    int n = points.length;
    int cnt = 1;
    int end = points[0][1];
    for (int i = 1; i < n; i++) {
      if (end < points[i][0]) {
        cnt++;
        end = points[i][1];
      }
    }
    return cnt;
  }
}
```

______________________________________________________________________
