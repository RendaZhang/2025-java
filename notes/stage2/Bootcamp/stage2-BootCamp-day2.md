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



