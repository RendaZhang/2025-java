# Java 云原生 BootCamp Day 2 操作手册

**最后更新：** 2025 年 6 月 28 日

本操作手册旨在指导 Java 云原生 BootCamp 每日对 AWS EKS 集群环境进行从 **创建** 到 **销毁** 的全流程练习。通过分阶段的明确步骤，您将使用 **Terraform**、**AWS CLI**、**eksctl** 等工具，以 **CLI 操作为主** 完成 VPC 网络部署、ALB 创建、IAM 角色配置、EKS 集群搭建、Cluster Autoscaler 安装以及 Terraform 资源导入等任务。在每个阶段，我们提供操作背景（中英混合说明）、精确的 CLI 命令步骤（必要时指明 AWS 控制台操作）、验证方法及预期结果，以及常见错误的故障排除提示，最后以 ✅ **验收清单** 帮助您确认阶段目标是否顺利达成。请在开始之前确保已满足前置条件，包括正确配置 AWS 凭证与 Terraform 远端状态等。

> **提示：** 以下演练假定 AWS 区域为 **`us-east-1`**，AWS CLI Profile 为 **`phase2-sso`**，Terraform 远端状态保存在 S3 Bucket **`phase2-tf-state-us-east-1`** 和 DynamoDB 表 **`tf-state-lock`**。EKS 集群名称默认为 **`dev`**，Route 53 托管区域（Hosted Zone）假定为 **`lab.rendazhang.com`**。如有不同环境配置，请酌情替换文中的参数值。

## 阶段一：VPC 网络配置

#### 操作目的与背景

在开始部署 EKS 集群之前，我们需要先搭建基本的网络环境。此阶段将使用 **Terraform** 创建一个新的 **VPC（Virtual Private Cloud）** 及其子网、路由表和 **NAT 网关**，为 EKS 集群提供隔离的网络空间和互联网出口。VPC 会划分若干 **公有子网**（承载 ALB 等公网资源）和 **私有子网**（承载 EKS 工作节点等需隔离的资源），并配置 NAT Gateway 供私有子网内的实例访问外部网络。通过标准化的网络拓扑，确保后续各阶段组件有良好的基础连接。*（术语提示：VPC、Subnet、Route Table、NAT Gateway 等保持英文，以方便对应 AWS 控制台界面）*

#### 操作步骤

1. **初始化 Terraform 后端**：打开终端，切换到项目 `infra/aws` 目录，并执行 Terraform 初始化命令。这会配置远程状态存储后端（使用您预先创建的 S3 Bucket 和 DynamoDB 表）：

   ```bash
   cd infra/aws
   terraform init -reconfigure
   ```

   如果出现 `NoSuchBucket` 或 `ResourceNotFoundException` 错误，说明 Terraform 后端的 S3 Bucket 或 DynamoDB 锁表不存在。请先根据配置创建 Bucket 和表，然后重试初始化（或在 `backend.tf` 文件中更新为正确名称）。

1. **检查 Terraform 计划**：可选步骤，执行 Terraform Plan 查看将要创建的资源列表，确保配置符合预期：

   ```bash
   terraform plan -var="region=us-east-1"
   ```

   Terraform 会列出即将创建的 VPC、子网、Internet Gateway、Route Table 等资源清单及数量。核对输出中 VPC CIDR、子网数量和 NAT Gateway 数量是否符合设计。如果计划有误，可调整 `terraform.tfvars` 配置后重新运行计划。*（注意：默认 VPC CIDR 如未显式设置，一般为 AWS 随机生成或模块默认值；可根据需要在 vars 文件中指定。）*

1. **应用 Terraform 配置**：确认计划无误后，执行 Terraform Apply 来创建网络基础设施。此命令将自动创建 VPC 及相关资源：

   ```bash
   terraform apply -auto-approve -var="region=us-east-1"
   ```

   > **说明：** 本项目 Terraform 脚本采用模块化设计，会同时创建 VPC、子网、路由、NAT Gateway 等资源。其中 NAT Gateway 由变量 `create_nat` 控制，默认为 `true`。如需暂时跳过 NAT 部署，可将该变量设为 false 再执行。但在标准演练中保持默认开启，以确保私有子网拥有互联网访问能力。

1. **输出查看**：Terraform 完成后，可通过 Terraform 输出或 AWS CLI 验证资源是否成功创建：

   - 执行 `terraform state list` 查看状态文件中应包含 `aws_vpc`、`aws_subnet`、`aws_nat_gateway` 等资源条目。

   - 使用 AWS CLI 验证 VPC 和子网：

     ```bash
     aws ec2 describe-vpcs --filters "Name=tag:Name,Values=*" --region us-east-1 --profile phase2-sso
     aws ec2 describe-subnets --filters "Name=vpc-id,Values=<VPC-ID>" --region us-east-1 --profile phase2-sso
     aws ec2 describe-nat-gateways --filter "Name=vpc-id,Values=<VPC-ID>" --region us-east-1 --profile phase2-sso
     ```

     将 `<VPC-ID>` 替换为实际创建的 VPC ID（Terraform 输出或 state 文件中可查）。预期看到1个新 VPC，若干子网（公有/私有各2个），以及状态为 **available** 的 NAT Gateway。

#### 验证命令与预期输出

执行上述 CLI 命令后，预期输出如下：

- `describe-vpcs` 返回包含新建 VPC 的列表，其中 `"State": "available"`，CIDR 与配置相符。
- `describe-subnets` 列出属于该 VPC 的子网，数量与 Terraform 配置相符（例如公有子网和私有子网各 2 个）。每个子网的 `"State": "available"` 且有正确的可用区 (`us-east-1a/1b`)。
- `describe-nat-gateways` 返回 NAT 网关列表，包含一个 **状态为 Available** 且 **关联了弹性 IP (EIP)** 的 NAT Gateway。

示例输出（摘录）：

```json
{
  "NatGateways": [
    {
      "NatGatewayId": "nat-0abcd123456ef7890",
      "State": "available",
      "VpcId": "vpc-0123abc45d678efgh",
      "SubnetId": "subnet-0ab1c2d3ef4567890",
      "NatGatewayAddresses": [
        {
          "PublicIp": "3.91.XX.XX",
          "AllocationId": "eipalloc-0abc123de456fgh78"
        }
      ]
    }
  ]
}
```

以上输出表示 NAT Gateway 已分配弹性公网 IP，并成功创建于相应 VPC 的公有子网内。

#### 常见错误提示与解决办法（Troubleshooting）

- **未登录 AWS 或凭证无效：** 如果 Terraform 执行报错例如 *`ExpiredToken`* 或 *`Unable to locate credentials`*，请先通过 AWS SSO 登录您的账户，然后重试命令。命令示例：

  ```bash
  aws sso login --profile phase2-sso
  ```

  确认登录成功后再运行 Terraform 操作。

- **S3 Bucket/锁表不存在：** 如果 `terraform init` 报错找不到 Bucket/DynamoDB 表，需按照配置文件中的名称预先创建**远端状态存储**资源。您可以通过 AWS 管理控制台或 AWS CLI 新建所需的 S3 存储桶和 DynamoDB 表，并确保名称完全匹配。

- **VPC 或子网配额不足：** 如果 AWS 账户下 VPC 数量已达上限，Terraform 可能创建失败。此时可适当删除不需要的 VPC，或在 AWS 控制台申请提升配额。

- **NAT Gateway 无法创建：** NAT 网关需要弹性 IP，如弹性 IP 配额不足（默认每区 5 个），也会导致错误。可释放闲置 EIP 或提高配额后重试。

- **Terraform 提示资源冲突：** 若日志显示 VPC ID 或子网 CIDR 冲突，可能是配置不当导致与现有网络重叠。请调整 Terraform VPC CIDR（如 `10.0.0.0/16` 等不与现有网段冲突）后重新部署。

#### ✅ 阶段一验收清单

