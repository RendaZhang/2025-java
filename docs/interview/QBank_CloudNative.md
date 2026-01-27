<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [云原生问题库](#%E4%BA%91%E5%8E%9F%E7%94%9F%E9%97%AE%E9%A2%98%E5%BA%93)
  - [AWS 的 CloudFormation 与 Terraform 有何异同？](#aws-%E7%9A%84-cloudformation-%E4%B8%8E-terraform-%E6%9C%89%E4%BD%95%E5%BC%82%E5%90%8C)
    - [中文版（面试回答）](#%E4%B8%AD%E6%96%87%E7%89%88%E9%9D%A2%E8%AF%95%E5%9B%9E%E7%AD%94)
    - [English Version (Interview-ready)](#english-version-interview-ready)
  - [请比较 AWS、Azure、和 GCP 的主要区别和优势？](#%E8%AF%B7%E6%AF%94%E8%BE%83-awsazure%E5%92%8C-gcp-%E7%9A%84%E4%B8%BB%E8%A6%81%E5%8C%BA%E5%88%AB%E5%92%8C%E4%BC%98%E5%8A%BF)
    - [30 秒总览（先给结论）](#30-%E7%A7%92%E6%80%BB%E8%A7%88%E5%85%88%E7%BB%99%E7%BB%93%E8%AE%BA)
    - [主要区别与优势（按维度对比）](#%E4%B8%BB%E8%A6%81%E5%8C%BA%E5%88%AB%E4%B8%8E%E4%BC%98%E5%8A%BF%E6%8C%89%E7%BB%B4%E5%BA%A6%E5%AF%B9%E6%AF%94)
      - [1) 产品广度与成熟度](#1-%E4%BA%A7%E5%93%81%E5%B9%BF%E5%BA%A6%E4%B8%8E%E6%88%90%E7%86%9F%E5%BA%A6)
      - [2) 混合云与企业身份（Enterprise / Hybrid）](#2-%E6%B7%B7%E5%90%88%E4%BA%91%E4%B8%8E%E4%BC%81%E4%B8%9A%E8%BA%AB%E4%BB%BDenterprise--hybrid)
      - [3) 数据平台与分析能力（Data / Analytics）](#3-%E6%95%B0%E6%8D%AE%E5%B9%B3%E5%8F%B0%E4%B8%8E%E5%88%86%E6%9E%90%E8%83%BD%E5%8A%9Bdata--analytics)
      - [4) AI / ML 平台与生态](#4-ai--ml-%E5%B9%B3%E5%8F%B0%E4%B8%8E%E7%94%9F%E6%80%81)
      - [5) 全球基础设施与区域覆盖](#5-%E5%85%A8%E7%90%83%E5%9F%BA%E7%A1%80%E8%AE%BE%E6%96%BD%E4%B8%8E%E5%8C%BA%E5%9F%9F%E8%A6%86%E7%9B%96)
      - [6) IaC 与平台工程（Infra as Code / Engineering）](#6-iac-%E4%B8%8E%E5%B9%B3%E5%8F%B0%E5%B7%A5%E7%A8%8Binfra-as-code--engineering)
    - [面试加分：怎么选（落到“业务与组织”）](#%E9%9D%A2%E8%AF%95%E5%8A%A0%E5%88%86%E6%80%8E%E4%B9%88%E9%80%89%E8%90%BD%E5%88%B0%E4%B8%9A%E5%8A%A1%E4%B8%8E%E7%BB%84%E7%BB%87)
    - [30-second summary](#30-second-summary)
    - [Key differences & advantages (by dimension)](#key-differences--advantages-by-dimension)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 云原生问题库

---

## AWS 的 CloudFormation 与 Terraform 有何异同？

### 中文版（面试回答）

**相同点：**
CloudFormation（CFN）和 Terraform 都是 **IaC（Infrastructure as Code）** 工具，用代码声明基础设施，支持版本管理、可重复部署、环境隔离（dev/stage/prod）、审计与自动化交付（CI/CD），并且都会处理资源之间的依赖关系。

**不同点（核心对比）：**

1. **厂商绑定与生态范围**

* **CloudFormation：** AWS 原生，只面向 AWS（及少量相关集成）。优势是“官方、最稳、与 AWS 服务深度集成”。
* **Terraform：** HashiCorp 生态，**多云/多平台**（AWS/Azure/GCP/K8s/GitHub/Datadog…），更适合跨云和混合基础设施。

2. **状态（State）管理方式**

* **CloudFormation：** 状态由 AWS 在 **Stack** 里托管，你不需要维护本地/远端 state 文件；回滚、资源跟踪是平台内置。
* **Terraform：** 依赖 **state 文件**（本地或远端如 S3 + DynamoDB Lock、Terraform Cloud），灵活但需要你负责锁、权限、备份、分环境隔离等治理。

3. **变更预览与执行体验**

* **CloudFormation：** 通过 **Change Set** 预览变更；更新失败通常会触发 **自动回滚**（可配置）。
* **Terraform：** `plan` 是核心体验，能更直观地看到 diff；执行 `apply`，失败时不会自动回滚到旧状态（通常需要你自己处理恢复/回退策略）。

4. **语言与抽象能力**

* **CloudFormation：** 模板多为 **YAML/JSON**，表达力相对“结构化”；复杂逻辑常依赖宏（Macro）、自定义资源（Custom Resource / Lambda）。
* **Terraform：** **HCL**（HashiCorp Configuration Language）表达力更强，循环、条件、模块化更顺手，工程化体验通常更好。

5. **资源覆盖与更新速度**

* **CloudFormation：** AWS 官方支持，关键服务一般很稳；但**新服务/新特性**有时 Terraform Provider 会更快（也可能反过来，取决于服务）。
* **Terraform：** Provider 覆盖广，更新节奏快，但质量与一致性取决于 provider（AWS provider 通常很成熟）。

6. **组织治理与复用**

* **CloudFormation：** AWS 体系内治理能力强，如 StackSets、与 Organizations/Control Tower 等配合。
* **Terraform：** 模块生态丰富，跨团队/跨平台复用强；配合 Terraform Cloud/Enterprise 做 policy、审批、workspace 管理也很成熟。

**怎么选（给面试加分的结论）：**

* 如果 **只做 AWS、追求原生集成、希望状态托管省心、并且组织已深度 AWS 化**：CloudFormation 很合适。
* 如果 **多云/多平台、希望更强的工程化与模块复用、统一一套 IaC 管理所有资源**：Terraform 更合适。
* 实际项目里也常见 **混用**：例如 AWS Landing Zone/组织级基座用 CFN/StackSets，业务侧跨平台资源用 Terraform 统一编排。

### English Version (Interview-ready)

**Similarities:**
Both CloudFormation (CFN) and Terraform are **Infrastructure as Code (IaC)** tools. They let you declaratively define infrastructure, version it in Git, reproduce environments (dev/stage/prod), automate delivery via CI/CD, and manage dependencies between resources.

**Key Differences:**

1. **Vendor scope / ecosystem**

* **CloudFormation:** AWS-native and AWS-focused. Strong “official support” and deep AWS integration.
* **Terraform:** Multi-cloud and multi-platform (AWS/Azure/GCP/K8s/GitHub/Datadog, etc.), great for hybrid or cross-cloud setups.

2. **State management**

* **CloudFormation:** State is managed by AWS in a **Stack**—no separate state file for you to operate.
* **Terraform:** Uses a **state file** (local or remote like S3 + DynamoDB lock / Terraform Cloud). More flexible, but you must govern locking, access control, backups, and environment separation.

3. **Change preview & execution model**

* **CloudFormation:** Uses **Change Sets** to preview updates; failures often trigger **automatic rollback** (configurable).
* **Terraform:** `terraform plan` is central and typically provides very clear diffs; `apply` does not automatically roll back on failure—you handle recovery/rollback strategies.

4. **Language & expressiveness**

* **CloudFormation:** Mostly **YAML/JSON**; more rigid/structured. Advanced logic often relies on Macros or Custom Resources (e.g., Lambda).
* **Terraform:** **HCL** is highly expressive with conditionals, loops, and strong modularization—often better for large-scale engineering practices.

5. **Resource coverage & update velocity**

* **CloudFormation:** Official AWS support; stable for core services, but new feature coverage timing varies.
* **Terraform:** Broad provider ecosystem and often fast-moving; quality depends on the provider (AWS provider is generally mature).

6. **Governance & reuse**

* **CloudFormation:** Strong AWS-org governance (e.g., StackSets, tight integration with AWS Organizations).
* **Terraform:** Excellent cross-team reuse via modules; strong governance workflows with Terraform Cloud/Enterprise (policies, approvals, workspaces).

**How to choose (strong closing):**

* Choose **CloudFormation** when you’re AWS-only, want AWS-managed state and native integration/governance.
* Choose **Terraform** when you need multi-cloud/multi-platform control, stronger modular engineering, and a single IaC tool across systems.
* In real-world setups, **mixing** is common: use CFN for AWS org foundations, Terraform for broader platform/third-party resources.

---

## 请比较 AWS、Azure、和 GCP 的主要区别和优势？

### 30 秒总览（先给结论）

* **AWS**：服务最全、生态最大、全球覆盖和成熟度最强，适合“**通用型**”与复杂场景、规模化与多样化工作负载。AWS 仍是市场第一梯队，和 Azure、GCP 一起占据主导份额。
* **Azure**：企业市场与 **Microsoft 生态/身份体系/混合云** 优势明显，适合“Windows/AD/Office/企业治理”深度用户与混合部署。
* **GCP**：在 **数据分析与 AI**、云原生（Kubernetes 生态）与网络能力上口碑强，尤其 BigQuery/Vertex AI 等更偏“数据与智能驱动”。

### 主要区别与优势（按维度对比）

#### 1) 产品广度与成熟度

* **AWS 优势**：服务矩阵最完整、细分服务多、社区/解决方案/第三方生态很强，适合业务类型多、变化快的大型平台。
* **Azure 优势**：在企业 IT（身份、管理、合规、Windows/SQL/Office）链路上“天然顺”，更容易和既有企业资产打通。
* **GCP 优势**：在数据与 AI 相关产品体验上更聚焦，很多团队用起来更“轻”和一致。

#### 2) 混合云与企业身份（Enterprise / Hybrid）

* **Azure 强项非常突出**：围绕 Microsoft Entra ID（原 Azure AD）、Azure Arc，把 on-prem / 多云资源纳入统一治理与 RBAC，是很多传统企业的现实选择。
* **AWS**：也能做混合（如 Outposts 等），但“企业 AD/Windows”一体化的心智通常没有 Azure 强。
* **GCP**：也能做混合与多云治理，但在企业桌面/身份/办公生态的天然粘性上不如微软。

#### 3) 数据平台与分析能力（Data / Analytics）

* **GCP 王牌**：BigQuery 是完全托管、serverless 的数仓/分析平台，适合大规模分析与数据驱动产品。
* **AWS / Azure**：也有成熟的数据分析栈（Redshift / Synapse 等），但很多人会把“上手与一致性、serverless 数据分析体验”更多联想到 GCP。

#### 4) AI / ML 平台与生态

* **三家都在强推 AI 平台**（AWS Bedrock、Azure 的 AI 平台体系、GCP Vertex AI 等），差异更多来自：

    * 你更依赖哪家的模型生态/企业集成（微软的企业软件入口、谷歌的数据与研究传统、AWS 的基础设施与服务覆盖）
    * 以及你团队在 MLOps、数据治理、成本控制上的实践能力

#### 5) 全球基础设施与区域覆盖

* **AWS**：全球 Region/AZ 覆盖非常强，适合做全球多区域容灾与低延迟部署；AWS 官方页面会公布当前 Regions/AZ 数量。
* **Azure/GCP**：全球覆盖也很强，但具体“你要的那几个国家/合规区域/边缘节点”才是选型关键（要按目标市场逐个核对）。

#### 6) IaC 与平台工程（Infra as Code / Engineering）

* **AWS 原生 IaC**：CloudFormation。
* **Azure 原生 IaC**：ARM 模板与 **Bicep**（微软官方推荐用 Bicep 改善可读性/工程化）。
* **GCP 原生 IaC**：Deployment Manager 将在 **2026-03-31** 停止支持，GCP 推 **Infrastructure Manager**（托管 Terraform 工作流）。

### 面试加分：怎么选（落到“业务与组织”）

* **你是互联网/平台型、多业务形态、跨区域**：AWS 往往更稳妥（广度+生态）。
* **你是大型企业 IT、微软体系重、强混合云/身份治理**：Azure 通常更匹配。
* **你是数据/分析/AI 驱动公司、重数仓与分析效率**：GCP 经常更有优势。

### 30-second summary

* **AWS**: Broadest service portfolio and ecosystem; very mature and widely adopted. Still a top market leader among the “big three.”
* **Azure**: Strongest **enterprise + Microsoft ecosystem + hybrid** story (identity/governance integration).
* **GCP**: Particularly strong in **data analytics and AI**, with standout experiences like BigQuery (serverless warehouse) and a cloud-native reputation.

### Key differences & advantages (by dimension)

1. **Breadth & maturity**

* **AWS** excels in breadth, depth, and third-party ecosystem.
* **Azure** aligns naturally with enterprise IT patterns and Microsoft tooling.
* **GCP** is often perceived as more focused and cohesive for data/AI-centric stacks.

2. **Enterprise identity & hybrid cloud**

* **Azure** has a clear advantage with Microsoft Entra ID and Azure Arc for unified governance across on-prem/multi-cloud resources.
* **AWS/GCP** can do hybrid too, but Azure tends to be the default for Microsoft-heavy enterprises.

3. **Data & analytics**

* **GCP** shines with **BigQuery** as a fully managed, serverless data warehouse/analytics platform.
* **AWS/Azure** are strong as well, but “serverless analytics UX” is a common GCP differentiator.

4. **AI/ML platforms**

All three offer robust AI platforms; the practical choice often comes down to your model ecosystem, enterprise integration needs, and your team’s MLOps/cost discipline.

5. **Global infrastructure**

* **AWS** has very strong global region/AZ coverage (check official region/AZ counts for current numbers).
* **Azure/GCP** are also global; the deciding factor is usually the specific regions/compliance requirements you need.

6. **IaC / platform engineering**

* **AWS**: CloudFormation.
* **Azure**: ARM + **Bicep** (Microsoft recommends Bicep as a more readable authoring experience).
* **GCP**: Deployment Manager support ends **Mar 31, 2026**; **Infrastructure Manager** is a managed Terraform-based workflow.

---
