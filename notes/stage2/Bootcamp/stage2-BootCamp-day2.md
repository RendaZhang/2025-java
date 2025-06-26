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



