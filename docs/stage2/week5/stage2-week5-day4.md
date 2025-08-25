<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 4 - S3 最小接入 + IRSA](#day-4---s3-%E6%9C%80%E5%B0%8F%E6%8E%A5%E5%85%A5--irsa)
  - [目标](#%E7%9B%AE%E6%A0%87)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 4 - S3 最小接入 + IRSA

## 目标

**IRSA + S3 最小闭环**：让 `svc-task/task-api` 在 **不使用任何长期密钥** 的前提下，凭 **ServiceAccount → IRSA → IAM Role** 访问 **S3** 的指定前缀，并完成端到端校验与安全/成本基线配置。

1. 新建 **S3 bucket**（Terraform 或控制台均可；**Block Public Access**、SSE-S3 开启）。
2. 配置 **IAM 角色**（IRSA）只允许 `bucket/$APP/*` 前缀读写；把 Role 绑定到 `${APP}-sa`。
3. 在应用添加 `/api/files/put?key=...` 与 `/api/files/get?key=...` 两个端点，演示最小读写。

验收清单：

- Pod（使用 `task-api` 的 SA）在容器内：
  - 能拿到 STS 身份（`aws sts get-caller-identity` 成功）。
  - 能对目标 **S3 桶/指定前缀** 执行最小操作（`PutObject/GetObject/List` 成功）。
  - 访问不在权限范围内的路径时 **被拒绝**（验证**最小权限**有效）。
- 所有变更可随 **`make start-all / stop-all`** 自动复现（Terraform + `post-recreate.sh` 已对齐）。

---
