# Day 1 · AWS VPC / ALB / IAM

---

## VPC Wizard 实操

### VPC

VPC ID: vpc-0e707170d90e574bb

#### Your AWS virtual network

```
dev-vpc
10.0.0.0/16
No IPv6
```

#### Subnets within this VPC

```
dev-subnet-public1-ap-southeast-1a
10.0.0.0/20
```

```
dev-subnet-private1-ap-southeast-1a
10.0.128.0/20
```

```
dev-subnet-public2-ap-southeast-1b
10.0.16.0/20
```

```
dev-subnet-private2-ap-southeast-1b
10.0.144.0/20
```

#### Route network traffic to resources

```
dev-rtb-public
2 subnet associations
2 routes including local
```

```
rtb-0902296680c7826d1
No subnet associations
1 route including local
```

```
dev-rtb-private2-ap-southeast-1b
1 subnet association
3 routes including local
```

```
dev-rtb-private1-ap-southeast-1a
1 subnet association
3 routes including local
```

#### Connections to other networks

```
dev-igw
Internet routes to 2 public subnets
2 private subnets route to the Internet
```

```
dev-nat-public1-ap-southeast-1a
Public NAT gateway
1 ENI with 1 EIP
```

```
dev-vpce-s3
Gateway endpoint to S3
```

---

## NAT & Route 校验

### 创建 **Public** 实例

1. 打开 **EC2 Console ▸ Instances ▸ Launch instance**
2. **AMI**：Amazon Linux 2023、x86\_64
3. **Instance type**：t2.micro（免费层）
4. **Network settings** →
   * VPC：`dev-vpc`
   * Subnet：选择 *public1*
   * **Auto-assign public IP**：**Enable**
   * Security group：允许 SSH 或 Session Manager（推荐“允许所有流出”即可）
5. 命名：`pub-test` → Launch

### 创建 **Private** 实例

1. 再 Launch instance：
   * 同样 AMI / 类型
   * Subnet：选择 *private1*
   * **Auto-assign public IP**：**Disable**（保持默认）
2. 命名：`pri-test` → Launch
3. **连接方式**：勾选 **Enable SSM**（或在 IAM Role 里附加 `AmazonSSMManagedInstanceCore`，这样不用公网 IP 也能登录）。

### 验证出网&路由

1. **连接 pub-test**（SSH 或 EC2 Instance Connect）：
   ```bash
   [ec2-user@ip-10-0-2-199 ~]$ curl -s ifconfig.me # 返回公网 IPv4
   18.143.103.157
   [ec2-user@ip-10-0-2-199 ~]$
   [ec2-user@ip-10-0-2-199 ~]$ TOKEN=$(curl -sX PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 60")
   [ec2-user@ip-10-0-2-199 ~]$
   [ec2-user@ip-10-0-2-199 ~]$ curl -s -H "X-aws-ec2-metadata-token: $TOKEN" http://169.254.169.254/latest/meta-data/public-ipv4
   18.143.103.157
   ```
   * 两条输出应一致（直接通过 IGW 出网）。

2. **连接 pri-test**（使用 Session Manager ➜ Shell）：
   ```bash
   sh-5.2$ curl -s ifconfig.me
   54.151.176.59
   sh-5.2$
   sh-5.2$ TOKEN=$(curl -sX PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 60")
   sh-5.2$
   sh-5.2$ curl -s -H "X-aws-ec2-metadata-token: $TOKEN" http://169.254.169.254/latest/meta-data/public-ipv4 # 预期 404
   <?xml version="1.0" encoding="iso-8859-1"?>
   <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
   <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
     <title>404 - Not Found</title>
    </head>
    <body>
     <h1>404 - Not Found</h1>
    </body>
   </html>
   ```
   * 第一条应返回 **NAT Gateway 的公网 EIP**（或与 Public 实例不同的 IP）。
   * 第二条应 404/无数据（私网实例本身无公网 IP）。

---

## 创建 & 验证 ALB

### 创建 Target Group

1. 进入 **EC2 Console ▸ Load Balancing ▸ Target Groups ▸ Create target group**。
2. **Target type** 选 **IP addresses**，因为稍后我们会直接把 CloudShell 内网 IP 注册进去。
3. **VPC** 选刚才的 **`dev-vpc`**。
4. 端口填写 **80 / HTTP**，**Health check path** 设为 `/`（L7 健康检查默认 5 xx 视为失败）。
5. 命名 `tg-python-demo` ➜ Create。

> *为什么选 IP 而不是实例？*
>
> * 省去为临时 Target 开安全组；IRSA 蓝绿发布时也会用到 IP-mode TargetGroup，提前练习。

### 部署临时 Python HTTP 服务器并注册 Target

1. **连接 `pub-test` 实例**

   ```bash
   sudo yum -y install python3
   nohup sudo python3 -m http.server 80 &
   ```
2. **修改安全组**
   * 找到 ALB 所用 SG（假设 `sg-0bf78fa180bd8c537`）。
   * 给 `pub-test` 的 SG 加一条：
     * **来源** = `sg-0bf78fa180bd8c537`
     * **端口** = 80 / TCP
