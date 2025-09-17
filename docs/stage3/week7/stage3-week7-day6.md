<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 6 - Kubernetes / 云原生最小面](#day-6---kubernetes--%E4%BA%91%E5%8E%9F%E7%94%9F%E6%9C%80%E5%B0%8F%E9%9D%A2)
  - [Step 1 - 算法：动态规划](#step-1---%E7%AE%97%E6%B3%95%E5%8A%A8%E6%80%81%E8%A7%84%E5%88%92)
    - [LC322. Coin Change（最少硬币个数）](#lc322-coin-change%E6%9C%80%E5%B0%91%E7%A1%AC%E5%B8%81%E4%B8%AA%E6%95%B0)
    - [LC139. Word Break（字符串能否被字典切分）](#lc139-word-break%E5%AD%97%E7%AC%A6%E4%B8%B2%E8%83%BD%E5%90%A6%E8%A2%AB%E5%AD%97%E5%85%B8%E5%88%87%E5%88%86)
    - [LC221. Maximal Square（最大全 1 正方形）](#lc221-maximal-square%E6%9C%80%E5%A4%A7%E5%85%A8-1-%E6%AD%A3%E6%96%B9%E5%BD%A2)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 6 - Kubernetes / 云原生最小面

- 算法：动态规划
- 面试能力/知识：Kubernetes 最小面要点 —— Pod / Service / Ingress、探针（startup/readiness/liveness）、HPA、ConfigMap / Secret、PDB、最小权限（OIDC/IRSA）
- 英语：准备并口述 1 分钟 “Why HPA + probes + least-privilege?”

---

## Step 1 - 算法：动态规划

### LC322. Coin Change（最少硬币个数）

**思路（经典一维 DP / 完全背包）**

- 定义：`dp[a]` 表示凑出金额 `a` 需要的最少硬币数。
- 初始化：`dp[0] = 0`；其余为不可达，用哨值 `INF = amount + 1` 表示（避免溢出）。
- 转移：对每个金额 `a = 1..amount`，遍历每个 `coin`：若 `a >= coin` 且 `dp[a - coin] != INF`，则
  `dp[a] = min(dp[a], dp[a - coin] + 1)`。
- 答案：`dp[amount] == INF ? -1 : dp[amount]`。

**Java 代码**

```java
import java.util.Arrays;

class Solution322 {
    public int coinChange(int[] coins, int amount) {
        if (amount == 0) return 0;
        int INF = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, INF);
        dp[0] = 0;

        for (int a = 1; a <= amount; a++) {
            for (int c : coins) {
                if (a >= c && dp[a - c] != INF) {
                    dp[a] = Math.min(dp[a], dp[a - c] + 1);
                }
            }
        }
        return dp[amount] == INF ? -1 : dp[amount];
    }
}
```

**复杂度**

- 时间：`O(amount * n)`，`n` 为硬币种类数。
- 空间：`O(amount)`。

**易错点**

- 不要用 `Integer.MAX_VALUE` 当 INF 去做 `+1`，会溢出；用 `amount + 1` 更安全。
- `amount=0` 时应返回 0。
- 有些人会把循环顺序写成「先 coin 再 amount」（也对）；但要确保允许重复使用同一硬币（完全背包），两种写法都可以。

### LC139. Word Break（字符串能否被字典切分）

**思路（一维 DP + 长度剪枝）**

- 定义：`dp[i]` 表示前缀 `s[0..i)` 可被切分。`dp[0]=true`。
- 为减少无效匹配，先计算字典中单词的 `minLen` 与 `maxLen`，只在这个长度区间内尝试切分。
- 转移：对 `i=1..n`，只遍历 `j` 在 `[i - maxLen, i - minLen] ∩ [0, i)`，若 `dp[j]` 为真且 `s.substring(j, i)` 在字典中，则 `dp[i]=true` 并 `break`。

**Java 代码**

```java
import java.util.*;

class Solution139 {
    public boolean wordBreak(String s, List<String> wordDict) {
        int n = s.length();
        Set<String> dict = new HashSet<>(wordDict);
        int minLen = Integer.MAX_VALUE, maxLen = 0;
        for (String w : dict) {
            int len = w.length();
            minLen = Math.min(minLen, len);
            maxLen = Math.max(maxLen, len);
        }
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        for (int i = 1; i <= n; i++) {
            // 剪枝：i - j 的长度只在 [minLen, maxLen] 内
            int start = Math.max(0, i - maxLen);
            int end = i - minLen;
            for (int j = end; j >= start; j--) { // 由近到远，有利于尽快命中
                if (dp[j]) {
                    // 直接 substring；若在意常数，可用 Trie 或 regionMatches 优化
                    if (dict.contains(s.substring(j, i))) {
                        dp[i] = true;
                        break;
                    }
                }
            }
        }
        return dp[n];
    }
}
```

**复杂度**

- 时间：最坏 `O(n * L)` 次字典查询，`L`≈可尝试的长度个数（≤`maxLen - minLen + 1`），HashSet 查询均摊 `O(1)`；
  注意 `substring` 有复制成本，但在 LeetCode 约束下可接受。
- 空间：`O(n) + O(|dict|)`。

**易错点**

- 未做长度剪枝会超时（尤其当字典含很多长词）。
- 直接从 `j=0..i-1` 全扫会慢；加上 `minLen/maxLen` 的窗口能显著提速。
- 注意 `List` 里可能有重复词，先放入 `Set` 去重。

### LC221. Maximal Square（最大全 1 正方形）

**思路（二维 DP → 一维滚动优化）**

- 如果 `matrix[i-1][j-1] == '1'`，则当前以该格为右下角的最大正方形边长
  `dp[i][j] = 1 + min(dp[i-1][j] , dp[i][j-1], dp[i-1][j-1])`；否则为 0。
- 用一维数组 `dp[j]` 表示上一行到当前列的结果，`prev` 存上一行的左上角（即原 `dp[i-1][j-1]`）。
- 维护最大边长 `maxSide`，答案返回 `maxSide * maxSide`。

**Java 代码**

```java
class Solution221 {
    public int maximalSquare(char[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return 0;
        int m = matrix.length, n = matrix[0].length;
        int[] dp = new int[n + 1]; // dp[j] 对应上一行的 dp[i-1][j]
        int maxSide = 0;

        for (int i = 1; i <= m; i++) {
            int prevDiag = 0; // 保存上一行上一列（dp[i-1][j-1]）
            for (int j = 1; j <= n; j++) {
                int temp = dp[j]; // 先备份上一行当前列（用于下一格的 prevDiag）
                if (matrix[i - 1][j - 1] == '1') {
                    dp[j] = Math.min(Math.min(dp[j], dp[j - 1]), prevDiag) + 1;
                    maxSide = Math.max(maxSide, dp[j]);
                } else {
                    dp[j] = 0;
                }
                prevDiag = temp;
            }
        }
        return maxSide * maxSide;
    }
}
```

**复杂度**

- 时间：`O(m * n)`；空间：`O(n)`。

**易错点**

- 注意输入是字符 `'0'/'1'`，别与整数 0/1 混淆。
- 一维滚动时 `prevDiag` 的更新顺序：进入下一列前，用 `temp` 保存旧的 `dp[j]` 再赋给 `prevDiag`。
- 返回的是**面积**（边长平方），不是边长。
