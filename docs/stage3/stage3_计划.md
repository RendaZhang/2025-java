<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [第三阶段 后端 & 全栈 Sprint](#%E7%AC%AC%E4%B8%89%E9%98%B6%E6%AE%B5-%E5%90%8E%E7%AB%AF--%E5%85%A8%E6%A0%88-sprint)
  - [时间轴](#%E6%97%B6%E9%97%B4%E8%BD%B4)
  - [Week 7](#week-7)
    - [Day 1 - 开场与 API 设计](#day-1---%E5%BC%80%E5%9C%BA%E4%B8%8E-api-%E8%AE%BE%E8%AE%A1)
    - [Day 2 - 数据库与缓存](#day-2---%E6%95%B0%E6%8D%AE%E5%BA%93%E4%B8%8E%E7%BC%93%E5%AD%98)
    - [Day 3 - 消息与一致性](#day-3---%E6%B6%88%E6%81%AF%E4%B8%8E%E4%B8%80%E8%87%B4%E6%80%A7)
    - [Day 4 - Java 并发](#day-4---java-%E5%B9%B6%E5%8F%91)
    - [Day 5 - 可观测与发布](#day-5---%E5%8F%AF%E8%A7%82%E6%B5%8B%E4%B8%8E%E5%8F%91%E5%B8%83)
    - [Day 6 - Kubernetes/云原生最小面](#day-6---kubernetes%E4%BA%91%E5%8E%9F%E7%94%9F%E6%9C%80%E5%B0%8F%E9%9D%A2)
    - [Day 7 - 全栈](#day-7---%E5%85%A8%E6%A0%88)
    - [Week 7 - 总复盘](#week-7---%E6%80%BB%E5%A4%8D%E7%9B%98)
      - [完成度与产出](#%E5%AE%8C%E6%88%90%E5%BA%A6%E4%B8%8E%E4%BA%A7%E5%87%BA)
      - [能力图谱（本周已覆盖）](#%E8%83%BD%E5%8A%9B%E5%9B%BE%E8%B0%B1%E6%9C%AC%E5%91%A8%E5%B7%B2%E8%A6%86%E7%9B%96)
      - [可直接复用的“面试模板”](#%E5%8F%AF%E7%9B%B4%E6%8E%A5%E5%A4%8D%E7%94%A8%E7%9A%84%E9%9D%A2%E8%AF%95%E6%A8%A1%E6%9D%BF)
      - [本周卡点 & 改进建议（已纳入 SOP）](#%E6%9C%AC%E5%91%A8%E5%8D%A1%E7%82%B9--%E6%94%B9%E8%BF%9B%E5%BB%BA%E8%AE%AE%E5%B7%B2%E7%BA%B3%E5%85%A5-sop)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 第三阶段 后端 & 全栈 Sprint

## 时间轴

> 两周总节奏：
> 每天三件事 = 算法 + 面试能力/知识 + 英语
> 学习即写文档，按需更新 `architecture.md / QBANK.md / elevator_pitch_en.md`。

Week 7 - 准备与能力打底：

- **D1 - 开场与环境就绪**：确定每日时间段与打卡方式；建立/检查面试资料骨架（按需）；自检英文电梯陈述的初稿。
- **D2–D6 - 能力巩固循环**：按“算法 + 面试知识 + 英语”的固定节奏推进；当日要点沉淀到文档；形成追问清单。
- **D7 - 周总结与简历前置清单**：回顾一周卡点；列出简历需要调整的表达与关键词（不写具体数据）；为 Week 8 Day1 做准备。

---

## Week 7

> 每天三件事：**算法（60–90m） + 面试能力/知识（90m） + 英语（30–45m）**，学习即按需更新文档（`architecture.md / QBANK.md / star_stories.md / elevator_pitch_en.md`）。不做重型实操。

### Day 1 - 开场与 API 设计

算法（LeetCode）

- **完成题目**：LC904（滑动窗口）、LC167（双指针）、LC209（滑动窗口）。
- **核心思路**：
  - LC904：保持窗口内≤2种水果；出现第3种时用“最近一段同类起点”或频次 Map 收缩；O(n)/O(1)。
  - LC167：有序数组双指针夹逼；`sum<target` 左移右指针？（更正：左指针++）；`sum>target` 右指针--；命中即返回 1-based 索引。
  - LC209：正整数数组滑窗；`sum>=target` 时尽量收缩更新最短长度。
- **踩坑与修正**：
  - LC904：切第3种前**先**更新答案，再移动 `start=next_start` 并同步 `curr_type/next_start`（已修正）。
  - LC167：把 `sum` 计算放循环体，早返回更清晰（建议更新）。
  - LC209：索引差转长度加 1 的语义明确；也可统一从 `i=0` 开始减少分支。
- **产出**：已撰写 **LC904 高质量复盘**（Pattern / Intuition / Steps / Complexity / Edge Cases / Mistakes & Fix / Clean Code 版本）。

面试知识（API 设计与可靠性 · 7 条）

1. **契约清晰 / 资源建模**：Canonical Model + 语义化 URL + OpenAPI/Schema 校验；统一 ID/金额/时间/分页；错误模型统一、Trace-ID 贯穿。
2. **版本化**：外部用 **URI 大版本**，内部可补 Header 协商；非破坏性演进留在同大版本；破坏性变更切 v2；Deprecation/Sunset + 分版本监控 + 有日程线的下线。
3. **鉴权与授权**：OIDC/JWT；最小权限（scope/aud）；短寿命 Access + **旋转** Refresh + 撤销表；401/403 语义清晰；服务间用短期凭证。
4. **幂等性**：POST 由 `Idempotency-Key` 实现“功能+响应幂等”；写前**原子占位** + 响应快照；回调按事件 ID 去重；设置幂等窗口 TTL。
5. **限流-重试-熔断**：入口令牌桶 + 并发舱壁；**只对幂等**请求做指数退避+抖动（设重试预算）；P95×1.5 级别超时；熔断半开探测；读链路缓存降级、写链路排队。
6. **错误码与可观测**：统一错误体 `code/message/traceId/details`；RED 指标 + 分布式追踪；结构化日志带 MDC（traceId）；对 5xx/高延迟强制采样；日志脱敏。
7. **灰度发布与回滚**：滚动/金丝雀/蓝绿按风险选；金丝雀 1%→5%→25%→50%→100% 守卫阈值；探针与优雅下线；DB 走 expand→migrate→contract；特性开关解耦。

英语（口语素材）

- **完成**：45s Elevator Pitch（云原生 Java 后端、契约与可运维优先、擅长 EKS/Observability）、1min “Why this API design?”（可演进/可观测/可回滚的电商 API 设计逻辑）。
- **练习建议**：两段各再删 10% 口头赘词；控制在 45s / 60s 内；标 3 个强调词，用停顿突出。

今日小结

- **做得好**：题目覆盖“滑窗+双指针”两大高频 Pattern；API 七件套成体系；口语材料成型可直接演练。
- **需要改进**：LeetCode 代码的**口述友好度**（尤其 LC167 可读性细节）；把“幂等 + 重试预算 + 熔断”的参数沉淀成模板。

### Day 2 - 数据库与缓存

算法（LeetCode）

- **完成**：LC141（快慢指针/Floyd）、LC739（单调栈）。
- **要点速记**
  - LC141：`slow=next` / `fast=next.next`，相遇即有环；判空用 `fast!=null && fast.next!=null`。
  - LC739：维护**递减栈（存索引）**；遇更高温度出栈并写入 `ans[j]=i-j`；注意用 `>` 而非 `>=`。
- **复盘**：建议将 LC739 作为“高质量复盘”（为何用索引、`>=` 的反例、O(n) 证明）。

面试知识（数据库 & 缓存 · 6 条体系化要点）

1. **索引选型与失效**：列表查询优先 `(user_id, status, created_at DESC)` 并**覆盖索引**；等值在前、范围在后；避免函数包列与隐式类型；必要时为高频模式**拆专用索引**。
2. **事务与隔离（RC/RR & MVCC）**：读多用 **RC + 一致性读** 提并发；写用**条件更新**（`UPDATE … WHERE qty>=?`）或**当前读 + 明确锁**；唯一约束/插入即占位 > 锁；控制**短事务**并**可重试**死锁。
3. **慢查询定位**：慢日志/RED→`EXPLAIN ANALYZE` 看 `type/key/rows/Extra`；用**组合+覆盖索引**、谓词改写（半开区间/强类型/去函数）、**seek 分页**替代大 OFFSET。
4. **读写分离坑**：**读亲和 + 主读回退 + 延迟感知**；写后关键读用 `read_token`（GTID/位点）保障 **read-after-write**，超时回主；异常延迟时关键读走主、非关键读走缓存。
5. **缓存一致性**：写库→删缓存（必要时**延时双删**/CDC 消息）；读 miss 用**互斥回源** + **逻辑过期** + **TTL 抖动**；缓存值携带 **version/etag** + Lua 原子“新旧值比较”防旧值回灌。
6. **三座大山（穿透/击穿/雪崩）**：
   - 穿透→**空值缓存 + 前置校验 + 布隆**；
   - 击穿→**单飞互斥 + 逻辑过期 + 预热/两级缓存 + 限速重建**；
   - 雪崩→**TTL 打散 + 分级限流/降级 + 渐进放量 + 多级兜底**；监控 `miss burst / db fallback / hotspot TopN`。

英语（口语素材）

- **产出**：1-minute 脚本 **“How I diagnose slow queries & index misses”**（包含 EXPLAIN/覆盖索引/seek 分页/锁等待排查的口语化表达）。
- **练习要点**：强调 “**EXPLAIN ANALYZE** shows real timing”，“**Covering index**”，以及 “**Seek pagination**”。

今日小结

- **做得好**：算法两大高频模式打卡；DB & Cache 六件套形成**可复述框架**；英文口条可直接用于问答。
- **可改进**：把“条件更新 + 幂等 + 重试预算”的数值模板化；为常见列表 SQL 固化 **EXPLAIN 检查清单**。

### Day 3 - 消息与一致性

算法（LeetCode｜树遍历）

- **完成**：LC94（中序·迭代栈）、LC102（层序·BFS），（选做）LC145（后序·双栈）。
- **要点速记**
  - LC94：外层 `while (cur!=null || !st.isEmpty())`；内层一路压左；出栈访问后转右；O(n)/O(h)。
  - LC102：先缓存本层 `sz` 再循环出队，避免动态读 `q.size()`；O(n)/O(w)。
  - LC145：`s1` 出栈进 `s2`，先压左后压右；`s2` 逆序即后序；O(n)/O(n)。
- **复盘**：已完成 1 篇高质量复盘（建议放在 LC102）。

面试知识（消息与一致性 · 体系化 6 条）

1. **Outbox（事务外箱）**：业务写入与事件写入同库同事务落地；Publisher/CDC “至少一次”投递，失败退避重试。
2. **幂等消费 & 去重键**：`eventId` 或 `aggregateId+version`；同库事务里先插 `processed_events` 再执行业务写；写法用**条件更新/UPSERT/版本检查**可重放。
3. **重试策略 & 重试预算**：仅对 `5xx/超时/429` 且**具幂等**的请求重试；**指数退避+抖动**；设置 **≤10% 预算**，熔断打开时停止重试、仅半开探测。
4. **DLQ / 停车场**：重试上限或不可重试错误“停靠”；可筛选/批量回放，回放走慢车道+令牌桶；记录 `traceId/eventId/aggregateId/error_code/attempts` 便于审计。
5. **顺序性与分区键**：以 `aggregateId`（如 `orderId`）为分区键，保证**同聚合有序**、全局不强求；消费者维护 `last_version`，重复丢弃、缺口停靠；热点聚合可拆流/分片。
6. **Exactly-once 的取舍**：端到端 EO 成本高；采用 **Outbox + at-least-once + 幂等消费 + 版本推进 + DLQ 回放** 达到 **effectively-once**；Kafka EOS 仅用于拓扑内、无外部副作用的场景。

英语（口语素材）

- **产出**：≈60s 答案 *“How we guarantee eventual consistency with outbox and idempotent consumers”*（涵盖 Outbox、本地原子性、idempotent writes、version 进位、DLQ/重试预算/分区保序）。
- **练习要点**：强调三句——“**Outbox = one local transaction**”、“**Idempotent consumers with eventId + version**”、“**Effectively-once > end-to-end exactly-once**”。

今日小结

- **做得好**：树遍历三板斧成型；一致性从“写→传→落地→回放”形成闭环表述，可直接面试复述。
- **可改进**：把“重试预算 & 熔断阈值”的**默认参数**沉淀成模板；为 `processed_events/agg_progress` 建表与索引规范清单。

### Day 4 - Java 并发

算法（堆 / Top-K）

- **完成**：LC347（哈希+最小堆）、LC215（快选 / 最小堆），（选做）LC23（小顶堆合并）。
- **要点速记**
  - LC347：`Map<num→freq>` + **size-k 最小堆**，总复杂度 O(n log k)；比较器按**频次**。
  - LC215：快选均摊 O(n)，目标索引 `n-k`；或用 **size-k 最小堆** O(n log k)。
  - LC23：k 路小顶堆，时间 O(N log k)。
- 已写 1 篇“高质量复盘”（建议放 LC347，说明为何选**最小堆**与边界处理）。

并发核心（知识卡片 5 条）

1. **内存模型 & `volatile`**：只保**可见/有序**不保**复合原子**；计数用 `LongAdder/Atomic*`；配置热更新用**不可变对象 + volatile 引用**；DCL 单例需 `volatile`。
2. **`synchronized` vs `ReentrantLock`**：简单短临界区用前者；需要**可中断/定时/多条件/公平**选后者；`tryLock(timeout)` + 重试/降级避免长等待。
3. **`ThreadPoolExecutor` 调参**：**有界队列**=`ArrayBlockingQueue`；按依赖**分池**；`core≈CPU`、`max≈4×CPU`（IO 型）；拒绝策略用 **CallerRuns/Abort** 形成**背压**；严禁无界队列。
4. **`CompletableFuture` 编排**：自定义有界池；子任务 `orTimeout/completeOnTimeout + exceptionally` 明确降级；关键分支失败**取消 siblings**；整体 **deadline**。
5. **并发排障 SOP**：看 `active/max`、`queue_fill`、`rejected`、依赖 P95 → `Thread.print -l`×3 判 I/O/锁/CPU → 当场**超时/降级/背压/熔断** → 修复（有界池、分池、`tryLock`、缩小临界区、重试预算）。

场景题（6 条，口语化备答）

- **#1 线程池背压**：有界队列 + CallerRuns/Abort；外呼强超时；与限流/重试/熔断协同。
- **#2 CF 并行**：fail-fast、可取消、明确降级、1.2s 总超时。
- **#3 锁竞争/死锁**：`Thread.print` 取证；当场 `tryLock(timeout)` + 缩小临界区 + 分段锁；统一加锁顺序根治。
- **#4 饱和 + 重试风暴**：入口令牌桶、有界池背压、**重试预算 ≤10% + 抖动**、熔断半开探测。
- **#5 监控与告警**：`active/max`、`queue_fill`、`rejected`、任务等待/执行 P95；阈值与 Runbook。
- **#6 整合答法**：限流 → 背压 → 超时/取消 → 预算化重试 + 熔断 → 热点锁治理 → 观测 & 值班 SOP（含可落地参数）。

英语（口语素材）

- **产出**：≈60s 脚本 *“Tuning ThreadPoolExecutor for bursts while protecting downstreams”*。
- **强调点**：
  - “**Bounded queue + CallerRuns/Abort = back-pressure**”；
  - “**Timeouts + deadline + cancellable fan-out**”；
  - “**Retry budgets + circuit breaker**”。

### Day 5 - 可观测与发布

- **Step 1 - 算法**：完成 LC703 / LC378 / LC373 / LC295；复盘已做。
- **Step 2 - 面试能力**：三大主题模拟面试一问一答成稿
  1. 日志/指标/追踪（Trace 贯穿、MDC、RED/USE、直方图+exemplars、采样/成本）
  2. SLO/告警/误差预算（事件口径、燃尽率双窗口、4xx口径、预算与发布闸门）
  3. 发布与回滚（滚动/蓝绿/金丝雀、探针与优雅下线、自动回滚阈值、EMC 数据库变更）
- **Step 3 - 英语**：1 分钟 incident postmortem 口语稿 + 填空模板 + 3 句高分表达。
- **Step 4 - 收尾**：3 张“告警场景 → 止血/定位/回滚判定”卡片 + 一页架构速记。

今日关键收获：

1. **“部署≠发布”与风险闸门**：金丝雀按 1%→5%→25% 放量，用**症状指标**（错误率/尾延迟/SLO）闸门，触发即关特性/回滚。
2. **SLO 驱动运维**：用**多窗口燃尽率**（1h 快窗页警、6h 慢窗工单）既及时又不扰民；错误预算直接约束发布节奏。
3. **端到端排障路径**：Grafana p95 → exemplars → Trace → traceId 查结构化日志；RED 判症状、USE 找根因。
4. **数据库变更的 EMC 三段式**：Expand→Migrate→Contract；**回滚只回应用版本**，避免“不可逆 schema”卡死。

> 我们把发布与可观测打通：先**部署**，用特性开关解耦**发布**；金丝雀按 1%→5%→25% 放量，**症状指标闸门**只盯错误率和 p95。Grafana 面板开了 **exemplars**，能从 p95 一键跳到慢 trace，再用 **traceId** 定位结构化日志。SLO 用**多窗口燃尽率**，快窗页警、慢窗工单；当预算吃紧，先**冻结发布**、开启**降级**。数据库遵循 **EMC 三段式**，必要时只回应用版本，保证任何时刻都可安全退回上一稳定版本。

### Day 6 - Kubernetes/云原生最小面

- **Step 1 - 算法（DP）**：
  - 完成 LC322 Coin Change（完全背包/最少枚数）、LC139 Word Break（布尔可达性 + 长度剪枝）、LC221 Maximal Square（二维 DP → 一维滚动）
  - **通用范式**：状态定义 → 转移方程 → 边界条件 → 滚动优化；辅以“样例→反例”自测。
- **Step 2 - 面试能力（6 小步）**：
  1. **Pod/Container**：requests 影响调度、limits 约束执行；延迟敏感应用常用“内存设限、CPU 不限”。
  2. **Service/Ingress/Gateway**：分层治理；502 查协议/探针，504 查超时链路；尽量无状态避免粘性副作用。
  3. **Probes & 优雅下线**：`readiness` 唯一接流量闸门；`SIGTERM→readiness=false→preStop 排空→宽限→SIGKILL`。
  4. **HPA**：指标匹配负载；v2 behavior“快涨慢降”；预热后再接流量，联动队列/并发指标。
  5. **配置与发布安全**：配置即代码，`checksum/config` 触发滚动；镜像用 **digest**；Rolling `0/1` + **PDB** + 规范 drain。
  6. **最小权限（RBAC/OIDC/IRSA）**：权限=爆炸半径控制器；按 **SA/sub/资源前缀/动词** 最小化；无静态密钥、全链路可审计。
- **Step 3 - 英语（60s）**：完成“**Probes + HPA + Least-Privilege**”口述稿与模板；三句加分短句可直接背诵。

今日关键收获：

- **readiness 是唯一接流量闸门**；liveness 只做自愈；发布前先预热。
- HPA 指标**匹配负载**，用 v2 **快涨慢降** + 稳定窗口止抖。
- 配置即代码：**不可变镜像 + checksum/config + revisionHistoryLimit**。
- **最小权限 = 小爆炸半径**：RBAC 到 SA，IRSA 限 `sub`，策略三减（主体/资源/动词）。
- 故障优先“**限流/降级/回滚**”，再扩容；观测链路 **RED→USE→Trace→日志**。

### Day 7 - 全栈

今天完成了什么

- **Step 1｜算法**：
  - LC354（排序 + 严格 LIS，宽相等高降序）、LC309（状态机：hold/sold/rest）、LC72（二维 DP → 一维滚动）。
  - 模板与坑位：严格递增的 `lower_bound`；冷冻期转移顺序；滚动数组 `prev/temp` 的时序。
- **Step 2｜面试能力（7 小步）**：
  1. **React/TS**：Hooks 两规则；State 触发渲染 / Ref 不触发；错误边界用类组件+Sentry。
  2. **路由&表单**：布局壳 + `<Outlet>`；路由层鉴权；大表单=非受控+schema；Query 做服务器状态。
  3. **SSR/CSR/选择性水合**：**用指标选型**（TTFB/LCP/TTI/INP）；Streaming SSR + 岛屿只水合交互区。
  4. **CSP/缓存**：指纹化静态一年 `immutable`；HTML 用 **SWR**；`nonce + strict-dynamic` 最小可用 CSP。
  5. **Sentry**：`release/environment/traceId` 三件套；BrowserTracing 透传到后端；采样与去噪分层。
  6. **环境变量**：**构建期 vs 运行期**；前端公开前缀白名单；`/env.json` 运行期注水；CI/CD 注入。
  7. **architecture.md 新节**：Web Rendering & Caching Strategy（流程图 + 取舍表 + 安全/缓存清单 + 观测串联）。
- **Step 3｜英语（60s）**：完成“**Server for first paint, islands for interaction**”脚本与模板。
- **Step 4｜Week 8 简历清单**：按“四区”输出关键词与句式模板，避免敏感数据。

关键收获

- **选型靠指标**：内容页走 SSR/Streaming 抢 **TTFB/LCP**，混合页用**选择性水合**保 **TTI/INP**。
- **安全与性能并举**：**指纹化 + SWR** 提速，**CSP nonce + strict-dynamic** 护航。
- **观测闭环**：前端 Sentry → 后端 Trace（透传 `sentry-trace/baggage`）→ 日志 `trace_id`，面板一跳定位。
- **配置即代码**：前端**运行期注水**不重建，后端 env/Secret 注入；发布回滚只切模板，资源不可变。

### Week 7 - 总复盘

#### 完成度与产出

- 坚持 **“每天三件事 = 算法 + 面试能力/知识 + 英语”** 的周目标，已覆盖 D1–D7 全部主题：API 设计、DB&缓存、消息一致性、Java 并发、可观测&发布、K8s/云原生、全栈整合。
- 文档体系同步沉淀：`architecture.md / QBANK.md / elevator_pitch_en.md` 按天补充要点与口语答案，形成可复用素材池。 &#x20;

#### 能力图谱（本周已覆盖）

- **API & 可靠性**：契约清晰、URL 大版本、幂等键、限流-重试预算-熔断、统一错误模型与 TraceID。
- **DB & Cache**：组合/覆盖索引、RC/RR 与条件更新、慢查询定位、读写分离读亲和与回主、Cache-Aside 与双删/互斥回源/逻辑过期、三座大山治理。
- **消息一致性**：Outbox + 幂等消费 + DLQ + 分区保序，追求 effectively-once。
- **Java 并发**：线程池调参与背压、CF 编排超时/取消、熔断与降级的协同。
- **可观测与发布**：RED/USE 看板、SLO/误差预算、金丝雀闸门、DB 变更 Expand→Migrate→Contract、Feature Flag 解耦。
- **K8s / 云原生**：探针分工（startup/readiness/liveness）、HPA“快上慢下”、最小权限（RBAC/OIDC/IRSA）。
- **全栈**：SSR/CSR/选择性水合按指标取舍、CSP/缓存分层、Sentry 前后端一跳串联、环境变量管理。

#### 可直接复用的“面试模板”

- 英语素材：45s 自我介绍 + 各主题 1min 答案（API、慢查询、Outbox、一致性、线程池、可观测/发布、K8s、Full-Stack）。
- 架构描述页：`architecture.md` 已包含总体图、可观测与发布、前端集成等章节，满足 3–5 页系统设计对答。
- 高频问答清单：`QBANK.md` 按主题索引到位，便于 Week 8 Mock 前快速检索。

#### 本周卡点 & 改进建议（已纳入 SOP）

- 统一“**重试预算 ≤10%** + 抖动 + 熔断半开探测”的参数模板，避免重试风暴（已在并发/调用治理中固化）。
- 数据库变更/可观测/发布流程，已在 `architecture.md` 的“可观测与发布/Runbook”落地，发布即实验、失败即回滚。
