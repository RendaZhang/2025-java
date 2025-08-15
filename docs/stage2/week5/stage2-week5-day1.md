<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Stage 2 Week 5 Day 1 - 应用骨架 + Docker 镜像 + 推送 ECR（最小可运行）](#stage-2-week-5-day-1---%E5%BA%94%E7%94%A8%E9%AA%A8%E6%9E%B6--docker-%E9%95%9C%E5%83%8F--%E6%8E%A8%E9%80%81-ecr%E6%9C%80%E5%B0%8F%E5%8F%AF%E8%BF%90%E8%A1%8C)
  - [前置检查 & 环境就绪](#%E5%89%8D%E7%BD%AE%E6%A3%80%E6%9F%A5--%E7%8E%AF%E5%A2%83%E5%B0%B1%E7%BB%AA)
    - [本机工具自检](#%E6%9C%AC%E6%9C%BA%E5%B7%A5%E5%85%B7%E8%87%AA%E6%A3%80)
    - [设置本周通用变量](#%E8%AE%BE%E7%BD%AE%E6%9C%AC%E5%91%A8%E9%80%9A%E7%94%A8%E5%8F%98%E9%87%8F)
    - [校验 AWS 身份 & EKS 连通](#%E6%A0%A1%E9%AA%8C-aws-%E8%BA%AB%E4%BB%BD--eks-%E8%BF%9E%E9%80%9A)
    - [预检 ECR 登录（为稍后推送做准备）](#%E9%A2%84%E6%A3%80-ecr-%E7%99%BB%E5%BD%95%E4%B8%BA%E7%A8%8D%E5%90%8E%E6%8E%A8%E9%80%81%E5%81%9A%E5%87%86%E5%A4%87)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Stage 2 Week 5 Day 1 - 应用骨架 + Docker 镜像 + 推送 ECR（最小可运行）

> **目标**
> 1. 以 Spring Initializr 生成 `apps/task-api`：Web、Actuator。
> 2. 提供 2 个端点：`GET /healthz`（返回 `"ok"`）与 `GET /api/tasks`（返回内存列表）。
> 3. `Dockerfile`（基于 `eclipse-temurin:21-jre`），本地构建，推到 ECR。

---

## 前置检查 & 环境就绪

### 本机工具自检

在终端依次执行，有输出即可：

```bash
(venv) renda@RendaZhangComputer:/mnt/d/0Repositories/2025Java$ java -version
openjdk version "21.0.8" 2025-07-15
OpenJDK Runtime Environment (build 21.0.8+9-Ubuntu-0ubuntu124.04.1)
OpenJDK 64-Bit Server VM (build 21.0.8+9-Ubuntu-0ubuntu124.04.1, mixed mode, sharing)
(venv) renda@RendaZhangComputer:/mnt/d/0Repositories/2025Java$ mvn -v
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: /mnt/d/Java/apache-maven-3.9.9
Java version: 21.0.8, vendor: Ubuntu, runtime: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "5.15.167.4-microsoft-standard-wsl2", arch: "amd64", family: "unix"
(venv) renda@RendaZhangComputer:/mnt/d/0Repositories/2025Java$ docker version --format '{{.Server.Version}}'
28.3.3
(venv) renda@RendaZhangComputer:/mnt/d/0Repositories/2025Java$ aws --version
aws-cli/2.27.41 Python/3.13.4 Linux/5.15.167.4-microsoft-standard-WSL2 exe/x86_64.ubuntu.24
(venv) renda@RendaZhangComputer:/mnt/d/0Repositories/2025Java$ kubectl version --client --output=yaml | sed -n '1,10p'
clientVersion:
  buildDate: "2025-05-15T08:27:33Z"
  compiler: gc
  gitCommit: 8adc0f041b8e7ad1d30e29cc59c6ae7a15e19828
  gitTreeState: clean
  gitVersion: v1.33.1
  goVersion: go1.24.2
  major: "1"
  minor: "33"
  platform: linux/amd64
```

### 设置本周通用变量

```bash
export AWS_REGION=us-east-1
export CLUSTER=dev
export PROFILE=phase2-sso
export ECR_REPO=task-manager
export NS=svc-task
export APP=task-api
```

### 校验 AWS 身份 & EKS 连通

```bash
# AWS SSO 登录
aws sso login --profile "$PROFILE"

# 身份应返回 Account / Arn
aws sts get-caller-identity --profile "$PROFILE"
{
    "UserId": "AROAYGHSMSUJ476XGJ3BO:RendaZhang",
    "Account": "563149051155",
    "Arn": "arn:aws:sts::563149051155:assumed-role/AWSReservedSSO_AdministratorAccess_bb673747cef3fdeb/RendaZhang"
}

# 查看是否已有集群（没有也没关系，继续下一步）
aws eks list-clusters --region "$AWS_REGION" --profile "$PROFILE" | grep -E "\"$CLUSTER\"" || echo "EKS not found (ok for now)"
```

### 预检 ECR 登录（为稍后推送做准备）

```bash
ACCOUNT_ID=$(aws sts get-caller-identity --profile "$PROFILE" --query Account --output text)
aws ecr get-login-password --region "$AWS_REGION" --profile "$PROFILE" \
| docker login --username AWS --password-stdin "$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
# 显示 Login Succeeded
```
