<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 1 - 开场与 API 设计（契约与可靠性打底）](#day-1---%E5%BC%80%E5%9C%BA%E4%B8%8E-api-%E8%AE%BE%E8%AE%A1%E5%A5%91%E7%BA%A6%E4%B8%8E%E5%8F%AF%E9%9D%A0%E6%80%A7%E6%89%93%E5%BA%95)
  - [目标](#%E7%9B%AE%E6%A0%87)
    - [步骤清单](#%E6%AD%A5%E9%AA%A4%E6%B8%85%E5%8D%95)
  - [第 1 步：开场与环境就绪（10–15 分钟）](#%E7%AC%AC-1-%E6%AD%A5%E5%BC%80%E5%9C%BA%E4%B8%8E%E7%8E%AF%E5%A2%83%E5%B0%B1%E7%BB%AA1015-%E5%88%86%E9%92%9F)
    - [创建 `docs/interview/QBANK.md`：](#%E5%88%9B%E5%BB%BA-docsinterviewqbankmd)
    - [创建 `docs/interview/elevator_pitch_en.md`](#%E5%88%9B%E5%BB%BA-docsinterviewelevator_pitch_enmd)
  - [第 2 步：算法训练](#%E7%AC%AC-2-%E6%AD%A5%E7%AE%97%E6%B3%95%E8%AE%AD%E7%BB%83)
    - [题 A：最短长度子数组和 ≥ S（可变长度滑动窗口）](#%E9%A2%98-a%E6%9C%80%E7%9F%AD%E9%95%BF%E5%BA%A6%E5%AD%90%E6%95%B0%E7%BB%84%E5%92%8C-%E2%89%A5-s%E5%8F%AF%E5%8F%98%E9%95%BF%E5%BA%A6%E6%BB%91%E5%8A%A8%E7%AA%97%E5%8F%A3)
  - [约束与提示](#%E7%BA%A6%E6%9D%9F%E4%B8%8E%E6%8F%90%E7%A4%BA)
  - [伪码（按这个思路写就行）](#%E4%BC%AA%E7%A0%81%E6%8C%89%E8%BF%99%E4%B8%AA%E6%80%9D%E8%B7%AF%E5%86%99%E5%B0%B1%E8%A1%8C)
  - [自测用例（全部应通过）](#%E8%87%AA%E6%B5%8B%E7%94%A8%E4%BE%8B%E5%85%A8%E9%83%A8%E5%BA%94%E9%80%9A%E8%BF%87)
  - [常见坑](#%E5%B8%B8%E8%A7%81%E5%9D%91)

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

## 第 2 步：算法训练

### 题 A：最短长度子数组和 ≥ S（可变长度滑动窗口）

**要求**：给定正整数数组 `nums` 与目标值 `target`，返回和 ≥ `target` 的最短连续子数组长度；不存在则返回 0。
**函数签名（Java）**：`int minSubArrayLen(int target, int[] nums)`

## 约束与提示

* 假设 `nums[i] > 0`（全正数，这是滑窗可行的关键）。
* 目标是 **O(n)** 时间、**O(1)** 额外空间。
* 窗口右指针向右推进累加；当窗口内和 ≥ target 时，尽量左缩小窗口并更新最小长度。

## 伪码（按这个思路写就行）

```text
minLen = +∞
sum = 0
left = 0
for right in [0..n-1]:
  sum += nums[right]
  while sum >= target:
    minLen = min(minLen, right - left + 1)
    sum -= nums[left]
    left += 1
return (minLen == +∞) ? 0 : minLen
```

## 自测用例（全部应通过）

* `target=7, nums=[2,3,1,2,4,3]  -> 2`（子数组 `[4,3]`）
* `target=4, nums=[1,4,4]        -> 1`
* `target=11, nums=[1,1,1,1,1,1,1,1] -> 0`
* 边界：`target=1, nums=[1] -> 1`；`target=3, nums=[2] -> 0`

## 常见坑

* 更新 `minLen` 后别忘了左缩时把 `sum` 减去 `nums[left]` 再 `left++`。
* 别把“固定窗口大小的最大/最小和问题”的写法套过来；本题窗口是**可变长**。
* 返回值为 0 的判定：`minLen` 没被更新时。

> 进阶（可选）：再写一个 `O(n log n)` 的前缀和 + 二分版本，对比两者复杂度与实现复杂度。
