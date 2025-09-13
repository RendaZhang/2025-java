<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 3 - 消息与一致性：Outbox & 去重 & 重试闭环](#day-3---%E6%B6%88%E6%81%AF%E4%B8%8E%E4%B8%80%E8%87%B4%E6%80%A7outbox--%E5%8E%BB%E9%87%8D--%E9%87%8D%E8%AF%95%E9%97%AD%E7%8E%AF)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [Step 1 - 算法（树遍历）](#step-1---%E7%AE%97%E6%B3%95%E6%A0%91%E9%81%8D%E5%8E%86)
    - [LC94. Binary Tree Inorder Traversal（迭代中序 + 栈）](#lc94-binary-tree-inorder-traversal%E8%BF%AD%E4%BB%A3%E4%B8%AD%E5%BA%8F--%E6%A0%88)
    - [LC102. Binary Tree Level Order Traversal（层序 BFS）](#lc102-binary-tree-level-order-traversal%E5%B1%82%E5%BA%8F-bfs)
    - [LC102 高质量复盘](#lc102-%E9%AB%98%E8%B4%A8%E9%87%8F%E5%A4%8D%E7%9B%98)
    - [LC145. Binary Tree Postorder Traversal（迭代后序）](#lc145-binary-tree-postorder-traversal%E8%BF%AD%E4%BB%A3%E5%90%8E%E5%BA%8F)
  - [Step 2 - 消息与一致性（Outbox & 去重 & 重试闭环）](#step-2---%E6%B6%88%E6%81%AF%E4%B8%8E%E4%B8%80%E8%87%B4%E6%80%A7outbox--%E5%8E%BB%E9%87%8D--%E9%87%8D%E8%AF%95%E9%97%AD%E7%8E%AF)
    - [Outbox（事务外箱）& 本地事务边界](#outbox%E4%BA%8B%E5%8A%A1%E5%A4%96%E7%AE%B1-%E6%9C%AC%E5%9C%B0%E4%BA%8B%E5%8A%A1%E8%BE%B9%E7%95%8C)
    - [幂等消费与去重键：表/Redis 实战与失败补偿](#%E5%B9%82%E7%AD%89%E6%B6%88%E8%B4%B9%E4%B8%8E%E5%8E%BB%E9%87%8D%E9%94%AE%E8%A1%A8redis-%E5%AE%9E%E6%88%98%E4%B8%8E%E5%A4%B1%E8%B4%A5%E8%A1%A5%E5%81%BF)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 3 - 消息与一致性：Outbox & 去重 & 重试闭环

## 今日目标

- **算法**：熟练二叉树遍历（前/中/后序、层序），完成 **2–3 道 LeetCode 树题**，至少 1 题做“可口述的复盘”。
- **面试知识**：把**消息与最终一致性**核心要点沉淀成 **5–7 条可复用 bullets**（Outbox、幂等消费、去重键、退避重试、DLQ/停车场、顺序性/分区键、Exactly-once 的工程化取舍），写进 `QBANK.md`。
- **英语**：准备一段 **≈60s** 的口语回答：*“How we guarantee eventual consistency with outbox and idempotent consumers.”*

---

## Step 1 - 算法（树遍历）

### LC94. Binary Tree Inorder Traversal（迭代中序 + 栈）

**思路提示**

- 模板：一路把左子树入栈；弹栈访问中间节点；转向右子树。
- 循环条件：`while (curr != null || !st.isEmpty())`。
- 口述关键：中序 = 左→中→右；栈记录回溯路径。

**Java 模板**

```java
public List<Integer> inorderTraversal(TreeNode root) {
    List<Integer> ans = new ArrayList<>();
    Deque<TreeNode> st = new ArrayDeque<>();
    TreeNode cur = root;
    while (cur != null || !st.isEmpty()) {
        while (cur != null) {
            st.push(cur);
            cur = cur.left;
        }
        cur = st.pop();
        ans.add(cur.val);
        cur = cur.right;
    }
    return ans;
}
```

**易错点**

- 忘记外层 `|| !st.isEmpty()` 导致遗漏右链。
- 把访问 `ans.add(cur.val)` 放错到内层 while。

**复杂度**：O(n)/O(h)。

### LC102. Binary Tree Level Order Traversal（层序 BFS）

**思路提示**

- 队列按层推进，记录当前层 `size`，循环 `size` 次出队即可分层。
- 口述关键：层与层隔离靠“本层大小”。

**Java 模板**

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> res = new ArrayList<>();
    if (root == null) return res;
    Deque<TreeNode> q = new ArrayDeque<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int sz = q.size();
        List<Integer> level = new ArrayList<>(sz);
        for (int i = 0; i < sz; i++) {
            TreeNode n = q.poll();
            level.add(n.val);
            if (n.left != null)  q.offer(n.left);
            if (n.right != null) q.offer(n.right);
        }
        res.add(level);
    }
    return res;
}
```

**易错点**

- 用 `for (int i=0; i<q.size(); i++)` 动态读 size 会出错；要先缓存 `sz`。
  **复杂度**：O(n)/O(w)（w 为最大层宽）。

### LC102 高质量复盘

- **Pattern**：队列分层 BFS
- **Intuition**：用“本层 size”截断，保证每层聚合
- **Steps**：入队根→循环直到队空；每轮缓存 `sz`，弹 `sz` 次、收集值、把子节点入队
- **Complexity**：Time O(n)，Space O(w)
- **Edge Cases**：空树、单节点、极度不平衡树
- **Mistakes & Fix**：动态读 `q.size()` 导致漏/重复 → 先缓存 `sz`

### LC145. Binary Tree Postorder Traversal（迭代后序）

**思路提示（双栈法最稳）**

栈 1 出栈即入栈 2；先压左再压右，最后栈 2 逆序就是后序。

**Java 模板**

```java
public List<Integer> postorderTraversal(TreeNode root) {
    List<Integer> ans = new ArrayList<>();
    if (root == null) return ans;
    Deque<TreeNode> s1 = new ArrayDeque<>(), s2 = new ArrayDeque<>();
    s1.push(root);
    while (!s1.isEmpty()) {
        TreeNode n = s1.pop();
        s2.push(n);
        if (n.left != null)  s1.push(n.left);
        if (n.right != null) s1.push(n.right);
    }
    while (!s2.isEmpty()) ans.add(s2.pop().val);
    return ans;
}
```

**复杂度**：O(n)/O(n)。

反前序 + 头插：

```java
public List<Integer> postorderTraversal(TreeNode root) {
    LinkedList<Integer> res = new LinkedList<>();
    if (root == null) return res;
    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        res.addFirst(node.val);        // 头插，等价于最终 reverse
        if (node.left != null)  stack.push(node.left);
        if (node.right != null) stack.push(node.right);
    }
    return res;
}
```

---

## Step 2 - 消息与一致性（Outbox & 去重 & 重试闭环）

### Outbox（事务外箱）& 本地事务边界

> Outbox：单库事务落数据+事件，Publisher 异步发布；至少一次发送 + 幂等消费（`eventId`/`aggregateId+version` 去重）≈ 几乎一次；分区键保证同聚合顺序；失败退避重试，CDC/归档保性能。

场景题

**面试官**

“下单成功要**占库存**并**通知**支付/仓库/搜索。如何保证**写数据库**和**发消息**‘要么都成功，要么都不成功’，避免双写不一致？你在（深圳市凡新科技 / 麦克尔斯深圳）是怎么落地的？”

**你：**

“我们用**事务外箱**（Outbox）把‘数据变更’和‘事件生成’放到**同一个本地事务**里：

- **应用事务**里做两步：① `INSERT/UPDATE` 业务表（orders/stock…）② `INSERT outbox(event)`。只要事务提交，**两者一定同时落地**。
- 事务外的**Publisher**轮询/CDC 读取 `outbox`：`SELECT ... FOR UPDATE SKIP LOCKED LIMIT N`，发布到 Kafka/SQS/Redis Stream，成功后把这行标记 `SENT`，失败就**退避重试**并回写 `attempts/next_retry_at`。
- **消费端幂等**：事件里自带 `eventId`（或 `aggregateId+version`），消费者先查 `processed_events`（唯一键），**见过就直接 ACK**，没见过才处理并插入标记。
  总体语义是**至少一次发送 + 幂等消费 = 几乎一次（effectively-once）**。我们在凡新下单→扣减库存→异步出库通知这条链路就是这么做的；在麦克尔斯，作品发布→索引更新→通知订阅者也同理。”

**简化表结构（示意）**

```sql
CREATE TABLE outbox (
  id           CHAR(36) PRIMARY KEY,      -- eventId (UUID)
  aggregate_id CHAR(36) NOT NULL,         -- 订单/商品ID
  aggregate_type VARCHAR(32) NOT NULL,    -- ORDER / STOCK ...
  event_type   VARCHAR(64) NOT NULL,      -- OrderCreated / StockReserved...
  payload      JSON NOT NULL,
  version      INT NOT NULL,              -- 聚合版本，用于顺序/去重
  status       ENUM('NEW','SENT','FAILED') DEFAULT 'NEW',
  attempts     INT DEFAULT 0,
  available_at DATETIME NOT NULL,         -- 下次可重试时间（退避）
  created_at   DATETIME NOT NULL
);

CREATE TABLE processed_events (
  event_id CHAR(36) PRIMARY KEY,          -- 去重键
  processed_at DATETIME NOT NULL
);
```

**应用层伪代码**

```java
// Tx begin
insertOrder(...);                 // 业务写
insertOutbox(event(orderId,...)); // 同库插入消息
// Tx commit  => 原子性成立
```

**Publisher 轮询（要点）**

- 批量拉取 `status=NEW AND available_at<=now()`，用 `FOR UPDATE SKIP LOCKED` 防并发争抢；
- 发布成功 → `status=SENT`；失败 → `attempts++`，按 `min(backoff*2^attempts, cap)` 回写 `available_at`；
- **崩溃边界**：如果“已发布但未标记 SENT”重启后会再发一次，所以**消费者必须幂等**。

**顺序性与分区**

- 需要**同一订单的事件按序**：Kafka 用 `partitionKey=orderId`；SQS 用 **FIFO**，`messageGroupId=orderId`；Redis Stream 按 stream per-aggregate 或在消费者端序列化处理。
- 跨聚合全局顺序一般**不保证**，用**因果字段**（version/timestamp）校正。

**真实落地小例子**

- 凡新：`OrderCreated` 与 `StockReserveRequested` 一起落库；Publisher 发 SQS，消费者是库存服务；出现网络抖动时，**重复消息**被 `processed_events` 吸收，不再重复扣减。
- 麦克尔斯：作品发布事件走 Kafka，搜索索引消费者先做**去重**再写 ES；Publisher 用**退避+抖动**，避免抖动期把队列压爆。

**追问 1：为什么不用 DB 里直接调用消息系统事务？**

“跨资源的**分布式事务**（2PC）复杂且脆弱，Outbox 把一致性**收敛在单库事务**里，外围只需‘至少一次 + 幂等’，工程成本更低、恢复性更好。”

**追问 2：如果消息量很大，轮询会不会很慢？**

“生产里我们更倾向**CDC（如 Debezium）**或**分片轮询**：按时间或主键区间扫描；并用**归档/TTL**压缩 `SENT` 行，保持表小而快。”

### 幂等消费与去重键：表/Redis 实战与失败补偿

> **幂等消费**：`eventId`（或 `aggregateId+version`）做去重键；**同库事务**里先插 `processed_events` 再执行业务写；写法用**条件更新/UPSERT/版本检查**做到可重放；Redis `SETNX+TTL` 快速挡重复，DB 约束兜底；失败走 **DLQ+重放**，顺序用**分区键/FIFO**。

场景题

**面试官**

“支付平台和库存系统都会**重复投递**事件（网络抖动/重试）。你在（深圳市凡新科技 / 麦克尔斯深圳）如何保证**消费端幂等**？具体怎么做**去重键**、**落库顺序**、**失败补偿**？”

**你：**

“我的做法是把 ‘**去重 + 业务落库**’ 放进同一个本地事务里，保证 ‘**要么都成功，要么都回滚**’，并且提供**双层去重**（DB 强约束 + Redis 快速挡重复）。

1. **去重键**
   - 统一使用 `eventId`（UUID）或 `aggregateId + version` 作为**全局唯一键**。
   - **DB 层**建 `processed_events(event_id PK)`，天然幂等；
   - **Redis 快速去重**：`SETNX de:{eventId} 1 EX <TTL>` 抢到再处理，没抢到直接 ACK（避免同一时刻并发重复执行）。
2. **同库事务顺序**（Inbox 模式）
    ```sql
    -- 在一次事务内完成两件事：业务变更 & 去重落账
    BEGIN;
    -- ① 先插 processed_events（如果已存在直接报错/返回）
    INSERT INTO processed_events(event_id, processed_at) VALUES(:eid, NOW());
    -- ② 再做业务落库（UPDATE/INSERT ...）
    UPDATE stock SET qty = qty - :n
        WHERE sku=:sku AND qty >= :n;  -- 条件更新，重复执行也不会二次扣
    COMMIT;
    ```
    > 这样如果第二次收到同一事件，`INSERT processed_events` 会**冲突**，直接回滚，业务不会再落一次。
3. **幂等写法**
   - **条件更新**：`UPDATE ... WHERE qty>=:n`；
   - **UPSERT**：`INSERT ... ON DUPLICATE KEY UPDATE ...`（比如“首次创建订单，重复则更新状态/时间”）；
   - **版本检查**：`WHERE version = :old` 成功后 `version=version+1`，重复事件因版本不匹配**零影响**。
4. **失败补偿**
   - **指数退避 + 尝试上限**：消费失败 `attempts++`，超过阈值（如 10 次）投递到 **DLQ/停车场**；
   - **人工/自动重放**：DLQ 可按 `eventId` 批量重放；
   - **顺序性**：按 `aggregateId` 做**分区键/FIFO**，单聚合同一消费者串行处理，避免乱序导致的版本冲突。

在凡新：支付回调/库存事件会**重复 3–5 次**，我们用 `processed_events` 做硬去重，外层再加 Redis TTL 去重，**99% 的重复在入口被挡**；在麦克尔斯：作品索引更新走 Kafka，消费者先插 `processed_events`，再写 ES，重复消息直接被忽略，**不会重复建索引**。”

追问 1（Redis 被逐出或丢失怎么办）

**面试官：**“如果 Redis 因为内存淘汰把 `de:{eventId}` 清掉了，会不会又重复处理？”

**你：**

“不会，因为**DB 层还有硬约束**。Redis 只是**快速挡**；真正的幂等保证靠 `processed_events` 主键或**业务唯一约束**（例如 `user_id+coupon_id` 唯一）。即使 Redis 丢了，DB 也会拒绝重复写。”

追问 2（顺序与并发）

**面试官：**“同一 `orderId` 的事件要按顺序消费，怎么做？”

**你：**

“把 `orderId` 当作**分区键**（Kafka partition / SQS messageGroupId），确保同聚合到同一队列分区，由**单个消费者串行**处理。确需并发就用**乐观版本**：版本不匹配的更新返回 0 行，进入**重试/停靠**。”

追问 3（真实事故）

**面试官：**“讲个你们因为没做幂等导致的事故。”

**你：**

“早期库存扣减没做条件更新，重复消息会**二次扣**。修复后改成‘`processed_events` PK 去重 + `UPDATE … WHERE qty>=`’，重复消息变成幂等重放，工单直接清零。”
