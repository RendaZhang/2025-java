<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 2 - 数据库与缓存：索引 + 事务 + 一致性打底（DB + Cache 基本面）](#day-2---%E6%95%B0%E6%8D%AE%E5%BA%93%E4%B8%8E%E7%BC%93%E5%AD%98%E7%B4%A2%E5%BC%95--%E4%BA%8B%E5%8A%A1--%E4%B8%80%E8%87%B4%E6%80%A7%E6%89%93%E5%BA%95db--cache-%E5%9F%BA%E6%9C%AC%E9%9D%A2)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [Step 1：LeetCode 算法训练](#step-1leetcode-%E7%AE%97%E6%B3%95%E8%AE%AD%E7%BB%83)
    - [题 A - LC141. Linked List Cycle（快慢指针 / Floyd）](#%E9%A2%98-a---lc141-linked-list-cycle%E5%BF%AB%E6%85%A2%E6%8C%87%E9%92%88--floyd)
    - [题 B - LC739. Daily Temperatures（单调栈）](#%E9%A2%98-b---lc739-daily-temperatures%E5%8D%95%E8%B0%83%E6%A0%88)
    - [题 C - LC20. Valid Parentheses（栈）](#%E9%A2%98-c---lc20-valid-parentheses%E6%A0%88)
  - [Step 2：数据库与缓存](#step-2%E6%95%B0%E6%8D%AE%E5%BA%93%E4%B8%8E%E7%BC%93%E5%AD%98)
    - [索引选型与失效（B+Tree、组合索引、覆盖索引、左前缀）](#%E7%B4%A2%E5%BC%95%E9%80%89%E5%9E%8B%E4%B8%8E%E5%A4%B1%E6%95%88btree%E7%BB%84%E5%90%88%E7%B4%A2%E5%BC%95%E8%A6%86%E7%9B%96%E7%B4%A2%E5%BC%95%E5%B7%A6%E5%89%8D%E7%BC%80)
    - [事务与隔离级别：RC / RR；MVCC 的一致性读 vs 当前读；如何避免幻读与超卖](#%E4%BA%8B%E5%8A%A1%E4%B8%8E%E9%9A%94%E7%A6%BB%E7%BA%A7%E5%88%ABrc--rrmvcc-%E7%9A%84%E4%B8%80%E8%87%B4%E6%80%A7%E8%AF%BB-vs-%E5%BD%93%E5%89%8D%E8%AF%BB%E5%A6%82%E4%BD%95%E9%81%BF%E5%85%8D%E5%B9%BB%E8%AF%BB%E4%B8%8E%E8%B6%85%E5%8D%96)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 2 - 数据库与缓存：索引 + 事务 + 一致性打底（DB + Cache 基本面）

## 今日目标

1. **算法**：链表/栈队列方向各 1 题，覆盖**快慢指针**与**单调栈**。
2. **面试知识**：搞清**MySQL 索引与事务**的“能与不能”，以及**读写分离 + 缓存一致性**（穿透/击穿/雪崩）的工程化解法，沉淀要点到 `QBANK.md`。
3. **英语**：1 分钟英文口述 —— “如何定位慢查询与索引失效”。

---

## Step 1：LeetCode 算法训练

### 题 A - LC141. Linked List Cycle（快慢指针 / Floyd）

**思路提示**

- 两个指针：`slow = slow.next`；`fast = fast.next.next`。若有环一定相遇；`fast` 或 `fast.next` 为空即可判无环。
- 口述要点：为什么相遇？（速度差 1，每轮把距离缩小 1）。

**Java 模板**

```java
public boolean hasCycle(ListNode head) {
    if (head == null || head.next == null) return false;
    ListNode slow = head, fast = head.next;
    while (fast != null && fast.next != null) {
        if (slow == fast) return true;
        slow = slow.next;
        fast = fast.next.next;
    }
    return false;
}
```

**易错点**

- `while` 条件必须检查 `fast` 和 `fast.next`；初始指针相对位置别都放 `head`（放也行，但第一次循环先移动再比较）。
- 单节点/双节点边界。

**复杂度**：Time O(n)，Space O(1)。

**自测用例**

- `[] -> false`，`[1] -> false`，`[1,2] (no cycle) -> false`
- `1->2->3->4->2 (cycle) -> true`

> 进阶：返回入环点可用 Floyd 第二阶段（相遇后一指针回 `head`，同步步进，下一次相遇处即入环点）。

### 题 B - LC739. Daily Temperatures（单调栈）

**思路提示**

- 维护**递减栈**（存索引）。遍历 `i`：当 `T[i] > T[stack.top]` 时，弹出 `j` 并设置 `ans[j] = i - j`；最后把 `i` 入栈。
- 口述要点：为什么用索引而非值？（需计算距离）；为什么递减？（遇到升温才有解）。

**Java 模板**

```java
public int[] dailyTemperatures(int[] T) {
    int n = T.length;
    int[] ans = new int[n];
    Deque<Integer> st = new ArrayDeque<>();
    for (int i = 0; i < n; i++) {
        while (!st.isEmpty() && T[i] > T[st.peek()]) {
            int j = st.pop();
            ans[j] = i - j;
        }
        st.push(i);
    }
    return ans;
}
```

**易错点**

- 比较用 `>` 而不是 `>=`（相等不算升温）。
- 栈里存**索引**，出栈时写答案。
- 递减栈的“递减”指**温度值递减**，不是索引递减（索引天然递增）。

**复杂度**：Time O(n)（每索引最多入栈/出栈一次），Space O(n)。

**自测用例**

- `[73,74,75,71,69,72,76,73] -> [1,1,4,2,1,1,0,0]`
- 全降序：`[5,4,3,2,1] -> [0,0,0,0,0]`
- 含相等：`[70,70,71] -> [2,1,0]`

### 题 C - LC20. Valid Parentheses（栈）

**思路提示**

- 扫描字符：遇到左括号入栈；遇到右括号，检查栈顶能否匹配；结束时栈必须空。

**Java 模板**

```java
public boolean isValid(String s) {
    Deque<Character> st = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        if (c=='('||c=='{'||c=='[') st.push(c);
        else {
            if (st.isEmpty()) return false;
            char t = st.pop();
            if ((c==')' && t!='(') || (c=='}' && t!='{') || (c==']' && t!='[')) return false;
        }
    }
    return st.isEmpty();
}
```

**易错点**

- 先判空栈再 `pop`；只允许括号字符（本题输入保证）。
  **复杂度**：O(n)/O(n)。

**自测**

- `"()[]{}" -> true`，`"(]" -> false`，`"([)]" -> false`，`"{[]}" -> true`。

---

## Step 2：数据库与缓存

### 索引选型与失效（B+Tree、组合索引、覆盖索引、左前缀）

> **索引选型与失效**：等值在前、范围在后，`(user_id, status, created_at DESC)` 覆盖列表页；避免函数包列与隐式转换；选择度差用**组合索引**提升；热查询必要时**拆专用索引**，不用指望索引合并；用 `EXPLAIN` 盯 `rows/key/filesort`。

场景题

**面试官**

“你在（深圳市凡新科技 / 麦克尔斯深圳）做订单与库存查询时，最常见的一条查询长这样：
按 `user_id + status` 过滤，按 `created_at DESC` 排序，取最近 20 条。偶尔还会加上 `channel`（Shopify/WooCommerce）。你会怎么设计**索引**？在什么情况下**索引会失效**，你怎么避免？”

**你**

“我会直接上一个**组合索引**：`(user_id, status, created_at DESC)`。

- 这样 `WHERE user_id=? AND status=?` 命中**最左前缀**，`ORDER BY created_at DESC LIMIT 20` 能做到**顺序读**，几乎不用再做 filesort。
- 如果查询只返回列表页字段（比如 `order_id, total, created_at, status`），我会把这些字段也纳入**覆盖索引**（只要都在二级索引上，查询就**不回表**）。
- 如果经常用到 `channel` 维度，我会根据**查询比例**和**选择度**决定是否把索引改成 `(user_id, channel, status, created_at DESC)`，或者再开一条以 `channel` 为第二位的索引（避免一个索引承担所有模式）。
- 文本模糊搜索我不会指望 B+Tree，`LIKE '%kw%'` 这种我会用**倒排/全文索引**或**ES**，避免全表扫。”

“**可能失效的坑**我会提前规避：

- **函数/计算包住列**：`DATE(created_at)=?` 会让索引失效，我改成 `created_at >= '2025-09-01 00:00:00' AND < '2025-09-02 00:00:00'`。
- **隐式类型转换**：`user_id` 是 `BIGINT`，但参数传字符串，可能走不到索引，我会在 DAL 层**强类型**。
- **范围列放前面**：`WHERE created_at > ? AND status = ?`，如果把 `created_at` 放在索引前面，后面的列就用不上索引了；所以把**等值列在前，范围列靠后**。
- **选择度差**：`status` 只有 3 个值，单列索引没意义，要靠 `user_id + status` 的组合来提高选择度。
- **回表过多**：遇到‘扫 10 万行再回表’的情况，我会把列表页必要字段放进覆盖索引里，或者改成**先查主键再回表**的两段式。”

“上线前我会用 `EXPLAIN` 看 `type`、`rows`、`key` 和 `Using index/Using filesort`；`rows` 过大就说明选择度不行。慢查询里还会看**扫描行数**与**回表次数**，必要时调整索引顺序或再拆一个更贴近热查询的索引。”

追问 1（实战细节）

**面试官：**“如果同样的查询，偶尔要先按 `channel` 过滤再按 `user_id` 呢？你是加一个新的 `(channel, user_id, status, created_at)` 还是用索引合并？”

**你：**

“我一般会**加一条新索引**，不要指望索引合并（AND 合并常常不稳定且代价高）。判断标准是**这条访问模式的占比**是否值得一条新索引。如果比例不高，我会把这类查询**引导到报表库/异步计算**，避免在 OLTP 上堆太多索引。”

追问 2（排序与覆盖）

**面试官：**“`ORDER BY created_at DESC` 一定能用上索引排序吗？什么情况下会退化成 filesort？”

**你：**

“有两个常见退化点：

1. **排序列不在索引的连续前缀里**（或者方向不一致）；
2. **查询列不被索引覆盖**且回表顺序与排序不一致，优化器可能选择 filesort。
   所以我会把 `created_at DESC` 放在组合索引的最后一位，并尽量让列表页**覆盖索引**，这样基本避免 filesort。”

追问 3（真实案例）

**面试官：**“讲一个你线上遇到的‘索引失效’问题。”

**你：**
“有一次活动页报慢，我们发现 SQL 写了 `WHERE DATE(created_at)=CURDATE()`，直接把索引废了；改成半开区间后 P95 从 900ms 掉到 80ms。还有一次是 `user_id` 参数是字符串，发生了**隐式转换**，修掉参数类型后 `rows` 从十几万降到几百。”

### 事务与隔离级别：RC / RR；MVCC 的一致性读 vs 当前读；如何避免幻读与超卖

> **事务与隔离**：读走 **RC + 一致性读** 提并发；写用**条件更新**（`UPDATE … WHERE qty>=?`）或**当前读 + 明确锁**保正确；RR 防幻靠 Next-Key，但更易死锁；唯一约束/插入即占位 > 锁；控制**短事务、统一加锁顺序、失败可重试**。

**场景题**

**面试官**

“你在（深圳市凡新科技 / 麦克尔斯深圳）做下单与库存扣减时，既要抗高并发，又要保证**不超卖**、**不出现脏数据**。MySQL/InnoDB 的默认隔离级别是 **可重复读（RR）**，很多团队又把线上切到 **读已提交（RC）** 提升并发。你会怎么选？具体到 **MVCC 的一致性读** 和 **当前读（锁定读）** 语义，你怎么落地？”

**你（口语化回答示范）**

“我把读写分两类：

- **一致性读（snapshot read）**：普通 `SELECT`，用 MVCC 读历史快照。RR 下‘同一事务多次查到的是同一份视图’，避免**不可重复读**；RC 下‘每条语句读最新已提交’，更‘新鲜’，但可能前后两次结果不同。
- **当前读（locking read）**：`SELECT … FOR UPDATE/ FOR SHARE`、`UPDATE/DELETE`，会加锁。RR 下会用**间隙锁/Next-Key 锁**来防止范围内的**幻读**；RC 下默认不加间隙锁，更多用**记录锁**，并发更高，但范围查询更容易出现幻写/插入竞争。

**我的选择**：

- **读路径**（列表/详情/报表）多用 **RC + 一致性读**，减少锁冲突，实时性也更好；
- **写路径**（下单扣减/配额/优惠券核销）不用纠结隔离级别，直接用 **‘条件更新’原子写法**或**当前读 + 明确锁**来保证正确性。”

**避免超卖的两种写法**

1. **条件更新（推荐）**：

```sql
UPDATE stock
SET qty = qty - :n
WHERE sku = :sku AND qty >= :n;
-- 受影响行数 = 1 才算成功；=0 表示库存不足
```

- 优点：不需要先 `SELECT FOR UPDATE`，天然**原子**，在 RC/RR 下都安全，锁粒度小、冲突少。
- 适用于“数值扣减”类场景（库存、配额、次数）。

2. **当前读 + 再写**：

```sql
START TRANSACTION;
SELECT qty FROM stock WHERE sku=:sku FOR UPDATE; -- 锁住这行（RR 下还会锁间隙）
-- 校验与扣减
UPDATE stock SET qty = qty - :n WHERE sku=:sku;
COMMIT;
```

- 适合需要**读取多个字段**再决定写入的业务；
- RR 下能靠 Next-Key 防**幻写**，但更可能**死锁**，需要**重试机制**与**短事务**控制。

**常见坑与我的做法**

- **幻读/重复写**：RC 下范围条件的 `SELECT … FOR UPDATE` 不锁间隙，可能插入“新纪录”造成幻影 → 改成**条件更新**或**唯一约束 + INSERT IGNORE/ON DUPLICATE KEY** 防重复。
- **长事务**：RR 下长事务会让 MVCC 的 undo 链变长，吞吐变差、历史回收受阻 → 严控**事务边界**，把计算挪到事务外；UI 上避免“开事务等用户操作”。
- **死锁**：并发写相同/相邻键很常见 → 统一**加锁顺序**（按主键/索引顺序）、**缩短事务**、**减少回表**（覆盖索引），并在代码里**捕获死锁并指数退避重试**。
- **隐式类型/函数包列**导致索引失效 → 在写路径尤其要**强类型**和**区间写法**（参见 Day2#1 索引策略）。

**面试官追问 1**

“如果要‘按条件占位’避免同一用户重复下单，你选 RR 还是 RC？”

**你**

“都可以，我更倾向**唯一约束**+**插入即占位**，规避隔离级别差异：

```sql
-- 唯一键 (user_id, order_uuid)
INSERT INTO orders(user_id, order_uuid, status, ...)
VALUES(:u, :oid, 'PENDING', ...)
ON DUPLICATE KEY UPDATE touched_at = NOW(); -- 幂等
```

或者对核销码/优惠券也是**唯一约束**来杜绝重复消费，比锁更干脆。”

**面试官追问 2**

“实际线上你怎么定位隔离级别相关的问题？”

**你**

“我会：

- 打开 `EXPLAIN` 看是否走到**覆盖索引**，减少锁范围；
- 用 `performance_schema`/慢日志看**等待事件**（锁等待/扫描行数）；
- 碰到死锁，抓 `SHOW ENGINE INNODB STATUS`，审计‘谁先锁了谁’→ 调整**加锁顺序**或改成**条件更新**；
- 对高争用表建**合理分区/热点拆分**，降低冲突；
- 定期巡检**长事务**、**历史版本长度**，看到异常就查出是哪个业务把事务拉得太长。”

**真实案例**

“凡新那边高峰期遇到过**优惠券重复核销**风控误判，我们一开始用 `SELECT … FOR UPDATE` 查是否核销过，再写入；在 RC 下因为不锁间隙，边界条件下出现并发插入比赛的窗口。后来改成**唯一索引 + 插入即占位**，问题消失。
在麦克尔斯那边，做**库存预留**时从 RR 改 RC 后，读写冲突少了一半，但我们把关键写全部改为**条件更新模式**，再配幂等与重试，稳定住了。”
