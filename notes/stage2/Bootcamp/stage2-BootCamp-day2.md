# Bootcamp Day 2 · EKS 集群落地 + Terraform 绑定（NodeGroup 版）

---

## 环境预检

> 目标：确定你当前 WSL 终端已经登录 `phase2-sso`，Region 设为 **us-east-1**，并确认 VPC/EC2 相关配额充足，避免后续集群创建中途失败。


### 检查 AWS CLI 登录状态 & 默认 Region

```bash
# A. 验证当前 profile & 区域
aws configure list

# B. 若 profile / region 不对，先切换
export AWS_PROFILE=phase2-sso
export AWS_REGION=us-east-1      # 或 aws configure set region us-east-1 --profile phase2-sso
```

终端应显示：

```bash
Name        Value                 Type           Location
----        -----                 ----           --------
profile     phase2-sso            env            ['AWS_PROFILE', 'AWS_DEFAULT_PROFILE']
access_key  ****************BHNY  sso
secret_key  ****************69b2  sso
region      us-east-1             config-file    ~/.aws/config
```

### 快速 Service Quotas 自检

#### 核心配额与正确的 QuotaCode

| 资源类别            | Quota 名称 - 控制台显示                                         | ServiceCode            | **Quota Code**                         | 默认值    | 建议阈值     |
| --------------- | -------------------------------------------------------- | ---------------------- | ------------------------------------- | ------ | -------- |
| ENI 数量          | Network interfaces per Region                            | `vpc`                  | **L-DF5E4CA3** | 5 000  | ≥ 5 000  |
| VPC 数量          | VPCs per Region                                          | `vpc`                  | L-F678F1CE     | 5      | 5 (默认即可) |
| SG / ENI        | Security groups per network interface                    | `vpc`                  | L-2AFB9258            | 5      | 5–10     |
| **vCPU (按需)**   | Running On-Demand Standard (A,C,D,H,I,M,R,T,Z) instances | `ec2`                  | **L-1216C47A** | 5 vCPU | ≥ 20     |
| **vCPU (Spot)** | All Standard (A,C,D,H,I,M,R,T,Z) Spot Instance Requests  | `ec2`                  | **L-34B43A08**   | 5 vCPU | ≥ 20     |
| ALB 数量          | Application Load Balancers per Region                    | `elasticloadbalancing` | L-53DA6B97  | 50     | 5 (足够)   |
| EIP 数量          | Elastic IPs (EC2-VPC)                                    | `ec2`                  | L-0263D0A3   | 5      | 5        |

#### CLI 快速查询与脚本示例

```bash
# 通用查询模板
svc=vpc ; code=L-DF5E4CA3
aws service-quotas get-service-quota \
     --service-code $svc --quota-code $code \
     --query 'Quota.[QuotaName,Value,Unit]' --output table

# 查看 EC2 标准族 Spot vCPU 上限
aws service-quotas get-service-quota \
     --service-code ec2 --quota-code L-34B43A08

# 一次列出 VPC 全部配额并筛选 ENI
aws service-quotas list-service-quotas --service-code vpc \
     --query 'Quotas[?contains(QuotaName, `Network interfaces`) ]'
```

- 如果命令返回 “No such quota”，先去 Console → Service Quotas 页面搜索同名配额，记下
它显示的代码，再回 CLI。
- 新账号找不到 Spot 相关代码？先在 Console “Request quota increase”——即使不提升也会同步出代码。

#### **目标值建议**

> * VPC ENI 每区 ≥ 250
> * EC2 Spot 实例 ≥ 5
> 
> 如果配额低于建议值，暂时不必申请提升；EKS 默认所需 ENI ≈ 10 以内、Spot t3.small × 2 远低于限制──但提前确认可避免出乎意料的 “LimitExceeded” 报错。

---

## 生成 `eksctl-cluster.yaml`

> 目标：用 **现成 VPC / 子网** ID 写出一份 `eksctl` 集群声明文件，包含：
> - 控制面放在私网子网；
> - 1 个 **Managed NodeGroup**：Spot *t3.small* × 2 + On-Demand *t3.medium* × 1；
> - 启用 OIDC（后续 IRSA / Autoscaler 要用）。

