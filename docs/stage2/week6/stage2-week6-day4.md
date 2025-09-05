<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 4 — Chaos 实验（Pod Kill + Network Latency）+ MTTR 记录](#day-4--chaos-%E5%AE%9E%E9%AA%8Cpod-kill--network-latency-mttr-%E8%AE%B0%E5%BD%95)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [第一步：预检与变量就位](#%E7%AC%AC%E4%B8%80%E6%AD%A5%E9%A2%84%E6%A3%80%E4%B8%8E%E5%8F%98%E9%87%8F%E5%B0%B1%E4%BD%8D)
  - [第二步：安装 Chaos Mesh（最小化，适配 containerd）](#%E7%AC%AC%E4%BA%8C%E6%AD%A5%E5%AE%89%E8%A3%85-chaos-mesh%E6%9C%80%E5%B0%8F%E5%8C%96%E9%80%82%E9%85%8D-containerd)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 4 — Chaos 实验（Pod Kill + Network Latency）+ MTTR 记录

## 今日目标

1. 在 `CHAOS_NS=chaos-testing` 安装 **Chaos Mesh**（最小可用配置，适配 EKS/containerd）。
2. 完成两项可复现的小实验并出图：
   - **PodKill**：随机杀掉 `svc-task/task-api` 的一个 Pod（持续 30s）。
   - **NetworkLatency**：对 `task-api` 注入 **100ms/30s** 往返延迟（`direction: both`），观察 **P95 上升**。
3. 用 Grafana 验证曲线变化（QPS/错误率/P95）并记录 **MTTR**（t0 故障、t1 扩容/重建、t2 恢复，目标 ≤ 1 分钟）。

---

## 第一步：预检与变量就位

把今天会用到的变量、命名空间与选择器确认清楚；确保 `task-api` 的 **label** 与 **Service selector** 一致；确认 **容器运行时**（Chaos Mesh 需要）。

```bash
# 进入仓库根目录
WORK_DIR=.
cd $WORK_DIR

# 载入 Week6 变量（含 us-east-1 / 命名）
set -a; source ./.env.week6.local; set +a
: "${NS:=svc-task}"
: "${APP:=task-api}"
: "${CHAOS_NS:=chaos-testing}"
printf "[week6] AWS_REGION=%s\n[week6] NS=%s\n[week6] APP=%s\n[week6] CHAOS_NS=%s\n" \
  "$AWS_REGION" "$NS" "$APP" "$CHAOS_NS"

# 登录 aws
aws sso login

# 创建/标记 Chaos 命名空间（如已存在会跳过）
kubectl get ns "$CHAOS_NS" >/dev/null 2>&1 || kubectl create ns "$CHAOS_NS"
kubectl label ns "$CHAOS_NS" app=chaos --overwrite
kubectl get ns "$CHAOS_NS" --show-labels

# 检查 task-api 的 Deployment 与标签（selector 必须能选中 Pod）
echo "== Deployment labels & selectors =="
kubectl -n "$NS" get deploy "$APP" -o jsonpath='labels: {.metadata.labels}{"\n"}selector: {.spec.selector.matchLabels}{"\n"}podLabels: {.spec.template.metadata.labels}{"\n"}' ; echo
echo "== Service selector =="
kubectl -n "$NS" get svc "$APP" -o jsonpath='{.spec.selector}{"\n"}'

# 因为已经启用了 HPA，记录一份现状，方便后续观察扩缩容
kubectl -n "$NS" get hpa || true

# 确认集群容器运行时（Chaos Mesh 需要 containerd 设置）
echo "== Node container runtimes =="
kubectl get nodes -o jsonpath='{range .items[*]}{.metadata.name}{" | "}{.status.nodeInfo.containerRuntimeVersion}{"\n"}{end}'
```

> 若看到 **`selector` / `podLabels` 内没有 `app=task-api`**，先执行以下修正（只在缺少时运行）：

```bash
# 给 Pod 模板补标签（安全：不改 selector，只给模板补）
kubectl -n "$NS" patch deploy "$APP" -p '{"spec":{"template":{"metadata":{"labels":{"app":"task-api"}}}}}'
# 再看一次
kubectl -n "$NS" get deploy "$APP" -o jsonpath='podLabels: {.spec.template.metadata.labels}{"\n"}'
```

输出：

```bash
[week6] AWS_REGION=us-east-1
[week6] NS=svc-task
[week6] APP=task-api
[week6] CHAOS_NS=chaos-testing

...
Successfully logged into Start URL: https://d-9066388969.awsapps.com/start

namespace/chaos-testing labeled

NAME            STATUS   AGE     LABELS
chaos-testing   Active   2m27s   app=chaos,kubernetes.io/metadata.name=chaos-testing,name=chaos-testing

== Deployment labels & selectors ==
labels: {"app":"task-api"}
selector: {"app":"task-api"}
podLabels: {"app":"task-api"}

== Service selector ==
{"app":"task-api"}

NAME       REFERENCE             TARGETS       MINPODS   MAXPODS   REPLICAS   AGE
task-api   Deployment/task-api   cpu: 5%/60%   2         10        2          3m49s

== Node container runtimes ==
ip-10-0-131-143.ec2.internal | containerd://1.7.27
ip-10-0-142-43.ec2.internal | containerd://1.7.27
ip-10-0-154-9.ec2.internal | containerd://1.7.27
```

---

## 第二步：安装 Chaos Mesh（最小化，适配 containerd）

把 Chaos Mesh 安装到 `chaos-testing` 命名空间，**不开 Dashboard**、**不开 DNS 服务**，仅保留核心组件（controller + daemonset）。

为 EKS/containerd 指定运行时与 socket。

新增 `task-api/k8s/chaos-mesh-values.yaml` 文件：

```yaml
## Chaos Mesh Helm values for minimal core components
## Namespace: chaos-testing
## Components: controller-manager + chaos-daemon

# 控制器副本数保持 1（默认即可；如多 AZ 可按需调高）
controllerManager:
  replicaCount: 1
  resources:
    requests: { cpu: 50m, memory: 128Mi }
    limits:   { cpu: 200m, memory: 256Mi }

# 不使用内置 Dashboard
dashboard:
  create: false

dnsServer:
  # DNSChaos 相关实验暂不启用
  create: false

# Daemon（每个节点一个，用于注入/网络干扰等）
chaosDaemon:
  # 指定容器运行时为 containerd
  runtime: containerd
  # EKS (containerd) 默认的 socket 路径
  socketPath: /run/containerd/containerd.sock
  hostNetwork: true
  privileged: true
  # 把 nodeSelector / affinity 全部清空，避免 NodeAffinity 拦截
  nodeSelector: {}
  affinity: {}
  # 允许所有常见污点（NoSchedule / NoExecute）
  tolerations:
    # 匹配所有 taint（含 NoSchedule/NoExecute/PreferNoSchedule）
    - operator: Exists
  resources:
    requests: { cpu: 50m, memory: 64Mi }
    limits:   { cpu: 200m, memory: 256Mi }

# Webhook 证书自动生成（默认即可）
dnsPolicy: ClusterFirstWithHostNet
```

使用 Helm 安装 chaos-mesh：

```bash
# 安装（或升级）Chaos Mesh
helm repo add chaos-mesh https://charts.chaos-mesh.org
helm repo update
helm upgrade --install chaos-mesh chaos-mesh/chaos-mesh \
  -n chaos-testing \
  -f task-api/k8s/chaos-mesh-values.yaml

# 等待就绪并做健康检查
kubectl -n "$CHAOS_NS" rollout status deploy/chaos-controller-manager --timeout=180s
kubectl -n "$CHAOS_NS" rollout status ds/chaos-daemon --timeout=180s
```

如果需要重新安装可以执行如下命令：

```bash
# 卸载
helm -n chaos-testing uninstall chaos-mesh
# 重新安装
helm upgrade --install chaos-mesh chaos-mesh/chaos-mesh \
  -n chaos-testing \
  -f task-api/k8s/chaos-mesh-values.yaml
# 检查
kubectl get pods --namespace chaos-testing -l app.kubernetes.io/instance=chaos-mesh
# 查看 某个 chaos-daemon Pod 的事件
POD=chaos-daemon-89rr4
kubectl describe pod $POD --namespace chaos-testing | sed -n '/Events/,$p'
```

基本检查：

```bash
$ kubectl -n chaos-testing rollout status deploy/chaos-controller-manager --timeout=180s
deployment "chaos-controller-manager" successfully rolled out

# 看 daemonset 目标数与就绪数
$ kubectl -n chaos-testing get ds/chaos-daemon -o wide
NAME           DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR   AGE   CONTAINERS     IMAGES                                   SELECTOR
chaos-daemon   2         2         2       2            2           <none>          25m   chaos-daemon   ghcr.io/chaos-mesh/chaos-daemon:v2.7.3   app.kubernetes.io/component=chaos-daemon,app.kubernetes.io/instance=chaos-mesh,app.kubernetes.io/name=chaos-mesh

$ kubectl -n chaos-testing get pods -o wide
NAME                                        READY   STATUS    RESTARTS   AGE   IP             NODE                          NOMINATED NODE   READINESS GATES
chaos-controller-manager-6c764d5687-b6jmj   1/1     Running   0          25m   10.0.151.171   ip-10-0-154-9.ec2.internal    <none>           <none>
chaos-daemon-22tf8                          1/1     Running   0          25m   10.0.154.9     ip-10-0-154-9.ec2.internal    <none>           <none>
chaos-daemon-sf49j                          1/1     Running   0          25m   10.0.142.43    ip-10-0-142-43.ec2.internal   <none>           <none>
```

额外检查：

```bash
# 验证 daemonset 覆盖到所有节点（Ready/Desired 应一致）
$ echo -n "[week6] chaos-daemon ready/desired = "
kubectl -n "$CHAOS_NS" get ds/chaos-daemon -o jsonpath='{.status.numberReady}/{.status.desiredNumberScheduled}'; echo
[week6] chaos-daemon ready/desired = 2/2

# 若 Ready < Desired，先看事件
$ kubectl -n chaos-testing describe ds chaos-daemon | sed -n '/Events/,$p'
Events:
  Type    Reason            Age   From                  Message
  ----    ------            ----  ----                  -------
  Normal  SuccessfulCreate  32m   daemonset-controller  Created pod: chaos-daemon-sf49j
  Normal  SuccessfulCreate  32m   daemonset-controller  Created pod: chaos-daemon-22tf8

# 看节点 taints 与 OS
$ kubectl get nodes -o custom-columns=NAME:.metadata.name,TAINTS:.spec.taints[*].key,OS:.status.nodeInfo.osImage
NAME                          TAINTS   OS
ip-10-0-142-43.ec2.internal   <none>   Amazon Linux 2023.8.20250818
ip-10-0-154-9.ec2.internal    <none>   Amazon Linux 2023.8.20250818

# 看 daemonset 的 tolerations/nodeSelector（是否限制了目标节点）
$ kubectl -n chaos-testing get ds chaos-daemon -o yaml | egrep -n 'tolerations|nodeSelector|affinity'
107:      tolerations:

# 查看 CRD 是否安装到位
$ kubectl get crd | egrep 'chaosmesh|podchaos|networkchaos|iochaos|timechaos' || true
iochaos.chaos-mesh.org                       2025-09-05T12:08:25Z
networkchaos.chaos-mesh.org                  2025-09-05T12:08:26Z
podchaos.chaos-mesh.org                      2025-09-05T12:08:28Z
podiochaos.chaos-mesh.org                    2025-09-05T12:08:28Z
podnetworkchaos.chaos-mesh.org               2025-09-05T12:08:29Z
timechaos.chaos-mesh.org                     2025-09-05T12:08:32Z

# 列一下关键 Pod
$ kubectl -n "$CHAOS_NS" get pods -o wide
NAME                                        READY   STATUS    RESTARTS   AGE   IP             NODE                          NOMINATED NODE   READINESS GATES
chaos-controller-manager-6c764d5687-b6jmj   1/1     Running   0          33m   10.0.151.171   ip-10-0-154-9.ec2.internal    <none>           <none>
chaos-daemon-22tf8                          1/1     Running   0          33m   10.0.154.9     ip-10-0-154-9.ec2.internal    <none>           <none>
chaos-daemon-sf49j                          1/1     Running   0          33m   10.0.142.43    ip-10-0-142-43.ec2.internal   <none>           <none>
```

如果 Pending 并提示 Too many pods → 临时释放 1 个 Pod 位（或扩节点）：

```bash
# 临时缩容（释放 1 个 Pod 位）
kubectl -n svc-task scale deploy/task-api --replicas=1

# 等待 daemonset 在该节点调度成功
kubectl -n chaos-testing get ds chaos-daemon -o wide
kubectl -n chaos-testing get pods -o wide | grep chaos-daemon

# 恢复业务副本
kubectl -n svc-task scale deploy/task-api --replicas=2
```

---
