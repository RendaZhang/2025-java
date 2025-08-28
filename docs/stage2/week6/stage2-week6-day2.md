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

# 等待就绪并查看日志
kubectl -n observability rollout status deploy/adot-collector-opentelemetry-collector --timeout=180s
kubectl -n observability logs deploy/adot-collector-opentelemetry-collector --tail=200 | egrep -i 'prometheusremotewrite|sigv4|wrote|export|remote write|success|200'
```

> `post-recreate.sh` 和 `pre-teardown.sh` 脚本已经同步更新以整合 ADOT Collector 到重建和销毁流程之中。

**已经确认**：`rollout status` 显示 `successfully rolled out`。

触发请求（命中 http_server_requests_*）：

```bash
kubectl -n "$NS" run hit --image=curlimages/curl:8.10.1 -it --rm -- \
  sh -lc 'for i in $(seq 1 150); do curl -s -o /dev/null http://task-api.svc-task.svc.cluster.local:8080/actuator/health; done; echo done'
```

检查日志：

```bash
kubectl -n observability logs deploy/adot-collector-opentelemetry-collector --tail=200 | egrep -i 'prometheusremotewrite|wrote|200'

# 日志里出现向 AMP 发送样本的迹象（如 `prometheusremotewrite`, `sigv4`, `200`, `wrote X samples` 等字样）。
# SigV4 的使用与 `prometheusremotewrite` 的结合方式见官方示例。
```

> 若短时间没看到写入，请继续执行触发请求命令来打一小波流量促进指标增长。

---
