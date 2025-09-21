<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 7 - 全栈](#day-7---%E5%85%A8%E6%A0%88)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [Step 1 - 算法练习](#step-1---%E7%AE%97%E6%B3%95%E7%BB%83%E4%B9%A0)
    - [LC354. Russian Doll Envelopes（嵌套信封）](#lc354-russian-doll-envelopes%E5%B5%8C%E5%A5%97%E4%BF%A1%E5%B0%81)
    - [LC309. Best Time to Buy and Sell Stock with Cooldown（含冷冻期）](#lc309-best-time-to-buy-and-sell-stock-with-cooldown%E5%90%AB%E5%86%B7%E5%86%BB%E6%9C%9F)
    - [LC72. Edit Distance（编辑距离）](#lc72-edit-distance%E7%BC%96%E8%BE%91%E8%B7%9D%E7%A6%BB)
  - [Step 2 - 全栈](#step-2---%E5%85%A8%E6%A0%88)
    - [React/TypeScript 基础最小面（函数组件/Hook、受控 vs 非受控、常用 TS 工具类型、错误边界）](#reacttypescript-%E5%9F%BA%E7%A1%80%E6%9C%80%E5%B0%8F%E9%9D%A2%E5%87%BD%E6%95%B0%E7%BB%84%E4%BB%B6hook%E5%8F%97%E6%8E%A7-vs-%E9%9D%9E%E5%8F%97%E6%8E%A7%E5%B8%B8%E7%94%A8-ts-%E5%B7%A5%E5%85%B7%E7%B1%BB%E5%9E%8B%E9%94%99%E8%AF%AF%E8%BE%B9%E7%95%8C)
    - [路由与表单（React Router v6、嵌套路由/懒加载、表单校验与数据流）](#%E8%B7%AF%E7%94%B1%E4%B8%8E%E8%A1%A8%E5%8D%95react-router-v6%E5%B5%8C%E5%A5%97%E8%B7%AF%E7%94%B1%E6%87%92%E5%8A%A0%E8%BD%BD%E8%A1%A8%E5%8D%95%E6%A0%A1%E9%AA%8C%E4%B8%8E%E6%95%B0%E6%8D%AE%E6%B5%81)
    - [SSR / CSR / 选择性水合（取舍与指标：TTFB/TTI/CLS；岛屿架构要点）](#ssr--csr--%E9%80%89%E6%8B%A9%E6%80%A7%E6%B0%B4%E5%90%88%E5%8F%96%E8%88%8D%E4%B8%8E%E6%8C%87%E6%A0%87ttfbtticls%E5%B2%9B%E5%B1%BF%E6%9E%B6%E6%9E%84%E8%A6%81%E7%82%B9)
    - [CSP / 缓存策略（CSP `nonce/hash`、Cache-Control/ETag、CDN/Edge/浏览器多级缓存）](#csp--%E7%BC%93%E5%AD%98%E7%AD%96%E7%95%A5csp-noncehashcache-controletagcdnedge%E6%B5%8F%E8%A7%88%E5%99%A8%E5%A4%9A%E7%BA%A7%E7%BC%93%E5%AD%98)
    - [Sentry 埋点与错误上报（前后端统一 TraceID、Source Map、错误分级与去噪）](#sentry-%E5%9F%8B%E7%82%B9%E4%B8%8E%E9%94%99%E8%AF%AF%E4%B8%8A%E6%8A%A5%E5%89%8D%E5%90%8E%E7%AB%AF%E7%BB%9F%E4%B8%80-traceidsource-map%E9%94%99%E8%AF%AF%E5%88%86%E7%BA%A7%E4%B8%8E%E5%8E%BB%E5%99%AA)
    - [环境变量管理（构建期 vs 运行期、公开变量白名单、CI/CD 注入、敏感信息不入包）](#%E7%8E%AF%E5%A2%83%E5%8F%98%E9%87%8F%E7%AE%A1%E7%90%86%E6%9E%84%E5%BB%BA%E6%9C%9F-vs-%E8%BF%90%E8%A1%8C%E6%9C%9F%E5%85%AC%E5%BC%80%E5%8F%98%E9%87%8F%E7%99%BD%E5%90%8D%E5%8D%95cicd-%E6%B3%A8%E5%85%A5%E6%95%8F%E6%84%9F%E4%BF%A1%E6%81%AF%E4%B8%8D%E5%85%A5%E5%8C%85)
  - [Step 3 - 补充 `architecture.md` 新小节](#step-3---%E8%A1%A5%E5%85%85-architecturemd-%E6%96%B0%E5%B0%8F%E8%8A%82)
  - [Step 4 - 1 分钟英文口语](#step-4---1-%E5%88%86%E9%92%9F%E8%8B%B1%E6%96%87%E5%8F%A3%E8%AF%AD)
  - [Step 5 - Week 8 Day 1（简历日）改写清单](#step-5---week-8-day-1%E7%AE%80%E5%8E%86%E6%97%A5%E6%94%B9%E5%86%99%E6%B8%85%E5%8D%95)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 7 - 全栈

## 今日目标

- **算法**：3 题。
- **面试能力/知识**：全栈关键点（React/TS、路由与表单、SSR/CSR/选择性水合、CSP/缓存、Sentry、env 管理）。
- **英语**：解释 “SSR vs CSR vs selective hydration” 的取舍（1 分钟）。

---

## Step 1 - 算法练习

### LC354. Russian Doll Envelopes（嵌套信封）

**思路（排序 + 一维 LIS，O(n log n)）**

- 先按宽度 `w` 升序排序；**宽度相等时按高度 `h` 降序**，这样相同宽度不会被误计入 LIS。
- 再对排序后的高度序列做 **LIS（严格递增）**，得到最多可嵌套的数量。
- LIS 用“耐心排序”思路：维护 `tails[]`，对每个高度二分找到第一个 `>= h` 的位置替换；严格递增保证相等高度不会算作嵌套。

**Java 代码**

```java
import java.util.*;

class Solution354 {
    public int maxEnvelopes(int[][] envelopes) {
        if (envelopes == null || envelopes.length == 0) return 0;
        Arrays.sort(envelopes, (a, b) -> {
            if (a[0] != b[0]) return Integer.compare(a[0], b[0]); // 宽度升序
            return Integer.compare(b[1], a[1]); // 同宽：高度降序
        });
        int[] tails = new int[envelopes.length];
        int len = 0;
        for (int[] e : envelopes) {
            int h = e[1];
            int i = Arrays.binarySearch(tails, 0, len, h);
            if (i < 0) i = -i - 1; // lower_bound
            tails[i] = h;
            if (i == len) len++;
        }
        return len;
    }
}
```

**复杂度**：排序 `O(n log n)` + LIS `O(n log n)` → 总 `O(n log n)`；空间 `O(n)`。

**易错点**

- **同宽度需高度降序**，否则相同宽度会被错误地连成递增序列。
- LIS 必须“严格递增”，二分使用 `lower_bound`（第一个 >= h）。
- 输入为空或长度为 0 时返回 0。

### LC309. Best Time to Buy and Sell Stock with Cooldown（含冷冻期）

**思路（状态机 DP，O(1) 空间）**

设三种状态：

- `hold`：持有一股；
- `sold`：今天刚卖出（进入冷冻日）；
- `rest`：空仓且不在冷冻日（可买）。

转移（遍历每天价格 `p`）：

- `sold' = hold + p`（今天把之前持有的卖了）；
- `hold' = max(hold, rest - p)`（继续持有或从休息中买入）；
- `rest' = max(rest, sold)`（空仓维持，或由前一天卖出度过冷冻日）。

答案 `max(sold, rest)`。

**Java 代码**

```java
class Solution309 {
    public int maxProfit(int[] prices) {
        if (prices == null || prices.length == 0) return 0;
        int hold = Integer.MIN_VALUE / 2; // 避免溢出
        int sold = Integer.MIN_VALUE / 2;
        int rest = 0;
        for (int p : prices) {
            int prevSold = sold;
            sold = hold + p;
            hold = Math.max(hold, rest - p);
            rest = Math.max(rest, prevSold);
        }
        return Math.max(sold, rest);
    }
}
```

**复杂度**：时间 `O(n)`；空间 `O(1)`。

**易错点**

- 初始 `hold` 不能用 `Integer.MIN_VALUE` 直接参与 `+p`，用 **`MIN/2`** 更安全。
- `rest` 与 `sold` 的更新顺序注意用 `prevSold` 暂存旧值。
- 返回 `max(sold, rest)`，不是只返回 `sold`。

### LC72. Edit Distance（编辑距离）

**思路（二维 DP → 一维滚动优化）**

- 设 `dp[i][j]` 为 `word1[0..i)` 到 `word2[0..j)` 的最少操作数。
- 初始化：`dp[i][0]=i`，`dp[0][j]=j`。
- 转移：若 `w1[i-1]==w2[j-1]`，`dp[i][j]=dp[i-1][j-1]`；否则
  `dp[i][j] = 1 + min(dp[i-1][j]/*删*/, dp[i][j-1]/*增*/, dp[i-1][j-1]/*替*/)`。
- 用一维数组 `dp[j]` 滚动行，变量 `prev` 保存左上角 `dp[i-1][j-1]`。

**Java 代码（O(min(m,n)) 空间版）**

```java
class Solution72 {
    public int minDistance(String word1, String word2) {
        if (word1 == null) word1 = "";
        if (word2 == null) word2 = "";
        // 保证用较短的串作为列，降低空间
        if (word1.length() < word2.length()) {
            String t = word1; word1 = word2; word2 = t;
        }
        int m = word1.length(), n = word2.length();
        int[] dp = new int[n + 1];
        for (int j = 0; j <= n; j++) dp[j] = j; // 第一行

        for (int i = 1; i <= m; i++) {
            int prev = dp[0];      // dp[i-1][0]
            dp[0] = i;             // dp[i][0]
            for (int j = 1; j <= n; j++) {
                int temp = dp[j];  // 备份 dp[i-1][j]
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[j] = prev;  // 继承左上角
                } else {
                    dp[j] = 1 + Math.min(Math.min(
                            dp[j],      // 删除：来自 dp[i-1][j]
                            dp[j - 1]   // 插入：来自 dp[i][j-1]
                    ), prev);          // 替换：来自 dp[i-1][j-1]
                }
                prev = temp; // 移动左上角
            }
        }
        return dp[n];
    }
}
```

**复杂度**：时间 `O(mn)`；空间 `O(min(m,n))`（将较短串作为列）。

**易错点**

- 一维滚动时 `prev`/`temp` 的更新顺序要正确：`prev` 代表“左上角”。
- 初始化首行首列别漏（相当于把另一串全部插入/删除）。
- 注意空串与大小写差异；本题区分大小写。

---

## Step 2 - 全栈

### React/TypeScript 基础最小面（函数组件/Hook、受控 vs 非受控、常用 TS 工具类型、错误边界）

- Hooks 遵循**顺序模型**：只在顶层、只在函数组件/自定义 Hook 调用。
- **State 触发渲染，Ref 不触发**；昂贵派生用 `useMemo`，函数引用稳定用 `useCallback`。
- `useEffect` 防三坑：**漏依赖 / 误用副作用 / 请求竞态**（用 AbortController 清理）。
- 表单：**小表单受控**，**大表单非受控 + 表单库 + schema 校验**；事件类型用 `React.ChangeEvent<>` 等。
- Props 建模：优先 `ComponentProps`、`Pick/Partial/Record`，**显式 children**。
- 错误边界：仍用**类组件** + Sentry 上报，链上 **release/environment/traceId**。
- 大列表：**稳定 key + 虚拟化 + React.memo**，必要时路由/组件级拆分与选择性水合。

> “我把 React 当**纯函数 + 状态槽位**来写：**State 负责可见变化、Ref 负责持久引用、Effect 只做副作用**；类型上用 `ComponentProps` 组合与显式 `children`，错误边界交给类组件并配合 Sentry 串起 trace。”

场景 A - Hooks 心智与规则

**面试官：** 用一句话讲讲 Hooks 的工作方式和两条硬规则？

**我：** Hooks 基于**渲染顺序**存取状态，每次渲染都会拿到独立的快照；两条硬规则是**只在最顶层调用**、**只在 React 函数组件或自定义 Hook 中调用**。违反顺序（如条件里调用）会把状态槽位对乱，导致“幽灵状态”。

场景 B - State vs Ref vs Memo

**面试官：** 什么时候用 `useState`，什么时候用 `useRef` 或 `useMemo`？

**我：** 需要**触发重新渲染**用 `useState`；只是**跨渲染持久存值**且**不触发渲染**用 `useRef`（比如保存上一次的值/计时器句柄）；**昂贵计算缓存**或**派生数据**用 `useMemo`，**函数稳定引用**用 `useCallback`，否则子组件容易反复重渲。

场景 C - `useEffect` 依赖与常见坑

**面试官：** `useEffect` 最容易踩的坑？

**我：** 三个：

1. **漏依赖**：依赖数组少了变量导致读到旧值；
2. **不必要的副作用**：可以放到渲染阶段的纯计算不该进 effect；
3. **竞态**：发请求没做取消/标记，晚返回覆盖早返回。实践里我会**把数据获取放到事件/路由层**，并在 effect 中清理 AbortController。

场景 D - 受控 vs 非受控表单

**面试官：** 复杂表单你怎么选？

**我：** 简单场景用**受控组件**（值走 state）；大表单或高频输入用**非受控 + 表单库**（如 `react-hook-form`）减少重渲，**校验放 schema**（Zod/Yup），提交时统一做**客户端 + 服务器**双校验。TS 上事件类型常用：`React.ChangeEvent<HTMLInputElement>`、`React.FormEvent<HTMLFormElement>`。

场景 E - Props 的类型建模

**面试官：** 复用已有组件的 Props，你怎么在 TS 里写？

**我：** 用工具类型组合，比如：

```ts
type ButtonProps = React.ComponentProps<'button'> & { loading?: boolean };
type CardTitleProps = Pick<CardProps, 'title' | 'subtitle'>;
type ApiResult<T> = { data: T; error?: string };
```

避免 `React.FC` 的隐式 `children` 争议，**显式声明 `children?: React.ReactNode`** 更清晰。默认值用函数参数默认值，不再用 `defaultProps`。

场景 F - 错误边界与异常收敛

**面试官：** 函数组件如何做错误边界？

**我：** 错误边界目前仍是**类组件**（`componentDidCatch`），我会写一个通用 `ErrorBoundary` 包裹路由级或关键区域；函数组件内部用 `try/catch` 只能抓**事件处理**，抓不到渲染期错误。上报走 **Sentry**，带上 **release/environment/traceId**，方便和后端串联。

场景 G - 列表渲染与性能

**面试官：** 大列表卡顿怎么治？

**我：** 三点：

1. **稳定的 key**（业务 id），避免索引当 key；
2. **虚拟列表**（如 react-window）减少真实节点数；
3. 把**纯展示子组件 `React.memo`**，并用 `useCallback/useMemo` 稳定 props 引用，配合选择性水合/分片渲染优化首屏。

### 路由与表单（React Router v6、嵌套路由/懒加载、表单校验与数据流）

### SSR / CSR / 选择性水合（取舍与指标：TTFB/TTI/CLS；岛屿架构要点）

### CSP / 缓存策略（CSP `nonce/hash`、Cache-Control/ETag、CDN/Edge/浏览器多级缓存）

### Sentry 埋点与错误上报（前后端统一 TraceID、Source Map、错误分级与去噪）

### 环境变量管理（构建期 vs 运行期、公开变量白名单、CI/CD 注入、敏感信息不入包）

---

## Step 3 - 补充 `architecture.md` 新小节

Web Rendering & Caching Strategy（SSR/CSR/Selective Hydration + 安全与缓存）

---

## Step 4 - 1 分钟英文口语

---

## Step 5 - Week 8 Day 1（简历日）改写清单
