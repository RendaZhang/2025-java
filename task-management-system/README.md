# 🧩 Task Management System - Spring Boot 微服务项目

> 一个基于 Spring Cloud 微服务架构的任务管理系统，集成服务注册发现、远程调用、容错降级、统一响应格式与接口文档展示。支持多实例负载均衡与容灾回退，适合作为面试展示或企业项目模板。

---

## 🧱 项目结构

```
task-management-system/
├── task-manager/         # 主业务服务（管理任务与分类）
├── user-service/         # 用户服务（提供示例接口用于调用与容错演示）
├── registry-server/      # Eureka 注册中心
└── docker-compose.yml    # 本地数据库容器管理（MySQL）
```

---

## 🚀 技术栈

| 技术方向 | 使用框架 |
|----------|-----------|
| 服务注册与发现 | Spring Cloud Netflix Eureka |
| 远程调用 | Spring Cloud OpenFeign |
| 负载均衡 | Spring Cloud LoadBalancer |
| 容错处理 | Resilience4j（熔断 + fallback） |
| 持久化 | Spring Data JPA + MySQL |
| 接口文档 | SpringDoc OpenAPI + Swagger UI |
| 异常处理 | GlobalExceptionHandler + CommonResponseDto |
| 统一响应格式 | CommonResponseDto<T> |
| 日志管理 | Logback（可支持 traceId） |
| 部署管理 | Docker Compose 管理 MySQL 服务 |

---

## 📦 模块功能概览

### ✅ task-manager

- 提供任务/分类管理的 CRUD 接口
- 提供 `/api/calls/hello-user-feign` 接口用于调用 `user-service`（通过 Feign 实现）
- 对调用失败支持自动 fallback，返回统一错误结构

### ✅ user-service

- 模拟用户服务，提供 `/api/users/hello` 接口
- 支持多实例注册到 Eureka，用于演示负载均衡

### ✅ registry-server

- Spring Cloud Eureka Server
- 提供 http://localhost:8761 控制台查看所有服务实例状态

---

## 🔁 调用链演示：Feign + 熔断 + fallback

1. 用户访问 `GET /api/calls/hello-user-feign`
2. `task-manager` 使用 Feign 调用 `user-service`
3. Feign 请求经过 Spring Cloud LoadBalancer 实现轮询
4. 如果 `user-service` 实例不可用，自动触发 fallback：
   - 进入 `GlobalFeignFallbackHandler`
   - 返回统一结构的 JSON 错误响应

---

## 📄 响应格式（统一封装）

#### ✅ 成功响应

```json
{
  "status": 200,
  "message": "Success",
  "data": "Hello from user-service-1"
}
```

#### ❌ 错误响应

```json
{
  "status": 503,
  "message": "Service temporarily unavailable: Load balancer does not contain an instance for the service user-service",
  "data": null
}
```

---

## 📚 接口文档 (Swagger UI)

> ✅ 自动根据代码生成接口文档

访问地址：[http://localhost:8080/docs](http://localhost:8080/docs)

- 所有接口自动包含 400/404/500 错误结构
- 支持 Authorize 按钮（可集成 Basic Auth 或 JWT）
- 所有接口响应与错误结构统一标准展示

---

## 🐳 本地启动 MySQL（Docker）

确保已安装 Docker，然后运行：

```bash
docker compose up -d
```

默认连接配置：

- Host: `localhost`
- Port: `3306`
- Username: `root`
- Password: `password`
- Database: `task_db`

---

## 🧪 本地多实例测试

1. 启动两个 `user-service` 实例（分别使用端口 8082、8083）：

```
-Dserver.port=8082 -DINSTANCE_ID=user-service-1
-Dserver.port=8083 -DINSTANCE_ID=user-service-2
```

2. 访问 `/api/calls/hello-user-feign` 多次，观察轮询结果
3. 关闭其中一个实例，观察 fallback 自动触发

---

## ✅ 异常统一处理（GlobalExceptionHandler）

支持捕获并统一响应：

- 参数校验失败（MethodArgumentNotValidException）
- 实体未找到（EntityNotFoundException）
- 数据库约束冲突（DataIntegrityViolationException）
- 未知异常（Exception）

---

## 🎯 项目亮点

- 全链路负载均衡 + fallback
- 统一响应格式 `CommonResponseDto<T>`
- 响应结构兼容 Swagger 展示
- 优雅日志与异常结构
- 完善模块分层架构
- 高度工程化标准，适合面试展示与实际部署

---

## 🧠 可扩展建议

| 功能 | 说明 |
|------|------|
| Gateway 接入 | 使用 Spring Cloud Gateway 聚合路由 |
| Redis 缓存 | 支持高频读场景（如任务列表缓存） |
| 鉴权支持 | 集成 Spring Security + JWT |
| 云部署 | 将整个系统部署至云服务器（阿里云/腾讯云） |
| 微服务治理 | 接入 Micrometer + Prometheus + Grafana 实现监控告警 |

---

## 👨‍💻 作者

- 👤 **Renda Zhang**
- 🌐 个人主页：[www.rendazhang.com](http://www.rendazhang.com)
- 📬 联系方式：_可添加_

---

> 本项目为 Spring Cloud 微服务架构学习与面试展示示例。支持完整本地部署、多实例注册、熔断降级、统一响应封装、Swagger 文档等常用能力。可作为 Java 后端开发工程师中高级面试项目模板参考。