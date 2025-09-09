<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [高频问题库](#%E9%AB%98%E9%A2%91%E9%97%AE%E9%A2%98%E5%BA%93)
  - [API Design & Reliability](#api-design--reliability)
    - [契约清晰：资源建模 & 语义化接口（Contract Clarity）](#%E5%A5%91%E7%BA%A6%E6%B8%85%E6%99%B0%E8%B5%84%E6%BA%90%E5%BB%BA%E6%A8%A1--%E8%AF%AD%E4%B9%89%E5%8C%96%E6%8E%A5%E5%8F%A3contract-clarity)
      - [场景题](#%E5%9C%BA%E6%99%AF%E9%A2%98)
      - [追问 1（场景深挖）](#%E8%BF%BD%E9%97%AE-1%E5%9C%BA%E6%99%AF%E6%B7%B1%E6%8C%96)
      - [追问 2（项目落地）](#%E8%BF%BD%E9%97%AE-2%E9%A1%B9%E7%9B%AE%E8%90%BD%E5%9C%B0)

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
