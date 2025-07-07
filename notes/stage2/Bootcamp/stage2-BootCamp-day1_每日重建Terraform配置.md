# Nightly Destroy → Morning Create: AWS Lab Cost-Saving Playbook

> **目的**
>
> * 为 Phase 2 云原生冲刺提供一套“每天关大件、省钱不掉链”的落地步骤
> * 记录四阶段实施过程、命令脚本、常见故障，供日后迁移 / 回顾

---

## 0 · 关键信息（你的环境）

| 名称                  | 当前值                                                                                                      |
| ------------------- | -------------------------------------------------------------------------------------------------------- |
| **子域(Hosted Zone)** | `lab.rendazhang.com`                                                                                     |
| **Route 53 NS**     | `ns-1737.awsdns-25.co.uk`<br>`ns-221.awsdns-27.com`<br>`ns-1086.awsdns-07.org`<br>`ns-985.awsdns-59.net` |
| **AWS Region**      | `us-east-1`                                                                                              |
| **Profile**         | `phase2-sso`                                                                                             |
| **State Bucket**    | `phase2-tf-state-us-east-1`                                                                              |
| **DynamoDB Lock**   | `tf-state-lock`                                                                                          |

---

## 1 · 背景与目标

* EKS 控制面 $0.10 / h、NAT GW $0.045 / h、ALB $0.031 / h，若 24×7 跑满约 $260/mo。
* 通过 **晚间关 NAT+ALB+EKS**、清空 Spot 节点，把日费压到 $0.01（只剩存储）。
* 仍需保持**固定入口**，便于 CI/CD、演示、监控连贯。

---

## 2 · 四阶段实施总览

| Part              | 完成日期 | 关键产出                                                              | 难点 & 解决                                              | 目录 / 文件                                                       |
| ----------------- | ---- | ----------------------------------------------------------------- | ---------------------------------------------------- | ------------------------------------------------------------- |
| **1 模块骨架**        | 6-25 | `network_base` / `nat` / `alb` 三模块骨架；根 `variables.tf` & `main.tf` | Terraform validate 通过                                | `infra/aws/modules/*`                                         |
| **2 资源填充**        | 6-25 | VPC∙Subnets∙RTB∙IGW + NAT GW + ALB/TG + 输出依赖                      | - SG Name 不能 `sg-*`<br>- ALB/TG 名称冲突→改 `name_prefix` | 同上                                                            |
| **3 Makefile 脚本** | 6-25 | `make start` / `stop` / `stop-hard`（SSO 自动登陆）                     | State lock 清理；凭证过期提醒                                 | `Makefile`                                                    |
| **4 固定域名**        | 6-25 | Route 53 公共 hosted-zone + ALIAS 记录自动指向 ALB                        | 子域 NS 委派到 AWS；ALIAS TTL 60 s                         | `modules/network_base/data+outputs.tf`<br>`infra/aws/main.tf` |

---

## 3 · 目录结构

```text
infra/aws/
├─ backend.tf        # S3 + DynamoDB backend (us-east-1)
├─ provider.tf       # AWS provider (profile = phase2-sso)
├─ variables.tf      # region + create_* 开关
├─ main.tf           # 调用 3 模块 + Route53 ALIAS
├─ modules/
│  ├─ network_base/  # VPC / Subnets / IGW / RTB / SG
│  ├─ nat/           # NAT GW + EIP + 私网默认路由
│  └─ alb/           # ALB / Listener / TG (IP)
└─ Makefile          # start / stop / stop-hard
```

---

## 4 · 核心变量

| 变量           | 默认                  | 含义                         |
| ------------ | ------------------- | -------------------------- |
| `create_nat` | `true`              | 控制 NAT Gateway             |
| `create_alb` | `true`              | 控制 ALB + Target Group      |
| `create_eks` | `false` *(Day 2开启)* | 控制 EKS Cluster + NodeGroup |

---

## 5 · 每日操作流程

```bash
## 晚上关机
make stop          # 关 NAT + ALB
make stop-hard     # 关 NAT + ALB + EKS

## 早上开机
aws sso login --profile phase2-sso
make start         # 开 NAT + ALB (+EKS)

## 快速验证
nslookup lab.rendazhang.com
curl -I http://lab.rendazhang.com
```

> **典型用时**
>
> * make stop：30 s – 2 min
> * make start（无 EKS）：≈ 2 min
> * make start + EKS：≈ 15 min

---

## 6 · 固定域名原理

1. 阿里云根域 `rendazhang.com` → **NS (lab) → Route 53 four NS**
2. Route 53 公共 hosted-zone `lab.rendazhang.com`
3. Terraform 生成 `A – ALIAS` → `${module.alb.alb_dns}`
4. 每次重建 ALB 时，ALIAS 自动更新，TTL 60 s 内全网生效。

---

## 7 · 成本估算 & 预算

| 资源                  | 开机        | 关机   | 备注            |
| ------------------- | --------- | ---- | ------------- |
| EKS Control Plane   | $2.4/d   | 0    | stop-hard 时删除 |
| NAT Gateway         | $1.08/d  | 0    | 每晚销毁          |
| ALB                 | $0.74/d  | 0    | 每晚销毁          |
| Hosted-zone         | $0.50/mo | same | 长留            |
| S3 State + DynamoDB | ¢级        | same | 长留            |

**AWS Budget 建议**

* 5 USD (月) 警戒（Free Tier）
* 80 USD (月) Soft Cap：超额发邮件 + SNS

---

## 8 · 常见故障速查

| 现象                       | 可能原因         | 修复                            |
| ------------------------ | ------------ | ----------------------------- |
| `NXDOMAIN`               | 阿里云未正确 NS 委派 | 核对四条 NS、TTL                   |
| `nat eip quota exceeded` | 残留未释放        | Console 手动 release EIP / 申请配额 |
| `state lock` 卡住          | 上次 apply 中断  | DynamoDB delete-item 清锁       |
| `aws sso login` 频繁弹窗     | 令牌过期 / 未缓存   | `--no-browser` 或设备信任          |

---

## 9 · 附录（Makefile & backend 片段）

<details><summary>点击展开</summary>

```make
AWS_PROFILE = phase2-sso
TF_DIR      = infra/aws
REGION      = us-east-1

start:
	aws sso login --profile $(AWS_PROFILE)
	terraform -chdir=$(TF_DIR) apply -auto-approve \
	  -var="region=$(REGION)" -var="create_nat=true" -var="create_alb=true" -var="create_eks=false"

stop:
	terraform -chdir=$(TF_DIR) apply -auto-approve \
	  -var="region=$(REGION)" -var="create_nat=false" -var="create_alb=false" -var="create_eks=false"

stop-hard: stop
	eksctl delete cluster --name dev --region $(REGION) || true
```

```hcl
terraform {
  backend "s3" {
    bucket      = "phase2-tf-state-us-east-1"
    key         = "eks/lab/terraform.tfstate"
    region      = "us-east-1"
    profile     = "phase2-sso"
    lock_table  = "tf-state-lock"
  }
}
```

</details>

---

> **后续**
>
> * Day 2：把 `create_eks=true`，导入/创建集群；Makefile 中添加 NodeGroup scale 命令
> * Week 5-8：蓝绿发布、Chaos Mesh、Bedrock Sidecar 均可沿用此成本模式
> * 若迁移到 macOS 仅需重新 `aws configure sso` & `terraform init`

Happy Saving & Building! 🚀
