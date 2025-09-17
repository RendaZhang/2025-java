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