### 拿到 VPC & Subnet ID

```bash
# 进入 Terraform 目录
cd infra/aws

# 如果之前 destroy 过，请先 make start 或 terraform apply，保证 state 里有资源
terraform init                # 若已 init 可跳过
terraform apply -refresh-only # 让 state 同步最新真实资源（几秒完成）

# 现在再取输出
terraform output -raw vpc_id
terraform output -json public_subnet_ids
terraform output -json private_subnet_ids
```

> 如果显示 “Output … not found”，说明 根模块没有定义这些 outputs，这时候就需要在根 outputs.tf 中补上对应的输出。

记下结果，下一步的 YAML 会用到：
```bash
private_subnet_ids = [
  "subnet-0422bec13e7eec9e6",
  "subnet-00630bdad3664ee18",
]
public_subnet_ids = [
  "subnet-066a65e68e06df5db",
  "subnet-08ca22e6d15635564",
]
vpc_id = "vpc-0b06ba5bfab99498b"
```

### 创建文件 `eksctl-cluster.yaml`

#### 全 Spot 实例配置

放在仓库根 `scripts/` 或临时目录——内容示例（请按你的真实 ID 替换）：

```yaml
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig

metadata:
  name: dev
  region: us-east-1
  version: "1.30"

vpc:
  id: "vpc-0b06ba5bfab99498b"
  subnets:
    private:
      us-east-1a: { id: "subnet-0422bec13e7eec9e6" }
      us-east-1b: { id: "subnet-00630bdad3664ee18" }
    public:
      us-east-1a: { id: "subnet-066a65e68e06df5db" }
      us-east-1b: { id: "subnet-08ca22e6d15635564" }

iam:
  # 为后续 IRSA / Autoscaler 打基础
  withOIDC: true
  # 指向手动建的 Role，最少权限：AmazonEKSClusterPolicy + AmazonEKSVPCResourceController
  serviceRoleARN: "arn:aws:iam::563149051155:role/eks-admin-role"

managedNodeGroups:
  - name: ng-mixed
    minSize: 0
    desiredCapacity: 3
    maxSize: 6
    # 3 × Spot (Random: t3.small or t3.medium)
    instanceTypes: ["t3.small","t3.medium"]
    spot: true
    privateNetworking: true
    labels: { role: "worker" }
    tags:
      project: phase2-sprint
    updateConfig:
      maxUnavailable: 1
    # 可选：限制 Spot 最高价（按需 70%）
    # spotMaxPrice: "0.026"
    subnets:
      - "subnet-0422bec13e7eec9e6"
      - "subnet-00630bdad3664ee18"

```

#### 混合 Spot + OD 实例配置

如果要实现 `2×Spot (t3.small) + 1×OD (t3.medium)` 的精确控制，必须使用 `instancesDistribution` 配置：
```yaml
managedNodeGroups:
  - name: ng-mixed
    minSize: 0
    desiredCapacity: 3
    maxSize: 6
    # 关键配置：混合实例策略
    instancesDistribution:
      instanceTypes: ["t3.small", "t3.medium"] # 把需要的按需类型放第一位
      onDemandBaseCapacity: 1     # 保证至少1个按需实例
      onDemandPercentageAboveBaseCapacity: 0  # 其余100%使用Spot
      spotInstancePools: 2        # 使用2种Spot实例类型
    privateNetworking: true
    labels: { role: "worker" }
    tags: { project: phase2-sprint }
    updateConfig: { maxUnavailable: 1 }
    subnets:
      - "subnet-0422bec13e7eec9e6"
      - "subnet-00630bdad3664ee18"
```

**要点**

* 控制面默认落在 **Private Subnet**（EKS 会自动选取）。
* `withOIDC: true` 之后，后续 **Cluster Autoscaler / IRSA** 可直接关联 IAM Policy。
* `desiredCapacity` = 3，但 Cluster Autoscaler 安装后可自动缩放。
* 容量验证命令：`kubectl get nodes -L node.kubernetes.io/instance-type,eks.amazonaws.com/capacityType`。
* `onDemandBaseCapacity: 1`：强制创建1个按需实例，EKS 会优先使用列表中的第一个实例类型（`t3.small`）。

