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