- [ ] VPC 已成功创建，状态为 *available*，CIDR 设置正确（无冲突）。
- [ ] 每个可用区均创建了**公有子网**和**私有子网**各至少1个，并正确关联到新 VPC。
- [ ] **Internet Gateway** 已关联到该 VPC，公有子网的路由表包含一条 0.0.0.0/0 指向 Internet Gateway 的路由。
- [ ] **NAT Gateway** 已创建且为 *available* 状态，私有子网的路由表包含一条 0.0.0.0/0 指向 NAT Gateway 的路由。
- [ ] Terraform 状态文件 (`terraform.state`) 中存在 VPC、Subnet、NAT 等资源记录，且未出现任何错误。

______________________________________________________________________

## 阶段二：ALB 创建与域名配置

#### 操作目的与背景

在完成基础网络后，本阶段将部署 **应用型负载均衡器（ALB, Application Load Balancer）** 及其配套资源，为集群中的服务提供对外访问入口。Terraform 将根据需要创建 ALB 以及安全组，并在 **Route 53** 中创建相应的 DNS 记录，将友好的域名指向 ALB。这一阶段确保集群对外服务的流量入口就绪。由于 ALB 是可选的高成本资源，我们可以通过 Terraform 开关灵活启停 ALB 以节省费用（稍后Stop操作中会涉及）。在每日演练中，通常在启动集群前开启 ALB，在停止集群时删除 ALB。

#### 操作步骤

1. **启用 ALB 资源**：在 `infra/aws/terraform.tfvars` 或命令行中确保变量 `create_alb` 设置为 `true`。默认配置已经开启 ALB 部署。如果上一阶段未包含 ALB，这里再次执行 Terraform Apply 将补充部署 ALB：

   ```bash
   terraform apply -auto-approve -var="region=us-east-1" -var="create_alb=true"
   ```

   此操作将创建一个 ALB（包含默认监听器）、关联的安全组，以及 DNS A记录（alias）。安全组规则默认开放 HTTP/HTTPS 所需端口；Route 53 DNS 记录会将 **lab.rendazhang.com** 根域名解析到新建 ALB 的域名上。*若您未预先创建 Route 53 Hosted Zone，Terraform 在这一步会报错，需要在 AWS 控制台手动创建名为 `lab.rendazhang.com` 的托管区或将域名变量调整为您自己的已配置域名。如不需要DNS，可暂时将相关 Terraform 资源注释掉。*

1. **查询 ALB 信息**：使用 AWS CLI 或控制台查看 ALB 是否创建成功：

   ```bash
   aws elbv2 describe-load-balancers --names <ALB-NAME> --region us-east-1 --profile phase2-sso
   ```

   其中 `<ALB-NAME>` 可以通过 Terraform 输出或 AWS 控制台获得（一般Terraform模块会以 "lab-alb" 命名ALB）。以上命令应返回 ALB 的详细信息，包括 **DNSName**（ALB的访问域名）和 **State: active** 等字段。记录下 ALB 的 **DNSName** 以备后续测试使用。

1. **验证 DNS 解析**：如果配置了 Route 53 域名，使用 **nslookup** 或 **dig** 查询 `lab.rendazhang.com`：

   ```bash
   nslookup lab.rendazhang.com
   ```

   预期解析结果的 CNAME 指向刚创建的 ALB 域名，例如：`dualstack.lab-alb-123456789.us-east-1.elb.amazonaws.com`。在浏览器中访问 `http://lab.rendazhang.com` 当前可能显示 404 或默认响应，因为尚未在 ALB 上部署后端服务，但能证明域名解析和 ALB Listener 正常工作。

#### 验证命令与预期输出

- **CLI 验证 ALB：** 执行 `aws elbv2 describe-load-balancers` 返回 JSON 中，应看到新建 ALB 列表项，其 `"State": "active"` 且 `"DNSName"` 字段有一个 AWS 提供的 \*.elb.amazonaws.com 域名。示例：

  ```json
  {
    "LoadBalancers": [{
      "LoadBalancerName": "lab-alb",
      "DNSName": "lab-alb-123456789.us-east-1.elb.amazonaws.com",
      "State": { "Code": "active" },
      "Type": "application",
      "Scheme": "internet-facing",
      ...
    }]
  }
  ```

- **DNS 验证：** 执行 `nslookup`（或 `dig`）查询自定义域名，输出应包含类似以下记录：

  ```
  Name:    lab.rendazhang.com
  Address: 3.XX.YY.ZZ
  Name:    lab.rendazhang.com
  Address: 34.ZZ.YY.XX
  ```

  或显示 `lab.rendazhang.com	canonical name = lab-alb-123456789.us-east-1.elb.amazonaws.com.` 表明 DNS CNAME 已指向 ALB。在浏览器访问该域名会返回 ALB 默认响应（如果未挂载后端，则为404），表明 ALB 对外可达。

#### 常见错误提示与解决办法（Troubleshooting）

- **Hosted Zone 未找到：** Terraform 创建 Route 53 记录时如果报错 *`NoSuchHostedZone`*，请确认 **lab.rendazhang.com** Hosted Zone 已在 Route 53 中创建，或修改 Terraform 中 `hosted_zone_id` 参数使用您的实际 Hosted Zone。您可以通过 AWS 控制台的 Route 53 服务新建该域的托管区域，然后重跑 Terraform。
- **域名未解析：** 如果 nslookup 无法解析域名，可能是本地 DNS 缓存或配置问题，或域名未正确托管。可尝试等待几分钟以允许 DNS 记录生效，或用 `dig @8.8.8.8 lab.rendazhang.com` 强制使用公共 DNS 查询。
- **ALB 限额问题：** AWS 帐户默认 ALB 有数量上限，如果创建 ALB 时返回配额超限错误，可删除一些不必要的 ELB/ALB 资源，或通过提交 AWS Support 提升 *Application Load Balancer Limit* 限额后重试。
- **ALB 状态非 active：** 若 `describe-load-balancers` 返回 ALB 状态为 *provisioning* 持续不变，可能表示正在分配弹性 IP 或未找到可用子网。请确保您的 VPC 至少有两个公有子网，每个子网都有 Internet Gateway 路由，且 Terraform 脚本中的子网 ID 正确无误。
- **安全组未开放端口：** 如果后续服务无法通过 ALB 访问，检查 ALB 安全组规则是否包含允许的入站端口（默认为 80/443）。Terraform 模块已经预配置了 HTTP/HTTPS 规则，如有自定义端口需求需要手动添加安全组规则。

#### ✅ 阶段二验收清单

- [ ] 已创建 ALB（Application Load Balancer），在 AWS 控制台或 CLI 查询中状态为 *active*，类型为 application，Scheme 为 internet-facing。
- [ ] ALB 分配了**安全组**，安全组入站规则包含 HTTP/HTTPS (80/443) 等必要端口，出站规则开放至 Internet。
- [ ] **Route 53 DNS** 记录已成功添加：`lab.rendazhang.com` CNAME 指向 ALB 域名（或 A记录别名指向 ALB，视配置而定）。
- [ ] 本地通过域名或 ALB 公共 DNS 测试连接，ALB 能返回默认响应（404/503 等，因暂无后端，但证明连接已到达 ALB）。
- [ ] Terraform 状态中存在 ALB 相关资源（如 `aws_lb`、`aws_route53_record` 等），后续可通过 Terraform 管理这些资源的生命周期。

______________________________________________________________________

## 阶段三：IAM 权限配置

#### 操作目的与背景

在创建 EKS 集群之前，需要准备必要的 **IAM 角色与权限**。EKS 控制平面本身需要一个具有特定权限的服务角色来创建和管理集群相关的 AWS 资源（如弹性网卡、安全组等）；此外，为实现更精细的权限控制，我们计划使用 **IRSA (IAM Roles for Service Accounts)** 为 Kubernetes 内的组件（如 Cluster Autoscaler）赋权。因此本阶段的目标是：

- 创建并配置 EKS 集群的服务角色（通常命名为 **eks-admin-role**），赋予 AWS 托管策略 **AmazonEKSClusterPolicy** 和 **AmazonEKSVPCResourceController**, 以满足控制平面对底层资源操作的权限要求。
- 准备 Cluster Autoscaler 所需的 IAM 策略和角色。Cluster Autoscaler 需要调用 EC2 Auto Scaling API，我们将创建一条自定义策略（示例命名 **EKSClusterAutoscalerPolicy**）授予所需权限，并关联到一个 IAM 角色（例如 **eks-cluster-autoscaler**）供 IRSA 使用。

