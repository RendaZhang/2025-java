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
* Built endpoints under `/api/tasks` with proper HTTP verbs.
* Returned **201 Created** with `Location` header on POST.
* Added paging and sorting (`page`, `size`, `sortBy`) and status filtering.

### ✅ Service / Repository Layer
* Leveraged **Spring Data JPA** for boilerplate-free persistence.
* Implemented dynamic queries (`findByStatus`, `findByStatusAndTitleContaining…`).
* Wrapped write operations in `@Transactional` while marking read methods `readOnly=true`.

### ✅ Observability
* Implemented **AOP-based logging** for both controller and service layers.
* Configured **Logback** to output to console and rolling files.
* Added structured error responses and a global exception handler.

Overall, the hands-on practice reinforced my understanding of Spring Boot’s opinionated setup and demonstrated how to create clean, maintainable, and observable REST services.

---

## Day 3 – Integrating MySQL & Production-ready API

Today I focused on deepening Spring Boot’s database integration.

### 🔗 MySQL + Spring Data JPA
* Configured HikariCP with a **10-connection pool** and explored `ddl-auto` modes (`update`, `validate`).
* Added a `Category` ↔ `Task` relationship (`@ManyToOne`/`@OneToMany`) enforcing a **unique category name**.

### 🛠 DTO Mapping
Introduced **MapStruct** to convert between entities and DTOs, preventing over-exposure of JPA entities and enabling a clean separation between persistence and presentation layers.

### 🔐 Security & Documentation
* Built layered `SecurityFilterChain`s: Swagger UI is publicly accessible, while `/api/**` requires HTTP Basic and returns **JSON 401** instead of browser pop-ups.
* Enabled **SpringDoc OpenAPI** at `/docs`, grouping endpoints and attaching global error schemas.

### 📊 Observability
* Implemented a `TraceIdFilter` that propagates an `X-Trace-Id` header and injects it into Logback with `%X{traceId}`.
* AOP logging now prints request/response details and method execution times for complete tracing.

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
2. Feign asks LoadBalancer for a healthy USER-SERVICE instance from Eureka
3. If failures exceed threshold, Resilience4j opens the circuit and `GlobalFeignFallbackHandler` returns a fallback response.

This hands-on exercise solidified my understanding of distributed service discovery, client-side load balancing, and fault-tolerance patterns in Spring Cloud.

---