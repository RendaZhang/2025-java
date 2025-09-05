<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 5 - 稳态与弹性：探针 + 资源配额 + HPA 触发与回落](#day-5---%E7%A8%B3%E6%80%81%E4%B8%8E%E5%BC%B9%E6%80%A7%E6%8E%A2%E9%92%88--%E8%B5%84%E6%BA%90%E9%85%8D%E9%A2%9D--hpa-%E8%A7%A6%E5%8F%91%E4%B8%8E%E5%9B%9E%E8%90%BD)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 5 - 稳态与弹性：探针 + 资源配额 + HPA 触发与回落

## 今日目标

1. 给 `task-api` 补齐/校准 **健康探针**（startup/readiness/liveness 指向 Actuator 子端点），确保发布与自愈更稳。
2. 设定**资源 requests/limits**（CPU/内存）并**校准 HPA**（CPU-based，最小改动），保证在轻量压测下能**可预期地扩容**，压测结束后**平滑回落**。
3. 通过 Grafana（QPS/错误率/P95）与 `kubectl describe hpa` 记录一次**扩容→稳定→回落**的完整时间线（含 t0/t1/t2/t3）。

---