这些 IAM 配置保证 EKS 集群和 Kubernetes 内组件都能遵循最小权限原则安全运行。

#### 操作步骤

1. **创建 EKS 集群服务角色（eks-admin-role）：** 打开 AWS 管理控制台，导航到 **IAM 服务 -> 角色 (Roles)**，点击“创建角色 (Create role)”：

   - **受信实体类型：** 选择 “AWS 服务”，然后选择 **EKS**，在用途中选择 **EKS - Cluster**（EKS 集群）作为信任实体。这将自动添加 EKS 创建集群所需的信任策略。
   - **附加权限策略：** 在策略列表中搜索并勾选 **AmazonEKSClusterPolicy** 和 **AmazonEKSVPCResourceController** 两项托管策略。这两项策略分别允许 EKS 管理集群和管理 VPC 资源的必要操作。
   - **设置角色名称：** 将角色命名为 **eks-admin-role**（或根据约定命名）。完成创建。

   创建完成后，进入该角色详情页，复制其 **ARN**（类似 `arn:aws:iam::<AccountID>:role/eks-admin-role`）。稍后将在 eksctl 集群配置文件和 Terraform 中引用此 ARN。

1. **为 IRSA 准备 IAM 策略（Cluster Autoscaler Policy）：** 在 IAM 控制台，导航到 **策略 (Policies)** 并点击“创建策略”。选择 **JSON** 选项卡，将下面的策略内容粘贴进去（该策略源自官方推荐的 Cluster Autoscaler 权限）：

   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Effect": "Allow",
         "Action": [
           "autoscaling:SetDesiredCapacity",
           "autoscaling:TerminateInstanceInAutoScalingGroup",
           "autoscaling:UpdateAutoScalingGroup",
           "autoscaling:DescribeAutoScalingGroups",
           "autoscaling:DescribeAutoScalingInstances",
           "autoscaling:DescribeLaunchConfigurations",
           "autoscaling:DescribeTags",
           "ec2:DescribeInstanceTypes",
           "ec2:DescribeLaunchTemplateVersions"
         ],
         "Resource": "*"
       }
     ]
   }
   ```

   以上策略授予 Cluster Autoscaler 调整 ASG 和查询资源的权限。点击“下一步”，为策略命名为 **EKSClusterAutoscalerPolicy**，并完成创建。

1. **创建 Cluster Autoscaler IRSA 角色（eks-cluster-autoscaler）：** 返回 **IAM -> 角色**，点击“创建角色”：

   - **受信实体类型：** 选择 **Web Identity**（Web 身份提供者）。在下拉中选择刚创建的 EKS 集群的 **OIDC 提供者**，主体为 `sts.amazonaws.com`。在具体身份提供者选项中，选择您的集群 OIDC URL（ eksctl 创建集群时开启了 OIDC 支持）。
   - **添加信任条件：** 当选择 OIDC 提供者后，需要指定可扮演此角色的 Kubernetes 服务账号。设置 **条件 (Condition)** 中的 `sub` 值为：`system:serviceaccount:kube-system:cluster-autoscaler`。这表示仅 `kube-system` 命名空间中名为 `cluster-autoscaler` 的服务账号可以通过IRSA来获取此角色。
   - **附加权限策略：** 选中上一步创建的 **EKSClusterAutoscalerPolicy**（或如果您希望，也可直接附加 Amazon 管理的 AutoScalingFullAccess，但自定义策略权限更精细）。
   - **角色命名：** 命名为 **eks-cluster-autoscaler**，完成创建。

   创建完成后，记录下该角色的 ARN。如未开启 OIDC 提供者或配置错误，控制台将无法列出 EKS 集群 OIDC，需要您先通过 `eksctl utils associate-iam-oidc-provider` 或 Terraform 创建 OIDC 提供商。通常使用 eksctl 且 YAML 中 `iam.withOIDC: true` 已自动完成此步骤。

1. **配置 Terraform 变量：** 打开 `infra/aws/terraform.tfvars` 文件，确认其中 `eks_admin_role_arn` 已设置为刚才创建的 eks-admin-role 的 ARN。默认该文件已填写一个示例 ARN，请确保替换为您账户中角色的实际 ARN。如果之前未创建 eks-admin-role 或 ARN 有误，这里需要更新后再继续。

   > **注意：** Terraform 脚本已包含创建 IRSA 角色的模块，会使用我们创建的 OIDC Provider 和固定的服务账号名称来定义 IRSA 角色和策略附加。由于脚本中 **硬编码** 了 Autoscaler 策略 ARN（默认指向账户 563149051155 的 EKSClusterAutoscalerPolicy）, 请务必修改 Terraform 脚本或变量以使用您自己账户中的策略 ARN，否则后续导入/应用过程可能失败。

   > **💡 修复建议：** 最佳实践是在 Terraform 脚本中将策略 ARN 提取为可配置变量，而不是写死具体 ARN。如果您拥有该代码库的维护权限，可将 IRSA 模块的 `aws_iam_role_policy_attachment` 中的 `policy_arn` 改为一个变量（例如 `var.autoscaler_policy_arn`），并通过 tfvars 提供，以提高代码的可移植性。

#### 验证命令与预期输出

- 登录 AWS 控制台，检查 **IAM -> 角色** 列表，应出现 **eks-admin-role** 和 **eks-cluster-autoscaler** 两个新角色。点进 eks-admin-role 的 **权限** 标签页，确认已附加 **AmazonEKSClusterPolicy** 和 **AmazonEKSVPCResourceController** 两项策略。

- 查看 eks-cluster-autoscaler 角色的 **信任关系** 配置，应包含 Web Identity 信任，Issuer 为您的 EKS 集群 OIDC 提供者，Condition 包含 `sub: system:serviceaccount:kube-system:cluster-autoscaler`。

- eks-cluster-autoscaler 角色的 **权限** 标签页应附加了 **EKSClusterAutoscalerPolicy** 策略，点进该策略可核对之前定义的 JSON 内容是否正确生效。

- （可选 CLI 验证）使用 AWS CLI 获取上述角色信息：

  ```bash
  aws iam list-attached-role-policies --role-name eks-admin-role --profile phase2-sso
  aws iam get-role --role-name eks-cluster-autoscaler --profile phase2-sso
  ```

  前者应列出包含 `AmazonEKSClusterPolicy` 等策略的 ARN；后者返回角色信任关系 JSON，其中 `AssumeRolePolicyDocument` 字段包含我们设置的 OIDC 条件。

#### 常见错误提示与解决办法（Troubleshooting）

- **角色创建失败或无权限选项：** 确认您在创建 eks-admin-role 时选择的是 **EKS - Cluster** 用途。如果未正确选择，可能导致缺少默认信任关系。您可以在角色创建后手动编辑信任关系策略为：

  ```json
  {
    "Version": "2012-10-17",
    "Statement": [{
      "Effect": "Allow",
      "Principal": { "Service": "eks.amazonaws.com" },
      "Action": "sts:AssumeRole"
    }]
  }
  ```

  并重新附加所需策略。或者删除角色重新按步骤创建。

- **OIDC 提供商未配置：** 如果在创建 IRSA 角色时，看不到预期的 OIDC Provider，可能是 eksctl 创建集群步骤漏掉了 `withOIDC: true`。解决方法：在 EKS 集群创建完成后，执行命令 `eksctl utils associate-iam-oidc-provider --cluster dev --approve --profile phase2-sso` 将 OIDC 提供商关联到集群。然后重新创建 IRSA 角色并附加策略。

- **策略 ARN 配置错误：** 假如 Terraform 后续 import 脚本在附加策略时报错 *NoSuchEntity*，可能是 IRSA 模块硬编码的策略 ARN 未指向正确的策略。请确保您在 Terraform 脚本中更新了策略 ARN（或在 tf-import 时修改脚本的 `POLICY_ARN` 变量为您的策略 ARN）。

- **权限不足：** 后续如果发现 EKS 集群创建失败，错误指向 IAM 角色问题，检查 eks-admin-role 是否确实具备了上述两个策略，特别是 AmazonEKSVPCResourceController 对应 VPC 子网和安全组的管理权限。必要时附加 **AmazonEKSServicePolicy**（EKS 服务策略）以满足更多集群操作需要。

- **Profile 凭证问题：** 如果使用 CLI 创建/查询角色遇到权限错误，确认已使用正确的 `phase2-sso` Profile 登录且该账户有足够权限创建和查看 IAM 角色。

#### ✅ 阶段三验收清单

- [ ] **eks-admin-role** 角色已创建，并附加 **AmazonEKSClusterPolicy** 和 **AmazonEKSVPCResourceController** 策略。信任实体包含 `eks.amazonaws.com` 服务。
- [ ] **EKSClusterAutoscalerPolicy** 自定义策略已创建，策略内容包含 Cluster Autoscaler 所需的 Auto Scaling 和 EC2 权限（SetDesiredCapacity、TerminateInstance 等）。
- [ ] **eks-cluster-autoscaler** 角色已创建，且信任关系指定了集群的 OIDC 提供者以及 `system:serviceaccount:kube-system:cluster-autoscaler` 条件限定。
- [ ] eks-cluster-autoscaler 角色已附加 **EKSClusterAutoscalerPolicy** 策略。后续 Kubernetes 集群中的 `cluster-autoscaler` 服务账号将可通过 IRSA 获得该角色权限。
- [ ] Terraform 配置文件中的 `eks_admin_role_arn` 已更新为 eks-admin-role 的实际 ARN，IRSA 模块中 Cluster Autoscaler 策略 ARN 已调整为您的策略 ARN（或已记录修改计划）。

______________________________________________________________________

## 阶段四：EKS 集群部署

#### 操作目的与背景

完成网络和 IAM 准备后，现在进入核心的 **Amazon EKS 集群** 部署阶段。本阶段将使用 **eksctl** 根据预定义的 YAML 配置文件创建 Kubernetes 控制平面和节点组。通过 eksctl，集群能够快速地在既有 VPC 中搭建，节点组可以启用 Spot 实例以降低成本，并自动配置必要的节点 IAM 角色、安全组等。我们选择 eksctl 而非 Terraform 创建集群，主要是利用其简洁的声明式配置和一键式部署能力。同时，在集群创建完成后，我们将通过导入（下一阶段）将集群纳入 Terraform 管理，以保持基础设施一致性。本阶段结束后，应当有一个状态为 **Active** 的 EKS 集群和运行中的工作节点。

#### 操作步骤

1. **查看 eksctl 配置文件**：打开项目中的 `infra/eksctl/eksctl-cluster.yaml` 文件，了解集群配置内容。关键配置包括：

   - 集群名称 **dev**，区域 **us-east-1**。
   - VPC 网络：引用了现有 VPC ID 和子网 ID（来自 Terraform 创建的 VPC）。请确保这些 ID 与您第一阶段创建的 VPC/子网一致。如有不同，需修改 YAML 使之匹配正确的资源。
   - IAM 集成：`withOIDC: true` 开启了 OIDC 提供商用于 IRSA；`serviceRoleARN` 设置为我们在上一阶段创建的 eks-admin-role 的 ARN，确保控制平面使用预授权角色。
   - 节点组配置：定义了一个名为 **ng-mixed** 的托管节点组，最小0、期望3、最大6节点，使用 Spot 实例（类型包括 t3.small 和 t3.medium）。Spot 实例不保证随时有容量，启用后可大幅节省成本，且节点组支持自动扩缩容。
   - 节点组使用**私有子网**部署（privateNetworking: true），意味着工作节点不暴露公有 IP。所有流量经由 NAT 出网。
   - 节点标签和污点：此例未设置污点，标签标记 role=worker 等，用于标识节点用途。
   - 升级策略等其他细节。通常无需改动上述默认配置，但请核实 eks-admin-role ARN、Subnet IDs 是否准确，否则创建会失败。

1. **执行集群创建**：确保 AWS SSO 已登录，然后运行 eksctl 创建集群命令：

   ```bash
   eksctl create cluster -f infra/eksctl/eksctl-cluster.yaml --profile phase2-sso
   ```

   eksctl 将根据 YAML 配置开始创建 EKS 控制平面和节点组。这一步耗时约 10~15 分钟。日志中将显示正在创建的资源，如 VPC 子网配置、安全组、EC2 实例等。耐心等待直到 eksctl 输出 **EKS cluster "dev" in "us-east-1" is ready** 或类似成功消息。

   > **注意：** 如果您之前已存在名为 dev 的集群，请先删除或更换名称。否则 eksctl 会警告集群已存在而终止操作。删除集群可参考阶段七的做法使用 `eksctl delete cluster --name dev`。

1. **更新 kubeconfig：** eksctl 创建集群成功后，通常会自动将新集群的认证信息加入本地 kubeconfig 文件（通常位于 `~/.kube/config`）。您可以通过以下命令确保当前上下文指向新集群：

   ```bash
   aws eks update-kubeconfig --name dev --region us-east-1 --profile phase2-sso
   ```

   该命令将使用 AWS CLI 将 EKS 集群的访问凭证添加到 kubeconfig。如果命令执行成功，表示 kubeconfig 更新完毕。

1. **验证集群运行状态**：使用 kubectl 验证 EKS 集群的基本运行情况：

   ```bash
   kubectl get nodes
   kubectl get pods -A
   ```

   预期 `get nodes` 列出新集群的节点（应有 3 个 Ready 状态的节点，对于 Spot 类型如果部分暂不可用节点数可能少于期望值）。`get pods -A` 会列出 `kube-system` 等命名空间中的系统 Pod，其中核心 DNS、CNI 插件等 Pod 应为 Running 状态。如果所有节点和系统 Pod 正常，则集群已成功启动。

#### 验证命令与预期输出

- **eksctl 输出确认：** 在 eksctl 执行完毕时，日志最后通常有 **"EKS cluster <dev> in <us-east-1> is ready"**，并列出节点组的节点信息如 EC2 实例ID。您也可以通过 AWS 控制台的 EKS页面查看 **dev** 集群状态为 **Active**。

- **AWS 控制台检查：** 打开 Amazon EKS 控制台，定位到 us-east-1 区域，看到名为 dev 的集群，点入可以查看其 **节点组** 列表有一个 ng-mixed，期望容量3，当前运行节点数可能为2-3（Spot容量情况）。在 **计算 -> EC2 实例** 页面，可以找到属于该节点组的 EC2 实例（Name通常含有 dev 和 ng-mixed 字样），状态为 running。

- **kubectl 输出：** 执行 `kubectl get nodes`，示例输出：

  ```
  NAME                                           STATUS   ROLES    AGE   VERSION
  ip-192-168-xx-xx.us-east-1.compute.internal    Ready    <none>   5m    v1.30...
  ip-192-168-yy-yy.us-east-1.compute.internal    Ready    <none>   5m    v1.30...
  ip-192-168-zz-zz.us-east-1.compute.internal    Ready    <none>   5m    v1.30...
  ```

  以上表示 3 个节点均已就绪 (STATUS = Ready)。
  `kubectl get pods -A` 列出各命名空间Pod，其中 `kube-system` 下的 **coredns**、**aws-node**（VPC CNI插件）、**kube-proxy** 等 Pod 均应为 **Running** 或 **Completed** 状态，没有持续 CrashLoop 的现象。

#### 常见错误提示与解决办法（Troubleshooting）

- **eksctl 命令未找到或版本过低：** 如果终端提示 *`eksctl: command not found`*，请先确认已安装 eksctl 工具并配置在 PATH 中。版本过低可能导致不支持最新 EKS 特性，建议使用文档要求的版本（≥0.180）。升级 eksctl 可通过 `brew upgrade eksctl` (Mac) 或从 GitHub releases 手动下载。
- **IAM 角色错误导致创建失败：** 如果 eksctl 日志显示错误例如 *`cannot assume role arn:aws:iam::...:role/eks-admin-role`* 或 *`AccessDenied`*，请检查 YAML 中 `serviceRoleARN` 是否正确填入了 eks-admin-role 的 ARN且该角色已附加必要策略（阶段三检查）。修正 ARN 或权限后，可使用 `eksctl delete cluster --name dev` 清理失败的残余，然后重新执行创建。
- **Subnet ID 无效或不存在：** 若 eksctl 报 *`InvalidSubnetID.NotFound`*，说明 YAML 中引用的子网 ID 有误，可能未更新为 Terraform 实际创建的值。请登录 VPC控制台获取正确的子网 ID，更新 YAML 后重试。
- **节点组创建超时或节点未达期望数：** 使用 Spot 实例时可能遇到容量不足或请求超时。如果 eksctl 最终提示节点组仅启动了部分节点，可在 EKS 控制台查看节点组详情的事件。如果持续 Spot 不可用，解决办法：可以调整实例类型（如增加其他类似规格的 instanceTypes）或暂时关闭 Spot 模式（YAML中将 `spot: true` 改为 false 使用按需实例），然后删除集群重建。
- **kubectl 连接失败：** 若 `kubectl get nodes` 报错 *`could not connect to cluster`* 或超时，可能 kubeconfig 未正确设置。请重试执行 `aws eks update-kubeconfig ...` 命令，并确保 AWS CLI 使用正确 Profile 和 Region。另一个可能原因是本地网络防火墙阻拦，EKS 需访问 AWS API 端点，请确保本地网络可以访问 \*.eks.amazonaws.com 域名。
- **系统 Pod 异常：** 如果 `aws-node` 或 `coredns` Pod CrashLoop，常见原因是少装置或配置错误。例如 VPC CNI 插件需要特定 IAM 权限（通常节点 IAM Role eksctl自动创建会附加，问题不多见），CoreDNS CrashLoop多因无法连通 cluster DNS，此时检查节点子网的 DHCP Option是否配置了 AmazonProvidedDNS（Terraform 一般默认配置无需特殊处理）。
- **集群名字冲突：** 如果尝试创建的集群名称在当前账户区域已存在（可能前一天忘记删或者有同名集群），eksctl 会报错终止。可更换 metadata.name 或先删除同名集群：`eksctl delete cluster --name dev --region us-east-1`.

#### ✅ 阶段四验收清单

- [ ] **EKS 集群 "dev"** 已创建，AWS 控制台中集群状态为 Active，且 Kubernetes 版本符合 eksctl 配置（例如 v1.30）。
- [ ] **节点组 "ng-mixed"** 已成功部署，期望节点数与实际运行节点数相符（允许有少量差异，Spot 节点可能延迟）。EC2 控制台中能看到相应数量的工作节点 EC2 实例，实例类型匹配 YAML 中定义 (t3.small/medium)。
- [ ] **本地 kubeconfig 已更新**，`kubectl config current-context` 指向新的 dev 集群，且可以正常通过 kubectl 与集群交互。
- [ ] `kubectl get nodes` 显示所有节点 **Ready**，`kubectl get pods -A` 显示系统 Pods **Running**，无 CrashLoopBackOff 等异常。
- [ ] eksctl YAML 中配置的各项均生效：包括 Spot 配置、节点标签、OIDC 提供者启用等。OIDC 提供者可在 AWS IAM 控制台 **身份提供商** 页看到对应条目。

______________________________________________________________________

## 阶段五：Terraform 资源导入

#### 操作目的与背景

为实现基础设施状态的一致管理，我们在使用 eksctl 创建集群后，需要将其相关资源导入 Terraform 状态。这样 Terraform 才能“知晓”集群的存在并进行后续管理（例如 IRSA 角色绑定、资源清单），形成闭环的 **IaC（Infrastructure as Code）** 管理模型。本阶段通过运行预先编写的脚本，将 EKS 集群、节点组、OIDC 提供商以及 IRSA 相关的 IAM 角色导入 Terraform。导入后，您可以通过 Terraform state 查询和 plan 来验证这些资源已受 Terraform 管理，而不会在后续 Terraform 操作中被意外删除或重复创建。

#### 操作步骤

1. **运行导入脚本**：项目中提供了 `scripts/tf-import.sh` 脚本，可自动完成所需资源的导入。执行该脚本：

   ```bash
   bash scripts/tf-import.sh
   ```

   脚本将执行以下导入操作：

   - 导入 **EKS 集群** 本身：将 Terraform 中 `module.eks.aws_eks_cluster.this[0]` 关联到当前集群名称 (dev)。

   - 导入 **EKS 托管节点组**：将 Terraform 中 `module.eks.aws_eks_node_group.ng[0]` 关联到 “集群名:节点组名” (例如 `dev:ng-mixed`)。

   - 导入 **OIDC 提供商**：将 Terraform 中 `module.eks.aws_iam_openid_connect_provider.oidc[0]` 关联到集群的 OIDC ARN。该 ARN 由脚本通过 AWS CLI 自动获取。

   - 导入 **IRSA 角色及附加策略**：分别将 `module.irsa.aws_iam_role.eks_cluster_autoscaler` 关联 IAM 角色 **eks-cluster-autoscaler**，以及将其策略附加关系关联对应策略 ARN。

   > **注意：** 脚本默认假设集群名称为 "dev"，Profile 为 phase2-sso。如果您的集群或配置不同，请在运行前编辑脚本开头的变量。如脚本执行报错，可逐个导入命令手工执行并调整参数。导入操作是幂等的，如果某资源已导入会有相应提示。

1. **检查 Terraform 状态**：导入完成后，运行 Terraform 状态命令查看结果：

   ```bash
   terraform state list | grep aws_eks_cluster
   ```

   您应该看到类似 `module.eks.aws_eks_cluster.this[0]`、`module.eks.aws_eks_node_group.ng[0]` 等条目出现在状态列表中，表明集群和节点组现已在 Terraform 状态内管理。此外，`module.irsa.aws_iam_role.eks_cluster_autoscaler` 等 IRSA 相关资源也应存在。

1. **Terraform Plan 验证一致性**：为了确保导入后的配置与实际基础设施一致，建议运行一次 Terraform Plan：

   ```bash
   terraform plan -var="region=us-east-1"
   ```

   理想情况下，Plan 输出应显示 **0 to add, 0 to change, 0 to destroy**，表示所有状态均匹配（或者仅有预期的变更，比如标签更新等无害变更）。如果 Plan 列出了对 EKS 或 NodeGroup 资源的修改/销毁计划，则可能导入有误或 Terraform 模块定义与实际配置不完全匹配。需人工检查 Terraform 配置和实际 AWS 资源属性，调整代码使二者一致，然后通过 `terraform apply` 使状态同步。常见的细微不一致包括标签差异、Launch Template 版本号等，通常可在 Terraform 配置中更新相应参数解决。

#### 验证命令与预期输出

- **导入脚本输出：** 脚本运行过程中，每个 `terraform import` 执行成功后会提示 **Import successful!** 或没有错误信息。如果出现错误，会有相应错误输出（如 "No such resource" 等）。

- **Terraform 状态列表：** 运行 `terraform state list` 后应包含以下关键条目：

  ```
  module.eks.aws_eks_cluster.this[0]
  module.eks.aws_eks_node_group.ng[0]
  module.eks.aws_iam_openid_connect_provider.oidc[0]
  module.irsa.aws_iam_role.eks_cluster_autoscaler
  module.irsa.aws_iam_role_policy_attachment.cluster_autoscaler_attach
  ```

  这些对应 EKS 集群、节点组、OIDC提供商、IRSA角色及策略附加关系。确认 `eks_cluster_autoscaler` 角色和策略关联均已导入（对应我们在阶段三创建的 IAM 角色和策略）。

- **Terraform Plan 输出：** 理想输出为：

  ```
  No changes. Infrastructure is up-to-date.
  ```

  如果有变化提示，也应无 **destroy** 操作，且 **create/modify** 仅限于您知晓的必要调整（比如给资源补充标签等）。出现 destroy 要特别警惕，避免误删现有资源。

#### 常见错误提示与解决办法（Troubleshooting）

- **导入脚本失败：** 如果 `tf-import.sh` 脚本未成功运行，您可以逐步执行其中命令并查看问题。例如导入 NodeGroup 时常见错误：

  - *No nodegroup found*: 脚本通过 AWS CLI `list-nodegroups` 获取节点组名称。若您的集群名称或 Profile 不同会导致空结果。解决：直接指定节点组名称导入，例如：

    ```bash
    terraform import 'module.eks.aws_eks_node_group.ng[0]' dev:ng-mixed
    ```

    将 `dev` 和 `ng-mixed` 替换为您的实际集群名和节点组名。

  - *Resource already managed*: 如果之前已经导入过，Terraform 会拒绝重复导入相同实体。您可以使用 `terraform state show <resource>` 查看其当前状态，或用 `terraform state rm <resource>` 移除冲突的状态后重新导入（谨慎操作）。

- **Launch Template ID 变更未捕获：** 由于 eksctl 创建的节点组使用了 Launch Template，Terraform 配置中可能需要该 Launch Template ID。当前 Terraform 模块里 Launch Template ID 是硬编码值。如果集群重建导致 Launch Template ID 更新，Terraform plan 会试图调整回旧值（或认为不一致）。

  - **💡 修复建议：** 将 Launch Template 相关配置改为通过数据源或变量传入，而不是硬编码具体 ID。目前手动解决办法：更新 Terraform 配置中的 `launch_template.id` 为新的 Launch Template ID（可在 AWS EC2 控制台的 Launch Templates中找到dev节点组对应的模板）。

- **节点 IAM Role ARN 不匹配：** 类似地，托管节点组的节点角色 ARN 也在 Terraform 配置中固定为了首次创建时的值。如果每次重建集群该 ARN 发生变化（如 eksctl 未复用旧角色），Terraform 可能报告差异。解决：可以在 eksctl 创建集群时指定复用已有节点角色，或在 Terraform 中将 `node_role_arn` 参数改为变量，通过导入步骤获取实际的角色 ARN 后赋值。手册执行中，如遇不匹配，可手动编辑 Terraform 配置文件 `infra/aws/modules/eks/main.tf`，更新第37行的 ARN 为新值。这属于代码优化范畴，实际演练时注意保持此值同步最新。

- **OIDC 提供商导入问题：** 如果导入 OIDC 提供商时报 ARN 不正确，可登录 IAM控制台 -> 身份提供商，找到类似 `oidc.eks.<region>.amazonaws.com/id/XXXX` 的提供者 ARN，确认脚本组装的 ARN 是否一致。如不一致，可手动执行 import：

  ```bash
  terraform import module.eks.aws_iam_openid_connect_provider.oidc[0] arn:aws:iam::<AccountID>:oidc-provider/oidc.eks.us-east-1.amazonaws.com/id/XXXX
  ```

  将 AccountID 和 XXXX 替换为实际值。

- **Terraform Plan 非预期更改：** 如果 Plan 显示需要新建或销毁资源（尤其 EKS 类资源），务必三思后再执行。通常导入完成即应无差异。如出现，可检查 Terraform版本、AWS Provider版本兼容性或模块bugs。例如 Terraform 1.x 对 AWS EKS 有些新字段默认值，必要时在 Terraform 配置中显式添加这些字段以消除虚假差异，然后再次 plan 确认。

#### ✅ 阶段五验收清单

- [ ] 已执行 `tf-import.sh` 或等效的 Terraform Import 操作，EKS 集群、节点组、OIDC Provider、IRSA 角色及策略绑定等资源成功导入 Terraform 状态。
- [ ] `terraform state list` 可以列出 EKS 集群 (`aws_eks_cluster`)、节点组 (`aws_eks_node_group`)、OIDC 提供商 (`aws_iam_openid_connect_provider`) 以及 IRSA 相关 IAM 资源 (`aws_iam_role` 和 `aws_iam_role_policy_attachment`)。
- [ ] 运行 Terraform Plan 显示无需额外变更（或仅有少量可接受的修改），证明 Terraform 配置与实际基础设施状态一致。特别地，没有 **销毁 (destroy)** EKS 集群或节点组的计划。
- [ ] Terraform 状态文件已备份到远端（S3），确保即使本地环境丢失也可恢复状态。导入操作日志（如 tf-import.sh 输出）妥善保存，以供日后排查参考。
- [ ] 针对 Terraform 脚本中硬编码的 AWS 资源 ID/ARN（如 Launch Template、IAM策略等）已有记录或改进方案，避免下次重建时出现不匹配问题。已经考虑对 Makefile 或脚本进行改进（例如增加 `stop-hard` 命令）以完善日常重建流程。

______________________________________________________________________

## 阶段六：Cluster Autoscaler 安装

#### 操作目的与背景

现在 EKS 集群已经运行，我们将部署 **Kubernetes Cluster Autoscaler（集群自动伸缩器）** 来根据工作负载自动增减节点数量。通过 Autoscaler，可以在集群 Pod 调度失败（因节点不足）时自动添加节点，在闲置时自动移除节点，实现弹性伸缩以优化成本和性能。本阶段主要内容：

- 使用之前为 Cluster Autoscaler 准备的 IRSA 服务账户（`kube-system:cluster-autoscaler`）部署 Autoscaler 应用。Autoscaler Pod 将以该服务账户运行，并通过其关联的 IAM Role 来调用 AWS 的 Auto Scaling API，实现对 EKS 托管节点组（实际上是 Auto Scaling Group）的扩缩容操作。
- 配置 Autoscaler 使其识别集群和节点组。由于我们使用的是 EKS 托管节点组 (Managed Node Group)，Autoscaler 可以通过 **AutoDiscovery** 模式自动发现 ASG。我们需提供集群名称和一些参数给 Autoscaler，以确保它只管理特定节点组并遵守我们设定的缩容规则。

#### 操作步骤

1. **下载 Cluster Autoscaler 部署 YAML：** Kubernetes 官方仓库提供了 AWS 专用的 Cluster Autoscaler 部署示例。运行以下命令获取最新版本的 YAML 清单：

   ```bash
   curl -O https://raw.githubusercontent.com/kubernetes/autoscaler/master/cluster-autoscaler/cloudprovider/aws/examples/cluster-autoscaler-autodiscover.yaml
   ```

   这将下载名为 `cluster-autoscaler-autodiscover.yaml` 的文件，其中包含 Cluster Autoscaler 的 Deployment 定义等。用编辑器打开此文件，我们需要做几点修改：

   - 在 Deployment 的 spec.template.spec.containers.args 下，找到 `--cluster-name=<YOUR CLUSTER NAME>`，将其值替换为 **dev**（您的集群名称）。

   - 确认 Deployment 的 spec.template.spec.containers.image 使用的版本与您的集群K8s版本兼容。例如，对于 Kubernetes 1.30，可使用 autoscaler 镜像 tag `v1.30.n`（n为最新小版本）。官方 YAML 可能使用 `latest` 或较老版本，请酌情调整镜像标签以匹配 Kubernetes 版本。

   - 将 Deployment spec.template.spec 中添加以下环境变量参数，以设置缩容行为：

     ```yaml
     - name: AWS_REGION
       value: us-east-1
     - name: TZ
       value: Asia/Shanghai         # 可选，设置时区以便日志按当地时间
     ```

     同时，在 args 部分添加 `--balance-similar-node-groups` 和 `--skip-nodes-with-system-pods=false` 等参数，以启用更智能的均衡扩容和允许缩容至0节点。完整args示例：

     ```yaml
       args:
         - --cloud-provider=aws
         - --cluster-name=dev
         - --namespace=kube-system
         - --balance-similar-node-groups
         - --skip-nodes-with-system-pods=false
         - --logtostderr=true
         - --stderrthreshold=info
         - --v=4
     ```

   - **服务账户与 IRSA：** YAML 默认会创建一个 ServiceAccount `cluster-autoscaler` 在 `kube-system` 命名空间。由于我们已提前创建对应的 IAM Role（eks-cluster-autoscaler）信任该 SA，我们需要将此 ServiceAccount 与 IAM Role 绑定。方法是在 ServiceAccount 定义下添加注解：

     ```yaml
     apiVersion: v1
     kind: ServiceAccount
     metadata:
       name: cluster-autoscaler
       namespace: kube-system
       annotations:
         eks.amazonaws.com/role-arn: arn:aws:iam::<AccountID>:role/eks-cluster-autoscaler
     ```

     将 `<AccountID>` 替换为您 AWS 账户 ID。此注解使 Autoscaler Pod 启动时，通过 IRSA 获得 eks-cluster-autoscaler 角色权限。

   - 添加一个保证 Pod 不被驱逐的注解：在 Deployment 的 metadata.annotations 下添加：

     ```yaml
     cluster-autoscaler.kubernetes.io/safe-to-evict: "false"
     ```

     以防止 Autoscaler 自身因空闲被驱逐。

1. **应用 YAML 部署 Autoscaler：** 保存编辑后的 YAML 文件，然后执行：

   ```bash
   kubectl apply -f cluster-autoscaler-autodiscover.yaml
   ```

   Kubernetes 将创建 **cluster-autoscaler** Deployment（位于 kube-system），以及关联的 ServiceAccount 等资源。等待几秒后，检查 Pod 状态：

   ```bash
   kubectl -n kube-system get pods | grep cluster-autoscaler
   ```

   应出现 **cluster-autoscaler-**\* 开头的 Pod 名称。几分钟内其状态应变为 **Running**。初次启动时可能需要拉取镜像，耐心等待。如果镜像拉取缓慢，可以考虑切换国内源或通过提前下载镜像等方式。

1. **验证 Autoscaler 工作状态**：查看 Autoscaler 日志以确认是否正常运行：

   ```bash
   kubectl -n kube-system logs -f deployment/cluster-autoscaler
   ```

   在日志中搜索关键词 `Registered ASG` 或 `node group`，应该看到 Autoscaler 发现了我们的节点组（Auto Scaling Group）并开始监控。例如日志可能包含：

   ```
   I... Cluster Autoscaler 1.30.x starting
   I... Registered ASG eksctl-dev-nodegroup-ng-mixed-NodeGroup-ABCDEF123456
   ```

   这表示 Autoscaler 已检测到 dev 集群下名为 ng-mixed 的节点组。同时, 它会周期性打印当前集群资源使用与扩缩容判断信息。当您部署新的应用使 Pod 无法调度（Pending）时，日志应出现 scale-up 触发的记录；当节点长时间空闲且满足缩减条件时，会记录 scale-down 操作。您可以通过模拟负载（部署一个超额请求资源的 Deployment）来测试 Autoscaler 功能。

#### 验证命令与预期输出

- **Deployment 状态：** 执行 `kubectl -n kube-system get deployment cluster-autoscaler -o wide`，应显示 READY 列为 `1/1`，表示1个副本全部就绪。ServiceAccount 绑定正确的话，可通过 `kubectl -n kube-system describe pod <cluster-autoscaler-pod>` 查看 **Annotations** 部分，能看到我们设置的 `eks.amazonaws.com/role-arn: arn:aws:iam::<AccountID>:role/eks-cluster-autoscaler`。

- **Pod 日志：** 通过 `kubectl logs` 查看日志，正常情况下无错误堆栈或权限报错。有关键几行需注意：

  - **启动信息：** 显示 Autoscaler 版本和参数列表，确认参数如 `--cluster-name=dev` 生效。
  - **IAM 权限检查：** 如果 IRSA 成功，日志不会有权限相关报错；否则会出现对 Auto Scaling API 调用被拒绝的信息，需要检查 IRSA 配置。
  - **发现节点组：** 日志列出已发现的 ASG 名称 (通常为 eksctl创建的ASG，包含节点组名)。
  - **运行周期**： 默认每10秒Autoscaler评估一次。如集群状态稳定，日志多为 "No unschedulable pods" 或 "Scale down evaluation: node X marked as unneeded" 等信息。

- **功能测试 (可选)：** 部署一个超出当前节点容量的应用。例如创建一个 Deployment 请求 4 个 vCPU，而目前3个 t3.small节点不够容纳，则会有Pod Pending。几分钟内Autoscaler应检测到 unschedulable pods 并在日志中输出扩容决策，然后通过 EKS添加新节点（此过程可在 EKS控制台的节点组活动中看到scale-up记录）。待新节点就绪，Pending Pod变为Running，Autoscaler日志会记录成功扩容。相反地，删除该Deployment后，约10分钟后Autoscaler会评估缩容，若节点空闲满足条件会移除多余节点（节点会逐渐变为 NotReady -> 删除）。

#### 常见错误提示与解决办法（Troubleshooting）

- **Autoscaler Pod CrashLoopBackOff：** 执行 `kubectl describe pod` 查看详细信息。如果镜像拉取失败，检查网络或更换镜像仓库。如果错误是 **permission denied** 之类，可能 IRSA 未生效，Pod 无法调用 AWS API。请确保 ServiceAccount 名称、命名空间和 IAM角色信任中的条件完全匹配且已加注解绑定角色 ARN。也可检查 AWS CloudWatch Logs 中是否有 Autoscaler 权限错误记录，定位问题。
- **未发现 ASG：** 日志持续提示 *“Failed to discover ASG: ...”* 之类，可能是 cluster-autoscaler 参数不正确。确保使用了 `--cloud-provider=aws` 和 `--cluster-name=dev`，名称必须与实际 EKS集群名完全一致。也确认节点组确实处于**自动伸缩组**模式（Managed Node Group 本质上会有一个隐含的 ASG，由 eksctl 创建）。通过 AWS EC2 控制台的 Auto Scaling Groups，可以看到名称类似 *eksctl-dev-nodegroup-ng-mixed-NodeGroup-...* 的ASG。Autoscaler只有检测到它才会工作。
- **扩容不起作用：** 若有 Pending Pod 而 Autoscaler未反应，可能 Pod 设置了 `cluster-autoscaler.kubernetes.io/safe-to-evict: "false"` 或 Pod 有本地存储，默认策略不驱逐带本地存储Pod，导致不触发扩容。可以通过给 Deployment 添加 `tolerations` 允许调度失败，或者在 Autoscaler 参数中调整 `--expendable-pods-priority-cutoff` 等。基本演练中不深入此项。
- **缩容不起作用：** 如果节点长期空闲却未缩容，检查是否有 Deployment 的 replicasets始终在每个节点留有一个Pod（即 **Pod 反亲和** 或 DaemonSet等），默认策略不会缩减导致驱逐系统Pod或DaemonSet Pod的节点。需要允许此类缩容需设置 `--skip-nodes-with-system-pods=false`（我们已经设置）以及 `--skip-nodes-with-local-storage=false` 视情况而定。
- **时间不同步警告：** 日志可能有 *“Node not found”* 或类似警告，多由于启动初期信息未完全同步，可忽略。另如果本地时区与UTC不同，可通过TZ环境变量调整日志时区，或忽略时区差异对功能无影响。

#### ✅ 阶段六验收清单

- [ ] Cluster Autoscaler 部署已创建，Pod 在 **kube-system** 命名空间中处于 Running 状态，且 **不会被驱逐**（带有 `cluster-autoscaler.kubernetes.io/safe-to-evict: "false"` 注解）。
- [ ] Cluster Autoscaler 使用的 ServiceAccount 已正确关联 IRSA：ServiceAccount 注解包含 eks-cluster-autoscaler 角色 ARN，AWS 控制台中该角色最近的 **信任关系** 调用时间已更新（表示有通过IRSA成功调用）。
- [ ] 查看 Autoscaler 日志，确认其**检测到 EKS节点组**并定期输出扫描信息，没有出现权限不足等错误。能够看到 **Registered ASG** 等日志行，表明集群节点组已被接管监控。
- [ ] 触发扩容/缩容测试正常：人为制造Pod Pending后，Autoscaler可以及时增加节点；移除负载后，一段时间内自动减少空闲节点，EKS 控制台的 Node Group 活动记录与 Autoscaler 日志相符。
- [ ] Autoscaler 部署配置符合优化要求：包括使用了**AutoDiscovery**（通过 `--cluster-name` 实现，无需手动列ASG）、开启了 **balance-similar-node-groups** 保证不同类型实例均衡扩容，配置了 `--skip-nodes-with-system-pods=false` 以允许缩容至0节点，以及镜像版本匹配当前K8s版本等。

______________________________________________________________________

## 阶段七：环境关停与清理（可选）

> **本阶段属于延伸操作，指导如何每日演练结束后安全地关闭或销毁资源。** 根据成本和需求，可以选择暂时停止高成本资源，或彻底销毁以确保下次演练从零开始。

#### 操作目的与背景

在完成一天的练习后，为节省费用，需要对云上资源进行关停处理。本项目提供了两种关停策略：

- **停止（Stop）模式**：保留基础结构（VPC、子网、安全组等）和Terraform状态，仅关闭高成本资源如 NAT 网关、ALB，并删除 EKS 控制面和节点。这可以快速释放大部分资源占用，同时保留网络以供下次快速重建。
- **销毁（Destroy）模式**：彻底删除所有通过 Terraform 和 eksctl 创建的资源，包括 VPC 本身。此模式适用于需要完全重置环境或者不再使用时。**警告：销毁操作不可恢复，执行前请确认无其他依赖。**

#### 操作步骤

**A. 日常停止模式：**

1. **停止高成本资源 (NAT, ALB)：** 使用 Makefile 提供的快捷命令执行 Terraform 部分关停：

   ```bash
   make stop
   ```

   该命令实质运行了 `terraform apply -var="create_nat=false" -var="create_alb=false" -var="create_eks=false"`。它会将 NAT Gateway 和 ALB 相关资源删除或关闭，同时不再保留 EKS相关的弹性网卡和辅助资源（因为 `create_eks=false` 将对应 Terraform管理的资源计入销毁范围，但由于我们已导入 cluster, nodegroup，这里应谨慎）。

   **注意：**`make stop` 并不会删除 VPC、本地存储等基础架构，只是把可关停部分停掉。执行完成后，可在 AWS 控制台验证 NAT Gateway 是否删除（应变为 deleted 状态），ALB 是否消失。

1. **删除 EKS 集群控制面：** NAT和ALB关停后，使用 eksctl 删除 EKS 集群：

   ```bash
   make stop-cluster
   ```

   该命令等价执行 `eksctl delete cluster --name dev --region us-east-1`。eksctl 将会删除控制平面以及节点组（将触发所有 EC2 实例和相关Auto Scaling Group的回收）。由于我们通过 IRSA 创建的OIDC提供商和IRSA角色是独立资源，它们**不会**被 eksctl 删除，需要保留用于下次重建复用（如不想保留可手动删除OIDC提供商）。
   等待命令完成，EKS 控制台中 dev 集群应消失。此时 VPC 等仍在，但不产生产生费用的资源已关停。

1. **（可选）标记停止完成**：可以执行 `make clean` 清理本地缓存文件，如 `.last-asg-bound` 等。并记录下当日操作日志 `make logs` 供事后分析。到此，环境已安全停止，次日可通过 `make start` 和 `make start-cluster` 快速重启集群。

**B. 完全销毁模式：**

1. **一键销毁所有资源：** 若确定不再保留环境，可使用：

   ```bash
   make destroy-all
   ```

   该命令会自动调用 `stop-cluster` 删除 EKS 集群，然后执行 `terraform destroy` 销毁 Terraform创建的所有资源。包括VPC、子网、路由表、安全组、以及通过Terraform管理的IAM角色、OIDC提供商等都会被删除。请谨慎运行此命令，并确保Terraform状态中没有非本练习范围的资源，以免误删。

1. **清理残留和状态**：如果 destroy-all 执行成功，AWS 上应不再有演练创建的任何资源。您可以检查 S3 Bucket 中 Terraform状态文件是否仍存在，通常 destroy 不会自动删除状态文件，可根据策略选择保留或手动删除。DynamoDB 锁表记录也可清理。最后，运行 `aws sso logout` 注销会话以确保账号安全。

#### 验证命令与预期输出

- **停止模式验证：** 执行 `make stop` 后，Terraform 日志应显示 `-/+` 或 `-` 操作针对 NAT Gateway、ALB 等资源（销毁或修改为 false）。完成后，用 AWS CLI `describe-nat-gateways` 应不再返回 NAT Gateway，`describe-load-balancers` 也查询不到 lab-alb。执行 `make stop-cluster`，eksctl 会输出删除进度，包括 CloudFormation stack 删除完成信息，最终提示集群删除成功。EKS 控制台刷新应看不到 dev 集群。
- **销毁模式验证：** `make destroy-all` 完成后，Terraform 会输出每个资源销毁完成的提示，最终 `Destroy complete!`。再次通过 AWS CLI/控制台检查 VPC、子网、Security Group 均已不存在。IAM 控制台中 eks-cluster-autoscaler 角色、OIDC Provider 也应删除（如 Terraform 有管理这些）。S3 Bucket 中 Terraform state 可手动验证或下载备份，确保最终一致。

#### 常见错误提示与解决办法（Troubleshooting）

- **Terraform destroy 失败：** 如果在 destroy 阶段 Terraform 提示删除某些资源失败，可能是依赖顺序问题或残留依赖。例如 EKS 集群未删干净造成 VPC 删不掉，可重试执行 `terraform destroy` 再次尝试。或者通过 AWS 控制台检查是否有资源需要先手工删除（如留存的 ENI、EC2 等）。

- **`make stop` 引发意外删除：** 由于我们将 `create_eks` 设置为 false 以停用 EKS相关Terraform资源，但我们导入了 EKS集群/节点组，这里可能产生混乱。实践中建议不对正在导入管理的 EKS 资源使用 terraform 削减操作，而是仅stop NAT/ALB即可。

  - **💡 修复建议：** 引入一个专门的 make 目标（例如 `make stop-nat-alb`）只关停 NAT 和 ALB，不涉及 EKS 资源，以避免 Terraform 试图销毁 EKS相关状态。此外，Makefile 中提到的 `stop-hard` 目标目前未实现, 建议补充对应规则以自动调用 stop 和 eksctl delete。例如：

  ```make
  stop-hard: stop
  	eksctl delete cluster --name $(CLUSTER) --region $(REGION) --profile $(AWS_PROFILE) || true
  ```

  这样可一键执行完全停止流程。

- **资源残留收费：** 停止模式下，VPC 和某些少量资源保留不会产生成本。但如果您的 Hosted Zone 和 DynamoDB 锁表等长时间不用，也可选择删除以节省极小成本。留意 AWS Cost Explorer，确保无遗留昂贵资源（如 EIP 没有释放会产生少量闲置费用）。

- **重复环境冲突：** 如果您每日重建，不销毁 VPC，则 VPC ID 保持不变，Route 53 记录也一直存在，无需每天改。但若曾销毁又重建，留意更新 eksctl YAML 和 DNS 等配置为新资源。导入脚本、Terraform配置里的ID也要相应更新。保持各阶段脚本和配置参数同步对重复演练非常重要。

#### ✅ 阶段七验收清单

- [ ] 执行 **停止模式** 后：NAT Gateway、ALB 已删除，EKS 集群与节点组已删除但 VPC 等基础结构仍在。AWS 账户不再有持续计费的计算/网络资源，集群状态为**已删除**。
- [ ] 准备第二天重建：保留的 VPC 和 Terraform状态等验证正确，无意外变动。下次执行 `make start && make start-cluster` 能在原 VPC 上重新创建相同配置的集群，实现配置复用。
- [ ] 执行 **销毁模式** 后：本演练涉及的所有 AWS 资源均删除，无残留。S3 上 Terraform状态可备份删除。确认云上不再产生任何费用。
- [ ] Makefile 脚本改进建议已经记录或实施，如增加 **stop-hard** 规则便于一键彻底关停、避免 Terraform 停止时干扰导入资源等，提升每日演练流程的可靠性和便利性。
- [ ] 整个每日演练流程（阶段一至七）构成闭环，您已经能够在一天内从无到有部署出完整的云原生环境，并在结束时安全地将其拆除。演练过程中的问题也都得到解决，为将来的自动化和大规模部署打下基础。祝贺您完成每日演练！
