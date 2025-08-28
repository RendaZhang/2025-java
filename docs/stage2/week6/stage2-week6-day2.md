<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 2 - ADOT Collector（采集 → AMP）+ 成本护栏](#day-2---adot-collector%E9%87%87%E9%9B%86-%E2%86%92-amp-%E6%88%90%E6%9C%AC%E6%8A%A4%E6%A0%8F)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

太棒了，收到你的偏好。我把 **Day 2** 的方案按“最小必要 + Terraform 做 IRSA + 收尾再讨论是否并入每日流程”的思路重新整理好了。

# Day 2 - ADOT Collector（采集 → AMP）+ 成本护栏

## 今日目标

1. 在 `observability` 命名空间部署 **ADOT Collector (Deployment 形态)**，只抓取 `svc-task / task-api:8080/actuator/prometheus`。
2. 使用 **Terraform** 创建并绑定 **IRSA**（仅授予 `AmazonPrometheusRemoteWriteAccess`），让 Collector 以 **SigV4** 向 **AMP（us-east-1, 你的 workspaceId）** 执行 `remote_write`。
3. 落地**成本护栏**：仅保留面试关键指标（HTTP 延迟直方图与 JVM 内存），限制抓取范围/频率，避免高基数标签。
4. 产出可验证证据（Collector 日志见到成功写入，指标数增长），但**暂不改动每日重建/销毁脚本**——**EOD 再决策**是否纳入。

---
