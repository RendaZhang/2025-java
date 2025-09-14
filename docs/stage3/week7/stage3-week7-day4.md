<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [day 4 - Java 并发：线程池、锁与原子性（吞吐与稳定性打底）](#day-4---java-%E5%B9%B6%E5%8F%91%E7%BA%BF%E7%A8%8B%E6%B1%A0%E9%94%81%E4%B8%8E%E5%8E%9F%E5%AD%90%E6%80%A7%E5%90%9E%E5%90%90%E4%B8%8E%E7%A8%B3%E5%AE%9A%E6%80%A7%E6%89%93%E5%BA%95)
  - [今日目标](#%E4%BB%8A%E6%97%A5%E7%9B%AE%E6%A0%87)
  - [Step 1 - 算法（堆 / Top-K）](#step-1---%E7%AE%97%E6%B3%95%E5%A0%86--top-k)
    - [LC347. Top K Frequent Elements（哈希计数 + 最小堆）](#lc347-top-k-frequent-elements%E5%93%88%E5%B8%8C%E8%AE%A1%E6%95%B0--%E6%9C%80%E5%B0%8F%E5%A0%86)
    - [LC347 高质量复盘](#lc347-%E9%AB%98%E8%B4%A8%E9%87%8F%E5%A4%8D%E7%9B%98)
    - [LC215. Kth Largest Element in an Array（快速选择 or 堆）](#lc215-kth-largest-element-in-an-array%E5%BF%AB%E9%80%9F%E9%80%89%E6%8B%A9-or-%E5%A0%86)
    - [LC23. Merge k Sorted Lists（小顶堆合并）](#lc23-merge-k-sorted-lists%E5%B0%8F%E9%A1%B6%E5%A0%86%E5%90%88%E5%B9%B6)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# day 4 - Java 并发：线程池、锁与原子性（吞吐与稳定性打底）

## 今日目标

1. **算法**：掌握「堆 / 优先队列 / Top-K」这一常见高频套路（2 题必做 + 1 题选做），能口述复杂度与边界。
2. **并发核心**：系统梳理 Java 并发的“最小必会集”并能结合业务给出取舍：
   - 线程模型与可见性：`volatile`、happens-before；
   - 同步与原子性：`synchronized` vs `ReentrantLock`、`Atomic*`/CAS；
   - 线程池：`ThreadPoolExecutor` 7 参、队列选择、拒绝策略、常见坑（死锁/阻塞/饥饿）；
   - 任务编排：`CompletableFuture` 基本组合、超时与取消；
   - 诊断：死锁定位、线程池饱和信号与指标。
3. **表达**：形成 1 分钟英文口述 —— *“How I tune a ThreadPoolExecutor for bursty workloads without melting downstreams.”*

---

## Step 1 - 算法（堆 / Top-K）

### LC347. Top K Frequent Elements（哈希计数 + 最小堆）

**思路**

- 用 `Map<num -> freq>` 统计频次；
- 维护一个大小为 `k` 的**最小堆**（按频次升序），堆满后遇到更高频的元素就弹出堆顶再加入；
- 最后堆中即为出现频次 Top-K。

**Java 模板**

```java
public int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> cnt = new HashMap<>();
    for (int x : nums) cnt.put(x, cnt.getOrDefault(x, 0) + 1);

    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]); // [value, freq]
    for (Map.Entry<Integer, Integer> e : cnt.entrySet()) {
        if (pq.size() < k) {
            pq.offer(new int[]{e.getKey(), e.getValue()});
        } else if (e.getValue() > pq.peek()[1]) {
            pq.poll();
            pq.offer(new int[]{e.getKey(), e.getValue()});
        }
    }
    int[] ans = new int[k];
    for (int i = k - 1; i >= 0; i--) ans[i] = pq.poll()[0];
    return ans;
}
```

**复杂度**：建表 O(n)，堆操作 O(m log k)（m 为不同元素数，m ≤ n），总 O(n log k)，空间 O(m)。

**易错点**：

- 堆比较器要按**频次**而非数值；
- `k == nums.length` 或 `k == 1` 边界；
- 若要稳定输出顺序，需二次排序（本题不要求）。
  **自测**：`[1,1,1,2,2,3], k=2 -> [1,2]`；`[4,1,-1,2,-1,2,3], k=2 -> [-1,2]`。

> 为什么选择**最小堆**而不是把所有元素放**最大堆**；当 `k` 远小于 `m` 时的优势；与“桶排序”的对比。

桶排序：

```java
public int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freqMap = new HashMap<>();
    for (int num : nums) {
        freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
    }

    // 初始化桶数组，下标 = 出现频率，值 = 所有具有该频率的元素列表
    List<Integer>[] buckets = new List[nums.length + 1];
    for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
        int freq = entry.getValue();
        if (buckets[freq] == null) buckets[freq] = new ArrayList<>();
        buckets[freq].add(entry.getKey());
    }

    // 倒序遍历桶，直到找到前 k 个高频元素
    List<Integer> resultList = new ArrayList<>();
    for (int i = buckets.length - 1; i >= 0 && resultList.size() < k; i--) {
        if (buckets[i] != null) {
            resultList.addAll(buckets[i]);
        }
    }

    // 转成数组
    int[] result = new int[k];
    for (int i = 0; i < k; i++) {
        result[i] = resultList.get(i);
    }
    return result;
}
```

### LC347 高质量复盘

- **Pattern**：哈希计数 + Size-K 最小堆
- **Intuition**：只需维护前 K 大频次，保留局部最优，丢弃其余
- **Steps**：计数 → 迭代 `map` 维护堆 → 导出答案
- **Complexity**：O(n log k)/O(m)
- **Edge Cases**：`k=1`、所有元素等频、存在负数/大数、`k == m`
- **Mistakes & Fix**：比较器写错导致堆顺序反了；误用最大堆导致每次需要 O(log m) 维护所有元素
- **Clean Code 要点**：`int[]{val,freq}` 或自定义类，注意比较器与堆大小控制

### LC215. Kth Largest Element in an Array（快速选择 or 堆）

**思路 1：快速选择（推荐，均摊 O(n)）**

- 目标是找第 `k` 大，等价于找索引 `n - k`（升序位置）；
- 随机选主元，分区后只在一侧递归/迭代。

**Java 模板（迭代 + 随机 pivot）**

```java
private static final java.util.Random RAND = new java.util.Random();

public int findKthLargest(int[] nums, int k) {
    int target = nums.length - k; // 第 k 大 → 升序下标 n-k
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int pivotIdx = lo + RAND.nextInt(hi - lo + 1);
        int pivot = nums[pivotIdx];
        // 三向切分：<pivot | =pivot | >pivot
        int lt = lo, i = lo, gt = hi;
        while (i <= gt) {
            if (nums[i] < pivot) swap(nums, lt++, i++);
            else if (nums[i] > pivot) swap(nums, i, gt--);
            else i++;
        }
        // 现在 [lo..lt-1] < pivot, [lt..gt] == pivot, [gt+1..hi] > pivot
        if (target < lt) hi = lt - 1;
        else if (target > gt) lo = gt + 1;
        else return nums[target]; // 命中 equals 区间
    }
    throw new AssertionError("Unreachable");
}

private static void swap(int[] a, int i, int j) {
    int t = a[i]; a[i] = a[j]; a[j] = t;
}
```

**思路 2：大小为 k 的最小堆（O(n log k)）**

```java
public int findKthLargestHeap(int[] nums, int k) {
    PriorityQueue<Integer> pq = new PriorityQueue<>();
    for (int x : nums) {
        pq.offer(x);
        if (pq.size() > k) pq.poll();
    }
    return pq.peek();
}
```

**复杂度**：快选均摊 O(n)，最坏 O(n²)（随机化可规避）；堆法 O(n log k)。

**易错点**：

- `k` 转为索引 `n-k`；
- 分区条件要与“升序位置”对应；
- 不要忘了随机化 pivot。

**自测**：`[3,2,1,5,6,4], k=2 -> 5`；`[3,2,3,1,2,4,5,5,6], k=4 -> 4`。

### LC23. Merge k Sorted Lists（小顶堆合并）

**思路**

- 把每条链表的头节点入**小顶堆**（按 `val`）；
- 每次弹出最小节点接到结果链，若该节点有 `next` 则把 `next` 入堆。

**Java 模板**

```java
public ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.val));
    for (ListNode h : lists) if (h != null) pq.offer(h);
    ListNode dummy = new ListNode(0), tail = dummy;
    while (!pq.isEmpty()) {
        ListNode n = pq.poll();
        tail = tail.next = n;
        if (n.next != null) pq.offer(n.next);
    }
    return dummy.next;
}
```

**复杂度**：设总节点数 N、链表数 k，时间 O(N log k)，空间 O(k)。
**易错点**：`null` 头节点要跳过；避免创建新节点导致丢失后续指针。
**自测**：`[[1,4,5],[1,3,4],[2,6]] -> [1,1,2,3,4,4,5,6]`。

---
