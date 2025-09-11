<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Elevator Pitch English](#elevator-pitch-english)
  - [Self Introduction](#self-introduction)
  - [API Design](#api-design)
  - [Slow queries & index misses](#slow-queries--index-misses)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Elevator Pitch English

---

## Self Introduction

45s Elevator Pitch

**要点备忘**

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

---

## API Design

1-min Answer — “Why this API design?”

**要点备忘**

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

---

## Slow queries & index misses

How I diagnose slow queries & index misses

**Script (≈60s)**

When a request feels slow, I first check whether it’s database time or app/network time using the slow-query log and basic APM. If it’s DB time, I run **EXPLAIN—ideally EXPLAIN ANALYZE**—and look at four things: the **access type** (`ALL/range/ref/eq_ref`), the **chosen key**, the **estimated rows**, and **Extra** flags like *Using filesort*, *Using temporary*, or *Using index condition*.

If rows examined are high or I see filesort, I align the query with a **composite index** that matches the filter and sort, e.g., `(user_id, status, created_at DESC)`, and try to make the list view **covering** so we avoid table lookups. I also fix typical pitfalls: remove functions on columns (`DATE(created_at)` → range predicates), avoid implicit type casts, and replace large **OFFSET** pagination with **seek** pagination using `(created_at, id)`.

If the plan looks fine but latency remains, I check **locks and waits**—deadlocks, long transactions, or hot rows. The loop is: measure → explain → rewrite or index → re-measure. This usually brings P95 back to target and keeps the plan stable under load.

3 个强调点（说话时可加重语气）

* “EXPLAIN **ANALYZE** shows real timing, not just estimates.”
* “**Covering index** to avoid random I/O.”
* “**Seek pagination** instead of large OFFSET.”
