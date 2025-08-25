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
