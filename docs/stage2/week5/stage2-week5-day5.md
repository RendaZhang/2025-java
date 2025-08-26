<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 5 - 收尾硬化 + 文档化 + 指标留痕](#day-5---%E6%94%B6%E5%B0%BE%E7%A1%AC%E5%8C%96--%E6%96%87%E6%A1%A3%E5%8C%96--%E6%8C%87%E6%A0%87%E7%95%99%E7%97%95)
- [Step 1/3 — 增加 PodDisruptionBudget（PDB）并验收](#step-13--%E5%A2%9E%E5%8A%A0-poddisruptionbudgetpdb%E5%B9%B6%E9%AA%8C%E6%94%B6)
    - [新增 PDB 清单](#%E6%96%B0%E5%A2%9E-pdb-%E6%B8%85%E5%8D%95)
    - [应用并检查](#%E5%BA%94%E7%94%A8%E5%B9%B6%E6%A3%80%E6%9F%A5)
    - [纳入 `post-recreate.sh`](#%E7%BA%B3%E5%85%A5-post-recreatesh)
    - [小提示](#%E5%B0%8F%E6%8F%90%E7%A4%BA)
  - [Step 2/3 — 轻量文档化 + 指标留痕（仅必要项）](#step-23--%E8%BD%BB%E9%87%8F%E6%96%87%E6%A1%A3%E5%8C%96--%E6%8C%87%E6%A0%87%E7%95%99%E7%97%95%E4%BB%85%E5%BF%85%E8%A6%81%E9%A1%B9)
    - [采集关键信息](#%E9%87%87%E9%9B%86%E5%85%B3%E9%94%AE%E4%BF%A1%E6%81%AF)
    - [快速“冷启动到就绪”测量（单 Pod）](#%E5%BF%AB%E9%80%9F%E5%86%B7%E5%90%AF%E5%8A%A8%E5%88%B0%E5%B0%B1%E7%BB%AA%E6%B5%8B%E9%87%8F%E5%8D%95-pod)
    - [运行事实（最小集）](#%E8%BF%90%E8%A1%8C%E4%BA%8B%E5%AE%9E%E6%9C%80%E5%B0%8F%E9%9B%86)
      - [指标留痕](#%E6%8C%87%E6%A0%87%E7%95%99%E7%97%95)
      - [已知约束/退路](#%E5%B7%B2%E7%9F%A5%E7%BA%A6%E6%9D%9F%E9%80%80%E8%B7%AF)
      - [一句话 STAR（面试备忘）](#%E4%B8%80%E5%8F%A5%E8%AF%9D-star%E9%9D%A2%E8%AF%95%E5%A4%87%E5%BF%98)

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
kubectl -n svc-task get pdb task-api-pdb -o jsonpath='{.status.disruptionsAllowed}'
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

## Step 2/3 — 轻量文档化 + 指标留痕（仅必要项）

目标：

把**最关键的运行事实**与**两三条可量化指标**落到文档，便于复现与面试输出。

只做最小集合。

### 采集关键信息

```bash
# 统一变量

export NS=svc-task
export APP=task-api

# 1) 访问入口（ALB DNS）

$ ALB_DNS=$(kubectl -n "$NS" get ing "$APP" -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'); echo "ALB_DNS=${ALB_DNS}"

# 输出：

ALB_DNS=k8s-svctask-taskapi-c91e97499e-1132038718.us-east-1.elb.amazonaws.com

# 2) 部署所用镜像（含 digest）

$ kubectl -n "$NS" get deploy "$APP" -o jsonpath='{.spec.template.spec.containers[0].image}'; echo

# 输出：

563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api@sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741

# 3) HPA 概览（目标/当前、Min/Max、副本）

$ kubectl -n "$NS" describe hpa "$APP" | sed -n '1,50p'

# 输出：

Name:                                                  task-api
Namespace:                                             svc-task
Labels:                                                <none>
Annotations:                                           <none>
CreationTimestamp:                                     Tue, 26 Aug 2025 22:01:04 +0800
Reference:                                             Deployment/task-api
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  4% (4m) / 60%
Min replicas:                                          2
Max replicas:                                          10
Behavior:
  Scale Up:
    Stabilization Window: 0 seconds
    Select Policy: Max
    Policies:
      - Type: Percent  Value: 100  Period: 60 seconds
  Scale Down:
    Stabilization Window: 120 seconds
    Select Policy: Max
    Policies:
      - Type: Percent  Value: 50  Period: 60 seconds
Deployment pods:       2 current / 2 desired
Conditions:
  Type            Status  Reason            Message
  ----            ------  ------            -------
  AbleToScale     True    ReadyForNewScale  recommended size matches current size
  ScalingActive   True    ValidMetricFound  the HPA was able to successfully calculate a replica count from cpu resource utilization (percentage of request)
  ScalingLimited  True    TooFewReplicas    the desired replica count is less than the minimum replica count
Events:           <none>

# 4) PDB 概览（DisruptionsAllowed/CurrentHealthy/DesiredHealthy）

$ kubectl -n "$NS" get pdb "${APP}-pdb" -o wide

# 输出：

NAME           MIN AVAILABLE   MAX UNAVAILABLE   ALLOWED DISRUPTIONS   AGE
task-api-pdb   1               N/A               1                     21m

$ kubectl -n "$NS" describe pdb "${APP}-pdb" | sed -n '1,80p'

# 输出：

Name:           task-api-pdb
Namespace:      svc-task
Min available:  1
Selector:       app=task-api
Status:
    Allowed disruptions:  1
    Current:              2
    Desired:              1
    Total:                2
Events:                   <none>

# 5) 当前副本与 Pod 状态

$ kubectl -n "$NS" get deploy "$APP" -o wide

# 输出：

NAME       READY   UP-TO-DATE   AVAILABLE   AGE   CONTAINERS   IMAGES                                                                                                                          SELECTOR
task-api   2/2     2            2           23m   task-api     563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api@sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741   app=task-api

$ kubectl -n "$NS" get pods -l app="$APP" -o wide

# 输出：

NAME                        READY   STATUS    RESTARTS   AGE   IP             NODE                           NOMINATED NODE   READINESS GATES
task-api-748665bf8d-f89z7   1/1     Running   0          24m   10.0.155.92    ip-10-0-144-167.ec2.internal   <none>           <none>
task-api-748665bf8d-pfnnk   1/1     Running   0          24m   10.0.149.143   ip-10-0-144-167.ec2.internal   <none>           <none>
```

> 预期：有 `ALB_DNS`，镜像为 `…@sha256:…`，`HPA` 显示 `target 60%`，`PDB` 的 `DisruptionsAllowed >= 1`。

### 快速“冷启动到就绪”测量（单 Pod）

> 目的：留下一个“**Time-to-Ready**（TTR）≈ X 秒”的量化指标。
>
> 做法：删除 1 个 Pod，计时直到新的 Pod `Ready`。

```bash
POD=$(kubectl -n "$NS" get pods -l app="$APP" -o jsonpath='{.items[0].metadata.name}')
echo "Deleting $POD to measure TTR..."
START=$(date +%s)
kubectl -n "$NS" delete pod "$POD" --wait=false
kubectl -n "$NS" wait --for=condition=ready pod -l app="$APP" --timeout=240s
END=$(date +%s); echo "Time-to-Ready=$((END-START))s"

# 输出：

Deleting task-api-748665bf8d-f89z7 to measure TTR...
pod "task-api-748665bf8d-f89z7" deleted
pod/task-api-748665bf8d-g5zr7 condition met
pod/task-api-748665bf8d-pfnnk condition met
Time-to-Ready=27s
```

> 把 `Time-to-Ready=XXs` 记录到文档。
> 若超时，可以顺便记一个**原因**（如镜像拉取慢、探针初值偏紧）。

### 运行事实（最小集）

- Region/Cluster/NS：us-east-1 / dev / svc-task
- ALB：k8s-svctask-taskapi-c91e97499e-1132038718.us-east-1.elb.amazonaws.com
- 镜像（digest）：563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api@sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741
- 探针：/actuator/health/{readiness,liveness}
- HPA：CPU 60%，min=2，max=10
- PDB：minAvailable=1

#### 指标留痕

- Time-to-Ready（删除单 Pod → 新 Pod Ready）：27s
- HPA 扩缩：
    ```bash
    # 扩容

    # 使用 hey 直接打 Service 的内网 DNS（:8080 是后端容器端口）
    kubectl -n svc-task run hey --image=williamyeh/hey:latest --restart=Never -- \
    -z 2m -c 50 -q 0 "http://task-api.svc-task.svc.cluster.local:8080/api/hello?name=HPA"

    # 输出：
    # pod/hey created

    # 观察扩容
    watch -n 5 'kubectl -n svc-task get hpa,deploy,pods -o wide'

    # 关键输出：
    ...
    NAME                                           REFERENCE             TARGETS         MINPODS   MAXPODS   REPLICAS   AGE
    horizontalpodautoscaler.autoscaling/task-api   Deployment/task-api   cpu: 496%/60%   2         10        8          62m
    ...

    # 缩容

    # 删除 pod
    kubectl -n svc-task delete pod hey

    # 观察缩容
    watch -n 5 'kubectl -n svc-task get hpa,deploy,pods -o wide'

    # 关键输出：
    ...
    NAME                                           REFERENCE             TARGETS        MINPODS   MAXPODS   REPLICAS   AGE
    horizontalpodautoscaler.autoscaling/task-api   Deployment/task-api   cpu: 11%/60%   2         10        2          64m
    ...

    ```
- 可用性演示：/api/hello 与 /actuator/health 通过 ALB 访问成功
    ```bash
    $ curl -s "http://k8s-svctask-taskapi-c91e97499e-1132038718.us-east-1.elb.amazonaws.com/api/hello?name=Renda"
    hello Renda
    ```
- PodDisruptionBudget 与 DisruptionsAllowed
    ```bash
    $ kubectl -n "$NS" get pdb task-api-pdb

    NAME           MIN AVAILABLE   MAX UNAVAILABLE   ALLOWED DISRUPTIONS   AGE
    task-api-pdb   1               N/A               1                     31m
    ```
- Ingress
    ```bash
    $ kubectl -n "$NS" get ingress task-api

    NAME       CLASS   HOSTS   ADDRESS                                                                 PORTS   AGE
    task-api   alb     *       k8s-svctask-taskapi-c91e97499e-1132038718.us-east-1.elb.amazonaws.com   80      21m
    ```


#### 已知约束/退路

- ECR 生命周期只保留 1 个 tag（成本低、回滚空间小）→ 部署使用 digest 避免 tag 漂移
- 若 ALB 异常：检查子网标签/ALBC 日志；若探针失败：调大 initialDelaySeconds
- 私网访问 S3：已启用 Gateway Endpoint，避免 NAT 费

#### 一句话 STAR（面试备忘）

```
S：无密钥访问 S3 的合规要求
T：在 EKS 中实现最小权限访问并对外稳定暴露
A：IRSA + 前缀级 S3 策略，ALB Ingress，HPA + PDB，Gateway Endpoint 降本
R：公网/集群内读写闭环通过，扩容→回落正常，TTR≈27s
```

---
