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

## 📅 第二阶段（第5-8周）：Java Cloud-Native Sprint

**主修 AWS + 副修 GCP (“Day-0” 基础 + 3-Day Bootcamp ➜ Week 5 – Week 8)**  
*时段：2025-07-01 → 2025-08-25 — 目标：补齐 AWS 实战、云 SRE、Gen-AI 集成，并生成可量化简历亮点*
---

## 📆 Timeline & Deliverables

| 时段               | 云侧重       | 主题                                                                                        | 关键实操 & 交付物                                                                                                                                                                                                      |      |                     |
| ---------------- | --------- | ----------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---- | ------------------- |
| **Day-0 (½ d)**  | —         | *Docker & K8s refresher*                                                                  | Labs: Play-with-Docker & Kubernetes Basics module → `day0-notes.md` （常用命令＋概念） ([aws.amazon.com][1], [eksctl.io][2])                                                                                             |      |                     |
| **Bootcamp 3 d** | AWS + GCP | Day 1 – VPC/LB/IAM 对照 · Day 2 – `eksctl` 建 EKS · Day 3 – Terraform 双 provider (EKS + GKE) | `eksctl-config.yaml` ([docs.aws.amazon.com][3]) · `main.tf` + S3 backend lock ([developer.hashicorp.com][4], [developer.hashicorp.com][5]) · 对照笔记                                                               |      |                     |
| **Week 5**       | **AWS**   | 双 GitOps CI/CD                                                                            | ① CodePipeline Blue-Green→EKS 截图 ([aws.amazon.com][6], [aws.amazon.com][1]) ② GitHub Actions→Argo CD→GKE GIF ③ IAM IRSA/OIDC role json ([docs.aws.amazon.com][7]) ④ Trivy 扫描集成到 CodeBuild ([chaos-mesh.org][8]) |      |                     |
| **Week 6**       | **AWS**   | K8s Deep-Dive + Helm + Chaos                                                              | Helm Chart (`charts/task-manager/`) · Chaos Mesh `pod-kill` + latency → MTTR < 3 min ([chaos-mesh.org][9], [chaos-mesh.org][8]) · HPA & PDB YAML                                                                |      |                     |
| **Week 7a**      | **AWS**   | Observability & SRE                                                                       | ADOT sidecar → Trace to AMP + Grafana ([docs.aws.amazon.com][10], [docs.aws.amazon.com][11]) · `sample_limit` 降低成本 ([docs.aws.amazon.com][12]) · SLO 99.9 % → CloudWatch Composite Alert                        |      |                     |
| **Week 7b**      | **GCP**   | Gen-AI PoC                                                                                | Spring AI + Vertex AI Gemini Pro demo · Redis/PGVector · Token budget monitor (Vertex pricing) ([cloud.google.com][13], [cloud.google.com][14])                                                                 |      |                     |
| **Week 8**       | 混合云       | Mock Marathon & Résumé v2                                                                 | 两轮 90-min 全英 mock 录像 · `progress.png` 折线 (语速                                                                                                                                                                    | MTTR | CI 成功率) · 简历 v2 PDF |

---

## 🛡 Built-in Guard-rails

1. **单集群多 Namespace**⇒省 EKS 控制层费用 ([docs.aws.amazon.com][3])
2. **S3 backend + state lock** 防止 tf 冲突 ([developer.hashicorp.com][4], [developer.hashicorp.com][5])
3. **Trivy** 镜像扫描在 CodeBuild 步骤 ([chaos-mesh.org][8])
4. **IRSA/OIDC** 最小权限部署流水线 ([docs.aws.amazon.com][7])
5. **Chaos Mesh privilege flag** 解决 EKS PSA 限制 ([chaos-mesh.org][9])
6. **AMP `sample_limit`** 防止指标爆表 ([docs.aws.amazon.com][12])
7. **Vertex AI Budget** 监控 token 花费 ([cloud.google.com][13])
8. **Day-8 Retro** — `terraform destroy`, `eksctl delete` + 账单审计

---

## 🎯 KPI Targets

| 指标         | Boot | W5   | W6      | W7      | W8      |
| ---------- | ---- | ---- | ------- | ------- | ------- |
| GitOps 成功率 | —    | 90 % | 93 %    | 96 %    | 98 %    |
| Chaos MTTR | —    | —    | < 3 min | < 2 min | < 1 min |
| Trace 覆盖率  | —    | 40 % | 70 %    | 95 %    | 95 %    |
| Mock 评分\*  | —    | 3.3  | 3.5     | 3.8     | 4.0     |

\*5-point scale: Coding / Java / Design / Comms

---

## ⏰ Daily Rhythm (≈ 4 h)

| 上午 1 h   | 上午 1 h             | 下午 1 h                  | 晚上 1 h           |
| -------- | ------------------ | ----------------------- | ---------------- |
| 云/容器文档速读 | Hands-on Lab / IaC | **英语**：Shadow 或 STAR 练习 | 技术博客 / 日报 & push |

---
