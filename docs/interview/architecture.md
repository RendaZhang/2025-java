<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Architecture](#architecture)
  - [1. 概述（Purpose & Scope）](#1-%E6%A6%82%E8%BF%B0purpose--scope)
  - [2. 总体架构（Overview）](#2-%E6%80%BB%E4%BD%93%E6%9E%B6%E6%9E%84overview)
  - [3. 组件与设计要点（Components）](#3-%E7%BB%84%E4%BB%B6%E4%B8%8E%E8%AE%BE%E8%AE%A1%E8%A6%81%E7%82%B9components)
    - [3.1 网关与鉴权（Gateway & AuthN/Z）](#31-%E7%BD%91%E5%85%B3%E4%B8%8E%E9%89%B4%E6%9D%83gateway--authnz)
    - [3.2 服务层（Services）](#32-%E6%9C%8D%E5%8A%A1%E5%B1%82services)
    - [3.3 数据层（Database）](#33-%E6%95%B0%E6%8D%AE%E5%B1%82database)
    - [3.4 缓存层（Redis）](#34-%E7%BC%93%E5%AD%98%E5%B1%82redis)
    - [3.5 消息与一致性（Messaging & Consistency）](#35-%E6%B6%88%E6%81%AF%E4%B8%8E%E4%B8%80%E8%87%B4%E6%80%A7messaging--consistency)
    - [3.6 对象存储与搜索（S3 / Search）](#36-%E5%AF%B9%E8%B1%A1%E5%AD%98%E5%82%A8%E4%B8%8E%E6%90%9C%E7%B4%A2s3--search)
  - [4. 环境与平台（Environments & Platform）](#4-%E7%8E%AF%E5%A2%83%E4%B8%8E%E5%B9%B3%E5%8F%B0environments--platform)
  - [5. 交付与发布（CI/CD & Releases）](#5-%E4%BA%A4%E4%BB%98%E4%B8%8E%E5%8F%91%E5%B8%83cicd--releases)
  - [6. 可观测与可靠性（Observability & Reliability）](#6-%E5%8F%AF%E8%A7%82%E6%B5%8B%E4%B8%8E%E5%8F%AF%E9%9D%A0%E6%80%A7observability--reliability)
  - [7. 安全（Security）](#7-%E5%AE%89%E5%85%A8security)
  - [8. 前端集成（Frontend Integration）](#8-%E5%89%8D%E7%AB%AF%E9%9B%86%E6%88%90frontend-integration)
  - [9. 运行手册（Runbooks）](#9-%E8%BF%90%E8%A1%8C%E6%89%8B%E5%86%8Crunbooks)
  - [10. API 风格与错误码（Appendix）](#10-api-%E9%A3%8E%E6%A0%BC%E4%B8%8E%E9%94%99%E8%AF%AF%E7%A0%81appendix)
  - [11. 数据与模型（Appendix）](#11-%E6%95%B0%E6%8D%AE%E4%B8%8E%E6%A8%A1%E5%9E%8Bappendix)
  - [12. 术语表（Glossary）](#12-%E6%9C%AF%E8%AF%AD%E8%A1%A8glossary)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Architecture

> 说明：本模板用于**围绕你最近工作里最相关的系统**做面试版架构文档（3–5 页）。
> 避免写具体业务数据，可在 `TODO` 处按需补充。
> 若生产在 **ECS/Fargate**，而方法论验证在 **EKS（内部演练）**，可在“环境与平台”中分别描述。

---

## 1. 概述（Purpose & Scope）

- **系统名称**：TODO（例如「订单与库存中台」或以你当前职责命名）
- **目标**：说明系统要解决的核心问题、主要消费者/上下游、可靠性与交付的原则（不写数值）。
- **范围**：本文件仅覆盖：总体架构、关键组件、交付与发布、可观测与稳定性、安全、前端集成与运行手册。
- **非目标**：暂不包含详尽业务规则、报表细节、机密参数与企业级合规细目。

## 2. 总体架构（Overview）

```mermaid
flowchart LR
  client[Client / Web / Mobile]
  gw[API Gateway / Ingress]
  bff[BFF / Edge Service (optional)]
  svc1[Service A<br/>Java + Spring Boot]
  svc2[Service B<br/>Java + Spring Boot]
  mq[Messaging\n(SQS/Kafka/RabbitMQ)]
  cache[(Redis Cluster)]
  db[(Aurora/MySQL or PostgreSQL)]
  s3[(Object Storage / S3)]
  search[(Search / OpenSearch-ES)]
  obs[Observability\n(OTel → Metrics/Logs/Traces)]
  ci[CI/CD\n(GitHub Actions → Cloud)]

  client --> gw --> bff --> svc1
  bff --> svc2
  svc1 <--> cache
  svc1 <--> db
  svc1 --> mq
  svc2 --> mq
  svc1 --> s3
  svc2 --> s3
  svc1 -.-> search
  svc2 -.-> search
  gw -. metrics/logs/traces .-> obs
  svc1 -. metrics/logs/traces .-> obs
  svc2 -. metrics/logs/traces .-> obs
  ci --> gw
  ci --> svc1
  ci --> svc2
```

**说明**

- **接口与网关**：API Gateway/Ingress 统一入口，路由、限流、鉴权前置；可选 BFF 聚合。
- **服务层**：Java + Spring Boot 微服务，遵循清晰的 API 契约与版本策略。
- **数据层**：主从/读写分离；缓存旁路/写策略；对象存储承载静态/大对象。
- **异步解耦**：消息队列 + Outbox（见 §5 一致性）。
- **可观测**：OTel SDK 埋点，统一指标/日志/追踪三板斧；按 SLO 设计告警。
- **交付**：CI/CD + IaC，金丝雀/蓝绿发布与快回滚。

## 3. 组件与设计要点（Components）

### 3.1 网关与鉴权（Gateway & AuthN/Z）

- 路由与限流：按路径/租户/用户维度；策略存放与灰度控制。
- 鉴权：OIDC/JWT；权限粒度（接口/资源/操作）。
- 错误码与幂等：统一错误码规范；`Idempotency-Key` 与幂等资源设计。

### 3.2 服务层（Services）

- **技术栈**：Java 17+/Spring Boot 3.x，分层（Controller/Service/Repo），关注可测试性与可观测性。
- **容错**：重试（指数退避 + 抖动）、超时、熔断、舱壁隔离、限流（令牌桶/漏桶）。
- **契约**：API 版本化（URL / Header / Accept），兼容性演进策略。

### 3.3 数据层（Database）

- **建模**：围绕核心实体（订单/库存等，按实际替换）；主键/唯一键/外键取舍。
- **读写分离**：写入主库，读取路由到只读副本（延迟与一致性策略）。
- **事务**：隔离级别选择；热点写冲突的缓解策略（分片键/队列化）。

### 3.4 缓存层（Redis）

- **模式**：旁路缓存（Cache-Aside）；写策略（失效/更新/双写）；TTL 与打散。
- **治理**：穿透/击穿/雪崩处理；热点 Key 识别与降载策略。

### 3.5 消息与一致性（Messaging & Consistency）

- **消息系统**：SQS/Kafka 其一（按实际选择）。
- **Outbox 模式**：本地事务写业务表 + outbox 表；异步转发到 MQ；消费者侧幂等。
- **重试与顺序**：指数退避、死信队列；分区键/消息键保障有序性（必要时）。

### 3.6 对象存储与搜索（S3 / Search）

- 大对象/附件走 S3；内容检索用 OpenSearch/ES（如确有需要）。

## 4. 环境与平台（Environments & Platform）

- **生产与预发**：Prod / Stage / Dev；只读凭证与最低权限访问。
- **计算平台**：
  - 生产：ECS/Fargate 或 EKS（二选一，按实际填写）。
  - 内部演练：EKS + IRSA + HPA + ADOT → AMP/Grafana + Chaos Mesh（验证策略与运行手册）。
- **配置管理**：ConfigMap/Secrets 或 SSM/Secrets Manager；按环境覆写。

## 5. 交付与发布（CI/CD & Releases）

- **CI**：GitHub Actions（OIDC AssumeRole）→ 构建/测试/扫描（如 CodeQL/Trivy）。
- **CD**：分环境部署（Helm/Task Definition）；预发验证、健康检查与门禁。
- **发布策略**：金丝雀/蓝绿；影子流量（如适用）；回滚开关与数据兼容策略。
- **IaC**：Terraform/CDK 管理基础设施，变更评审与最小权限访问。

## 6. 可观测与可靠性（Observability & Reliability）

- **OTel**：统一埋点，TraceID 贯穿；关键指标（可用性、P95/错误率、队列堆积等）。
- **日志**：结构化日志；敏感信息脱敏；按租户/请求聚合。
- **追踪**：入口/外呼 span；采样策略；跨服务链路可视化。
- **告警与 SLO**：以用户体验为中心设定 SLI/SLO；告警分级与抑制；值班与升级路径。
- **容量与伸缩**：HPA/VPA 或 ECS Auto Scaling；PDB 与中断处置；压测基线与应急预案。
- **演练**：故障注入与演练清单（pod‑kill/网络延迟/依赖降级），以运行手册收敛到“可回滚、可观测、可定位”。

## 7. 安全（Security）

- **身份与权限**：IRSA（EKS）或 Task Role（ECS）；最小权限（Principle of Least Privilege）。
- **密钥与配置**：KMS 加密；Secrets Manager/External Secrets；密钥轮换与审计。
- **网络与通信**：VPC 分层、Security Group/NetworkPolicy；TLS；CORS/CSP（前端）。
- **数据治理**：PII/敏感字段标注与脱敏；审计日志与访问留痕。

## 8. 前端集成（Frontend Integration）

- **框架与渲染**：Astro + React + TypeScript；SSR/CSR/选择性水合（按页面类型选用）。
- **契约**：BFF 或 API 契约优先；类型共享/SDK 生成（可选）。
- **性能与交付**：Asset 分割、懒加载、LQIP；Nginx/CDN 缓存策略；CSP。
- **可观测**：Sentry/前端埋点与后端 Trace 关联；统一错误码与排障链路。

## 9. 运行手册（Runbooks）

- **发布回滚**：
   1 - 预检（迁移/兼容/健康检查）→ 2 - 渐进放量/监测 → 3 - 触发回滚的阈值 → 4 - 回滚步骤与验证。
- **告警处置**：
  - API 错误率上升：检查近期发布/依赖可用性/下游限流；启用降级与熔断；查看追踪与热点日志。
  - 队列堆积：评估上游峰值/消费者速率；临时扩容或启用批处理；确认死信速率。
  - 数据延迟/不一致：回看 outbox/消费 offset；幂等补偿或对账。
- **故障沟通**：值班升级路径、跨团队协作与事后复盘模板（postmortem 模板链接）。

## 10. API 风格与错误码（Appendix）

- **命名与分页**：RESTful 命名；`GET /resources?page=&size=`；幂等 `PUT` 与安全 `GET`。
- **错误码**：统一结构 `{ code, message, traceId }`；可扩展错误域与本地化消息。
- **幂等等约定**：`Idempotency-Key`的来源与过期；去重键在消息侧的规则。

## 11. 数据与模型（Appendix）

- **核心实体**：TODO（以词汇表描述，不暴露机密字段）。
- **表关系**：ER 草图（可后补）。
- **索引策略**：主键、唯一键、覆盖索引与热点列注意事项。

## 12. 术语表（Glossary）

- OTel（OpenTelemetry）：可观测性开源标准框架。
- IRSA：IAM Roles for Service Accounts（K8s 最小权限）。
- Outbox：保证跨边界消息投递与幂等的常用模式。
- PDB（PodDisruptionBudget）：K8s 中断预算，控制可同时中断的副本数量。
