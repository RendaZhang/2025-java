<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Week 2 - Review of Spring Framework, Databases, and Caching](#week-2---review-of-spring-framework-databases-and-caching)
  - [Day 1 - Spring](#day-1---spring)
    - [🔍 Spring Core Summary (IOC & AOP)](#-spring-core-summary-ioc--aop)
  - [Day 2 – Spring Boot Basics, REST API & Observability](#day-2--spring-boot-basics-rest-api--observability)
    - [✅ Spring Boot Auto-configuration](#-spring-boot-auto-configuration)
    - [✅ RESTful CRUD for `Task`](#-restful-crud-for-task)
    - [✅ Service / Repository Layer](#-service--repository-layer)
    - [✅ Observability](#-observability)
  - [Day 3 – Integrating MySQL & Production-ready API](#day-3--integrating-mysql--production-ready-api)
    - [🔗 MySQL + Spring Data JPA](#-mysql--spring-data-jpa)
    - [🛠 DTO Mapping](#-dto-mapping)
    - [🔐 Security & Documentation](#-security--documentation)
    - [📊 Observability](#-observability)
  - [Day 4 – Spring Cloud Microservices Fundamentals](#day-4--spring-cloud-microservices-fundamentals)
    - [End-to-end Flow](#end-to-end-flow)
  - [Day 5 – MySQL Query‑Optimization Summary 🚀](#day-5--mysql-query%E2%80%91optimization-summary-)
    - [🔍 Indexing Strategy](#-indexing-strategy)
    - [🛠 Execution‑Plan Analysis](#-execution%E2%80%91plan-analysis)
    - [⚡️ Query‑Pattern Improvements](#%EF%B8%8F-query%E2%80%91pattern-improvements)
    - [📈 Measurable Gains](#-measurable-gains)
    - [Takeaways](#takeaways)
  - [Day 6 – Redis Caching Integration Summary 🚀](#day-6--redis-caching-integration-summary-)
    - [🔑 Key Redis Concepts Reviewed](#-key-redis-concepts-reviewed)
    - [🛠 Implementation Highlights](#-implementation-highlights)
    - [📈 Measured Performance](#-measured-performance)
    - [🧩 Lessons Learned](#-lessons-learned)
  - [Day 7 - Weekly Technical Log](#day-7---weekly-technical-log)
    - [🛠 Platform & Infrastructure](#-platform--infrastructure)
    - [⚙️ Core Features Delivered](#-core-features-delivered)
    - [📈 Outcomes & Lessons](#-outcomes--lessons)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Week 2 - Review of Spring Framework, Databases, and Caching

---

## Day 1 - Spring

### 🔍 Spring Core Summary (IOC & AOP)

**IOC (Inversion of Control)** is a fundamental concept in Spring Framework, where object creation and management are delegated to the Spring container. This is achieved using **Dependency Injection (DI)**. There are three main types of DI:

- Constructor Injection (preferred for immutability and testing)
- Setter Injection
- Field Injection

In our project, we implemented all three to demonstrate flexibility and clarity in wiring components.

**AOP (Aspect-Oriented Programming)** in Spring helps us modularize cross-cutting concerns such as logging, security, and transaction management.

We used the following annotations to implement AOP:

- `@Aspect` to define the aspect
- `@Before`, `@After`, `@Around` to handle method execution points
- `@Pointcut` to specify matched methods in the service layer

A logging aspect was created to track all service method calls and their execution times, improving observability and maintainability.

---

## Day 2 – Spring Boot Basics, REST API & Observability

Today I focused on **Spring Boot fundamentals** and built a production-ready REST API.

### ✅ Spring Boot Auto-configuration

Spring Boot uses conditional annotations (e.g. `@ConditionalOnClass`) under `@EnableAutoConfiguration` to scan the class-path and register context beans automatically, following the “Convention over Configuration” principle.

### ✅ RESTful CRUD for `Task`

- Built endpoints under `/api/tasks` with proper HTTP verbs.
- Returned **201 Created** with `Location` header on POST.
- Added paging and sorting (`page`, `size`, `sortBy`) and status filtering.

### ✅ Service / Repository Layer

- Leveraged **Spring Data JPA** for boilerplate-free persistence.
- Implemented dynamic queries (`findByStatus`, `findByStatusAndTitleContaining…`).
- Wrapped write operations in `@Transactional` while marking read methods `readOnly=true`.

### ✅ Observability

- Implemented **AOP-based logging** for both controller and service layers.
- Configured **Logback** to output to console and rolling files.
- Added structured error responses and a global exception handler.

Overall, the hands-on practice reinforced my understanding of Spring Boot’s opinionated setup and demonstrated how to create clean, maintainable, and observable REST services.

---

## Day 3 – Integrating MySQL & Production-ready API

Today I focused on deepening Spring Boot’s database integration.

### 🔗 MySQL + Spring Data JPA

- Configured HikariCP with a **10-connection pool** and explored `ddl-auto` modes (`update`, `validate`).
- Added a `Category` ↔ `Task` relationship (`@ManyToOne`/`@OneToMany`) enforcing a **unique category name**.

### 🛠 DTO Mapping

Introduced **MapStruct** to convert between entities and DTOs, preventing over-exposure of JPA entities and enabling a clean separation between persistence and presentation layers.

### 🔐 Security & Documentation

- Built layered `SecurityFilterChain`s: Swagger UI is publicly accessible, while `/api/**` requires HTTP Basic and returns **JSON 401** instead of browser pop-ups.
- Enabled **SpringDoc OpenAPI** at `/docs`, grouping endpoints and attaching global error schemas.

### 📊 Observability

- Implemented a `TraceIdFilter` that propagates an `X-Trace-Id` header and injects it into Logback with `%X{traceId}`.
- AOP logging now prints request/response details and method execution times for complete tracing.

All endpoints were validated with Postman, including edge cases such as unique-constraint violations and validation errors.

---

## Day 4 – Spring Cloud Microservices Fundamentals

| Component | Purpose | Key Takeaways |
|-----------|---------|---------------|
| **Eureka** | Service registry / discovery | TASK-MANAGER and USER-SERVICE instances register & self-heal |
| **Spring Cloud LoadBalancer** | Client-side load balancing | `@LoadBalanced RestTemplate` and Feign resolve `http://USER-SERVICE` |
| **Feign + Resilience4j** | Declarative HTTP client with circuit-breaking | Global fallback returns graceful degradation when USER-SERVICE is down |
| **Tracing & Observability** | `TraceIdFilter` + MDC + Feign full logging | Logs show traceId across inter-service calls |
| **Swagger Groups** | Separate docs per service group | Access via `/docs` with Basic Auth |

### End-to-end Flow

1. `task-manager` uses Feign `UserClient`
1. Feign asks LoadBalancer for a healthy USER-SERVICE instance from Eureka
1. If failures exceed threshold, Resilience4j opens the circuit and `GlobalFeignFallbackHandler` returns a fallback response.

This hands-on exercise solidified my understanding of distributed service discovery, client-side load balancing, and fault-tolerance patterns in Spring Cloud.

---

## Day 5 – MySQL Query‑Optimization Summary 🚀

**Key focus:** understanding indexes, reading execution plans, and speeding‑up the `task‑manager` DB queries.

### 🔍 Indexing Strategy

- Added **composite covering index** `idx_status_created_id (status, created_time DESC, id)` on **tasks**.
  \* Effect: pagination query

  ```sql
  SELECT id,title,status,created_time
  FROM tasks
  WHERE status='DONE'
  ORDER BY created_time DESC
  LIMIT 20;
  ```

  switched from `type = ALL`, `rows ≈ 50 000` to `type = range`, `rows ≈ 25` with `Extra = Using index` (no filesort).

- Ensured single‑column indexes on high‑cardinality fields:
  `category_id`, `created_time`, and `users.email`.

### 🛠 Execution‑Plan Analysis

- Used `EXPLAIN ANALYZE` and focused on:

  - **type** (avoided `ALL` scans),
  - **key / possible_keys** (verified actual index),
  - **rows × filtered** (estimated rows after filter),
  - **Extra** (`Using filesort / Using temporary` were eliminated).

- Converted sub‑query

  ```sql
  ...WHERE category_id IN (SELECT id FROM categories ...)
  ```

  to an **INNER JOIN**, dropping scanned rows from 30 k ➜ 90.

### ⚡️ Query‑Pattern Improvements

| Technique | Example & Result |
| ----------------------------- | ----------------------------------------------------------------------------------------------------------- |
| **Covering index** | `SELECT id,status,created_time …` – no table look‑ups |
| **Cursor/seek pagination** | `WHERE (created_time,id) < (?,?) ORDER BY created_time DESC LIMIT 20` – removed huge `LIMIT offset` cost |
| **Range instead of function** | Replaced `DATE(created_time)=?` with `BETWEEN '2025-05‑01 00:00:00' AND '05‑02 00:00:00'` – preserved index |
| **Slow‑log profiling** | Enabled `slow_query_log`, `long_query_time = 1 s`; worst call fell from **850 ms ➜ 35 ms** after indexing |

### 📈 Measurable Gains

| Endpoint | Before | After | Gain |
| --------------------------------------- | ------- | --------------------- | ---- |
| `/api/tasks?page=0&size=20&status=DONE` | 0.85 s | **35 ms** | ×24 |
| `/api/tasks?page=10000…` (offset) | timeout | **90 ms** with cursor | — |
| Category JOIN lookup | 320 ms | **18 ms** | ×17 |

### Takeaways

1. **Indexes pay off only when the query can use them** – avoid wildcards, functions, large offsets.
1. **Covering composite indexes** both filter *and* order data, killing filesorts.
1. **EXPLAIN ANALYZE** + slow‑log form a tight feedback loop for optimisation.
1. Even small schema tweaks (one composite index) can yield order‑of‑magnitude speed‑ups, directly boosting API responsiveness and scalability.

These optimisations leave the `task‑manager` micro‑services ready for higher traffic and set a solid foundation for future caching and sharding work.

---

## Day 6 – Redis Caching Integration Summary 🚀

Today I focused on introducing a high-performance **Redis** cache layer into the Spring-Boot-based micro-services.

---

### 🔑 Key Redis Concepts Reviewed

- **In-memory, single-threaded design** → sub-millisecond reads/writes.
- Data structures mastered: **String** (hot key/value), **Hash** (object fields), **List** (queue), **ZSet** (rank).
- Persistence & HA: **RDB / AOF** snapshots, master-replica with Sentinel.

---

### 🛠 Implementation Highlights

| Aspect | Approach |
| ----------------------- | -------------------------------------------------------------------------------------------- |
| **Dependency & Config** | `spring-boot-starter-data-redis` with Lettuce, pool 10/2; `EnableCaching` global switch. |
| **Read-through cache** | `@Cacheable(value="taskCache", key="#id", unless="#result==null")` on `TaskService.findOne`. |
| **Write consistency** | `@CacheEvict` on `update` & `delete` to prevent stale reads. |
| **TTL strategy** | 30 min ± random jitter to avoid **cache avalanche**. |
| **Cache penetration** | `unless="#result==null"` still caches empty result (short TTL) to block repeated misses. |
| **Cache breakdown** | Prepared Mutex lock (Redisson) for future hot-key protection; logical-expiry pattern noted. |

---

### 📈 Measured Performance

| Scenario | Latency |
| ---------------------- | -------- |
| Cache disabled | 120 ms |
| First query (miss) | 115 ms |
| Subsequent query (hit) | **8 ms** |

Hit rate visible via `keyspace_hits / keyspace_misses` and application logs (“Cache hit for key 1”).

---

### 🧩 Lessons Learned

1. **Cover more than 95 % reads** with a disciplined read-through pattern; write-invalidate keeps data fresh.
1. Randomised TTL and small null-value caches effectively neutralise avalanche & penetration.
1. Even a single hot entity cached drops latency by an order of magnitude and cuts DB load dramatically.
1. Spring’s annotation-driven caching is fast to adopt, yet allows fine-grained tuning via custom `CacheManager`.

With Redis caching in place, the Task-Manager service set is ready to handle higher traffic while keeping database pressure low.

---

## Day 7 - Weekly Technical Log

This week I transformed a simple Spring-Boot application into a fully-fledged **micro-service playground** and, in the process, level-upped my back-end toolkit.

### 🛠 Platform & Infrastructure

- **Modularised** the code-base into eight Maven modules: *registry-server*, *gateway-server*, *task-manager*, *user-service* and a shared *common-lib* that contains DTOs, a Trace-ID logging filter and a Feign auth interceptor.
- Spun up **Docker Compose** for MySQL 8 and Redis 7, plus multi-instance `user-service` containers to visualise client-side load-balancing.
- Exposed a **Spring-Cloud-Gateway** (port 8888) that aggregates Swagger UIs and routes traffic to downstream services discovered through **Eureka**.

### ⚙️ Core Features Delivered

- Built CRUD REST APIs for **tasks**, **categories** and **users**, documented automatically via SpringDoc OpenAPI.
- Added **Feign + Spring Cloud LoadBalancer** for declarative service-to-service calls; wrapped them with **Resilience4j circuit-breakers** and a global fallback handler.
- Integrated **Redis read-through caching** (`@Cacheable`) on hot task look-ups, with 30-minute TTL and random jitter to mitigate cache avalanche. Cold query latency fell from 120 ms to **8 ms** on cache hit.
- Tuned MySQL with composite covering indexes (`status, created_time, id`) and rewrote sub-queries into joins; `EXPLAIN ANALYZE` shows row scans dropping from 50 k to 25 and filesort removed.
- Centralised observability: Logback now prints `[traceId]` for each request, propagated across Feign calls; rolling logs persist 30 days.

### 📈 Outcomes & Lessons

- **Service discovery + client-side LB** eliminates hard-coded URLs and scales horizontally with zero config.
- A single, well-designed covering index can yield **×24** performance gains—measure before optimising.
- Caching is powerful but fragile; mixing TTL randomisation, null-value caching and mutex locks prevents penetration, avalanche and breakdown.
- Writing a shared *common-lib* slashes duplicate code and keeps cross-cutting concerns (logging, error envelopes) consistent.