---

## `eksctl create cluster` 并等待 CloudFormation 完成

> 目标：把你刚写好的 `infra/eksctl/eksctl-cluster.yaml` 真正跑起来，拉起控制面与 3 台节点，并生成 `cluster-info.txt` 备档。整个流程通常 12 – 18 分钟。

### 确保凭证 & 区域

```bash
# 如果刚打开新终端，先刷新 SSO
aws sso login --profile phase2-sso

export AWS_PROFILE=phase2-sso
export AWS_REGION=us-east-1
```

### 安装 eksctl

安装前置依赖：`aws` 和 `kubectl`。

AWS 官方建议只用 GitHub Release 中的原生二进制，避免第三方源版本滞后或带私补丁。

```bash
cd /tmp
# 一行脚本拉最新稳定版（会自动解析你的架构）
curl -L -o eksctl.tar.gz   https://github.com/eksctl-io/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz
# 解压并移动
tar xz -C /tmp -f eksctl.tar.gz
sudo mv /tmp/eksctl /usr/local/bin/
# 检查
eksctl version
```

### 创建集群

在仓库根（或任何目录）执行：

```bash
eksctl create cluster -f infra/eksctl/eksctl-cluster.yaml \
  --profile "$AWS_PROFILE" --kubeconfig ~/.kube/config  \
  --verbose 3                  # 可选：更详细日志
```

**你会看到：**

1. eksctl 先创建 **CloudFormation Stack** `eksctl-dev-cluster`
   1. 
2. 下载 IAM OIDC provider & VPC configs
3. 创建 **EKS 控制面**（\~10 min）
4. 创建 **Managed NodeGroup**（\~3 min）
5. 写入 `~/.kube/config` 并测试 `kubectl` 连接


### 观察进度（可选）

```bash
# 实时看 Stack 事件
aws cloudformation describe-stack-events --stack-name eksctl-dev-cluster \
  --query 'StackEvents[0:5].[ResourceStatus,ResourceType,LogicalResourceId]' \
  --output table --profile $AWS_PROFILE --region $AWS_REGION --no-paginate
# 输出示例：
-------------------------------------------------------------------------------------------
|                                   DescribeStackEvents                                   |
+--------------------+----------------------------------+---------------------------------+
|  CREATE_COMPLETE   |  AWS::CloudFormation::Stack      |  eksctl-dev-cluster             |
|  CREATE_COMPLETE   |  AWS::EC2::SecurityGroupIngress  |  IngressDefaultClusterToNodeSG  |
|  CREATE_COMPLETE   |  AWS::EC2::SecurityGroupIngress  |  IngressNodeToDefaultClusterSG  |
|  CREATE_IN_PROGRESS|  AWS::EC2::SecurityGroupIngress  |  IngressNodeToDefaultClusterSG  |
|  CREATE_IN_PROGRESS|  AWS::EC2::SecurityGroupIngress  |  IngressDefaultClusterToNodeSG  |
+--------------------+----------------------------------+---------------------------------+
```

### 集群验证

当 eksctl 打印 `kubectl get nodes --watch` 时，等待出现 **3 Ready**：

```bash
# 使用 AWS CLI 验证
aws eks describe-cluster --name dev --region us-east-1 --profile phase2-sso
# 使用 AWS 检查节点组
aws eks list-nodegroups --cluster-name dev --region us-east-1 --profile phase2-sso
# 使用 kubectl 检查
kubectl get nodes -o wide
# 检查节点组成
kubectl get nodes -L node.kubernetes.io/instance-type,eks.amazonaws.com/capacityType
# 确认组件健康
kubectl get cs
# 检查 OIDC 是否最终启用
# 应返回类似：https://oidc.eks.us-east-1.amazonaws.com/id/E0204AE78E971608F5B7BDCE0379F55F
aws eks describe-cluster --name dev --query "cluster.identity.oidc.issuer" --output text --profile phase2-sso
# 检查所有资源
# 当创建 EKS 集群时，会创建服务角色（如 `eks-admin-role`）
# IAM 更改可能需要时间全局传播（通常几秒到几分钟）
# `eksctl get` 命令需要调用 STS 获取当前身份，如果 IAM 角色未完全生效，会报错。
eksctl get cluster --region us-east-1 --profile phase2-sso
eksctl get cluster --name dev --region us-east-1 --profile phase2-sso
eksctl get nodegroup --cluster dev --region us-east-1 --profile phase2-sso
```

