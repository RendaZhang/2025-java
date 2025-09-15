<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [第三阶段 后端 & 全栈 Sprint](#%E7%AC%AC%E4%B8%89%E9%98%B6%E6%AE%B5-%E5%90%8E%E7%AB%AF--%E5%85%A8%E6%A0%88-sprint)
  - [时间轴](#%E6%97%B6%E9%97%B4%E8%BD%B4)
  - [Week 7 - 每日清单](#week-7---%E6%AF%8F%E6%97%A5%E6%B8%85%E5%8D%95)
    - [Day 1 - 开场与 API 设计](#day-1---%E5%BC%80%E5%9C%BA%E4%B8%8E-api-%E8%AE%BE%E8%AE%A1)
    - [Day 2 - 数据库与缓存](#day-2---%E6%95%B0%E6%8D%AE%E5%BA%93%E4%B8%8E%E7%BC%93%E5%AD%98)
    - [Day 3 - 消息与一致性](#day-3---%E6%B6%88%E6%81%AF%E4%B8%8E%E4%B8%80%E8%87%B4%E6%80%A7)
    - [Day 4 - Java 并发](#day-4---java-%E5%B9%B6%E5%8F%91)
    - [Day 5 - 可观测与发布](#day-5---%E5%8F%AF%E8%A7%82%E6%B5%8B%E4%B8%8E%E5%8F%91%E5%B8%83)
    - [Day 6 - Kubernetes/云原生最小面](#day-6---kubernetes%E4%BA%91%E5%8E%9F%E7%94%9F%E6%9C%80%E5%B0%8F%E9%9D%A2)
    - [Day 7 - 全栈与一周复盘](#day-7---%E5%85%A8%E6%A0%88%E4%B8%8E%E4%B8%80%E5%91%A8%E5%A4%8D%E7%9B%98)
  - [Week 8 - 每日清单](#week-8---%E6%AF%8F%E6%97%A5%E6%B8%85%E5%8D%95)
    - [Day 1 - 简历日（唯一任务）](#day-1---%E7%AE%80%E5%8E%86%E6%97%A5%E5%94%AF%E4%B8%80%E4%BB%BB%E5%8A%A1)
    - [Day 2 - Mock #1 行为面 + 英语沟通](#day-2---mock-1-%E8%A1%8C%E4%B8%BA%E9%9D%A2--%E8%8B%B1%E8%AF%AD%E6%B2%9F%E9%80%9A)
    - [Day 3 - Mock #2 编码/算法沟通](#day-3---mock-2-%E7%BC%96%E7%A0%81%E7%AE%97%E6%B3%95%E6%B2%9F%E9%80%9A)
    - [Day 4 - Mock #3 后端问答（Java/DB/缓存/并发）](#day-4---mock-3-%E5%90%8E%E7%AB%AF%E9%97%AE%E7%AD%94javadb%E7%BC%93%E5%AD%98%E5%B9%B6%E5%8F%91)
    - [Day 5 - Mock #4 系统设计（服务化与伸缩）](#day-5---mock-4-%E7%B3%BB%E7%BB%9F%E8%AE%BE%E8%AE%A1%E6%9C%8D%E5%8A%A1%E5%8C%96%E4%B8%8E%E4%BC%B8%E7%BC%A9)
    - [Day 6 - Mock #5 全栈 & 线上问题排查](#day-6---mock-5-%E5%85%A8%E6%A0%88--%E7%BA%BF%E4%B8%8A%E9%97%AE%E9%A2%98%E6%8E%92%E6%9F%A5)
    - [Day 7 - Mock #6 综合 Panel（终面演练）](#day-7---mock-6-%E7%BB%BC%E5%90%88-panel%E7%BB%88%E9%9D%A2%E6%BC%94%E7%BB%83)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 第三阶段 后端 & 全栈 Sprint

## 时间轴

> 两周总节奏：
> 每天三件事 = 算法 + 面试能力/知识 + 英语
> （学习即写文档，按需更新 `architecture.md / QBANK.md / star_stories.md / elevator_pitch_en.md`）。

Week 7 - 准备与能力打底：

- **D1 - 开场与环境就绪**：确定每日时间段与打卡方式；建立/检查面试资料骨架（按需）；自检英文电梯陈述的初稿。
- **D2–D6 - 能力巩固循环**：按“算法 + 面试知识 + 英语”的固定节奏推进；当日要点沉淀到文档；形成追问清单。
- **D7 - 周总结与简历前置清单**：回顾一周卡点；列出简历需要调整的表达与关键词（不写具体数据）；为 Week 8 Day1 做准备。

Week 8 - 成稿与模拟面试：

- **D1 - 简历日（唯一任务）**：完成中/英一页简历（仅写工作经历；融合后端/全栈与云原生要点；不写具体业务数据）；同步英文电梯陈述。
- **D2–D6 - 模拟面试循环**：每日 1 场 Mock（主题轮换：行为/英语、编码、后端问答、系统设计、全栈与排查）；当日复盘回填材料。
- **D7 - 综合演练**：全流程模拟（代码 + 设计 + 行为 + 英文 Q&A）；输出最终回答模板索引与高频追问清单。

---

## Week 7 - 每日清单

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

- **算法**：堆/优先队列（TopK、合并流）；做 2–3 题。
- **面试能力/知识**：日志/指标/追踪（OTel/Prom/Grafana 基本面）；SLO/告警阈值；发布与回滚（灰度/蓝绿）→ `QBANK.md`；`architecture.md` 补 1 小节。
- **英语**：incident postmortem（1 分钟结构化复盘）。
- **当日收尾**：收集 3 个常见告警场景与处置策略。

### Day 6 - Kubernetes/云原生最小面

- **算法**：动态规划入门（爬楼梯/打家劫舍思路抽象）；做 2–3 题。
- **面试能力/知识**：Pod/Service/Ingress、探针、HPA、Config/Secret、PDB、最小权限（OIDC/IRSA）→ `QBANK.md`。
- **英语**：1 分钟说明 “Why HPA + probes + least‑privilege?”。
- **当日收尾**：把“扩缩容与故障演练”的回答写成 5–7 条 bullet。

### Day 7 - 全栈与一周复盘

- **算法**：周错题复盘 + 计时 1 题（30–45m）。
- **面试能力/知识**：全栈关键点（React/TS、路由与表单、SSR/CSR/选择性水合、CSP/缓存、Sentry、env 管理）→ `QBANK.md`；`architecture.md` 补 1 小节。
- **英语**：解释 “SSR vs CSR vs selective hydration” 的取舍（1 分钟）。
- **当日收尾**：形成“简历日（Week 8 D1）需要改写的表达与关键词”清单（不写具体业务数据）。

---

## Week 8 - 每日清单

> 总原则：**Day 1 只做简历**；Day 2–Day 7 每天一场 **模拟面试（Mock）+ 当日复盘**。全程不做重型实操；以“可复述、能过线”为准。

### Day 1 - 简历日（唯一任务）

- **前置准备**
  - 打开并备份两份最新简历：`20250622_张人大_Java后端开发.docx` 与 `20250622_RendaZhang_JavaBackendDeveloper.docx`（另存为当日副本）。
  - 打开对齐材料：`architecture.md / QBANK.md / star_stories.md / elevator_pitch_en.md`（用于术语与表述一致）。
  - 建立一个当日勾选表（纸质/电子皆可），避免漏项。
- **内容改写（中文→英文同步）**
  1. **最近工作经历（唯一主体）**：改写为 6–8 条职责/方法要点；自然融入后端/云原生/全栈关键词（Java/Spring Boot、微服务、API 设计/鉴权/幂等、消息队列、MySQL/Redis、Docker/Kubernetes、CI/CD、可观测、发布与回滚、React/TypeScript、CSP/Sentry、Nginx/CDN）。**不写具体业务数据**。
  2. **技能与工具**：分三组列示并按岗位重要度排序——后端核心 / 全栈前端 / 云原生与平台工程；避免堆砌，保持每组 1 行内可读。
  3. **证书与教育**：保留并前置与岗位最相关项（如 AWS SAA-C03）。
  4. **结构取舍**：删除独立“项目经历”段，仅保留工作经历；英文版与中文版结构保持一致。
- **术语与关键词（ATS 对齐）**
  - Backend: Java 17+, Spring Boot 3, Microservices, API design, Auth, Idempotency, Rate limiting, Messaging (Kafka/SQS), MySQL/Aurora, Redis.
  - Cloud/Platform: Docker, Kubernetes, CI/CD, Observability (OTel/Prometheus/Grafana), SLO, Canary/Blue-Green, Cloud (AWS/AliCloud/Tencent), OIDC/IRSA, IaC (Terraform/CDK).
  - Full‑stack: React, TypeScript, Routing/Forms, SSR/CSR/Selective hydration, Performance, Sentry, CSP, Nginx/CDN, BFF.
- **版式与导出**
  - 一页限制；10–11pt 正文字号；行距 1.15–1.2；左右边距适中；Bullet 统一“• ”或“- ”；中英文标点统一；日期格式统一（YYYY.MM–）。
  - 导出 CN/EN 两份 PDF 与源文件（DOCX）；
    - 文件名规范：`RendaZhang_Resume_CN_2025W8D1.pdf` / `RendaZhang_Resume_EN_2025W8D1.pdf`。
- **自检清单：三读 + 两一致 + 一演练**
  1. 三读：事实/语法/版式。
  2. 两一致：与 `architecture.md / QBANK.md / star_stories.md` 的术语与说法一致；中英两版相互一致。
  3. 一演练：对照简历完成 45 秒英文电梯陈述 与 90 秒中文版本；各录音 1 次快速回听。
- **提交与备份**
  - 将 PDF 与源文件放入 `docs/resume/`（或你的既有路径），Git 提交并 Push；同步一份到网盘/云盘，生成可分享链接。

### Day 2 - Mock #1 行为面 + 英语沟通

- **热身**：电梯自我介绍 2 次；STAR 回顾（3 个）。
- **模拟**：30’ 行为面（团队协作/冲突/压力/学习）、15’ 英语追问（Why this design / Incident）。
- **复盘**：记录追问与卡点 → 精修 `star_stories.md`；补充英文过渡句与常用短语。
- **收尾**：把“行为面高频问”整理成 7–10 条要点清单。

### Day 3 - Mock #2 编码/算法沟通

- **热身**：10’ 模板回顾（双指针/滑窗/树/堆/基础 DP）。
- **模拟**：45–60’ 代码题（边思路边实现；口述复杂度与边界）。
- **复盘**：错因分类（读题/边界/实现/复杂度）；把可复用模板记入 `QBANK.md`。
- **收尾**：准备 1 段“如何优化/重构该题”的口述版。

### Day 4 - Mock #3 后端问答（Java/DB/缓存/并发）

- **热身**：3 分钟口播“数据库索引与缓存一致性”的要点。
- **模拟**：50’ 后端问答 + 10’ 追问（权衡与落地）。
- **复盘**：把不会/不稳的问题写成 5–7 条要点 → `QBANK.md`。
- **收尾**：为 2 个薄弱点准备“反问与澄清”句式（中/英）。

### Day 5 - Mock #4 系统设计（服务化与伸缩）

- **热身**：5’ 需求澄清清单 + 5’ 容量/一致性/观测三角形。
- **模拟**：60’ 设计题（需求→边界→数据/一致性→伸缩/缓存/队列→观测/发布/成本）。
- **复盘**：把白板方案整理入 `architecture.md`；列出 3 个可替代的权衡点。
- **收尾**：准备 1 分钟“如何做发布回滚与告警阈值”的口述。

### Day 6 - Mock #5 全栈 & 线上问题排查

- **热身**：前端渲染策略与安全（SSR/CSR/选择性水合、CSP/缓存、Sentry）。
- **模拟**：30’ 全栈问答（BFF/鉴权/联调）、20’ 线上问题排查（网络/浏览器/后端日志三向定位）。
- **复盘**：补 `QBANK.md` 与 `architecture.md` 要点。
- **收尾**：把“CORS/CSP/鉴权常见坑”写成 1 页卡片（≤12 条）。

### Day 7 - Mock #6 综合 Panel（终面演练）

- **热身**：电梯自述 + 设计题框架 + 行为面 STAR 快速回顾。
- **模拟**：30’ 代码 + 30’ 设计 + 20’ 行为 + 10’ 英文 Q\&A（全链路）。
- **复盘**：形成最终“回答模板索引”与“高频追问清单”；确认投递材料与面试当日 checklist。
- **收尾**：冻结材料版本（简历/一页架构图/要点清单），准备面试周节奏。
