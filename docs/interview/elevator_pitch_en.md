<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Elevator Pitch English](#elevator-pitch-english)
  - [Self Introduction](#self-introduction)
  - [API Design](#api-design)
  - [Slow queries & index misses](#slow-queries--index-misses)
  - [Message and Consistency](#message-and-consistency)
  - [Java Concurrency](#java-concurrency)
  - [Observability and Release](#observability-and-release)
  - [Kubernetes / Cloud-Native](#kubernetes--cloud-native)
  - [Full-Stack](#full-stack)

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

---

## Message and Consistency

How we guarantee eventual consistency with outbox and idempotent consumers?

**Script (≈60s)**
We guarantee eventual consistency by **collapsing strong guarantees locally** and making the rest **safe to retry**.
On the write side, the service uses an **Outbox pattern**: in one local transaction we both **update the business table** and **insert an outbox event**. If the transaction commits, the change and the event are durably recorded together. A background publisher or CDC then delivers the event to Kafka/SQS with **at-least-once** semantics.

On the read/apply side, consumers are **idempotent**. Each event carries an **eventId** and an **aggregateVersion**. The consumer first records `eventId` in a `processed_events` table (unique key), then applies the change using **idempotent writes**—for example, a **conditional update**
`UPDATE stock SET qty = qty - n WHERE sku = ? AND qty >= n`,
or an **UPSERT**. We also track `last_version(aggregateId)` so repeats are ignored and **gaps** trigger a **parking lot/DLQ** for safe redrive.

Delivery noise is handled by **exponential backoff with jitter**, small **retry budgets**, and **per-aggregate partitioning** to keep order without sacrificing throughput. Everything is observable: unified error shape plus **trace IDs** across logs and metrics.

We’ve used this in production: instead of fragile 2PC, **local atomicity + at-least-once delivery + idempotent consumers** gives us **effectively-once** outcomes—no double charges, no missed reservations—even during spikes.

**3 sound bites to emphasize**

* “**Outbox = data change and event in one local transaction.**”
* “**Idempotent consumers with eventId + version.**”
* “**Effectively-once > expensive end-to-end exactly-once.**”

---

## Java Concurrency

1-min Answer — Tuning ThreadPoolExecutor for bursts while protecting downstreams

**Script (≈60s)**
On peak traffic I treat the thread pool as a **back-pressure valve, not a warehouse**.
First, I **bulkhead by dependency**—inventory, payments, coupons each have their own bounded pool. For a typical IO-heavy caller I use something like: core ≈ CPU, max ≈ 4×CPU, and an **ArrayBlockingQueue** sized to my **waiting budget**. The rejection policy is **CallerRuns** or **Abort** so pressure propagates back instead of piling up.

Every downstream call has **hard timeouts** and the orchestration has a **global deadline**. With `CompletableFuture` I run branches on a **custom bounded executor**, add `orTimeout`, and if a key branch fails I **cancel siblings** and degrade gracefully.

**Retries are budgeted**—exponential backoff with jitter, ≤10% of success volume, and we honor `Retry-After`. When error rate spikes we flip the **circuit breaker**: no retries while open, only small probes in half-open.

We watch **active/max**, **queue fill**, **rejections**, and **task wait/exec P95**. If the queue trends up for minutes we **lower incoming RPS** or widen capacity; we don’t “fix latency” by just adding threads.
Finally, we avoid lock hot spots: short critical sections, `tryLock` with timeouts, and per-key striping when needed.

This keeps tail latency flat during bursts **without melting downstreams**.

**3 sound bites to emphasize**

* “**Bounded queue + CallerRuns/Abort = back-pressure, not backlog.**”
* “**Timeouts + deadline + cancellable fan-out** keep threads free.”
* “**Retry budgets + circuit breaker** turn storms into controlled drizzle.”

---

## Observability and Release

60s Sample — Incident Postmortem (spoken)：

**Context & impact.**

At **19:05** during a **5% canary** for the **Checkout API v3**, our **error rate hit 2.4%**—about **2× the baseline**—and **p95 latency** rose **60%**. The **guardrail** halted promotion and routed traffic back to **v2** within minutes.

**Detection.**

We were alerted by a **multi-window burn-rate** page at **\~3 minutes**, and used **exemplars** to jump from the Grafana p95 panel to a slow **trace**.

**Mitigation.**

We **closed the feature flag**, **stopped ramp-up**, enabled a **read-cache TTL bump**, and protected downstreams with **rate-limits**. **Error rate** dropped below **0.2%** and p95 normalized in **6 minutes**.

**Root cause.**

A schema change introduced a **new query pattern** without a **composite index**; it only surfaced under **real traffic mix**.

**Fix & prevention.**

We added the index, **backfilled**, re-ran the canary successfully, and updated the **runbook**: schema follows **expand → migrate → contract**, canary **gates on symptom metrics** (errors/latency/SLO), and CI now checks **query plans** for new access paths.

Fill-in Template（30–60 秒直读）：

- **Context & impact**: “At `time`, during `canary % / rollout type` for `service/version`, `metric` reached `value vs baseline`, **p95** increased `X%`. We `halted/froze/rolled back` to `stable version`.”
- **Detection**: “`Alert type` triggered in `N mins`; we used `exemplars/trace link` from `dashboard` to pinpoint `span/dep`.”
- **Mitigation**: “We `close flag/stop ramp/limit/degade`, `scale or cache tweak`; metrics recovered to `target` in `N mins`.”
- **Root cause**: “`Cause` (e.g., **missing index / timeout mismatch / retry storm**), visible only under `pattern`.”
- **Fix & prevention**: “We `code/db fix`, `re-canary result`; updated **runbook** and added `gate/CI check/playbook`. Schema changes follow **expand → migrate → contract**; canary **gates on symptom metrics**.”

3 Sound Bites（面试加分短句）：

1. “**We gate promotion on symptom metrics**—error rate and p95—not just CPU.”
2. “Schema changes follow **expand → migrate → contract**, so **rollback only reverts the app**.”
3. “**Multi-window burn-rate** keeps alerts actionable: fast window pages, slow window opens a ticket.”

> 先用上面的 **Sample** 朗读 1 次，再用 **Template** 换成最近一次真实问题/练习中的服务名和指标，各练 1 次，总计不超过 10 分钟。

---

## Kubernetes / Cloud-Native

Why HPA + probes + least-privilege keep reliability (≈60s)

**Polished sample (ready to read, ~120–140 words)**

“Reliability isn’t one switch. It’s three guardrails that work together.
First, **probes** control the traffic valve: `startup` protects cold start, `readiness` is the only gate to receive traffic, and `liveness` is for self-heal. This prevents fake health and gives us graceful drain during rollouts.
Second, **HPA** keeps capacity elastic. We pick metrics that match the workload—CPU for compute, queue depth or concurrency for I/O—and we configure **‘fast up, slow down’** with stabilization windows so scaling doesn’t oscillate.
Third, **least-privilege** limits the blast radius. With RBAC and IRSA we bind each workload to a minimal role—no static keys, auditable in CloudTrail.
Together, these three turn incidents into contained events: traffic only hits warm pods, capacity grows before tail latency explodes, and any misuse is fenced by policy and observable end-to-end.”

Fill-in template

“At `team/service`, reliability = **probes + HPA + least-privilege**.
**Probes**: `startup` shields cold start, `readiness` is the only traffic gate, `liveness` restarts only when necessary.
**HPA**: metric = `CPU/queue depth/concurrency`, target `value`, behavior **fast up / slow down** with `window`.
**Least-privilege**: RBAC + `IRSA/OIDC`, role scoped to `namespace/SA`, actions `verbs` on `ARN/prefix`; no static keys, full audit.
Together they keep `tail latency/SLO` stable even during `rollouts/peaks`.”

3 sound bites

- “**Readiness is the only traffic gate.** Liveness is for self-heal.”
- “HPA is **fast up, slow down** with metrics that match the workload.”
- “**Least-privilege fences the blast radius**, and CloudTrail lets us prove it.”

---

## Full-Stack

60-second Script (ready to read)

“**We pick rendering modes by metrics, not preference.**

For content or SEO pages, we use **SSR—often streaming with Suspense**—to cut **TTFB/LCP**. For heavy dashboards, we stay **CSR** to keep client-side interactivity simple. For mixed pages, we adopt **selective hydration**: render most HTML on the server, then hydrate only the **interactive islands** like forms and charts, so **TTI/INP** stays low.

Performance and safety ship together: **fingerprinted static assets** get long-cache; **HTML uses SWR** so the CDN can serve stale while refreshing. **CSP with a nonce** locks scripts down—no secrets baked into the page.

On reliability, the browser **propagates trace IDs** to the backend; **Sentry** captures errors and performance spans, so we can jump from a slow page to the exact backend trace. During rollouts we flip modes **per route** and rollback by switching templates, with assets immutable. In short: **server for first paint, islands for interaction, and guardrails for safety and observability**.”

Fill-in Template (30–60s)

“At `team/app`, we choose **SSR / CSR / selective hydration** by `metrics: TTFB/LCP/TTI/INP`.

- **SSR (streaming)** for `content/SEO pages` to improve `TTFB/LCP`.
- **CSR** for `dashboards/heavy interactions`.
- **Selective hydration** so only `forms/charts/widgets` hydrate, keeping `TTI/INP` low.

We cache **fingerprinted assets** long-term and serve **HTML with SWR**. **CSP + nonce** secures scripts; **no secrets in the bundle**.

We **propagate trace IDs** and use **Sentry** for errors and spans, letting us jump from `p95 panel` to the backend trace.

For rollouts, we **toggle by route** and rollback via **template switch**, with immutable assets.”

3 Sound Bites

- “**Server for first paint, islands for interaction.**”
- “**Pick SSR/CSR by TTFB, LCP, and TTI—not taste.**”
- “**CSP + SWR + trace IDs = fast, safe, debuggable.**”