如果想验证服务负载：

```bash
# 投放一个示例 Pod
kubectl run nginx --image=nginx -n default --restart=Never
kubectl expose pod nginx --port 80 --type ClusterIP
# 测试完毕后进行清理
kubectl delete service nginx
kubectl delete pod nginx
# 检查 Pod
kubectl get pods
# 检查 Service
kubectl get services
```

---

## 开启控制面日志 (API & Authenticator)

> 目标：把 **API** 与 **Authenticator** 两类日志接入 CloudWatch，方便后续 SRE 排障与成本分析。完成后应在 CloudWatch > Log groups 看到 `/aws/eks/dev/cluster` 下的 `api` 与 `authenticator` 子流。

### 启用日志

```bash
# 在项目根或任意目录执行
eksctl utils update-cluster-logging --cluster dev --region us-east-1 --enable-types api,authenticator --profile phase2-sso --approve
```

* eksctl 会生成一个 CloudFormation Stack `eksctl-dev-cluster-logging`，过程 < 2 min。
* 成功后 CLI 显示 `updated cluster logging`.

### 验证日志配置 & CloudWatch LogGroup

```bash
aws eks describe-cluster --name dev --profile phase2-sso --region us-east-1 --query "cluster.logging.clusterLogging[?enabled].types" --output table
```

应看到：
```bash
--------------------------
|     DescribeCluster    |
+------+-----------------+
|  api |  authenticator  |
+------+-----------------+
```

```bash
aws logs describe-log-groups --profile phase2-sso --region us-east-1 --log-group-name-prefix "/aws/eks/dev/cluster" --query 'logGroups[].logGroupName' --output text
```

应看到：

```
/aws/eks/dev/cluster
```

进入 AWS Console ➜ CloudWatch ➜ Logs ➜ Log groups ➜ 该组里面应出现
`api`、`authenticator` 流（稍等 1–2 min 有首批条目）。

```bash
## Log streams: 
authenticator-9db45ef355ac2c7f857a5994e1931f3b 2025-06-26 15:06:10 (UTC)
authenticator-46f5034735ad5a31785c0e0af6ace8e0 2025-06-26 15:06:10 (UTC)
kube-apiserver-46f5034735ad5a31785c0e0af6ace8e0 2025-06-26 15:04:45 (UTC)
kube-apiserver-9db45ef355ac2c7f857a5994e1931f3b 2025-06-26 15:03:17 (UTC)
```

---

## 安装 Cluster Autoscaler - IRSA 版

> **目标**
>
> 1. 给集群绑定 OIDC Provider（若 eksctl 创建时已自动启用，可跳过脚本确认）。
> 2. 为 Autoscaler 创建 **专属 ServiceAccount + IAM 角色**（最小权限）。
> 3. 使用 Helm 安装 Cluster Autoscaler，并验证节点能按负载自动伸缩。

### 检查 / 关联 OIDC Provider

```bash
# 检查当前 EKS 集群是否已启用 OIDC
eksctl utils associate-iam-oidc-provider --cluster dev --region us-east-1 --profile phase2-sso --approve=false
# 输出如果类似下面这样，就说明 已存在 OIDC：
2025-06-26 23:32:45 [ℹ]  IAM Open ID Connect provider is already associated with cluster "dev" in "us-east-1"

# 通过 AWS CLI 检查 IAM OIDC Provider 是否已存在
aws eks describe-cluster --name dev --region us-east-1 --profile phase2-sso --query "cluster.identity.oidc.issuer" --output text
# 返回一串类似如下的 URL 说明 EKS 已经绑定了 OIDC Provider
https://oidc.eks.us-east-1.amazonaws.com/id/E0204AE78E971608F5B7BDCE0379F55F

# 如未看到 OIDC ARN，则关联：
eksctl utils associate-iam-oidc-provider --cluster dev --region us-east-1 --approve --profile phase2-sso
```

