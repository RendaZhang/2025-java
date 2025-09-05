<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 4 — Chaos 实验（Pod Kill + Network Latency）+ MTTR 记录](#day-4--chaos-%E5%AE%9E%E9%AA%8Cpod-kill--network-latency-mttr-%E8%AE%B0%E5%BD%95)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [第一步：预检与变量就位](#%E7%AC%AC%E4%B8%80%E6%AD%A5%E9%A2%84%E6%A3%80%E4%B8%8E%E5%8F%98%E9%87%8F%E5%B0%B1%E4%BD%8D)
  - [第二步：安装 Chaos Mesh（最小化，适配 containerd）](#%E7%AC%AC%E4%BA%8C%E6%AD%A5%E5%AE%89%E8%A3%85-chaos-mesh%E6%9C%80%E5%B0%8F%E5%8C%96%E9%80%82%E9%85%8D-containerd)
  - [第三步：PodKill 实验（30s）](#%E7%AC%AC%E4%B8%89%E6%AD%A5podkill-%E5%AE%9E%E9%AA%8C30s)
  - [第四步：NetworkLatency 实验（100ms / 30s）](#%E7%AC%AC%E5%9B%9B%E6%AD%A5networklatency-%E5%AE%9E%E9%AA%8C100ms--30s)
  - [第五步：把 Chaos Mesh 的安装集成进重建和销毁流程（可选开关）](#%E7%AC%AC%E4%BA%94%E6%AD%A5%E6%8A%8A-chaos-mesh-%E7%9A%84%E5%AE%89%E8%A3%85%E9%9B%86%E6%88%90%E8%BF%9B%E9%87%8D%E5%BB%BA%E5%92%8C%E9%94%80%E6%AF%81%E6%B5%81%E7%A8%8B%E5%8F%AF%E9%80%89%E5%BC%80%E5%85%B3)

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

把 Auth 校验开关关掉：关闭后 vauth.kb.io 直接放行。

```bash
# 给目标命名空间授权
kubectl label ns svc-task chaos-mesh.org/inject=enabled --overwrite
# 关闭校验
kubectl -n chaos-testing set env deploy/chaos-controller-manager SECURITY_MODE=false
kubectl -n chaos-testing rollout status deploy/chaos-controller-manager
```

---

## 第三步：PodKill 实验（30s）

在 `chaos-testing` 命名空间创建一个 **PodChaos**，随机杀掉 `svc-task` 命名空间中、带 `app=task-api` 标签的 **一个** Pod，持续 30 秒；

观察 Pod 被杀→重建的自愈过程。

新增 `task-api/k8s/chaos/experiment-pod-kill.yaml` 文件，写入实验清单（30s 随机杀 1 个 Pod，优雅期 0）：

```yaml
apiVersion: chaos-mesh.org/v1alpha1
kind: PodChaos
metadata:
  name: kill-task-api-one
  namespace: chaos-testing
spec:
  action: pod-kill
  mode: one
  selector:
    namespaces:
      - svc-task
    labelSelectors:
      app: task-api
  gracePeriod: 0
  duration: '30s'
```

```bash
# 应用实验和记录开始时间（t0）
kubectl apply -f task-api/k8s/chaos/experiment-pod-kill.yaml
date '+[t0] %F %T %Z'

# 观察实验对象与目标 Pod 的变化
kubectl -n chaos-testing get podchaos
# 观察目标 Pod 变化（看到一个 task-api Pod Terminating→新 Pod Running）
kubectl -n svc-task get pods -o wide -w

# … 等待新 Pod 就绪 …

# 记录 MTTR
# 当确认新 Pod Ready 后，记录恢复时间（t2）
#   （按 Ctrl+C 退出上面的 watch，再执行下面这行）
date '+[t2] %F %T %Z'
#   手工计算 MTTR ~= t2 - t0

# 事件与诊断

# 看实验事件（确认注入/恢复）
kubectl -n chaos-testing describe podchaos kill-task-api-one | sed -n '/Events/,$p'

# 看业务命名空间的事件（新 Pod 调度/就绪）
kubectl -n svc-task get events --sort-by=.lastTimestamp | tail -n 20

# 清理
kubectl -n chaos-testing delete -f task-api/k8s/chaos/experiment-pod-kill.yaml --ignore-not-found
```

> 期间看到一个 `task-api` Pod 变为 `Terminating`，随后新的 Pod 被调度并 `Running`。30 秒到期后实验自动结束。

> 说明：
> 当前 `task-api` **有 2 个副本**（HPA：`REPLICAS=2`），因此杀掉一个 Pod 通常不会明显影响请求成功率；
> 本实验主要验证**自愈能力**。
> 如果想让效果在 Grafana 上更“显著”，可以在实验前临时将副本改为 1（可选）：
> `kubectl -n svc-task scale deploy task-api --replicas=1`
> 做完实验后再恢复为 2。

输出记录：

```bash
$ kubectl -n chaos-testing get podchaos
NAME                AGE
kill-task-api-one   1s

$ kubectl -n svc-task get pods -o wide -w
NAME                        READY   STATUS    RESTARTS   AGE   IP             NODE                         NOMINATED NODE   READINESS GATES
task-api-7c8b778754-mrvdv   1/1     Running   0          51s   10.0.149.229   ip-10-0-154-9.ec2.internal   <none>           <none>
task-api-7c8b778754-wz9gf   1/1     Running   0          13m   10.0.144.250   ip-10-0-154-9.ec2.internal   <none>           <none>
# 被杀 Pod 名称：task-api-7c8b778754-qpg4q
# 新 Pod 名称：task-api-7c8b778754-mrvdv

# 手工计算得出 MTTR 约为 56 秒。
[t0] 2025-09-06 00:59:50 CST
[t2] 2025-09-06 01:00:46 CST
MTTR ~= 56s

$ kubectl -n chaos-testing describe podchaos kill-task-api-one | sed -n '/Events/,$p'
      Events:
        Operation:      Apply
        Timestamp:      2025-09-05T16:59:50Z
        Type:           Succeeded
      Id:               svc-task/task-api-7c8b778754-qpg4q
      Injected Count:   1
      Phase:            Injected
      Recovered Count:  0
      Selector Key:     .
    Desired Phase:      Run
Events:
  Type    Reason           Age    From            Message
  ----    ------           ----   ----            -------
  Normal  FinalizerInited  8m41s  initFinalizers  Finalizer has been inited
  Normal  Updated          8m41s  initFinalizers  Successfully update finalizer of resource
  Normal  Updated          8m41s  desiredphase    Successfully update desiredPhase of resource
  Normal  Applied          8m41s  records         Successfully apply chaos for svc-task/task-api-7c8b778754-qpg4q
  Normal  Updated          8m41s  records         Successfully update records of resource

$ kubectl -n svc-task get events --sort-by=.lastTimestamp | tail -n 20
LAST SEEN   TYPE      REASON                   OBJECT                                              MESSAGE
24m         Normal    Scheduled                pod/task-api-7c8b778754-wz9gf                       Successfully assigned svc-task/task-api-7c8b778754-wz9gf to ip-10-0-154-9.ec2.internal
11m         Normal    Scheduled                pod/task-api-7c8b778754-mrvdv                       Successfully assigned svc-task/task-api-7c8b778754-mrvdv to ip-10-0-154-9.ec2.internal
11m         Normal    Created                  pod/task-api-7c8b778754-mrvdv                       Created container: task-api
11m         Normal    Killing                  pod/task-api-7c8b778754-qpg4q                       Stopping container task-api
11m         Normal    Started                  pod/task-api-7c8b778754-mrvdv                       Started container task-api
11m         Normal    Pulled                   pod/task-api-7c8b778754-mrvdv                       Container image "563149051155.dkr.ecr.us-east-1.amazonaws.com/task-api@sha256:d409d3f8925d75544f34edf9f0dbf8d772866b27609ef01826e1467fee52170a" already present on machine
11m         Normal    SuccessfulCreate         replicaset/task-api-7c8b778754                      Created pod: task-api-7c8b778754-mrvdv
11m         Warning   Unhealthy                pod/task-api-7c8b778754-mrvdv                       Liveness probe failed: Get "http://10.0.149.229:8080/actuator/health/liveness": dial tcp 10.0.149.229:8080: connect: connection refused
11m         Warning   Unhealthy                pod/task-api-7c8b778754-mrvdv                       Readiness probe failed: Get "http://10.0.149.229:8080/actuator/health/readiness": dial tcp 10.0.149.229:8080: connect: connection refused
11m         Normal    SuccessfullyReconciled   targetgroupbinding/k8s-svctask-taskapi-0d313cbf1f   Successfully reconciled
```

---

## 第四步：NetworkLatency 实验（100ms / 30s）

对 `svc-task` 命名空间中、带 `app=task-api` 标签的 **全部 Pod** 注入 **双向 100ms 延迟（抖动 10ms）**，持续 **30 秒**。

预期在 Grafana 中看到 **P95 明显抬升**。

新增 `task-api/k8s/chaos/experiment-net-latency.yaml` 文件：

```yaml
apiVersion: chaos-mesh.org/v1alpha1
kind: NetworkChaos
metadata:
  name: net-latency-task-api
  namespace: chaos-testing
spec:
  action: delay
  mode: all
  selector:
    namespaces:
      - svc-task
    labelSelectors:
      app: task-api
  delay:
    latency: '100ms'
    jitter: '10ms'
    correlation: '0'
  # 只对 task-api Pod 的出站流量加延迟
  direction: to
  duration: '30s'
```

```bash
# 应用实验
kubectl apply -f task-api/k8s/chaos/experiment-net-latency.yaml

# 观察实验对象状态
kubectl -n chaos-testing get networkchaos
kubectl -n chaos-testing describe networkchaos net-latency-task-api | sed -n '/Events/,$p'

# 清理
kubectl -n chaos-testing delete -f task-api/k8s/chaos/experiment-net-latency.yaml --ignore-not-found
```

> 同时打点轻负载，保证曲线可见：

```bash
kubectl -n svc-task run nl-hit --image=curlimages/curl:8.10.1 --restart=Never -it --rm -- \
  sh -lc 'for i in $(seq 1 300); do curl -s -o /dev/null http://task-api:8080/api/hello; done; echo done'
```

**Grafana 观测**

- 时间窗：`Last 15m`；刷新：`30s`。
- 看 **P95 Latency (ms)** 面板应出现短时峰值（≈ +100ms 的量级，实际取决于基线）。
- QPS/错误率可能轻微波动，但不一定显著。

> 若未看到效果，快速排查要点：
>
> 1. `chaos-daemon` 是否都 `Running`；
> 2. 目标 Pod 是否匹配 `app=task-api`；
> 3. `describe networkchaos` 的事件是否显示注入失败（若有失败信息，把那几行贴我）。

输出记录：

```bash
$ kubectl -n chaos-testing get networkchaos
NAME                   ACTION   DURATION
net-latency-task-api   delay    30s

$ kubectl -n chaos-testing describe networkchaos net-latency-task-api | sed -n '/Events/,$p'
      Events:
        Operation:      Apply
        Timestamp:      2025-09-05T17:51:00Z
        Type:           Succeeded
        Operation:      Recover
        Timestamp:      2025-09-05T17:51:30Z
        Type:           Succeeded
      Id:               svc-task/task-api-7c8b778754-wz9gf
      Injected Count:   1
      Phase:            Not Injected
      Recovered Count:  1
      Selector Key:     .
      Events:
        Operation:      Apply
        Timestamp:      2025-09-05T17:51:01Z
        Type:           Succeeded
        Operation:      Recover
        Timestamp:      2025-09-05T17:51:30Z
        Type:           Succeeded
      Id:               svc-task/task-api-7c8b778754-mrvdv
      Injected Count:   1
      Phase:            Not Injected
      Recovered Count:  1
      Selector Key:     .
    Desired Phase:      Stop
  Instances:
    svc-task/task-api-7c8b778754-mrvdv:  4
    svc-task/task-api-7c8b778754-wz9gf:  4
Events:
  Type    Reason           Age    From            Message
  ----    ------           ----   ----            -------
  Normal  FinalizerInited  2m36s  initFinalizers  Finalizer has been inited
  Normal  Updated          2m36s  initFinalizers  Successfully update finalizer of resource
  Normal  Started          2m36s  desiredphase    Experiment has started
  Normal  Updated          2m36s  desiredphase    Successfully update desiredPhase of resource
  Normal  Updated          2m36s  records         Successfully update records of resource
  Normal  Applied          2m36s  records         Successfully apply chaos for svc-task/task-api-7c8b778754-wz9gf
  Normal  Updated          2m36s  records         Successfully update records of resource
  Normal  Applied          2m35s  records         Successfully apply chaos for svc-task/task-api-7c8b778754-mrvdv
  Normal  Updated          2m35s  records         Successfully update records of resource
  Normal  TimeUp           2m6s   desiredphase    Time up according to the duration
  Normal  Updated          2m6s   desiredphase    Successfully update desiredPhase of resource
  Normal  Updated          2m6s   records         Successfully update records of resource
  Normal  Recovered        2m6s   records         Successfully recover chaos for svc-task/task-api-7c8b778754-wz9gf
  Normal  Updated          2m6s   records         Successfully update records of resource
  Normal  Recovered        2m6s   records         Successfully recover chaos for svc-task/task-api-7c8b778754-mrvdv
  Normal  Updated          2m6s   records         Successfully update records of resource
```

## 第五步：把 Chaos Mesh 的安装集成进重建和销毁流程（可选开关）

已经更新 `scripts/post-recreate.sh` 脚本集成了 Chaos Mesh 的安装（可选，默认不开启）。

已经更新 `scripts/pre-teardown.sh` 脚本集成了 Chaos Mesh 的卸载（可选，默认开启）。
