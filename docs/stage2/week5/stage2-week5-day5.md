<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 5 - 收尾硬化 + 文档化 + 指标留痕](#day-5---%E6%94%B6%E5%B0%BE%E7%A1%AC%E5%8C%96--%E6%96%87%E6%A1%A3%E5%8C%96--%E6%8C%87%E6%A0%87%E7%95%99%E7%97%95)
- [Step 1/3 — 增加 PodDisruptionBudget（PDB）并验收](#step-13--%E5%A2%9E%E5%8A%A0-poddisruptionbudgetpdb%E5%B9%B6%E9%AA%8C%E6%94%B6)
    - [新增 PDB 清单](#%E6%96%B0%E5%A2%9E-pdb-%E6%B8%85%E5%8D%95)
    - [应用并检查](#%E5%BA%94%E7%94%A8%E5%B9%B6%E6%A3%80%E6%9F%A5)
    - [纳入 `post-recreate.sh`](#%E7%BA%B3%E5%85%A5-post-recreatesh)
    - [小提示](#%E5%B0%8F%E6%8F%90%E7%A4%BA)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 5 - 收尾硬化 + 文档化 + 指标留痕

**目标**：轻量“收尾硬化 + 文档化 + 指标留痕”，不过度工程化。

**必要任务：**

1. **K8s 资源硬化（最小）**
   - 为 `task-api` 增加 **PodDisruptionBudget**（保持最少 1 个可用副本；示例片段见下），资源 Requests/Limits 已在前文配置可维持不变。
2. **演示脚本**
   - `demo/start.sh`：一键 apply 本周 YAML；`demo/stop.sh`：清理 ALB/TG 等本周资源（不销毁集群），便于复现与演示。
3. **README/计划文档更新**
   - 补充 **访问方式、镜像 tag、ALB DNS、（可选）S3 说明、已知限制**，并附关键截图。
4. **量化指标与 STAR**
   - 记录 HPA 触发截图、冷启动大致时延、以及“本周部署成功次数/尝试次数”；补一条 **STAR 一句话**作为面试素材。

---

# Step 1/3 — 增加 PodDisruptionBudget（PDB）并验收

目标：

为 `task-api` 增加一个 **最小可用副本保障**，确保在**自愿中断**（节点维护、滚动升级、手动 drain）时，始终至少保留 1 个可用 Pod。

与现有 HPA（`minReplicas: 2`）相配合，避免“一刀切”中断。

### 新增 PDB 清单

`WORK_DIR=/mnt/d/0Repositories/CloudNative`

> 文件：`${WORK_DIR}/task-api/k8s/base/pdb.yaml`

```yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: task-api-pdb
  namespace: svc-task
spec:
  # 最小可用副本，与 HPA(min=2) 兼容
  minAvailable: 1
  selector:
    matchLabels:
      app: task-api
```

> 说明：
>
> - `minAvailable: 1`：在当前副本通常为 2 的情况下，允许**逐个**中断，保证流量不中断。
> - 与 HPA 搭配：当副本被扩到更多时，PDB 会自动以数字约束；保持简单就好。

### 应用并检查

```bash
kubectl apply -f "${WORK_DIR}/task-api/k8s/base/pdb.yaml"

# 查看 PDB 概况
kubectl -n svc-task get pdb task-api-pdb

# 详细检查（关注 DisruptionsAllowed / CurrentHealthy / DesiredHealthy）
kubectl -n svc-task describe pdb task-api-pdb | sed -n '1,120p'
```

> 不需要重启 Deployment；PDB 是控制“自愿中断”的策略对象。

**预期：**

* `Allowed disruptions`（或 `DisruptionsAllowed`）为 **≥ 1**（在当前 2 个就绪副本时一般为 1）。
* `CurrentHealthy`（当前就绪）≥ `DesiredHealthy`（期望就绪）。
* 若 `DisruptionsAllowed=0`，通常是因为就绪副本数太少（未达 2）或探针未达 `READY`。

### 纳入 `post-recreate.sh`

```sh
...

# PodDisruptionBudget 名称（与 Deployment 同名 + "-pdb"）
PDB_NAME="${PDB_NAME:-${APP}-pdb}"

...

# 在 check_task_api 中加上相关的检查
check_task_api() {
  ...
  log "🔎 验证 PodDisruptionBudget (${PDB_NAME})"

  kubectl -n "${NS}" get pdb "${PDB_NAME}" >/dev/null 2>&1 || \
    abort "缺少 PodDisruptionBudget ${PDB_NAME}"

  local pdb_min disruptions_allowed current_healthy desired_healthy
  pdb_min=$(kubectl -n "${NS}" get pdb "${PDB_NAME}" -o jsonpath='{.spec.minAvailable}')
  disruptions_allowed=$(kubectl -n "${NS}" get pdb "${PDB_NAME}" -o jsonpath='{.status.disruptionsAllowed}')
  current_healthy=$(kubectl -n "${NS}" get pdb "${PDB_NAME}" -o jsonpath='{.status.currentHealthy}')
  desired_healthy=$(kubectl -n "${NS}" get pdb "${PDB_NAME}" -o jsonpath='{.status.desiredHealthy}')

  [[ "$pdb_min" != "1" ]] && abort "PodDisruptionBudget minAvailable=$pdb_min (expected 1)"
  disruptions_allowed=${disruptions_allowed:-0}
  current_healthy=${current_healthy:-0}
  desired_healthy=${desired_healthy:-0}

  if [ "$disruptions_allowed" -lt 1 ]; then
    abort "PodDisruptionBudget disruptionsAllowed=$disruptions_allowed (<1)，可能是就绪副本不足或探针未 READY"
  fi

  if [ "$current_healthy" -lt "$desired_healthy" ]; then
    abort "PodDisruptionBudget currentHealthy=$current_healthy < desiredHealthy=$desired_healthy"
  fi

  log "✅ PodDisruptionBudget 检查通过 (allowed=${disruptions_allowed}, healthy=${current_healthy}/${desired_healthy})"
  ...
}

# === 部署 task-api 到 EKS（幂等）===
deploy_task_api() {
  ...
  # 在业务资源发布的段落里，加入对 PDB 的应用，
  # 顺序在 Deployment/Service 之后：
  kubectl -n "${NS}" apply -f "${K8S_BASE_DIR}/deploy-svc.yaml"
  log "🗂️  apply 清单：pdb.yaml"
  ...
}

...
```

### 小提示

- PDB 仅影响**自愿中断**（evict/drain/滚动升级），**不**影响节点故障等非自愿中断。
- 如果未来把 `minReplicas` 临时降到 1，PDB 仍要求至少 1 个就绪副本；这通常是想要的行为。

---