> 命令完成后，再次执行 `describe-cluster` 确认 OIDC ARN 存在。

### 创建 IAM Policy & Role（IRSA）

IRSA: IAM roles for service accounts

#### 下载官方最小策略

```bash
cat > autoscaler-iam-policy.json <<'EOF'
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "autoscaling:DescribeAutoScalingGroups",
        "autoscaling:DescribeAutoScalingInstances",
        "autoscaling:DescribeLaunchConfigurations",
        "autoscaling:DescribeTags",
        "autoscaling:SetDesiredCapacity",
        "autoscaling:TerminateInstanceInAutoScalingGroup",
        "ec2:DescribeLaunchTemplateVersions"
      ],
      "Resource": "*"
    }
  ]
}
EOF
```

#### 创建 IAM Policy

```bash
POLICY_ARN=$(aws iam create-policy --policy-name "EKSClusterAutoscalerPolicy" --policy-document file://autoscaler-iam-policy.json --query 'Policy.Arn' --output text --profile phase2-sso)
# 检查
echo $POLICY_ARN
# 输出
arn:aws:iam::563149051155:policy/EKSClusterAutoscalerPolicy
```

#### 创建具有信任策略的 IAM Role

```bash
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text --profile phase2-sso)
# 检查
echo $ACCOUNT_ID
# 输出
563149051155

cat > trust-policy.json <<EOF
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": { "Federated": "arn:aws:iam::$ACCOUNT_ID:oidc-provider/$(aws eks describe-cluster --name dev --region us-east-1 --profile phase2-sso --query 'cluster.identity.oidc.issuer' --output text | sed -e "s|https://||")" },
    "Action": "sts:AssumeRoleWithWebIdentity",
    "Condition": { "StringEquals": { "$(aws eks describe-cluster --name dev --region us-east-1 --profile phase2-sso --query 'cluster.identity.oidc.issuer' --output text | sed -e "s|https://||"):sub": "system:serviceaccount:kube-system:cluster-autoscaler" } }
  }]
}
EOF

ROLE_ARN=$(aws iam create-role --role-name eks-cluster-autoscaler --assume-role-policy-document file://trust-policy.json --query 'Role.Arn' --output text --profile phase2-sso)
# 检查
echo $ROLE_ARN
# 输出
arn:aws:iam::563149051155:role/eks-cluster-autoscaler

aws iam attach-role-policy --role-name eks-cluster-autoscaler --policy-arn "$POLICY_ARN" --profile phase2-sso
```

> 记下 **`ROLE_ARN`**，稍后 Helm Chart 需要用到。
>
> `ROLE_ARN` = `arn:aws:iam::563149051155:role/eks-cluster-autoscaler`

### 安装 Cluster Autoscaler（Helm）

```bash
# 下载官方安装脚本 & 安装
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
# 检查
helm version

# 添加 repo & 安装 chart
# 预期输出："autoscaler" has been added to your repositories
helm repo add autoscaler https://kubernetes.github.io/autoscaler
# 更新
helm repo update

# 用 Helm 安装或升级一个名叫 cluster-autoscaler 的 Kubernetes 服务（具体 chart 来自 autoscaler 仓库），部署到 kube-system 命名空间，并传入了一堆自定义参数。
helm upgrade --install cluster-autoscaler autoscaler/cluster-autoscaler -n kube-system --create-namespace \
  --set awsRegion=us-east-1 \
  --set autoDiscovery.clusterName=dev \
  --set rbac.serviceAccount.create=true \
  --set rbac.serviceAccount.name=cluster-autoscaler \
  --set extraArgs.balance-similar-node-groups=true \
  --set extraArgs.skip-nodes-with-system-pods=false \
  --set rbac.serviceAccount.annotations."eks\\.amazonaws\\.com/role-arn"="arn:aws:iam::563149051155:role/eks-cluster-autoscaler" \
  --set image.tag=v1.30.0 # Replace 1.30 with your k8s server version

# To verify that cluster-autoscaler has started, run:
kubectl --namespace=kube-system get pods -l "app.kubernetes.io/name=aws-cluster-autoscaler,app.kubernetes.io/instance=cluster-autoscaler"

# 检查 serviceAccountName 名字是否一致
# 预期输出是 cluster-autoscaler
kubectl -n kube-system get pod -l app.kubernetes.io/name=aws-cluster-autoscaler -o jsonpath="{.items[0].spec.serviceAccountName}"

# 如果 Helm 部署失败，重新部署后，需要执行以下命令删除旧 Pod，让 Deployment 拉新配置
kubectl -n kube-system delete pod -l app.kubernetes.io/name=aws-cluster-autoscaler

```

