# 📌 2025 Java 学习计划

## 🎯 总体目标

系统复习与提升 Java 基础知识、算法能力以及英语综合技能，以顺利通过外企技术面试，达到中高级 Java 开发工程师或 Java 项目技术 Leader 的水平。

---

## 📅 第一阶段（第1-4周）：基础复习与技能提升

### 🔖 第1周：Java基础、开发工具与算法快速复习

#### ☑️ Java基础（共3天）
- **Day1: Java语法与OOP**
  - 数据类型、控制结构（条件语句、循环）
  - 面向对象基础（封装、继承、多态）

- **Day2: 集合框架与异常处理**
  - 集合框架（List, Set, Map）
  - 异常机制（try-catch、throws、自定义异常）

- **Day3: Java多线程基础**
  - 线程创建（Thread、Runnable、Callable）
  - 线程池使用（ExecutorService）
  - 线程同步（synchronized、Lock）与线程安全（竞态条件、原子操作）

#### ☑️ 开发工具（共1天）
- Git（版本控制与分支管理）
- Maven（项目构建与依赖管理）
- MySQL（基本操作与优化技巧）
- IntelliJ IDEA（基础操作与快捷键）
- Docker（容器化基础使用）

#### ☑️ 算法基础（贯穿全周）
- 每日完成1-2道 Leetcode Medium 算法题目练习（数据结构与基础算法）

#### ☑️ 英语听力与口语（贯穿全周）
- 每天收听30分钟英文技术播客或视频（如TechLead）
- 每天英文口语复述算法题或技术知识点，锻炼表达能力

---

### 🔖 第2周：Spring 框架、数据库与缓存复习

- **Day1: Spring核心**
  - 依赖注入（IOC）、面向切面编程（AOP）
- **Day2: Spring Boot 基础**
  - 自动装配、配置文件、REST API搭建
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

---

### 🔖 第3周：算法强化与性能优化初步

| 天次 | 学习主题 | 关键要点 | 主要产出 |
|------|----------|----------|----------|
| **Day 1** | 动态规划基础复盘 | - DP 四步模板<br>- 斐波那契、爬楼梯、01 背包 | 4 道题代码 + `dp-day1.md` 英文总结 |
| **Day 2** | 动态规划进阶 | - 一维压缩、O(n log n) LIS<br>- 最大子数组和（Kadane）<br>- 区间 DP / Bitmask DP 入门 | 5 道题代码 + `dp-day2.md` 英文总结 |
| **Day 3** | 回溯 & 贪心 | - 回溯三步框架 + 剪枝<br>- 组合/全排列/组合总和<br>- 区间贪心（无重叠区间） | 5 道题 + `backtrack-greedy-day3.md` |
| **Day 4** | JVM 内部结构 & GC 原理 | - 堆/Metaspace 布局<br>- G1/ZGC 收集器机制<br>- GC 日志、jstat、VisualVM | `gc_base.log` + `jvm-gc-day4.md` |
| **Day 5** | JVM 性能调优实战 | - 压测 ► 诊断 ► 调参 ► 复测闭环<br>- 参数：堆大小、MaxGCPauseMillis、IHOP | baseline / tuned 报表 + `jvm-tune-report.md` |
| **Day 6** | MySQL 高级优化 & 索引深度 | - 覆盖索引、下推谓词<br>- Buffer Pool 命中率<br>- EXPLAIN JSON / optimizer_trace | 优化前后 EXPLAIN + `mysql-adv-day6.md` |
| **Day 7** | 周整合 & 输出 | - 知识库 & Cheat Sheet<br>- Pull Request：GC/Index 参数合并<br>- 英文周报 ≥ 200 词 + 5 min 算法讲解视频 | `week3-cheatsheet.md` + `week3-report-en.md` + `week4-plan-draft.md` |

---

### 🔖 第4周：英语能力提升与面试准备

- **Day1-Day3: 技术英文阅读与写作**
  - 阅读英文技术文章或书籍（如《Effective Java》）
  - 每天撰写英文技术博客或学习总结
- **Day4-Day5: 模拟技术面试**
  - 每周进行一次英文技术面试模拟
  - 改善英文自我介绍与技术问答表现
- **Day6-Day7: 面试总结与准备强化**
  - 总结本周面试经验与不足，制定针对性改进措施

---

## 📅 第二阶段（第 5 – 8 周）：Java Cloud-Native 跑通实战（仅用 AWS，EKS NodeGroup）

