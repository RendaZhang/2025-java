<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [高频问题库](#%E9%AB%98%E9%A2%91%E9%97%AE%E9%A2%98%E5%BA%93)
  - [API Design & Reliability](#api-design--reliability)
    - [契约清晰：资源建模 & 语义化接口（Contract Clarity）](#%E5%A5%91%E7%BA%A6%E6%B8%85%E6%99%B0%E8%B5%84%E6%BA%90%E5%BB%BA%E6%A8%A1--%E8%AF%AD%E4%B9%89%E5%8C%96%E6%8E%A5%E5%8F%A3contract-clarity)
      - [场景题](#%E5%9C%BA%E6%99%AF%E9%A2%98)
      - [追问 1（场景深挖）](#%E8%BF%BD%E9%97%AE-1%E5%9C%BA%E6%99%AF%E6%B7%B1%E6%8C%96)
      - [追问 2（项目落地）](#%E8%BF%BD%E9%97%AE-2%E9%A1%B9%E7%9B%AE%E8%90%BD%E5%9C%B0)
    - [版本化策略：URI vs Header；向后兼容与下线流程](#%E7%89%88%E6%9C%AC%E5%8C%96%E7%AD%96%E7%95%A5uri-vs-header%E5%90%91%E5%90%8E%E5%85%BC%E5%AE%B9%E4%B8%8E%E4%B8%8B%E7%BA%BF%E6%B5%81%E7%A8%8B)
      - [场景题](#%E5%9C%BA%E6%99%AF%E9%A2%98-1)
      - [追问 1（深挖迁移计划）](#%E8%BF%BD%E9%97%AE-1%E6%B7%B1%E6%8C%96%E8%BF%81%E7%A7%BB%E8%AE%A1%E5%88%92)
      - [追问 2（工程实现）](#%E8%BF%BD%E9%97%AE-2%E5%B7%A5%E7%A8%8B%E5%AE%9E%E7%8E%B0)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 高频问题库

- **Java**：集合/并发（synchronized、Lock、CAS、线程池）、JVM（内存结构、GC）、异常与最佳实践
- **Spring**：IOC/AOP、RestController、Actuator、配置管理、事务/连接池
- **微服务 & K8s**：Deployment/Service/Ingress/HPA/探针、无状态、滚动发布与回滚、ConfigMap/Secret
- **AWS & 云原生**：EKS NodeGroup vs Fargate、ALB、IRSA/OIDC、ECR、S3、CloudWatch、AMP、Grafana
- **DevOps**：CI/CD（GitHub Actions OIDC）、Trivy、回滚策略、IaC（Terraform 后端与锁）、最小权限
- **SRE**：SLI/SLO/错误预算、MTTR、Chaos、容量与成本权衡
- **行为/英文**：冲突处理、推动落地、失败复盘、跨团队协作、影响力（每题准备一条 STAR）

## API Design & Reliability

### 契约清晰：资源建模 & 语义化接口（Contract Clarity）

> **契约清晰（资源建模/语义化 URL/统一字段语义/错误模型/可观测 Trace-ID）**：对外坚持 Canonical Model + OpenAPI/JSON Schema 校验，分页/排序/时间/金额/ID 统一约定，返回体可观测可排障；渠道差异放在内部映射层，外部只做向后兼容的增量字段。

#### 场景题

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

#### 追问 1（场景深挖）

**面试官：**“上游新增了一个渠道特有字段，比如 `shopify_location_id`，但你不想污染对外契约，怎么处理？”

**你：**“我不会把渠道细节渗透进公开模型，而是：

1. **内部映射层**吸收它（Connector DTO）；
2. 对外契约只在**业务确实需要**且跨渠道有共同语义时才**增量添加**字段（只做向后兼容的**可选字段**）；
3. 对必须透传的极少数字段，用 `extensions.*` 命名空间承载，并在 OpenAPI 标注**非核心**。这样不破坏现有调用方，也避免**破坏性变更**在高峰期放大。”（与我们在活动期高 QPS 的稳定性目标一致。）

#### 追问 2（项目落地）

**面试官：**“你在凡新或 Michaels 有没有因为契约不清导致事故？后来怎么改的？”

**你：**“有一次库存批量同步的响应里，**金额字段单位**没写清，导致一个下游任务把分当元，差点误触发大额补货。后来我们把**金额强制最小单位 + 货币代码**写进 Schema，并在 CI 里做**契约校验**与**示例响应校验**；同时在库存批同步流程里也加了**幂等键与步骤化编排**（我们用 **Lambda + SQS + Step Functions** 重构这条链路，整体耗时也从 ~25min 降到 ~7min）。”

### 版本化策略：URI vs Header；向后兼容与下线流程

> **版本化（URI 大版本 + Header 可选；向后兼容优先）**：非破坏性演进留在同大版本，破坏性才切 v2；提供兼容层与双写验证；Deprecation/Sunset 通知 + 分版本监控 + 强制下线日程，做到“可见、可控、可回滚”。

#### 场景题

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

#### 追问 1（深挖迁移计划）

**面试官：**“如果大量老客户端一时半会升级不了，导致你迟迟不能下线 v1 怎么办？”

**你：**
“我们会把**兼容层**做成**可配置的**：

* 先在 v2 内部保留一层**适配器**把 `warehouses` 聚合成 `stockQuantity` 返回给 v1 客户；
* 在 API 网关对 v1/v2 的**QPS、错误率、延迟**做**分版本监控**，并在每次版本公告后给出**采纳率**；
* 设一个明确的**日程线**：例如 90 天后进入‘降级窗口’，老版本只做**安全修复**不加新特性；180 天后**强制下线**（返回 410 + 链接到迁移文档）。
  这样我们既不拖累新版本的演进，也给合作方足够时间。”

#### 追问 2（工程实现）

**面试官：**“Spring Boot 里你怎么同时支持 URI 版本和 Header 版本？”

**你：**
“实际做法是**对外统一用 URI 大版本**，对内需要时再开 Header 协商：

* 控制器层：`/api/v1/...` 与 `/api/v2/...` 各有路由；
* 若同一路径用 Header：在 `@RequestMapping` 的 `produces` 里声明 `application/vnd.renda.stock+json;v=1/2`，并配置 `ContentNegotiationStrategy`；
* OpenAPI 文档分**两个 group**（v1/v2）生成 swagger，CI 里对两套 **JSON Schema** 做**契约校验**与**向后兼容检查**（新增字段只能是可选、禁止删除/改义）。
  配合灰度和回滚开关，风险可控。”
