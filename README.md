<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [2025 Java 学习计划](#2025-java-%E5%AD%A6%E4%B9%A0%E8%AE%A1%E5%88%92)
  - [总体目标](#%E6%80%BB%E4%BD%93%E7%9B%AE%E6%A0%87)
  - [第一阶段（第 1-4 周）：基础复习与技能提升](#%E7%AC%AC%E4%B8%80%E9%98%B6%E6%AE%B5%E7%AC%AC-1-4-%E5%91%A8%E5%9F%BA%E7%A1%80%E5%A4%8D%E4%B9%A0%E4%B8%8E%E6%8A%80%E8%83%BD%E6%8F%90%E5%8D%87)
    - [第 1 周：Java 基础、开发工具与算法快速复习](#%E7%AC%AC-1-%E5%91%A8java-%E5%9F%BA%E7%A1%80%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7%E4%B8%8E%E7%AE%97%E6%B3%95%E5%BF%AB%E9%80%9F%E5%A4%8D%E4%B9%A0)
      - [Java 基础（共 3 天）](#java-%E5%9F%BA%E7%A1%80%E5%85%B1-3-%E5%A4%A9)
      - [开发工具（共 1 天）](#%E5%BC%80%E5%8F%91%E5%B7%A5%E5%85%B7%E5%85%B1-1-%E5%A4%A9)
      - [算法基础（贯穿全周）](#%E7%AE%97%E6%B3%95%E5%9F%BA%E7%A1%80%E8%B4%AF%E7%A9%BF%E5%85%A8%E5%91%A8)
      - [英语听力与口语（贯穿全周）](#%E8%8B%B1%E8%AF%AD%E5%90%AC%E5%8A%9B%E4%B8%8E%E5%8F%A3%E8%AF%AD%E8%B4%AF%E7%A9%BF%E5%85%A8%E5%91%A8)
    - [第 2 周：Spring 框架、数据库与缓存复习](#%E7%AC%AC-2-%E5%91%A8spring-%E6%A1%86%E6%9E%B6%E6%95%B0%E6%8D%AE%E5%BA%93%E4%B8%8E%E7%BC%93%E5%AD%98%E5%A4%8D%E4%B9%A0)
    - [第 3 周：算法强化与性能优化初步](#%E7%AC%AC-3-%E5%91%A8%E7%AE%97%E6%B3%95%E5%BC%BA%E5%8C%96%E4%B8%8E%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96%E5%88%9D%E6%AD%A5)
    - [第 4 周：英语能力提升与面试准备](#%E7%AC%AC-4-%E5%91%A8%E8%8B%B1%E8%AF%AD%E8%83%BD%E5%8A%9B%E6%8F%90%E5%8D%87%E4%B8%8E%E9%9D%A2%E8%AF%95%E5%87%86%E5%A4%87)
  - [第二阶段 Java Cloud-Native Sprint](#%E7%AC%AC%E4%BA%8C%E9%98%B6%E6%AE%B5-java-cloud-native-sprint)
    - [执行背景](#%E6%89%A7%E8%A1%8C%E8%83%8C%E6%99%AF)
      - [声明](#%E5%A3%B0%E6%98%8E)
      - [护栏](#%E6%8A%A4%E6%A0%8F)
      - [每日模板](#%E6%AF%8F%E6%97%A5%E6%A8%A1%E6%9D%BF)
    - [当前进度](#%E5%BD%93%E5%89%8D%E8%BF%9B%E5%BA%A6)
      - [快速导航](#%E5%BF%AB%E9%80%9F%E5%AF%BC%E8%88%AA)
    - [时间轴 & 核心交付物](#%E6%97%B6%E9%97%B4%E8%BD%B4--%E6%A0%B8%E5%BF%83%E4%BA%A4%E4%BB%98%E7%89%A9)
      - [KPI & 简历映射](#kpi--%E7%AE%80%E5%8E%86%E6%98%A0%E5%B0%84)
  - [Week 5 - Cloud-Native 微服务上云（EKS）](#week-5---cloud-native-%E5%BE%AE%E6%9C%8D%E5%8A%A1%E4%B8%8A%E4%BA%91eks)
    - [前置检查](#%E5%89%8D%E7%BD%AE%E6%A3%80%E6%9F%A5)
    - [Day 1 - 复盘：应用骨架 + Docker 镜像 + 推送 ECR](#day-1---%E5%A4%8D%E7%9B%98%E5%BA%94%E7%94%A8%E9%AA%A8%E6%9E%B6--docker-%E9%95%9C%E5%83%8F--%E6%8E%A8%E9%80%81-ecr)
      - [今天做了什么（Done）](#%E4%BB%8A%E5%A4%A9%E5%81%9A%E4%BA%86%E4%BB%80%E4%B9%88done)
      - [关键决策与记录](#%E5%85%B3%E9%94%AE%E5%86%B3%E7%AD%96%E4%B8%8E%E8%AE%B0%E5%BD%95)
      - [明天计划（Next）](#%E6%98%8E%E5%A4%A9%E8%AE%A1%E5%88%92next)
    - [Day 2 - K8s 基础对象（NS/SA/Config/Secret/Deployment/Service）](#day-2---k8s-%E5%9F%BA%E7%A1%80%E5%AF%B9%E8%B1%A1nssaconfigsecretdeploymentservice)
    - [Day 3 - Ingress（ALB）对外暴露 + HPA](#day-3---ingressalb%E5%AF%B9%E5%A4%96%E6%9A%B4%E9%9C%B2--hpa)
    - [Day 4 -（可选但高收益）S3 最小接入 + IRSA](#day-4--%E5%8F%AF%E9%80%89%E4%BD%86%E9%AB%98%E6%94%B6%E7%9B%8As3-%E6%9C%80%E5%B0%8F%E6%8E%A5%E5%85%A5--irsa)
    - [Day 5 - 收尾硬化 + 文档化 + 指标留痕](#day-5---%E6%94%B6%E5%B0%BE%E7%A1%AC%E5%8C%96--%E6%96%87%E6%A1%A3%E5%8C%96--%E6%8C%87%E6%A0%87%E7%95%99%E7%97%95)
    - [常见问题与 20 分钟退路](#%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98%E4%B8%8E-20-%E5%88%86%E9%92%9F%E9%80%80%E8%B7%AF)
  - [Week 6 - 观测 & 韧性](#week-6---%E8%A7%82%E6%B5%8B--%E9%9F%A7%E6%80%A7)
    - [通用前置](#%E9%80%9A%E7%94%A8%E5%89%8D%E7%BD%AE)
    - [Day 1 - 应用指标暴露 + AMP 工作区](#day-1---%E5%BA%94%E7%94%A8%E6%8C%87%E6%A0%87%E6%9A%B4%E9%9C%B2--amp-%E5%B7%A5%E4%BD%9C%E5%8C%BA)
    - [Day 2 - ADOT Collector（采集 → AMP）+ 成本护栏](#day-2---adot-collector%E9%87%87%E9%9B%86-%E2%86%92-amp-%E6%88%90%E6%9C%AC%E6%8A%A4%E6%A0%8F)
    - [Day 3 - Grafana Dash + SLI/SLO 口径](#day-3---grafana-dash--slislo-%E5%8F%A3%E5%BE%84)
    - [Day 4 - Chaos Mesh 安装 + `pod-kill`/`network-latency` 实验](#day-4---chaos-mesh-%E5%AE%89%E8%A3%85--pod-killnetwork-latency-%E5%AE%9E%E9%AA%8C)
    - [Day 5 - 整理与硬化（配额/限额/告警）](#day-5---%E6%95%B4%E7%90%86%E4%B8%8E%E7%A1%AC%E5%8C%96%E9%85%8D%E9%A2%9D%E9%99%90%E9%A2%9D%E5%91%8A%E8%AD%A6)
    - [20 分钟退路清单](#20-%E5%88%86%E9%92%9F%E9%80%80%E8%B7%AF%E6%B8%85%E5%8D%95)
  - [Week 7 - CI/CD + DevOps](#week-7---cicd--devops)
    - [通用前置](#%E9%80%9A%E7%94%A8%E5%89%8D%E7%BD%AE-1)
    - [Day 1 - 创建 GitHub OIDC 角色 + EKS RBAC 映射](#day-1---%E5%88%9B%E5%BB%BA-github-oidc-%E8%A7%92%E8%89%B2--eks-rbac-%E6%98%A0%E5%B0%84)
    - [Day 2 - CI：Maven 构建 + 单测 + 镜像构建/扫描/推送](#day-2---cimaven-%E6%9E%84%E5%BB%BA--%E5%8D%95%E6%B5%8B--%E9%95%9C%E5%83%8F%E6%9E%84%E5%BB%BA%E6%89%AB%E6%8F%8F%E6%8E%A8%E9%80%81)
    - [Day 3 - CD：发布到 EKS（滚动更新）+ 策略参数](#day-3---cd%E5%8F%91%E5%B8%83%E5%88%B0-eks%E6%BB%9A%E5%8A%A8%E6%9B%B4%E6%96%B0-%E7%AD%96%E7%95%A5%E5%8F%82%E6%95%B0)
    - [Day 4 - 回滚与手动触发；参数化环境](#day-4---%E5%9B%9E%E6%BB%9A%E4%B8%8E%E6%89%8B%E5%8A%A8%E8%A7%A6%E5%8F%91%E5%8F%82%E6%95%B0%E5%8C%96%E7%8E%AF%E5%A2%83)
    - [Day 5 - 指标留痕 + 文档固化 + 清理脚本](#day-5---%E6%8C%87%E6%A0%87%E7%95%99%E7%97%95--%E6%96%87%E6%A1%A3%E5%9B%BA%E5%8C%96--%E6%B8%85%E7%90%86%E8%84%9A%E6%9C%AC)
    - [20 分钟退路总表](#20-%E5%88%86%E9%92%9F%E9%80%80%E8%B7%AF%E6%80%BB%E8%A1%A8)
  - [Week 8 — Mock + Résumé](#week-8--mock--r%C3%A9sum%C3%A9)
    - [通用前置](#%E9%80%9A%E7%94%A8%E5%89%8D%E7%BD%AE-2)
  - [Day 1 - 成果打包 & 架构一页纸（One-Pager）](#day-1---%E6%88%90%E6%9E%9C%E6%89%93%E5%8C%85--%E6%9E%B6%E6%9E%84%E4%B8%80%E9%A1%B5%E7%BA%B8one-pager)
  - [Day 2 - Mock-1（全英）：运行/韧性/观测深挖](#day-2---mock-1%E5%85%A8%E8%8B%B1%E8%BF%90%E8%A1%8C%E9%9F%A7%E6%80%A7%E8%A7%82%E6%B5%8B%E6%B7%B1%E6%8C%96)
  - [Day 3 - 简历 v2（中/英）+ LinkedIn 打磨](#day-3---%E7%AE%80%E5%8E%86-v2%E4%B8%AD%E8%8B%B1-linkedin-%E6%89%93%E7%A3%A8)
    - [Day 4 - Mock-2（全英）：AI + 多云系统设计](#day-4---mock-2%E5%85%A8%E8%8B%B1ai--%E5%A4%9A%E4%BA%91%E7%B3%BB%E7%BB%9F%E8%AE%BE%E8%AE%A1)
    - [Day 5 - Day-8 Cleanup 收官 + 招聘投递包](#day-5---day-8-cleanup-%E6%94%B6%E5%AE%98--%E6%8B%9B%E8%81%98%E6%8A%95%E9%80%92%E5%8C%85)
    - [高频问题库](#%E9%AB%98%E9%A2%91%E9%97%AE%E9%A2%98%E5%BA%93)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 2025 Java 学习计划

## 总体目标

系统复习与提升 Java 基础知识、算法能力以及英语综合技能，以顺利通过外企技术面试，达到中高级 Java 开发工程师或 Java 项目技术 Leader 的水平。

---

## 第一阶段（第 1-4 周）：基础复习与技能提升

### 第 1 周：Java 基础、开发工具与算法快速复习

#### Java 基础（共 3 天）

- **Day1: Java 语法与 OOP**
  - 数据类型、控制结构（条件语句、循环）
  - 面向对象基础（封装、继承、多态）
- **Day2: 集合框架与异常处理**
  - 集合框架（List, Set, Map）
  - 异常机制（try-catch、throws、自定义异常）
- **Day3: Java 多线程基础**
  - 线程创建（Thread、Runnable、Callable）
  - 线程池使用（ExecutorService）
  - 线程同步（synchronized、Lock）与线程安全（竞态条件、原子操作）

#### 开发工具（共 1 天）

- Git（版本控制与分支管理）
- Maven（项目构建与依赖管理）
- MySQL（基本操作与优化技巧）
- IntelliJ IDEA（基础操作与快捷键）
- Docker（容器化基础使用）

#### 算法基础（贯穿全周）

- 每日完成 1-2 道 Leetcode Medium 算法题目练习（数据结构与基础算法）

#### 英语听力与口语（贯穿全周）

- 每天收听 30 分钟英文技术播客或视频（如 TechLead）
- 每天英文口语复述算法题或技术知识点，锻炼表达能力

### 第 2 周：Spring 框架、数据库与缓存复习

- **Day1: Spring 核心**
  - 依赖注入（IOC）、面向切面编程（AOP）
- **Day2: Spring Boot 基础**
  - 自动装配、配置文件、REST API 搭建
- **Day3: Spring Boot 项目实战**
  - 整合数据库、实现业务功能、异常处理
- **Day4: Spring Cloud 微服务入门**
  - Eureka 服务注册、Ribbon 负载均衡、Feign 接口调用
- **Day5: MySQL 优化**
  - 索引优化、SQL 查询优化、慢查询分析
- **Day6: Redis 缓存设计**
  - 缓存场景、缓存设计、缓存击穿/雪崩应对策略
- **Day7: 周总结与项目优化**
  - 项目代码整合与文档编写

### 第 3 周：算法强化与性能优化初步

| 天次 | 学习主题 | 关键要点 | 主要产出 |
|------|----------|----------|----------|
| **Day 1** | 动态规划基础复盘 | - DP 四步模板<br>- 斐波那契、爬楼梯、01 背包 | 4 道题代码 + `dp-day1.md` 英文总结 |
| **Day 2** | 动态规划进阶 | - 一维压缩、O(n log n) LIS<br>- 最大子数组和（Kadane）<br>- 区间 DP / Bitmask DP 入门 | 5 道题代码 + `dp-day2.md` 英文总结 |
| **Day 3** | 回溯 & 贪心 | - 回溯三步框架 + 剪枝<br>- 组合/全排列/组合总和<br>- 区间贪心（无重叠区间） | 5 道题 + `backtrack-greedy-day3.md` |
| **Day 4** | JVM 内部结构 & GC 原理 | - 堆/Metaspace 布局<br>- G1/ZGC 收集器机制<br>- GC 日志、jstat、VisualVM | `gc_base.log` + `jvm-gc-day4.md` |
| **Day 5** | JVM 性能调优实战 | - 压测 ► 诊断 ► 调参 ► 复测闭环<br>- 参数：堆大小、MaxGCPauseMillis、IHOP | baseline / tuned 报表 + `jvm-tune-report.md` |
| **Day 6** | MySQL 高级优化 & 索引深度 | - 覆盖索引、下推谓词<br>- Buffer Pool 命中率<br>- EXPLAIN JSON / optimizer_trace | 优化前后 EXPLAIN + `mysql-adv-day6.md` |
| **Day 7** | 周整合 & 输出 | - 知识库 & Cheat Sheet<br>- Pull Request：GC/Index 参数合并<br>- 英文周报 ≥ 200 词 + 5 min 算法讲解视频 | `week3-cheatsheet.md` + `week3-report-en.md` + `week4-plan-draft.md` |

### 第 4 周：英语能力提升与面试准备

- **Day1-Day3: 技术英文阅读与写作**
  - 阅读英文技术文章或书籍（如《Effective Java》）
  - 每天撰写英文技术博客或学习总结
- **Day4-Day5: 模拟技术面试**
  - 每周进行一次英文技术面试模拟
  - 改善英文自我介绍与技术问答表现
- **Day6-Day7: 面试总结与准备强化**
  - 总结本周面试经验与不足，制定针对性改进措施

---

## 第二阶段 Java Cloud-Native Sprint

### 执行背景

#### 声明

2025 - Stage 2 · AWS 专版：

> - **目标导向**：以**外企 Java 云原生面试**为导向，优先“可讲清楚 + 可演示 + 可量化”，避免陷入低收益 debug。
> - **成本/生命周期原则**：
>   1) 用时开、用完关：**busy=重建，idle=销毁**；
>   2) **单集群多命名空间**，避免多控制面；
>   3) 任何新增资源须满足“**Day-8 一键清理**”。
> - **计划组成**：Week5（微服务上云）→ Week6（观测+韧性）→ Week7（CI/CD+DevOps）→ Week8（Mock+简历）。
> - **产物要求**：每个模块最少 3 个“可截图/可量化”产物 + 一段面试 STAR 故事。

#### 护栏

全阶段通用：

- **预算**：总账单 ≤ ¥1,500；常规按需启停 ≈ ¥800（已含缓冲）。
- **计费控制**：EKS 控制面仅上课日开 6h；节点 `t3.small` Spot 混合，夜间 `scale-down-to-0`。
- **安全**：IRSA/OIDC 最小权限，Terraform `backend s3 + dynamodb lock + AES256`，流水线 Trivy 扫描。
- **可观测**：Prom 采样 `sample_limit` 并过滤 `kubelet_*`；CloudWatch Budget（\$80）与告警已开启。
- **清理**：ALB/TG、ECR、S3、DynamoDB、Budget、EKS 全纳入 `cleanup.sh`。

#### 每日模板

> 避免低效 debug

1) **30min 理论速读**：当天主题的 3–5 个必知点（写入 `notes/YYMMDD.md`）。
2) **90min 关键实操**：只做“面试可讲”的最小闭环（可截图、可复现）。
3) **20min 量化留痕**：把 1–2 个指标/截图落进 README（如 GitOps 成功率、MTTR、Trace 覆盖）。
4) **20min STAR 记录**：一句话补全场景/决策/结果（面试素材）。
5) **Git 提交**：代码、YAML、笔记一并提交；空闲即执行 `destroy`。

> 全流程仅依赖 AWS 账户即可完成；后续若需多云演示，可启用 Terraform GKE provider 再走一遍 Helm / Argo 即可。

### 当前进度

- Bootcamp **Day 1 ~ Day 3 已完成**（一键重建 / 一键销毁 已验证；Route53 保留 ≈ $0.5/月）。

#### 快速导航

进度链接：

- Bootcamp 笔记与操作：`stage2-BootCamp-day0.md` / `stage2-BootCamp-day1*.md` / `stage2-BootCamp-day2*.md`
- 一键重建/销毁脚本：`infra/`（Terraform + 脚本）
- 学习笔记（Google Drive/2025）：按模块对应 Week 5~8

### 时间轴 & 核心交付物

| 时段 | 核心主题 & 关键实操 | 主要交付物 |
|------|-------------------|-----------|
| **Day-0 (½ d)** | **Docker & K8s Refresher**<br>· Play-with-Docker：`build / run / push`<br>· Kubernetes Basics 1-3：`kubectl create / expose / scale` | `stage2-BootCamp-day0.md` |
| **Bootcamp 3 d** | **云底座 + IaC**<br>**Day 1** VPC / ALB / IAM 对照（AWS）<br>**Day 2** `eksctl` 创建 **EKS Managed NodeGroup** (Spot t3.small ×2 + OD t3.medium ×1)<br>**Day 3** Terraform 导入 EKS；`backend "s3" + DynamoDB lock + AES256` |
| **Week 5** | **Cloud-Native 微服务上云（EKS）**<br>① Spring Boot 微服务骨架 → Docker 镜像 → 推送 ECR<br>② K8s 基础对象：Namespace / SA(IRSA) / ConfigMap / Secret / Deployment / Service<br>③ Ingress（AWS Load Balancer Controller）+ HPA → 通过 ALB DNS 暴露服务；可选：接入 S3 | 源码仓 + Docker 镜像 Tag<br>K8s YAML/Helm（含 IRSA）<br>ALB DNS 可访问截图 + （可选）S3 读写验证 |
| **Week 6** | **可观测 & 韧性（Observability + Resilience）**<br>① ADOT Collector → **Amazon Managed Prometheus (AMP)**（`sample_limit`=10k，drop `kubelet_*`）+ Grafana Cloud 仪表盘<br>② Spring Actuator 暴露应用指标，定义 SLI/SLO（可用性、P95、错误率）<br>③ **Chaos Mesh**：`pod-kill`/`network-latency` 演练 + **HPA 自愈**，输出 **MTTR ≤ 1 min** | ADOT 配置 & AMP Workspace<br>Grafana Dash 截图（应用/集群指标）<br>Chaos 报告（MTTR/P95） + HPA 触发截图 |
| **Week 7** | **CI/CD + DevOps 自动化**<br>① GitHub Actions（OIDC）→ **ECR** 推镜像 → **EKS** 滚动发布（Helm/`kubectl`）<br>② 阶段门：Maven 单测 + **Trivy** 镜像扫描 + 策略化回滚（`rollout undo`）<br>③ 指标留痕：构建时长、发布成功率、平均改动交付时间（Lead Time） | `.github/workflows/ci-cd.yml`<br>IAM OIDC 角色 + `aws-auth` RBAC<br>发布/回滚脚本与运行截图 |
| **Week 8** | **Mock Marathon & Résumé v2**<br>① Mock-1：故障注入 + SLO 深挖（全英，含追问）<br>② Mock-2：AI + 多云系统设计（全英，画架构图）<br>③ 成果打包：README/作品集/两段演示视频/`progress.png` 指标折线<br>④ **Day-8 Cleanup**：一键销毁 + 账单审计 + 重建演练清单 | 两段 90min 录像 + 评分表（含改进项）<br>简历 v2（中/英）PDF + LinkedIn 文案<br>`progress.png` + 架构图 + `cleanup/` 脚本 & 账单截图 |

#### KPI & 简历映射

| 指标 | 目标 | 简历措辞示例 |
|------|------|-------------|
| GitOps 发布成功率 | ≥ 98 % | “双链路 GitOps 发布成功率 **98 %+**” |
| Chaos 平均恢复 MTTR | ≤ 1 min | “注入 pod-kill 后平均恢复 **49 s** (业内基线 5 min)” |
| Trace 覆盖率 | ≥ 95 % | “OpenTelemetry 链路覆盖 **95 %** API 请求” |
| 英文 Mock 综合分 | ≥ 4.0 / 5 | “全英文系统设计 Mock 得分 **4.2 / 5**” |

---

## Week 5 - Cloud-Native 微服务上云（EKS）

> 目标：把一个最小可讲、可演示的 **Spring Boot 微服务** 跑上现有 EKS（NodeGroup），通过 ALB 对外暴露；必要时最小接入 S3（IRSA），并完成可量化产物。
> 原则：**只做最小闭环**（可截图/可复现），遇到卡顿 > 20 分钟走“退路方案”，避免低收益 debug。

### 前置检查

10 分钟完成：

- 若集群已销毁：执行现有 **一键重建**（半小时内可起）。
- 设定本周通用变量（bash）：
  ```bash
  export AWS_REGION=us-east-1
  export ECR_REPO=task-api
  export CLUSTER=dev
  export NS=svc-task
  export APP=task-api
  ```

> 预期产物总表：源码仓（`task-api`）、ECR 镜像 tag、K8s YAML/Helm、ALB DNS 可访问截图、（可选）S3 读写验证。

### Day 1 - 复盘：应用骨架 + Docker 镜像 + 推送 ECR

#### 今天做了什么（Done）

* 起了一个最小 **Spring Boot 3 + Actuator** 服务（`/api/hello`、`/api/ping` 与 `/actuator/health{,/liveness,/readiness}`）。
* 编写 **多阶段 Dockerfile**，本地构建镜像并推送到 **Amazon ECR（us-east-1）**。
* 使用 **digest** 固定镜像版本：`sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741`。
* 在 **EKS 集群 `dev` / 命名空间 `svc-task`** 部署 `Deployment+Service(ClusterIP)`，通过 `kubectl port-forward` 完成端到端验证（业务接口与健康探针均返回 `UP`）。

#### 关键决策与记录

* **Region / Cluster / Repo**：`us-east-1` / `dev` / `task-api`。
* **AWS 身份**：采用 **SSO Profile `phase2-sso`**；脚本已内置 `--profile` 支持。
* **镜像标记策略**：推送 `:0.1.0` 与 `:latest`，**部署用 digest 锁定**（避免 tag 漂移）。
* **ECR 生命周期策略**：当前设置“仅保留 1 个 tag + 未打标签 1 天过期”→ 成本低但**回滚空间极小**；建议后续调整为**至少保留最近 5–10 个 tag**。

#### 明天计划（Next）

* 安装 **AWS Load Balancer Controller**，为服务创建 **Ingress（ALB）**：

  * 校验子网标签 / Controller 权限 / 安全组放行；
  * Ingress 健康检查指向 `/actuator/health/readiness`；
  * 获取 **ALB DNS** 并完成公网访问验证与截图存证。
* （可选）为 `task-api` 添加 **HPA（基于 CPU 60%）**，做一次轻压测观察扩缩。
* 文档更新：在 README/计划文档中记录 **ALB DNS、发布步骤、探针路径** 与**一张流量路径草图**。

### Day 2 - K8s 基础对象（NS/SA/Config/Secret/Deployment/Service）

**做什么**

1. 建命名空间与 **IRSA** ServiceAccount（后续可接 S3）。
2. 写最小化 Deployment + ClusterIP Service；加 liveness/readiness probes。
3. 用 ConfigMap 注入 `APP_NAME`、`STAGE=dev` 等。

**YAML 示范（可直接落库 `k8s/base/`）**

```yaml
# ns-sa.yaml
apiVersion: v1
kind: Namespace
metadata: { name: ${NS} }
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: ${APP}-sa
  namespace: ${NS}
  annotations:
    eks.amazonaws.com/role-arn: arn:aws:iam::<ACCOUNT_ID>:role/${APP}-irsa # 暂可留空，Day4 再补
```

```yaml
# deploy-svc.yaml
apiVersion: v1
kind: ConfigMap
metadata: { name: ${APP}-cm, namespace: ${NS} }
data:
  APP_NAME: "task-api"
  STAGE: "dev"
---
apiVersion: v1
kind: Deployment
metadata: { name: ${APP}, namespace: ${NS} }
spec:
  replicas: 1
  selector: { matchLabels: { app: ${APP} } }
  template:
    metadata: { labels: { app: ${APP} } }
    spec:
      serviceAccountName: ${APP}-sa
      containers:
      - name: ${APP}
        image: <ECR_URI>/${ECR_REPO}:0.1.0
        ports: [{ containerPort: 8080 }]
        envFrom: [{ configMapRef: { name: ${APP}-cm } }]
        readinessProbe: { httpGet: { path: /actuator/health/readiness, port: 8080 }, initialDelaySeconds: 10, periodSeconds: 5 }
        livenessProbe:  { httpGet: { path: /actuator/health/liveness,  port: 8080 }, initialDelaySeconds: 30, periodSeconds: 10 }
---
apiVersion: v1
kind: Service
metadata: { name: ${APP}-svc, namespace: ${NS} }
spec:
  type: ClusterIP
  selector: { app: ${APP} }
  ports: [{ port: 80, targetPort: 8080 }]
```

**关键命令**

```bash
ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
sed -e "s|\${NS}|$NS|g" -e "s|\${APP}|$APP|g" \
    -e "s|\${ECR_REPO}|$ECR_REPO|g" \
    -e "s|\<ACCOUNT_ID\>|$ACCOUNT|g" \
    -e "s|\<ECR_URI\>|$ACCOUNT.dkr.ecr.$AWS_REGION.amazonaws.com|g" \
    k8s/base/*.yaml | kubectl apply -f -
kubectl get pod -n $NS -w
```

**产物**：

`kubectl get deploy,svc -n $NS` 截图；就绪 1/1。

**退路**：

Probe 失败 → 暂时改为 `/actuator/health`；或放宽 `initialDelaySeconds`。

### Day 3 - Ingress（ALB）对外暴露 + HPA

**做什么**

1. **若尚未安装 AWS Load Balancer Controller**，用已有脚本/Helm 安装（与 OIDC 角色绑定）。
2. 配置 Ingress，自动创建 ALB；设置 HPA 基于 CPU 60%。

**Ingress 示例（`k8s/ingress.yaml`）**

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ${APP}-ing
  namespace: ${NS}
  annotations:
    kubernetes.io/ingress.class: alb
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    alb.ingress.kubernetes.io/healthcheck-path: /actuator/health/readiness
spec:
  rules:
  - http:
      paths:
      - path: /
        pathType: Prefix
        backend: { service: { name: ${APP}-svc, port: { number: 80 } } }
```

**HPA 示例**

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata: { name: ${APP}-hpa, namespace: ${NS} }
spec:
  scaleTargetRef: { apiVersion: v1, kind: Deployment, name: ${APP} }
  minReplicas: 1
  maxReplicas: 3
  metrics:
  - type: Resource
    resource: { name: cpu, target: { type: Utilization, averageUtilization: 60 } }
```

**关键命令**

```bash
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml
# 等待 ALB 创建完成，获取 DNS
kubectl get ingress -n $NS
```

**产物**：

ALB DNS 可访问首页/健康检查截图；`kubectl describe hpa` 输出。

**退路**：

ALB 迟迟不出 → 检查子网 tag / sg；临时改为 `kubectl port-forward` 验证服务可用性。

### Day 4 -（可选但高收益）S3 最小接入 + IRSA

**做什么**

1. 新建 **S3 bucket**（Terraform 或控制台均可；**Block Public Access**、SSE-S3 开启）。
2. 配置 **IAM 角色**（IRSA）只允许 `bucket/$APP/*` 前缀读写；把 Role 绑定到 `${APP}-sa`。
3. 在应用添加 `/api/files/put?key=...` 与 `/api/files/get?key=...` 两个端点，演示最小读写。

**最小策略示例（`iam/s3-irsa.json`）**

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": ["s3:PutObject","s3:GetObject"],
    "Resource": "arn:aws:s3:::<BUCKET>/${APP}/*"
  }]
}
```

**验证**

```bash
curl -X POST "http://<ALB_DNS>/api/files/put?key=test.txt" -d 'hello'
curl "http://<ALB_DNS>/api/files/get?key=test.txt"   # 预期返回 hello
```

**产物**：

两条 curl 成功截图；应用日志相关片段。

**退路**：

若当日遇到卡点 → 暂跳过 S3，保留 IRSA 绑定，Day5 先完成文档与演示。

### Day 5 - 收尾硬化 + 文档化 + 指标留痕

**做什么**

1. K8s 资源硬化：为容器加 `requests/limits`，为 ns 增加 `ResourceQuota`（可选），为部署添加 `PodDisruptionBudget`（1）。
2. 输出 **演示脚本**：`demo/start.sh`（apply 所有 YAML）、`demo/stop.sh`（清理除集群外的本周资源；ALB/TG 包含）。
3. README 更新：加入“访问方式、镜像 tag、ALB DNS、（可选）S3 说明、已知限制”。
4. **量化指标**：记录本周 GitOps 发布成功率（若未上 GitOps，记录“部署成功次数/尝试次数”）、HPA 触发截图、平均冷启动时间。
5. **STAR 一句话**：补充到“面试素材”区。

**参考片段**

```yaml
# pdb.yaml
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata: { name: ${APP}-pdb, namespace: ${NS} }
spec:
  minAvailable: 1
  selector: { matchLabels: { app: ${APP} } }
```

**验收清单（打勾即过）**

- [ ] ALB DNS 可稳定访问 `/healthz` 与 `/api/tasks`
- [ ] 部署就绪 1/1（或 HPA 扩展 1→3 后恢复）
- [ ] （可选）S3 PUT/GET 成功
- [ ] README 已更新“部署与访问”与“本周指标”
- [ ] `demo/start.sh` / `demo/stop.sh` 可独立执行
- [ ] 本周截图归档：ECR、Pods、Ingress、HPA、（可选）S3

### 常见问题与 20 分钟退路

- **ECR 登录失败** → 换 Docker Hub 公有仓并继续（后续再切回 ECR）。
- **ALB 不创建** → 先用 `kubectl port-forward svc/${APP}-svc 8080:80` 完成演示；另开 issue 排查子网 tag/控制器。
- **IRSA 无法授权** → 临时改用 `aws configure` 本地测试通过后，再把策略最小化回 IRSA。

> 完成 Week 5 后，即可在面试里完整叙述“**Java 微服务 → 容器化 → EKS 部署 → 通过 ALB 暴露 →（可选）S3 集成**”的闭环，并展示截图与指标作为佐证。

---

## Week 6 - 观测 & 韧性

> 目标：在**不引入重型运维**的前提下，建立“应用 + 集群”的指标观测、SLO 口径与最小化 Chaos 自愈演示。
> 原则：继续 **单集群多命名空间**；一次只做“可讲清楚的最小闭环”。卡顿 > 20 分钟即走退路方案。

### 通用前置

10 分钟 完成：

```bash
export AWS_REGION=us-east-1
export NS=svc-task          # 与 Week5 保持一致
export APP=task-api
export AMP_ALIAS=renda-lab
```

产物总表：

- `observability/` 目录（ADOT 配置、Grafana Notes）
- `chaos/` 目录（安装 values、实验 YAML、报告）
- 截图：Grafana 图、HPA describe、Chaos 成功页、MTTR 计算过程

### Day 1 - 应用指标暴露 + AMP 工作区

**做什么**

1. 在 `task-api` 开启 **Actuator + Prometheus**：
   - `pom.xml` 增：`spring-boot-starter-actuator`、`micrometer-registry-prometheus`
   - `application.yml`：
     ```yaml
     management:
       endpoints.web.exposure.include: health,info,prometheus
       endpoint.health.probes.enabled: true
     ```
2. 本地跑一次 `/actuator/prometheus` 验证。
3. 创建 **AMP Workspace**（一次性）：
   ```bash
   aws amp create-workspace --region $AWS_REGION --alias $AMP_ALIAS \
     --query workspaceId --output text > .amp_id
   ```
4. 记录 AMP remote\_write 端点（供 ADOT 使用）：
   ```bash
   AMP_ID=$(cat .amp_id)
   echo "https://aps-workspaces.$AWS_REGION.amazonaws.com/workspaces/$AMP_ID/api/v1/remote_write" > .amp_rw
   ```

**产物**：

- `/actuator/prometheus` 截图
- `.amp_id`、`.amp_rw` 文件（勿提交敏感数据，可写入 README 为占位）

**退路**：

若 AMP 创建受限 → 暂改为 **Prometheus Helm（本地集群内）+ Grafana OSS**，后面步骤中的“remote\_write”全部替换为本地 `http://prometheus:9090`（仅演示用）。

### Day 2 - ADOT Collector（采集 → AMP）+ 成本护栏

**做什么**

1. 新建 `observability/adot-collector.yaml`：最小 **Agent/DaemonSet**，采集 **应用 /actuator/prometheus** 与 **kube-state-metrics**（如未装可先跳过）。带**成本护栏**：
   ```yaml
   apiVersion: v1
   kind: Namespace
   metadata: { name: aws-observe }
   ---
   apiVersion: v1
   kind: DaemonSet
   metadata: { name: adot, namespace: aws-observe, labels: { app: adot } }
   spec:
     selector: { matchLabels: { app: adot } }
     template:
       metadata: { labels: { app: adot } }
       spec:
         serviceAccountName: adot-sa
         containers:
         - name: collector
           image: public.ecr.aws/aws-observability/aws-otel-collector:latest
           args: ["--config=/etc/otel/config.yaml"]
           volumeMounts: [{ name: conf, mountPath: /etc/otel }]
         volumes:
         - name: conf
           configMap:
             name: adot-conf
   ---
   apiVersion: v1
   kind: ConfigMap
   metadata: { name: adot-conf, namespace: aws-observe }
   data:
     config.yaml: |
       receivers:
         prometheus:
           config:
             scrape_configs:
             - job_name: "app"
               metrics_path: /actuator/prometheus
               static_configs:
               - targets: ["${APP}-svc.${NS}.svc.cluster.local:80"]
       processors:
         filter/drop_kubelet:
           metrics:
             include:
               match_type: regexp
               metric_names: ["kubelet_.*"]
         attributes:
           actions:
           - key: project
             value: phase2-sprint
             action: upsert
         batch: {}
       exporters:
         prometheusremotewrite:
           endpoint: ${AMP_REMOTE_WRITE}
           auth:
             authenticator: sigv4auth
           external_labels:
             cluster: dev
           # 成本护栏
           remote_write_queue:
             max_samples_per_send: 10000
       extensions:
         sigv4auth:
           region: ${AWS_REGION}
       service:
         extensions: [sigv4auth]
         pipelines:
           metrics:
             receivers: [prometheus]
             processors: [filter/drop_kubelet, attributes, batch]
             exporters: [prometheusremotewrite]
   ```
   > 将 `${AMP_REMOTE_WRITE}` 手动替换为 `.amp_rw` 的值；`${AWS_REGION}` 替换为变量。
2. 绑定 SA 的最小权限（可沿用集群级 IRSA；如无则直接 `kubectl apply` 运行，不涉及 AWS 写权限）。
3. 应用：
   ```bash
   kubectl apply -f observability/adot-collector.yaml
   ```
4. 在 AMP 中确认有 **最新时间序列**（Grafana 配置见 Day3）。

**产物**：

- `observability/adot-collector.yaml`
- AMP 工作区有新指标（可用 `curl` 验证 remote\_write 200）

**退路**：

若 ADOT 配置反复出错 → 改用 **kube-prometheus-stack** Helm（Operator）一键起 Prom+Grafana（成本稍高，但流程直观），或暂时只走 CloudWatch Container Insights 做截图演示。

### Day 3 - Grafana Dash + SLI/SLO 口径

**做什么**

1. **Grafana Cloud**（或本地 Grafana）：新增 **Prometheus(AMP)** 数据源（SigV4 认证）。
2. 导入一个最小仪表盘：
   - 应用：`http_server_requests_seconds_bucket{app="$APP"}` 计算 **P95**
   - 错误率：`sum(rate(http_server_requests_seconds_count{status=~"5..",app="$APP"}[5m])) / sum(rate(http_server_requests_seconds_count{app="$APP"}[5m]))`
   - 资源：容器 CPU/内存（cAdvisor 指标或 `container_*` 指标）
3. 定义 SLI/SLO：
   - **可用性**：5xx 率 < 0.1%（近 1 天） → SLO=99.9%
   - **延迟**：P95 < 300ms（近 1 天）
     写入 `observability/slo.yaml`（文档式定义）。

**产物**：

- Grafana 仪表盘截图（至少 3 张：P95、错误率、CPU/内存）
- `observability/slo.yaml`（口径 + 阈值）

**退路**：

Grafana Cloud 配置困难 → 使用 **kubectl port-forward** 暂时本地访问 Grafana OSS 服务；或作为替代，导出 AMP 的 `series`/`query_range` 返回 JSON 截图存证。

### Day 4 - Chaos Mesh 安装 + `pod-kill`/`network-latency` 实验

**做什么**

1. 安装（EC2 NodeGroup 支持 **privileged**）：
   ```bash
   helm repo add chaos-mesh https://charts.chaos-mesh.org
   helm install chaos-mesh chaos-mesh/chaos-mesh -n chaos-testing --create-namespace \
     --set chaosDaemon.securityContext.privileged=true
   ```
2. `pod-kill`（30 秒）：`chaos/pod-kill.yaml`
   ```yaml
   apiVersion: chaos-mesh.org/v1alpha1
   kind: PodChaos
   metadata: { name: kill-${APP}, namespace: chaos-testing }
   spec:
     action: pod-kill
     mode: one
     selector:
       namespaces: [${NS}]
       labelSelectors: { app: ${APP} }
     duration: "30s"
   ```
   执行与观测：
   ```bash
   date +%s > .t0 && kubectl apply -f chaos/pod-kill.yaml
   kubectl get pods -n $NS -w   # 观察新 Pod 就绪
   date +%s > .t1 && echo "MTTR=$(( $(cat .t1) - $(cat .t0) ))s"
   ```
3. `network-latency`（100ms\@30s）：
   ```yaml
   apiVersion: chaos-mesh.org/v1alpha1
   kind: NetworkChaos
   metadata: { name: net-lat-${APP}, namespace: chaos-testing }
   spec:
     action: delay
     mode: one
     selector:
       namespaces: [${NS}]
       labelSelectors: { app: ${APP} }
     delay: { latency: "100ms", correlation: "0", jitter: "0ms" }
     duration: "30s"
   ```

   观察 **P95 抬升** 与 **HPA** 是否触发（可用 `hey`/`wrk` 适度压测 1–2 分钟）。

**产物**：

- Chaos Dashboard 成功页面或 `kubectl` 输出截图
- **MTTR 计算**记录（`MTTR=xx s`）
- Grafana 截图（实验窗口内 P95/错误率/副本数变化）

**退路**：

Chaos Mesh 不稳定 → **手动 `kubectl delete pod`** 替代表演自愈；网络延迟改为用 `tc netem` 容器进行最小演示。

### Day 5 - 整理与硬化（配额/限额/告警）

**做什么**

1. 为 `${NS}` 增加 **ResourceQuota** 与 **LimitRange**（控制资源与成本）：

   ```yaml
   apiVersion: v1
   kind: ResourceQuota
   metadata: { name: rq-basic, namespace: ${NS} }
   spec:
     hard: { pods: "6", requests.cpu: "1", requests.memory: "1Gi", limits.cpu: "2", limits.memory: "2Gi" }
   ---
   apiVersion: v1
   kind: LimitRange
   metadata: { name: lr-defaults, namespace: ${NS} }
   spec:
     limits:
     - default: { cpu: "200m", memory: "256Mi" }
       defaultRequest: { cpu: "100m", memory: "128Mi" }
       type: Container
   ```
2. 写 `observability/ALERTS.md`：

   - 错误率 > 0.5%（5 分钟）触发
   - P95 > 500ms（5 分钟）触发
   - 副本数 ≥ 3 且 CPU < 20%（10 分钟）提示过度扩容

   > 可先写“**逻辑告警**”方案，后续 Week 7a 再落 CloudWatch/Grafana 真实告警。
3. 归档 Week 6 产物：图表、YAML、`chaos_report.md`（含实验步骤、观察与结论）。
4. 更新 README「本周指标」：**MTTR**、**P95**、**错误率**、**HPA 触发**截图。

**验收清单**

- [ ] `/actuator/prometheus` 暴露正常
- [ ] ADOT → AMP 已入库（查询到 app 指标）
- [ ] Grafana 出图（P95 / 错误率 / CPU 内存）
- [ ] `pod-kill` 成功、**MTTR ≤ 60s**
- [ ] `network-latency` 生效，图上可见 P95 抬升
- [ ] README 已更新指标与截图

### 20 分钟退路清单

- **AMP 开通受阻** → 临时切 **kube-prometheus-stack**（Prom+Grafana 本地版），保证出图即可；后续再切回 AMP。
- **ADOT 配置故障** → 先只观测 **应用指标**（Prom 本地拉取），集群指标后补。
- **Chaos 安装失败** → 手动删 Pod 模拟；或只保留 `pod-kill` 一项。
- **Grafana 认证/网络问题** → 使用 `kubectl port-forward` 本地访问 Grafana 服务或导出时序 JSON 作为证据。

---

## Week 7 - CI/CD + DevOps

> 目标：把“**提交代码 → 自动构建/测试 → 推镜像 → 发布到 EKS**”贯通，且**无需长期密钥**（GitHub OIDC AssumeRole），保留最小回滚策略与指标留痕。
> 原则：**不上复杂工具就能跑通**；卡顿 > 20 分钟走退路（见每天“退路”）。

### 通用前置

10 分钟完成：

```bash
export AWS_REGION=us-east-1
export CLUSTER=dev
export NS=svc-task
export APP=task-api
export ECR_REPO=task-api
```

产物总表：

- `iam/github-oidc/`（信任策略、权限策略、创建脚本）
- `.github/workflows/ci-cd.yml`（构建、扫描、推送、发布、回滚）
- `deploy/`（Helm values 或 Kustomize/patch 脚本）
- 截图：Actions 运行记录、EKS 滚动发布与回滚、时间指标

### Day 1 - 创建 GitHub OIDC 角色 + EKS RBAC 映射

**做什么**

1. **创建 OIDC 身份提供商（GitHub）**（一次性；若账户已有可跳过）：
   - URL: `https://token.actions.githubusercontent.com`
   - Audience: `sts.amazonaws.com`
2. **创建 IAM 角色**（可命名：`github-actions-renda-lab`）：
   - **信任策略（trust policy）**（限制到你的 GitHub 仓库）：

     ```json
     {
       "Version": "2012-10-17",
       "Statement": [{
         "Effect": "Allow",
         "Principal": { "Federated": "arn:aws:iam::<ACCOUNT_ID>:oidc-provider/token.actions.githubusercontent.com" },
         "Action": "sts:AssumeRoleWithWebIdentity",
         "Condition": {
           "StringEquals": {
             "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
           },
           "StringLike": {
             "token.actions.githubusercontent.com:sub": "repo:<GITHUB_USER_OR_ORG>/<REPO_NAME>:*"
           }
         }
       }]
     }
     ```
   - **权限策略**（最小化，供 ECR/EKS 使用）：
     ```json
     {
       "Version": "2012-10-17",
       "Statement": [
         { "Effect": "Allow", "Action": [
             "ecr:GetAuthorizationToken","ecr:BatchCheckLayerAvailability","ecr:CompleteLayerUpload",
             "ecr:UploadLayerPart","ecr:InitiateLayerUpload","ecr:PutImage","ecr:DescribeRepositories",
             "ecr:ListImages","ecr:CreateRepository"
           ], "Resource": "*" },
         { "Effect": "Allow", "Action": ["eks:DescribeCluster"], "Resource": "*" }
       ]
     }
     ```
3. **把该角色加入 EKS RBAC**（`aws-auth` ConfigMap）：
   ```yaml
   # 追加到 kube-system/ns 下的 aws-auth ConfigMap 的 mapRoles:
   - rolearn: arn:aws:iam::<ACCOUNT_ID>:role/github-actions-renda-lab
     username: github-actions
     groups:
       - system:masters   # 若要更细粒度，可绑定到自定义 ClusterRole
   ```
   > 为减少权限问题，先用 `system:masters` 跑通；Week 8 再细化到最小 RBAC。

**验收**

- 运行：`aws eks update-kubeconfig --name $CLUSTER --region $AWS_REGION` 后，手动 `kubectl auth can-i create deploy -A` 通过。
- README 记录 Role ARN、受信主体（repo）、权限边界。

**退路**

- OIDC 一直卡住 → 临时用 **访问密钥** 创建一个最小权限用户供本周演示（**周末删除**），或改用 **CodeBuild/CodePipeline** 先完成闭环。

### Day 2 - CI：Maven 构建 + 单测 + 镜像构建/扫描/推送

**做什么**

1. 在仓库根创建 `.github/workflows/ci-cd.yml`：**CI 作业**
   ```yaml
   name: CI-CD
   on:
     push:
       branches: [ "main" ]
     workflow_dispatch: {}
   env:
     AWS_REGION: us-east-1
     ECR_REPO: task-api
     IMAGE_TAG: ${{ github.sha }}
     CLUSTER: dev
     NS: svc-task
     APP: task-api

   jobs:
     ci:
       runs-on: ubuntu-latest
       permissions:
         id-token: write   # OIDC
         contents: read
       steps:
         - uses: actions/checkout@v4

         - name: Set up JDK 21
           uses: actions/setup-java@v4
           with:
             distribution: temurin
             java-version: '21'
             cache: 'maven'

         - name: Build & Test (Maven)
           run: mvn -B -DskipTests=false clean test package

         - name: Configure AWS (OIDC)
           uses: aws-actions/configure-aws-credentials@v4
           with:
             role-to-assume: arn:aws:iam::<ACCOUNT_ID>:role/github-actions-renda-lab
             aws-region: ${{ env.AWS_REGION }}

         - name: Login to ECR
           uses: aws-actions/amazon-ecr-login@v2

         - name: Build image
           run: |
             ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
             IMAGE="$ACCOUNT_ID.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${IMAGE_TAG}"
             docker build -t "$IMAGE" ./task-api
             echo "IMAGE=$IMAGE" >> $GITHUB_ENV

         - name: Trivy scan (High/Critical only, fail on critical)
           uses: aquasecurity/trivy-action@0.20.0
           with:
             image-ref: ${{ env.IMAGE }}
             severity: HIGH,CRITICAL
             exit-code: '1'
             ignore-unfixed: true

         - name: Push image
           run: docker push "${IMAGE}"
   ```
2. 推一次代码，确保 **CI 绿**；记录**构建时长**。

**验收**

- CI 通过；ECR 出现 `${IMAGE_TAG}` 镜像。
- README 增加“构建成功/日期/构建时长”。

**退路**

- Trivy 扫描阻塞（误报或基础镜像问题）→ 先把 **`exit-code: '0'`**，记录 issue，后续换基础镜像再严格把关。

### Day 3 - CD：发布到 EKS（滚动更新）+ 策略参数

**做什么**

在同一个 `ci-cd.yml` 里新增 **cd 作业**（依赖 ci）：

```yaml
  cd:
    needs: [ci]
    runs-on: ubuntu-latest
    permissions:
      id-token: write
      contents: read
    steps:
      - uses: actions/checkout@v4

      - name: Configure AWS (OIDC)
        uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: arn:aws:iam::<ACCOUNT_ID>:role/github-actions-renda-lab
          aws-region: ${{ env.AWS_REGION }}

      - name: Update kubeconfig
        run: aws eks update-kubeconfig --name $CLUSTER --region $AWS_REGION

      - name: Patch image & apply (kubectl)
        run: |
          ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
          IMAGE="$ACCOUNT_ID.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${IMAGE_TAG}"
          # 用 yq/kustomize/helm 皆可；这里示例直接 kubectl set image：
          kubectl -n $NS set image deploy/${APP} ${APP}=$IMAGE
          kubectl -n $NS annotate deploy/${APP} deploy.kubernetes.io/revision-keep='5' --overwrite
          kubectl -n $NS rollout status deploy/${APP} --timeout=180s

      - name: Smoke test via Ingress
        run: |
          ALB=$(kubectl -n $NS get ing -o jsonpath='{.items[0].status.loadBalancer.ingress[0].hostname}')
          curl -sSf "http://$ALB/healthz" | grep -i ok
```

**建议在 Deployment 增加滚动策略（一次性改 YAML）**：

```yaml
spec:
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  progressDeadlineSeconds: 180
```

**验收**

- Actions 中 `cd` 任务成功；`rollout status` 成功。
- 记录 **Lead Time**（从 push 到服务可用的时长）。

**退路**

- `rollout` 超时 → 暂把副本数设为 1、放宽 `readinessProbe`，先保证闭环；晚些再调优健康检查。

### Day 4 - 回滚与手动触发；参数化环境

**做什么**

1. 在 `ci-cd.yml` 追加一个**手动回滚工作流**（`workflow_dispatch` 接收 `imageTag` 参数）：

```yaml
  rollback:
    if: ${{ github.event_name == 'workflow_dispatch' }}
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: arn:aws:iam::<ACCOUNT_ID>:role/github-actions-renda-lab
          aws-region: ${{ env.AWS_REGION }}
      - name: Update kubeconfig
        run: aws eks update-kubeconfig --name $CLUSTER --region $AWS_REGION
      - name: Rollout undo (to previous)
        run: kubectl -n $NS rollout undo deploy/${APP}
```

> 如需指定镜像版本回滚，可加输入参数 `imageTag`，再用 `kubectl set image`。

2. **环境防护**：给 `cd` 作业加 `environment: dev`，并在 GitHub 仓库里为 `dev` 环境开启**手动批准**（防误发）。
3. 记录“回滚成功截图/命令”。

**验收**

- `rollback` 任务手动触发可成功回滚；`rollout history` 可见修订列表。

**退路**

- 回滚失败 → 直接 `kubectl set image` 指到上一个 tag（ECR 列表里复制），然后 `rollout status`。

### Day 5 - 指标留痕 + 文档固化 + 清理脚本

**做什么**

1. **指标**：
   - 统计近 1 周 **发布成功率**（成功/总次数）
   - 统计 **平均构建时长**、**平均 Lead Time**
   - 追加到 README “本周指标”区，并生成 `progress.png`（表格/截图皆可，简单即可）
2. **文档**：
   - 在 README “部署方式”下新增 “**CI/CD（GitHub Actions + OIDC）**” 小节：写清“如何授权/如何触发/如何回滚”
   - 把 OIDC 角色、`aws-auth` 映射、最小权限策略放入 `iam/github-oidc/README.md`（便于面试展示）
3. **脚本**：
   - `deploy/redeploy.sh`（用于本地复现与演示）
   - 确保 `cleanup.sh` 不误删 ECR 里用于回滚的 1～2 个最近 tag（保留 2～5 个）

**验收**

- README “CI/CD + 指标” 小节完善；Actions 截图已归档。
- 一键 `destroy` 后，README 明确“**如何在下次重建后恢复 CI/CD**”（通常只需：重建集群 → 更新 kubeconfig → `aws-auth` 映射 → 立即可发版）。

**退路**

- 指标获取麻烦 → 先人工统计（Actions 运行页复制数据），后续有需要再接入 GitHub API 脚本。

### 20 分钟退路总表

- **OIDC 配置耗时** → 临时用访问密钥用户跑通本周，**周末删除**；或切 **CodeBuild/CodePipeline**（只跑 ECR 推送 + `kubectl` 发布）
- **Trivy 阻塞** → 先放宽为警告模式，记 issue；不阻断主线
- **`kubectl` 权限问题** → 回到 Day1，确认 `aws-auth` 已添加 `rolearn`；或先将角色加入 `system:masters` 再慢慢收敛
- **发布失败** → 使用 `rollout undo` 或“指定 tag”回滚；务必记录“失败原因 + 改进项”两行文字

---

## Week 8 — Mock + Résumé

> 目标：把前 3 周产出**打包为“面试可讲、可量化”的成果**，并通过两次全英文 Mock 找出最后的短板；同时完成简历 v2 与一键销毁/重建收官。
> 原则：**最小可展示** > 形式主义；当天卡顿 > 20 分钟走退路方案。

### 通用前置

10 分钟 完成：

```bash
export NS=svc-task
export APP=task-api
export CLUSTER=dev
```

本周产物目录建议：

```
docs/
  interview/
    qbank.md           # 高频问&标准要点
    star_stories.md    # STAR 素材库（每条 ≤ 6 行）
    mock1_scorecard.md # 打分与改进项
    mock2_scorecard.md
  architecture/
    system-onepager.md # 一页纸架构说明
    diagrams/          # 架构图(png/svg)
  resume/
    resume_cn_v2.pdf
    resume_en_v2.pdf
  metrics/
    progress.png       # 指标折线
cleanup/
  day8_runbook.md
  cleanup.sh
```

## Day 1 - 成果打包 & 架构一页纸（One-Pager）

**做什么**

1. **仓库整理**：统一命名、删无用分支/文件、补 `.gitignore`。
2. **架构一页纸**（`docs/architecture/system-onepager.md`）：

   - 背景/目标（3 行）
   - 架构图（EKS NodeGroup + ALB + IRSA + S3/AMP/Grafana + CI/CD）
   - 流程：Dev → CI → ECR → CD → EKS → ALB → 用户；应用 → ADOT → AMP → Grafana
   - SLO 口径：99.9% 可用、P95<300ms、错误率<0.1%
   - 成本策略：单集群多 NS、Spot、采样/过滤、Budget
3. **演示脚本**：`demo/start.sh` / `demo/stop.sh` 最终检查（Week5 已有的基础上补充观测/Chaos 一键启动/关闭）。
4. **指标折线**：把 Week5–7 的关键指标整理进 `docs/metrics/progress.png`（可先用表格截图代替）。

**产物**

- `system-onepager.md` + 架构图
- `demo/*` 脚本可运行
- `progress.png`（或临时表格/截图）

**退路（20 分钟原则）**

- 架构图工具卡顿 → 先画手稿拍照上传；后补电子版
- `progress.png` 不会画 → 用 README 表格 + 截图替代

## Day 2 - Mock-1（全英）：运行/韧性/观测深挖

**主题**：你是资深 Java/Cloud 候选人，面试官围绕 **运行稳定性** 追问（EKS、HPA、Chaos、SLO、日志指标）。
**流程（90 分钟自测/录制）**

1. **Warm-up 10′**：英文自我介绍（30–45 秒 Elevator Pitch）
2. **Tech Q\&A 60′**（录音/录像，问题示例写入 `docs/interview/qbank.md`）：

   - 如何把 Spring Boot 上到 EKS？（对象/Ingress/HPA/探针/无状态）
   - IRSA 最小权限如何配置？为什么不用长期密钥？
   - 观测链路：ADOT → AMP → Grafana；如何控制成本？
   - Chaos 场景：`pod-kill` MTTR 如何测，如何缩短？
   - SLO 设计：99.9% 与错误预算的关系？
3. **Hands-on 10′**：现场滚动发布 `kubectl set image` 并观察 `rollout status`
4. **Debrief 10′**：自我打分并写入 `docs/interview/mock1_scorecard.md`（得分+3 个改进项）

**产物**

- 90′ 录音/录像（可选）
- `mock1_scorecard.md`（含“下一步改进”）
- `qbank.md` 新增 8–12 个高频问要点答案（用要点，不写大段）

**退路**

- 无法录制 → 用语音备忘或记要点
- 现场失败 → 用预先录像的发布演示作为替代（保证面试可讲）

## Day 3 - 简历 v2（中/英）+ LinkedIn 打磨

**做什么**

1. **简历框架**（一页，项目置顶）：
   - **Cloud DevOps Lab（2025）** — 3–5 条量化要点：
     - 例：*“构建 **CI/CD（GitHub OIDC）**，从提交到上线 **Lead Time ≤ 8 min**；引入 **Trivy** 阶段门拦截高危镜像。”*
     - 例：*“在 **EKS NodeGroup** 上落地 **HPA+PDB**，`pod-kill` 实验 **MTTR 49s**；SLO 99.9%。”*
     - 例：*“ADOT→AMP→Grafana：**Trace/Metric 覆盖 ≥ 95%**；`sample_limit`+relabel 降低指标写入 **50%+**。”*
     - 例：*“IRSA 最小权限访问 **S3**，清退长期密钥；**Day-8 Cleanup** 一键销毁，月固定成本 ≈ \$0.5（Route53）。”*
   - 技术栈关键字：Java 21 / Spring Boot / Docker / K8s / EKS / Helm / Terraform / GitHub Actions / OIDC / AMP / Grafana / Chaos Mesh / AWS（ALB、ECR、S3、CloudWatch）
2. **中文/英文版**各出一份 PDF（文件名：`Renda_Zhang_Java_Cloud_2025_CN.pdf` / `..._EN.pdf`）。
3. **LinkedIn/拉钩/猎聘**短版简介（3 行内）：

   - *“Java 后端 + 云原生（EKS）— 搭建 CI/CD 与观测闭环，IRSA 最小权限与成本优化，面向外企中高级岗位。”*
4. 在 `docs/resume/` 保存源文件与 PDF；README 顶部加“**Résumé v2**”链接。

**产物**

- 简历 v2（中/英）PDF
- LinkedIn 简介文案（贴到个人主页）

**退路**

- 量化指标缺数字 → 先写“目标/现状”，后补精确值；或填区间/近似值（例如“≈50%”）

### Day 4 - Mock-2（全英）：AI + 多云系统设计

**主题**：面试官给场景：*“构建一个面向 10k QPS 的任务管理服务，支持文本智能摘要（Bedrock Titan），可移植到其他云。”*
**流程（90 分钟自测/录制）**

1. **白板/画图 25′**：画出 **API Gateway/ALB → EKS → Service → Bedrock**；缓存/熔断/重试；队列（SQS 可选）；多 AZ；IaC 与 GitHub OIDC。
2. **权衡 20′**：成本 vs 延迟、Fargate vs NodeGroup、AMP vs 自建 Prom、GitOps vs 直推。
3. **扩展问题 30′**：
   - 限流与回放（令牌桶/漏桶 + SQS）
   - 隐私与密钥（IRSA、KMS、Parameter Store）
   - 可移植性（Terraform provider/Helm values，可迁移到 GKE）
4. **收尾 15′**：把答案精炼成 `docs/architecture/design-qa.md`；更新一张“对比表”。

**产物**

- 架构图（png/svg）
- `design-qa.md`（10–15 个要点 Q/A）
- `mock2_scorecard.md`（得分 + 改进项）

**退路**

- 画图耗时 → 用文字列表 + 手绘照片替代
- 对比点拿不准 → 写“权衡框架”而非定论（比如“功能/成本/复杂度/可移植性”四象限）

### Day 5 - Day-8 Cleanup 收官 + 招聘投递包

**做什么**

1. **最终 cleanup**（`cleanup/day8_runbook.md`）：
   - 顺序：暂停 Actions → 导出指标/截图 → `demo/stop.sh` → `terraform destroy` → `eksctl delete cluster` → 清理 ALB/TG/ECR 冷镜像/S3/DynamoDB/Budgets（保留 Route53）
   - 验证：**账单面板 0 新增固定支出**（除 Route53）
2. **重建演练（抽查）**：半小时重建一次，验证 README“快速开始”无缺页。
3. **招聘投递包**：
   - 简历 v2（中/英）+ README 项目亮点 + 架构一页纸 + 两段 Mock 结论
   - `docs/interview/star_stories.md` 补齐 6–8 条 STAR（故障自愈 / 成本优化 / CI/CD 整改 / 权限收敛 / 性能压测 / 团队协作）。
4. **计划交接**：在 README 顶部“**下一阶段建议**”留 3 条路线（如：Redis 缓存、金丝雀/蓝绿对比、Bedrock 成本配额自动化）。

**产物**

- `cleanup/*` 运行记录 + 账单截图
- 一次“重建成功”截图（≤30 分钟）
- 招聘投递包压缩包（私用）

**退路**

- `destroy` 卡资源依赖 → 单资源控制台删除，记录在 `day8_runbook.md` 里
- 重建超时 → 记录卡点与命令输出，后续安排半天专修（不占本周）

### 高频问题库

详情查看文档：[高频问题库](./docs/interview/QBANK.md)
