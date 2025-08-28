<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 2 - ADOT Collector（采集 → AMP）+ 成本护栏](#day-2---adot-collector%E9%87%87%E9%9B%86-%E2%86%92-amp-%E6%88%90%E6%9C%AC%E6%8A%A4%E6%A0%8F)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [第一步：预检与环境准备（变量 / remote write / 命名空间）](#%E7%AC%AC%E4%B8%80%E6%AD%A5%E9%A2%84%E6%A3%80%E4%B8%8E%E7%8E%AF%E5%A2%83%E5%87%86%E5%A4%87%E5%8F%98%E9%87%8F--remote-write--%E5%91%BD%E5%90%8D%E7%A9%BA%E9%97%B4)
  - [第二步：Terraform 创建 IRSA](#%E7%AC%AC%E4%BA%8C%E6%AD%A5terraform-%E5%88%9B%E5%BB%BA-irsa)
    - [在 Terraform 中需要做的修改（或新增的资源）](#%E5%9C%A8-terraform-%E4%B8%AD%E9%9C%80%E8%A6%81%E5%81%9A%E7%9A%84%E4%BF%AE%E6%94%B9%E6%88%96%E6%96%B0%E5%A2%9E%E7%9A%84%E8%B5%84%E6%BA%90)
    - [快速自检命令](#%E5%BF%AB%E9%80%9F%E8%87%AA%E6%A3%80%E5%91%BD%E4%BB%A4)

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

### 在 Terraform 中需要做的修改（或新增的资源）

1. **创建一个 IAM Role（供 IRSA 使用）**
   * **信任策略（Assume Role Policy）关键点**：
     * Principal 为你账户中的 **OIDC Provider ARN**；
     * `aud` 条件：`sts.amazonaws.com`；
     * `sub` 条件：`system:serviceaccount:observability:adot-collector`（命名空间与 SA 名必须与下方一致）。
   * **权限**：**附加托管策略** `arn:aws:iam::aws:policy/AmazonPrometheusRemoteWriteAccess`（不需要别的策略）。
2. **在 K8s 创建 ServiceAccount 并加注解**
   * **命名空间**：`observability`（你已创建）。
   * **ServiceAccount 名**：`adot-collector`。
   * **关键注解**：`eks.amazonaws.com/role-arn=<上面 IAM Role 的 ARN>`。
   * 先让 TF 输出 Role ARN，再用 `kubectl`/manifests 创建 SA——任选其一，但**最终要有这条注解**。

> 小提示：如果你使用的是流行的 IRSA 模块（例如 *iam-role-for-service-accounts-eks*），模块参数里通常有：
>
> * `oidc_provider_arn` / `oidc_provider_url`
> * `role_name`
> * `policy_arns`（放入 `AmazonPrometheusRemoteWriteAccess`）
> * `namespace_service_accounts = { "observability" = ["adot-collector"] }`
>   具体字段名依模块不同，保证上面“事实条件”成立即可。

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
