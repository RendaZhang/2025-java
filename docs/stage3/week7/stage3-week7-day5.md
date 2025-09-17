<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 5 - 可观测与发布（指标·告警·灰度回滚）](#day-5---%E5%8F%AF%E8%A7%82%E6%B5%8B%E4%B8%8E%E5%8F%91%E5%B8%83%E6%8C%87%E6%A0%87%C2%B7%E5%91%8A%E8%AD%A6%C2%B7%E7%81%B0%E5%BA%A6%E5%9B%9E%E6%BB%9A)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [Step 1 - 算法：堆 / 数据流](#step-1---%E7%AE%97%E6%B3%95%E5%A0%86--%E6%95%B0%E6%8D%AE%E6%B5%81)
    - [LC703. Kth Largest Element in a Stream（数据流第 K 大）](#lc703-kth-largest-element-in-a-stream%E6%95%B0%E6%8D%AE%E6%B5%81%E7%AC%AC-k-%E5%A4%A7)
    - [LC378. Kth Smallest in a Sorted Matrix（有序矩阵第 K 小）](#lc378-kth-smallest-in-a-sorted-matrix%E6%9C%89%E5%BA%8F%E7%9F%A9%E9%98%B5%E7%AC%AC-k-%E5%B0%8F)
    - [LC378 高质量复盘](#lc378-%E9%AB%98%E8%B4%A8%E9%87%8F%E5%A4%8D%E7%9B%98)
    - [LC373. Find K Pairs with Smallest Sums（两数组和最小的 K 对）](#lc373-find-k-pairs-with-smallest-sums%E4%B8%A4%E6%95%B0%E7%BB%84%E5%92%8C%E6%9C%80%E5%B0%8F%E7%9A%84-k-%E5%AF%B9)
    - [LC295. Find Median from Data Stream（数据流中位数）](#lc295-find-median-from-data-stream%E6%95%B0%E6%8D%AE%E6%B5%81%E4%B8%AD%E4%BD%8D%E6%95%B0)
  - [Step 2 - 可观测 × 发布核心](#step-2---%E5%8F%AF%E8%A7%82%E6%B5%8B-%C3%97-%E5%8F%91%E5%B8%83%E6%A0%B8%E5%BF%83)
    - [日志 / 指标 / 追踪（OTel / Prom / Grafana）](#%E6%97%A5%E5%BF%97--%E6%8C%87%E6%A0%87--%E8%BF%BD%E8%B8%AAotel--prom--grafana)
    - [SLO / 告警阈值 / 误差预算](#slo--%E5%91%8A%E8%AD%A6%E9%98%88%E5%80%BC--%E8%AF%AF%E5%B7%AE%E9%A2%84%E7%AE%97)
    - [发布与回滚（灰度 / 蓝绿 / 金丝雀）](#%E5%8F%91%E5%B8%83%E4%B8%8E%E5%9B%9E%E6%BB%9A%E7%81%B0%E5%BA%A6--%E8%93%9D%E7%BB%BF--%E9%87%91%E4%B8%9D%E9%9B%80)
  - [Step 3 - 一页架构速记：可观测与发布策略](#step-3---%E4%B8%80%E9%A1%B5%E6%9E%B6%E6%9E%84%E9%80%9F%E8%AE%B0%E5%8F%AF%E8%A7%82%E6%B5%8B%E4%B8%8E%E5%8F%91%E5%B8%83%E7%AD%96%E7%95%A5)
    - [TraceID 串联排障（4 步到位）](#traceid-%E4%B8%B2%E8%81%94%E6%8E%92%E9%9A%9C4-%E6%AD%A5%E5%88%B0%E4%BD%8D)
    - [看板读法（RED/USE）](#%E7%9C%8B%E6%9D%BF%E8%AF%BB%E6%B3%95reduse)
    - [SLI / SLO / 告警阈值](#sli--slo--%E5%91%8A%E8%AD%A6%E9%98%88%E5%80%BC)
    - [发布与回滚（灰度/蓝绿/滚动）](#%E5%8F%91%E5%B8%83%E4%B8%8E%E5%9B%9E%E6%BB%9A%E7%81%B0%E5%BA%A6%E8%93%9D%E7%BB%BF%E6%BB%9A%E5%8A%A8)
    - [Runbook（10 分钟止血）](#runbook10-%E5%88%86%E9%92%9F%E6%AD%A2%E8%A1%80)
    - [安全与合规](#%E5%AE%89%E5%85%A8%E4%B8%8E%E5%90%88%E8%A7%84)
  - [Step 4 - 1 分钟英文口语](#step-4---1-%E5%88%86%E9%92%9F%E8%8B%B1%E6%96%87%E5%8F%A3%E8%AF%AD)
    - [60s Sample — Incident Postmortem (spoken)](#60s-sample--incident-postmortem-spoken)
    - [Fill-in Template（30–60 秒直读）](#fill-in-template3060-%E7%A7%92%E7%9B%B4%E8%AF%BB)
    - [3 Sound Bites（面试加分短句）](#3-sound-bites%E9%9D%A2%E8%AF%95%E5%8A%A0%E5%88%86%E7%9F%AD%E5%8F%A5)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 5 - 可观测与发布（指标·告警·灰度回滚）

## 今日目标

- **算法**：继续巩固“堆 / 优先队列（Top-K、合并流/数据流）”，完成 2–3 题并能口述复杂度与边界。
- **面试知识**：梳理日志/指标/追踪（OTel/Prom/Grafana 基本面），能给出一套 SLO 与告警阈值，并讲清楚发布与回滚（灰度/蓝绿）的关键守卫项；要点沉淀到 `QBANK.md`，并给 `architecture.md` 补 1 小节。
- **英语**：练习 1 分钟 **Incident Postmortem** 的结构化口述。

---

## Step 1 - 算法：堆 / 数据流

### LC703. Kth Largest Element in a Stream（数据流第 K 大）

**思路**

维护**大小为 k 的最小堆**；堆内始终是“当前最大的 k 个数”，堆顶即第 k 大。

**Java 模板**

```java
class KthLargest {
    private final int k;
    private final PriorityQueue<Integer> pq = new PriorityQueue<>(); // min-heap

    public KthLargest(int k, int[] nums) {
        this.k = k;
        for (int x : nums) add(x);
    }
    public int add(int val) {
        if (pq.size() < k) pq.offer(val);
        else if (val > pq.peek()) { pq.poll(); pq.offer(val); }
        return pq.peek();
    }
}
```

**复杂度**：初始化/每次 `add` 皆 O(log k)；空间 O(k)

**易错点**：`k` 可能 > 初始数组长度（先填够 k 再比较）；不要把堆写成**最大堆**。

### LC378. Kth Smallest in a Sorted Matrix（有序矩阵第 K 小）

**推荐思路：二分答案 + 计数**

矩阵每行、每列递增。设值域 `[lo, hi] = [matrix[0][0], matrix[n-1][n-1]]`；二分 `mid`，用“**从左下角/右上角起数 ≤mid 的元素个数**”来决定收缩区间。

**Java 模板（值域二分）**

```java
public int kthSmallest(int[][] a, int k) {
    int n = a.length;
    int lo = a[0][0], hi = a[n-1][n-1];
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (countLE(a, mid) >= k) hi = mid;
        else lo = mid + 1;
    }
    return lo;
}
private int countLE(int[][] a, int x) {
    int n = a.length, i = n - 1, j = 0, cnt = 0; // 从左下角走“Z”字
    while (i >= 0 && j < n) {
        if (a[i][j] <= x) { cnt += i + 1; j++; }  // 这一列上方都 ≤ x
        else { i--; }
    }
    return cnt;
}
```

**复杂度**：计数 O(n)，二分约 `log(hi-lo)` 次 → 总 O(n log V)；空间 O(1)

**易错点**：`mid` 取法避免溢出；计数函数从**左下角或右上角**出发更易写对。

**替代**：

小顶堆合并法 O(k log n)（把每行首元素入堆，弹出 k-1 次）。

```java
public int kthSmallest(int[][] matrix, int k) {
    int n = matrix.length;
    if (n == 0) return -1;

    // 最多只需要 n 个堆节点（每行一个游标）
    Node[] heap = new Node[Math.min(n, k)];
    int size = 0;

    // 初始化：每行第 1 个元素入堆
    for (int r = 0; r < n && r < k; r++) {
        heap[size] = new Node(matrix[r][0], r, 0);
        siftUp(heap, size++);
    }

    // 弹出 k-1 次：每次把该行的下一个元素补进堆
    while (--k > 0) {
        Node top = heap[0];
        int r = top.r, c = top.c;

        if (c + 1 < n) {                // 该行还有下一个
            heap[0] = new Node(matrix[r][c + 1], r, c + 1);
            siftDown(heap, 0, size);
        } else {                         // 该行耗尽，缩小堆
            heap[0] = heap[--size];
            heap[size] = null;
            siftDown(heap, 0, size);
        }
    }
    return heap[0].val;
}

// ===== 手写最小堆 =====
static class Node {
    int val, r, c;
    Node(int v, int r, int c) { this.val = v; this.r = r; this.c = c; }
}

private void siftUp(Node[] a, int i) {
    while (i > 0) {
        int p = (i - 1) >> 1;
        if (a[i].val >= a[p].val) break;
        swap(a, i, p);
        i = p;
    }
}
private void siftDown(Node[] a, int i, int n) {
    while (true) {
        int l = (i << 1) + 1;
        if (l >= n) break;
        int r = l + 1;
        int s = (r < n && a[r].val < a[l].val) ? r : l;
        if (a[i].val <= a[s].val) break;
        swap(a, i, s);
        i = s;
    }
}
private void swap(Node[] a, int i, int j) {
    Node t = a[i]; a[i] = a[j]; a[j] = t;
}
```

### LC378 高质量复盘

**关键不变量**：`countLE(mid) >= k` → 答案在左半区含 `mid`；计数的“Z 字走法”

**复杂度**：O(n log V)；当值域很大但 `n` 小时与堆法对比

**边界**：重复元素、单行/单列、极值（k=1 或 k=n²）

**踩坑**：计数方向写反、二分死循环、溢出

**为什么选“值域二分 + 计数”**，对比堆法的适用性与复杂度。

- 小顶堆合并
  - 时间：**O(k log n)**（堆大小 ≤ n，每次弹出/插入 log n）
  - 空间：**O(n)**
  - 适合：**k 较小**时非常快（经验阈值：当 `k ≲ 30n` 左右通常优于二分）
- 值域二分 + 单调计数
  - 时间：**O(n · log(range))**；`log(range)`≈ 31 次左右（int 范围）
  - 空间：**O(1)**
  - 适合：**k 很大**或想要更稳定的性能/更低内存时

> 小结：`k` 小选堆，`k` 大选二分；二者都对重复元素、负数友好，二分更省内存更稳定，堆在 Top-K 场景下常数更小。

### LC373. Find K Pairs with Smallest Sums（两数组和最小的 K 对）

**思路**：把 `(i,0)`（固定 `A[i]`，配 `B[0]`）入**小顶堆**（按 `A[i]+B[j]` 排序），每次弹 `(i,j)` 后把 `(i, j+1)` 入堆。

```java
public List<List<Integer>> kSmallestPairs(int[] A, int[] B, int k) {
    List<List<Integer>> res = new ArrayList<>();
    if (A.length==0 || B.length==0 || k<=0) return res;
    PriorityQueue<int[]> pq = new PriorityQueue<>((x,y)->(A[x[0]]+B[x[1]])-(A[y[0]]+B[y[1]]));
    int m = Math.min(A.length, k);
    for (int i = 0; i < m; i++) pq.offer(new int[]{i,0});
    while (k-- > 0 && !pq.isEmpty()) {
        int[] t = pq.poll();
        res.add(List.of(A[t[0]], B[t[1]]));
        if (t[1] + 1 < B.length) pq.offer(new int[]{t[0], t[1]+1});
    }
    return res;
}
```

**复杂度**：O(k log min(n, k))；空间 O(min(n, k))

### LC295. Find Median from Data Stream（数据流中位数）

**思路**：**双堆**——左边**最大堆**存较小的一半，右边**最小堆**存较大的一半；保证两堆大小差 ≤1，左堆允许多一个。

```java
class MedianFinder {
    private PriorityQueue<Integer> left = new PriorityQueue<>(Comparator.reverseOrder());
    private PriorityQueue<Integer> right = new PriorityQueue<>();
    public void addNum(int num) {
        if (left.isEmpty() || num <= left.peek()) left.offer(num);
        else right.offer(num);
        if (left.size() < right.size()) left.offer(right.poll());
        else if (left.size() - right.size() > 1) right.offer(left.poll());
    }
    public double findMedian() {
        if (left.size() == right.size()) return (left.peek() + right.peek()) / 2.0;
        return left.peek();
    }
}
```

**复杂度**：单次插入 O(log n)，查询 O(1)

---

## Step 2 - 可观测 × 发布核心

### 日志 / 指标 / 追踪（OTel / Prom / Grafana）

场景 A - Trace 贯穿与日志关联

**面试官：** 你如何保证一次请求的 TraceID 能跨多个微服务，并且在日志里也能串起来？

**我：** 我用 OTel 的 W3C `traceparent`/`tracestate` 头做上下文传播，Java 侧用 OTel Java agent 或 spring-boot autoconfigure 自动注入。HTTP 客户端/服务端会把上下文放进线程上下文。日志侧用 SLF4J MDC，logback pattern 带上 `%X{trace_id} %X{span_id}`。这样 Grafana 里看到异常点，点回 trace，再用 traceId grep 日志就能一跳到位。

**面试官：** 给个日志模式的例子？

**我：** logback pattern 类似：`%d{ISO8601} %-5level [%thread] %logger{36} traceId=%X{trace_id} spanId=%X{span_id} - %msg%n`。生产环境建议结构化 JSON，字段名固定，避免模糊解析。

**面试官：** 高并发下如何避免漏链路或乱串？

**我：** 三点：1 - 所有出口都用支持上下文的 HTTP/gRPC 客户端；2 - 禁掉线程池里“丢上下文”的自定义包装，或用 `ContextPropagators`/`TaskDecorator` 传递上下文；3 - 异步回调也要从 `Context.current()` 取 span。

场景 B - 指标基线与 RED/USE

**面试官：** 你在 Grafana 看一个服务，最先看哪些指标？

**我：** 先 RED：Rate（QPS/吞吐）、Errors（4xx/5xx、失败率）、Duration（p50/p90/p95）。系统面看 USE：Utilization（CPU/内存/线程/连接池占用）、Saturation（排队长度、GC、磁盘队列）、Errors（系统级错误）。这两套能快速定位是应用逻辑问题还是资源瓶颈。

**面试官：** 直方图怎么设置更有用？

**我：** 用 Micrometer/OTel 的 histogram，把关键接口打直方图，并且合理分桶，比如 API 延迟 10ms–5s 对数分桶，避免过细导致时序规模爆炸；为高价值接口开启 exemplars，这样能从 p95 点直接跳到样本 trace。

**面试官：** 标签会不会把 Prometheus 撑爆？

**我：** 控卡三条：1 - 禁止高基数标签（如 userId、请求体 hash）；2 - 控制 path 归一（模板化 `/orders/{id}`）；3 - 聚合维度有限白名单，非必要不打点。必要时在 collector 侧做 relabel/drop。

场景 C - 故障排查路径（p95 飙高）

**面试官：** 晚高峰 p95 从 120ms 涨到 900ms，你怎么排？

**我：** 先看 RED，确认是全局还是某两个接口；再看下游依赖面板（DB/Redis/外部 API）的 duration 和错误率；看 Saturation：线程池队列、连接池等待、GC、CPU 抢占。如果某接口异常，点 exemplars 拉一条慢 trace，看 span 哪一段膨胀，是 SQL 慢、重试风暴还是外部超时。最后回日志，用 traceId 定位代码上下文。

**面试官：** 如果是数据库导致的呢？

**我：** 短期：提高连接池上限+慢查询阈值观测、加缓存/只读副本、扩大超时并降低重试次数。长期：索引/SQL 计划优化、写读分离、热点拆表。并把回滚/降级开关写进 playbook。

场景 D - 采样与成本

**面试官：** Trace 量很大你怎么控成本？

**我：** 常规用 head sampling，比如 5–10%，并为错误/高延迟的请求动态提高采样；关键交易链路可按路由白名单全采。高流量场景用 tail sampling 在 collector 聚合后决定保留“异常/代表性”的 trace。日志用事件级采样（如相同异常 1 分钟仅采集 N 条）。

**面试官：** 什么时候必须全量？

**我：** 金融结算、风控审计或发布窗口短时全量，保证可追溯；窗口外恢复采样，避免存储爆炸。

场景 E - 日志质量与脱敏

**面试官：** 如何保证日志既可排障又不泄露隐私？

**我：** 结构化日志 + 字段级脱敏：手机号/邮箱打掩码或哈希；严禁落原始密钥/令牌；异常栈限制深度；在网关或 SDK 统一过滤敏感 query/body。落地上通过集中规则（logback turbo/filter 或 sidecar filter）统一治理。

**面试官：** 线上临时调试怎么做而不打扰业务？

**我：** 用日志级别动态开关（按 traceId/用户/路由范围），短时间提升到 DEBUG 并设置采样上限；或者用“诊断标记”头，仅对带标记的请求增加附加日志和 span 事件。

场景 F - 看板到行动（Runbook）

**面试官：** 看到错误率升高到 3%，你会怎么做？

**我：** 先确认是否真错误（4xx 分离），若 5xx 超出阈值，执行 Runbook：1 - 启动限流/熔断，止血下游；2 - 切回上一稳定版本或关闭灰度批次；3 - 打开降级开关；4 - 在 10 分钟观察窗内复核 RED 和关键下游指标；5 - 复盘记录 root cause、修复项和阈值是否需要重估。

### SLO / 告警阈值 / 误差预算

口袋清单（带走就能用）

- 先定**用户视角**的 SLI，再定 SLO，再考虑是否对外成 SLA。
- 事件型 SLI 最易落地：成功率 & p95/p99；入口统一计数、重试去重。
- 告警用“症状页警 + 原因工单”，多窗口燃尽率（1h 快、6h 慢）。
- 错误预算是**发布闸门**与**稳定性投资**的货币。
- 4xx 单列看板；确属策略问题的 4xx 需要进可用性讨论。

高分表达

- “我们用**多窗口燃尽率**做页警，症状导向叫醒人、原因导向建工单，既及时又不扰民。”
- “错误预算不是 KPI，而是**发布节奏与降级开关**的决策依据。”

场景 A - 把概念讲清楚

**面试官：** SLI、SLO、SLA 有什么区别？

**我：** SLI 是可量化的“怎么衡量好坏”（如成功率/延迟）；SLO 是我们对 SLI 设定的目标（例如 99.9% 月可用性）；SLA 是对外承诺/赔偿条款。我会先定 SLI，再给出合理的 SLO，最后再谈是否作为 SLA 对外承诺。

场景 B - 怎么选 SLI 才不踩坑

**面试官：** 给一个 API 服务，你会选什么 SLI？

**我：** 两类：

1. 可用性（事件型）：成功请求数 / 有效请求总数（2xx+部分 3xx），4xx 单独统计不计入后端错误；
2. 延迟（事件型）：p95/p99 小于阈值的占比。
   计数口径在**入口层**统一，避免各服务各算；对重试要去重或按“用户视角”计次，避免“重试风暴把失败率算双份”。

场景 C - SLO 怎么定

**面试官：** 99.9% 与 99.99% 怎么取舍？

**我：** 看业务损失与成本曲线。99.99% 月度只允许 ~4.3 分钟的错误预算，排障/发布窗口都被压缩；若团队值守和自动化不足，99.99% 反而导致频繁告警与回滚。我一般：核心交易 99.95% 或 99.99%，一般读 API 99.9%，并在高峰期/大促临时提升采样与观察窗口。

场景 D - 错误预算怎么“用”

**面试官：** 说说错误预算与发布节奏的关系。

**我：** 错误预算 = 1 − SLO。比如 99.9% 月度预算约 43.2 分钟。如果近期 burn rate 高/预算见底，我会：1 - 冻结非紧急发布；2 - 开启降级策略（缓存/静态页/关闭次要特性）；3 - 聚焦稳定性工作（回放测试、限流、熔断、超时统一）。预算“花得其所”，而不是“花光就挨打”。

场景 E - 告警分级与多窗口燃尽率

**面试官：** 你如何设告警阈值，既能及时又不扰民？

**我：** 用“多窗口 + 燃尽率（burn rate）”组合：

- **快速页警**：1 小时窗口，burn rate≈14（意味着很快把预算烧掉）→ 立刻叫醒人；
- **缓慢工单**：6 小时窗口，burn rate≈6 → 创建工单/白天处理。
  这类“症状导向”的告警（失败率/高延迟）用于**叫醒人**；“原因导向”（CPU、队列、连接池）降一级，只**发工单**。

场景 F - 4xx 飙升算不算“可用性”失败

**面试官：** 如果 4xx 飙升到 10%，算不算 SLO 违约？

**我：** 后端可用性 SLI 通常不把**用户侧错误**（4xx）算入失败，但需要**单独面板**盯住（比如 401/429）。场景允许的话，可把“确实由我们策略导致的 429”记为部分失败，避免通过“把错误算给用户”来掩盖问题。

场景 G - 延迟类 SLO 与指标选择

**面试官：** 只看平均延迟可以吗？

**我：** 不行。要看 p95/p99，并关注**分位数占比**（如“p95<300ms 的请求 >= 99%”）或**尾延迟预算**。发布/灰度期间建议用 exemplars 把 p95 面板直接跳转到对应 trace，把问题定位到具体依赖或 SQL。

场景 H - 预算见底时要做什么

**面试官：** 错误预算剩余 <25% 了，你接下来怎么做？

**我：** 执行预案：1 - 冻结发布；2 - 针对 Top N 故障模式出修复计划与回归用例；3 - 分析告警噪声，优化阈值或聚合；4 - 复盘把“根因→改进→验证”闭环，并视情况调整 SLO（不盲目拔高）。

### 发布与回滚（灰度 / 蓝绿 / 金丝雀）

口袋清单

- 部署与发布解耦：**先部署、后放量、可回滚、开关止血**。
- 金丝雀看“**症状指标**”（错误率/延迟/SLO），双窗口判定，快速窗做页警。
- DB 变更按 **expand→migrate→contract**，回滚只回应用不回破坏性 schema。
- 健康 = readiness（接流量）≠ liveness（存活）；加 **preStop** 保证优雅下线。
- 依赖超时/重试/幂等一致，防“重试放大器”。

高分表达

- “我们用**分阶段放量 + 症状阈值闸门**，触发即关特性或回滚，爆炸半径始终可控。”
- “**数据库遵循 EMC 三段式**，任何时候都能安全退回上一稳定版本。”

场景 A - 选择发布策略

**面试官：** 滚动、蓝绿、金丝雀怎么选？

**我：** 先看“风险 × 影响面”。小改动、无状态服务→滚动；需要极速回滚/零停机→蓝绿（流量一键切回旧环境）；高风险或对尾延迟敏感→金丝雀，按 1%→5%→25%→100%节拍放量，用指标做闸门。受限资源时，滚动最省，蓝绿最贵，金丝雀介于两者之间但需要更强的度量与自动化。

场景 B - 健康检查与优雅下线

**面试官：** 你如何避免刚上线就被流量打爆或“假健康”？

**我：** 三件事：

1. `readinessProbe` 作为“可接流量”的唯一入口，启动时加 `startupProbe` 防止冷启动被判死。
2. **优雅下线**：`preStop` + `terminationGracePeriodSeconds`，让连接池/队列清空。
3. **预热**：新 Pod 先自检、拉缓存、建连接，再放入负载均衡。必要时配短暂**黏性会话**避免状态打散。

场景 C - 自动回滚的阈值与观察窗

**面试官：** 金丝雀回滚用什么阈值更靠谱？

**我：** 我用“**多指标 + 双窗口**”：

- 快窗 5 分钟：失败率 > 2×基线且绝对值 > 1%；p95 延迟 ↑40%；饱和度（队列/连接池）> 80%。
- 慢窗 30 分钟：burn rate 触发（错误预算燃尽率超阈）→回滚或冻结放量。
  只盯症状类指标（错误率、延迟、SLO 违约），资源类（CPU、内存）只做佐证，避免“假阳性”。

场景 D - 数据库变更与可回滚

**面试官：** 上线涉及数据库 schema 怎么做才能可回滚？

**我：** **expand → migrate → contract** 三段式：

1. **expand**：向后兼容的新增（加列/加索引/加可空列），老版本也能用；
2. **migrate**：应用双写/回填/灰度读，验证新路径；
3. **contract**：确认稳定后再删旧列/旧逻辑。
   回滚只回**应用版本**，不回“破坏性 schema”；若必须改非兼容项，先做影子表/影子写，确认正确再切换。

场景 E - 特性开关与“部署≠发布”

**面试官：** 如何降低一次发布的爆炸半径？

**我：** **先部署，后发布**。用**特性开关**控制新功能暴露面：按人群、按比例、按租户逐步打开；遇到问题先“关开关”而不是“滚版本”。关键链路配**熔断/降级开关**，比如关闭推荐、降级为静态页，确保核心交易可用。

场景 F - 兼容性与依赖治理

**面试官：** 多服务协同升级怎么避免“鸡生蛋”问题？

**我：** **向后兼容的接口版本化**（v1/v2 并存），先上游，后下游；或引入**适配层**在网关做协议转换。依赖超时、重试、幂等都要统一：外呼接口先降重试，避免“重试风暴放大故障”。

场景 G - 突发事故：内存泄漏

**面试官：** 新版本导致内存飙升，你怎么处置？

**我：** 立即**停止放量**，把金丝雀权重降到 0，观察 5 分钟；若旧版本一切正常，**回滚**并触发 Runbook：1 - 收集堆快照/指标与异常样本 trace；2 - 评估是否需要限流/降级保护下游；3 - 事后复盘：泄漏点、回归用例、阈值是否需要调整。

场景 H - Kubernetes 细节落地

**面试官：** K8s 下你会启用哪些发布保护？

**我：**

- **RollingUpdate**：`maxUnavailable=0`、`maxSurge=1` 保证容量；关键服务加 **PodDisruptionBudget**。
- **分批金丝雀**：Argo Rollouts / Flagger 配 1%→5%→25%→50%→100% 阶梯与指标闸门。
- **回滚**：`kubectl rollout undo` 或在 CD 系统一键回滚到上个 Stable。
- **配置幂等**：镜像不可变 tag、ConfigMap/Secret 版本化，避免“回滚代码却加载新配置”。

---

## Step 3 - 一页架构速记：可观测与发布策略

```
Client ──traceparent──▶ API Gateway ──▶ Service A ──▶ Service B ──▶ DB/Redis/外部API
   │                         │               │
   │                         │               ├─ Metrics (Prom/OTel Histogram + Exemplars)
   │                         │               └─ Traces (OTel Spans)
   │                         └─ Logs(JSON + MDC trace_id/span_id)
   │
   ├─ Metrics ─▶ ADOT Collector ─▶ AMP/Prometheus ─▶ Grafana (RED/USE, SLO, Burn-rate)
   ├─ Traces  ─▶ Collector Tail/Head Sampling ─▶ Tempo/Jaeger/X-Ray ─▶ Grafana Explore
   ├─ Logs    ─▶ Loki/ES (Pipelines: 结构化/脱敏/采样)
   └─ Alerts  ─▶ 多窗口燃尽率 → On-call（页警）/ 工单（根因类）

CD/发布：Git → CI → Argo Rollouts/Flagger（1%→5%→25%→100%）→ K8s
K8s 关键：startupProbe + readinessProbe；preStop + TerminationGrace；PDB；HPA
DB 变更：Expand → Migrate → Contract（回滚只回应用，不回破坏性 schema）
Feature Flags：部署≠发布；按人群/比例/租户灰度；熔断/降级开关
```

两句高分表达

- “我们用**分阶段放量 + 多窗口燃尽率**做闸门，触发即关开关或回滚，爆炸半径可控。”
- “**EMC 三段式数据库变更**让任何时刻都能 **只回应用** 安全退回上一稳定版本。”

### TraceID 串联排障（4 步到位）

1. **入口统一传播**：W3C `traceparent/tracestate`（OTel SDK/Agent 自动注入）。
2. **日志带上下文**：MDC 固定字段：`trace_id`、`span_id`、`service`、`route`、`err_code`。
   - Logback（建议结构化 JSON）：

     ```
     traceId=%X{trace_id} spanId=%X{span_id} route=%X{route} code=%X{err}
     ```
3. **面板直达 Trace**：关键接口用 Histogram + **Exemplars**，从 `p95` 一键跳到样本 Trace。
4. **Trace→日志**：用 traceId 在日志系统一跳定位异常上下文（请求参数已脱敏）。

> 采样建议：默认 head 5–10%，**错误/高延迟**动态升采；必要时 Collector **tail sampling** 聚焦异常链路。

### 看板读法（RED/USE）

- **RED（服务体验）**：Rate/QPS、Errors（5xx/网关失败）、Duration（p50/p95/p99）。
- **USE（资源健康）**：Utilization（CPU/内存/连接数/线程）、Saturation（排队/GC/磁盘队列）、Errors。
- 先看 RED 判“症状”，再看 USE 找“根因”；下游依赖单独分组面板。

### SLI / SLO / 告警阈值

- **SLI（事件口径统一在入口）**
  - 可用性：`成功数 / 有效请求数`（2xx + 业务允许的 3xx；4xx 单列，不混入后端失败）。
  - 延迟：`p95/p99 < 阈值的占比` 或尾延迟预算。
- **SLO 推荐**
  - 核心交易：99.95%～99.99%；一般读 API：99.9%。
- **多窗口燃尽率（症状类页警）**
  - 快窗 **1h**：Burn rate≈**14** → 立刻页警；
  - 慢窗 **6h**：Burn rate≈**6** → 工单/白天处理。
- **预算管理**：预算见底（<25%）→ 冻结非紧急发布 + 启动降级 + 聚焦稳定性缺陷。

### 发布与回滚（灰度/蓝绿/滚动）

- **策略选择**：小改无状态→滚动；零停机/极速回滚→蓝绿；高风险→**金丝雀**（度量驱动放量）。
- **金丝雀闸门（双窗口）**
  - **5 分钟快窗**：失败率 > **2×基线** 且绝对值 > **1%**；p95 **+40%**；饱和度 > **80%** ⇒ **停放量/回滚**
  - **30 分钟慢窗**：SLO **burn rate** 触发 ⇒ 回滚/冻结
  - 只盯**症状指标**（错误率/延迟/SLO），资源指标只作佐证，减少假阳性。
- **K8s 落地关键**
  - `startupProbe` 防冷启动误杀；`readinessProbe` 作为唯一“接流量”闸门；
  - `preStop` + `terminationGracePeriodSeconds` **优雅下线**；
  - **PDB** 保底副本数；镜像不可变 Tag；ConfigMap/Secret 版本化；`rollout undo` 一键回滚。
- **DB 改动**：**Expand → Migrate → Contract**；影子表/双写/回填；**回滚只回应用版本**。

### Runbook（10 分钟止血）

1. 判断是否 **SLO 症状触发**（而非仅资源波动）。
2. **止血**：限流/熔断；关特性开关；必要时切回旧环境/上个 Stable。
3. **观察窗**：5 分钟快窗 + 30 分钟慢窗复核 RED/关键依赖。
4. **记录**：Trace 样本、慢查询、错误分布；
5. **复盘**：根因→修复项→回归用例→阈值/告警优化（是否降噪/合并）。

### 安全与合规

- 日志**字段级脱敏**（手机号/邮箱/令牌），限制异常栈深度与请求体落盘；
- 临时调试：**按 traceId/路由**动态提级日志 + 事件采样上限，避免扰民与成本爆炸。

---

## Step 4 - 1 分钟英文口语

### 60s Sample — Incident Postmortem (spoken)

**Context & impact.**

At **19:05** during a **5% canary** for the **Checkout API v3**, our **error rate hit 2.4%**—about **2× the baseline**—and **p95 latency** rose **60%**. The **guardrail** halted promotion and routed traffic back to **v2** within minutes.

**Detection.**

We were alerted by a **multi-window burn-rate** page at **\~3 minutes**, and used **exemplars** to jump from the Grafana p95 panel to a slow **trace**.

**Mitigation.**

We **closed the feature flag**, **stopped ramp-up**, enabled a **read-cache TTL bump**, and protected downstreams with **rate-limits**. **Error rate** dropped below **0.2%** and p95 normalized in **6 minutes**.

**Root cause.**

A schema change introduced a **new query pattern** without a **composite index**; it only surfaced under **real traffic mix**.

**Fix & prevention.**

We added the index, **backfilled**, re-ran the canary successfully, and updated the **runbook**: schema follows **expand → migrate → contract**, canary **gates on symptom metrics** (errors/latency/SLO), and CI now checks **query plans** for new access paths.

### Fill-in Template（30–60 秒直读）

- **Context & impact**: “At `time`, during `canary % / rollout type` for `service/version`, `metric` reached `value vs baseline`, **p95** increased `X%`. We `halted/froze/rolled back` to `stable version`.”
- **Detection**: “`Alert type` triggered in `N mins`; we used `exemplars/trace link` from `dashboard` to pinpoint `span/dep`.”
- **Mitigation**: “We `close flag/stop ramp/limit/degade`, `scale or cache tweak`; metrics recovered to `target` in `N mins`.”
- **Root cause**: “`Cause` (e.g., **missing index / timeout mismatch / retry storm**), visible only under `pattern`.”
- **Fix & prevention**: “We `code/db fix`, `re-canary result`; updated **runbook** and added `gate/CI check/playbook`. Schema changes follow **expand → migrate → contract**; canary **gates on symptom metrics**.”

### 3 Sound Bites（面试加分短句）

1. “**We gate promotion on symptom metrics**—error rate and p95—not just CPU.”
2. “Schema changes follow **expand → migrate → contract**, so **rollback only reverts the app**.”
3. “**Multi-window burn-rate** keeps alerts actionable: fast window pages, slow window opens a ticket.”

> 先用上面的 **Sample** 朗读 1 次，再用 **Template** 换成最近一次真实问题/练习中的服务名和指标，各练 1 次，总计不超过 10 分钟。
