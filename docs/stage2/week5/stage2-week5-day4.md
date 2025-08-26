<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 4 - S3 最小接入 + IRSA](#day-4---s3-%E6%9C%80%E5%B0%8F%E6%8E%A5%E5%85%A5--irsa)
  - [目标](#%E7%9B%AE%E6%A0%87)
  - [Step 1/4：Terraform — S3 桶 + IRSA（最小权限到前缀）](#step-14terraform--s3-%E6%A1%B6--irsa%E6%9C%80%E5%B0%8F%E6%9D%83%E9%99%90%E5%88%B0%E5%89%8D%E7%BC%80)
    - [需要落实的修改（最小必需集）](#%E9%9C%80%E8%A6%81%E8%90%BD%E5%AE%9E%E7%9A%84%E4%BF%AE%E6%94%B9%E6%9C%80%E5%B0%8F%E5%BF%85%E9%9C%80%E9%9B%86)
      - [S3 Bucket（默认安全基线）](#s3-bucket%E9%BB%98%E8%AE%A4%E5%AE%89%E5%85%A8%E5%9F%BA%E7%BA%BF)
      - [IAM Policy（仅允许该前缀）](#iam-policy%E4%BB%85%E5%85%81%E8%AE%B8%E8%AF%A5%E5%89%8D%E7%BC%80)
      - [IRSA Role（信任 EKS OIDC，仅绑定到应用级 SA）](#irsa-role%E4%BF%A1%E4%BB%BB-eks-oidc%E4%BB%85%E7%BB%91%E5%AE%9A%E5%88%B0%E5%BA%94%E7%94%A8%E7%BA%A7-sa)
    - [具体的 HCL 文件改动](#%E5%85%B7%E4%BD%93%E7%9A%84-hcl-%E6%96%87%E4%BB%B6%E6%94%B9%E5%8A%A8)
      - [修改 `infra/aws/main.tf` 文件](#%E4%BF%AE%E6%94%B9-infraawsmaintf-%E6%96%87%E4%BB%B6)
      - [修改 `infra/aws/outputs.tf` 文件](#%E4%BF%AE%E6%94%B9-infraawsoutputstf-%E6%96%87%E4%BB%B6)
      - [修改 `infra/aws/terraform.tfvars` 文件](#%E4%BF%AE%E6%94%B9-infraawsterraformtfvars-%E6%96%87%E4%BB%B6)
      - [修改 `infra/aws/variables.tf` 文件](#%E4%BF%AE%E6%94%B9-infraawsvariablestf-%E6%96%87%E4%BB%B6)
      - [新增 `infra/aws/modules/app_irsa_s3/main.tf` 文件](#%E6%96%B0%E5%A2%9E-infraawsmodulesapp_irsa_s3maintf-%E6%96%87%E4%BB%B6)
      - [新增 `infra/aws/modules/app_irsa_s3/outputs.tf` 文件](#%E6%96%B0%E5%A2%9E-infraawsmodulesapp_irsa_s3outputstf-%E6%96%87%E4%BB%B6)
      - [新增 `infra/aws/modules/app_irsa_s3/variables.tf` 文件](#%E6%96%B0%E5%A2%9E-infraawsmodulesapp_irsa_s3variablestf-%E6%96%87%E4%BB%B6)
      - [新增 `infra/aws/modules/app_irsa_s3/versions.tf` 文件](#%E6%96%B0%E5%A2%9E-infraawsmodulesapp_irsa_s3versionstf-%E6%96%87%E4%BB%B6)
    - [Terraform 输出（供脚本与 K8s 使用）](#terraform-%E8%BE%93%E5%87%BA%E4%BE%9B%E8%84%9A%E6%9C%AC%E4%B8%8E-k8s-%E4%BD%BF%E7%94%A8)
    - [快速自检（CLI 即时校验）](#%E5%BF%AB%E9%80%9F%E8%87%AA%E6%A3%80cli-%E5%8D%B3%E6%97%B6%E6%A0%A1%E9%AA%8C)
  - [Step 2/4 — 给 SA 加 IRSA 注解 + 注入 S3 变量 + 回滚更新（不改应用代码）](#step-24--%E7%BB%99-sa-%E5%8A%A0-irsa-%E6%B3%A8%E8%A7%A3--%E6%B3%A8%E5%85%A5-s3-%E5%8F%98%E9%87%8F--%E5%9B%9E%E6%BB%9A%E6%9B%B4%E6%96%B0%E4%B8%8D%E6%94%B9%E5%BA%94%E7%94%A8%E4%BB%A3%E7%A0%81)
    - [准备变量（用 `terraform output` 的值）](#%E5%87%86%E5%A4%87%E5%8F%98%E9%87%8F%E7%94%A8-terraform-output-%E7%9A%84%E5%80%BC)
    - [更新 `post-recreate.sh` 文件](#%E6%9B%B4%E6%96%B0-post-recreatesh-%E6%96%87%E4%BB%B6)
    - [注入 S3 相关变量（复用已有的 ConfigMap/envFrom）](#%E6%B3%A8%E5%85%A5-s3-%E7%9B%B8%E5%85%B3%E5%8F%98%E9%87%8F%E5%A4%8D%E7%94%A8%E5%B7%B2%E6%9C%89%E7%9A%84-configmapenvfrom)
    - [应用并更新](#%E5%BA%94%E7%94%A8%E5%B9%B6%E6%9B%B4%E6%96%B0)
    - [基本自检](#%E5%9F%BA%E6%9C%AC%E8%87%AA%E6%A3%80)
  - [Step 3/4 — 集群内用 aws-cli 做 STS/S3 最小闭环验证（含越权被拒）](#step-34--%E9%9B%86%E7%BE%A4%E5%86%85%E7%94%A8-aws-cli-%E5%81%9A-stss3-%E6%9C%80%E5%B0%8F%E9%97%AD%E7%8E%AF%E9%AA%8C%E8%AF%81%E5%90%AB%E8%B6%8A%E6%9D%83%E8%A2%AB%E6%8B%92)
    - [写 Job 清单](#%E5%86%99-job-%E6%B8%85%E5%8D%95)
    - [运行与查看结果](#%E8%BF%90%E8%A1%8C%E4%B8%8E%E6%9F%A5%E7%9C%8B%E7%BB%93%E6%9E%9C)
    - [更新 `scripts/post-recreate.sh` 脚本](#%E6%9B%B4%E6%96%B0-scriptspost-recreatesh-%E8%84%9A%E6%9C%AC)
  - [Step 4/4 — S3 成本/安全增强（Gateway Endpoint + Bucket Policy 强化 + 前缀生命周期）](#step-44--s3-%E6%88%90%E6%9C%AC%E5%AE%89%E5%85%A8%E5%A2%9E%E5%BC%BAgateway-endpoint--bucket-policy-%E5%BC%BA%E5%8C%96--%E5%89%8D%E7%BC%80%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F)
    - [在私有子网打通直连：VPC Gateway Endpoint for S3（省 NAT 费）](#%E5%9C%A8%E7%A7%81%E6%9C%89%E5%AD%90%E7%BD%91%E6%89%93%E9%80%9A%E7%9B%B4%E8%BF%9Evpc-gateway-endpoint-for-s3%E7%9C%81-nat-%E8%B4%B9)
    - [Bucket Policy 强化（最小且不“误杀”现有流程）](#bucket-policy-%E5%BC%BA%E5%8C%96%E6%9C%80%E5%B0%8F%E4%B8%94%E4%B8%8D%E8%AF%AF%E6%9D%80%E7%8E%B0%E6%9C%89%E6%B5%81%E7%A8%8B)
    - [只清理“测试前缀”的生命周期（避免脏数据）](#%E5%8F%AA%E6%B8%85%E7%90%86%E6%B5%8B%E8%AF%95%E5%89%8D%E7%BC%80%E7%9A%84%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F%E9%81%BF%E5%85%8D%E8%84%8F%E6%95%B0%E6%8D%AE)
    - [具体的 HCL 文件改动](#%E5%85%B7%E4%BD%93%E7%9A%84-hcl-%E6%96%87%E4%BB%B6%E6%94%B9%E5%8A%A8-1)
      - [更新 `infra/aws/main.tf` 文件](#%E6%9B%B4%E6%96%B0-infraawsmaintf-%E6%96%87%E4%BB%B6)
      - [更新 `infra/aws/modules/app_irsa_s3/main.tf` 文件](#%E6%9B%B4%E6%96%B0-infraawsmodulesapp_irsa_s3maintf-%E6%96%87%E4%BB%B6)
      - [更新 `infra/aws/modules/app_irsa_s3/outputs.tf` 文件](#%E6%9B%B4%E6%96%B0-infraawsmodulesapp_irsa_s3outputstf-%E6%96%87%E4%BB%B6)
      - [更新 `infra/aws/modules/app_irsa_s3/variables.tf` 文件](#%E6%9B%B4%E6%96%B0-infraawsmodulesapp_irsa_s3variablestf-%E6%96%87%E4%BB%B6)
      - [更新 `infra/aws/modules/network_base/main.tf` 文件](#%E6%9B%B4%E6%96%B0-infraawsmodulesnetwork_basemaintf-%E6%96%87%E4%BB%B6)
      - [更新 `infra/aws/modules/network_base/outputs.tf` 文件](#%E6%9B%B4%E6%96%B0-infraawsmodulesnetwork_baseoutputstf-%E6%96%87%E4%BB%B6)
      - [更新 `infra/aws/outputs.tf` 文件](#%E6%9B%B4%E6%96%B0-infraawsoutputstf-%E6%96%87%E4%BB%B6)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 4 - S3 最小接入 + IRSA

## 目标

**IRSA + S3 最小闭环**：让 `svc-task/task-api` 在 **不使用任何长期密钥** 的前提下，凭 **ServiceAccount → IRSA → IAM Role** 访问 **S3** 的指定前缀，并完成端到端校验与安全/成本基线配置。

1. 新建 **S3 bucket**（Terraform 或控制台均可；**Block Public Access**、SSE-S3 开启）。
2. 配置 **IAM 角色**（IRSA）只允许 `bucket/$APP/*` 前缀读写；把 Role 绑定到 `${APP}-sa`。
3. 在应用添加 `/api/files/put?key=...` 与 `/api/files/get?key=...` 两个端点，演示最小读写。

验收清单：

- Pod（使用 `task-api` 的 SA）在容器内：
  - 能拿到 STS 身份（`aws sts get-caller-identity` 成功）。
  - 能对目标 **S3 桶/指定前缀** 执行最小操作（`PutObject/GetObject/List` 成功）。
  - 访问不在权限范围内的路径时 **被拒绝**（验证**最小权限**有效）。
- 所有变更可随 **`make start-all / stop-all`** 自动复现（Terraform + `post-recreate.sh` 已对齐）。

意义：

- **验证 Pod 能在完全没有长期密钥的情况下**，通过 **ServiceAccount** → **IRSA（EKS OIDC）** → **IAM Role** → **STS 短期凭证**访问外部云资源（以 **S3** 为例）。
- 同时验证**最小权限**（只放行到 `s3://<bucket>/<app-prefix>/*`，并把 `ListBucket` 限定到该前缀），证明“能力收敛而不是一把梭”。

---

## Step 1/4：Terraform — S3 桶 + IRSA（最小权限到前缀）

### 需要落实的修改（最小必需集）

#### S3 Bucket（默认安全基线）

**资源：**

1. `aws_s3_bucket`（名称全局唯一）
2. `aws_s3_bucket_public_access_block`（四项全开）
3. `aws_s3_bucket_ownership_controls`（`BucketOwnerEnforced`）
4. `aws_s3_bucket_server_side_encryption_configuration`（默认 **SSE-S3**）
5. `aws_s3_bucket_versioning`（`Enabled`）
6. `aws_s3_bucket_lifecycle_configuration`：仅对测试前缀（`s3_prefix`）做到期清理

**关键点：**

- 桶名**必须全局唯一**。
- 默认拒绝公网、默认加密；生命周期只作用在**测试前缀**上，避免误删。

#### IAM Policy（仅允许该前缀）

**资源：** `aws_iam_policy`（或 inline policy）

**策略要点（两条 Allow）：**

1. **ListBucket**：只允许列举 `s3_prefix`
   - `Action: "s3:ListBucket"`
   - `Resource: arn:aws:s3:::<bucket>`
   - `Condition`：`StringLike "s3:prefix" = ["${s3_prefix}*", "${s3_prefix}"]`
2. **Get/Put**：只允许对象在该前缀
   - `Action: ["s3:GetObject","s3:PutObject"]`
   - `Resource: arn:aws:s3:::<bucket>/${s3_prefix}*`

> 备注：暂不强制 `DeleteObject`，验证闭环够用；需要时单独加。

#### IRSA Role（信任 EKS OIDC，仅绑定到应用级 SA）

**资源：**

1. `aws_iam_role`（信任策略 `sts:AssumeRoleWithWebIdentity`）
   - `Principal.Federated = <EKS OIDC provider ARN>`
   - `Condition`：
     - `StringEquals "<OIDC_URL>:aud" = "sts.amazonaws.com"`
     - `StringEquals "<OIDC_URL>:sub" = "system:serviceaccount:${namespace}:${sa_name}"`
2. `aws_iam_role_policy_attachment`（把上面的 S3 policy 挂到该 Role）

> 注意：`<OIDC_URL>` 要去掉 `https://` 再拼：`replace(var.oidc_provider_url, "https://", "")`。

### 具体的 HCL 文件改动

#### 修改 `infra/aws/main.tf` 文件

新增内容：

```hcl
...

module "task_api" {
  source            = "./modules/app_irsa_s3"                    # 应用级 S3 桶 + IRSA 权限模块
  create_irsa       = var.create_eks                             # 仅在创建 EKS 时生成 IRSA 角色（s3 桶不受影响）
  cluster_name      = var.cluster_name                           # 集群名称
  region            = var.region                                 # 部署区域
  namespace         = var.task_api_namespace                     # 应用所在命名空间
  sa_name           = var.task_api_sa_name                       # 目标 ServiceAccount 名称
  app_name          = var.task_api_app_name                      # 应用名称
  s3_bucket_name    = var.task_api_s3_bucket_name                # 可选指定 S3 桶名称
  s3_prefix         = var.task_api_s3_prefix                     # S3 前缀
  oidc_provider_arn = module.eks.oidc_provider_arn               # OIDC Provider ARN
  oidc_provider_url = module.eks.oidc_provider_url_without_https # OIDC Provider URL（无 https）
  depends_on        = [module.eks]                               # 依赖 EKS 模块
}

...
```

#### 修改 `infra/aws/outputs.tf` 文件

新增内容：

```hcl
...

output "task_api_irsa_role_arn" {
  description = "task-api 应用使用的 IRSA Role ARN"
  value       = var.create_eks ? module.task_api.irsa_role_arn : null
}

output "task_api_bucket_name" {
  description = "task-api 应用的 S3 桶名称"
  value       = module.task_api.bucket_name
}

output "task_api_bucket_arn" {
  description = "task-api 应用的 S3 桶 ARN"
  value       = module.task_api.bucket_arn
}

output "task_api_s3_prefix" {
  description = "task-api 应用使用的 S3 前缀"
  value       = module.task_api.s3_prefix
}

output "task_api_bucket_url" {
  description = "task-api 应用的 S3 URL"
  value       = module.task_api.bucket_url
}
```

#### 修改 `infra/aws/terraform.tfvars` 文件

新增内容：

```hcl
...

# --- task-api 应用配置 ---
task_api_namespace      = "svc-task"
task_api_sa_name        = "task-api"
task_api_app_name       = "task-api"
task_api_s3_bucket_name = null
task_api_s3_prefix      = "task-api/"

...
```

#### 修改 `infra/aws/variables.tf` 文件

新增内容：

```hcl
...

# -------- Task API 应用配置 --------
variable "task_api_namespace" {
  description = "Namespace for task API ServiceAccount"
  type        = string
  default     = "svc-task"
}

variable "task_api_sa_name" {
  description = "ServiceAccount name for task API"
  type        = string
  default     = "task-api"
}

variable "task_api_app_name" {
  description = "Application name for task API"
  type        = string
  default     = "task-api"
}

variable "task_api_s3_bucket_name" {
  description = "Optional S3 bucket name for task API"
  type        = string
  default     = null
}

variable "task_api_s3_prefix" {
  description = "S3 prefix for task API objects"
  type        = string
  default     = "task-api/"
}

...
```

#### 新增 `infra/aws/modules/app_irsa_s3/main.tf` 文件

```hcl
// ---------------------------
// 应用级云权限模块：S3 桶 + IRSA Role
// ---------------------------

locals {
  bucket_name = coalesce( # 若未指定则生成唯一桶名
    var.s3_bucket_name,
    "${var.cluster_name}-${var.app_name}-${random_pet.bucket_suffix.id}"
  )
  oidc_url_without_https = var.oidc_provider_url == null ? null : replace(var.oidc_provider_url, "https://", "")
}

resource "random_pet" "bucket_suffix" {
  length    = 2
  separator = "-"
}

# --- S3 Bucket ---
resource "aws_s3_bucket" "this" {
  bucket = local.bucket_name # S3 桶名称

  tags = {
    Application = var.app_name     # 所属应用
    Environment = var.cluster_name # 环境/集群名
    Region      = var.region       # 部署区域
  }

  lifecycle {
    prevent_destroy = true # 避免每日销毁流程误删
  }
}

resource "aws_s3_bucket_public_access_block" "this" {
  bucket                  = aws_s3_bucket.this.id # 绑定到上方桶
  block_public_acls       = true                  # 禁止 Public ACL
  block_public_policy     = true                  # 禁止 Public Policy
  ignore_public_acls      = true                  # 忽略已有的 Public ACL
  restrict_public_buckets = true                  # 阻止公共桶
}

resource "aws_s3_bucket_ownership_controls" "this" {
  bucket = aws_s3_bucket.this.id

  rule {
    object_ownership = "BucketOwnerEnforced" # 统一所有权语义
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "this" {
  bucket = aws_s3_bucket.this.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256" # 默认使用 SSE-S3 加密
    }
  }
}

resource "aws_s3_bucket_versioning" "this" {
  bucket = aws_s3_bucket.this.id

  versioning_configuration {
    status = "Enabled" # 打开版本控制以便审计回滚
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "this" {
  bucket = aws_s3_bucket.this.id

  rule {
    id     = "cleanup-test-prefix" # 仅清理测试前缀
    status = "Enabled"

    filter {
      prefix = var.s3_prefix # 作用于指定前缀
    }

    expiration {
      days = 30 # 30 天后自动过期
    }
  }
}

# --- IAM Policy ---
resource "aws_iam_policy" "this" {
  count       = var.create_irsa ? 1 : 0 # 仅在需要 IRSA 时创建策略
  name        = "${var.cluster_name}-${var.app_name}-s3"
  description = "Minimal S3 access for ${var.app_name}"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = ["s3:ListBucket"]
        Resource = aws_s3_bucket.this.arn
        Condition = {
          StringLike = {
            "s3:prefix" = ["${var.s3_prefix}*"]
          }
        }
      },
      {
        Effect   = "Allow"
        Action   = ["s3:GetObject", "s3:PutObject"]
        Resource = "${aws_s3_bucket.this.arn}/${var.s3_prefix}*"
      }
    ]
  })
}

# --- IRSA Role ---
resource "aws_iam_role" "this" {
  count       = var.create_irsa ? 1 : 0 # 控制 IRSA 角色创建
  name        = "${var.cluster_name}-${var.app_name}-irsa"
  description = "IRSA role for ${var.app_name}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = "sts:AssumeRoleWithWebIdentity"
        Principal = {
          Federated = var.oidc_provider_arn
        }
        Condition = {
          StringEquals = {
            "${local.oidc_url_without_https}:aud" = "sts.amazonaws.com"
            "${local.oidc_url_without_https}:sub" = "system:serviceaccount:${var.namespace}:${var.sa_name}"
          }
        }
      }
    ]
  })

  lifecycle {
    create_before_destroy = true # 确保替换时先建后删
  }
}

resource "aws_iam_role_policy_attachment" "this" {
  count      = var.create_irsa ? 1 : 0 # 仅在创建 IRSA 时附加策略
  role       = aws_iam_role.this[0].name
  policy_arn = aws_iam_policy.this[0].arn

  lifecycle {
    create_before_destroy = true
  }
}
```

#### 新增 `infra/aws/modules/app_irsa_s3/outputs.tf` 文件

```hcl
output "irsa_role_arn" {
  description = "IRSA role ARN"
  value       = try(aws_iam_role.this[0].arn, null)
}

output "bucket_name" {
  description = "S3 bucket name"
  value       = aws_s3_bucket.this.bucket
}

output "bucket_arn" {
  description = "S3 bucket ARN"
  value       = aws_s3_bucket.this.arn
}

output "s3_prefix" {
  description = "S3 prefix for objects"
  value       = var.s3_prefix
}

output "bucket_url" {
  description = "S3 URL with prefix"
  value       = "s3://${aws_s3_bucket.this.bucket}/${var.s3_prefix}"
}
```

#### 新增 `infra/aws/modules/app_irsa_s3/variables.tf` 文件

```hcl
variable "cluster_name" {
  description = "EKS cluster name"
  type        = string
}

variable "region" {
  description = "AWS region"
  type        = string
}

variable "namespace" {
  description = "Kubernetes namespace for the ServiceAccount"
  type        = string
}

variable "sa_name" {
  description = "Kubernetes ServiceAccount name"
  type        = string
}

variable "app_name" {
  description = "Application name"
  type        = string
}

variable "s3_bucket_name" {
  description = "S3 bucket name (optional)"
  type        = string
  default     = null
}

variable "s3_prefix" {
  description = "S3 object prefix"
  type        = string
}

variable "oidc_provider_arn" {
  description = "OIDC provider ARN"
  type        = string
  default     = null
}

variable "oidc_provider_url" {
  description = "OIDC provider URL"
  type        = string
  default     = null
}

variable "create_irsa" {
  description = "Whether to create IAM policy and IRSA role"
  type        = bool
  default     = true
}
```

#### 新增 `infra/aws/modules/app_irsa_s3/versions.tf` 文件

```hcl
// 模块使用的 Terraform 及 provider 版本
terraform {
  required_version = "~> 1.12"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.7"
    }
  }
}
```

### Terraform 输出（供脚本与 K8s 使用）

运行 `terraform apply` 后，Terraform 的 Outputs 包含如下：

```bash
...
task_api_bucket_arn = "arn:aws:s3:::dev-task-api-welcomed-anteater"
task_api_bucket_name = "dev-task-api-welcomed-anteater"
task_api_bucket_url = "s3://dev-task-api-welcomed-anteater/task-api/"
task_api_irsa_role_arn = "arn:aws:iam::563149051155:role/dev-task-api-irsa"
task_api_s3_prefix = "task-api/"
...
```

### 快速自检（CLI 即时校验）

先设置：

```bash
export AWS_PROFILE=phase2-sso
export AWS_REGION=us-east-1
```

**IRSA Role 存在且附加了策略**

```bash
$ aws iam get-role --role-name "dev-task-api-irsa" \
  --query 'Role.Arn'

"arn:aws:iam::563149051155:role/dev-task-api-irsa"

$ aws iam list-attached-role-policies --role-name "dev-task-api-irsa" \
  --query 'AttachedPolicies[].PolicyName'

[
    "dev-task-api-s3"
]

```

**信任策略绑定到 SA**

```bash
$ aws iam get-role --role-name "dev-task-api-irsa" \
  --query 'Role.AssumeRolePolicyDocument.Statement[0].Condition'

{
    "StringEquals": {
        "oidc.eks.us-east-1.amazonaws.com/id/832372465C509509317C17435FAFD16D:sub": "system:serviceaccount:svc-task:task-api",
        "oidc.eks.us-east-1.amazonaws.com/id/832372465C509509317C17435FAFD16D:aud": "sts.amazonaws.com"
    }
}

# 已经包含：
#   "<OIDC_HOST>:aud": "sts.amazonaws.com"
#   "<OIDC_HOST>:sub": "system:serviceaccount:svc-task:task-api"
```

**S3 桶安全基线**

```bash
$ aws s3api get-bucket-encryption --bucket "dev-task-api-welcomed-anteater"

{
    "ServerSideEncryptionConfiguration": {
        "Rules": [
            {
                "ApplyServerSideEncryptionByDefault": {
                    "SSEAlgorithm": "AES256"
                },
                "BucketKeyEnabled": false
            }
        ]
    }
}

$ aws s3api get-public-access-block --bucket "dev-task-api-welcomed-anteater"

{
    "PublicAccessBlockConfiguration": {
        "BlockPublicAcls": true,
        "IgnorePublicAcls": true,
        "BlockPublicPolicy": true,
        "RestrictPublicBuckets": true
    }
}

$ aws s3api get-bucket-ownership-controls --bucket "dev-task-api-welcomed-anteater"

{
    "OwnershipControls": {
        "Rules": [
            {
                "ObjectOwnership": "BucketOwnerEnforced"
            }
        ]
    }
}
```

**前缀约束**：

- Role 关联的 Policy `dev-task-api-s3` 的 JSON 已经把资源限定到 `arn:aws:s3:::dev-task-api-welcomed-anteater/task-api/*`：
     ```json
     {
         "Statement": [
             {
                 "Action": [
                     "s3:ListBucket"
                 ],
                 "Condition": {
                     "StringLike": {
                         "s3:prefix": [
                             "task-api/*"
                         ]
                     }
                 },
                 "Effect": "Allow",
                 "Resource": "arn:aws:s3:::dev-task-api-welcomed-anteater"
             },
             {
                 "Action": [
                     "s3:GetObject",
                     "s3:PutObject"
                 ],
                 "Effect": "Allow",
                 "Resource": "arn:aws:s3:::dev-task-api-welcomed-anteater/task-api/*"
             }
         ],
         "Version": "2012-10-17"
     }
     ```
- 真正功能性验证会在 **Step 3/4** 用 Pod 内 `aws-cli` 来执行。

---

## Step 2/4 — 给 SA 加 IRSA 注解 + 注入 S3 变量 + 回滚更新（不改应用代码）

### 准备变量（用 `terraform output` 的值）

```bash
export WORK_DIR="/mnt/d/0Repositories/CloudNative"
export AWS_PROFILE=phase2-sso
export AWS_REGION=us-east-1
export NS=svc-task
export APP=task-api
export TASK_API_SERVICE_ACCOUNT_NAME="task-api"

# 来自 Terraform 输出
export ROLE_ARN="arn:aws:iam::563149051155:role/dev-task-api-irsa"
export S3_BUCKET="dev-task-api-welcomed-anteater"
export S3_PREFIX="task-api/"
```

### 更新 `post-recreate.sh` 文件

新增如下内容：

```bash
...

# 确保 task-api 的 ServiceAccount 存在并带 IRSA 注解
ensure_task_api_service_account() {
  log "🛠️ 确保 task-api ServiceAccount ${TASK_API_SERVICE_ACCOUNT_NAME} 存在并带 IRSA 注解"
  if ! kubectl -n $NS get sa $TASK_API_SERVICE_ACCOUNT_NAME >/dev/null 2>&1; then
    log "创建 ServiceAccount ${TASK_API_SERVICE_ACCOUNT_NAME}"
    kubectl -n ${NS} create serviceaccount ${TASK_API_SERVICE_ACCOUNT_NAME}
  fi
  # 写入/覆盖 IRSA 注解
  kubectl -n ${NS} annotate sa ${TASK_API_SERVICE_ACCOUNT_NAME} \
    "eks.amazonaws.com/role-arn=${TASK_API_ROLE_ARN}" --overwrite
}

# === 部署 task-api 到 EKS（幂等）===
deploy_task_api() {
  ...
  kubectl -n "${NS}" apply -f "${K8S_BASE_DIR}/ns-sa.yaml"
  # 在这里调用从而 确保应用级 SA 带 IRSA 注解
  ensure_task_api_service_account
  ...
}

...
```

### 注入 S3 相关变量（复用已有的 ConfigMap/envFrom）

> 之前的 `ConfigMap task-api-config` 已经通过 `envFrom` 注入到容器。
> 现在只需要在 **ConfigMap 文件** 里增加三项，然后 apply。

修改文件路径：`${WORK_DIR}/task-api/k8s/base/configmap.yaml`

将 `data:` 下新增这三行（保持其它键不变）：

```yaml
data:
  APP_NAME: "task-api"
  WELCOME_MSG: "hello from ${AWS_REGION}"
  S3_BUCKET: "dev-task-api-welcomed-anteater"
  S3_PREFIX: "task-api/"
  AWS_REGION: "us-east-1"
```

### 应用并更新

如果已经执行了每日销毁，则完成前面修改后，直接重建即可进行基本自检。

如果是处于每日正常运行的状态，则执行如下命令来应用修改。

```bash
# 直接 annotate
kubectl -n "$NS" annotate sa "$TASK_API_SERVICE_ACCOUNT_NAME" \
  "eks.amazonaws.com/role-arn=$ROLE_ARN" --overwrite

# 应用并滚动更新
kubectl apply -f "${WORK_DIR}/task-api/k8s/base/configmap.yaml"
kubectl -n "$NS" rollout restart deploy/"$APP"
kubectl -n "$NS" rollout status deploy/"$APP" --timeout=180s
```

### 基本自检

确认 IRSA 注入与环境变量就绪。

```bash
$ kubectl -n "$NS" get sa "$TASK_API_SERVICE_ACCOUNT_NAME" -o yaml | grep -n "eks.amazonaws.com/role-arn"
# 能看到注解里的 Role ARN
5:    eks.amazonaws.com/role-arn: arn:aws:iam::563149051155:role/dev-task-api-irsa

# 取一个 Pod 名并检查环境：
$ POD=$(kubectl -n "$NS" get pods -l app="$TASK_API_SERVICE_ACCOUNT_NAME" -o jsonpath='{.items[0].metadata.name}')

$ kubectl -n "$NS" exec "$POD" -- sh -lc 'env | grep -E "S3_BUCKET|S3_PREFIX|AWS_REGION|AWS_ROLE_ARN|AWS_WEB_IDENTITY_TOKEN_FILE"'
# 能看到 `AWS_ROLE_ARN` 与 `AWS_WEB_IDENTITY_TOKEN_FILE`（由 EKS Webhook 自动注入）
# 能看到 `S3_BUCKET/S3_PREFIX/AWS_REGION` 三个自定义变量
AWS_ROLE_ARN=arn:aws:iam::563149051155:role/dev-task-api-irsa
AWS_WEB_IDENTITY_TOKEN_FILE=/var/run/secrets/eks.amazonaws.com/serviceaccount/token
S3_BUCKET=dev-task-api-welcomed-anteater
WELCOME_MSG=hello from ${AWS_REGION}
AWS_REGION=us-east-1
S3_PREFIX=task-api/

$ kubectl -n "$NS" exec "$POD" -- sh -lc 'ls -l /var/run/secrets/eks.amazonaws.com/serviceaccount/ && [ -s /var/run/secrets/eks.amazonaws.com/serviceaccount/token ] && echo "token OK"'
# 确认 WebIdentity Token 已挂载
# `token OK` 表示投影令牌存在。
total 0
lrwxrwxrwx 1 root root 12 Aug 25 17:56 token -> ..data/token
token OK
```

---

## Step 3/4 — 集群内用 aws-cli 做 STS/S3 最小闭环验证（含越权被拒）

在集群里起一个一次性 **aws-cli Job**，使用 `serviceAccountName: task-api`（已绑定 IRSA）完成：

1. 取 STS 身份；
2. 在**允许的前缀**写入/列举/读取；
3. 在**不允许的前缀**尝试写入并确认被拒。

### 写 Job 清单

> 前面已经把 `S3_BUCKET/S3_PREFIX/AWS_REGION` 放在 `ConfigMap task-api-config`，这里直接 `envFrom` 复用。

新建文件：`$WORK_DIR/task-api/k8s/awscli-smoke.yaml`

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: awscli-smoke
  namespace: svc-task
spec:
  backoffLimit: 0
  template:
    spec:
      serviceAccountName: task-api
      restartPolicy: Never
      containers:
        - name: awscli
          image: amazon/aws-cli:2.17.33
          imagePullPolicy: IfNotPresent
          envFrom:
            - configMapRef:
                name: task-api-config
          command: ["sh","-lc"]
          args:
            - |
              set -euo pipefail
              echo "== STS get-caller-identity =="
              aws sts get-caller-identity --output json

              TS=$(date +%s)
              KEY_OK="${S3_PREFIX%/}/smoke/${TS}.txt"      # 允许的前缀
              KEY_DENY="not-allowed/${TS}.txt"             # 不允许的前缀（应被拒）
              echo "hello from IRSA $(date -Iseconds)" > /tmp/x.txt

              echo "== Put to allowed prefix =="
              aws s3 cp /tmp/x.txt "s3://${S3_BUCKET}/${KEY_OK}"

              echo "== List allowed prefix (top 5) =="
              aws s3 ls "s3://${S3_BUCKET}/${S3_PREFIX%/}/smoke/" | head -n 5 || true

              echo "== Get just uploaded object =="
              aws s3 cp "s3://${S3_BUCKET}/${KEY_OK}" - | head -c 80; echo

              echo "== Negative test: put to DISALLOWED prefix (should fail) =="
              if aws s3 cp /tmp/x.txt "s3://${S3_BUCKET}/${KEY_DENY}"; then
                echo "UNEXPECTED: write to disallowed prefix succeeded"; exit 2
              else
                echo "EXPECTED: AccessDenied on disallowed prefix"
              fi

              echo "ALL OK"
```

### 运行与查看结果

```bash
$ kubectl apply -f "$WORK_DIR/task-api/k8s/awscli-smoke.yaml"
# 输出：
job.batch/awscli-smoke created

# 等待完成（成功或失败都会结束）
$ kubectl -n svc-task wait --for=condition=complete job/awscli-smoke --timeout=180s || true
# 输出：
job.batch/awscli-smoke condition met

# 查看日志（应包含 STS 信息、Put/List/Get 成功，以及对 not-allowed 前缀的 AccessDenied）
$ kubectl -n svc-task logs job/awscli-smoke

# 输出：
# `aws sts get-caller-identity` 返回 `Account/Arn`（说明 IRSA 生效）
# 允许前缀的 `cp/ls/cp` 成功。
# 不允许前缀写入显示 **AccessDenied** 或类似拒绝信息，并打印 `EXPECTED: AccessDenied`。

== STS get-caller-identity ==
{
    "UserId": "AROAYGHSMSUJ2PGBBJHBY:botocore-session-1756147736",
    "Account": "563149051155",
    "Arn": "arn:aws:sts::563149051155:assumed-role/dev-task-api-irsa/botocore-session-1756147736"
}
== Put to allowed prefix ==
upload: ../tmp/x.txt to s3://dev-task-api-welcomed-anteater/task-api/smoke/1756147736.txt
== List allowed prefix (top 5) ==
2025-08-25 18:48:58         41 1756147736.txt
== Get just uploaded object ==
hello from IRSA 2025-08-25T18:48:56+0000

== Negative test: put to DISALLOWED prefix (should fail) ==
upload failed: ../tmp/x.txt to s3://dev-task-api-welcomed-anteater/not-allowed/1756147736.txt An error occurred (AccessDenied) when calling the PutObject operation: User: arn:aws:sts::563149051155:assumed-role/dev-task-api-irsa/botocore-session-1756147736 is not authorized to perform: s3:PutObject on resource: "arn:aws:s3:::dev-task-api-welcomed-anteater/not-allowed/1756147736.txt" because no identity-based policy allows the s3:PutObject action
EXPECTED: AccessDenied on disallowed prefix
ALL OK

# 验证后清理
$ kubectl -n svc-task delete job awscli-smoke --ignore-not-found
# 输出：
job.batch "awscli-smoke" deleted
```

### 更新 `scripts/post-recreate.sh` 脚本

新增如下内容：

```sh
...

# ---- aws-cli IRSA smoke test ----
# Launches a temporary aws-cli Job (serviceAccount=task-api) to:
#   1) call STS get-caller-identity
#   2) write/list/read under the allowed S3 prefix
#   3) verify writes to a disallowed prefix are denied
awscli_s3_smoke() {
  log "🧪 aws-cli IRSA S3 smoke test"
  local manifest="${ROOT_DIR}/task-api/k8s/awscli-smoke.yaml"

  kubectl apply -f "$manifest"

  if ! kubectl -n "$NS" wait --for=condition=complete job/awscli-smoke --timeout=180s; then
    kubectl -n "$NS" logs job/awscli-smoke || true
    kubectl -n "$NS" delete job awscli-smoke --ignore-not-found
    abort "aws-cli smoke job failed"
  fi

  kubectl -n "$NS" logs job/awscli-smoke || true
  kubectl -n "$NS" delete job awscli-smoke --ignore-not-found
  log "✅ aws-cli smoke test finished"
}

# 检查 task-api
check_task_api() {
  log "🔎 验证 IRSA 注入与运行时环境"

  # 1) ServiceAccount 注解检查
  kubectl -n "${NS}" get sa "${TASK_API_SERVICE_ACCOUNT_NAME}" -o yaml | \
    grep -q "eks.amazonaws.com/role-arn" || \
    abort "ServiceAccount 未正确注解 eks.amazonaws.com/role-arn"

  # 2) 获取一个 Pod 名称以检查环境变量
  local pod
  pod=$(kubectl -n "${NS}" get pods -l app="${TASK_API_SERVICE_ACCOUNT_NAME}" \
    -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || true)
  [[ -z "$pod" ]] && abort "未找到 ${APP} Pod，无法进行 IRSA 自检"

  # 等待 Pod 进入 Running 状态
  local wait_time=0
  local max_wait=60
  while [[ $wait_time -lt $max_wait ]]; do
    pod_status=$(kubectl -n "${NS}" get pod "$pod" -o jsonpath='{.status.phase}' 2>/dev/null || echo "Unknown")
    if [[ "$pod_status" == "Running" ]]; then
      break
    fi
    sleep 3
    wait_time=$((wait_time+3))
  done
  [[ "$pod_status" != "Running" ]] && abort "Pod $pod 未进入 Running 状态，当前状态: $pod_status"

  # 3) 确认关键环境变量存在
  local env_out
  env_out=$(kubectl -n "${NS}" exec "$pod" -- sh -lc 'env') || \
    abort "无法获取 Pod 环境变量"
  for key in S3_BUCKET S3_PREFIX AWS_REGION AWS_ROLE_ARN AWS_WEB_IDENTITY_TOKEN_FILE; do
    echo "$env_out" | grep -q "^${key}=" || abort "缺少环境变量 ${key}"
  done

  # 4) 确认 WebIdentity Token 已正确挂载
  kubectl -n "${NS}" exec "$pod" -- sh -lc \
    'ls -l /var/run/secrets/eks.amazonaws.com/serviceaccount/ && [ -s /var/run/secrets/eks.amazonaws.com/serviceaccount/token ]' >/dev/null || \
     abort "WebIdentity Token 缺失或为空"

  log "✅ task-api ServiceAccount IRSA 自检通过"

  log "🔎 验证 task-api ALB、Ingress、dns"

  local outdir="${SCRIPT_DIR}/.out"; mkdir -p "$outdir"
  local dns

  log "⏳ Waiting for ALB to be provisioned ..."
  # 获取 ALB 的 DNS 名称
  local t=0; local timeout=600
  while [[ $t -lt $timeout ]]; do
    dns=$(kubectl -n "$NS" get ing "$APP" -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || true)
    [[ -n "${dns}" ]] && break
    sleep 5; t=$((t+5))
  done
  [[ -z "${dns}" ]] && abort "Timeout waiting ALB"

  log "✅ ALB ready: http://${dns}"
  echo "${dns}" > "${outdir}/alb_${APP}_dns"

  log "🧪 ALB DNS Smoke test: "
  local smoke_retries=10
  local smoke_ok=0
  local smoke_wait=5
  for ((i=1; i<=smoke_retries; i++)); do
    if curl -sf "http://${dns}/api/hello?name=Renda" | sed -n '1p'; then
      smoke_ok=1
      break
    else
      log "⏳ Smoke test attempt $i/${smoke_retries} failed, retrying in ${smoke_wait}s..."
      sleep $smoke_wait
    fi
  done
  [[ $smoke_ok -eq 0 ]] && abort "Smoke test failed: /api/hello (DNS may not be ready or network issue)"
  curl -s "http://${dns}/actuator/health" | grep '"status":"UP"' || { log "❌ Health check failed"; return 1; }

  log "✅ ALB DNS Smoke test passed"

  awscli_s3_smoke
}

...

check_task_api
```

---

## Step 4/4 — S3 成本/安全增强（Gateway Endpoint + Bucket Policy 强化 + 前缀生命周期）

### 在私有子网打通直连：VPC Gateway Endpoint for S3（省 NAT 费）

1. 在 `infra/aws/` 下的你认为合适的位置或方式添加 **S3 网关端点** 并把它**关联到所有“私有子网”的路由表**。
2. 打上清晰标签，便于巡检。

**关键细节：**

- 只把**私有子网**的路由表关联到 S3 Endpoint，公有子网通常不需要。
- 端点就绪后，私网里的 EC2/Pod 访问 S3 **不再走 NAT**，计费显著下降。

**Terraform 输出**

```bash
s3_gateway_endpoint_id = "vpce-00537e89ab3325cf4"
```

**快速自检：**

```bash
# 1) 端点状态
$ aws ec2 describe-vpc-endpoints --filters "Name=service-name,Values=com.amazonaws.us-east-1.s3" --profile phase2-sso --region us-east-1 \
  --query 'VpcEndpoints[].{Id:VpcEndpointId,State:State,Type:VpcEndpointType,RTs:RouteTableIds}'

# 输出：
[
    {
        "Id": "vpce-00537e89ab3325cf4",
        "State": "available",
        "Type": "Gateway",
        "RTs": [
            "rtb-026a8fa8865c4474c",
            "rtb-00dc799eaa7b2ae78"
        ]
    }
]

# 2) 路由表出现 S3 前缀列表路由（pl-开头）
$ aws ec2 describe-route-tables \
  --route-table-ids "rtb-026a8fa8865c4474c" "rtb-00dc799eaa7b2ae78" \
  --profile phase2-sso \
  --region us-east-1 \
  --query 'RouteTables[].Routes[?DestinationPrefixListId != `null` && contains(DestinationPrefixListId, `pl-`)]'

# 输出：
[
    [
        {
            "DestinationPrefixListId": "pl-63a5400a",
            "GatewayId": "vpce-00537e89ab3325cf4",
            "Origin": "CreateRoute",
            "State": "active"
        }
    ],
    [
        {
            "DestinationPrefixListId": "pl-63a5400a",
            "GatewayId": "vpce-00537e89ab3325cf4",
            "Origin": "CreateRoute",
            "State": "active"
        }
    ]
]
```

### Bucket Policy 强化（最小且不“误杀”现有流程）

给 `aws_s3_bucket` 增加一份 **Bucket Policy**（如果已经有了就更新，如果没有就新增），至少包含两条“保护性 Deny”：

1. **强制 TLS 传输**（任何非 HTTPS 一律拒绝）
   - `Effect: Deny`
   - `Principal: "*"`
   - `Action: ["s3:GetObject","s3:PutObject","s3:DeleteObject"]`
   - `Condition: Bool -> aws:SecureTransport = false`
2. **仅允许来自 VPC 的访问**（经 **Gateway Endpoint**）
   - `Effect: Deny` / `Principal: "*"`
   - `Action: ["s3:GetObject","s3:PutObject","s3:DeleteObject"]`
   - `Condition: StringNotEqualsIfExists -> aws:SourceVpc = "<VPC ID>"`

> 说明：
>
> - 我们**不**在 Bucket Policy 里强制 `x-amz-server-side-encryption` 头，因为已启用 **默认 SSE-S3**；强制请求头会让 `aws s3 cp` 失败（它默认不带头）。
> - 不推荐直接用 `NotPrincipal` 锁死到单一 Role，会**影响控制台/临时运维**；先用 “TLS + SourceVpc” 两个护栏即可。

**Terraform 输出**

```bash
task_api_bucket_policy_id = "dev-task-api-welcomed-anteater"
```

**快速自检：**

```bash
$ aws s3api get-bucket-policy --profile phase2-sso --bucket "de
v-task-api-welcomed-anteater" --query 'Policy' | jq -r .
```

输出：

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "DenyInsecureTransport",
      "Effect": "Deny",
      "Principal": "*",
      "Action": [
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject"
      ],
      "Resource": [
        "arn:aws:s3:::dev-task-api-welcomed-anteater/*",
        "arn:aws:s3:::dev-task-api-welcomed-anteater"
      ],
      "Condition": {
        "Bool": {
          "aws:SecureTransport": "false"
        }
      }
    },
    {
      "Sid": "DenyNonVpc",
      "Effect": "Deny",
      "Principal": "*",
      "Action": [
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject"
      ],
      "Resource": [
        "arn:aws:s3:::dev-task-api-welcomed-anteater/*",
        "arn:aws:s3:::dev-task-api-welcomed-anteater"
      ],
      "Condition": {
        "StringNotEqualsIfExists": {
          "aws:SourceVpc": "vpc-0b06ba5bfab99498b"
        }
      }
    }
  ]
}
```

### 只清理“测试前缀”的生命周期（避免脏数据）

在 `aws_s3_bucket_lifecycle_configuration` 增加针对 **测试/临时** 前缀的规则，例如：

- `ID = "cleanup-smoke"`
- `Filter` 只匹配 `task-api/smoke/`（或实际用的压测前缀）
- `Expiration`：7～14 天

**关键细节：**

- **仅**作用于 `smoke/` 这类临时前缀，不要覆盖业务数据。
- 如果启用了 Versioning，可额外清理 `NoncurrentVersionExpiration`。

**Terraform 输出**

```bash
task_api_bucket_lifecycle_rules = [
  "cleanup-smoke:Enabled",
]
```

**快速自检：**

```bash
$ aws s3api get-bucket-lifecycle-configuration --profile phase2-sso --bucket "dev-task-api-welcomed-anteater"
```

输出：

```json
{
    "TransitionDefaultMinimumObjectSize": "all_storage_classes_128K",
    "Rules": [
        {
            "Expiration": {
                "Days": 7
            },
            "ID": "cleanup-smoke",
            "Filter": {
                "Prefix": "task-api/smoke/"
            },
            "Status": "Enabled",
            "NoncurrentVersionExpiration": {
                "NoncurrentDays": 7
            }
        }
    ]
}
```

### 具体的 HCL 文件改动

#### 更新 `infra/aws/main.tf` 文件

在 task_api 模块中新增如下：

```hcl
...
module "task_api" {
  ...
  # 新增如下行，其他保持不变
  vpc_id            = module.network_base.vpc_id                 # 桶策略限制访问的 VPC
  ...
}
...
```

#### 更新 `infra/aws/modules/app_irsa_s3/main.tf` 文件

修改 `aws_s3_bucket_lifecycle_configuration` 资源为如下：

```hcl
...
resource "aws_s3_bucket_lifecycle_configuration" "this" {
  bucket = aws_s3_bucket.this.id

  # 仅清理临时 smoke 前缀，避免误删业务数据
  rule {
    id     = "cleanup-test-prefix" # 仅清理测试前缀
    id     = "cleanup-smoke"
    status = "Enabled"

    filter {
      prefix = var.s3_prefix # 作用于指定前缀
      prefix = "${var.s3_prefix}smoke/" # 目标前缀：<prefix>smoke/
    }

    expiration {
      days = 30 # 30 天后自动过期
      days = 7 # 7 天后自动过期
    }

    noncurrent_version_expiration {
      noncurrent_days = 7 # 清理旧版本
    }
  }
}
...
```

新增如下内容：

```hcl
...

# --- Bucket Policy ---
data "aws_iam_policy_document" "bucket" {
  statement {
    sid    = "DenyInsecureTransport"
    effect = "Deny"
    principals {
      type        = "*"
      identifiers = ["*"]
    }
    # 仅“数据面”动作：
    # - 避免阻断管理面（Get/PutBucketPolicy、PutBucketLifecycleConfiguration 等）
    # - 避免刷新时从公网端点读取桶信息被 403
    actions = [
      "s3:GetObject",
      "s3:PutObject",
      "s3:DeleteObject"
    ]
    resources = [
      aws_s3_bucket.this.arn,
      "${aws_s3_bucket.this.arn}/*"
    ]
    condition {
      test     = "Bool"
      variable = "aws:SecureTransport"
      values   = ["false"]
    }
  }

  dynamic "statement" {
    for_each = var.vpc_id == null ? [] : [var.vpc_id]
    content {
      sid    = "DenyNonVpc"
      effect = "Deny"
      principals {
        type        = "*"
        identifiers = ["*"]
      }
      # 同样只覆盖“数据面”动作
      actions = [
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject"
      ]
      resources = [
        aws_s3_bucket.this.arn,
        "${aws_s3_bucket.this.arn}/*"
      ]
      condition {
        # IfExists 避免在没有 SourceVpc 上下文（例如公网端点）时被误判为“不等”
        test     = "StringNotEqualsIfExists"
        variable = "aws:SourceVpc"
        values   = [var.vpc_id]
      }
    }
  }
}

resource "aws_s3_bucket_policy" "this" {
  bucket = aws_s3_bucket.this.id
  policy = data.aws_iam_policy_document.bucket.json
}

...
```

#### 更新 `infra/aws/modules/app_irsa_s3/outputs.tf` 文件

新增如下内容：

```hcl
...

output "bucket_policy_id" {
  description = "S3 bucket policy resource ID"
  value       = aws_s3_bucket_policy.this.id
}

output "bucket_lifecycle_rules" {
  description = "Lifecycle rule IDs and statuses"
  value       = [for r in aws_s3_bucket_lifecycle_configuration.this.rule : "${r.id}:${r.status}"]
}
```

#### 更新 `infra/aws/modules/app_irsa_s3/variables.tf` 文件

新增如下内容：

```hcl
...

variable "vpc_id" {
  description = "VPC ID for optional bucket policy SourceVpc restriction"
  type        = string
  default     = null
}
```

#### 更新 `infra/aws/modules/network_base/main.tf` 文件

新增如下内容：

```hcl
...
data "aws_region" "current" {}               # 当前区域，用于 VPC Endpoint（使用 id 字段）
...
# S3 Gateway Endpoint：私有子网直连 S3，绕过 NAT 以节省成本
resource "aws_vpc_endpoint" "s3" {
  vpc_id            = aws_vpc.this.id
  service_name      = "com.amazonaws.${data.aws_region.current.id}.s3"
  vpc_endpoint_type = "Gateway"
  route_table_ids   = aws_route_table.private[*].id # 仅关联私有路由表

  tags = {
    Name        = "${var.cluster_name}-s3-endpoint"
    ManagedBy   = "Terraform"
    Description = "Gateway endpoint for S3"
  }
}
...
```

#### 更新 `infra/aws/modules/network_base/outputs.tf` 文件

新增如下内容：

```hcl
...
output "s3_gateway_endpoint_id" {
  description = "S3 网关端点 ID"
  value       = aws_vpc_endpoint.s3.id
}
...
```

#### 更新 `infra/aws/outputs.tf` 文件

新增如下内容：

```hcl
...
output "s3_gateway_endpoint_id" {
  description = "S3 网关端点 ID"
  value       = module.network_base.s3_gateway_endpoint_id
}
...

output "task_api_bucket_policy_id" {
  description = "task-api 桶策略资源 ID"
  value       = module.task_api.bucket_policy_id
}

output "task_api_bucket_lifecycle_rules" {
  description = "task-api 桶生命周期规则及状态"
  value       = module.task_api.bucket_lifecycle_rules
}
```

---