> 目标：在 8 周内完成「Docker ➜ Kubernetes ➜ GitOps ➜ Chaos ➜ 观测 ➜ SRE SLO ➜ Bedrock AI Sidecar」全链路实践  
> 结果交付：可截图/可量化的流水线、指标、故障自愈报告与新版简历亮点

| 时段 | 重点主题 & 关键实操 | 主要交付物 |
|------|-------------------|-----------|
| **Day-0（半天）** | **Docker & K8s 快速回忆**<br>• Play-with-Docker 实验：build / run / push<br>• Kubernetes Basics 模块 1-3：Deploy ➜ Expose ➜ Scale | `day0-notes.md`（常用命令 + 概念速记） |
| **Bootcamp 3 天** | **云底座 + IaC**<br>Day 1：VPC / LB / IAM（AWS vs）<br>Day 2：`eksctl` 创建首个 **EKS Managed Node Group**（t3.small + Spot）<br>Day 3：Terraform 双 provider（EKS + 预留 GKE stub）+ S3 后端 & DynamoDB 锁 | `eksctl-config.yaml`<br>`main.tf` & `terraform.tfstate`<br>《云服务对照笔记》 |
| **Week 5** | **双 GitOps CI/CD**<br>① CodeCommit → CodeBuild → ECR → **CodeDeploy 蓝绿 ➜ EKS(NodeGroup)**<br>② GitHub Actions → Docker Hub → **Argo CD**（部署到同集群另一 ns）<br>③ IRSA 最小权限 + **Trivy 镜像扫描** | CodePipeline & 蓝绿截图<br>Argo CD Sync GIF<br>IAM JSON + 扫描报告 |
| **Week 6** | **Helm + HPA + Chaos 自愈**<br>• Helm Chart 打包 & 部署<br>• 设置 HPA + PDB<br>• Chaos Mesh `pod-kill / latency` 实验，生成 MTTR + P95 报告 | `charts/task-manager/`<br>Chaos 报告（MTTR < 3 min） |
| **Week 7-a** | **可观测 & SRE**<br>• OpenTelemetry（ADOT Sidecar）→ Amazon Managed Prometheus + Grafana Cloud<br>• 使用 `sample_limit` 控费<br>• 定义 99.9 % SLO，CloudWatch 合成告警 | Trace GIF + Grafana Dash<br>SLO YAML & Post-mortem |
| **Week 7-b** | **生成式 AI Sidecar（AWS Bedrock）**<br>• Spring AI + **Bedrock Titan**<br>• 向量检索：Redis Vector / PGVector<br>• Token Budget + Rate Limiter（成本护栏） | Chat-for-Admin 演示视频<br>《Bedrock FinOps 分析》 |
| **Week 8** | **Mock Marathon & Résumé v2**<br>• Mock-1：故障注入 + SLO 深挖（英文）<br>• Mock-2：AI & 多云系统设计（英文）<br>• **Day-8 Cleanup**：`terraform destroy` + `eksctl delete cluster` + 账单审计 | 两段 90 min 录像 + 评分表<br>`progress.png`（语速 / filler / MTTR / CI 成功率曲线）<br>简历 v2 PDF |

### 🎯 关键 KPI

| 指标 | 目标 |
|------|------|
| GitOps 发布成功率 | ≥ 98 % |
| Chaos MTTR | ≤ 1 min |
| Trace 覆盖率 | ≥ 95 % |
| Mock 综合评分（5 分制） | ≥ 4.0 |

### 🛡 费用 & 安全护栏

* **单集群 + NodeGroup t3.small (Spot 混合)** —— 控制平面 ¥85/月  
* **S3 Backend + DynamoDB lock** —— 防止 `terraform apply` 冲突  
* **IRSA (OIDC)** —— CI/CD 免明文 Key  
* **Trivy** —— 流水线镜像漏洞扫描  
* **Chaos Daemon privileged=true** —— NodeGroup 无 Fargate 限制  
* **AMP `sample_limit`** —— 防止高频 kubelet 指标爆表  
* **AWS Budgets + Bedrock Token Monitor** —— 成本预警  
* **Day-8 Retro** —— 删除集群 & 资源，导出账单

> **执行顺序：Day-0 → Bootcamp → Week 5-8**。  
> 若后续想再补多云，可把 Terraform GKE stub 激活即可。整个阶段无需 Google Cloud 账号即可完成所有必需交付物与面试故事。

---
