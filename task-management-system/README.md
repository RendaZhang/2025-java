# 🧩 Task Management System - 微服务任务管理系统

> 这是一个基于 Spring Cloud 微服务架构构建的任务管理系统，具备注册发现、负载均衡、远程调用、认证鉴权、容错降级、统一响应封装、接口聚合文档等特性，适合作为中高级 Java 后端面试项目展示模板。

---

## 📁 项目结构

```
task-management-system/
├── gateway-server/         # 🔀 Spring Cloud Gateway 网关（统一入口 + Swagger聚合）
├── registry-server/        # 📘 Eureka 注册中心
├── task-manager/           # ✅ 主业务服务（任务管理 + Feign/RestTemplate调用）
├── user-service/           # 👤 用户服务（带安全保护，供调用测试用）
└── docker-compose.yml      # 🐳 启动 MySQL 数据库的容器配置
```

---

## 🧱 技术架构

| 功能领域         | 技术栈组件 |
|------------------|------------|
| 微服务注册发现   | Spring Cloud Netflix Eureka |
| API 网关         | Spring Cloud Gateway |
| 声明式远程调用   | OpenFeign |
| 客户端负载均衡   | Spring Cloud LoadBalancer |
| 容错降级机制     | Resilience4j CircuitBreaker |
| 安全认证         | Spring Security + Basic Auth |
| 认证统一配置     | 自定义认证组件（支持 Basic/Bearer/Custom） |
| 数据访问         | Spring Data JPA + MySQL |
| 接口文档         | SpringDoc OpenAPI + 聚合 Swagger |
| 异常处理         | GlobalExceptionHandler + CommonResponseDto |
| 日志管理         | Logback + traceId 支持 |
| 服务配置         | application.yml 多服务隔离配置 |

---

## 🔗 模块间调用链流程

以用户请求 `/api/calls/hello-user-feign` 为例：

```text
[Browser]
   ↓ ① HTTP GET /api/calls/hello-user-feign
[Gateway] (8888)
   ↓ ② 路由转发至 /task-manager/api/calls/hello-user-feign
[task-manager]
   ↓ ③ 使用 FeignClient 调用 user-service（附带认证头）
[user-service]
   ↓ ④ 返回 “Hello from user-service-1”
[task-manager]
   ↓ ⑤ 封装为统一响应 CommonResponseDto
[Gateway → 浏览器]
```

✅ 支持多个实例轮询调用（基于 Eureka 注册）

✅ 调用失败自动触发 fallback 并返回友好 JSON 错误结构

---

## 📄 响应格式（统一结构）

```json
// ✅ 成功响应
{
  "status": 200,
  "message": "Success",
  "data": "Hello from user-service-1"
}

// ❌ 失败响应（例如 Feign 调用失败）
{
  "status": 503,
  "message": "Service temporarily unavailable: ..."
}
```

---

## 🧪 接口测试与聚合文档

### 🔗 Swagger 地址（聚合展示）：

```
http://localhost:8888/swagger-ui.html
```

> 网关统一聚合 task-manager / user-service 的所有接口，支持切换服务查看。

---

## 🛡️ 安全认证（Basic Auth）

- `user-service` 启用 Basic Auth 保护 `/api/**`
- `task-manager` 中 RestTemplate / Feign 调用会自动附带认证头

统一配置于 `application.yml`：

```yaml
auth:
  services:
    user-service:
      type: basic
      username: renda
      password: password
```

✅ 还支持 bearer token、自定义 header 类型

---

## 🧩 RestTemplate 与 Feign 统一认证机制

内置拦截器自动根据服务名读取配置，附加认证头：

```java
// RestTemplate 调用（自动附带认证）
restTemplate.getForObject("http://user-service/api/users/hello", String.class);

// FeignClient 调用（自动附带认证）
userClient.hello();
```

无需在业务代码中显式处理认证逻辑。

---

## 🐳 本地运行（MySQL 容器）

```bash
docker compose up -d
```

- MySQL 端口：3306
- 用户名：root
- 密码：password
- 数据库名：task_db

---

## ✅ 快速启动顺序

1. 启动 `registry-server`：Eureka 控制台 http://localhost:8761
2. 启动 `user-service`（支持多实例注册：8082/8083）
3. 启动 `task-manager`（包含调用接口）
4. 启动 `gateway-server`：网关入口 http://localhost:8888

---

## 🎯 面试讲解建议

> “这个项目采用 Spring Cloud 架构搭建，包含服务注册发现、网关转发、认证鉴权、熔断降级、负载均衡、统一响应封装等模块，并整合了 RestTemplate 和 FeignClient 的通用认证机制，接口聚合到 Gateway 的 Swagger 页面中，展示了微服务架构在中型系统中的标准应用场景。”

---

## 🧠 后续可扩展方向

| 方向 | 描述 |
|------|------|
| ✅ Spring Cloud Config | 配置中心支持动态刷新 |
| ✅ JWT + 权限控制 | 使用 Spring Security 细粒度控制接口权限 |
| ✅ Gray release | 灰度发布 + Canary 流量控制 |
| ✅ Prometheus + Grafana | 服务监控告警与指标收集 |
| ✅ Docker Compose 多服务 | 多模块一键部署 |
| ✅ Kubernetes 部署 | Helm + Ingress 实现容器化交付

---

## 👨‍💻 作者

- 👤 **Renda Zhang**
- 🌐 [www.rendazhang.com](http://www.rendazhang.com)
- 📫 更多功能请参考项目文档或联系作者定制

---

> 如需源码 Demo 或部署模板，请联系作者。欢迎用于学习、展示、实战练习等用途！

```