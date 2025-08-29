<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 3 — Grafana 出图 + 建立 SLI/SLO 口径](#day-3--grafana-%E5%87%BA%E5%9B%BE--%E5%BB%BA%E7%AB%8B-slislo-%E5%8F%A3%E5%BE%84)
- [第一步：预检与决策（变量、命名、查询端点、存在性检查）](#%E7%AC%AC%E4%B8%80%E6%AD%A5%E9%A2%84%E6%A3%80%E4%B8%8E%E5%86%B3%E7%AD%96%E5%8F%98%E9%87%8F%E5%91%BD%E5%90%8D%E6%9F%A5%E8%AF%A2%E7%AB%AF%E7%82%B9%E5%AD%98%E5%9C%A8%E6%80%A7%E6%A3%80%E6%9F%A5)
  - [第二步：Terraform 给 Grafana 配 IRSA，仅改必要项](#%E7%AC%AC%E4%BA%8C%E6%AD%A5terraform-%E7%BB%99-grafana-%E9%85%8D-irsa%E4%BB%85%E6%94%B9%E5%BF%85%E8%A6%81%E9%A1%B9)
    - [在 Terraform 中需要做的修改点](#%E5%9C%A8-terraform-%E4%B8%AD%E9%9C%80%E8%A6%81%E5%81%9A%E7%9A%84%E4%BF%AE%E6%94%B9%E7%82%B9)
    - [本地自检命令](#%E6%9C%AC%E5%9C%B0%E8%87%AA%E6%A3%80%E5%91%BD%E4%BB%A4)
    - [`terraform apply` **输出**](#terraform-apply-%E8%BE%93%E5%87%BA)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 3 — Grafana 出图 + 建立 SLI/SLO 口径

今日目标：

1. 在集群内安装 **Grafana（OSS，Helm）**，通过 **SigV4** 连接昨天的 **AMP Workspace**（只读）。
2. 用最少的 3 个图表（QPS、错误率、P95）做出**可面试**的基本仪表盘，并加上变量（命名空间/应用）。
3. 形成 **SLI/SLO 口径**（公式 + 阈值建议），并截取关键截图作为证据。

> 成本策略：Grafana OSS 无云账单；查询 AMP 会有极小量 Query 费用（可通过缩小时间范围、降低刷新频率来控成本）。

---

# 第一步：预检与决策（变量、命名、查询端点、存在性检查）

**目标**：

确定今天会用到的变量与命名，确认 AMP 的“查询端点”，检查是否已有同名资源，避免后续冲突。

在终端执行：

```bash
WORK_DIR=.
# 进入仓库根目录
cd $WORK_DIR

# 载入变量
set -a; source ./.env.week6.local; set +a
echo "AWS_PROFILE=$AWS_PROFILE"
echo "AWS_REGION=$AWS_REGION"
echo "AMP_WORKSPACE_ID=$AMP_WORKSPACE_ID"

# 登录 aws
aws sso login

# 计算 AMP 的“查询根地址”（注意：这不是 remote_write）
AMP_QUERY_BASE=$(
  aws amp describe-workspace \
    --region "$AWS_REGION" \
    --workspace-id "$AMP_WORKSPACE_ID" \
    --query 'workspace.prometheusEndpoint' --output text
)
echo "[week6] AMP_QUERY_BASE=$AMP_QUERY_BASE"

# 3) 今天要用到的“建议命名”（你可沿用/修改；我后续按这组名称给命令）
export GF_NS=observability
export GF_RELEASE=grafana
export GF_SA=grafana
export GF_IAM_ROLE_NAME=grafana-amp-query     # Terraform 会创建这个角色

printf "[week6] NS=%s, RELEASE=%s, SA=%s, IAM_ROLE=%s\n" "$GF_NS" "$GF_RELEASE" "$GF_SA" "$GF_IAM_ROLE_NAME"

# 4) 存在性检查（避免名称冲突）
kubectl get ns "$GF_NS" --show-labels
helm -n "$GF_NS" ls | grep -E "^$GF_RELEASE\b" || echo "[week6] helm release <$GF_RELEASE> not found (good)"
kubectl -n "$GF_NS" get sa "$GF_SA" || echo "[week6] serviceaccount <$GF_SA> not found (good)"
```

输出：

```bash
AWS_PROFILE=phase2-sso
AWS_REGION=us-east-1
AMP_WORKSPACE_ID=ws-4c9b04d5-5e49-415e-90ef-747450304dca

...
Successfully logged into Start URL: https://d-9066388969.awsapps.com/start

[week6] AMP_QUERY_BASE=https://aps-workspaces.us-east-1.amazonaws.com/workspaces/ws-4c9b04d5-5e49-415e-90ef-747450304dca/

[week6] NS=observability, RELEASE=grafana, SA=grafana, IAM_ROLE=grafana-amp-query
```
---

## 第二步：Terraform 给 Grafana 配 IRSA，仅改必要项

1. 账户里已有与 EKS 集群匹配的 **IAM OIDC Provider**（已经有）。
2. 新建 **IAM Role：`grafana-amp-query`**，**只附加** 托管策略：`AmazonPrometheusQueryAccess`。
3. 该 Role 的 **信任策略** 将权限绑定到 **唯一的 SA**：`system:serviceaccount:observability:grafana`，并包含 `aud=sts.amazonaws.com`。
4. ServiceAccount **由 Helm 创建**；因此 Terraform **不创建 SA**，仅输出 Role ARN。

### 在 Terraform 中需要做的修改点

* **data/引用** 当前集群的 **OIDC Issuer/Provider ARN**。
* **创建 IAM Role**：`name = grafana-amp-query`（可自定义）；
  * 信任策略 `assume_role_policy` 包含：
    * `Principal = { Federated = <你的 OIDC Provider ARN> }`
    * `Condition.StringEquals["<OIDC_ISSUER_HOST>/aud"] = "sts.amazonaws.com"`
    * `Condition.StringEquals["<OIDC_ISSUER_HOST>/sub"] = "system:serviceaccount:observability:grafana"`
* **attach policy**：`arn:aws:iam::aws:policy/AmazonPrometheusQueryAccess`。
* **输出**：`role_arn`。

### 本地自检命令

核对 TF 是否指向正确 Issuer。

```bash
# 推导当前集群与 issuer（和 Day2 做法一致）
CTX_CLUSTER=$(kubectl config view --minify -o jsonpath='{.contexts[0].context.cluster}')
CLUSTER=${CTX_CLUSTER##*/}
ISSUER=$(aws eks describe-cluster --name "$CLUSTER" --region "$AWS_REGION" --query 'cluster.identity.oidc.issuer' --output text)
ISSUER_HOST=${ISSUER#https://}
echo "[week6] cluster=$CLUSTER"
echo "[week6] issuer=$ISSUER"
echo "[week6] issuer_host=$ISSUER_HOST"
```

### `terraform apply` **输出**

1. **IAM Role ARN**（期望类似）：
   ```
   arn:aws:iam::<ACCOUNT_ID>:role/grafana-amp-query
   ```
2. **信任策略的关键字段**（节选即可）——我需要看到这两行的实际值：
   * `"<OIDC_ISSUER_HOST>/aud": "sts.amazonaws.com"`
   * `"<OIDC_ISSUER_HOST>/sub": "system:serviceaccount:observability:grafana"`
3. （可选）你也可以用 CLI 验证并贴回：
   ```bash
   aws iam get-role --role-name grafana-amp-query --query 'Role.Arn' --output text
   aws iam get-role --role-name grafana-amp-query --query 'Role.AssumeRolePolicyDocument' --output json
   ```

> 这一步**不需要**在 K8s 里创建 `ServiceAccount/grafana`；
> 会在下一步（Helm 安装）里让 Helm 自己创建，
> 并在 values 里给它加上 `eks.amazonaws.com/role-arn=<上面 Role ARN>` 注解。

---
