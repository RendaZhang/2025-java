<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Architecture](#architecture)
  - [概述（Purpose & Scope）](#%E6%A6%82%E8%BF%B0purpose--scope)
  - [总体架构（Overview）](#%E6%80%BB%E4%BD%93%E6%9E%B6%E6%9E%84overview)
  - [组件与设计要点（Components）](#%E7%BB%84%E4%BB%B6%E4%B8%8E%E8%AE%BE%E8%AE%A1%E8%A6%81%E7%82%B9components)
    - [网关与鉴权（Gateway & AuthN/Z）](#%E7%BD%91%E5%85%B3%E4%B8%8E%E9%89%B4%E6%9D%83gateway--authnz)
    - [服务层（Services）](#%E6%9C%8D%E5%8A%A1%E5%B1%82services)
    - [数据层（Database）](#%E6%95%B0%E6%8D%AE%E5%B1%82database)
    - [缓存层（Redis）](#%E7%BC%93%E5%AD%98%E5%B1%82redis)
    - [消息与一致性（Messaging & Consistency）](#%E6%B6%88%E6%81%AF%E4%B8%8E%E4%B8%80%E8%87%B4%E6%80%A7messaging--consistency)
    - [对象存储与搜索（S3 / Search）](#%E5%AF%B9%E8%B1%A1%E5%AD%98%E5%82%A8%E4%B8%8E%E6%90%9C%E7%B4%A2s3--search)
  - [环境与平台（Environments & Platform）](#%E7%8E%AF%E5%A2%83%E4%B8%8E%E5%B9%B3%E5%8F%B0environments--platform)
  - [可观测与发布](#%E5%8F%AF%E8%A7%82%E6%B5%8B%E4%B8%8E%E5%8F%91%E5%B8%83)
    - [TraceID 串联排障（4 步到位）](#traceid-%E4%B8%B2%E8%81%94%E6%8E%92%E9%9A%9C4-%E6%AD%A5%E5%88%B0%E4%BD%8D)
    - [看板读法（RED/USE）](#%E7%9C%8B%E6%9D%BF%E8%AF%BB%E6%B3%95reduse)
    - [SLI / SLO / 告警阈值](#sli--slo--%E5%91%8A%E8%AD%A6%E9%98%88%E5%80%BC)
    - [发布与回滚（灰度/蓝绿/滚动）](#%E5%8F%91%E5%B8%83%E4%B8%8E%E5%9B%9E%E6%BB%9A%E7%81%B0%E5%BA%A6%E8%93%9D%E7%BB%BF%E6%BB%9A%E5%8A%A8)
    - [Runbook（10 分钟止血）](#runbook10-%E5%88%86%E9%92%9F%E6%AD%A2%E8%A1%80)
    - [安全与合规](#%E5%AE%89%E5%85%A8%E4%B8%8E%E5%90%88%E8%A7%84)
    - [3 个常见告警场景与处置策略](#3-%E4%B8%AA%E5%B8%B8%E8%A7%81%E5%91%8A%E8%AD%A6%E5%9C%BA%E6%99%AF%E4%B8%8E%E5%A4%84%E7%BD%AE%E7%AD%96%E7%95%A5)
  - [安全（Security）](#%E5%AE%89%E5%85%A8security)
  - [前端集成（Frontend Integration）](#%E5%89%8D%E7%AB%AF%E9%9B%86%E6%88%90frontend-integration)
  - [运行手册（Runbooks）](#%E8%BF%90%E8%A1%8C%E6%89%8B%E5%86%8Crunbooks)
  - [API 风格与错误码（Appendix）](#api-%E9%A3%8E%E6%A0%BC%E4%B8%8E%E9%94%99%E8%AF%AF%E7%A0%81appendix)
  - [数据与模型（Appendix）](#%E6%95%B0%E6%8D%AE%E4%B8%8E%E6%A8%A1%E5%9E%8Bappendix)
  - [术语表（Glossary）](#%E6%9C%AF%E8%AF%AD%E8%A1%A8glossary)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Architecture

> 说明：本模板用于**围绕你最近工作里最相关的系统**做面试版架构文档（3–5 页）。
> 避免写具体业务数据，可在 `TODO` 处按需补充。
> 若生产在 **ECS/Fargate**，而方法论验证在 **EKS（内部演练）**，可在“环境与平台”中分别描述。

---

## 概述（Purpose & Scope）

- **系统名称**：TODO（例如「订单与库存中台」或以你当前职责命名）
- **目标**：说明系统要解决的核心问题、主要消费者/上下游、可靠性与交付的原则（不写数值）。
- **范围**：本文件仅覆盖：总体架构、关键组件、交付与发布、可观测与稳定性、安全、前端集成与运行手册。
- **非目标**：暂不包含详尽业务规则、报表细节、机密参数与企业级合规细目。

---

## 总体架构（Overview）

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

---

## 组件与设计要点（Components）

### 网关与鉴权（Gateway & AuthN/Z）

- 路由与限流：按路径/租户/用户维度；策略存放与灰度控制。
- 鉴权：OIDC/JWT；权限粒度（接口/资源/操作）。
- 错误码与幂等：统一错误码规范；`Idempotency-Key` 与幂等资源设计。

### 服务层（Services）

- **技术栈**：Java 17+/Spring Boot 3.x，分层（Controller/Service/Repo），关注可测试性与可观测性。
- **容错**：重试（指数退避 + 抖动）、超时、熔断、舱壁隔离、限流（令牌桶/漏桶）。
- **契约**：API 版本化（URL / Header / Accept），兼容性演进策略。

### 数据层（Database）

- **建模**：围绕核心实体（订单/库存等，按实际替换）；主键/唯一键/外键取舍。
- **读写分离**：写入主库，读取路由到只读副本（延迟与一致性策略）。
- **事务**：隔离级别选择；热点写冲突的缓解策略（分片键/队列化）。

### 缓存层（Redis）

- **模式**：旁路缓存（Cache-Aside）；写策略（失效/更新/双写）；TTL 与打散。
- **治理**：穿透/击穿/雪崩处理；热点 Key 识别与降载策略。

### 消息与一致性（Messaging & Consistency）

- **消息系统**：SQS/Kafka 其一（按实际选择）。
- **Outbox 模式**：本地事务写业务表 + outbox 表；异步转发到 MQ；消费者侧幂等。
- **重试与顺序**：指数退避、死信队列；分区键/消息键保障有序性（必要时）。

### 对象存储与搜索（S3 / Search）

- 大对象/附件走 S3；内容检索用 OpenSearch/ES（如确有需要）。

---

## 环境与平台（Environments & Platform）

- **生产与预发**：Prod / Stage / Dev；只读凭证与最低权限访问。
- **计算平台**：
  - 生产：ECS/Fargate 或 EKS（二选一，按实际填写）。
  - 内部演练：EKS + IRSA + HPA + ADOT → AMP/Grafana + Chaos Mesh（验证策略与运行手册）。
- **配置管理**：ConfigMap/Secrets 或 SSM/Secrets Manager；按环境覆写。

---

## 可观测与发布

- “我们用**分阶段放量 + 多窗口燃尽率**做闸门，触发即关开关或回滚，爆炸半径可控。”
- “**EMC 三段式数据库变更**让任何时刻都能**只回应用**安全退回上一稳定版本。”

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

交付与发布（CI/CD & Releases）

- **CI**：GitHub Actions（OIDC AssumeRole）→ 构建/测试/扫描（如 CodeQL/Trivy）。
- **CD**：分环境部署（Helm/Task Definition）；预发验证、健康检查与门禁。
- **发布策略**：金丝雀/蓝绿；影子流量（如适用）；回滚开关与数据兼容策略。
- **IaC**：Terraform/CDK 管理基础设施，变更评审与最小权限访问。

可观测与可靠性（Observability & Reliability）

- **OTel**：统一埋点，TraceID 贯穿；关键指标（可用性、P95/错误率、队列堆积等）。
- **日志**：结构化日志；敏感信息脱敏；按租户/请求聚合。
- **追踪**：入口/外呼 span；采样策略；跨服务链路可视化。
- **告警与 SLO**：以用户体验为中心设定 SLI/SLO；告警分级与抑制；值班与升级路径。
- **容量与伸缩**：HPA/VPA 或 ECS Auto Scaling；PDB 与中断处置；压测基线与应急预案。
- **演练**：故障注入与演练清单（pod‑kill/网络延迟/依赖降级），以运行手册收敛到“可回滚、可观测、可定位”。

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

### 3 个常见告警场景与处置策略

场景 1 - 5xx 错误率飙升（发布/金丝雀窗口）：

- **触发信号**
  - 5 分钟快窗：`error_rate > 2×基线 且 绝对值>1%` → 立即停放量/回滚
  - 30 分钟慢窗：SLO **burn rate** 超阈 → 冻结发布
- **止血动作（3 分钟内）**
  - 停止金丝雀放量/流量切回稳定版本；关闭相关**特性开关**
  - 入口**限流**，下游**熔断**；区分 4xx/5xx，避免“用户侧错误”误报
- **定位路径**
  - Grafana p95 面板 → **exemplars** → 慢 trace → 关联 **traceId** 查结构化日志
  - 看依赖面板（DB/Redis/外部 API）错误分布与时序是否同涨
- **回滚判定**
  - 快窗触发即回滚；若回滚后 10 分钟内错误率 < 0.2%、p95 回归，则恢复放量
- **复盘与加固**
  - 补“症状指标闸门”规则；把本次 root cause 写入 Runbook & 回归用例

场景 2 - p95 延迟暴涨但无明显 5xx（资源饱和/排队）：

- **触发信号**
  - p95 较基线 **+40%**；线程池/连接池占用或队列 `>80%`；GC/CPU 抬升
- **止血动作**
  - 临时**扩容**（HPA/副本数）、**提高池大小**并设置**排队上限**；
  - 降低重试/缩短超时（以 `P95×1.5` 级别设定），打开**读缓存**降压
- **定位路径**
  - Trace 看哪一段 span 膨胀（SQL、外呼、锁等待）；
  - DB 看慢查询、锁等待/死锁；JVM 看 GC 停顿与分配速率
- **回稳与复盘**
  - 指标回到阈值内持续 15 分钟再撤销临时扩容；
  - 优化索引/SQL 或热点分离；沉淀容量与超时/重试模板

场景 3 - 外部依赖超时 → 重试风暴放大故障：

- **触发信号**
  - 下游调用**超时率**升高；调用侧**重试次数**与请求量短时倍增；队列/连接等待拉长
- **止血动作**
  - **收紧重试**到“**重试预算 ≤10% + 抖动**”，仅对**幂等**请求保留有限重试
  - **快速熔断**（半开探测），必要时**降级**为静态/缓存结果
- **定位路径**
  - 对比客户端与服务端时序：是对方变慢还是网络抖动；核对双方**超时/重试**是否不匹配
- **预防与复盘**
  - 统一“超时/重试/幂等”策略；新增**下游延迟/错误**的 SLO 守门
  - 在发布窗口对关键依赖开启**更严的闸门与观察窗**

---

## 安全（Security）

- **身份与权限**：IRSA（EKS）或 Task Role（ECS）；最小权限（Principle of Least Privilege）。
- **密钥与配置**：KMS 加密；Secrets Manager/External Secrets；密钥轮换与审计。
- **网络与通信**：VPC 分层、Security Group/NetworkPolicy；TLS；CORS/CSP（前端）。
- **数据治理**：PII/敏感字段标注与脱敏；审计日志与访问留痕。

---

## 前端集成（Frontend Integration）

- **框架与渲染**：Astro + React + TypeScript；SSR/CSR/选择性水合（按页面类型选用）。
- **契约**：BFF 或 API 契约优先；类型共享/SDK 生成（可选）。
- **性能与交付**：Asset 分割、懒加载、LQIP；Nginx/CDN 缓存策略；CSP。
- **可观测**：Sentry/前端埋点与后端 Trace 关联；统一错误码与排障链路。

---

## 运行手册（Runbooks）

- **发布回滚**：
   1 - 预检（迁移/兼容/健康检查）→ 2 - 渐进放量/监测 → 3 - 触发回滚的阈值 → 4 - 回滚步骤与验证。
- **告警处置**：
  - API 错误率上升：检查近期发布/依赖可用性/下游限流；启用降级与熔断；查看追踪与热点日志。
  - 队列堆积：评估上游峰值/消费者速率；临时扩容或启用批处理；确认死信速率。
  - 数据延迟/不一致：回看 outbox/消费 offset；幂等补偿或对账。
- **故障沟通**：值班升级路径、跨团队协作与事后复盘模板（postmortem 模板链接）。

---

## API 风格与错误码（Appendix）

- **命名与分页**：RESTful 命名；`GET /resources?page=&size=`；幂等 `PUT` 与安全 `GET`。
- **错误码**：统一结构 `{ code, message, traceId }`；可扩展错误域与本地化消息。
- **幂等等约定**：`Idempotency-Key`的来源与过期；去重键在消息侧的规则。

---

## 数据与模型（Appendix）

- **核心实体**：TODO（以词汇表描述，不暴露机密字段）。
- **表关系**：ER 草图（可后补）。
- **索引策略**：主键、唯一键、覆盖索引与热点列注意事项。

---

## 术语表（Glossary）

- OTel（OpenTelemetry）：可观测性开源标准框架。
- IRSA：IAM Roles for Service Accounts（K8s 最小权限）。
- Outbox：保证跨边界消息投递与幂等的常用模式。
- PDB（PodDisruptionBudget）：K8s 中断预算，控制可同时中断的副本数量。
