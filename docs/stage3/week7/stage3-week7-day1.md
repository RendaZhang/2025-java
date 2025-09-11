<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 1 - 开场与 API 设计（契约与可靠性打底）](#day-1---%E5%BC%80%E5%9C%BA%E4%B8%8E-api-%E8%AE%BE%E8%AE%A1%E5%A5%91%E7%BA%A6%E4%B8%8E%E5%8F%AF%E9%9D%A0%E6%80%A7%E6%89%93%E5%BA%95)
  - [目标](#%E7%9B%AE%E6%A0%87)
    - [步骤清单](#%E6%AD%A5%E9%AA%A4%E6%B8%85%E5%8D%95)
  - [第 1 步：开场与环境就绪](#%E7%AC%AC-1-%E6%AD%A5%E5%BC%80%E5%9C%BA%E4%B8%8E%E7%8E%AF%E5%A2%83%E5%B0%B1%E7%BB%AA)
    - [创建 `docs/interview/QBANK.md`：](#%E5%88%9B%E5%BB%BA-docsinterviewqbankmd)
    - [创建 `docs/interview/elevator_pitch_en.md`](#%E5%88%9B%E5%BB%BA-docsinterviewelevator_pitch_enmd)
  - [第 2 步：LeetCode 算法训练](#%E7%AC%AC-2-%E6%AD%A5leetcode-%E7%AE%97%E6%B3%95%E8%AE%AD%E7%BB%83)
    - [LC 904. Fruit Into Baskets（中等，Sliding Window + HashMap）](#lc-904-fruit-into-baskets%E4%B8%AD%E7%AD%89sliding-window--hashmap)
    - [LC 167. Two Sum II – Input Array Is Sorted（简单，Two Pointers）](#lc-167-two-sum-ii--input-array-is-sorted%E7%AE%80%E5%8D%95two-pointers)
    - [LC 209. Minimum Size Subarray Sum（中等，Sliding Window）](#lc-209-minimum-size-subarray-sum%E4%B8%AD%E7%AD%89sliding-window)
    - [复盘 LC 904](#%E5%A4%8D%E7%9B%98-lc-904)
  - [第 3 步：API 设计与可靠性要点](#%E7%AC%AC-3-%E6%AD%A5api-%E8%AE%BE%E8%AE%A1%E4%B8%8E%E5%8F%AF%E9%9D%A0%E6%80%A7%E8%A6%81%E7%82%B9)
    - [契约清晰：资源建模 和 语义化接口（Contract Clarity）](#%E5%A5%91%E7%BA%A6%E6%B8%85%E6%99%B0%E8%B5%84%E6%BA%90%E5%BB%BA%E6%A8%A1-%E5%92%8C-%E8%AF%AD%E4%B9%89%E5%8C%96%E6%8E%A5%E5%8F%A3contract-clarity)
    - [版本化策略：URI vs Header；向后兼容与下线流程](#%E7%89%88%E6%9C%AC%E5%8C%96%E7%AD%96%E7%95%A5uri-vs-header%E5%90%91%E5%90%8E%E5%85%BC%E5%AE%B9%E4%B8%8E%E4%B8%8B%E7%BA%BF%E6%B5%81%E7%A8%8B)
    - [鉴权与授权：JWT/OIDC、最小权限、Token 续期与旋转](#%E9%89%B4%E6%9D%83%E4%B8%8E%E6%8E%88%E6%9D%83jwtoidc%E6%9C%80%E5%B0%8F%E6%9D%83%E9%99%90token-%E7%BB%AD%E6%9C%9F%E4%B8%8E%E6%97%8B%E8%BD%AC)
    - [幂等性：幂等键、PUT vs POST、重试安全](#%E5%B9%82%E7%AD%89%E6%80%A7%E5%B9%82%E7%AD%89%E9%94%AEput-vs-post%E9%87%8D%E8%AF%95%E5%AE%89%E5%85%A8)
    - [限流-重试-熔断：客户端与服务端协同；退避策略；降级与快速失败](#%E9%99%90%E6%B5%81-%E9%87%8D%E8%AF%95-%E7%86%94%E6%96%AD%E5%AE%A2%E6%88%B7%E7%AB%AF%E4%B8%8E%E6%9C%8D%E5%8A%A1%E7%AB%AF%E5%8D%8F%E5%90%8C%E9%80%80%E9%81%BF%E7%AD%96%E7%95%A5%E9%99%8D%E7%BA%A7%E4%B8%8E%E5%BF%AB%E9%80%9F%E5%A4%B1%E8%B4%A5)
    - [错误码与可观察性：统一错误模型 / Trace-ID / 指标-日志-链路关联](#%E9%94%99%E8%AF%AF%E7%A0%81%E4%B8%8E%E5%8F%AF%E8%A7%82%E5%AF%9F%E6%80%A7%E7%BB%9F%E4%B8%80%E9%94%99%E8%AF%AF%E6%A8%A1%E5%9E%8B--trace-id--%E6%8C%87%E6%A0%87-%E6%97%A5%E5%BF%97-%E9%93%BE%E8%B7%AF%E5%85%B3%E8%81%94)
    - [灰度发布与回滚：逐步放量 / 健康检查 / 一键回滚](#%E7%81%B0%E5%BA%A6%E5%8F%91%E5%B8%83%E4%B8%8E%E5%9B%9E%E6%BB%9A%E9%80%90%E6%AD%A5%E6%94%BE%E9%87%8F--%E5%81%A5%E5%BA%B7%E6%A3%80%E6%9F%A5--%E4%B8%80%E9%94%AE%E5%9B%9E%E6%BB%9A)
  - [第 4 步：英语口语素材](#%E7%AC%AC-4-%E6%AD%A5%E8%8B%B1%E8%AF%AD%E5%8F%A3%E8%AF%AD%E7%B4%A0%E6%9D%90)
    - [45s Elevator Pitch（口语版）](#45s-elevator-pitch%E5%8F%A3%E8%AF%AD%E7%89%88)
    - [1-min Answer - “Why this API design?”](#1-min-answer---why-this-api-design)
    - [练习与落地（两分钟内搞定）](#%E7%BB%83%E4%B9%A0%E4%B8%8E%E8%90%BD%E5%9C%B0%E4%B8%A4%E5%88%86%E9%92%9F%E5%86%85%E6%90%9E%E5%AE%9A)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 1 - 开场与 API 设计（契约与可靠性打底）

## 目标

1. **算法**：完成数组/哈希方向 2–3 题（含 1 题高质量复盘：思路→复杂度→边界→错因）。
2. **面试知识**：在 `QBANK.md` 新增 **≤ 7 条**「API 设计与可靠性」要点（契约/版本化/鉴权/幂等/限流-重试-熔断/错误码规范）。
3. **英语**：写出 45 秒英文自我介绍 v1 + 1 分钟回答 “Why this API design?” v1。

### 步骤清单

1. **开场与环境就绪（10–15m）**：确认今日时间块与产出文件清单。
2. **算法训练（60–90m）**：数组/哈希→滑动窗口或双指针 2–3 题 + 1 题复盘记录。
3. **API 设计与可靠性要点（60–90m）**：整理 ≤7 条面试要点写入 `QBANK.md`。
4. **英语口语素材（30–45m）**：电梯自我介绍 v1 + “Why this API design?” v1。
5. **收尾（10–15m）**：列本周目标/卡点，做一次小结

> 交付物：`QBANK.md`（新增条目）、`elevator_pitch_en.md`（v1）。

---

## 第 1 步：开场与环境就绪

在 `docs/interview/` 下创建 2 个文件。

### 创建 `docs/interview/QBANK.md`：

```markdown
# 高频问题库

- **Java**：集合/并发（synchronized、Lock、CAS、线程池）、JVM（内存结构、GC）、异常与最佳实践
- **Spring**：IOC/AOP、RestController、Actuator、配置管理、事务/连接池
- **微服务 & K8s**：Deployment/Service/Ingress/HPA/探针、无状态、滚动发布与回滚、ConfigMap/Secret
- **AWS & 云原生**：EKS NodeGroup vs Fargate、ALB、IRSA/OIDC、ECR、S3、CloudWatch、AMP、Grafana
- **DevOps**：CI/CD（GitHub Actions OIDC）、Trivy、回滚策略、IaC（Terraform 后端与锁）、最小权限
- **SRE**：SLI/SLO/错误预算、MTTR、Chaos、容量与成本权衡
- **行为/英文**：冲突处理、推动落地、失败复盘、跨团队协作、影响力（每题准备一条 STAR）

## API Design & Reliability — W7D1（≤ 7 bullets）

- [ ] 契约清晰（资源建模/语义化 URL/请求-响应结构）
- [ ] 版本化策略（URI vs Header；向后兼容）
- [ ] 鉴权与授权（JWT/OIDC；最小权限；Token 续期）
- [ ] 幂等性（幂等键/PUT vs POST/重试安全）
- [ ] 限流-重试-熔断（客户端与服务端配合；退避策略）
- [ ] 错误码与可观察性（统一错误模型/Trace-ID/指标）
- [ ] 灰度发布与回滚（逐步放量/健康检查/回滚开关）
```

### 创建 `docs/interview/elevator_pitch_en.md`

```markdown
# Elevator Pitch (v1) — 45s

Hi, I’m Renda Zhang, a Java backend developer focused on cloud-native microservices...
- Core strengths (3 bullets): <!-- 技术亮点/项目亮点 -->
- Recent focus: <!-- 与岗位画像对齐的一点 -->
- What I bring: <!-- 与业务价值挂钩 -->

## 1-min Answer: “Why this API design?”

- Context: <!-- 业务背景/非功能性约束 -->
- Key choices: versioning, idempotency, rate-limit/retry/circuit, error model
- Trade-offs: <!-- 延迟、成本、演进性之间的权衡 -->
- Outcome: <!-- 可观测性/稳定性/迭代效率带来的影响 -->
```

---

## 第 2 步：LeetCode 算法训练

目标：

完成 2 题（滑动窗口 + 双指针），可选 1 题挑战；并对其中 1 题做高质量复盘。

> 小提示：如果某题卡住 >10 分钟再看提示；否则先独立思考。

### LC 904. Fruit Into Baskets（中等，Sliding Window + HashMap）

- 思路提示：维护一个“最多包含两种元素”的窗口；用 `count[type]` 记录数量；当种类数 > 2 时左指针缩小直到回到 ≤ 2；每轮更新最大窗口长度。
- 关键不变量：窗口内**水果种类 ≤ 2**。
- 常见坑：缩窗时记得把 `count[left]` 递减到 0 再删除键；更新答案的位置要在扩窗后每轮都尝试。
- 自测用例：
  - `[1,2,1] -> 3`
  - `[0,1,2,2] -> 3`
  - `[1,2,3,2,2] -> 4`

### LC 167. Two Sum II – Input Array Is Sorted（简单，Two Pointers）

- 思路提示：有序数组；左右指针夹逼。`sum < target` 左指针右移；`sum > target` 右指针左移；相等返回（注意题目通常要求 1-based 索引）。
- 常见坑：越界与死循环；别用哈希表（本题更看重双指针）。
- 自测用例：`numbers=[2,7,11,15], target=9 -> [1,2]`

### LC 209. Minimum Size Subarray Sum（中等，Sliding Window）

- 思路提示：正整数数组；右指针扩张累加，`sum >= target` 时左指针尽可能收缩并更新最短长度。
- 自测用例：`target=7, nums=[2,3,1,2,4,3] -> 2`

### 复盘 LC 904

Pattern：

Sliding Window（保持窗口内**至多两种**水果类型）。

Intuition：

题目等价于：在数组中找到“最多包含两种不同元素”的最长连续子数组长度。自然想到用右指针扩张窗口、当种类数 > 2 时用左指针收缩，直到种类数 ≤ 2 为止，同时记录历史最大长度。

Steps：

1. 用一个结构维护窗口内“每种水果的出现次数”（可用 `Map<Integer,Integer>`）。
2. 右指针 `r` 逐步右移、计数 +1；若窗口的键数 > 2，则移动左指针 `l`，将 `fruits[l]` 计数 -1，减到 0 则从 `Map` 删除该键，直到键数 ≤ 2。
3. 每次扩张或收缩后，以 `r - l + 1` 更新答案。

> 你的实现等价思路：不显式维护计数，而是用 `next_start` 记录“最近一段连续的最新水果开始位置”。当出现第 3 种时，直接把 `l` 跳到 `next_start`，并重置集合为“上一种 + 当前新种”，从而 O(1) 地完成“批量收缩”。

Complexity：

- Time：O(n)，每个元素最多进出窗口一次。
- Space：O(1)，窗口内最多两种类型（`Map`/`Set` 常数级）。

Edge Cases：

- 全相同元素（如 `[1,1,1,1]`）→ 直接返回数组长度。
- 只有两种元素交替（如 `[1,2,1,2,1,2]`）→ 返回数组长度。
- 频繁切换第三种（如 `[1,2,3,2,2]`）→ 注意在出现第 3 种时的收缩与“最新连续段起点”的更新。
- 极短数组（长度 0/1/2）→ 边界直接返回长度（按题目约束通常 ≥1）。

Mistakes & Fix：

- **坑点**：在检测到第 3 种水果时，如果没有**先**用历史窗口长度更新答案、**再**正确设置 `start = next_start` 与同步 `curr_type/next_start`，容易丢失最佳解或出现 off-by-one。
- **修正**：先 `result = max(result, curr_max)`，然后用 `curr_max = curr_max - (next_start - start) + 1`（等价于 `i - next_start + 1`）重置窗口长度，并把 `start` 跳到 `next_start`，最后更新 `curr_type = fruits[i]` 与 `next_start = i`。

Clean Code（面试友好版，Java）：

```java
public int totalFruit(int[] fruits) {
    int n = fruits.length, l = 0, ans = 0;
    Map<Integer, Integer> freq = new HashMap<>(4);
    for (int r = 0; r < n; r++) {
        freq.put(fruits[r], freq.getOrDefault(fruits[r], 0) + 1);
        while (freq.size() > 2) {
            int x = fruits[l++];
            int c = freq.get(x) - 1;
            if (c == 0) freq.remove(x);
            else freq.put(x, c);
        }
        ans = Math.max(ans, r - l + 1);
    }
    return ans;
}
```

> 可选变体（纯 O(1) 状态、无 Map）：维护 `last`, `secondLast`, `lastCount`, `currMax` 四个量，遇到第三种时把 `currMax` 设为 `lastCount + 1` 并重置“最近两种”的身份——口述简单，但写码时容错较低。

---

## 第 3 步：API 设计与可靠性要点

### 契约清晰：资源建模 和 语义化接口（Contract Clarity）

> **契约清晰（资源建模/语义化 URL/统一字段语义/错误模型/可观测 Trace-ID）**：对外坚持 Canonical Model + OpenAPI/JSON Schema 校验，分页/排序/时间/金额/ID 统一约定，返回体可观测可排障；渠道差异放在内部映射层，外部只做向后兼容的增量字段。

> 备注：**版本化**、**幂等**、**限流/重试/熔断**会在后面的 2–7 条分别展开。

**面试官：**
“你在跨境电商/库存中台里，如何把**商品/库存**这类核心对象建模，并通过**清晰稳定的 API 契约**让前端、第三方渠道（Shopify/WooCommerce 等）都能稳用？如果上游平台字段各不相同、且业务量在活动期飙升，你会怎么设计接口与返回结构？”（你可以结合你在深圳市凡新科技 & Michaels 的经历来回答）

**你：**

“我会先做一个**Canonical Model（规范化域模型）**，然后把各平台的字段映射进来，API 对外只暴露**我们的一致语义**。例如把 `Product`、`Variant`、`StockItem` 拆清，`/products/{id}`、`/variants/{id}`、`/stocks?variantId=...&channel=...` 用**语义化 URL**和**查询参数**表达资源与过滤。之所以坚持对外契约稳定，是因为我们的服务在活动期会到 **80k–150k req/day，峰值 \~1.2k QPS**，而且要保持 P95 < 140ms，所以**任何破坏性变更都会造成放大效应**。这在我现在的工作环境里是常态（AWS 上 6 个 Spring Boot 微服务 + 自动扩容），因此我会把契约做成**可文档化、可校验**的，比如 OpenAPI + JSON Schema，前后端都能对齐检查。”

“以**库存**为例，我会规定：

- **ID 与类型**：所有 ID 统一用字符串（避免某些平台 `variant_id` 的长整型在 JS 客户端精度丢失）；金额统一**分为最小货币单位**（如分）、**货币代码**单独字段；时间全用 **RFC3339 UTC**。
- **分页与排序**：`page/size/sort` 统一格式；对大列表返回 `nextCursor` 以便前端/任务稳定翻页。
- **并发读写**：读 API 返回 `ETag/Last-Modified`，写 API 支持 `If-Match` 做并发控制；结合缓存（我们线上用 **Redis Cluster + Aurora 只读副本** 做读写分离），读路径可控、延迟稳定。”

“在 **Michaels** 做电商与 MakerPlace 的 API 时，我们也坚持把**登录与用户域**契约化，比如 **JWT 轮换 + OAuth2** 统一安全语义，接口文档清晰，移动端/前端对接成本低；同时在性能上通过**索引与响应结构优化**把接口延迟打下来，证明**契约清晰**有助于定位与优化。 ”

“错误返回我会统一一个**错误模型**：

```json
{ "code": "STOCK_NOT_FOUND", "message": "Stock item not found", "requestId": "trace-id", "details": { "variantId": "v123", "channel": "shopify" } }
```

配合**Trace-ID** 贯通到 CloudWatch / X-Ray / Grafana，这对我们线上**快速定位**很关键（我们有完善的可观测和零停机发布流程）。”

**面试官：**“上游新增了一个渠道特有字段，比如 `shopify_location_id`，但你不想污染对外契约，怎么处理？”

**你：**

“我不会把渠道细节渗透进公开模型，而是：

1. **内部映射层**吸收它（Connector DTO）；
2. 对外契约只在**业务确实需要**且跨渠道有共同语义时才**增量添加**字段（只做向后兼容的**可选字段**）；
3. 对必须透传的极少数字段，用 `extensions.*` 命名空间承载，并在 OpenAPI 标注**非核心**。这样不破坏现有调用方，也避免**破坏性变更**在高峰期放大。”（与我们在活动期高 QPS 的稳定性目标一致。）

**面试官：**“你在凡新或 Michaels 有没有因为契约不清导致事故？后来怎么改的？”

**你：**

“有一次库存批量同步的响应里，**金额字段单位**没写清，导致一个下游任务把分当元，差点误触发大额补货。后来我们把**金额强制最小单位 + 货币代码**写进 Schema，并在 CI 里做**契约校验**与**示例响应校验**；同时在库存批同步流程里也加了**幂等键与步骤化编排**（我们用 **Lambda + SQS + Step Functions** 重构这条链路，整体耗时也从 ~25min 降到 ~7min）。”

### 版本化策略：URI vs Header；向后兼容与下线流程

> **版本化（URI 大版本 + Header 可选；向后兼容优先）**：非破坏性演进留在同大版本，破坏性才切 v2；提供兼容层与双写验证；Deprecation/Sunset 通知 + 分版本监控 + 强制下线日程，做到“可见、可控、可回滚”。

**面试官：**
“你在（深圳市凡新科技/麦克尔斯深圳）做商品与库存接口时，有一次业务要从‘单仓数量’升级到‘多仓分布库存’。这对返回结构是**破坏性变更**：原来 `stockQuantity` 是一个整数，现在要返回 `warehouses[]` 明细。你会怎么做**版本化**？是放在 URL 里 `/v2/stocks`，还是用 `Accept: application/vnd.xxx+json;v=2` 的 Header？旧客户端还在跑，你怎么做到**平滑迁移**和**按期下线**？”

**你：**

“对**外部/多团队依赖**的 API，我优先选 **URI 大版本**（`/api/v1/...` → `/api/v2/...`），因为它**可见性强**、文档和路由隔离清晰，前端/第三方也最好理解。对于**内部 BFF 或同域微服务**之间，我会保留 **Header 版本**（`Accept: ...;v=2`），用 Spring 的内容协商把同一条路径映射到不同的 `produces`。
落地上我会遵循这几条：

1. **非破坏性演进**（新增可选字段、增加新接口）只在**同大版本**里做，比如在 v1 返回里增加 `warehouses`（可选），同时保留 `stockQuantity`；
2. **破坏性变更**（字段语义变化、枚举收缩等）才启 **v2** 路径；
3. **双写/影子读** 验证：服务内部先把多仓逻辑双写到新表/新索引，线上对 v2 做**小流量灰度**，对比指标与告警；
4. **治理与下线**：对 v1 返回 **Deprecation/Sunset** 头（例如 `Sunset: <日期>`），在 API 网关或 Ingress/Grafana 里**按版本打点**，当 v1 调用量 < X% 持续 Y 天，就发最后通知并**切 410**；
5. 期间提供一个**兼容层**：v2 服务对老客户端仍可回填 `stockQuantity = sum(warehouses[].qty)`，让迁移有缓冲期。
   在凡新那边做促销高峰时，这种‘大版本在 URL，小迭代在 Schema’的策略更稳；在麦克尔斯那边，移动端同学更喜欢**明确的路径版本**，他们升级 App 时能直观看到 v2。整体目标是让‘**破坏性只发生在大版本切换**’，其它都是**增量可兼容**。”

**面试官：**“如果大量老客户端一时半会升级不了，导致你迟迟不能下线 v1 怎么办？”

**你：**

“我们会把**兼容层**做成**可配置的**：

- 先在 v2 内部保留一层**适配器**把 `warehouses` 聚合成 `stockQuantity` 返回给 v1 客户；
- 在 API 网关对 v1/v2 的**QPS、错误率、延迟**做**分版本监控**，并在每次版本公告后给出**采纳率**；
- 设一个明确的**日程线**：例如 90 天后进入‘降级窗口’，老版本只做**安全修复**不加新特性；180 天后**强制下线**（返回 410 + 链接到迁移文档）。
  这样我们既不拖累新版本的演进，也给合作方足够时间。”

**面试官：**“Spring Boot 里你怎么同时支持 URI 版本和 Header 版本？”

**你：**

“实际做法是**对外统一用 URI 大版本**，对内需要时再开 Header 协商：

- 控制器层：`/api/v1/...` 与 `/api/v2/...` 各有路由；
- 若同一路径用 Header：在 `@RequestMapping` 的 `produces` 里声明 `application/vnd.renda.stock+json;v=1/2`，并配置 `ContentNegotiationStrategy`；
- OpenAPI 文档分**两个 group**（v1/v2）生成 swagger，CI 里对两套 **JSON Schema** 做**契约校验**与**向后兼容检查**（新增字段只能是可选、禁止删除/改义）。
  配合灰度和回滚开关，风险可控。”

### 鉴权与授权：JWT/OIDC、最小权限、Token 续期与旋转

> **鉴权与授权（JWT/OIDC，最小权限，续期与旋转）**：统一 OIDC，短寿命 Access + 旋转 Refresh + 撤销表；按 `scope/aud` 做最小权限；服务间用客户端凭证/临时凭证；区分 401/403 并可观测（traceId/指标/告警）。

**面试官：**
“你在（深圳市凡新科技 / 麦克尔斯深圳）做订单与库存 API 时，前端（Web/小程序/APP）和三方渠道都要访问。你怎么做**统一登录与鉴权**？具体到 **JWT/OIDC** 的落地细节、**最小权限**的授权设计、以及**Access/Refresh** 的**续期与旋转**，你怎么权衡安全与可用性？”

**你：**

“我们把**身份认证**统一到 OIDC（例如 IDP：Cognito/Keycloak/公司自建），客户端用 **Auth Code + PKCE** 获取 **短时 Access Token（JWT）** 和 **较长 Refresh Token**。服务端（Spring Boot）作为 **OAuth2 Resource Server** 校验 JWT 的签名与过期，走 **JWKs** 自动拉取公钥并做**缓存**。
**授权**层面，我坚持**最小权限**：

- 面向外部调用，用 **scope** 粒度（`product:read`、`stock:write`），避免一刀切的 `admin`；
- 面向内部微服务，采用 **audience（aud）** 与 **资源级/操作级**组合（比如只能改“库存”但不能改“价格”），把权限做成**可配置策略**（如基于角色/属性的 ABAC）。
  **续期**我用‘短 Access + 可旋转 Refresh’：Access 约 5–15 分钟，Refresh 7–30 天，**刷新时旋转**（旧 Refresh 立即失效），并把 **jti（令牌唯一 ID）** 写进**黑名单/撤销表**（Redis/DB），防止被盗用。
  我们线上有**并发与多设备**，所以刷新接口设计成**幂等**，只承认‘最新签发’的 Refresh；如果同一 Refresh 被重复使用，我会触发**全账户 Refresh 封禁**并发告警。
  对**服务间调用**，我们禁用‘人为生成的长寿命 Token’，而走**客户端凭证流**或云原生临时凭证（比如 IRSA 访问云资源），降低泄漏风险。
  最后，把**401/403** 语义分清（未认证 vs 已认证但无权限），错误体里带 **traceId**，利于排障。”

**面试官：**“如果 JWT 泄露，或者我们要**强制登出**某个用户，怎么让**本来‘自包含’不可撤销**的 JWT 立即失效？”

**你：**

“我们有两层方案：

1. **短 TTL 的 Access** + **Gateway 层的撤销列表**：把需要立刻失效的 `jti` 放到 Redis，API Gateway 或全局过滤器先查撤销表，命中就拒绝；
2. **旋转 Refresh** + **一次性使用**：刷新时颁发新的 Refresh，并把旧的标记为‘已消费’，如果旧的再次出现就判定可疑并封禁。
   这两层能把‘自包含 Token 不可撤销’的问题控制在可接受窗口内（几分钟级）。此外我们开启 **kid（key id）轮换**，密钥换代时能平滑过渡。”

**面试官：**“如何观察和演练这个体系？”

**你：**

“指标我们会分三类：

- **认证失败率**（签名/过期/撤销命中）、**刷新成功率**、**刷新重放**告警；
- **授权拒绝率**（403）按 `scope`/`aud` 分维度；
- **JWKs 拉取与缓存命中率**、**IDP 延迟**。
  我们有**失效演练**（把某用户/某应用加入撤销表；把某把密钥下线），确认 401 生效、刷新被拒并且告警到位。”

**面试官：**“能结合你在凡新/麦克尔斯的经验说个具体例子吗？”

**你：**

“促销高峰时，**库存写入**要打得很紧，我们把 `stock:write` 单独成 scope，并给三方渠道一个**只读**的 `stock:read`。有次某脚本用错了客户端凭据，触发了**403**，我们通过错误体里的 `traceId` 很快定位到是**权限维度**不对，不是程序 Bug。
另外一次移动端升级后，出现**Refresh 重放**，我们通过**旋转 + jti 撤销**挡住了重放，并把这一模式加入**风控告警**。这些属于真实环境里‘安全与可用’的平衡：尽量短的 Access + 自动刷新，搭配清晰的权限边界。”

### 幂等性：幂等键、PUT vs POST、重试安全

> **幂等性（POST 幂等键、PUT/DELETE 自幂等、回调按事件 ID 去重）**：服务端原子占位（Redis SETNX 或 DB 唯一键）+ 响应快照复用；设置幂等窗口 TTL；与**指数退避**配合避免重试风暴；异步链路用 **Outbox + 消费端幂等** 保证最终一致。

**面试官：**
“促销高峰里，用户可能**连点两次下单**，第三方支付/库存回调也可能**重复推送**。你在（深圳市凡新科技 / 麦克尔斯深圳）如何保证**不会重复创建**订单/扣减库存？你具体怎么设计**幂等键**、**返回语义**，以及**与重试策略的配合**？”

**你：**

“我把问题分两层：**写接口幂等** + **事件/回调幂等**。

- **写接口（客户端→我们）**：POST 创建类接口要求客户端带 `Idempotency-Key`（或我们在 BFF 生成），幂等键 = `method + path + canonical(body) + user/tenant` 的哈希。服务端先做 **原子占位**（Redis `SETNX`/DB 唯一键），抢到占位才执行业务；执行完把**响应快照**缓存起来（含状态码、关键字段）。后续同键请求直接返回**同一份响应**（201 或 200），而不是再执行业务逻辑。
- **事件/回调（他们→我们 / 我们→下游）**：以**事件 ID**当幂等键，消费者端先查 `processed_events`（DB/Redis）是否见过，没见过才处理并**原子写入**‘已处理’标记；见过就直接 ACK。

  语义上我会遵守：

1. **PUT/DELETE** 本身具幂等；
2. **POST** 通过 `Idempotency-Key` 做到‘**功能幂等** + **响应幂等**’；
3. 返回如果命中幂等缓存，带一个 `Idempotent-Replay: true` 的响应头，方便排障。
   这套在凡新那边的订单与库存写路径都落了地；在麦克尔斯那边，支付回调我们就是用**回调事件 ID**做幂等键的。”

**面试官：**“你怎么避免并发条件下的**双写**？Redis 会不会不可靠？”

**你：**

“占位一定要**原子**且**可恢复**：

- Redis 用 `SET key value NX EX=ttl`，抢到才继续；执行完成把**响应摘要**写回同 key，值里存状态与必要字段。
- 如果担心 Redis 丢数据或需要强一致，我会在 DB 里建一张 `idempotency` 表（`idempotency_key` 唯一索引），业务在**同一事务里**插入占位记录并处理。并发下只有一个事务能成功，其它事务收到**唯一键冲突**后转为读已存在的响应摘要。
- **TTL（幂等窗口）**按业务风险定，比如创建订单 24h，库存写入 1–3h。窗口内重复请求都命中缓存；窗口外按新请求处理。”

**（伪代码，面试口述用）**

```java
// before controller
String key = hash(method, path, canonical(body), userId);
if (redis.setNx(key, "PROCESSING", ttl)) {
    Result r = handleBusiness();  // do create order / stock deduct in tx
    redis.set(key, serialize(r), ttl);   // cache response snapshot
    return r;                            // 201 Created
} else {
    return deserialize(redis.get(key));  // replay same response (201/200)
}
```

**面试官：**“如果下游超时了你会怎么重试？怎么避免**重试风暴**？”

**你：**

“我把**幂等**和**重试**绑在一起设计：

- 客户端/任务统一用**指数退避 + 抖动**（如 200ms、500ms、1.2s…，上限 5–7 次）；
- 后端在返回体里给出**可重试与否**：`429/503` 搭配 `Retry-After`，**可重试**；`4xx` 里非瞬时错误**不可重试**；
- 幂等键保证重试**不会产生副作用**；
- 对**写链路的异步下发**（比如出库通知）用**事务外箱（Transactional Outbox）+ 队列**，消费端也按事件 ID 幂等；如用队列的 FIFO + 去重（SQS FIFO/内容去重）进一步降重。”

**面试官：**“给我一个你真实遇到的例子。”

**你：**

“凡新那边在大促高峰，有用户在慢网环境**连点两次下单**，以前会出现两张‘相同订单’，后来我们把 BFF 统一生成 `Idempotency-Key`，落到后端做**占位 + 响应快照**，第二次直接重放响应，问题就没了。
在麦克尔斯那边，**支付平台回调**会在网络抖动时**重复推送** 3–5 次，我们用回调的 `eventId` 做幂等键，消费者先查‘已处理表’，见过就**幂等 ACK**，**不会重复扣款/更新**。这两处上线后，重复写导致的工单几乎归零，告警也更干净。”

### 限流-重试-熔断：客户端与服务端协同；退避策略；降级与快速失败

> **限流-重试-熔断**：入口令牌桶 + 服务内并发舱壁；只对幂等请求做**指数退避+抖动**（10% 重试预算）；**P95×1.5** 量级超时；错误/超时率阈值触发熔断与半开探测；读链路缓存降级、写链路排队受理；全链路观测 `429/5xx/熔断/重试`。

**面试官：**
“在大促或库存同步高峰时，你们的下游（支付、三方渠道、库存引擎）会抖动。你在（深圳市凡新科技 / 麦克尔斯深圳）如何**限流**、**重试**、**熔断**，既保护下游又保证整体体验？说说**策略与参数**，以及在 Spring Boot/K8s 里怎么落地？”

**你：**

“我把它当成一套‘**压力控制闭环**’：**入口限流 → 调用重试/超时 → 熔断与降级 → 指标与自愈**。

- **限流（服务端）**：网关/Ingress 层做**令牌桶**（每租户/每 API），比如：`RPS=200，突发=400`；服务内部再做**并发量阈值**（`maxConcurrent=200`），超出立刻**快速失败**返回 `429`，带 `Retry-After`。促销期我们会给**关键写接口**更严格的限额，读接口放宽。
- **限流（客户端/BFF）**：BFF 自身也做**本地并发限制**，避免把抖动放大成**重试风暴**。对同一个用户或同一个商品操作，我们会合并/串行化。
- **重试**：只对**幂等**的请求做，且**超时/503/429**才重试；使用**指数退避 + 抖动**（如 200ms、500ms、1.2s、2.5s、…，最多 5 次），并设**重试预算**（例如每分钟请求的 ≤10% 可用于重试），防止二次放大。
- **超时**：外呼都设置**硬超时**（P95×1.5 左右起步），避免线程长时间占用；不同下游不同超时，禁止“一个超时管天下”。
- **熔断**：错误率或超时率超过阈值（如 50% 且请求数≥N），**打开熔断**一段时间（比如 30–60s），期间直接失败；然后**半开**探测少量请求，恢复后再关闭。
- **降级**：读链路返回**缓存/近似值**（例如价格/库存读走 Redis 缓存 + 过期容忍），写链路则**排队/延后**（Outbox + 队列）并返回**受理中**；对非关键接口直接**功能降级**（如去掉次要字段/统计）。
- **观测**：分**版本/租户/接口**统计 `RPS、并发、429/5xx、超时率、重试次数、熔断状态`，并把**限流命中率**、**重试预算**暴露到仪表盘；做到‘哪里在保护、保护了多少’一目了然。

在 EKS + Spring Boot 的落地：网关（NGINX Ingress/ALB/API Gateway）做**硬限流**；服务内用 **Resilience4j**（或等价组件）做**TimeLimiter/Retry/RateLimiter/CircuitBreaker/Bulkhead**；部署层面给关键服务加**HPA + PDB + 合理的资源限制**，让自动扩容和限流协同而不是对冲。”

**面试官：**“能给我一组你实际会用的参数吗？怎么权衡‘保护下游’和‘用户体验’？”

**你：**

“拿**库存读**举例：

- 网关限流：每租户 `RPS 200，突发 400`；服务内并发 `maxConcurrent 200`；
- 超时：P95 大约 80ms，那我会先设 150–200ms 的客户端超时；
- 重试：指数退避 + 抖动，最多 4–5 次；预算 10%；
- 熔断：统计窗口 10s，错误/超时率 >50% 且调用数≥50，打开 30s；半开 10 个请求探测；
- 降级：命中熔断即读缓存（可过期 30–60s），返回‘库存大于 0/未知’这类**弱一致**信息，并提示页面做**轻提示**。
  促销时我们宁可**严格保护写链路**（下单/扣减），把失败暴露得更明显一些，也不要把下游打挂导致**全局雪崩**。读链路能容忍短期不准，写链路尽量排队或受理中。”

**面试官：**“如果某个依赖刚好抖了 2–3 分钟，所有客户端一起重试会不会压塌你们？”

**你：**

“我们做了三件事：

1. **重试预算**：全局限制重试占比 ≤10%，一旦达到预算，就**不再重试**直接快速失败；
2. **抖动**：退避时间随机化，避免同一时刻同步重试；
3. **排队/背压**：服务内并发满载时直接拒绝（429），并把 `Retry-After` 写清楚；异步链路用队列限速消费。
   此外，**观测层**有‘**异常重试速率**’的告警，看见就先把重试预算降下来，必要时临时调低入口 RPS。”

**面试官：**“有没有真实的事故和你们的改进？”

**你：**

“凡新那边有次第三方库存端点在半夜抖了 5 分钟，早期我们没做重试预算，导致重试量把线程池打满。复盘后我们引入**预算 + 并发舱壁**（Bulkhead），并把**缓存降级**做成开关，一键打开后 P95 直接回落。
在麦克尔斯那边，MakerPlace 图片处理链路偶发慢，我们把**超时**从 3s 切到 1.5s 并加熔断，前端降级为**低清图占位**；随后把慢服务拆到**独立队列**限速消费，线上体验稳定了。”

### 错误码与可观察性：统一错误模型 / Trace-ID / 指标-日志-链路关联

> **错误码与可观察性**：统一错误模型（`code/message/traceId/details`）+ 明确 4xx/5xx 映射；W3C trace 贯穿响应体/日志/指标；结构化日志携带 MDC（traceId 等）；RED 指标联动 APM；采样对 5xx/高延迟强制保留；日志脱敏与告警基线到位。

**面试官：**
“促销高峰里，你们的下单接口间歇性报错。客服只拿到一条报障：‘结算失败，请稍后重试’。你怎么通过**统一错误码**和**可观测性**（日志、指标、分布式追踪）**迅速定位**是用户错误还是服务端问题？结合你在（深圳市凡新科技 / 麦克尔斯深圳）的实践讲讲。”

**你：**

“我会把‘错误→定位’做成**一跳直达**：

1. **统一错误模型**让前端/客服拿到**可报障信息**：

```json
{ "code":"PAYMENT_TIMEOUT", "message":"Payment timeout, please retry",
  "traceId":"8a3c60f7…", "hint":"retry after 3s",
  "details":{ "orderId":"o123", "gateway":"stripe" } }
```

2. **Trace-ID** 贯穿**响应体 + 日志 + 指标 + 链路追踪**：客服把 `traceId` 给我们，我们在日志平台/可观测平台（EKS 上的 OTel/ADOT→Prometheus/Grafana/CloudWatch/X-Ray）就能**直跳到那条请求**。
3. 指标采用 **RED 法**（Rate/Errors/Duration）：先看该接口 `5xx/4xx` 比例和 P95 是否异常，再通过 `traceId` 打开**分布式链路**定位慢点或失败点（比如外呼支付网关超时 vs 我们的校验 400）。
   在凡新的库存/订单链路，我们这样能把‘用户填错地址’（**4xx**）和‘支付网关波动’（**5xx/超时**）几分钟内区分清楚；在麦克尔斯的 MakerPlace，我们把 `traceId` 展示在移动端错误页里，客服直接抄给我们。”

**面试官：**“错误码你怎么分层？HTTP 状态码和业务码怎么配？”

**你：**

“**HTTP 只表达通用语义**，**业务码表达具体原因**：

- 4xx：用户侧/可预期错误，比如 `VALIDATION_FAILED`、`AUTH_REQUIRED`、`RATE_LIMITED`；
- 5xx：服务侧/依赖侧，比如 `UPSTREAM_TIMEOUT`、`DB_DEADLOCK`、`STOCK_INCONSISTENT`。
  **映射规则**固定：`400/401/403/404/409/422/429` 用于常见场景；`500/502/503/504` 对应服务端/依赖错误。错误体统一四段：`code/message/traceId/details`；**message 面向人类**（可本地化），`code` 面向程序，`details` 放**可排障字段**（不含敏感信息）。
  另外我们会规定：**相同幂等键的重放**返回同一业务码，并在响应头加 `Idempotent-Replay:true`，排障更清楚。”

**面试官：**“在 Spring Boot / K8s 上，你怎么把 Trace-ID 贯穿并串起‘指标-日志-链路’三件事？”

**你：**

“落地分三步：

1. **链路追踪**：W3C `traceparent` 头透传（网关→BFF→微服务→下游）；未携带则在网关生成。后端用 **OpenTelemetry SDK** 自动注入 span。
2. **结构化日志**：日志全用 JSON，`traceId/spanId/tenant/userId` 写进 **MDC**，日志 Appender 自动带上：

```java
// in a filter
String traceId = currentTraceIdOrGenerate();
MDC.put("traceId", traceId);
// controller logs will carry it; also return in body/header
```

3. **指标关联**：在计时器/计数器（Micrometer）上打 `api=placeOrder, outcome=success|error, http_status=...` 等标签；Grafana 面板支持按 `traceId` 链接到 APM，形成**点开指标→跳到具体 trace→再看相关日志**的三连。
   采样方面：默认 **概率采样**（如 5–10%），对 `5xx`/高延迟请求**强制采样**，确保关键问题有完整 trace。”

**面试官：**“怎么避免把隐私或安全数据打到日志？告警怎么设？”

**你：**

“我们做了两件事：

- **日志脱敏/拦截**：统一的 `LogSanitizer` 过滤 `password/token/card/email` 等字段；**从不**把 Authorization、JWT、银行卡号写日志；错误体的 `details` 也做白名单。
- **告警基线**：
  - SLO：如下单成功率 99.5%，**误差预算**消耗≥5% 触发告警；
  - `5xx` 比例、`429` 命中率、P95 超过阈值持续 5 分钟；
  - **错误码分布**偏移（例如 `PAYMENT_TIMEOUT` 激增）触发调度，第一时间看依赖健康与熔断状态。
    这样既安全又可定位。”

**面试官：**“讲个你遇到的真实定位案例。”

**你：**

“凡新一次活动夜里，`/checkout` 的 `5xx` 抬头。我们从 Grafana 上看到 `UPSTREAM_TIMEOUT` 占比升高，随手点进一个 trace，发现**支付网关调用 3s 超时**。同时日志里同一个 `traceId` 显示我们内部处理只有 40ms——很快就定位到是**外部依赖抖动**，切开关走**降级队列**并调低重试预算，几分钟内恢复。
麦克尔斯那边有次是**4xx**暴涨，错误码 `VALIDATION_FAILED`，trace 里显示是 `postalCode` 校验新规则上线，回滚后即恢复。统一错误码 + trace 贯穿真的省了太多时间。”

### 灰度发布与回滚：逐步放量 / 健康检查 / 一键回滚

> **灰度与回滚**：滚动/金丝雀/蓝绿按风险选型；金丝雀 1%→5%→25%→50%→100%，以 `5xx/4xx/P95/资源/错误码分布` 设守卫，失败自动回滚；探针 + preStop 优雅下线；DB 走 **expand→migrate→contract**，特性开关解耦交付与发布；发布即实验、指标闭环。

**面试官：**
“你在（深圳市凡新科技 / 麦克尔斯深圳）做库存与下单链路时，如何把**高风险改动**安全上线？比如新版本涉及**缓存策略调整 + 一个字段语义变化**，你怎么做**灰度策略**、**健康检查**、以及**快速回滚**？”

**你：**

“我的思路是‘**发布即实验**’：把每次上线当成带监控的实验，**小流量试水 → 指标达标再放量 → 不达标立即回滚**。

- **发布策略选择**
  - **滚动更新（K8s 默认）**：常规小改动；`maxUnavailable=0、maxSurge=25%`，确保无损切换。
  - **金丝雀（Canary）**：有**性能/缓存语义**风险时，用 1%→5%→25%→50%→100% 分阶段放量；按 **4xx/5xx、P95、错误预算燃尽率** 设**自动阻断/回滚**阈值。
  - **蓝绿（Blue/Green）**：涉及**大版本或依赖升级**时，用全量双环境；流量开关一键切回旧环境。
  - **特性开关（Feature Flag）**：把业务开关与**交付开关解耦**，先灰度代码，再灰度开关。
- **健康检查与就绪**
  - **Startup/Readiness/Liveness** 三探针分工明确：Startup 给冷启动、Readiness 挡流量、Liveness 防僵死；
  - **preStop 钩子 + 优雅下线**（如 `sleep 5`），配合 Ingress/ALB 目标组的健康阈值，避免**半关闭阶段**丢请求；
  - **只读路径**先验证（GET/查询），**写路径**在金丝雀阶段**缩小配额**并捆绑幂等等保护。
- **回滚**
  - **一键回滚**：Helm `rollback` / Argo Rollouts `abort` / 网关流量权重回拨；
  - **数据向后兼容**：DB 走 **expand→migrate→contract** 两阶段，确保旧版本还能工作；
  - **事后验证**：回滚后继续观察 10–30 分钟，确认指标归位。”

**面试官：**“如果涉及数据库表结构变更，你怎么保证灰度与回滚不被数据库‘卡住’？”

**你：**

“我严格走**向后兼容的双阶段**：

1. **Expand**：先上线 v1.5，加**新列/新表**（可空）与**写入双写**，读路径仍用旧列；
2. **Migrate**：异步回填/对账，监控读取命中率与差异；
3. **Switch**：新版本开始读新列，但**旧列仍保持实时同步**；
4. **Contract**：确认稳定（通常一到两周）才删除旧列。
   这样**任何阶段都可回滚**到旧版本；契约层面只做**向后兼容**的新增字段，不做破坏性变更。”

**面试官：**“你会设置哪些‘自动回滚’阈值？”

**你：**

“按**金丝雀批次**设硬阈值，例如每 5 分钟窗口：

- **错误率**：`5xx > 1%` 或 `4xx（非校验）> 3%` 立即阻断；
- **延迟**：`P95 较基线上浮 > 30%` 阻断；
- **资源**：`CPU>80% & GC 暴涨` 或 `容器重启` 超阈值阻断；
- **错误码分布**：`PAYMENT_TIMEOUT`、`STOCK_INCONSISTENT` 异常抬头即阻断。
  阻断后：**自动回滚 + 触发告警 + 开启降级**（读走缓存、写排队）。这些阈值在凡新大促和麦克尔斯的 MakerPlace 我都踩过坑，后来固定为发布模板的一部分。”

**面试官：**“能给我一套落地做法吗？”

**你：**

“K8s 侧：

- Deployment：`rollingUpdate.maxSurge=25%、maxUnavailable=0`；配 PDB 保证有**最小可用副本**；
- 探针：`startupProbe` > `readinessProbe` > `livenessProbe`，超时阈值分开设；
- HPA：根据 `CPU/内存/自定义 QPS` 指标自动扩，但**不代替**限流；
- 渐进式交付：**Argo Rollouts/Flagger** 配 `setWeight` 与 `metric` 守卫，失败自动 `abort`；
- 网关：ALB/NGINX 做**权重路由**；金丝雀阶段把**写操作**路由比例更低。

应用侧：

- Spring Boot 加**启动预热**（连接池/缓存）与**优雅停机**；
- 加**版本与commit id**到 `/actuator/info`；
- 监控面板预置‘上线看板’：金丝雀 vs 基线对比（RPS/5xx/4xx/P95/CPU/重启数）。这样**发布即实验**就能闭环。”

**面试官：**“说个你当时印象深刻的灰度/回滚案例。”

**你：**

“凡新有次把**库存写入缓存策略**改成‘写后淘汰’，金丝雀 5% 时 `STOCK_INCONSISTENT` 错误码抬头且 P95 拉长，我们立即**abort 回滚**，切回旧策略，随后排查发现是**并发回写与淘汰顺序**导致短暂不一致；修复后再灰度就稳了。
麦克尔斯那边，移动端依赖的**图片处理服务**升级了底层库，1% 金丝雀就发现**内存泄漏 + 容器重启**，我们一键回滚并把这条链路拆出**独立队列限速**，再上线就顺滑了。”

---

## 第 4 步：英语口语素材

### 45s Elevator Pitch（口语版）

**要点备忘（不需要念出来）**

- 我是谁：Java 后端 + Cloud-native
- 做过啥：电商/库存/订单 API，Spring Boot + EKS
- 方法论：契约清晰、版本化、最小权限、幂等、限流/重试/熔断、统一错误模型+Trace ID
- 价值：在高峰期稳定可扩展、问题可快速定位
- 诉求：希望负责核心 API 的设计与可靠性

**脚本**

Hi, I’m **Renda Zhang**, a Java backend engineer focused on **cloud-native microservices**.
In my recent roles at Shenzhen-based teams, I’ve been building and operating **e-commerce APIs**—products, inventory and checkout—on **Spring Boot** with **Kubernetes/EKS**. I care a lot about **predictable contracts** and **operability**: OpenAPI-driven schemas, **versioning when changes are breaking**, **OIDC/JWT** with least-privilege scopes, **idempotency keys** for writes, and **rate-limit + retry with backoff + circuit breaker** to protect dependencies.
We also standardized a **unified error model** with **trace IDs**, so incidents are easy to triage during promotion spikes.
I’m looking to join a team where I can **own core API design and reliability**, and I bring hands-on experience with **EKS, Redis, MySQL/Postgres, and observability pipelines** to keep services both **scalable and debuggable**.

**可替换短句（按岗位画像切换）**

- 如果更偏平台侧：“…and I’ve been productizing these patterns as **reusable starters and policies** so teams ship safer by default.”
- 如果更偏业务侧：“…and I translate business rules into **clear API contracts** that third-party channels can adopt with minimal friction.”

### 1-min Answer - “Why this API design?”

**要点备忘（不需要念出来）**

- 场景：多渠道商品/库存/下单
- 非功能约束：流量波动、可演进、排障效率
- 设计：Canonical Model、版本化、鉴权与最小权限、幂等与重试、限流与熔断、错误模型与可观测
- 权衡：一致性 vs 延迟、缓存与回写、灰度与回滚
- 结果：可预测、抗压、易演进

**脚本**

For multi-channel commerce, the API has to be **predictable, evolvable, and observable**.
First, I define a **canonical domain model**—products, variants, stock items—so different channels map into **one set of meanings**. We keep **contracts explicit** with OpenAPI and **use URL versioning** when a change is breaking.
Security is **OIDC/JWT** with **least-privilege scopes**; service-to-service calls use short-lived credentials.
Writes are **idempotent** via an `Idempotency-Key`, so client retries are safe. Around dependencies I add **rate-limit + retry with jitter + circuit breaker**, and I set **sensible timeouts** per call.
Errors follow a **unified shape**—`code, message, traceId, details`—and we propagate the **trace ID** across logs, metrics and distributed traces, which makes on-call triage straightforward.
On evolution, we **add fields compatibly** within a version, and for risky changes we go **canary** with clear rollback gates.
This design lets us **absorb traffic spikes** without surprises, **debug quickly**, and **ship changes confidently**. In my last projects, it’s been a practical balance between **consistency and latency**: cached reads where safe, guarded writes with queues and outbox when needed.

**可能的追问 & 即兴回答**

- Why URL versioning? → “It’s visible and simple for partners; headers are fine internally, but URLs reduce coordination cost across teams.”
- How do you avoid double-writes? → “Idempotency key + atomic reservation, and we replay the same response if the key repeats.”
- How do you roll back safely? → “Canary with hard guards on 5xx/P95; DB follows **expand-migrate-contract** so older versions keep working.”

### 练习与落地（两分钟内搞定）

* 把上面两段粘到 `elevator_pitch_en.md`。
* 大声读三遍，录一次音，检查是否**在 45s / 60s 以内**，语速放慢，句子收尾要干净。
* 标记 3 个最想强调的词，用手势或停顿突出即可。
