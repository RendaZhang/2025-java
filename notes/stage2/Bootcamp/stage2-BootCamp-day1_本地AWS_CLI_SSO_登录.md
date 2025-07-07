# AWS SSO + Terraform Local Workflow Cheat Sheet

> **Purpose ｜用途**
>
> ① 快速回顾 "Console 侧启用 IAM Identity Center → 本地 AWS CLI SSO 登录 → Terraform init" 的完整流水。
> ② 隔一段时间后想不起细节，或换到 macOS / 另一台电脑时，可按本表再跑一次。

______________________________________________________________________

## 0 · 关键参数 (你的环境)

| 名称 | 当前值 |
| -------------------------- | ---------------------------------------- |
| **SSO Start URL** | `https://d-9066388969.awsapps.com/start` |
| **SSO Region (Home)** | `us-east-1` |
| **AWS Account ID** | `563149051155` |
| **Permission Set** | `AdministratorAccess` |
| **Terraform State Bucket** | `phase2-tf-state-us-east-1` |
| **DynamoDB Lock Table** | `tf-state-lock` |
| **CLI Profile Name** | `phase2-sso` |

______________________________________________________________________

## 1 · Console 侧一次性设置 (仅首次)

1. **Enable IAM Identity Center** ：AWS Console → *IAM Identity Center* → **Enable**。
1. **新增 User** ：`你常用邮箱` → 自动收到初次登陆邮件。
1. **创建 Permission Set** (建议 "Custom")：
   - 若实验期可直接用 `AdministratorAccess`，后期再细分为 S3 + DynamoDB + EKS 三条最小策略。
1. **Assign User to Account** ：*AWS Accounts → Assign users* → 勾选账号 563149051155 + 所选 Permission Set。

完成后记下「SSO Start URL + Home Region」。

______________________________________________________________________

## 2 · 本地机器第一次设置 (Windows WSL / macOS / Linux)

```bash
# 1. 安装 AWS CLI v2 (WSL 可用 apt, macOS 可用 brew)
aws --version   # >= 2.7

# 2. 交互式配置 SSO Profile
aws configure sso --profile phase2-sso
# >> 输入 SSO Start URL, SSO Region, 选择 Account & Permission Set

# 3. 浏览器弹窗 -> Allow 授权
aws sso login --profile phase2-sso

# 4. (可选) 默认使用该 Profile
export AWS_PROFILE=phase2-sso
```

> **文件落点**
> `~/.aws/config`   → 保存 profile + sso‑session
> `~/.aws/cli/cache` → 存短期凭证 (12 h 过期)，到期重跑 `aws sso login`。

______________________________________________________________________

## 3 · Terraform Backend / Provider 关键片段

```hcl
# backend.tf
terraform {
  backend "s3" {
    bucket      = "phase2-tf-state-us-east-1"
    key         = "eks/lab/terraform.tfstate"
    region      = "us-east-1"
    profile     = "phase2-sso"
    lock_table  = "tf-state-lock"   # Terraform 1.7+ 写法 (旧版 dynamodb_table 亦可)
  }
}

# provider.tf
provider "aws" {
  region  = var.region
  profile = "phase2-sso"
  default_tags = {
    project = "phase2-sprint"
  }
}
```

______________________________________________________________________

## 4 · 日常工作流 (每次开机 / 换终端)

```bash
# Step A: 刷新 SSO 凭证
aws sso login --profile phase2-sso   # 无弹窗则已缓存

# Step B: Terraform 生命周期
cd infra/aws
terraform init    # 首次或换机器时
terraform plan
terraform apply   # 若要变更
```

📝 **提示**

- `aws sso logout` 可以主动失效本机刷新令牌。
- 若使用 `Terragrunt` 同理在 `~/.aws/config` 指定 `profile` 即可。

______________________________________________________________________

## 5 · 迁移到新电脑 / macOS 步骤

1. 安装 **AWS CLI v2** & **Terraform >= 1.3**。
1. 重跑 **Section 2** 的 `aws configure sso` & `aws sso login`。
1. 将 **backend.tf / provider.tf** 原样复制；或只改 `bucket` 名。
1. 重新 `terraform init` 拉插件 & state；确认 S3 + DynamoDB lock 可访问。

> **不需要复制 `~/.aws/credentials`** —— SSO 登录生成的短期凭证自动写入缓存。

______________________________________________________________________

## 6 · Troubleshooting 常见错误

| 错误提示 | 根因 / 解决方案 |
| ---------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------- |
| `NoCredentialProviders` 或 `failed to refresh cached credentials` | 忘记先 `aws sso login` 或 CLI 版本过旧。 |
| `profile ... is configured to use SSO but is missing required configuration` | `backend.tf`/`provider.tf` 忘写 `profile = "phase2-sso"`；或 AWS Provider 旧版本 (`terraform providers lock`). |
| `access denied (s3:PutObject)` | Permission Set 缺 `AmazonS3FullAccess` 或 Bucket 级策略阻塞。 |
| DynamoDB Lock 抛 `AccessDeniedException` | Permission Set 缺 `AmazonDynamoDBFullAccess` 或表名写错。 |
| `dynamodb_table deprecated` Warning | 换成 `lock_table` (Terraform 1.7+) or 继续忽略。 |

______________________________________________________________________

## 7 · 安全最佳实践

- **最小权限**：实验期可先用 `AdministratorAccess`，正式环境拆分为 S3 + DynamoDB + EKS Cluster Admin 三条策略。
- **MFA**：为 Identity Center 用户启用 MFA (Authenticator App)。
- **State 加密**：S3 后端默认 SSE-S3；如需 SSE-KMS 请在 backend 加 `kms_key_id = "alias/tf-state-key"`。
- **锁表计费**：DynamoDB `PAY_PER_REQUEST` 仅万次级别 $0.25/月，可忽略。

______________________________________________________________________

> **记得**：每 12 h 重新 `aws sso login`；Terraform 换机时只需「CLI+SSO+backend.tf」三件套，别带任何长效密钥。

Happy Terraforming! 🚀
