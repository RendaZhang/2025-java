<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 1 - 应用指标暴露 + AMP 工作区](#day-1---%E5%BA%94%E7%94%A8%E6%8C%87%E6%A0%87%E6%9A%B4%E9%9C%B2--amp-%E5%B7%A5%E4%BD%9C%E5%8C%BA)
- [第一步：在仓库根目录创建本地环境文件并验证加载](#%E7%AC%AC%E4%B8%80%E6%AD%A5%E5%9C%A8%E4%BB%93%E5%BA%93%E6%A0%B9%E7%9B%AE%E5%BD%95%E5%88%9B%E5%BB%BA%E6%9C%AC%E5%9C%B0%E7%8E%AF%E5%A2%83%E6%96%87%E4%BB%B6%E5%B9%B6%E9%AA%8C%E8%AF%81%E5%8A%A0%E8%BD%BD)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 1 - 应用指标暴露 + AMP 工作区

**目标**：

让 `task-api` 暴露 Prometheus 指标端点（`/actuator/prometheus`），并把 Week 6 的“模式/云资源开关”落在本地 env 文件中；

若选择 Managed 模式，则创建 AMP Workspace（仅创建，不接入采集——采集放到 Day 2）。

**产出**：

1. `.env.week6.local`（开关与变量）
2. `task-api` 已启用 Actuator + Prometheus（可本地或集群内 `curl` 验证）
3. （可选）已创建 AMP Workspace，并把 `AMP_WORKSPACE_ID` 写回 `.env.week6.local`

---

# 第一步：在仓库根目录创建本地环境文件并验证加载

**目标**：

新建 `.env.week6.local`（被 git 忽略），写入 Week 6 所需的最小变量，并在当前 Shell 中正确加载。

**执行：**

```bash
# 进入仓库根目录
cd $WORK_DIR

# 1) 新建/覆盖本地 env 文件（注意：region 已按你的 us-east-1）
tee .env.week6.local >/dev/null <<'EOF'
# Mode: free | managed  （默认free，后续如需走AMP再切managed）
WEEK6_MODE=free

# Region / Names
AWS_REGION=us-east-1
NS=svc-task
APP=task-api

# Namespaces
PROM_NAMESPACE=observability
CHAOS_NS=chaos-testing

# AMP (仅在 managed 模式下使用；ID 会在创建后回填)
AMP_ALIAS=amp-renda-cloud-lab-wk6-use1
AMP_WORKSPACE_ID=
EOF

# 2) 确保被 git 忽略（如已有则不会重复添加）
grep -qxF '.env.week6.local' .gitignore || echo '.env.week6.local' >> .gitignore

# 3) 加载并回显关键变量，确认写入成功
set -a; source ./.env.week6.local; set +a
printf "WEEK6_MODE=%s\nAWS_REGION=%s\nNS=%s\nAPP=%s\nPROM_NAMESPACE=%s\nCHAOS_NS=%s\nAMP_ALIAS=%s\nAMP_WORKSPACE_ID=%s\n" \
  "$WEEK6_MODE" "$AWS_REGION" "$NS" "$APP" "$PROM_NAMESPACE" "$CHAOS_NS" "$AMP_ALIAS" "${AMP_WORKSPACE_ID:-<empty>}"
```

**验收标准（你看到的输出应类似）：**

```
WEEK6_MODE=free
AWS_REGION=us-east-1
NS=svc-task
APP=task-api
PROM_NAMESPACE=observability
CHAOS_NS=chaos-testing
AMP_ALIAS=amp-renda-cloud-lab-wk6-use1
AMP_WORKSPACE_ID=<empty>
```

---
