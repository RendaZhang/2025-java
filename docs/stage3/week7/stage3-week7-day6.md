<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 6 - Kubernetes / 云原生最小面](#day-6---kubernetes--%E4%BA%91%E5%8E%9F%E7%94%9F%E6%9C%80%E5%B0%8F%E9%9D%A2)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [Step 1 - 算法：动态规划](#step-1---%E7%AE%97%E6%B3%95%E5%8A%A8%E6%80%81%E8%A7%84%E5%88%92)
    - [LC322. Coin Change（最少硬币个数）](#lc322-coin-change%E6%9C%80%E5%B0%91%E7%A1%AC%E5%B8%81%E4%B8%AA%E6%95%B0)
    - [LC139. Word Break（字符串能否被字典切分）](#lc139-word-break%E5%AD%97%E7%AC%A6%E4%B8%B2%E8%83%BD%E5%90%A6%E8%A2%AB%E5%AD%97%E5%85%B8%E5%88%87%E5%88%86)
    - [LC221. Maximal Square（最大全 1 正方形）](#lc221-maximal-square%E6%9C%80%E5%A4%A7%E5%85%A8-1-%E6%AD%A3%E6%96%B9%E5%BD%A2)
  - [Step 2 - Kubernetes / 云原生](#step-2---kubernetes--%E4%BA%91%E5%8E%9F%E7%94%9F)
    - [Pod & Container 基础](#pod--container-%E5%9F%BA%E7%A1%80)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 6 - Kubernetes / 云原生最小面

## 今日目标

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

- 如果 `matrix[i-1][j-1] == '1'`，则当前以该格为右下角的最大正方形边长 `dp[i][j] = 1 + min(dp[i-1][j] , dp[i][j-1], dp[i-1][j-1])`；否则为 0。
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

---

## Step 2 - Kubernetes / 云原生

### Pod & Container 基础

- **Pod 是“进程组”**：同 IP/localhost、共享 Volume、同调度命运；适合主容器 + sidecar/Init 协作。
- **requests 决定放得下，limits 决定用得了**：延迟敏感服务常用“**内存有限、CPU 不限**”避免 throttle；关注 QoS 级别。
- **OOM 与 CrashLoop**：内存超限 → OOMKill；启动期/探针过严 → CrashLoopBackOff；排查用 `describe`、`logs -p`、事件时间线。
- **Init/Sidecar 生命周期**：Init 只做前置短任务；Sidecar 要配 `preStop` 与合理终止宽限，避免截断请求。
- **日志首选 stdout/stderr + JSON**：集中采集、字段固定（建议含 `trace_id`/`span_id`）；避免无界本地文件。
- **临时存储有额度**：为 `emptyDir`/日志等设 **ephemeral-storage** request/limit，防驱逐；监控磁盘压力量表。
- **镜像不可变**：用 **Digest 或不可变 tag**，避免“回滚代码却拉到新镜像”。

> “把 Pod 当作一个**共享网络与卷的 OS 进程组**；**调度看 requests，约束看 limits**。对延迟敏感应用，我们**内存设限、CPU 不限**，日志走 stdout 的结构化 JSON，再用 sidecar/daemon 采集。”

场景 A - 为什么调度单位是 Pod 而不是容器

**面试官：** K8s 为什么以 Pod 为最小调度单位？

**我：** 因为同一 Pod 里的容器**共享一组资源与命名空间**：一个 IP/端口空间、同一 localhost、共享 Volume、同一调度命运。这让**主容器 + 辅助容器**（如 sidecar 代理、日志收集、初始化任务）能在**进程间通信成本极低**的前提下协作。调度放到“Pod 级别”，能把这些强耦合进程一次性编排、扩缩和回滚。

场景 B - 资源请求/限制与 QoS

**面试官：** `requests` 和 `limits` 分别起什么作用？如何避免性能抖动？

**我：** `requests` 影响**调度**与预留；`limits` 由 cgroup **强制约束**。

QoS 取决于两者配置：Guaranteed（req=lim 且全量设置）> Burstable > BestEffort。

性能要稳：

- 对**延迟敏感**服务，常用“**设内存 limit，CPU 不设 limit**（仅设 request）”避免 CPU throttle；
- 内存超限会被 **OOMKill**，要留安全裕度，并观测 RSS 与堆外内存；
- 结合 HPA 扩容，用**平均利用率**而不是瞬时峰值，配稳定窗口防抖。

场景 C - 重启策略与常见陷阱

**面试官：** `restartPolicy` 有哪些，和控制器的关系是？

**我：** Pod 级有 `Always`/`OnFailure`/`Never`。Deployment/ReplicaSet 强制 `Always`（服务型进程），Job 通常 `OnFailure`。

常见陷阱是**把启动失败**误当业务错误：探针/环境变量缺失导致 **CrashLoopBackOff**。

排查顺序：`kubectl describe` 看事件 → `kubectl logs -p` 看上次崩溃日志 → 校验探针与资源是否过严。

场景 D - Init/Sidecar 与生命周期

**面试官：** Init 与 Sidecar 你怎么用？

**我：** **InitContainer** 做**一次性前置**（拉配置、等待依赖、做数据迁移的“哨兵检查”）；全部成功后主容器才启动。**Sidecar**长期伴随主容器（如 envoy、日志/指标收集）。

注意：

- Init 不能做长时任务，否则**阻塞扩容**；
- Sidecar 需要**优雅下线**（`preStop` + 合理 `terminationGracePeriodSeconds`），否则请求可能在退出时被截断。

场景 E - 日志与临时存储

**面试官：** 线上日志怎么落？为什么很多团队不建议写本地文件？

**我：** **首选 stdout/stderr + 结构化 JSON**，由节点或 sidecar 采集器（如 Fluent Bit）拉走。

写本地文件易踩：**轮转不可控、占用临时存储、迁移丢失**、侧车再采集一跳延迟。

若必须本地，挂 `emptyDir` 并**限制临时存储 request/limit**，防止因磁盘压力被驱逐。
