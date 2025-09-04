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
    - [Day 1：应用骨架 + Docker 镜像 + 推送 ECR](#day-1%E5%BA%94%E7%94%A8%E9%AA%A8%E6%9E%B6--docker-%E9%95%9C%E5%83%8F--%E6%8E%A8%E9%80%81-ecr)
    - [Day 2 + Day 3：K8s 基础对象、ALB 暴露、HPA 弹性](#day-2--day-3k8s-%E5%9F%BA%E7%A1%80%E5%AF%B9%E8%B1%A1alb-%E6%9A%B4%E9%9C%B2hpa-%E5%BC%B9%E6%80%A7)
    - [Day 4：S3 最小接入 + IRSA](#day-4s3-%E6%9C%80%E5%B0%8F%E6%8E%A5%E5%85%A5--irsa)
    - [Day 5 - 收尾硬化 + 文档化 + 指标留痕](#day-5---%E6%94%B6%E5%B0%BE%E7%A1%AC%E5%8C%96--%E6%96%87%E6%A1%A3%E5%8C%96--%E6%8C%87%E6%A0%87%E7%95%99%E7%97%95)
    - [Week 5 - 总复盘](#week-5---%E6%80%BB%E5%A4%8D%E7%9B%98)
      - [本周完成的闭环（从源码到公网与云资源）](#%E6%9C%AC%E5%91%A8%E5%AE%8C%E6%88%90%E7%9A%84%E9%97%AD%E7%8E%AF%E4%BB%8E%E6%BA%90%E7%A0%81%E5%88%B0%E5%85%AC%E7%BD%91%E4%B8%8E%E4%BA%91%E8%B5%84%E6%BA%90)
      - [关键证据与指标（本周留痕）](#%E5%85%B3%E9%94%AE%E8%AF%81%E6%8D%AE%E4%B8%8E%E6%8C%87%E6%A0%87%E6%9C%AC%E5%91%A8%E7%95%99%E7%97%95)
      - [本周经验与坑位](#%E6%9C%AC%E5%91%A8%E7%BB%8F%E9%AA%8C%E4%B8%8E%E5%9D%91%E4%BD%8D)
      - [一分钟复述（面试版本）](#%E4%B8%80%E5%88%86%E9%92%9F%E5%A4%8D%E8%BF%B0%E9%9D%A2%E8%AF%95%E7%89%88%E6%9C%AC)
  - [Week 6 - 可观测性 & 韧性（Observability + Resilience）](#week-6---%E5%8F%AF%E8%A7%82%E6%B5%8B%E6%80%A7--%E9%9F%A7%E6%80%A7observability--resilience)
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

> 完成 Week 5 后，即可在面试里完整叙述“**Java 微服务 → 容器化 → EKS 部署 → 通过 ALB 暴露 → S3 集成**”的闭环。

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

### Day 1：应用骨架 + Docker 镜像 + 推送 ECR

今天做了什么（Done）

- 起了一个最小 **Spring Boot 3 + Actuator** 服务（`/api/hello`、`/api/ping` 与 `/actuator/health{,/liveness,/readiness}`）。
- 编写 **多阶段 Dockerfile**，本地构建镜像并推送到 **Amazon ECR（us-east-1）**。
- 使用 **digest** 固定镜像版本：`sha256:927d20ca4cebedc14f81770e8e5e49259684723ba65b76e7c59f3003cc9a9741`。
- 在 **EKS 集群 `dev` / 命名空间 `svc-task`** 部署 `Deployment+Service(ClusterIP)`，通过 `kubectl port-forward` 完成端到端验证（业务接口与健康探针均返回 `UP`）。

关键决策与记录

- **Region / Cluster / Repo**：`us-east-1` / `dev` / `task-api`。
- **AWS 身份**：采用 **SSO Profile `phase2-sso`**；脚本已内置 `--profile` 支持。
- **镜像标记策略**：推送 `:0.1.0` 与 `:latest`，**部署用 digest 锁定**（避免 tag 漂移）。
- **ECR 生命周期策略**：当前设置“仅保留 1 个 tag + 未打标签 1 天过期”→ 成本低但**回滚空间极小**；建议后续调整为**至少保留最近 5–10 个 tag**。

### Day 2 + Day 3：K8s 基础对象、ALB 暴露、HPA 弹性

今天做了什么（Done）

1. **K8s 基础对象规范化（k8s/base）**
   - `Namespace`：`svc-task`
   - `ServiceAccount`：`task-api`（预留 IRSA 注解位，Day 4 再绑定 IAM Role）
   - `ConfigMap`：`task-api-config`（示例：`APP_NAME` / `WELCOME_MSG`）
   - `Deployment + Service(ClusterIP)`：
     - 镜像以 **ECR Digest** 固定（避免 tag 漂移）。
     - 配置 `readiness/liveness` → `/actuator/health/{readiness,liveness}`。
     - 资源水位：`requests: cpu 100m / mem 128Mi`，`limits: cpu 500m / mem 512Mi`。
2. **AWS Load Balancer Controller（ALBC）上线**
   - **IRSA** 用 Terraform 创建：IAM Policy + Role + 信任策略 + `kube-system/aws-load-balancer-controller` SA 注解。
   - **控制器本体**用 Helm 写进 `post-recreate.sh`：
     - 固定 `chart` 与 `image.tag`；
     - 升级前显式 `kubectl apply -k .../config/crd?ref=<controller-version>` 处理 CRDs；
     - `serviceAccount.create=false`（复用 TF 管理的 SA）；
     - `rollout status` 等待就绪并打印末尾日志。
3. **Ingress → 公网 ALB**
   - `ingressClassName: alb`，关键注解：
     - `alb.ingress.kubernetes.io/scheme: internet-facing`
     - `alb.ingress.kubernetes.io/target-type: ip`
     - `alb.ingress.kubernetes.io/healthcheck-path: /actuator/health/readiness`
     - `alb.ingress.kubernetes.io/healthcheck-port: traffic-port`
   - ALB DNS 已分配；根路径无资源返回 `404`，健康检查 `UP`，**公网可达**验证通过。
4. **弹性扩缩（HPA, autoscaling/v2）**
   - 目标：CPU **60%**，`min=2 / max=10`，配置了 `behavior` 的放大/回落策略。
   - 安装/确认 `metrics-server`（Helm，`--kubelet-insecure-tls` 以增强兼容性）。
   - **压测**（集群内流量，直打 ClusterIP）：
     - 内网域名：`http://task-api.svc-task.svc.cluster.local:8080/...`
     - 使用镜像：`williamyeh/hey:latest`
     - 命令示例：
       ```bash
       kubectl -n svc-task run hey --image=williamyeh/hey:latest --restart=Never -- \
         -z 2m -c 50 -q 0 "http://task-api.svc-task.svc.cluster.local:8080/api/hello?name=HPA"
       ```
     - 观察结果：`cpu: 496%/60%`，`Pods: 2 → 8`；压测结束后约 1 分钟回落到 `cpu: 2%/60%` 与 `Pods: 2`。
5. **每日重建脚本已更新**
   - `post-recreate.sh` 已集成：ALBC 安装/升级（含 CRDs）、Ingress 发布与等待、metrics-server 安装、HPA 发布、冒烟验证。
   - 与 `infra/aws` 的 Terraform 模块对齐（IRSA 在 HCL，控制器在脚本）。

> 环境锚点：`AWS_REGION=us-east-1`、`CLUSTER=dev`、`NS=svc-task`、`ECR_REPO=task-api`。
>
> 说明：ECR 仍采用 **digest 部署**；ECR 生命周期目前“93.73 MB 的镜像 + 只保留 1 个 tag + 1 天清理 untagged”，可节省成本但**回滚空间非常小**，短期可接受，后面若要更稳妥，可以把“最近 1 个”调成“最近 2–5 个”。

### Day 4：S3 最小接入 + IRSA

**今天做了什么**

- 为 `svc-task/task-api` 建立 **IRSA**：创建最小权限的 IAM Role（信任 EKS OIDC，仅允许 `s3:ListBucket` 受 `prefix` 约束，以及对 `<bucket>/<app-prefix>/*` 的 `GetObject/PutObject`），并把 Role 绑定到该 ServiceAccount 的 `eks.amazonaws.com/role-arn` 注解。
- **最小闭环验证**：在集群内以该 SA 运行一次性 **aws-cli Job**，完成 `sts get-caller-identity`、对**允许前缀**的 Put/List/Get 成功、对**不允许前缀**写入触发 AccessDenied，验证“最小权限”生效。
- **成本/安全护栏**：为 S3 配置 Gateway Endpoint（私网访问不经 NAT）、Bucket 默认加密 + 强制 TLS、对 `smoke/` 前缀设置保留期生命周期清理。

### Day 5 - 收尾硬化 + 文档化 + 指标留痕

**今天做了什么（DONE）**

- 为 `task-api` 增加 **PodDisruptionBudget**（保持最少 1 个可用副本；示例片段见下），资源 Requests/Limits 已在前文配置可维持不变。
- 记录 HPA 扩缩容、冷启动大致时延、以及“本周部署成功次数/尝试次数”；补一条 **STAR 一句话** 作为面试素材。

### Week 5 - 总复盘

#### 本周完成的闭环（从源码到公网与云资源）

- **应用与镜像**
  - 最小 **Spring Boot 3 + Actuator** 服务（`/api/hello`、`/actuator/health{/,readiness,liveness}`）。
  - 多阶段 **Docker 构建**，产物体积约 **93.73 MB**；推送到 **ECR** 并用 **digest** 部署（已固定：`sha256:927d20ca4ce...`）。
- **集群内“站稳”**
  - `Namespace=svc-task`，`ServiceAccount=task-api`，`ConfigMap` 注入基础配置。
  - `Deployment + Service(ClusterIP)`，探针接入 Actuator；资源基线 `requests: 100m/128Mi`，`limits: 500m/512Mi`。
- **对外暴露（ALB）**
  - **AWS Load Balancer Controller**：IRSA 用 Terraform 管理，控制器用 Helm 写入 `post-recreate.sh`（含 CRDs 升级与就绪等待）。
  - `Ingress`→ 公网 **ALB**；健康检查走 `/actuator/health/readiness`；ALB DNS 可达。
- **弹性与稳定性**
  - **metrics-server** 安装，**HPA（CPU=60%，min=2,max=10）** 生效：压测时 `2 → 8` 扩容，结束后自动回落到 `2`。
  - **PDB（minAvailable=1）** 就位，保障自愿中断期间至少 1 个可用副本。
- **无密钥访问云资源（IRSA + S3）**
  - 以 **IRSA** 绑定 `task-api` SA 到最小权限 **IAM Role**：仅允许 `GetObject/PutObject` 到 `s3://dev-task-api-welcomed-anteater/task-api/*`，`ListBucket` 仅限该前缀。
  - 集群内 **aws-cli Job** 冒烟：允许前缀读写成功；越权前缀写入 **AccessDenied**（最小权限有效）。
  - **S3 Gateway Endpoint** 上线（私网访问不经 NAT），Bucket 默认 **SSE-S3** + **强制 TLS**；仅对 `smoke/` 前缀启用生命周期清理。
- **自动化与可复现**
  - **`post-recreate.sh`** 已集成：ALBC 安装/升级（含 CRDs）、业务清单发布、Ingress 发布与等待、metrics-server/HPA、SA 注解与 S3 冒烟。
  - 集群基础由 **Terraform** 管理；**ECR** 按目前策略**保留**（不随日常销毁），其余资源可一键 `make start-all / stop-all` 循环复现。

#### 关键证据与指标（本周留痕）

- **访问入口**：ALB DNS 正常对外服务；**镜像**以 `…@sha256` 形式部署。
- **Time-to-Ready（TTR）≈ 27s**（删除单 Pod → 新 Pod Ready）。
- **HPA 触发记录**：负载时 CPU `≈496%/60%`，副本 `2 → 8`；回落时 CPU `≈2%/60%`，副本 `8 → 2`。
- **PDB**：`DisruptionsAllowed=1`（在 2 副本基线上满足逐个中断）。
- **IRSA 冒烟**：`sts get-caller-identity` 成功；允许前缀读写 OK、越权前缀 AccessDenied。

#### 本周经验与坑位

- **Digest 固定** 避免 tag 漂移；将 ECR 生命周期设为“保留 1 个 tag”，**成本极低**但**回滚弹性小**（目前可接受，后续建议保留 2–5 个 tag）。
- **kubeconfig 时序/证书**：Terraform 创建 SA 时曾遇到与 API 服务器 TLS 握手超时；通过 `aws eks update-kubeconfig` 刷新后恢复。将 **SA 创建迁至脚本层**，规避了 Provider 与控制面就绪的竞态。
- **ALB 常见卡点**（已规避）：子网标签、Controller 就绪、探针路径/延时参数。
- **成本意识**：S3 网关端点让私网流量绕过 NAT，降低账单暴露面。

#### 一分钟复述（面试版本）

> “这一周我把一个 Spring Boot 服务从源码打包到 ECR，用 digest 固定部署到 EKS；通过 ALB Ingress 对外提供服务，并用 HPA（CPU60%）验证 `2→8→2` 的自动扩缩。同时用 IRSA 让 Pod 无密钥访问 S3 的指定前缀，集群内 aws-cli 冒烟验证允许前缀 OK、越权拒绝；S3 开启默认加密与强制 TLS，并通过 VPC Gateway Endpoint 降低 NAT 成本。全链路写进 `post-recreate.sh` 与 Terraform，每日可一键重建/销毁。TTR 约 27 秒，PDB 确保自愿中断不中断业务。”

---

## Week 6 - 可观测性 & 韧性（Observability + Resilience）

目标：

1. **应用可观测入口就绪**：为 `task-api` 启用 **Spring Boot Actuator + Prometheus** 暴露端点（`/actuator/prometheus`）。
2. **观测链路最小落地**：
   - **Managed 模式**：**ADOT Collector → Amazon Managed Prometheus (AMP)**，Grafana 可自建/云托管，用于面试展示“上云观测链路”。
3. **SLI/SLO 成形**：定义并出图至少 3 个 SLI（建议：**可用性、P95 延迟、错误率**），明确计算公式与目标 SLO（示例：可用性 ≥ 99%，P95 < 300 ms，错误率 < 1%）。
4. **韧性演示（Chaos）**：用 **Chaos Mesh** 进行 `pod-kill` 与 `network-latency(≈100ms/30s)` 小实验，观察 **P95 抬升**与 **HPA 触发**，度量 **MTTR**（目标 ≤ 1 分钟，作为面试谈资与改进参考）。
5. **面试物料输出**：沉淀一页式复盘（指标口径、图表截图、实验结论、MTTR 计算）+ 目录化产物，确保“可复制、可演示、可讲故事”。

原则：

- **成本优先**：凡产生云费用的资源（如 AMP/Grafana 托管）**全部纳入每日销毁/重建流程**。
- **不过度工程**：以“**先出图、可验证、可讲清**”为准则；能跑通与可复现优先于完美架构。
- **承接 Week 5**：最大化复用既有 **EKS 集群、命名空间 `svc-task`、服务 `task-api`** 等内容与脚本。
- **20 分钟退路**：任一环节超 20 分钟未通，立刻启用退路：`port-forward` 本地看图、用“手动删 Pod”替代复杂混沌场景，确保**周目标不失焦**。
- **面试导向**：每一步都能输出“**做了什么 → 为什么 → 结果如何**”的证据链（命令、截图、公式、结论）。

### 通用前置

> 建议在 Week 6 开始前一次性完成，后续各日共用。

**环境变量文件**：在仓库根目录新建 `.env.week6.local`

```bash
# 区域/命名
AWS_REGION=us-east-1
NS=svc-task
APP=task-api

# 观测/混沌命名空间（可按需调整）
PROM_NAMESPACE=observability
CHAOS_NS=chaos-testing

# AMP 配置（ID 在创建后回填）
AMP_ALIAS=amp-rcl-o11y-wk6-use1
AMP_WORKSPACE_ID=
```

### Day 1 - 应用指标暴露 + AMP 工作区

今天做了什么（Done）

- 新增本地 env 文件：`.env.week6.local`（被 `.gitignore` 忽略，便于本地 CLI 读写）。
  - 关键变量：`AWS_REGION=us-east-1`、`NS=svc-task`、`APP=task-api`、`PROM_NAMESPACE=observability`、`CHAOS_NS=chaos-testing`。
  - AMP 别名采用专业命名：`AMP_ALIAS=amp-renda-cloud-lab-wk6-use1`。
  - 本周直接使用 AMP（托管 Prometheus）。
- 更新 `task-api`：
  - `pom.xml` 增加 `micrometer-registry-prometheus` 依赖（在已有 Actuator 基础上补齐）。
  - `application.yml` 扩展暴露：`health,info,metrics,prometheus`；开启 `http.server.requests` 直方图（便于 P95）。
- 本地验证成功：`/actuator/prometheus` 返回 `# HELP`/`# TYPE` 指标文本。
- 在 `us-east-1` **成功创建** AMP Workspace：
  - `AMP_WORKSPACE_ID=ws-4c9b04d5-5e49-415e-90ef-747450304dca`
  - `remote_write=https://aps-workspaces.us-east-1.amazonaws.com/workspaces/ws-4c9b04d5-5e49-415e-90ef-747450304dca/api/v1/remote_write`
- 已写回 `AMP_WORKSPACE_ID` 到 `.env.week6.local` 以便后续使用。
- 基于最新 `task-api` 代码构建并推送 ECR：
  - `VERSION=0.1.0-2508272044`
  - `DIGEST=sha256:d409d3f8925d75544f34edf9f0dbf8d772866b27609ef01826e1467fee52170a`
- 滚动更新 Deployment 到 **digest**，`rollout` 成功；集群内冒烟验证 `/actuator/prometheus` 通过。
- 从集群内对 AMP `remote_write` 做 POST 探测：得到 **HTTP 403（预期）** → 说明 **DNS/TLS/出网正常**，只是尚未做 SigV4 认证（明天由 ADOT+IRSA 解决）。
- 读取集群与认证前置：
  - `EKS cluster: dev`
  - `OIDC issuer: https://oidc.eks.us-east-1.amazonaws.com/id/4A580B5B467656AA8A2E18C0238FBC3A`
  - `IAM OIDC provider: PRESENT`（已存在）
  - `AmazonPrometheusRemoteWriteAccess` ARN 获取成功。

今日的关键决策：

- **AMP 不纳入每日销毁/重建**：为保留多天历史曲线（面试材料更完整），采用“Workspace 持久 + 采集器按需开关”的策略；可以待结束后统一清理。
- 每日流程脚本不读取 `.env.week6.local`。

### Day 2 - ADOT Collector（采集 → AMP）+ 成本护栏

今日回顾：

- 在 `observability` 命名空间部署 **ADOT Collector（Deployment）**，只采集 `task-api` 的 `/actuator/prometheus`。
- 通过 **IRSA** 赋予最小权限（`AmazonPrometheusRemoteWriteAccess`），以 **SigV4** 将指标写入 **AMP**。
- 加上**成本护栏**（采集范围/频率与指标白名单），并完成端到端验证与脚本整合。

关键变更与配置：

- **IRSA（Terraform 模块化）**
  - IAM Role：`arn:aws:iam::563149051155:role/adot-collector`（信任策略包含 `aud=sts.amazonaws.com` 与 `sub=system:serviceaccount:observability:adot-collector`）。
- **Helm 部署 ADOT Collector**（OpenTelemetry 官方 Chart + **ADOT 镜像**）
  - K8s ServiceAccount：`observability/adot-collector`，**由 Helm 管理**，并带注解 `eks.amazonaws.com/role-arn` 指向上述 Role。
  - Release：`adot-collector`，Deployment 名：`adot-collector-opentelemetry-collector`。
  - 使用 **完整 FQDN** 作为采集目标；示例：`task-api.svc-task.svc.cluster.local:8080/actuator/prometheus`。
  - **SigV4**：`extensions.sigv4auth.region=us-east-1`，`exporters.prometheusremotewrite.auth.authenticator=sigv4auth`。
  - **成本护栏**：
    - `scrape_interval: 30s`，`scrape_timeout: 10s`，`sample_limit: 5000`；
    - `metric_relabel_configs` **只保留** `http_server_requests_seconds_(bucket|sum|count)` 与 `jvm_memory_used_bytes`；
    - 暂不启用 exporter 的发送队列/重试（后续按需加）。
  - **Telemetry 日志级别**：回调到 `info`（生产友好）。
- **远端写入**：AMP `remote_write` 指向 Day 1 创建的 Workspace（`ws-4c9b04d5-...`，区域 `us-east-1`）。

验收与证据：

- 部署：`kubectl -n observability rollout status deploy/adot-collector-opentelemetry-collector` → **successfully rolled out**。
- 业务流量：用 `curlimages/curl` 对 `/api/hello` 与 `/actuator/health` 连续发请求，触发 `http_server_requests_*` 增长。
- 成功写入验证：
  - 由于 ADOT 的 `prometheusremotewrite` **成功发送默认不打印 2xx 日志**，采用 **port-forward + 自监控指标** 验证：
    - `curl localhost:8888/metrics | grep otelcol_exporter_sent_metric_points{exporter="prometheusremotewrite"}`
    - 计数 **> 0**，表明已成功向 AMP 发送样本。
- 脚本整合：已将 **Collector 安装/卸载** 纳入 `post-recreate.sh` 与 `pre-teardown.sh`（与“每日重建/销毁”流程对齐）。

今日决策与取舍：

- **由 Helm 管理 ServiceAccount**。
- **维持最小采集面**（仅应用指标），先确保“能写入、能出图”，后续再按需扩展 kube-state / node 指标。
- **发送队列/重试**暂不启用，待稳定运行后再视需要加固。
- **成本控制**优先：采样频率、白名单指标、限制标签基数（`method/status/outcome` 为主）。

### Day 3 - Grafana Dash + SLI/SLO 口径

今日回顾

- 在集群内用 **Helm** 部署 **Grafana OSS**，通过 **IRSA + SigV4** 连接 **AMP**。
- 用 **最小仪表盘**（QPS / 错误率 / P95）把数据“出图”。
- 将 Grafana 的安装与仪表盘 **纳入每日重建/销毁流程**，做到一键复原。

关键实现

- **身份与权限**
  - 通过 **Terraform** 创建 **IRSA Role**：`arn:aws:iam::563149051155:role/grafana-amp-query`，仅附 `AmazonPrometheusQueryAccess`。
  - Helm 让 **ServiceAccount: `observability/grafana`** 随部署创建，并在 SA 上添加注解 `eks.amazonaws.com/role-arn=<上面 Role>`。
  - Grafana 以环境变量开启 SigV4 支持：`GF_AUTH_SIGV4_AUTH_ENABLED=true`；默认凭证链：`AWS_SDK_LOAD_CONFIG=true`。
- **数据源（Data Source）**
  - 使用 **AMP 专用插件**：`grafana-amazonprometheus-datasource`。
  - URL 指向 **Workspace 的 `prometheusEndpoint` 根**（末尾带 `/`，不手拼 `/api/v1/query`）。
  - `jsonData`：`authType=default`、`defaultRegion=us-east-1`、`httpMethod=POST`；并显式打开 `sigV4Auth=true / sigV4AuthType=default / sigV4Region=us-east-1`。
  - UI 中 **Save & Test = 成功**（已看到 “Successfully queried the Prometheus API.”）。
- **最小仪表盘（3 图 + 变量）**
  - 变量 `app = label_values(http_server_requests_seconds_count, application)`。
  - **QPS**：`sum(rate(http_server_requests_seconds_count{application="$app"}[5m]))`
  - **错误率(%)**：`( sum(rate(http_server_requests_seconds_count{application="$app",status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count{application="$app"}[5m])) ) * 100`
  - **P95(ms)**：`histogram_quantile(0.95, sum by (le) (rate(http_server_requests_seconds_bucket{application="$app"}[5m]))) * 1000`
  - 刷新 `30s`，时窗建议 `Last 15m` 或 `Last 1h`。
- **自动化与复原**
  - 已把 **Grafana 的 Helm 安装与 values（含数据源与仪表盘 provisioning）** 纳入 **post-recreate / pre-teardown**，Pod 启动即可**自动加载仪表盘**。

常见坑 & 处理

- **403 Forbidden**：
  - 已通过 **AMP 专用插件** + IRSA 生效 + 正确 URL/Region 解决；
  - 备用方案（未用）：核心 Prometheus 数据源 + `sigV4Auth=true`。
- **插件页面显示 “No Authentication”**：通常是插件/类型未正确识别或 values 未完全生效；通过修正数据源类型与 jsonData 后解决。

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

### Day 1 - 成果打包 & 架构一页纸（One-Pager）

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

### Day 2 - Mock-1（全英）：运行/韧性/观测深挖

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

### Day 3 - 简历 v2（中/英）+ LinkedIn 打磨

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
