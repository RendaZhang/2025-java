<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [OpenTelemetry 分布式可观测性实战：统一传播、Exemplars 关联与 Collector 网关化](#opentelemetry-%E5%88%86%E5%B8%83%E5%BC%8F%E5%8F%AF%E8%A7%82%E6%B5%8B%E6%80%A7%E5%AE%9E%E6%88%98%E7%BB%9F%E4%B8%80%E4%BC%A0%E6%92%ADexemplars-%E5%85%B3%E8%81%94%E4%B8%8E-collector-%E7%BD%91%E5%85%B3%E5%8C%96)
  - [一句话总览](#%E4%B8%80%E5%8F%A5%E8%AF%9D%E6%80%BB%E8%A7%88)
  - [核心概念卡片](#%E6%A0%B8%E5%BF%83%E6%A6%82%E5%BF%B5%E5%8D%A1%E7%89%87)
  - [端到端通路（ASCII 拓扑）](#%E7%AB%AF%E5%88%B0%E7%AB%AF%E9%80%9A%E8%B7%AFascii-%E6%8B%93%E6%89%91)
  - [指标与追踪的正确关联](#%E6%8C%87%E6%A0%87%E4%B8%8E%E8%BF%BD%E8%B8%AA%E7%9A%84%E6%AD%A3%E7%A1%AE%E5%85%B3%E8%81%94)
  - [Spring Boot 最小落地清单](#spring-boot-%E6%9C%80%E5%B0%8F%E8%90%BD%E5%9C%B0%E6%B8%85%E5%8D%95)
  - [OTel Collector → AMP / APM 配置示例（最小网关）](#otel-collector-%E2%86%92-amp--apm-%E9%85%8D%E7%BD%AE%E7%A4%BA%E4%BE%8B%E6%9C%80%E5%B0%8F%E7%BD%91%E5%85%B3)
  - [Grafana 设置要点](#grafana-%E8%AE%BE%E7%BD%AE%E8%A6%81%E7%82%B9)
  - [谁生成 TraceID（网关/客户端/应用）](#%E8%B0%81%E7%94%9F%E6%88%90-traceid%E7%BD%91%E5%85%B3%E5%AE%A2%E6%88%B7%E7%AB%AF%E5%BA%94%E7%94%A8)
  - [异步、MQ、批处理要点](#%E5%BC%82%E6%AD%A5mq%E6%89%B9%E5%A4%84%E7%90%86%E8%A6%81%E7%82%B9)
  - [最佳实践速记](#%E6%9C%80%E4%BD%B3%E5%AE%9E%E8%B7%B5%E9%80%9F%E8%AE%B0)
  - [快速排错清单](#%E5%BF%AB%E9%80%9F%E6%8E%92%E9%94%99%E6%B8%85%E5%8D%95)
  - [常用资源属性（Resource Attributes）](#%E5%B8%B8%E7%94%A8%E8%B5%84%E6%BA%90%E5%B1%9E%E6%80%A7resource-attributes)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# OpenTelemetry 分布式可观测性实战：统一传播、Exemplars 关联与 Collector 网关化

## 一句话总览

- **拼接同一条链路**靠三件套：**统一数据模型**（Trace/Span/Attributes）、**统一上下文传播**（W3C Trace Context）和**Collector 中枢**（多协议接收、加工、转发）。
- **Trace 与 Metrics 通路分离**：Trace 走 **OTLP → Collector → APM**；Metrics 走 **Micrometer/Prometheus → Collector → AMP**。
- **指标↔追踪关联**不要把 `trace_id` 当指标标签，而用 **Exemplars** 或 **spanmetrics 聚合**。

## 核心概念卡片

**数据模型**：Trace（端到端调用）、Span（一步）、Attributes/Events/Links（元数据、时点事件、跨链路关联）。
**上下文传播**：W3C Trace Context（`traceparent`/`tracestate`；gRPC metadata），入站提取、出站注入；Baggage 用于跨服务业务键值。
**Collector 中枢**：Receivers（otlp/prometheus/zipkin/jaeger/xray…）、Processors（batch/tail_sampling/spanmetrics/脱敏）、Exporters（otlp、prometheusremotewrite…），部署形态支持 Sidecar/DaemonSet/网关。

## 端到端通路（ASCII 拓扑）

```
Browser/Mobile ──(traceparent)──▶ API Gateway/Envoy ──▶ Service A (root span)
                                              │             └─ DB / Cache / MQ
                                              └───────────▶ Service B
                                                                      └─ Batch Worker (span links)

Traces:  SDK → OTLP → OTel Collector → [tail-sampling/redact] → APM(Tempo/Jaeger/厂商)
Metrics: Micrometer(/actuator/prometheus) → Collector prometheus receiver → AMP(remote_write)
Grafana: AMP 指标源 + Trace 数据源 → Exemplars/Links 一键从图跳到该 Trace
```

## 指标与追踪的正确关联

**路 A（推荐）— Exemplars**

- 有**活动 Span**且使用**直方图类指标**（如 `http.server.requests` duration）时，`micrometer-tracing-bridge-otel` 会把当前 `trace_id` 作为 **exemplar** 附在样本桶上。
- Collector/AMP 保留 exemplars；Grafana 开启 exemplars + 配置 Trace 数据源后，图上出现可点击“小点”，直达对应 Trace。
- **优势**：不引入高基数标签，精准定位“当次请求”。

**路 B — spanmetrics 聚合**

- Collector 从 Trace 流聚合 **RED** 指标（Rate/Errors/Duration），打上低基数标签（`service.name`、`http.route`…），导出到 AMP。
- Grafana 指标面板配置数据链接到 Trace 数据源，按 service/route/time 跳转。
- **适合**：SLO/总体态势；非逐条精确跳转。

> 反例：**不要**把 `trace_id`/`span_id` 作为 Prom 指标标签（高基数会拖垮采集与查询）。

## Spring Boot 最小落地清单

**依赖（Maven 片段）**

```xml
<!-- 指标暴露为 Prometheus -->
<dependency>
  <groupId>io.micrometer</groupId><artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<!-- Tracing 桥接到 OpenTelemetry（示例、MDC、exemplars 感知） -->
<dependency>
  <groupId>io.micrometer</groupId><artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>

<!-- OTel OTLP 导出器（若应用需直发 Collector 的 traces） -->
<dependency>
  <groupId>io.opentelemetry</groupId><artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>

<!-- Actuator 暴露 /actuator/prometheus -->
<dependency>
  <groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**应用配置（`application.yml`）**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,info
  tracing:
    sampling:
      probability: 1.0     # 演示期可全采；生产配合 Collector tail_sampling
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true  # 直方图→可挂 exemplars
    tags:
      service: task-api

logging:
  pattern:
    level: "%5p [traceId=%X{traceId:-},spanId=%X{spanId:-}]"
```

**Java Agent（零侵入自动注入 span）**

```bash
java \
 -javaagent:/opt/otel/opentelemetry-javaagent.jar \
 -Dotel.service.name=task-api \
 -Dotel.exporter.otlp.endpoint=http://otel-collector:4317 \
 -Dotel.resource.attributes=deployment.environment=prod \
 -jar app.jar
```

> Agent 通过字节码插桩：入站开 server span、出站开 client span、自动注入/提取 headers、记录异常并结束 span。

**关键业务的少量手动补点（可选）**

```java
Tracer tracer = GlobalOpenTelemetry.get().getTracer("task-api");
Span span = tracer.spanBuilder("Checkout")
    .setAttribute("order.id", orderId)
    .startSpan();
try (Scope s = span.makeCurrent()) {
    service.chargePayment(); // 出站 HTTP/DB/MQ 会被 Agent 自动挂子 span 并注入传播头
} catch (Exception e) {
    span.recordException(e);
    span.setStatus(StatusCode.ERROR);
    throw e;
} finally {
    span.end();
}
```

## OTel Collector → AMP / APM 配置示例（最小网关）

```yaml
receivers:
  otlp:
    protocols: { grpc: {}, http: {} }        # 接 traces（也可接 metrics/logs）
  prometheus:
    config:
      scrape_configs:
        - job_name: 'k8s-apps'
          kubernetes_sd_configs: [{ role: pod }]
          relabel_configs:
            # 仅抓取目标命名空间/带特定 label 的 Pod，示例略

processors:
  batch: {}
  spanmetrics:
    metrics_exporter: prometheus
    latency_histogram_buckets: [50ms, 100ms, 250ms, 500ms, 1s, 2s]

exporters:
  prometheusremotewrite:
    endpoint: https://aps-workspaces.${REGION}.amazonaws.com/workspaces/${WSID}/api/v1/remote_write
  prometheus: {}
  otlp/apm:
    endpoint: tempo:4317   # 或 Jaeger/X-Ray/厂商 APM

extensions:
  sigv4auth:
    region: ${REGION}

service:
  extensions: [sigv4auth]
  pipelines:
    metrics:
      receivers: [prometheus]
      processors: [batch]
      exporters: [prometheusremotewrite]
    traces:
      receivers: [otlp]
      processors: [batch]   # 生产建议加 tail_sampling
      exporters: [otlp/apm]
    metrics/spanmetrics:
      receivers: []
      processors: []
      exporters: [prometheus]
```

## Grafana 设置要点

- **数据源**：新增 **Prometheus（AMP）** 并开启 **Exemplars**；新增 **Trace** 数据源（Tempo/Jaeger/X-Ray/厂商 APM）。
- **面板**：使用 `http.server.requests` 直方图/百分位，启用 Exemplars；若走 spanmetrics，在 **Data links** 配置到 Trace 数据源的跳转模板（按 service/route/time）。

## 谁生成 TraceID（网关/客户端/应用）

- **首跳且无上游 `traceparent` 的组件**生成 TraceID：浏览器 SDK、移动端、API 网关（Envoy/Nginx/SCG）或首个应用均可。
- 关键是**正确传播**（透传/注入/提取），而非“必须在网关生成”。

## 异步、MQ、批处理要点

- 在消息属性里**透传上下文**；消费者侧**提取**或使用 **Span Links** 关联多个上游。
- 批处理：将每条输入记录的上游 trace 以 **link** 关联到当前 batch span。
- 常见断点：MQ 丢头、消费者不提取、消费侧误开新根 span 而未做 links。

## 最佳实践速记

**Do**

- 统一 **W3C Trace Context**，Collector 处理与 B3/Jaeger/X-Ray 的互转。
- 标准化 `service.name` / `service.namespace` / `deployment.environment`。
- **Exemplars** +（可选）**spanmetrics** 搭桥指标↔追踪。
- Collector 网关化：**尾采样**（慢/错优先）、脱敏、限流、集中密钥。
- 日志注入 `traceId`/`spanId`（MDC），实现“日志↔Trace”互跳。
- 关键业务用少量**手动 Span**补语义。

**Don’t**

- 不要把 `trace_id`/`span_id` 当 Prom 标签。
- 不要各自发明字段名（请遵循 **Semantic Conventions**）。
- 不要让 SDK 直连公网 APM（统一走 Collector）。

## 快速排错清单

- **传播**：入站是否有 `traceparent`？中间层是否正确透传/注入/提取？
- **视图断裂**：缺 Span 的库/协议是否被 Agent 支持？需不需要额外 instrumentation？
- **Exemplars 不显示**：是否直方图指标？是否存在活动 Span？Grafana exemplars 与 Trace 源是否开启？
- **高费用/爆表**：是否把高基数字段当标签？是否全量采样且未做尾采样？
- **K8s/EKS**：Collector IRSA 权限、AMP remote_write Endpoint/Region/WorkspaceID 是否正确？
- **时间**：应用/Collector/NTP 时间是否同步（影响查询窗口）？

## 常用资源属性（Resource Attributes）

`service.name`、`service.namespace`、`service.instance.id`、`deployment.environment`（dev/stage/prod）、`k8s.namespace.name`、`k8s.pod.name`、`k8s.container.name`、`cloud.provider`、`cloud.region`、`cloud.account.id`
