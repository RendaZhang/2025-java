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
    - [Pod & Container 基础：Pod 为什么是最小调度单位；资源请求/限制；重启策略；日志规范](#pod--container-%E5%9F%BA%E7%A1%80pod-%E4%B8%BA%E4%BB%80%E4%B9%88%E6%98%AF%E6%9C%80%E5%B0%8F%E8%B0%83%E5%BA%A6%E5%8D%95%E4%BD%8D%E8%B5%84%E6%BA%90%E8%AF%B7%E6%B1%82%E9%99%90%E5%88%B6%E9%87%8D%E5%90%AF%E7%AD%96%E7%95%A5%E6%97%A5%E5%BF%97%E8%A7%84%E8%8C%83)
    - [Service / Ingress / Gateway：流量路径；超时/重试/黏性会话；常见 502/504 排查口径](#service--ingress--gateway%E6%B5%81%E9%87%8F%E8%B7%AF%E5%BE%84%E8%B6%85%E6%97%B6%E9%87%8D%E8%AF%95%E9%BB%8F%E6%80%A7%E4%BC%9A%E8%AF%9D%E5%B8%B8%E8%A7%81-502504-%E6%8E%92%E6%9F%A5%E5%8F%A3%E5%BE%84)
    - [Probes 与优雅下线：startup/readiness/liveness 的边界；`preStop` + `terminationGrace`；预热与缓存](#probes-%E4%B8%8E%E4%BC%98%E9%9B%85%E4%B8%8B%E7%BA%BFstartupreadinessliveness-%E7%9A%84%E8%BE%B9%E7%95%8Cprestop--terminationgrace%E9%A2%84%E7%83%AD%E4%B8%8E%E7%BC%93%E5%AD%98)
    - [HPA 与自动扩缩容：CPU/内存 vs 自定义指标；`stabilizationWindow`、`scaleDownPolicy` 抖动治理；冷启动](#hpa-%E4%B8%8E%E8%87%AA%E5%8A%A8%E6%89%A9%E7%BC%A9%E5%AE%B9cpu%E5%86%85%E5%AD%98-vs-%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8C%87%E6%A0%87stabilizationwindowscaledownpolicy-%E6%8A%96%E5%8A%A8%E6%B2%BB%E7%90%86%E5%86%B7%E5%90%AF%E5%8A%A8)
    - [配置与发布安全：ConfigMap/Secret 版本化与回滚；镜像不可变标签；RollingUpdate 参数；PDB 与 `drain`](#%E9%85%8D%E7%BD%AE%E4%B8%8E%E5%8F%91%E5%B8%83%E5%AE%89%E5%85%A8configmapsecret-%E7%89%88%E6%9C%AC%E5%8C%96%E4%B8%8E%E5%9B%9E%E6%BB%9A%E9%95%9C%E5%83%8F%E4%B8%8D%E5%8F%AF%E5%8F%98%E6%A0%87%E7%AD%BErollingupdate-%E5%8F%82%E6%95%B0pdb-%E4%B8%8E-drain)
    - [最小权限与身份（RBAC / OIDC / IRSA）：ServiceAccount 绑定最小权限；云资源精细授权；密钥不落盘](#%E6%9C%80%E5%B0%8F%E6%9D%83%E9%99%90%E4%B8%8E%E8%BA%AB%E4%BB%BDrbac--oidc--irsaserviceaccount-%E7%BB%91%E5%AE%9A%E6%9C%80%E5%B0%8F%E6%9D%83%E9%99%90%E4%BA%91%E8%B5%84%E6%BA%90%E7%B2%BE%E7%BB%86%E6%8E%88%E6%9D%83%E5%AF%86%E9%92%A5%E4%B8%8D%E8%90%BD%E7%9B%98)

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

### Pod & Container 基础：Pod 为什么是最小调度单位；资源请求/限制；重启策略；日志规范

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

### Service / Ingress / Gateway：流量路径；超时/重试/黏性会话；常见 502/504 排查口径

- **路径心智图**：Client → DNS → 外部 LB → **Ingress/Gateway(L7)** → **Service(L4)** → Pod。
- **Service 取舍**：ClusterIP 内网；NodePort 兜底；LoadBalancer 云上对外。公网常用 **LB + Ingress**。
- **502 vs 504**：502 看**上游断连/协议/TLS/探针**；504 看**超时不匹配**（Ingress 超时 < 应用时长）。
- **超时/重试对齐**：**外松内紧**，幂等请求才有限重试；加**抖动回退**，避免放大故障。
- **会话保持**：优先**无状态**；粘性仅作短期权衡，注意**滚动升级 + TTL**。
- **真实 IP 与限流**：信任链路明确；`X-Forwarded-For`/Proxy Protocol 配套；限流优先按**令牌/租户**。

> “我把流量治理分层：**L7 的 Ingress/Gateway 负责路由与策略，L4 的 Service 负责发现与均衡**；**超时与重试外松内紧**，502 查协议/探针，504 查超时链路，尽量无状态避免粘性副作用。”

场景 A - 从 Client 到 Pod 的流量路径

**面试官：** 说一下从用户到后端 Pod 的典型路径，以及各层的职责？

**我：** 常见是 **Client → DNS → 外部 LB → Ingress/Gateway → Service → Pod**。

- **Ingress** 是 L7 路由与 TLS 终止（基于域名/路径），**Gateway API** 是 Ingress 的升级版，能力更细（超时、重试、流量分配更原生）；
- **Service** 做 L4 发现与负载（ClusterIP/NodePort/LoadBalancer），屏蔽 Pod 漂移；
- **Pod** 由 kube-proxy/iptables/ipvs 命中，落到后端容器。

场景 B - ClusterIP / NodePort / LoadBalancer 什么时候用

**面试官：** 三种 Service 类型你怎么取舍？

**我：**

- **ClusterIP**：集群内访问（默认）；
- **NodePort**：简单对外、无云厂商 LB 时兜底；
- **LoadBalancer**：云上最常用，对外暴露一个公网/私网 VIP。

通常**公网进来用 LB + Ingress**；集群内服务间通信用 ClusterIP。

场景 C  - 502 vs 504 如何快速定位

**面试官：** 线上有时 502，有时 504，你怎么分辨与排查？

**我：**

- **502** 多为**上游断连/握手失败**（后端没起好、协议不匹配、TLS 终止错位、readiness 失败）；
- **504** 是**超时**（Ingress/Gateway 超时 < 应用处理时长）。

排查顺序：看 Ingress/Gateway 日志与**后端探针**；核对**超时链路**（Client / Ingress / Service / 应用）是否一致；若是 gRPC，要检查 **:authority / HTTP2** 与 **grpc-timeout** 是否正确。

场景 D - 超时与重试的对齐

**面试官：** 超时与重试在 Ingress、应用、客户端各如何配置更稳妥？

**我：** 原则是**从内到外逐级放宽**、且**只对幂等请求有限重试**：

- **客户端**设置**总超时**；
- **Ingress/Gateway** 设置**请求超时 < 客户端**，并限制**最大重试次数 + 抖动回退**；
- **应用**内部更短的**下游超时**，避免被外层重试放大。

同时把**429/503**这类**可重试**与**不可重试**错误区分，避免“重试风暴”。

场景 E - 会话保持与无状态改造

**面试官：** 要不要开 sticky session？

**我：** 能**无状态**就无状态，状态放**外部存储（Redis/DB）**。

确需粘性：

- 短期解决“冷缓存命中率低/长连接握手成本高”；
- 注意**滚动升级**时粘性会把旧 Pod“粘死”，要配较短 cookie TTL 或在灰度阶段只对小流量粘性。
- 长期建议**一致性哈希或外部会话**来替代。

场景 F - 真实客户端 IP 与限流

**面试官：** 应用层如何拿到用户真实 IP 做审计/限流？

**我：** 在 Ingress/Gateway **保留并信任** `X-Forwarded-For` / `X-Real-IP`，应用只读**受信代理**插入的头。若外部 LB 用 **Proxy Protocol**，Ingress 要同步开启。限流优先按**用户令牌/租户**，IP 只作兜底（NAT 下 IP 不稳定）。

### Probes 与优雅下线：startup/readiness/liveness 的边界；`preStop` + `terminationGrace`；预热与缓存

- **三分工**：startup 保护冷启动；**readiness 是唯一接流量闸门**；liveness 只为**自愈重启**。
- **轻量 readiness**：只测“可安全接流量”的内部条件；外部依赖用**熔断/降级**而非探针硬连。
- **优雅下线五步**：`SIGTERM → readiness false → preStop 排空 → 宽限期 → SIGKILL`；HTTP/2/gRPC 要做 **drain**。
- **发布保护**：`startupProbe` 足够宽松、**预热**后才 ready；`maxUnavailable=0`、`maxSurge=1`、关键服务加 **PDB**。
- **时序一致性**：Ingress/Gateway/应用的**超时与 Keep-Alive** 与 `terminationGrace` 对齐，避免 502/504。
- **观测字段**：在日志/指标中记录 **probe 状态变化、终止信号、排空耗时、未完成请求数**，便于复盘。

> “我们把 **readiness 当唯一闸门**、**liveness 只做自愈**，发布时先 `SIGTERM`→`readiness=false`→`preStop` 排空并对 gRPC 做 **drain**，让**时序与超时**严格对齐，就不会在滚动升级里炸出 502/504。”

场景 A - 三类 Probe 到底怎么分工

**面试官：** startup、readiness、liveness 的边界？

**我：**

- **startupProbe**：**冷启动期的保护罩**。在它“放行”前，liveness/readiness 都**不生效**，避免慢启动被误杀。
- **readinessProbe**：**唯一接流量的闸门**。失败 = 从 Service 端点摘除，不再分到新请求。
- **livenessProbe**：进程**自愈**开关。持续失败 = kubelet **重启容器**。

调参：先给 startup 足够的 `initialDelaySeconds`/`failureThreshold`，readiness 再严格一些，liveness 最保守，避免“自杀循环”。

场景 B - “假健康报错”与依赖检查

**面试官：** 常见“健康但报错”的原因？

**我：**

两类坑：

1. **探针写太重**：readiness 每次都跑依赖探活（连 DB/外部 API），一旦下游抖动就把自己摘流，形成雪崩；
2. **指标口径错**：liveness 监控业务错误码，导致小抖动触发重启。

实践：**readiness 仅检查“是否能安全接流量”**（线程/队列/连接池可用、关键本地依赖就绪），**下游依赖用熔断/降级**兜住，不要让探针放大故障。

场景 C - 优雅下线的完整序列

**面试官：** 描述一次优雅下线的时序。

**我：**

1. **接收 SIGTERM**（或节点驱逐/滚动升级触发）；
2. 立刻让 **readiness 返回失败** → 从 Service 端点摘除，**不再接新流量**；
3. 执行 **preStop**：停止接受新连接，开启**连接/队列排空**，等待飞行中的请求完成；
4. 在 `terminationGracePeriodSeconds` **宽限期**内完成收尾；
5. 若仍未完成，最后 **SIGKILL** 强制终止。

细节：HTTP/2/gRPC 要开启**连接耗尽**（drain），设置**服务器/反代的 Keep-Alive 超时**覆盖宽限期，避免升级时 502/504。

场景 D - 发布时 502/504 的探针与时序问题

**面试官：** 滚动升级偶发 502/504，你会怎么看？

**我：**

多半是**时序没对齐**：新 Pod **未预热**就被判 ready（缓存未建、JIT/连接未起），或旧 Pod **还在处理**时已被摘除/被 SIGKILL。

动作：

- 给 **startupProbe** 足够时间；
- **预热**：启动后先拉缓存/建连接再放行 readiness；
- **preStop + 合理的 terminationGrace** 保证排空；
- Ingress/网关配置**上游连接耗尽**与**超时一致性**；
- Deployment 设 `maxUnavailable=0, maxSurge=1`，关键服务配 **PDB**，杜绝容量“塌陷”。

场景 E - gRPC / 长连接的健康与排空

**面试官：** gRPC 如何做健康检查与排空？

**我：**

健康用 **gRPC health check**（`grpc.health.v1.Health/Check`），readiness 只要返回 SERVING；下线时先**停止新流量**，再对现有 HTTP/2 流开启 **drain**（发送 GOAWAY/限制新流），等待活跃 RPC 完成，宽限期内强制截止未完成的长尾。

### HPA 与自动扩缩容：CPU/内存 vs 自定义指标；`stabilizationWindow`、`scaleDownPolicy` 抖动治理；冷启动

- **按负载选指标**：CPU/内存适合计算型；I/O/外依赖型优先**队列深度/并发/QPS**等**自定义指标**。
- **容量估算**：`副本 ≈ (峰值 QPS / 单 Pod QPS) × 1.2~1.5`；把这个目标转成**平均每 Pod 的目标值**给 HPA。
- **快涨慢降**：v2 behavior：`scaleUp 0s 窗口 + (Pods/Percent)`；`scaleDown 稳定窗口 ≥300s`，每分钟最多 -20%。
- **冷启动治理**：`startupProbe` + **预热后才 readiness**；`minReplicas` ≥ 稳态需求；必要时**过量预置**少量空载副本。
- **联动策略**：重试有**预算 + 抖动**；必要时引入 **KEDA/Adapter** 用**队列/并发**扩容。
- **与集群层协同**：确保 **requests** 合理，配 **Cluster Autoscaler** 与 **PDB**，避免“扩容无位可排”。
- **限流优先**：扩容跟不上先**限流/降级**，再扩容，防止尾延迟雪崩。

> “我们把 HPA 做成**快涨慢降**，指标按**负载类型**选择（CPU vs 队列/并发），**预热后才接流量**，并用 **Cluster Autoscaler + PDB** 保证扩容能落地；扩不及时时先**限流**守住尾延迟。”

场景 A - 该选什么指标来扩缩容

**面试官：** 你用什么作为 HPA 指标？CPU 就够了吗？

**我：**

看负载类型。

- **CPU/内存**适合 **CPU 绑定**或**内存增长性**的服务；
- **自定义指标**适合业务负载，比如**每 Pod 并发数/QPS、队列长度、pending jobs**；
- I/O 或外部依赖主导的服务，用 CPU 扩容常常**动作滞后**或**放大抖动**。这类我更偏向**队列深度/吞吐**或**请求在途数**做目标。

场景 B - 如何设定目标值与估算容量

**面试官:** 有一个 API，每个 Pod 稳态能抗 `~200` QPS，峰值要 3k QPS，你怎么估？

**我：**

先给**容量心智算式**：`需要副本 ≈ 峰值 QPS / 单 Pod 可承载 QPS × 安全系数(1.2~1.5)`。

所以 `3000/200=15`，×1.3 安全系数 ≈ **20 副本**。HPA 的**自定义平均目标**就设为**每 Pod 200 QPS 左右**（或并发 200），`minReplicas` 至少**略大于稳态需求**，防止频繁冷启动。

场景 C - 控制抖动与“锯齿”

**面试官：** 怎么避免 HPA 来回抖？

**我：**

用 **v2 behavior** 做“**快涨慢降**”：

- **scaleUp**：窗口 0s，允许**每分钟 +100% 或 +10 个 Pod**，取较大者；
- **scaleDown**：**稳定窗口 300s**，每分钟最多 `-20%`；
- 另外配合 **readiness 预热**与**队列排空**，保证新 Pod 真能接流量，旧 Pod 退场不丢请求。

> 示例片段（可参考）：

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata: { name: api-hpa }
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: api
  minReplicas: 4
  maxReplicas: 50
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 60
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 0
      selectPolicy: Max
      policies:
      - type: Percent
        value: 100
        periodSeconds: 60
      - type: Pods
        value: 10
        periodSeconds: 60
    scaleDown:
      stabilizationWindowSeconds: 300
      selectPolicy: Max
      policies:
      - type: Percent
        value: 20
        periodSeconds: 60
```

场景 D - 冷启动与“扩容不见效”

**面试官：** 扩容了，但延迟仍然高，是不是 HPA 没用？

**我：**

常见是**冷启动/连接预热**没做：新 Pod 标记 ready 太早，缓存/连接/JIT 还没好；或者节点资源不足，需要 **Cluster Autoscaler** 先拉新节点。

处理：

- **startupProbe** + **预热**后再通过 **readiness**；
- `minReplicas` 给到**稳态需要量**，避免每次从 1 拉满；
- 结合 **集群自动扩容**与 **PDB**，保证新副本有位可调度。

场景 E - 与重试/超时/队列的联动

**面试官：** 高峰下，扩容跟不上，重试会把问题放大吗？

**我：** 会。

对**幂等请求**保留**有限重试 + 抖动回退**，同时以**队列长度/在途数**作为**外部指标**驱动扩容（KEDA 或 Prometheus Adapter）。把**重试预算 ≤10%** 写进策略，避免“重试风暴”。

场景 F - HPA 与 VPA/requests 的关系

**面试官：** 用 HPA 的同时，requests/limits 和 VPA 怎么配？

**我：** **CPU HPA 依赖 requests 计算利用率**，所以必须合理设置 `requests`；**VPA 与 HPA 不要同时调同一资源**（常见是 VPA 只建议或只动内存，HPA 只看 CPU/自定义指标）。还要避免 **CPU limit 过低**导致节流，引起**假高利用率**误扩容。

### 配置与发布安全：ConfigMap/Secret 版本化与回滚；镜像不可变标签；RollingUpdate 参数；PDB 与 `drain`

### 最小权限与身份（RBAC / OIDC / IRSA）：ServiceAccount 绑定最小权限；云资源精细授权；密钥不落盘
