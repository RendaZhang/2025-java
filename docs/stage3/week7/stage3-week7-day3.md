<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 3 - 消息与一致性：Outbox & 去重 & 重试闭环](#day-3---%E6%B6%88%E6%81%AF%E4%B8%8E%E4%B8%80%E8%87%B4%E6%80%A7outbox--%E5%8E%BB%E9%87%8D--%E9%87%8D%E8%AF%95%E9%97%AD%E7%8E%AF)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [Step 1 - 算法（树遍历）](#step-1---%E7%AE%97%E6%B3%95%E6%A0%91%E9%81%8D%E5%8E%86)
    - [LC94. Binary Tree Inorder Traversal（迭代中序 + 栈）](#lc94-binary-tree-inorder-traversal%E8%BF%AD%E4%BB%A3%E4%B8%AD%E5%BA%8F--%E6%A0%88)
    - [LC102. Binary Tree Level Order Traversal（层序 BFS）](#lc102-binary-tree-level-order-traversal%E5%B1%82%E5%BA%8F-bfs)
    - [LC145. Binary Tree Postorder Traversal（迭代后序）](#lc145-binary-tree-postorder-traversal%E8%BF%AD%E4%BB%A3%E5%90%8E%E5%BA%8F)
    - [LC102 高质量复盘](#lc102-%E9%AB%98%E8%B4%A8%E9%87%8F%E5%A4%8D%E7%9B%98)

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

### LC145. Binary Tree Postorder Traversal（迭代后序）

**思路提示（双栈法最稳）**

- 栈1出栈即入栈2；先压左再压右，最后栈2逆序就是后序。
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

### LC102 高质量复盘

- **Pattern**：队列分层 BFS
- **Intuition**：用“本层 size”截断，保证每层聚合
- **Steps**：入队根→循环直到队空；每轮缓存 `sz`，弹 `sz` 次、收集值、把子节点入队
- **Complexity**：Time O(n)，Space O(w)
- **Edge Cases**：空树、单节点、极度不平衡树
- **Mistakes & Fix**：动态读 `q.size()` 导致漏/重复 → 先缓存 `sz`

---
