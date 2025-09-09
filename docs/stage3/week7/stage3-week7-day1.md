<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 1 - 开场与 API 设计（契约与可靠性打底）](#day-1---%E5%BC%80%E5%9C%BA%E4%B8%8E-api-%E8%AE%BE%E8%AE%A1%E5%A5%91%E7%BA%A6%E4%B8%8E%E5%8F%AF%E9%9D%A0%E6%80%A7%E6%89%93%E5%BA%95)
  - [目标](#%E7%9B%AE%E6%A0%87)
    - [步骤清单](#%E6%AD%A5%E9%AA%A4%E6%B8%85%E5%8D%95)
  - [第 1 步：开场与环境就绪（10–15 分钟）](#%E7%AC%AC-1-%E6%AD%A5%E5%BC%80%E5%9C%BA%E4%B8%8E%E7%8E%AF%E5%A2%83%E5%B0%B1%E7%BB%AA1015-%E5%88%86%E9%92%9F)
    - [创建 `docs/interview/QBANK.md`：](#%E5%88%9B%E5%BB%BA-docsinterviewqbankmd)
    - [创建 `docs/interview/elevator_pitch_en.md`](#%E5%88%9B%E5%BB%BA-docsinterviewelevator_pitch_enmd)
  - [第 2 步：算法训练（LeetCode，60–90 分钟）](#%E7%AC%AC-2-%E6%AD%A5%E7%AE%97%E6%B3%95%E8%AE%AD%E7%BB%83leetcode6090-%E5%88%86%E9%92%9F)
    - [LC 904. Fruit Into Baskets（中等，Sliding Window + HashMap）](#lc-904-fruit-into-baskets%E4%B8%AD%E7%AD%89sliding-window--hashmap)
    - [LC 167. Two Sum II – Input Array Is Sorted（简单，Two Pointers）](#lc-167-two-sum-ii--input-array-is-sorted%E7%AE%80%E5%8D%95two-pointers)
    - [LC 209. Minimum Size Subarray Sum（中等，Sliding Window）](#lc-209-minimum-size-subarray-sum%E4%B8%AD%E7%AD%89sliding-window)
    - [复盘 LC 904](#%E5%A4%8D%E7%9B%98-lc-904)
      - [Pattern](#pattern)
      - [Intuition](#intuition)
      - [Steps](#steps)
      - [Complexity](#complexity)
      - [Edge Cases](#edge-cases)
      - [Mistakes & Fix](#mistakes--fix)
      - [Clean Code（面试友好版，Java）](#clean-code%E9%9D%A2%E8%AF%95%E5%8F%8B%E5%A5%BD%E7%89%88java)
  - [第 3 步：API 设计与可靠性要点](#%E7%AC%AC-3-%E6%AD%A5api-%E8%AE%BE%E8%AE%A1%E4%B8%8E%E5%8F%AF%E9%9D%A0%E6%80%A7%E8%A6%81%E7%82%B9)
    - [要点 1｜契约清晰：资源建模 & 语义化接口（Contract Clarity）](#%E8%A6%81%E7%82%B9-1%EF%BD%9C%E5%A5%91%E7%BA%A6%E6%B8%85%E6%99%B0%E8%B5%84%E6%BA%90%E5%BB%BA%E6%A8%A1--%E8%AF%AD%E4%B9%89%E5%8C%96%E6%8E%A5%E5%8F%A3contract-clarity)
    - [要点 2｜版本化策略：URI vs Header；向后兼容与下线流程](#%E8%A6%81%E7%82%B9-2%EF%BD%9C%E7%89%88%E6%9C%AC%E5%8C%96%E7%AD%96%E7%95%A5uri-vs-header%E5%90%91%E5%90%8E%E5%85%BC%E5%AE%B9%E4%B8%8E%E4%B8%8B%E7%BA%BF%E6%B5%81%E7%A8%8B)

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

## 第 1 步：开场与环境就绪（10–15 分钟）

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

## 第 2 步：算法训练（LeetCode，60–90 分钟）

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

#### Pattern

Sliding Window（保持窗口内**至多两种**水果类型）。

#### Intuition

题目等价于：在数组中找到“最多包含两种不同元素”的最长连续子数组长度。自然想到用右指针扩张窗口、当种类数 > 2 时用左指针收缩，直到种类数 ≤ 2 为止，同时记录历史最大长度。

#### Steps

1. 用一个结构维护窗口内“每种水果的出现次数”（可用 `Map<Integer,Integer>`）。
2. 右指针 `r` 逐步右移、计数 +1；若窗口的键数 > 2，则移动左指针 `l`，将 `fruits[l]` 计数 -1，减到 0 则从 `Map` 删除该键，直到键数 ≤ 2。
3. 每次扩张或收缩后，以 `r - l + 1` 更新答案。

> 你的实现等价思路：不显式维护计数，而是用 `next_start` 记录“最近一段连续的最新水果开始位置”。当出现第 3 种时，直接把 `l` 跳到 `next_start`，并重置集合为“上一种 + 当前新种”，从而 O(1) 地完成“批量收缩”。

#### Complexity

* Time：O(n)，每个元素最多进出窗口一次。
* Space：O(1)，窗口内最多两种类型（`Map`/`Set` 常数级）。

#### Edge Cases

* 全相同元素（如 `[1,1,1,1]`）→ 直接返回数组长度。
* 只有两种元素交替（如 `[1,2,1,2,1,2]`）→ 返回数组长度。
* 频繁切换第三种（如 `[1,2,3,2,2]`）→ 注意在出现第 3 种时的收缩与“最新连续段起点”的更新。
* 极短数组（长度 0/1/2）→ 边界直接返回长度（按题目约束通常 ≥1）。

#### Mistakes & Fix

* **坑点**：在检测到第 3 种水果时，如果没有**先**用历史窗口长度更新答案、**再**正确设置 `start = next_start` 与同步 `curr_type/next_start`，容易丢失最佳解或出现 off-by-one。
* **修正**：先 `result = max(result, curr_max)`，然后用 `curr_max = curr_max - (next_start - start) + 1`（等价于 `i - next_start + 1`）重置窗口长度，并把 `start` 跳到 `next_start`，最后更新 `curr_type = fruits[i]` 与 `next_start = i`。

#### Clean Code（面试友好版，Java）

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

### 要点 1｜契约清晰：资源建模 & 语义化接口（Contract Clarity）

> **契约清晰（资源建模/语义化 URL/统一字段语义/错误模型/可观测 Trace-ID）**：对外坚持 Canonical Model + OpenAPI/JSON Schema 校验，分页/排序/时间/金额/ID 统一约定，返回体可观测可排障；渠道差异放在内部映射层，外部只做向后兼容的增量字段。

> 备注：**版本化**、**幂等**、**限流/重试/熔断**会在后面的 2–7 条分别展开。

**面试官：**
“你在跨境电商/库存中台里，如何把**商品/库存**这类核心对象建模，并通过**清晰稳定的 API 契约**让前端、第三方渠道（Shopify/WooCommerce 等）都能稳用？如果上游平台字段各不相同、且业务量在活动期飙升，你会怎么设计接口与返回结构？”（你可以结合你在深圳市凡新科技 & Michaels 的经历来回答）

**你（口语化回答示范）：**
“我会先做一个**Canonical Model（规范化域模型）**，然后把各平台的字段映射进来，API 对外只暴露**我们的一致语义**。例如把 `Product`、`Variant`、`StockItem` 拆清，`/products/{id}`、`/variants/{id}`、`/stocks?variantId=...&channel=...` 用**语义化 URL**和**查询参数**表达资源与过滤。之所以坚持对外契约稳定，是因为我们的服务在活动期会到 **80k–150k req/day，峰值 \~1.2k QPS**，而且要保持 P95 < 140ms，所以**任何破坏性变更都会造成放大效应**。这在我现在的工作环境里是常态（AWS 上 6 个 Spring Boot 微服务 + 自动扩容），因此我会把契约做成**可文档化、可校验**的，比如 OpenAPI + JSON Schema，前后端都能对齐检查。”

“以**库存**为例，我会规定：

* **ID 与类型**：所有 ID 统一用字符串（避免某些平台 `variant_id` 的长整型在 JS 客户端精度丢失）；金额统一**分为最小货币单位**（如分）、**货币代码**单独字段；时间全用 **RFC3339 UTC**。
* **分页与排序**：`page/size/sort` 统一格式；对大列表返回 `nextCursor` 以便前端/任务稳定翻页。
* **并发读写**：读 API 返回 `ETag/Last-Modified`，写 API 支持 `If-Match` 做并发控制；结合缓存（我们线上用 **Redis Cluster + Aurora 只读副本** 做读写分离），读路径可控、延迟稳定。”

“在 **Michaels** 做电商与 MakerPlace 的 API 时，我们也坚持把**登录与用户域**契约化，比如 **JWT 轮换 + OAuth2** 统一安全语义，接口文档清晰，移动端/前端对接成本低；同时在性能上通过**索引与响应结构优化**把接口延迟打下来，证明**契约清晰**有助于定位与优化。 ”

“错误返回我会统一一个**错误模型**：

```json
{ "code": "STOCK_NOT_FOUND", "message": "Stock item not found", "requestId": "trace-id", "details": { "variantId": "v123", "channel": "shopify" } }
```

配合**Trace-ID** 贯通到 CloudWatch / X-Ray / Grafana，这对我们线上**快速定位**很关键（我们有完善的可观测和零停机发布流程）。”

**面试官：**“上游新增了一个渠道特有字段，比如 `shopify_location_id`，但你不想污染对外契约，怎么处理？”

**你：**“我不会把渠道细节渗透进公开模型，而是：

1. **内部映射层**吸收它（Connector DTO）；
2. 对外契约只在**业务确实需要**且跨渠道有共同语义时才**增量添加**字段（只做向后兼容的**可选字段**）；
3. 对必须透传的极少数字段，用 `extensions.*` 命名空间承载，并在 OpenAPI 标注**非核心**。这样不破坏现有调用方，也避免**破坏性变更**在高峰期放大。”（与我们在活动期高 QPS 的稳定性目标一致。）

**面试官：**“你在凡新或 Michaels 有没有因为契约不清导致事故？后来怎么改的？”

**你：**“有一次库存批量同步的响应里，**金额字段单位**没写清，导致一个下游任务把分当元，差点误触发大额补货。后来我们把**金额强制最小单位 + 货币代码**写进 Schema，并在 CI 里做**契约校验**与**示例响应校验**；同时在库存批同步流程里也加了**幂等键与步骤化编排**（我们用 **Lambda + SQS + Step Functions** 重构这条链路，整体耗时也从 ~25min 降到 ~7min）。”

### 要点 2｜版本化策略：URI vs Header；向后兼容与下线流程

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

* 先在 v2 内部保留一层**适配器**把 `warehouses` 聚合成 `stockQuantity` 返回给 v1 客户；
* 在 API 网关对 v1/v2 的**QPS、错误率、延迟**做**分版本监控**，并在每次版本公告后给出**采纳率**；
* 设一个明确的**日程线**：例如 90 天后进入‘降级窗口’，老版本只做**安全修复**不加新特性；180 天后**强制下线**（返回 410 + 链接到迁移文档）。
  这样我们既不拖累新版本的演进，也给合作方足够时间。”

**面试官：**“Spring Boot 里你怎么同时支持 URI 版本和 Header 版本？”

**你：**
“实际做法是**对外统一用 URI 大版本**，对内需要时再开 Header 协商：

* 控制器层：`/api/v1/...` 与 `/api/v2/...` 各有路由；
* 若同一路径用 Header：在 `@RequestMapping` 的 `produces` 里声明 `application/vnd.renda.stock+json;v=1/2`，并配置 `ContentNegotiationStrategy`；
* OpenAPI 文档分**两个 group**（v1/v2）生成 swagger，CI 里对两套 **JSON Schema** 做**契约校验**与**向后兼容检查**（新增字段只能是可选、禁止删除/改义）。
  配合灰度和回滚开关，风险可控。”

---