3. **重注册 Target**
   * 在 **Target Groups ▸ Register targets**；
   * 选 “Instances” → 勾选 `pub-test` → Include as **instance ID** *或* 填入 `10.0.0.x` 私网 IP。
4. **等待健康检查**  (30 s × 2 次) → 状态应变 **healthy**。但是目前还没有任何 Load Balancer 把流量指向这个 Target Group，因此不会做健康检查。目前状态是 `Unused`，等你创建并关联 ALB 监听器后，它会先变成 initial → 几秒后变成 healthy（若探测通过）。

### 创建 Application Load Balancer

1. **EC2 ▸ Load Balancers ▸ Create load balancer ▸ Application Load Balancer**。
2. **基本配置**
   * Name：`alb-demo`
   * Scheme：**Internet-facing**
   * IP address type：**IPv4**
3. **Network mapping**
   * VPC：`dev-vpc`
   * Subnets：勾选 **2 个 public 子网**（`10.0.0.0/20`, `10.0.16.0/20`）确保跨 AZ。
4. **Security group**
   * 创建或选用允许 **0.0.0.0/0 → TCP 80** 的 SG。
5. **Listeners & routing**
   * Listener 1：**HTTP : 80** ➜ Target group 选 **`tg-python-demo`**。
6. 点击 **Create load balancer**；等待状态 **Active**。

### 验证 & 记录

1. 在负载均衡器详情页复制 **DNS name**。
2. DNS Name: alb-demo-1754203016.ap-southeast-1.elb.amazonaws.com
3. 本地浏览器访问 `http://<DNS-name>`，应显示目录索引或 200 OK。

---

## IAM 角色设计与权限验证

### 创建 `eks-admin-role`

1. 打开 **IAM Console → Roles → Create role**。
2. **Trusted entity** 选 **AWS service**，服务列表里搜索 **EKS** 并勾选 *EKS – Cluster*（这样角色可由控制面自动使用）。
3. **Attach policies**：搜索并勾选
   * **AmazonEKSClusterPolicy** 
   * **AmazonEKSVPCResourceController** 
4. **Role name** 填 `eks-admin-role`，描述写 “Phase-2 EKS admin for lab cluster”。
5. **Create role**，记录返回页面顶部的 **Role ARN**。
6. "EKSAdminRoleArn":"arn:aws:iam::563149051155:role/eks-admin-role"

> *为什么只挂两条？*
> 这两条托管策略已包含 EKS 控制面创建/标记节点 ENI、管理安全组等所需权限，是官方推荐的“起步组合”。

### 使用 IAM Policy Simulator 验证最小权限