> Chart 会自动创建 `Deployment` + `ServiceAccount` 并注入 IRSA Annotation。

### 验证 Autoscaler 工作

#### 确认 Pod Ready

```bash
# 检查 POD 是否 READY
kubectl -n kube-system get pod -l app.kubernetes.io/name=aws-cluster-autoscaler
# 日志里应看到 Scale up/down，没有再用 NodeInstanceRole
kubectl -n kube-system logs -l app.kubernetes.io/name=aws-cluster-autoscaler --tail=30
# 检查是否成功 Rollout
kubectl -n kube-system rollout status deployment/cluster-autoscaler-aws-cluster-autoscaler
# 查看动态日志
kubectl -n kube-system logs -f deployment/cluster-autoscaler-aws-cluster-autoscaler | grep -i "autoscaler"
```

#### 触发扩容 / 缩容

```bash
###############################################################################
# 1) 创建一个基本 Deployment（先不上资源请求）
###############################################################################
kubectl create deployment cpu-hog --image=busybox \
  -- /bin/sh -c "while true; do :; done"
# 解释：BusyBox 里用死循环吃 CPU，先生成 1 个副本

###############################################################################
# 2) 给 Deployment 加上 CPU Request
###############################################################################
kubectl set resources deployment cpu-hog \
  --requests=cpu=400m
# 解释：要求 0.4 vCPU，等会儿我们会把副本数调到 20，保证触发扩容

###############################################################################
# 3) 放大副本，制造 8 vCPU 的瞬时需求
###############################################################################
kubectl scale deployment cpu-hog --replicas=20

###############################################################################
# 4) 观察节点 & Pod 调度（开两个终端窗口更直观）
###############################################################################
# 4-a 查看节点规模变化
kubectl get nodes -w
# 4-b 查看 Pod 状态
kubectl get pods -l app=cpu-hog -w
# 4-c 看 Cluster Autoscaler 日志（确认它在决策）
kubectl -n kube-system logs -l app.kubernetes.io/name=aws-cluster-autoscaler -f --tail=20

# ⏱️ 等 5-10 分钟：你应当看到
#   · Autoscaler 日志出现 “Scale up” 字样
#   · 新 EC2 节点加入 Ready
#   · cpu-hog 的 Pod 从 Pending 变 Running

###############################################################################
# 5) 测试缩容：删除 Deployment，观察节点回收
###############################################################################
kubectl delete deployment cpu-hog

# 同样用 `kubectl get nodes -w` + Autoscaler 日志
# 大约 10-20 分钟后会看到 Scale-down，并自动终止空闲节点
###############################################################################
```

> 看到 Autoscaler 日志中 `scale up` 和稍后 `scale down`，以及 Node 数量随之变化，即验证成功。

---

## 把集群资源导入 Terraform

> **目标**
>
> 1. 把刚创建好的 **EKS Cluster、OIDC Provider、Managed NodeGroup、IAM 角色** 等资源全部纳入 `infra/aws` 的 Terraform 状态；
> 2. 运行 `terraform plan` 显示 **“No changes”**，证明无漂移；
> 3. 把导入脚本 & 日志存档到仓库（`terraform-import.log`）。

### 确保本地 Terraform 后端指向 **us-east-1**

```bash
cd infra/aws
terraform init   # 若刚才切换终端，先刷新 SSO 再 init
```
