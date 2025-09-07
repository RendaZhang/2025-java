<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 1 - 开场与 API 设计（契约与可靠性打底）](#day-1---%E5%BC%80%E5%9C%BA%E4%B8%8E-api-%E8%AE%BE%E8%AE%A1%E5%A5%91%E7%BA%A6%E4%B8%8E%E5%8F%AF%E9%9D%A0%E6%80%A7%E6%89%93%E5%BA%95)
  - [目标](#%E7%9B%AE%E6%A0%87)
    - [步骤清单](#%E6%AD%A5%E9%AA%A4%E6%B8%85%E5%8D%95)
  - [第 1 步：开场与环境就绪（10–15 分钟）](#%E7%AC%AC-1-%E6%AD%A5%E5%BC%80%E5%9C%BA%E4%B8%8E%E7%8E%AF%E5%A2%83%E5%B0%B1%E7%BB%AA1015-%E5%88%86%E9%92%9F)
    - [创建 `docs/interview/QBANK.md`：](#%E5%88%9B%E5%BB%BA-docsinterviewqbankmd)
    - [创建 `docs/interview/elevator_pitch_en.md`](#%E5%88%9B%E5%BB%BA-docsinterviewelevator_pitch_enmd)
  - [第 2 步：算法训练（LeetCode，60–90 分钟）](#%E7%AC%AC-2-%E6%AD%A5%E7%AE%97%E6%B3%95%E8%AE%AD%E7%BB%83leetcode6090-%E5%88%86%E9%92%9F)
    - [LC 904. Fruit Into Baskets（中等，Sliding Window + HashMap）](#lc-904-fruit-into-baskets%E4%B8%AD%E7%AD%89sliding-window--hashmap)
    - [LC 167. Two Sum II – Input Array Is Sorted（简单，Two Pointers）](#lc-167-two-sum-ii--input-array-is-sorted%E7%AE%80%E5%8D%95two-pointers)
    - [LC 209. Minimum Size Subarray Sum（中等，Sliding Window）](#lc-209-minimum-size-subarray-sum%E4%B8%AD%E7%AD%89sliding-window)
    - [复盘 LC 904](#%E5%A4%8D%E7%9B%98-lc-904)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 1 - 开场与 API 设计（契约与可靠性打底）

## 目标

1. **算法**：完成数组/哈希方向 2–3 题（含 1 题高质量复盘：思路→复杂度→边界→错因）。
2. **面试知识**：在 `QBANK.md` 新增 **≤ 7 条**「API 设计与可靠性」要点（契约/版本化/鉴权/幂等/限流-重试-熔断/错误码规范）。
3. **英语**：写出 45 秒英文自我介绍 v1 + 1 分钟回答 “Why this API design?” v1。
4. **收尾**：列明本周 3–5 个关键目标与潜在卡点；提交一次 commit（包含题解/要点/英语草稿）。

### 步骤清单

1. **开场与环境就绪（10–15m）**：确认今日时间块与产出文件清单。
2. **算法训练（60–90m）**：数组/哈希→滑动窗口或双指针 2–3 题 + 1 题复盘记录。
3. **API 设计与可靠性要点（60–90m）**：整理 ≤7 条面试要点写入 `QBANK.md`。
4. **英语口语素材（30–45m）**：电梯自我介绍 v1 + “Why this API design?” v1。
5. **收尾（10–15m）**：列本周目标/卡点，做一次小结 commit。

> 交付物：`QBANK.md`（新增条目）、`elevator_pitch_en.md`（v1）。

---

## 第 1 步：开场与环境就绪（10–15 分钟）

在 `docs/interview/` 下创建 2 个文件。

### 创建 `docs/interview/QBANK.md`：

```markdown
# 高频问题库

- **Java**：集合/并发（synchronized、Lock、CAS、线程池）、JVM（内存结构、GC）、异常与最佳实践
- **Spring**：IOC/AOP、RestController、Actuator、配置管理、事务/连接池
- **微服务 & K8s**：Deployment/Service/Ingress/HPA/探针、无状态、滚动发布与回滚、ConfigMap/Secret
- **AWS & 云原生**：EKS NodeGroup vs Fargate、ALB、IRSA/OIDC、ECR、S3、CloudWatch、AMP、Grafana
- **DevOps**：CI/CD（GitHub Actions OIDC）、Trivy、回滚策略、IaC（Terraform 后端与锁）、最小权限
- **SRE**：SLI/SLO/错误预算、MTTR、Chaos、容量与成本权衡
- **行为/英文**：冲突处理、推动落地、失败复盘、跨团队协作、影响力（每题准备一条 STAR）

## API Design & Reliability — W7D1（≤ 7 bullets）

- [ ] 契约清晰（资源建模/语义化 URL/请求-响应结构）
- [ ] 版本化策略（URI vs Header；向后兼容）
- [ ] 鉴权与授权（JWT/OIDC；最小权限；Token 续期）
- [ ] 幂等性（幂等键/PUT vs POST/重试安全）
- [ ] 限流-重试-熔断（客户端与服务端配合；退避策略）
- [ ] 错误码与可观察性（统一错误模型/Trace-ID/指标）
- [ ] 灰度发布与回滚（逐步放量/健康检查/回滚开关）
```

### 创建 `docs/interview/elevator_pitch_en.md`

```markdown
# Elevator Pitch (v1) — 45s

Hi, I’m Renda Zhang, a Java backend developer focused on cloud-native microservices...
- Core strengths (3 bullets): <!-- 技术亮点/项目亮点 -->
- Recent focus: <!-- 与岗位画像对齐的一点 -->
- What I bring: <!-- 与业务价值挂钩 -->

## 1-min Answer: “Why this API design?”

- Context: <!-- 业务背景/非功能性约束 -->
- Key choices: versioning, idempotency, rate-limit/retry/circuit, error model
- Trade-offs: <!-- 延迟、成本、演进性之间的权衡 -->
- Outcome: <!-- 可观测性/稳定性/迭代效率带来的影响 -->
```

---

## 第 2 步：算法训练（LeetCode，60–90 分钟）

目标：

完成 2 题（滑动窗口 + 双指针），可选 1 题挑战；并对其中 1 题做高质量复盘。

> 小提示：如果某题卡住 >10 分钟再看提示；否则先独立思考。

### LC 904. Fruit Into Baskets（中等，Sliding Window + HashMap）

- 思路提示：维护一个“最多包含两种元素”的窗口；用 `count[type]` 记录数量；当种类数 > 2 时左指针缩小直到回到 ≤ 2；每轮更新最大窗口长度。
- 关键不变量：窗口内**水果种类 ≤ 2**。
- 常见坑：缩窗时记得把 `count[left]` 递减到 0 再删除键；更新答案的位置要在扩窗后每轮都尝试。
- 自测用例：
  - `[1,2,1] -> 3`
  - `[0,1,2,2] -> 3`
  - `[1,2,3,2,2] -> 4`

### LC 167. Two Sum II – Input Array Is Sorted（简单，Two Pointers）

- 思路提示：有序数组；左右指针夹逼。`sum < target` 左指针右移；`sum > target` 右指针左移；相等返回（注意题目通常要求 1-based 索引）。
- 常见坑：越界与死循环；别用哈希表（本题更看重双指针）。
- 自测用例：`numbers=[2,7,11,15], target=9 -> [1,2]`

### LC 209. Minimum Size Subarray Sum（中等，Sliding Window）

- 思路提示：正整数数组；右指针扩张累加，`sum >= target` 时左指针尽可能收缩并更新最短长度。
- 自测用例：`target=7, nums=[2,3,1,2,4,3] -> 2`

### 复盘 LC 904

按以下结构写复盘（200–300 字）：

- **Pattern**（滑动窗口/双指针）
- **Intuition**（为什么这样滑/夹逼）
- **Steps**（关键循环与收缩/扩张条件）
- **Complexity**：Time/Space
- **Edge Cases**（空数组、单元素、全相同、多种类切换等）
- **Mistakes & Fix**（今天踩过的 1–2 个坑）

---
