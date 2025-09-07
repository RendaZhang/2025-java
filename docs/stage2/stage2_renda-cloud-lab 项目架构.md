<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [renda-cloud-lab 项目架构](#renda-cloud-lab-%E9%A1%B9%E7%9B%AE%E6%9E%B6%E6%9E%84)
  - [项目架构概览](#%E9%A1%B9%E7%9B%AE%E6%9E%B6%E6%9E%84%E6%A6%82%E8%A7%88)
    - [1. **整体定位**](#1-%E6%95%B4%E4%BD%93%E5%AE%9A%E4%BD%8D)
    - [2. **基础设施层（Infra）**](#2-%E5%9F%BA%E7%A1%80%E8%AE%BE%E6%96%BD%E5%B1%82infra)
    - [3. **部署层（Kubernetes）**](#3-%E9%83%A8%E7%BD%B2%E5%B1%82kubernetes)
    - [4. **可观测性**](#4-%E5%8F%AF%E8%A7%82%E6%B5%8B%E6%80%A7)
    - [5. **自动化脚本与流程**](#5-%E8%87%AA%E5%8A%A8%E5%8C%96%E8%84%9A%E6%9C%AC%E4%B8%8E%E6%B5%81%E7%A8%8B)
    - [6. **示例应用与容器**](#6-%E7%A4%BA%E4%BE%8B%E5%BA%94%E7%94%A8%E4%B8%8E%E5%AE%B9%E5%99%A8)
    - [7. **文档与图表**](#7-%E6%96%87%E6%A1%A3%E4%B8%8E%E5%9B%BE%E8%A1%A8)
    - [总结](#%E6%80%BB%E7%BB%93)
  - [Terraform 管理的基础设施概览](#terraform-%E7%AE%A1%E7%90%86%E7%9A%84%E5%9F%BA%E7%A1%80%E8%AE%BE%E6%96%BD%E6%A6%82%E8%A7%88)
    - [1. **远程状态与 Provider**](#1-%E8%BF%9C%E7%A8%8B%E7%8A%B6%E6%80%81%E4%B8%8E-provider)
    - [2. **网络基座（`modules/network_base`）**](#2-%E7%BD%91%E7%BB%9C%E5%9F%BA%E5%BA%A7modulesnetwork_base)
    - [3. **NAT 网关模块（`modules/nat`）**](#3-nat-%E7%BD%91%E5%85%B3%E6%A8%A1%E5%9D%97modulesnat)
    - [4. **EKS 集群模块（`modules/eks`）**](#4-eks-%E9%9B%86%E7%BE%A4%E6%A8%A1%E5%9D%97moduleseks)
    - [5. **IRSA 模块族**](#5-irsa-%E6%A8%A1%E5%9D%97%E6%97%8F)
    - [6. **预算控制（`budgets.tf`）**](#6-%E9%A2%84%E7%AE%97%E6%8E%A7%E5%88%B6budgetstf)
    - [7. **变量与输出**](#7-%E5%8F%98%E9%87%8F%E4%B8%8E%E8%BE%93%E5%87%BA)
    - [总结](#%E6%80%BB%E7%BB%93-1)
  - [项目部署层综述](#%E9%A1%B9%E7%9B%AE%E9%83%A8%E7%BD%B2%E5%B1%82%E7%BB%BC%E8%BF%B0)
    - [**1. Kubernetes 清单（`deploy/k8s`）**](#1-kubernetes-%E6%B8%85%E5%8D%95deployk8s)
    - [**2. Helm 自定义值（`deploy/helm-values`）**](#2-helm-%E8%87%AA%E5%AE%9A%E4%B9%89%E5%80%BCdeployhelm-values)
    - [**3. 部署与运维脚本（`scripts/`）**](#3-%E9%83%A8%E7%BD%B2%E4%B8%8E%E8%BF%90%E7%BB%B4%E8%84%9A%E6%9C%ACscripts)
    - [**4. Makefile 统一调度**](#4-makefile-%E7%BB%9F%E4%B8%80%E8%B0%83%E5%BA%A6)
    - [总结](#%E6%80%BB%E7%BB%93-2)
  - [项目中最小权限相关的 IRSA / ServiceAccount 设计](#%E9%A1%B9%E7%9B%AE%E4%B8%AD%E6%9C%80%E5%B0%8F%E6%9D%83%E9%99%90%E7%9B%B8%E5%85%B3%E7%9A%84-irsa--serviceaccount-%E8%AE%BE%E8%AE%A1)
    - [**架构分层定位**](#%E6%9E%B6%E6%9E%84%E5%88%86%E5%B1%82%E5%AE%9A%E4%BD%8D)
    - [**基础设施层（Terraform）**](#%E5%9F%BA%E7%A1%80%E8%AE%BE%E6%96%BD%E5%B1%82terraform)
    - [**部署层（Kubernetes / Helm / 脚本）**](#%E9%83%A8%E7%BD%B2%E5%B1%82kubernetes--helm--%E8%84%9A%E6%9C%AC)
    - [**所处架构层次**](#%E6%89%80%E5%A4%84%E6%9E%B6%E6%9E%84%E5%B1%82%E6%AC%A1)
  - [成本控制策略概览](#%E6%88%90%E6%9C%AC%E6%8E%A7%E5%88%B6%E7%AD%96%E7%95%A5%E6%A6%82%E8%A7%88)
    - [**1. 生命周期管理：按需启停与自动清理**](#1-%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F%E7%AE%A1%E7%90%86%E6%8C%89%E9%9C%80%E5%90%AF%E5%81%9C%E4%B8%8E%E8%87%AA%E5%8A%A8%E6%B8%85%E7%90%86)
    - [**2. AWS Budgets 与成本预警**](#2-aws-budgets-%E4%B8%8E%E6%88%90%E6%9C%AC%E9%A2%84%E8%AD%A6)
    - [**3. Spot 与自动伸缩**](#3-spot-%E4%B8%8E%E8%87%AA%E5%8A%A8%E4%BC%B8%E7%BC%A9)
    - [**4. 存储与网络优化**](#4-%E5%AD%98%E5%82%A8%E4%B8%8E%E7%BD%91%E7%BB%9C%E4%BC%98%E5%8C%96)
    - [**5. 其他成本护栏**](#5-%E5%85%B6%E4%BB%96%E6%88%90%E6%9C%AC%E6%8A%A4%E6%A0%8F)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# renda-cloud-lab 项目架构

## 项目架构概览

### 1. **整体定位**

- **renda-cloud-lab** 是一个以“按日销毁→次日重建”为策略的云原生实验室。
- **目标**：用最小成本在 AWS 上实践 EKS、Terraform、Helm、可观测性与混沌工程等技术。

### 2. **基础设施层（Infra）**

- **Terraform 模块化管理**：
  - `network_base`：建立 VPC、公私子网、S3 Gateway Endpoint。
  - `nat` 模块：按需创建 NAT 网关。
  - `eks` 模块：负责 EKS 集群、NodeGroup、IAM/OIDC、日志与升级策略。
- **IRSA 子模块**：
  - 将 Kubernetes ServiceAccount 与特定 IAM 角色绑定，实现最小权限访问（如 ALB Controller、ADOT Collector、Grafana、应用 S3 权限等）。
- **参数控制**：
  - 通过 `create_nat`、`create_eks` 等参数控制是否构建对应资源，确保每日启动/销毁时 Terraform 状态保持整洁。

### 3. **部署层（Kubernetes）**

- **系统组件**：
  - `post-recreate.sh`：在集群就绪后通过 Helm 安装/升级 AWS Load Balancer Controller、Cluster Autoscaler、metrics-server、ADOT Collector、Grafana，并可选启用 Chaos Mesh。
- **业务示例**：
  - `deploy/k8s/app/`：部署 Spring Boot 示例服务 *task-api*（包含 ConfigMap、ServiceAccount、Deployment、Service、PDB），暴露 `/api/hello` 与 `/actuator` 端点。
  - `ingress.yaml`：通过 ALB Ingress 提供公网访问。
  - `hpa.yaml`：设定 CPU 60% 触发的 2-10 副本的 HPA。
  - `jobs/smoke/`：用临时 Job 在集群内做端到端冒烟测试。
- **混沌实验**：
  - `experiments/chaos/`：包含对 PodKill、网络延迟的 Chaos Mesh 定义，用于验证系统韧性。

### 4. **可观测性**

- **ADOT Collector**：
  - 使用 IRSA 将指标 `remote_write` 至 Amazon Managed Prometheus（AMP）。
- **Grafana**：
  - 通过另一 IRSA 角色直接从 AMP 拉取数据。
- **Spring Boot Actuator**：
  - 暴露指标并配合 Prometheus 注解，HPA 与自动扩缩容可据此获取指标。

### 5. **自动化脚本与流程**
- **Makefile 统一入口**：
  - `make start-all`：创建 NAT+EKS 并执行后续安装。
  - `make stop-all`：先卸载组件再销毁控制面与 NAT。
  - `preflight.sh`：检查 AWS 配额与 CLI 工具。
  - `lifecycle/`：实现 pre/post teardown、缩容节点组、处理 Spot 中断通知等。
- **日志管理**：
  - 所有脚本输出被写入 `scripts/logs/`，保持可追溯。

### 6. **示例应用与容器**

- **task-api**：
  - 子目录提供 Spring Boot 源码与 Dockerfile，构建好的镜像 Digest 固定在部署清单中。
  - **Actuator 配置**：启用 Liveness/Readiness 探针、Prometheus 端点、直方图统计等。

### 7. **文档与图表**

- **文档**：
  - `docs/`：包含前置条件、运行手册、问题排查、环境重建指南等。
- **图表**：
  - `diagrams/`：用 Graphviz 展示 Terraform 生成的架构图，辅助理解各模块关系。

### 总结

该仓库展示了一个面向实践的云原生实验环境设计：

1. 解耦 Infra 与 Deploy：Terraform 专注网络与 EKS 基础，部署层完全由 Helm + 脚本负责，避免状态污染。
2. 成本优化：Spot 节点、可选 NAT、每日自动销毁策略大幅降低长期运行成本。
3. 可观测性与韧性：通过 ADOT + AMP + Grafana 打通监控链路，结合 Chaos Mesh 与 HPA 验证弹性与稳定性。
4. 安全与合规：广泛运用 IRSA 精细化权限，S3 Gateway Endpoint 降低公网依赖，日志与清理脚本确保资源一致性。
5. 可扩展性：清晰的模块与脚本结构使新增 AWS 服务、Helm Chart 或 GitOps 流程变得容易；AGENTS.md 为后续 AI/协作提供操作准则。

整体而言，renda-cloud-lab 是一个面向 AWS/EKS 的实验平台，集成了基础设施即代码、应用部署、可观测性、自动扩缩容与混沌工程等关键能力，非常适合用于实践云原生理念、验证设计模式或作为后续多云/AI sidecar 实验的基础。

---

## Terraform 管理的基础设施概览

### 1. **远程状态与 Provider**

- **远程状态管理**：
  - 使用 **S3 + DynamoDB** 作为 Terraform 远程状态及锁机制，确保状态的一致性与并发安全。
- **Provider 配置**：
  - 通过 `provider.tf` 与 `provider_billing.tf` 定义两个 AWS Provider：
    - **主 Provider**：用于资源创建。
    - **别名 Billing Provider**：专用于 AWS Budgets。
  - **标签管理**：所有资源统一打上 `project = phase2-sprint` 标签。

### 2. **网络基座（`modules/network_base`）**

- **VPC 创建**：
  - 创建一个 `10.0.0.0/16` 的 VPC。
- **子网划分**：
  - 划分两组公有子网与两组私有子网，并为每组私有子网配置独立路由表。
- **路由表配置**：
  - 公有子网关联的路由表中默认路由指向 **Internet Gateway**。
  - 私有子网路由表默认不出公网。
- **EKS 标签**：
  - 公有与私有子网均带有符合 EKS 要求的 Tag（如 `kubernetes.io/role/elb`），可被后续 EKS 自动识别。
- **S3 Gateway Endpoint**：
  - 为私有路由表配置 S3 Gateway Endpoint，使私有子网访问 S3 时无需经过 NAT 网关。

### 3. **NAT 网关模块（`modules/nat`）**

- **功能**：
  - 可选创建单个 NAT 网关，位于指定公有子网，并将若干私有路由表的 `0.0.0.0/0` 路由指向该 NAT。
- **成本控制**：
  - 用 `create` 变量控制是否启用，便于节省日常实验环境成本。

### 4. **EKS 集群模块（`modules/eks`）**

- **集群创建**：
  - 创建启用 OIDC 的 EKS 集群，默认开启 API 与 authenticator 日志。
- **IAM 角色与 OIDC**：
  - 定义集群 IAM 角色并附加必要托管策略；同时创建 OIDC Provider 以支持 IRSA。
- **安全组策略**：
  - 节点与控制平面互通（`443, 0-65535`）。
  - 节点之间全量互通。
  - 可选 SSH 访问与 NodePort 范围放通。
- **节点组配置**：
  - 使用 `aws_launch_template` + `aws_eks_node_group` 构建托管节点组：
    - 支持 **ON_DEMAND** 或 **SPOT**。
    - `scaling_config` 设定初始为 `desired=1`，并通过生命周期配置忽略 `desired_size` 以便 Cluster Autoscaler 调整。
- **节点 IAM 角色**：
  - 附加标准策略（`AmazonEKSWorkerNodePolicy`、`AmazonEKS_CNI_Policy`、`ECR ReadOnly`）。

### 5. **IRSA 模块族**

- **Cluster Autoscaler（`modules/irsa`）**：
  - 最小权限策略，允许运维控制 AutoScaling 组。
- **AWS Load Balancer Controller（`modules/irsa_albc`）**：
  - 引用官方策略 `policy.json`，允许创建/修改 ALB、Security Group 等。
- **ADOT Collector (AMP remote_write)（`modules/irsa_adot_amp`）**：
  - 附加 `AmazonPrometheusRemoteWriteAccess` 托管策略。
- **Grafana (AMP query)（`modules/irsa_grafana_amp`）**：
  - 附加 `AmazonPrometheusQueryAccess` 托管策略。
- **应用级 IRSA + S3（`modules/app_irsa_s3`）**：
  - 自动创建带版本控制、SSE、生命周期规则的 S3 Bucket，并在需要时创建相应 IRSA。
  - 桶策略强制 HTTPS 访问、可选限制 VPC 来源，生命周期规则清理 `smoke` 前缀下的对象。

### 6. **预算控制（`budgets.tf`）**

- **功能**：
  - 若 `create_budget = true`，通过 `aws.billing` provider 创建月度成本预算。
- **提醒机制**：
  - 超出预算的指定阈值（默认 80%）时向配置邮箱发送提醒。

### 7. **变量与输出**

- **变量配置**：
  - `variables.tf` 提供大量可配置项（如集群名称、节点组类型、实例规格、IRSA 角色名、预算参数等）。
  - 配合 `terraform.tfvars` 给出默认实验环境配置（如 `us-east-1`，集群名 `dev`）。
- **输出汇总**：
  - `outputs.tf` 汇总关键资源 ID/ARN（如 VPC、子网、IRSA 角色、S3 桶信息等），方便后续脚本或手动查验。

### 总结

整个 Terraform 架构分为以下层次结构：

**网络基座 → NAT → EKS → 多个 IRSA 模块 → 应用 S3 + IRSA → 成本预算**。

- **模块化设计**：
  - 通过模块化设计与丰富的可配置变量，既可快速部署完整的实验环境，也便于按需裁剪（例如关闭 EKS 或 NAT 以节省费用）。
- **IRSA 机制**：
  - IRSA 机制贯穿始终，为各类控制器及应用提供最小权限的 AWS 访问能力。
  - 结合 S3 策略与生命周期规则，构建基础安全与成本护栏。
- **适用场景**：
  - 该 Terraform 架构适合日常重建/销毁的开发实验场景。
  - 通过远程状态与预算提醒，在多人协作与成本控制方面提供保障。

---

## 项目部署层综述

本仓库的部署层围绕 **Kubernetes 清单**、**Helm Chart 自定义值** 和一套 **生命周期脚本** 构建，配合 **Makefile** 统一调度，实现从基础设施创建、应用部署到销毁回收的自动化流程。

### **1. Kubernetes 清单（`deploy/k8s`）**

- **应用基线**：
  - `deploy/k8s/app/` 定义了命名空间与 ServiceAccount、配置、部署与服务以及 PodDisruptionBudget。
    - **命名空间**：`svc-task` 与 `task-api` ServiceAccount。
    - **ConfigMap**：提供应用环境变量。
    - **Deployment + Service**：固定 ECR 镜像、探针、资源配额等。
    - **PDB**：保证最少 1 个可用副本，与 HPA 配合避免流量中断。
- **自动扩缩容与入口**：
  - **HPA**：设定 CPU 60% 目标，副本范围 2–10，包含自定义 behavior。
  - **Ingress**：通过 AWS Load Balancer Controller 创建公网 ALB，并显式设置健康检查、转发端口等参数。
- **运维与测试**：
  - **冒烟 Job**：`task-api-smoke` 通过 Curl 校验 API 和健康端点。
  - **权限验证 Job**：`awscli-smoke` 使用 IRSA 访问 S3 并做正/反向测试。
  - **Chaos Mesh 实验样例**：随机 Pod 杀死和网络延迟注入。

### **2. Helm 自定义值（`deploy/helm-values`）**

- **ADOT Collector**：
  - 部署为 Deployment，ServiceAccount 带 IRSA 注解，配置 SigV4 认证并将指标写入 Amazon Managed Prometheus。
- **Grafana**：
  - 安装 AMP 数据源插件并通过 IRSA 查询 AMP；提供预置仪表盘。
- **Chaos Mesh**：
  - 仅部署核心控制器与 daemon，使用 containerd runtime 并放开污点约束。

### **3. 部署与运维脚本（`scripts/`）**

- **`post-recreate.sh`**：
  - 在 Terraform 启动基础设施后运行，自动：
    - 更新 kubeconfig、等待 API 就绪。
    - 安装/升级 ALB Controller、Cluster Autoscaler、metrics-server、ADOT Collector、Grafana；可选安装 Chaos Mesh。
    - 部署 `task-api`、Ingress、HPA，并执行冒烟测试和 Spot 通知绑定。
- **`pre-teardown.sh`**：
  - 销毁前删除 ALB Ingress、卸载控制器与可选组件，并等待 ALB 回收。
- **`post-teardown.sh`**：
  - 在 IaC 销毁后兜底清理残留日志组、ALB/TargetGroup/安全组，验证 NAT/EKS/ASG 通知是否彻底移除。
- **`scale-nodegroup-zero.sh`**：
  - 单独缩容所有 NodeGroup 到零，便于停机节省费用。
- **其他脚本**：
  - **`check-tools.sh`**：检查并自动安装 CLI 依赖。
  - **`preflight.sh`**：读取关键 AWS Service Quota。
  - **`tf-import.sh`**：协助将现有集群导入 Terraform 状态。

### **4. Makefile 统一调度**

**Makefile** 封装 Terraform 与脚本调用，提供以下命令，实现一键化生命周期管理：
- **`start`**：启动基础设施。
- **`post-recreate`**：执行 `post-recreate.sh`。
- **`stop`**：停止基础设施。
- **`pre-teardown`**：执行 `pre-teardown.sh`。
- **`post-teardown`**：执行 `post-teardown.sh`。
- **`start-all`**：启动基础设施并执行后续部署。
- **`stop-all`**：停止基础设施并清理资源。

### 总结

本部署层通过 **Kubernetes 清单**、**Helm Chart 自定义值** 和 **生命周期脚本** 的紧密配合，结合 **Makefile** 的统一调度，实现了从基础设施创建、应用部署到销毁回收的全流程自动化。这种设计不仅提高了部署效率，还确保了环境的一致性与可维护性，特别适合需要频繁重建与销毁的实验场景。

---

## 项目中最小权限相关的 IRSA / ServiceAccount 设计

### **架构分层定位**

项目明确区分 **基础设施层 (Infra)** 与 **部署层 (Deploy)**：
- **Terraform**：仅负责 VPC、EKS、IAM/IRSA 等基础设施的声明式管理。
- **Helm 与脚本**：部署 Kubernetes 组件并注入所需 ServiceAccount/IRSA 注解，避免与 Terraform 状态耦合。

### **基础设施层（Terraform）**

| 模块               | 绑定的 ServiceAccount                | 最小权限 / IAM Policy                                                                 | 作用                                                                 |
|--------------------|--------------------------------------|--------------------------------------------------------------------------------------|--------------------------------------------------------------------|
| `irsa`             | `cluster-autoscaler` (kube-system)  | 自定义策略，仅允许 Auto Scaling、EC2、EKS 描述与伸缩操作                              | Cluster Autoscaler 调整节点组容量                                   |
| `irsa_albc`        | `aws-load-balancer-controller` (kube-system) | 官方推荐 `policy.json`，创建/管理 ALB 所需权限                                        | AWS Load Balancer Controller                                       |
| `irsa_adot_amp`    | `adot-collector` (observability)     | 托管策略 `AmazonPrometheusRemoteWriteAccess`，仅 `remote_write`                      | ADOT Collector 向 AMP 写指标                                       |
| `irsa_grafana_amp` | `grafana` (observability)            | 托管策略 `AmazonPrometheusQueryAccess`，只读查询 AMP                                   | Grafana 查询 AMP                                                   |
| `app_irsa_s3`      | `task-api` (svc-task)                | 自建策略：`ListBucket/GetObject/PutObject` 仅限特定前缀，桶策略强制 TLS 且可选限制 VPC | 业务 Pod 访问专属 S3 前缀                                          |

以上模块均在 `infra/aws/main.tf` 中按需实例化，与网络、EKS 集群等资源一起构成基础设施层。

### **部署层（Kubernetes / Helm / 脚本）**

| 组件                          | ServiceAccount 定义与注解                                                                 | IRSA 角色                     |
|-------------------------------|------------------------------------------------------------------------------------------|------------------------------|
| `task-api`                    | `deploy/k8s/app/ns-sa.yaml` 定义 SA；注解由脚本动态写入                                     | `dev-task-api-irsa`（最小 S3 权限） |
| `aws-load-balancer-controller` | 脚本自动创建并注解 SA                                                                      | `aws-load-balancer-controller` |
| `adot-collector`               | Helm values 显式注解 `eks.amazonaws.com/role-arn`                                           | `adot-collector`              |
| `grafana`                      | Helm values 显式注解 `eks.amazonaws.com/role-arn`                                           | `grafana-amp-query`           |
| **校验脚本**                   | `post-recreate.sh` 在部署后检查各 SA 的 IRSA 注解和 WebIdentity Token                       | 确保最小权限配置生效             |
| **冒烟测试**                   | `awscli-smoke` Job 使用 `task-api` SA，验证仅能访问允许的 S3 前缀                            | 验证 S3 最小权限                |

### **所处架构层次**

- **Infra 层**：
  - 所有 IRSA IAM 角色与策略由 Terraform 定义并与 EKS OIDC Provider 关联，确保基础设施级别的最小权限控制。
- **Deploy 层**：
  - Helm/脚本在集群内创建 ServiceAccount 并打上 IRSA 注解，使工作负载在运行时以对应最小权限的 IAM Role 访问 AWS 资源。

---

## 成本控制策略概览

该项目围绕 **“按需启停、自动清理、预算预警”** 三大方向设计成本控制机制，结合 AWS 原生能力与脚本自动化，实现对 EKS 及相关云资源的精细化成本治理。

### **1. 生命周期管理：按需启停与自动清理**

- **每日销毁 → 次日重建**：
  - 核心策略是在每个工作日开始前重建实验环境，结束后销毁计费资源，实现“闲置关停、忙碌重启”。
- **Makefile 统一调度**：
  - 提供 `start-all`、`stop-all` 等命令，自动缩容节点组并删除 NAT 网关、EKS 控制面等高成本组件。
- **缩容脚本**：
  - `scale-nodegroup-zero.sh` 将所有节点组 `desiredSize` 设为 0，以避免闲置节点费用。
- **清理脚本**：
  - **`pre-teardown.sh`**：删除 ALB 类型 Ingress 并卸载控制器，触发云侧负载均衡器回收。
  - **`post-teardown.sh`**：在 IaC 销毁后兜底清理可能继续计费的 CloudWatch 日志、ALB/TargetGroup、安全组等，并验证 NAT 和集群是否已删除。
- **运行手册指引**：
  - `make stop-all` 是日常关闭资源的标准步骤。

### **2. AWS Budgets 与成本预警**

- **独立计费 Provider**：
  - 通过 `provider_billing.tf` 配置 `aws.billing` alias 访问 Billing API。
- **Terraform 管理预算**：
  - `budgets.tf` 创建月度成本预算并设置阈值、邮件提醒。
- **变量化阈值**：
  - `variables.tf` 提供预算开关、上限、告警邮箱与百分比阈值，便于按需调整。

### **3. Spot 与自动伸缩**

- **节点组容量类型可选 ON_DEMAND/SPOT**：
  - 支持以 Spot 实例降低节点成本。
- **项目文档强调 Spot + IRSA 的成本与权限优势**。
- **预检脚本**：
  - `preflight.sh` 统计运行中的 Spot vCPU，帮助评估配额与潜在费用。

### **4. 存储与网络优化**

- **S3 生命周期**：
  - 对应用桶的 `smoke` 前缀设置 7 天过期策略，自动清理临时对象以节约存储成本。
- **网关端点与预算机制**：
  - 文档提出利用 S3 Gateway Endpoint 配合 AWS Budgets 进一步优化带宽与成本。

### **5. 其他成本护栏**

- **文档化操作**：
  - 多份指南强调完成关停流程后只保留低成本基础资源，并提示 Route 53、AMP 等服务的持续费用需自行评估。
- **运行手册**：
  - 提供自动化脚本和手动步骤的组合，确保在快速迭代与成本控制之间取得平衡。
