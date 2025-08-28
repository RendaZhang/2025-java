<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 2 - ADOT Collector（采集 → AMP）+ 成本护栏](#day-2---adot-collector%E9%87%87%E9%9B%86-%E2%86%92-amp-%E6%88%90%E6%9C%AC%E6%8A%A4%E6%A0%8F)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [第一步：预检与环境准备（变量 / remote write / 命名空间）](#%E7%AC%AC%E4%B8%80%E6%AD%A5%E9%A2%84%E6%A3%80%E4%B8%8E%E7%8E%AF%E5%A2%83%E5%87%86%E5%A4%87%E5%8F%98%E9%87%8F--remote-write--%E5%91%BD%E5%90%8D%E7%A9%BA%E9%97%B4)
  - [第二步：Terraform 创建 IRSA](#%E7%AC%AC%E4%BA%8C%E6%AD%A5terraform-%E5%88%9B%E5%BB%BA-irsa)
    - [在 Terraform 中的关键修改](#%E5%9C%A8-terraform-%E4%B8%AD%E7%9A%84%E5%85%B3%E9%94%AE%E4%BF%AE%E6%94%B9)
    - [快速自检命令](#%E5%BF%AB%E9%80%9F%E8%87%AA%E6%A3%80%E5%91%BD%E4%BB%A4)
  - [第三步：用 Helm 安装/升级 ADOT Collector](#%E7%AC%AC%E4%B8%89%E6%AD%A5%E7%94%A8-helm-%E5%AE%89%E8%A3%85%E5%8D%87%E7%BA%A7-adot-collector)
    - [安装 ADOT Collector](#%E5%AE%89%E8%A3%85-adot-collector)
    - [触发请求（命中 http_server_requests_*）](#%E8%A7%A6%E5%8F%91%E8%AF%B7%E6%B1%82%E5%91%BD%E4%B8%AD-http_server_requests_)
    - [端到端验证（port-forward）](#%E7%AB%AF%E5%88%B0%E7%AB%AF%E9%AA%8C%E8%AF%81port-forward)
  - [第四步：收尾](#%E7%AC%AC%E5%9B%9B%E6%AD%A5%E6%94%B6%E5%B0%BE)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

太棒了，收到你的偏好。我把 **Day 2** 的方案按“最小必要 + Terraform 做 IRSA + 收尾再讨论是否并入每日流程”的思路重新整理好了。

# Day 2 - ADOT Collector（采集 → AMP）+ 成本护栏

## 今日目标

1. 在 `observability` 命名空间部署 **ADOT Collector (Deployment 形态)**，只抓取 `svc-task / task-api:8080/actuator/prometheus`。
2. 使用 **Terraform** 创建并绑定 **IRSA**（仅授予 `AmazonPrometheusRemoteWriteAccess`），让 Collector 以 **SigV4** 向 **AMP（us-east-1, 你的 workspaceId）** 执行 `remote_write`。
3. 落地**成本护栏**：仅保留面试关键指标（HTTP 延迟直方图与 JVM 内存），限制抓取范围/频率，避免高基数标签。
4. 产出可验证证据（Collector 日志见到成功写入，指标数增长），但**暂不改动每日重建/销毁脚本**——**EOD 再决策**是否纳入。

---

## 第一步：预检与环境准备（变量 / remote write / 命名空间）

**目标**：

把 Day 2 用到的关键变量就位，确认 `remote_write`，并准备 `observability` 命名空间（后续给 ADOT 用）。

修改本地环境变量文件 `.env.week6.local`，加上 AWS_PROFILE，从而省去每条命令的 `--profile`：

```bash
AWS_PROFILE=phase2-sso
```

执行如下命令初始化本地环境：

```bash
WORK_DIR=.

# 进入仓库根目录
cd $WORK_DIR

# 加载本地变量
set -a; source ./.env.week6.local; set +a
printf "AWS_PROFILE=%s\nAWS_REGION=%s\nAMP_WORKSPACE_ID=%s\nNS=%s\nAPP=%s\n" \
  "$AWS_PROFILE" "$AWS_REGION" "$AMP_WORKSPACE_ID" "$NS" "$APP"

# 登录 aws cli
aws sso login

# 计算 remote_write 并打印（用于后续 ADOT）
AMP_ENDPOINT=$(
  aws amp describe-workspace \
    --region "$AWS_REGION" \
    --workspace-id "$AMP_WORKSPACE_ID" \
    --query 'workspace.prometheusEndpoint' --output text
)
REMOTE_WRITE="${AMP_ENDPOINT}api/v1/remote_write"
echo "[week6] remote_write = $REMOTE_WRITE"
```

准备 ADOT 专用命名空间（如已存在会跳过）：

```bash
kubectl get ns observability >/dev/null 2>&1 || kubectl create ns observability
kubectl label ns observability app=observability --overwrite
kubectl get ns observability --show-labels
```

输出：

```bash
AWS_PROFILE=phase2-sso
AWS_REGION=us-east-1
AMP_WORKSPACE_ID=ws-4c9b04d5-5e49-415e-90ef-747450304dca
NS=svc-task
APP=task-api

...
Successfully logged into Start URL: https://d-9066388969.awsapps.com/start

[week6] remote_write = https://aps-workspaces.us-east-1.amazonaws.com/workspaces/ws-4c9b04d5-5e49-415e-90ef-747450304dca/api/v1/remote_write

namespace/observability created
namespace/observability labeled
NAME            STATUS   AGE   LABELS
observability   Active   29s   app=observability,kubernetes.io/metadata.name=observability
```

---

## 第二步：Terraform 创建 IRSA

目标：

让 `observability` 命名空间中的 **ServiceAccount: `adot-collector`** 具备向 **AMP** 执行 `remote_write` 的最小权限（`AmazonPrometheusRemoteWriteAccess`），并与 EKS OIDC 关联。

### 在 Terraform 中的关键修改

新增 `infra\aws\modules\irsa_adot_amp\main.tf` 文件：

```hcl
// ---------------------------
// IRSA 模块：为 ADOT Collector 绑定 IAM 角色
// 权限：AmazonPrometheusRemoteWriteAccess（最小权限用于 AMP remote_write）
// ---------------------------

resource "aws_iam_role" "adot_amp_remote_write" {
  name        = var.name
  description = "IRSA role for ADOT Collector AMP remote_write in ${var.cluster_name}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = "sts:AssumeRoleWithWebIdentity",
        Principal = {
          Federated = var.oidc_provider_arn
        },
        Condition = {
          StringEquals = {
            "${var.oidc_provider_url_without_https}:aud" = "sts.amazonaws.com",
            "${var.oidc_provider_url_without_https}:sub" = "system:serviceaccount:${var.namespace}:${var.service_account_name}"
          }
        }
      }
    ]
  })

  lifecycle {
    create_before_destroy = true
  }
}

# 附加 AWS 托管策略：AmazonPrometheusRemoteWriteAccess
resource "aws_iam_role_policy_attachment" "remote_write" {
  role       = aws_iam_role.adot_amp_remote_write.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonPrometheusRemoteWriteAccess"

  lifecycle {
    create_before_destroy = true
  }
}

```

修改 `infra/aws/terraform.tfvars` 文件，新增如下变量：

```hcl
...
# ADOT Collector（AMP remote_write）IRSA 配置
adot_irsa_role_name       = "adot-collector" # IRSA 角色名称（用于 ADOT Collector）
adot_service_account_name = "adot-collector" # ServiceAccount 名称
adot_namespace            = "observability"  # 命名空间
...
```

> 这里省略其他非关键的修改。

### 快速自检命令

```bash
# 查看 Role
$ aws iam get-role --role-name adot-collector --query 'Role.Arn' --output text
arn:aws:iam::563149051155:role/adot-collector

$ aws iam get-role --role-name adot-collector --query 'Role.AssumeRolePolicyDocument' --output json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": {
                "Federated": "arn:aws:iam::563149051155:oidc-provider/oidc.eks.us-east-1.amazonaws.com/id/0525D0C7B73CE910581C981BEDCE5E29"
            },
            "Action": "sts:AssumeRoleWithWebIdentity",
            "Condition": {
                "StringEquals": {
                    "oidc.eks.us-east-1.amazonaws.com/id/0525D0C7B73CE910581C981BEDCE5E29:sub": "system:serviceaccount:observability:adot-collector",
                    "oidc.eks.us-east-1.amazonaws.com/id/0525D0C7B73CE910581C981BEDCE5E29:aud": "sts.amazonaws.com"
                }
            }
        }
    ]
}

# 查看 SA 注解（必须）
$ kubectl -n observability get sa adot-collector -o yaml | grep eks.amazonaws.com/role-arn -n
5:    eks.amazonaws.com/role-arn: arn:aws:iam::563149051155:role/adot-collector
```

---

## 第三步：用 Helm 安装/升级 ADOT Collector

> 说明：
> - 官方 ADOT Helm 仓库已标注**不再建议使用**，我们采用 **OpenTelemetry 官方 Helm Chart**，但将 **镜像换成 ADOT Collector**（内置 SigV4 扩展），并用 **values.yaml 的 `config`** 字段提供 Collector 配置。这样既符合 Helm 习惯，也能用 SigV4 给 AMP 做 `remote_write`。
> - SigV4 的正确写法：使用 **`prometheusremotewrite` 导出器 + `sigv4auth` 扩展**，并在 `service.extensions` 与 `exporters.prometheusremotewrite.auth.authenticator` 两处引用。

### 安装 ADOT Collector

```bash
# 准备 values 文件
tee task-api/k8s/adot-collector-values.yaml >/dev/null <<EOF
## ADOT Collector Helm values for AMP remote_write via SigV4
## Namespace: observability
## ServiceAccount: adot-collector (IRSA annotated)

mode: deployment
replicaCount: 1

serviceAccount:
  create: true
  name: adot-collector
  annotations:
    eks.amazonaws.com/role-arn: arn:aws:iam::563149051155:role/adot-collector

# 用 ADOT Collector 镜像（内置 SigV4 扩展）
image:
  repository: public.ecr.aws/aws-observability/aws-otel-collector
  tag: v0.43.0

resources:
  requests: { cpu: 100m, memory: 128Mi }
  limits:   { cpu: 200m, memory: 256Mi }

clusterRole:
  # Required for prometheus receiver with kubernetes_sd_configs
  create: true
  rules:
    - apiGroups: [""]
      resources: ["pods", "nodes", "endpoints", "services", "namespaces"]
      verbs: ["get", "list", "watch"]

config:
  extensions:
    # Keep default health_check extension; add SigV4 for request signing
    health_check: {}
    sigv4auth:
      region: us-east-1
      service: aps
  receivers:
    prometheus:
      config:
        global:
          scrape_interval: 30s
          scrape_timeout: 10s
        scrape_configs:
          - job_name: 'task-api'
            scrape_interval: 30s
            scrape_timeout: 10s
            metrics_path: /actuator/prometheus
            static_configs:
              # 直接使用 FQDN（Fully Qualified Domain Name，完全限定域名）
              - targets: ['task-api.svc-task.svc.cluster.local:8080']
            sample_limit: 2000
            metric_relabel_configs:
              # 仅保留关键指标（HTTP 延迟直方图 + JVM 内存）
              - source_labels: [__name__]
                regex: 'http_server_requests_seconds_(bucket|sum|count)|jvm_memory_used_bytes'
                action: keep
              # 丢弃高基数标签，控制成本
              - action: labeldrop
                regex: 'uri|exception'
  processors:
    batch:
      timeout: 5s
      send_batch_size: 1000
      send_batch_max_size: 10000
  exporters:
    # Remote write to Amazon Managed Prometheus
    prometheusremotewrite:
      endpoint: https://aps-workspaces.us-east-1.amazonaws.com/workspaces/ws-4c9b04d5-5e49-415e-90ef-747450304dca/api/v1/remote_write
      auth:
        authenticator: sigv4auth
  service:
    extensions:
      - health_check
      - sigv4auth
    pipelines:
      # Export collector + pod metrics to AMP
      metrics:
        receivers:
          - prometheus
        processors:
          - batch
        exporters:
          - prometheusremotewrite
EOF

# 安装/升级（OpenTelemetry 官方 Helm 仓库 + 指定 ADOT 镜像）
helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
helm repo update
helm upgrade --install adot-collector open-telemetry/opentelemetry-collector \
  -n observability -f task-api/k8s/adot-collector-values.yaml

# 如果需要可以执行如下命令清理残留的部署
helm -n observability uninstall adot-collector
kubectl -n observability delete deploy adot-collector-opentelemetry-collector

# 等待就绪并查看日志

$ kubectl -n observability rollout status deploy/adot-collector-opentelemetry-collector --timeout=180s
deployment "adot-collector-opentelemetry-collector" successfully rolled out

$ kubectl -n observability logs deploy/adot-collector-opentelemetry-collector --tail=200 | egrep -i 'prometheusremotewrite|sigv4|wrote|export|remote write|success|200'
2025/08/28 18:57:21 attn: users of the `datadog`, `logzio`, `sapm`, `signalfx` exporter components. please refer to https://github.com/aws-observability/aws-otel-collector/issues/2734 in regards to an upcoming ADOT Collector breaking change
2025-08-28T18:57:22.162Z        info    builders/builders.go:26 Development component. May change in the future.       {"kind": "exporter", "data_type": "traces", "name": "debug"}
2025-08-28T18:57:22.163Z        info    builders/builders.go:26 Development component. May change in the future.       {"kind": "exporter", "data_type": "logs", "name": "debug"}
2025-08-28T18:57:22.167Z        info    extensions/extensions.go:42     Extension is starting...        {"kind": "extension", "name": "sigv4auth"}
2025-08-28T18:57:22.167Z        info    extensions/extensions.go:59     Extension started.      {"kind": "extension", "name": "sigv4auth"}
2025-08-28T18:57:22.167Z        info    healthcheckextension@v0.117.0/healthcheckextension.go:32        Starting health_check extension {"kind": "extension", "name": "health_check", "config": {"Endpoint":"10.0.149.89:13133","TLSSetting":null,"CORS":null,"Auth":null,"MaxRequestBodySize":0,"IncludeMetadata":false,"ResponseHeaders":null,"CompressionAlgorithms":null,"ReadTimeout":0,"ReadHeaderTimeout":0,"WriteTimeout":0,"IdleTimeout":0,"Path":"/","ResponseBody":null,"CheckCollectorPipeline":{"Enabled":false,"Interval":"5m","ExporterFailureThreshold":5}}}
```

> `post-recreate.sh` 和 `pre-teardown.sh` 脚本已经同步更新以整合 ADOT Collector 到重建和销毁流程之中。

**已经确认**：`rollout status` 显示 `successfully rolled out`。

### 触发请求（命中 http_server_requests_*）

```bash
$ kubectl -n "$NS" run curl --image=curlimages/curl:8.10.1 --restart=Never --rm --attach -- sh -lc 'for i in $(seq 1 300); do curl -s -o /dev/null http://task-api.svc-task.svc.cluster.local:8080/api/hello; curl -s -o /dev/null http://task-api.svc-task.svc.cluster.local:8080/actuator/health; done; echo done'

If you don't see a command prompt, try pressing enter.
done
pod "curl" deleted
```

检查日志：

```bash
$ kubectl -n observability logs deploy/adot-collector-opentelemetry-collector --tail=200 | egrep -i 'prometheusremotewrite|wrote|200'
# ADOT Collector 的 prometheusremotewrite 导出器默认只在失败时输出日志；成功发送样本时不会打印 “200” 或 “remote write” 等信息。
# 因此 egrep 未命中并不代表没有写入。
```

### 端到端验证（port-forward）

其中一个终端执行（保持挂着）：

```bash
kubectl -n observability port-forward deploy/adot-collector-opentelemetry-collector 8888
```

另一个终端发请求验证：

```bash
$ curl -s localhost:8888/metrics | grep otelcol_exporter_sent_metric_points
# HELP otelcol_exporter_sent_metric_points Number of metric points successfully sent to destination. [alpha]
# TYPE otelcol_exporter_sent_metric_points counter
otelcol_exporter_sent_metric_points{exporter="prometheusremotewrite",service_instance_id="08167170-fd26-4092-a18f-362ce9fd3977",service_name="aws-otel-collector",service_version="v0.43.0"} 390

$ curl -s http://localhost:8888/metrics | grep 'otelcol_exporter_sent_metric_points{exporter="prometheusremotewrite"'
otelcol_exporter_sent_metric_points{exporter="prometheusremotewrite",service_instance_id="08167170-fd26-4092-a18f-362ce9fd3977",service_name="aws-otel-collector",service_version="v0.43.0"} 405
```

该值大于 0 即表示样本已通过 Remote Write 发送。

---

## 第四步：收尾

快速健康检查：

```bash
# ADOT 部署健康：
$ kubectl -n observability get deploy,pod -l app.kubernetes.io/instance=adot-collector
NAME                                                     READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/adot-collector-opentelemetry-collector   1/1     1            1           14m

NAME                                                          READY   STATUS    RESTARTS   AGE
pod/adot-collector-opentelemetry-collector-7f5476f4d8-llp6c   1/1     Running   0          14m

# IRSA 注解匹配：
$ kubectl -n observability get sa adot-collector -o yaml | grep eks.amazonaws.com/role-arn -n
5:    eks.amazonaws.com/role-arn: arn:aws:iam::563149051155:role/adot-collector

# 采集目标可达（集群内访问）：
$ kubectl -n observability run curl --rm -it --image=curlimages/curl:8.10.1 --restart=Never -- sh -lc 'curl -sf http://task-api.svc-task.svc.cluster.local:8080/actuator/prometheus | head -n 5'
# HELP application_ready_time_seconds Time taken for the application to be ready to service requests
# TYPE application_ready_time_seconds gauge
application_ready_time_seconds{application="task-api",main_application_class="com.renda.task.TaskApiApplication"} 18.184
# HELP application_started_time_seconds Time taken to start the application
# TYPE application_started_time_seconds gauge
pod "curl" deleted

# 观察 ADOT 日志（更有效的关键字）：
# 默认日志不会打印 HTTP 200；
# 若 `--set config.service.telemetry.logs.level=debug` 重新部署，可看到 exporter 更详细的行为。
$ kubectl -n observability logs deploy/adot-collector-opentelemetry-collector --tail=300 | rg -i 'prometheusremotewrite|export|remote|aps|error|denied|retry|throttle'
2025/08/28 18:57:21 attn: users of the `datadog`, `logzio`, `sapm`, `signalfx` exporter components. please refer to https://github.com/aws-observability/aws-otel-collector/issues/2734 in regards to an upcoming ADOT Collector breaking change
2025-08-28T18:57:22.162Z        info    builders/builders.go:26 Development component. May change in the future.       {"kind": "exporter", "data_type": "traces", "name": "debug"}
2025-08-28T18:57:22.163Z        info    builders/builders.go:26 Development component. May change in the future.       {"kind": "exporter", "data_type": "logs", "name": "debug"}
2025-08-28T18:57:22.167Z        info    healthcheckextension@v0.117.0/healthcheckextension.go:32        Starting health_check extension {"kind": "extension", "name": "health_check", "config": {"Endpoint":"10.0.149.89:13133","TLSSetting":null,"CORS":null,"Auth":null,"MaxRequestBodySize":0,"IncludeMetadata":false,"ResponseHeaders":null,"CompressionAlgorithms":null,"ReadTimeout":0,"ReadHeaderTimeout":0,"WriteTimeout":0,"IdleTimeout":0,"Path":"/","ResponseBody":null,"CheckCollectorPipeline":{"Enabled":false,"Interval":"5m","ExporterFailureThreshold":5}}}
```

直接手动进入 observability 命名空间的临时 debug 容器进行 curl 访问：

```bash
$ kubectl -n observability run debug -it --image=curlimages/curl:8.10.1 --rm -- sh
If you don't see a command prompt, try pressing enter.
~ $ curl -sf http://task-api.svc-task.svc.cluster.local:8080/actuator/health
{"status":"UP","groups":["liveness","readiness"]}
~ $ exit
Session ended, resume using 'kubectl attach debug -c debug -i -t' command when the pod is running
pod "debug" deleted
```

如果 pod 自动删除失败，执行如下命令检查并删除：

```bash
kubectl -n observability get pod curl
kubectl -n observability logs pod curl
kubectl -n observability delete pod curl
```

命名空间检查：

```bash
$ kubectl -n observability get pods
NAME                                                      READY   STATUS    RESTARTS   AGE
adot-collector-opentelemetry-collector-7f5476f4d8-llp6c   1/1     Running   0          17m

$ kubectl -n svc-task get pods
NAME                        READY   STATUS    RESTARTS   AGE
task-api-7c8b778754-95fcm   1/1     Running   0          3h34m
task-api-7c8b778754-r2lh9   1/1     Running   0          4h1m

$ kubectl -n kube-system get pods
NAME                                                         READY   STATUS    RESTARTS   AGE
aws-load-balancer-controller-8574d469c6-dq64b                1/1     Running   0          4h9m
aws-load-balancer-controller-8574d469c6-kt4ln                1/1     Running   0          4h9m
aws-node-gjcnn                                               2/2     Running   0          15m
aws-node-tqfb8                                               2/2     Running   0          4h11m
cluster-autoscaler-aws-cluster-autoscaler-5877d8fbb9-m82fx   1/1     Running   0          4h3m
coredns-5d849c4789-5bz4c                                     1/1     Running   0          4h25m
coredns-5d849c4789-rxjl6                                     1/1     Running   0          4h25m
kube-proxy-d4psb                                             1/1     Running   0          4h11m
kube-proxy-xt25h                                             1/1     Running   0          15m
metrics-server-cdbfbb9d5-7ctpw                               1/1     Running   0          4h1m
```

---
