<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Day 5 - 可观测与发布（指标·告警·灰度回滚）](#day-5---%E5%8F%AF%E8%A7%82%E6%B5%8B%E4%B8%8E%E5%8F%91%E5%B8%83%E6%8C%87%E6%A0%87%C2%B7%E5%91%8A%E8%AD%A6%C2%B7%E7%81%B0%E5%BA%A6%E5%9B%9E%E6%BB%9A)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [Step 1 - 算法：堆 / 数据流](#step-1---%E7%AE%97%E6%B3%95%E5%A0%86--%E6%95%B0%E6%8D%AE%E6%B5%81)
    - [LC703. Kth Largest Element in a Stream（数据流第 K 大）](#lc703-kth-largest-element-in-a-stream%E6%95%B0%E6%8D%AE%E6%B5%81%E7%AC%AC-k-%E5%A4%A7)
    - [LC378. Kth Smallest in a Sorted Matrix（有序矩阵第 K 小）](#lc378-kth-smallest-in-a-sorted-matrix%E6%9C%89%E5%BA%8F%E7%9F%A9%E9%98%B5%E7%AC%AC-k-%E5%B0%8F)
    - [LC378 高质量复盘](#lc378-%E9%AB%98%E8%B4%A8%E9%87%8F%E5%A4%8D%E7%9B%98)
    - [LC373. Find K Pairs with Smallest Sums（两数组和最小的 K 对）](#lc373-find-k-pairs-with-smallest-sums%E4%B8%A4%E6%95%B0%E7%BB%84%E5%92%8C%E6%9C%80%E5%B0%8F%E7%9A%84-k-%E5%AF%B9)
    - [LC295. Find Median from Data Stream（数据流中位数）](#lc295-find-median-from-data-stream%E6%95%B0%E6%8D%AE%E6%B5%81%E4%B8%AD%E4%BD%8D%E6%95%B0)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Day 5 - 可观测与发布（指标·告警·灰度回滚）

## 今日目标

- **算法**：继续巩固“堆 / 优先队列（Top-K、合并流/数据流）”，完成 2–3 题并能口述复杂度与边界。
- **面试知识**：梳理日志/指标/追踪（OTel/Prom/Grafana 基本面），能给出一套 SLO 与告警阈值，并讲清楚发布与回滚（灰度/蓝绿）的关键守卫项；要点沉淀到 `QBANK.md`，并给 `architecture.md` 补 1 小节。
- **英语**：练习 1 分钟 **Incident Postmortem** 的结构化口述。

---

## Step 1 - 算法：堆 / 数据流

### LC703. Kth Largest Element in a Stream（数据流第 K 大）

**思路**

维护**大小为 k 的最小堆**；堆内始终是“当前最大的 k 个数”，堆顶即第 k 大。

**Java 模板**

```java
class KthLargest {
    private final int k;
    private final PriorityQueue<Integer> pq = new PriorityQueue<>(); // min-heap

    public KthLargest(int k, int[] nums) {
        this.k = k;
        for (int x : nums) add(x);
    }
    public int add(int val) {
        if (pq.size() < k) pq.offer(val);
        else if (val > pq.peek()) { pq.poll(); pq.offer(val); }
        return pq.peek();
    }
}
```

**复杂度**：初始化/每次 `add` 皆 O(log k)；空间 O(k)

**易错点**：`k` 可能 > 初始数组长度（先填够 k 再比较）；不要把堆写成**最大堆**。

### LC378. Kth Smallest in a Sorted Matrix（有序矩阵第 K 小）

**推荐思路：二分答案 + 计数**

矩阵每行、每列递增。设值域 `[lo, hi] = [matrix[0][0], matrix[n-1][n-1]]`；二分 `mid`，用“**从左下角/右上角起数 ≤mid 的元素个数**”来决定收缩区间。

**Java 模板（值域二分）**

```java
public int kthSmallest(int[][] a, int k) {
    int n = a.length;
    int lo = a[0][0], hi = a[n-1][n-1];
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (countLE(a, mid) >= k) hi = mid;
        else lo = mid + 1;
    }
    return lo;
}
private int countLE(int[][] a, int x) {
    int n = a.length, i = n - 1, j = 0, cnt = 0; // 从左下角走“Z”字
    while (i >= 0 && j < n) {
        if (a[i][j] <= x) { cnt += i + 1; j++; }  // 这一列上方都 ≤ x
        else { i--; }
    }
    return cnt;
}
```

**复杂度**：计数 O(n)，二分约 `log(hi-lo)` 次 → 总 O(n log V)；空间 O(1)

**易错点**：`mid` 取法避免溢出；计数函数从**左下角或右上角**出发更易写对。

**替代**：

小顶堆合并法 O(k log n)（把每行首元素入堆，弹出 k-1 次）。

```java
public int kthSmallest(int[][] matrix, int k) {
    int n = matrix.length;
    if (n == 0) return -1;

    // 最多只需要 n 个堆节点（每行一个游标）
    Node[] heap = new Node[Math.min(n, k)];
    int size = 0;

    // 初始化：每行第 1 个元素入堆
    for (int r = 0; r < n && r < k; r++) {
        heap[size] = new Node(matrix[r][0], r, 0);
        siftUp(heap, size++);
    }

    // 弹出 k-1 次：每次把该行的下一个元素补进堆
    while (--k > 0) {
        Node top = heap[0];
        int r = top.r, c = top.c;

        if (c + 1 < n) {                // 该行还有下一个
            heap[0] = new Node(matrix[r][c + 1], r, c + 1);
            siftDown(heap, 0, size);
        } else {                         // 该行耗尽，缩小堆
            heap[0] = heap[--size];
            heap[size] = null;
            siftDown(heap, 0, size);
        }
    }
    return heap[0].val;
}

// ===== 手写最小堆 =====
static class Node {
    int val, r, c;
    Node(int v, int r, int c) { this.val = v; this.r = r; this.c = c; }
}

private void siftUp(Node[] a, int i) {
    while (i > 0) {
        int p = (i - 1) >> 1;
        if (a[i].val >= a[p].val) break;
        swap(a, i, p);
        i = p;
    }
}
private void siftDown(Node[] a, int i, int n) {
    while (true) {
        int l = (i << 1) + 1;
        if (l >= n) break;
        int r = l + 1;
        int s = (r < n && a[r].val < a[l].val) ? r : l;
        if (a[i].val <= a[s].val) break;
        swap(a, i, s);
        i = s;
    }
}
private void swap(Node[] a, int i, int j) {
    Node t = a[i]; a[i] = a[j]; a[j] = t;
}
```

### LC378 高质量复盘

**关键不变量**：`countLE(mid) >= k` → 答案在左半区含 `mid`；计数的“Z 字走法”

**复杂度**：O(n log V)；当值域很大但 `n` 小时与堆法对比

**边界**：重复元素、单行/单列、极值（k=1 或 k=n²）

**踩坑**：计数方向写反、二分死循环、溢出

**为什么选“值域二分 + 计数”**，对比堆法的适用性与复杂度。

- 小顶堆合并
  - 时间：**O(k log n)**（堆大小 ≤ n，每次弹出/插入 log n）
  - 空间：**O(n)**
  - 适合：**k 较小**时非常快（经验阈值：当 `k ≲ 30n` 左右通常优于二分）
- 值域二分 + 单调计数
  - 时间：**O(n · log(range))**；`log(range)`≈ 31 次左右（int 范围）
  - 空间：**O(1)**
  - 适合：**k 很大**或想要更稳定的性能/更低内存时

> 小结：`k` 小选堆，`k` 大选二分；二者都对重复元素、负数友好，二分更省内存更稳定，堆在 Top-K 场景下常数更小。

### LC373. Find K Pairs with Smallest Sums（两数组和最小的 K 对）

**思路**：把 `(i,0)`（固定 A\[i]，配 B\[0]）入**小顶堆**（按 `A[i]+B[j]` 排序），每次弹 `(i,j)` 后把 `(i, j+1)` 入堆。

```java
public List<List<Integer>> kSmallestPairs(int[] A, int[] B, int k) {
    List<List<Integer>> res = new ArrayList<>();
    if (A.length==0 || B.length==0 || k<=0) return res;
    PriorityQueue<int[]> pq = new PriorityQueue<>((x,y)->(A[x[0]]+B[x[1]])-(A[y[0]]+B[y[1]]));
    int m = Math.min(A.length, k);
    for (int i = 0; i < m; i++) pq.offer(new int[]{i,0});
    while (k-- > 0 && !pq.isEmpty()) {
        int[] t = pq.poll();
        res.add(List.of(A[t[0]], B[t[1]]));
        if (t[1] + 1 < B.length) pq.offer(new int[]{t[0], t[1]+1});
    }
    return res;
}
```

**复杂度**：O(k log min(n, k))；空间 O(min(n, k))

### LC295. Find Median from Data Stream（数据流中位数）

**思路**：**双堆**——左边**最大堆**存较小的一半，右边**最小堆**存较大的一半；保证两堆大小差 ≤1，左堆允许多一个。

```java
class MedianFinder {
    private PriorityQueue<Integer> left = new PriorityQueue<>(Comparator.reverseOrder());
    private PriorityQueue<Integer> right = new PriorityQueue<>();
    public void addNum(int num) {
        if (left.isEmpty() || num <= left.peek()) left.offer(num);
        else right.offer(num);
        if (left.size() < right.size()) left.offer(right.poll());
        else if (left.size() - right.size() > 1) right.offer(left.poll());
    }
    public double findMedian() {
        if (left.size() == right.size()) return (left.peek() + right.peek()) / 2.0;
        return left.peek();
    }
}
```

**复杂度**：单次插入 O(log n)，查询 O(1)

---