1. 打开 **[https://policysim.aws.amazon.com/](https://policysim.aws.amazon.com/)** 左栏 **Roles** 选 `eks-admin-role`。
2. 右侧 **Actions** 搜索 **`s3:ListBucket`**；Resource 选 **All resources** → **Run simulation**。
   * 结果应显示 **“implicitDeny”**，证明当前角色对 S3 无权操作。
3. IAM Policy Simulator 的 S3 相关的 Action 的测试结果全部显示："denied Implicitly denied (no matching statement)"

> 最小权限原则要求“一开始默认拒绝，再按需加权”。

### 追加 S3 只读策略并复测

1. 回到 **Role 详情 → Add permissions → Attach policies**，搜索 **AmazonS3ReadOnlyAccess** ➜ Attach。
2. 刷新 **Policy Simulator**，重复 **`s3:ListBucket`** 测试：现在应是 **“allowed”**。
3. 再测试 **`s3:PutObject`**，仍应 **implicitDeny**。
4. IAM Policy Simulator 的 S3 跟 Read 相关的 Action 测试结果为 “allowed”，其他 Action 测试结果为 “denied - implicitDeny”

### 预告：OIDC Provider 关联

* **IRSA** 需要在集群创建后，将 EKS 的 OIDC Issuer URL 与 IAM 创建信任关系：
  ```bash
  eksctl utils associate-iam-oidc-provider --cluster <cluster-name> --approve
  ```
* 因为现在还没集群，先记住命令；明天 `eksctl create cluster` 完成后执行即可。

---

## Terraform stub 初始化

> **目标**：在 `infra/aws/` 目录放置 4 个基础文件，让明天直接 `terraform init && terraform plan` 即可导入现有 VPC、子网与 IAM 角色。
> 本步骤只“写文件 + `terraform init`”，**暂不 `apply`**。

### 目录结构

```text
your-repo/
└─ infra/
   └─ aws/
      ├─ backend.tf
      ├─ provider.tf
      ├─ variables.tf
      └─ terraform.tfvars
```

### `backend.tf` — 远端状态存储

> 先手动创建一次，之后所有环境都能复用同一个桶 / 表。

1. **创建 S3 Bucket**
   - 进 **S3 Console → Create bucket**
   - **Bucket name**：`phase2-tf-state-<your-uniq-suffix>`
      * 名字全局唯一，用你 GitHub 用户名或日期后缀防止重名。
   - **Region**：`us-east-1`（与项目一致）
   - **Block all public access**：保持默认（全部勾选✅）
   - **Object Ownership**：`ACLs disabled` + `Bucket owner preferred`
   - **Versioning**：**Enable**（这样可以回滚旧状态）
   - 其他保持默认 ➜ **Create bucket**
2. **创建 DynamoDB 表（用于锁）**
   - 进 **DynamoDB Console → Tables → Create table**
   - **Table name**：`tf-state-lock`
   - **Partition key**：`LockID` (String)
   - **Capacity mode**：`On-demand`（PAY\_PER\_REQUEST，省心省钱）
   - 其他默认 ➜ **Create table**
3. 把信息写进 backend.tf：
   ```hcl
   terraform {
      backend "s3" {
         bucket         = "phase2-tf-state-<your-uniq-suffix>"  # 你新建的桶名
         key            = "eks/lab/terraform.tfstate" # 桶里的对象路径
         region         = "us-east-1"
         dynamodb_table = "tf-state-lock"             # 刚建的锁表名
         encrypt        = true
      }
   }
   ```

> 到此，S3 桶每月几毛钱，DynamoDB 锁表几分钱，成本几乎可忽略。
> 
> **注意**：
> * `terraform init` 时若桶或表不存在，会直接报错，所以要先手动建好。
> * 以后若想迁移到别的桶，可运行 `terraform init -migrate-state`。

### `provider.tf`  — AWS Provider

1. 检查 profile 是不是 default
   - Terraform 调用 AWS Provider 的凭证链与 AWS CLI 完全一致。检查方法：
      ```
      aws configure list
      ```
   - 输出示例：
      ```
      Name           Value                  Type            Location
      ----           -----                  ----            --------
      profile        <not set>              None            None
      access_key     ****************RTAH   container-role    
      secret_key     ****************nBd4   container-role    
      region         us-east-1         env             ['AWS_REGION', 'AWS_DEFAULT_REGION']
      ```
   - 如果 Profile = <not set>，且你在 ~/.aws/credentials 里只配置了 [default]，那 Terraform 就会用它。
   - 如果你在机器上有多个 profile（[default], [work], [prod]…），且想用一个非 default，可在 provider "aws" 里加一行 profile = "work"，或者临时导出 AWS_PROFILE=work。
2. 把信息写进 provider.tf：
   ```hcl
   provider "aws" {
      region              = var.region
      # profile           = "default"      # 若本机用 named profile 可解注
      default_tags = {
         project = "phase2-sprint"
      }
   }
   ```

> default_tags 作用：为所有由此 provider 创建的 AWS 资源统一打标签；常见字段有 project, env, owner。

### `variables.tf` — 输入变量
```hcl
variable "region" {
  description = "AWS region"
  type        = string
}

variable "vpc_id" {
  description = "Existing VPC for EKS"
  type        = string
}

variable "public_subnet_ids" {
  description = "List of public subnet IDs"
  type        = list(string)
}

variable "private_subnet_ids" {
  description = "List of private subnet IDs"
  type        = list(string)
}

variable "eks_admin_role_arn" {
  description = "IAM role ARN with EKS admin permissions"
  type        = string
}
```

### `terraform.tfvars` — 具体值（按你环境填写）

- region：右上角 Region 切换器（如 United States (N. Virginia) us-east-1）
- vpc_id：VPC Console → Your VPCs → 复制 VPC ID (vpc-…)
- public_subnet_ids：VPC Console → Subnets → 过滤 VPC → 复制两行 Subnet ID (subnet-…) 且 Auto-assign public IP = yes
- private_subnet_ids: 同上，过滤 Name 包含 private；Auto-assign public IP = no
- eks_admin_role_arn：IAM Console → Roles → 搜 eks-admin-role → 详情页顶部 ARN
- 把信息写进 terraform.tfvars：
   ```hcl
   region              = "us-east-1"

   vpc_id              = "vpc-0e707170d90e574bb"

   public_subnet_ids   = [
   "subnet-xxxxxxxxxxxxxxxxx",  # 10.0.0.0/20
   "subnet-yyyyyyyyyyyyyyyyy"   # 10.0.16.0/20
   ]

   private_subnet_ids  = [
   "subnet-zzzzzzzzzzzzzzzzz",  # 10.0.128.0/20
   "subnet-aaaaaaaaaaaaaaaaa"   # 10.0.144.0/20
   ]

   eks_admin_role_arn  = "arn:aws:iam::563149051155:role/eks-admin-role"
   ```
> **替换**四个 `subnet-*` 为你实际的 Subnet ID（在 Subnets 页面可见）。

### 初始化并验证

```bash
cd infra/aws
terraform init
terraform init -reconfigure
terraform plan
```

预期输出：**0 to add, 0 to change, 0 to destroy**（因为暂时只声明 provider & backend）。