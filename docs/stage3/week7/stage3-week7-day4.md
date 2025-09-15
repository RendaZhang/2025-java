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
  - [Step 2 - Java 并发核心梳理](#step-2---java-%E5%B9%B6%E5%8F%91%E6%A0%B8%E5%BF%83%E6%A2%B3%E7%90%86)
    - [内存模型 & 可见性：happens-before / `volatile` 的边界](#%E5%86%85%E5%AD%98%E6%A8%A1%E5%9E%8B--%E5%8F%AF%E8%A7%81%E6%80%A7happens-before--volatile-%E7%9A%84%E8%BE%B9%E7%95%8C)
    - [`synchronized` vs `ReentrantLock`：可中断 / 定时 / 公平 / 条件队列](#synchronized-vs-reentrantlock%E5%8F%AF%E4%B8%AD%E6%96%AD--%E5%AE%9A%E6%97%B6--%E5%85%AC%E5%B9%B3--%E6%9D%A1%E4%BB%B6%E9%98%9F%E5%88%97)
    - [`ThreadPoolExecutor` 七参数、队列取舍与拒绝策略](#threadpoolexecutor-%E4%B8%83%E5%8F%82%E6%95%B0%E9%98%9F%E5%88%97%E5%8F%96%E8%88%8D%E4%B8%8E%E6%8B%92%E7%BB%9D%E7%AD%96%E7%95%A5)
    - [`CompletableFuture` 任务编排：并行、超时、取消与自定义 Executor](#completablefuture-%E4%BB%BB%E5%8A%A1%E7%BC%96%E6%8E%92%E5%B9%B6%E8%A1%8C%E8%B6%85%E6%97%B6%E5%8F%96%E6%B6%88%E4%B8%8E%E8%87%AA%E5%AE%9A%E4%B9%89-executor)
    - [并发诊断与排障：死锁、线程池饱和、阻塞点定位，5 分钟 SOP](#%E5%B9%B6%E5%8F%91%E8%AF%8A%E6%96%AD%E4%B8%8E%E6%8E%92%E9%9A%9C%E6%AD%BB%E9%94%81%E7%BA%BF%E7%A8%8B%E6%B1%A0%E9%A5%B1%E5%92%8C%E9%98%BB%E5%A1%9E%E7%82%B9%E5%AE%9A%E4%BD%8D5-%E5%88%86%E9%92%9F-sop)
  - [Step 3 - 并发场景题](#step-3---%E5%B9%B6%E5%8F%91%E5%9C%BA%E6%99%AF%E9%A2%98)
    - [突发流量 + 下游限速，线程池怎么“吸收不作死”？](#%E7%AA%81%E5%8F%91%E6%B5%81%E9%87%8F--%E4%B8%8B%E6%B8%B8%E9%99%90%E9%80%9F%E7%BA%BF%E7%A8%8B%E6%B1%A0%E6%80%8E%E4%B9%88%E5%90%B8%E6%94%B6%E4%B8%8D%E4%BD%9C%E6%AD%BB)
    - [`CompletableFuture` 并行编排要做到：fail-fast + 可取消 + 明确降级](#completablefuture-%E5%B9%B6%E8%A1%8C%E7%BC%96%E6%8E%92%E8%A6%81%E5%81%9A%E5%88%B0fail-fast--%E5%8F%AF%E5%8F%96%E6%B6%88--%E6%98%8E%E7%A1%AE%E9%99%8D%E7%BA%A7)
    - [锁竞争 / 死锁如何 5 分钟内定位并修复？](#%E9%94%81%E7%AB%9E%E4%BA%89--%E6%AD%BB%E9%94%81%E5%A6%82%E4%BD%95-5-%E5%88%86%E9%92%9F%E5%86%85%E5%AE%9A%E4%BD%8D%E5%B9%B6%E4%BF%AE%E5%A4%8D)
    - [线程池饱和 + 重试风暴，如何协同治理？](#%E7%BA%BF%E7%A8%8B%E6%B1%A0%E9%A5%B1%E5%92%8C--%E9%87%8D%E8%AF%95%E9%A3%8E%E6%9A%B4%E5%A6%82%E4%BD%95%E5%8D%8F%E5%90%8C%E6%B2%BB%E7%90%86)
    - [线程池监控与告警：看哪些指标？阈值怎么定？](#%E7%BA%BF%E7%A8%8B%E6%B1%A0%E7%9B%91%E6%8E%A7%E4%B8%8E%E5%91%8A%E8%AD%A6%E7%9C%8B%E5%93%AA%E4%BA%9B%E6%8C%87%E6%A0%87%E9%98%88%E5%80%BC%E6%80%8E%E4%B9%88%E5%AE%9A)
    - [把并发策略整合落地：一段可直接用于面试的完整口语回答](#%E6%8A%8A%E5%B9%B6%E5%8F%91%E7%AD%96%E7%95%A5%E6%95%B4%E5%90%88%E8%90%BD%E5%9C%B0%E4%B8%80%E6%AE%B5%E5%8F%AF%E7%9B%B4%E6%8E%A5%E7%94%A8%E4%BA%8E%E9%9D%A2%E8%AF%95%E7%9A%84%E5%AE%8C%E6%95%B4%E5%8F%A3%E8%AF%AD%E5%9B%9E%E7%AD%94)
  - [Step 4：1 分钟英文口语](#step-41-%E5%88%86%E9%92%9F%E8%8B%B1%E6%96%87%E5%8F%A3%E8%AF%AD)
    - [1-min Answer — Tuning ThreadPoolExecutor for bursts while protecting downstreams](#1-min-answer--tuning-threadpoolexecutor-for-bursts-while-protecting-downstreams)
    - [**3 sound bites to emphasize**](#3-sound-bites-to-emphasize)

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
- Three-Way Partitioning: `<pivot | =pivot | >pivot`

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

手写最小堆（维护大小为 k 的堆）。

用长度为 k 的最小堆保存目前最大的 k 个数；新数大于堆顶才替换并下沉。
最终堆顶就是第 k 大。

```java
public int findKthLargest(int[] nums, int k) {
    int[] heap = new int[k];
    // 先放入前 k 个元素并建堆（最小堆）
    System.arraycopy(nums, 0, heap, 0, k);
    buildMinHeap(heap);

    // 扫描剩余元素：只有比堆顶大的才有资格进入 top-k
    for (int i = k; i < nums.length; i++) {
        if (nums[i] > heap[0]) {
            heap[0] = nums[i];
            siftDown(heap, 0, k);
        }
    }
    return heap[0]; // 第 k 大
}

// 自底向上建最小堆
private void buildMinHeap(int[] a) {
    for (int i = (a.length >>> 1) - 1; i >= 0; i--) {
        siftDown(a, i, a.length);
    }
}

// 下沉（最小堆）
private void siftDown(int[] a, int i, int n) {
    while (true) {
        int l = (i << 1) + 1;
        if (l >= n) break;
        int r = l + 1;
        int smallest = (r < n && a[r] < a[l]) ? r : l;
        if (a[i] <= a[smallest]) break;
        swap(a, i, smallest);
        i = smallest;
    }
}

private void swap(int[] a, int i, int j) {
    int t = a[i]; a[i] = a[j]; a[j] = t;
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

## Step 2 - Java 并发核心梳理

### 内存模型 & 可见性：happens-before / `volatile` 的边界

> **可见性 vs 原子性**：`volatile` 保障**可见/有序**不保障**复合原子**；计数/聚合用 `LongAdder/Atomic*` 或串行队列；**安全发布**用“不可变对象 + volatile 引用”；DCL 单例需 `volatile`；跨线程顺序靠 `start/join`、`synchronized unlock/lock`、`volatile write/read` 的 happens-before。

**面试官：**

“你们在（深圳市凡新科技 / 麦克尔斯深圳）有个**库存曝光服务**：多个线程持续累加访问量并在 1s 定时刷到 Redis。偶发地，页面 PV/库存快照会**落后**或**计数丢失**。请你解释 Java 内存模型里的 **happens-before** 关系、`volatile` 能/不能解决什么，并给出代码级修正。”

**你：**

“我先分两件事：**可见性/有序性** 和 **原子性**。

- `volatile` 只保证**写→读**的可见性和一定的**指令有序**（建立 happens-before：对同一变量 `volatile write` 先于后续线程的 `volatile read`），**不保证复合操作的原子性**。
- 对‘计数丢失’这种++操作，我会用 **原子类**（`AtomicLong`/热点高时用 `LongAdder`）或把更新放进**单线程/串行化**的队列；
- 对‘配置刷新/开关不生效’这种 **发布-订阅/开关切换**，我会用 `volatile` 或 **不可变对象 + volatile 引用**做**安全发布**。”

**反例 & 修正**

```java
// 反例：可见性不可靠 + 原子性缺失
class CounterBad {
  private long pv = 0;                 // 非 volatile，且 ++ 非原子
  void inc() { pv++; }                 // 读-改-写竞争
  long get() { return pv; }            // 可能读到旧值（CPU 缓存未刷回）
}

// 修正 1：高并发计数 —— LongAdder 更抗热点
class CounterGood {
  private final LongAdder pv = new LongAdder();
  void inc() { pv.increment(); }       // 分段计数，聚合时冲突少
  long get() { return pv.sum(); }
}

// 修正 2：配置热更新 —— 不可变对象 + volatile 引用
class Config { final int ttl; final boolean enable; Config(int t, boolean e){ttl=t;enable=e;} }
class ConfHolder {
  private volatile Config cfg = new Config(60, true); // 安全发布
  Config get(){ return cfg; }
  void reload(){ cfg = loadFromDb(); }                // 对所有线程立即可见
}
```

**happens-before 你要说得出的 4 条常用规则**

1. 程序次序规则：同一线程内的代码顺序。
2. **监视器**：对同一锁，`unlock` 先于后续线程的 `lock`（`synchronized`）。
3. **volatile**：对同一变量的 `write` 先于后续线程的 `read`。
4. **线程启动/终止**：`Thread.start()` 先于子线程内操作；子线程内操作先于 `Thread.join()` 返回。

**易错场景 & 取舍**

- **双重检查单例（DCL）**必须把实例引用设为 `volatile`，否则可能看到**半初始化对象**。
- **组合操作**（如 `check-then-act`、`put-if-absent`）用 `ConcurrentHashMap.computeIfAbsent` 或锁/CAS 保护；仅靠 `volatile` 不够。
- **计数/埋点**：热点极高时 `AtomicLong` 会退化自旋，**LongAdder**冲突更小；但**要快照**（求准确值）时用 `sum()`，它与上一刻的 `inc()` 有极小窗口差。
- **有序性**：`volatile` 可阻止相关指令重排，但**不能**代替锁来“临界区互斥”。

**项目实话实说**

“凡新那边促销统计我们一开始用 `AtomicLong`，峰值下自旋热点明显；换成 `LongAdder` 后 CPU 降了不少。Michaels 的开关配置用的是**不可变配置 + volatile 引用**，热更新后前端请求马上生效，不需要重启。”

### `synchronized` vs `ReentrantLock`：可中断 / 定时 / 公平 / 条件队列

> **锁的选择**：短小互斥→`synchronized`；需要**可中断/定时/多条件/可观测**→`ReentrantLock`。公平锁减吞吐；`unlock` 一定放 `finally`；条件队列要**循环检查**；读多写少可上 `ReentrantReadWriteLock`（只允许**降级**）；热点用**锁分段**，长等待用 **tryLock(timeout)+重试/降级**。

**面试官：**

“在（深圳市凡新科技 / 麦克尔斯深圳）的大促高峰，你们有个**库存预留**的热点段：偶发下游抖动时，线程在等待锁期间**堆积**，无法快速取消，导致**线程池被占死**。你会选择 `synchronized` 还是 `ReentrantLock`？为什么？”

**你：**

“我会把选择标准说清楚：

- **简单互斥、临界区短、无需可中断/定时/多条件队列** → `synchronized` 就够，JIT 对偏向/轻量级锁已经很优化了；
- **需要更细的控制**（例如**等待可中断**、**超时放弃**、**多条件变量**、或**可选公平性**）→ 用 `ReentrantLock`（基于 AQS）。库存预留这种‘热点且可能长等待’就该用 `ReentrantLock`，这样**等待线程可取消**，避免把线程池卡死。”

**关键差异**

- **可中断获取**：`lockInterruptibly()` 只有 `ReentrantLock` 支持 —— 等锁时能响应 `Thread.interrupt()`。
- **定时获取**：`tryLock(timeout, unit)` 允许**超时退避**，可与**重试预算**协同；`synchronized` 没法。
- **条件队列**：`newCondition()` 可建多个条件（如 `notEmpty/notFull`），`wait/notify` 只能一个条件队列且易误用。
- **公平性**：`new ReentrantLock(true)` 可公平，但吞吐下降；大多数场景用**非公平**提升吞吐。
- **可观测/灵活性**：`isLocked/hasQueuedThreads/getQueueLength` 等有助于指标化与排障。
- **语义**：两者都**可重入**；`synchronized` 通过监视器，`ReentrantLock` 通过 AQS 队列。

**等待可中断 + 超时退避（库存热点）**

```java
class StockGuard {
  private final ReentrantLock lock = new ReentrantLock(); // 非公平更高吞吐
  boolean runWithLock(Duration d, Runnable task) throws InterruptedException {
    if (lock.tryLock(d.toMillis(), TimeUnit.MILLISECONDS)) { // 定时获取
      try { task.run(); return true; }
      finally { lock.unlock(); }
    }
    return false; // 超时放弃，交给上层重试/降级
  }
}
// 调用处：失败则触发指数退避 + 幂等重试，避免长等待压死线程池
```

**多条件队列（有界缓存/异步出库）**

```java
class BoundedBuffer<T> {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition notEmpty = lock.newCondition();
  private final Condition notFull  = lock.newCondition();
  private final Deque<T> q = new ArrayDeque<>();
  private final int cap;

  void put(T x) throws InterruptedException {
    lock.lockInterruptibly();
    try {
      while (q.size() == cap) notFull.await(); // 可中断等待
      q.addLast(x);
      notEmpty.signal(); // 唤醒一个取者
    } finally { lock.unlock(); }
  }
  T take() throws InterruptedException {
    lock.lockInterruptibly();
    try {
      while (q.isEmpty()) notEmpty.await();
      T v = q.removeFirst();
      notFull.signal();
      return v;
    } finally { lock.unlock(); }
  }
}
```

**读多写少补充 - 读写锁与降级**

- `ReentrantReadWriteLock`：读多写少可提升吞吐；**可降级**（持有写锁时获取读锁再释放写锁），**不可升级**（先读后写会死锁/饥饿）。
- 极端读场景可考虑 `StampedLock`（乐观读），但 API 更复杂，注意**中断与可重入限制**。

**工程取舍与坑**

- **一定 `unlock()` 放在 `finally`**；`Condition.await()` 需在**持锁**前提下调用，醒来要**循环检查条件（防虚假唤醒）**。
- 公平锁**减少饥饿**但吞吐更低（队列严格 FIFO，缓存局部性变差）。
- **锁粗化**（把多次短锁合并）能省切换，但要警惕临界区过大造成争用；
- **锁分段/分片**（例如用 `stripe = hash(key) % N` 选择不同锁）能摊薄热点；
- **避免双重检查单例未加 `volatile`** 导致半初始化可见；
- `synchronized` 适合**非常短小**且无中断需求的路径（JIT 可内联/消除），否则用 `ReentrantLock` 获得**超时/可中断/多条件**这些工程特性。
- 指标化：导出**活跃线程数、队列长度、等待时长分位、拒绝数**；看到长等待优先**缩小临界区/分段**，其次再调线程数。

**项目口径**

“凡新那边库存预留把热点 SKU 的临界区改成 `ReentrantLock.tryLock(100ms)`，拿不到就**快速失败 + 幂等重试**，线程池再也没被‘长等待’拖死。麦克尔斯那边有个有界队列用两个 `Condition` 做 `notEmpty/notFull`，比 `wait/notify` 可读且不容易误唤醒。”

### `ThreadPoolExecutor` 七参数、队列取舍与拒绝策略

> **线程池 = 背压阀**：**有界队列 + 合理 core/max + CallerRuns/Abort 推回上游**；按**依赖分池**（Bulkhead），外呼**强制超时**，命中拒绝→**降级+退避**；拒绝把 `LinkedBlockingQueue` 用成**无界**；SynchronousQueue 需配强限流；观测 `active/queue/rejected/p95` 做动态调度。

**面试官：**

“大促瞬时高峰涌进来，你们的**下游限速**，结果你这边线程池一路涨、队列越积越多，延迟飙升甚至 OOM。你会怎么**选型与调参**，既**吸收突发**又不把下游打穿？”

**你：**

“我把线程池当成**背压阀**来设计：**有界队列 + 合理的 core/max + 拒绝策略推回上游**，再配超时/重试预算/熔断。”

1 - 七参数怎么选

`ThreadPoolExecutor(core, max, keepAlive, unit, workQueue, threadFactory, handler)`

- **corePoolSize**
  - **CPU 密集**：≈ `CPU核数`（或 `核数 * 1.0`）。
  - **IO 阻塞**：按 `核数 * (1 + 阻塞比)` 估算；例如阻塞≈3 倍计算时长，可把 `max` 提高到 `~ 核数 * 4`。
- **maximumPoolSize**：允许短时**扩张吸收突发**，但要**有限度**，避免把下游压穿。
- **keepAliveTime**：突发型业务把**非核心线程** 30–120s 回收；也可 `allowCoreThreadTimeOut(true)` 让 core 也缩。
- **workQueue（关键）**：强烈建议**有界**，容量体现你的**等待预算**。
- **threadFactory**：起**可读名**（含依赖名/用途），便于诊断；可带 MDC/traceId。
- **handler（拒绝策略）**：用来**施加背压**或**快速失败**，比“无限排队”更健康。

2 - 队列选型（取舍）

- **`ArrayBlockingQueue(cap)`（推荐）**：**有界 FIFO**，简单可控，最符合“背压”。
- **`LinkedBlockingQueue`**：默认**无界**（反模式 / 坑，可能 OOM）；如用务必**指定上限**。
- **`SynchronousQueue`**：**零容量**直传；适合**低延迟 + 可弹性扩线程**，但**极易打穿下游**，除非有**强兜底**（限流/熔断）。
- **`PriorityBlockingQueue`**：有优先级但**可能饿死**低优先任务；仅在确需优先级时用。

3- 拒绝策略（语义与场景）

- **`CallerRunsPolicy`（首选）**：把任务在**调用线程**执行 → **自然限速**，把压力**传回上游**；适合 BFF/同步链路。
- **`AbortPolicy`**：抛 `RejectedExecutionException` → **快速失败**，让上层走**降级/重试**。
- **`DiscardPolicy/DiscardOldestPolicy`**：静默丢弃/丢最老任务，**不建议**用于关键业务（难排障）。

4 - 反模式 RISK 清单（常见坑）

- **无界队列 + 巨大 max**：看似稳，其实**无限排队 → 高尾延迟/OOM**。
- **一个池干所有事**：CPU 任务与 IO 任务**混用** → 互相拖垮（饥饿/死锁）。
- **阻塞任务塞进 `ForkJoinPool`/默认 `CompletableFuture` 池** → 线程**饥饿**。
- **任务内再 `submit()` 并同步等待**（嵌套提交）→ **线程耗尽**死锁。
- **无限等待的下游调用**（无超时）→ 线程长期占用。

> 修复：**按依赖分池（Bulkhead）**；阻塞任务用**自定义 Executor**；所有外呼**必须有超时**；避免同步等待嵌套。

5 - 一套“突发但要保护下游”的实用模板

```java
int cores = Runtime.getRuntime().availableProcessors();
int queueCap = 2000; // 等待预算：能接受在本层堆多少
ThreadPoolExecutor pool = new ThreadPoolExecutor(
    Math.max(cores, 8),          // core：基础吞吐
    Math.max(cores * 4, 32),     // max：吸收突发，但有限度
    60, TimeUnit.SECONDS,        // 非核心回收
    new ArrayBlockingQueue<>(queueCap), // 有界FIFO = 背压阀
    r -> { Thread t = new Thread(r, "outbound-stock-%d".formatted(r.hashCode())); t.setDaemon(true); return t; },
    new ThreadPoolExecutor.CallerRunsPolicy() // 调用方背压
);
pool.allowCoreThreadTimeOut(true); // 突发过后及时收缩
```

**配套策略**

- 任务里**强制超时**（`TimeLimiter`/`CompletableFuture.orTimeout`）；
- 命中拒绝策略时：返回**清晰错误**或**降级**，并按**重试预算**退避；
- 指标：`active/queue/rejected/p95`，看到**队列持续高位**优先**降入口 RPS/放大下游限额/扩容**，不是“盲目加线程”。

6 - 与限流/重试/熔断的协同

- **限流**：入口**令牌桶**控制进入线程池的速率；`429 + Retry-After` 指导上游退避。
- **重试**：只对**幂等**请求；遵守**预算**，避免把队列堆满。
- **熔断**：下游异常率/超时升高时**打开熔断**，线程池**不再接活**（或改走降级）。
- **Bulkhead（分仓壁）**：为每个关键下游**单独线程池**（或信号量池），互不牵连。

7 - 诊断与观测（上线就要有）

- 导出：`poolSize/activeCount/queueSize/completedTaskCount/rejectedCount`；
- 采样**任务耗时分位**，分依赖/分接口看；
- **线程命名**可读（带依赖名），异常栈里一眼定位；
- `rejectedCount` 异常抬头 = **背压生效**，不是“坏事”，配合**降级开关**即可。

**项目口径**

“凡新我们给每个外呼（支付、库存）各一组**有界池 + CallerRuns**，配上**重试预算**，高峰期把压力稳稳**卡在调用方**，尾延迟大幅收敛；

麦克尔斯把原来单池改成**按依赖分池**，并给 `CompletableFuture` 指定自定义 Executor，解决了**默认池被阻塞任务吃光**的问题。”

### `CompletableFuture` 任务编排：并行、超时、取消与自定义 Executor

> **CF 编排**：总是用**自定义有界线程池**；子任务 `orTimeout/completeOnTimeout + 降级`，总体 `allOf` 加 **deadline**，超时**取消 siblings**；竞速用 `anyOf`；避免在 commonPool 跑阻塞 IO；通过装饰器传递 **MDC/traceId**；别在任务里嵌套阻塞等待。

**面试官：**

“在（深圳市凡新科技 / 麦克尔斯深圳）的下单页，你需要**并行**查询：价格、库存、优惠、地址校验。要求**总体超时 1.2s**，任何子调用超时要**快速降级**，并且**不要把公共线程池卡死**。你怎么用 `CompletableFuture` 编排？”

**你：**

“我有四个原则：**自定义 Executor**、**fail-fast 超时**、**可取消**、**清晰降级**。默认 `commonPool` 是 `ForkJoinPool`，**不能塞阻塞 IO**，所以我总是传入**专用线程池**（按依赖分池）。整体用 `allOf` 聚合，子任务统一 `orTimeout` + `exceptionally` 降级；一旦总体超时或关键任务失败，**取消其余**。”

**代码骨架（并行 + 超时 + 降级 + 取消）**

```java
Executor ioPool = outboundPool("checkout-io"); // 你的自定义、有界线程池

CompletableFuture<Price> fPrice = CompletableFuture
    .supplyAsync(() -> priceApi.get(sku), ioPool)
    .orTimeout(400, TimeUnit.MILLISECONDS)                // 子调用超时
    .exceptionally(e -> Price.fallback());                // 明确降级

CompletableFuture<Stock> fStock = CompletableFuture
    .supplyAsync(() -> stockApi.get(sku), ioPool)
    .orTimeout(300, TimeUnit.MILLISECONDS)
    .exceptionally(e -> Stock.unknown());                 // 可返回未知/保守值

CompletableFuture<Coupon> fCoupon = CompletableFuture
    .supplyAsync(() -> couponApi.check(user), ioPool)
    .orTimeout(300, TimeUnit.MILLISECONDS)
    .exceptionally(e -> Coupon.empty());

CompletableFuture<AddressCheck> fAddr = CompletableFuture
    .supplyAsync(() -> addrApi.validate(addr), ioPool)
    .orTimeout(250, TimeUnit.MILLISECONDS)
    .exceptionally(e -> AddressCheck.skip());

CompletableFuture<Void> all = CompletableFuture.allOf(fPrice, fStock, fCoupon, fAddr);

// 总体超时 + 取消其余
try {
    all.orTimeout(1200, TimeUnit.MILLISECONDS).join();
} catch (CompletionException e) {
    // overall timeout/fail-fast，取消还在跑的子任务（若 API 支持中断/超时，会尽快返回）
    fPrice.cancel(true); fStock.cancel(true); fCoupon.cancel(true); fAddr.cancel(true);
}
// 聚合结果（子任务已各自降级，getNow 不会抛异常）
Result r = new Result(
    fPrice.getNow(Price.fallback()),
    fStock.getNow(Stock.unknown()),
    fCoupon.getNow(Coupon.empty()),
    fAddr.getNow(AddressCheck.skip())
);
```

**常用模式**

- **Fail-fast 聚合**：关键依赖失败就**立即返回**（而不是等其它都完成）。做法：给关键依赖单独 `orTimeout/exceptionally`，并在 `handle/whenComplete` 里触发**取消 siblings**。
- **Fastest-wins（竞速）**：`anyOf(f1, f2, ...)` 选最快结果（如多机房并发读），其余任务在 `thenAccept` 里**best effort cancel**。
- **串并混排**：`thenCompose` 串行依赖（拿到 user 再查优惠）、`thenCombine` 汇合独立分支。
- **超时占位**：`completeOnTimeout(fallback, 300, ms)` —— 超时直接返回**默认值**，不抛异常。

**异常与上下文**

- **不要 `get()`/`join()` 早取**，统一在末端聚合；
- 用 `exceptionally/handle` 明确每个子任务的**降级逻辑**；
- **MDC/traceId 传递**：`CompletableFuture` 不会自动继承 MDC，包一层 `Supplier` 复制/恢复 MDC（或用装饰器 Executor）；
- **中断语义**：取消只是一种信号，确保下游客户端**尊重超时/中断**（HTTP 客户端配置 read/connect timeout），否则线程仍被占。

**反模式（常见坑）**

- 把阻塞 IO 跑在 `ForkJoin commonPool`；
- `LinkedBlockingQueue` 无界池接 CF 任务 → **无限排队**；
- 在任务内部**同步等待另一个 CF**（嵌套 `get()`）→ **线程互等**；
- 没有 `orTimeout/completeOnTimeout`，导致**整体任务无上限**；
- 降级不明确，异常被吞掉，排障困难。

**项目口径**

“凡新那边我们把 checkout 的四个外呼**并行**起来，子任务 `250–400ms` 超时，各自有 fallback；总体 **1.2s deadline** 到就**取消其余**并回前端‘受理中’。麦克尔斯那边把默认 `commonPool` 全部替换成**分池**，p95 立即下降，且**拒绝数**成了清晰的背压信号。”

### 并发诊断与排障：死锁、线程池饱和、阻塞点定位，5 分钟 SOP

> **并发排障 SOP**：指标判型（active/queue/rejected、CPU/GC、依赖 P95）→ 采样线程栈（`Thread.print` 连打 2–3 次）定位 **I/O/锁/CPU** → 当场止血（超时/降级/背压/熔断/收紧并发）→ 根因修复（有界池+超时、分池、加锁顺序、`tryLock`、降对象膨胀、重试预算）。出现 “deadlock” 即统一**锁顺序**或使用超时锁。

**面试官：**

“促销高峰里接口 P95 飙升、线程池不出活。你如何 **5 分钟内** 判断是**CPU 打满**、**下游阻塞**、还是**锁竞争/死锁**，并给出**止血**与**根因**？”

**你：**

“我有一套 **SOP**：**看指标 → 采样线程栈 → 对症止血 → 复盘修复**。”

① 先看指标（1 分钟：判型）

- **线程池四件套**：`activeCount / poolSize / queueSize / rejectedCount`
  - `queueSize` 持续攀升 + `rejectedCount` 抬头 → **池饱和/背压生效**；
  - `active≈max` 且出活慢 → 任务**被阻塞**（I/O、锁）。
- **CPU/GC**：CPU 100% + GC 次数/停顿上升 → **计算/分配压力**或**对象膨胀**（大队列）。
- **依赖红灯**：下游 P95、超时率、熔断状态；若依赖同时抬头，优先判断**下游阻塞**。

> 结论模板：
> - CPU 高 + 线程大多 RUNNABLE → **CPU 绑定/自旋/热点**；
> - CPU 不高 + 队列涨/超时多 → **外部 I/O 或锁等待**。

② 线程栈取证（2 分钟：定位）

**命令**（连打 2–3 次，间隔 5s）：

```
jcmd <pid> Thread.print -l > tdump1.txt
jcmd <pid> Thread.print -l > tdump2.txt
```

**怎么看**

- 大量线程 `TIMED_WAITING on java.net...` / `WAITING on java.util.concurrent.CompletableFuture$Signaller` → **I/O/下游慢**或**无超时**。
- `BLOCKED (on object monitor)` / `parking to wait for <...AQS>` → **锁竞争**；若 dump 顶部出现
  `Found one Java-level deadlock` → **死锁**。
- 线程名可读（建议命名如 `outbound-stock-*`），一眼看出**哪条依赖**卡住。
- **热点类目**：
  - `AbstractQueuedSynchronizer`：`synchronized/ReentrantLock/CountDownLatch` 等等待；
  - `ForkJoinPool.commonPool-worker-*`：阻塞任务误入 **commonPool**；
  - `SynchronousQueue`：直传队列 + 下游慢 ⇒ **堆线程/打穿**。

**补刀**

```
jcmd <pid> GC.class_histogram > histo.txt   # 看大对象/队列膨胀
jfr start duration=60s filename=profile.jfr # 1 分钟 JFR 看锁/IO热点
```

③ 当场止血（1 分钟：控损）

- **强制超时/限速**：临时把外呼客户端 read/connect timeout 降到 300–800ms；尊重 `Retry-After`；将**重试预算**降为 ≤5%。
- **打开降级/熔断**：对“非关键读”直接旧值/占位；关键写**受理中**。
- **收紧并发**：把问题依赖的线程池 **max/queue** 下调，命中 `CallerRuns/Abort` 让**上游减速**（背压）；或直接**临时摘除**问题下游。
- **杀环路**：发现死锁或嵌套等待，**关闭相关入口**（灰度下线）并重启单实例释放锁。

④ 根因与修复（+ 事后规范）

**常见根因 → 修复清单**

1. **无超时/大超时** → 所有外呼**必须**配置超时（请求 deadline 统一 ≤1.5–2s）；`CompletableFuture.orTimeout / TimeLimiter` 落地。
2. **池饱和（无界队列）** → 改为**有界** `ArrayBlockingQueue`；拒绝策略用 `CallerRuns/Abort`；建立**背压指标**。
3. **阻塞任务跑在 commonPool** → 给 CF 指定**自定义 Executor**（按依赖分池）。
4. **锁竞争/死锁** →
   - 固化**加锁顺序**；
   - 长等待改 `tryLock(timeout)+重试/降级`；
   - 缩小临界区/**锁分段**；
   - 用 `ReentrantReadWriteLock`/`LongAdder` 降低写冲突。
5. **对象膨胀/GC**（大队列/大集合） → **控队列上限**、分批处理、减少中间对象。
6. **重试风暴** → **指数退避 + 抖动 + 预算**；熔断打开期间不重试，仅半开探测。

代码小工具（两段，线上非常好用）

**A. 线程池探针（日志 + 指标）**

```java
class InstrumentedExecutor extends ThreadPoolExecutor {
  InstrumentedExecutor(int core, int max, int q) {
    super(core, max, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(q),
      r -> new Thread(r, "outbound-stock-" + r.hashCode()),
      new CallerRunsPolicy());
    allowCoreThreadTimeOut(true);
  }
  @Override protected void beforeExecute(Thread t, Runnable r) {
    // 采样记录排队时长/开始时间（可放到 MDC）
  }
  @Override protected void afterExecute(Runnable r, Throwable t) {
    // 上报耗时/异常，若 t!=null 记录
  }
}
```

**B. 锁看门狗（持锁超时报警）**

```java
class TimedLock implements AutoCloseable {
  private final ReentrantLock lock; private final long start=System.nanoTime();
  TimedLock(ReentrantLock l, long warnMs) {
    this.lock = l;
    try { if(!l.tryLock(warnMs, TimeUnit.MILLISECONDS))
            throw new RuntimeException("lock timeout"); }
    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
  }
  public void close() {
    long costMs=(System.nanoTime()-start)/1_000_000;
    if (costMs > 200) log.warn("Lock held {} ms", costMs);
    lock.unlock();
  }
}
// 用法：try (var g=new TimedLock(lock,100)) { /*临界区*/ }
```

死锁“一句话排查”

- 看 `Thread.print` 顶部是否有 “**Found one Java-level deadlock**”；
- 找到两个（或多）线程互相 `BLOCKED`，标出**锁对象**与**获取顺序**；
- **修复**：统一获取顺序；或拆一把锁；或把其中一个改为 `tryLock` + 超时退避。

---

## Step 3 - 并发场景题

### 突发流量 + 下游限速，线程池怎么“吸收不作死”？

**面试官：**

“促销 5 分钟峰值打过来，你们要并发调用库存与优惠服务。现在的线程池**队列越积越多**、尾延迟飙升，偶尔还 OOM。请你说说你会怎么设计 `ThreadPoolExecutor`（参数、队列、拒绝策略），以及怎么和**超时/重试/熔断**配合，既吃下突发又不把下游打穿？最好结合你在（深圳市凡新科技 / 麦克尔斯深圳）的经历给一组**可落地**的参数。”

**你：**

“我把线程池当**背压阀**来用，不当仓库。落地分四步：

1. **按依赖分池（Bulkhead）**
   支付、库存、优惠**各一组**线程池，互不拖累；`CompletableFuture` 一律用**自定义池**，不占 `commonPool`。
2. **有界队列 + 适度弹性**
   我选 `ArrayBlockingQueue`，把队列容量当作**等待预算**而不是垃圾桶。凡新那次活动我用过一组参数：
   - `core = max(8, CPU核)`，`max ≈ 核数 * 4`（IO 阻塞型业务）；
   - `queueCap = 2000`（能接受在本层最多囤 2k 个请求，再多就宁可拒）；
   - `keepAlive = 60s`，`allowCoreThreadTimeOut(true)`（峰值后尽快回收）；
   - 队列**坚决不用无界 `LinkedBlockingQueue`**，也不会在这种场景用 `SynchronousQueue`（太容易把下游打穿）。
3. **拒绝策略 = 把压力推回去**
   用 `CallerRunsPolicy` 或 `AbortPolicy`：
   - **CallerRuns**：调用线程自己跑，**自然限速**（BFF 线程被占就降速了）；
   - **Abort**：直接抛异常，BFF 把它转成**清晰的降级/受理中**。
     命中拒绝时我会**记录指标**（`rejectedCount`）并触发**灰度降入口 RPS**。
4. **协同：超时 / 重试预算 / 熔断**
   - 外呼**硬超时**（300–800ms 一档），总体**deadline**（比如 1.2s）；
   - **只对幂等**请求重试，**指数退避 + 抖动**，**预算 ≤10%**；
   - 下游错误/超时率越线**打开熔断**，熔断期**不再重试**，仅半开探测；
   - 命中 `429` 尊重 `Retry-After`，把下一次退避对齐。

**实战口径**

- 在**凡新**，库存外呼池我们配了：`core=12, max=48, queue=2000, CallerRuns`，外呼 HTTP 客户端 `read/connect=500ms`，命中拒绝就**快速失败 + 幂等重试**（预算 10%）。峰值时**rejected**会上来，但尾延迟收敛，下游也没有被打穿。
- 在**麦克尔斯**，图片索引原本用无界队列，直接把 GC 顶爆。改成**有界 + Abort** 后，前端拿到“受理中”占位；等消费链路恢复我们**异步补齐**，整体体验反而更稳。”

**一段可复用代码（骨架）**

```java
int cores = Runtime.getRuntime().availableProcessors();
ThreadPoolExecutor pool = new ThreadPoolExecutor(
    Math.max(cores, 8),
    Math.max(cores * 4, 32),
    60, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(2000),
    r -> { Thread t = new Thread(r, "outbound-stock-" + r.hashCode()); t.setDaemon(true); return t; },
    new ThreadPoolExecutor.CallerRunsPolicy() // 或 AbortPolicy
);
pool.allowCoreThreadTimeOut(true);
```

**面试官可能追问 & 我会补充**

- **为什么不用无界队列？** → 它把延迟藏起来，最后以 OOM/长尾爆雷。
- **为什么不用 `SynchronousQueue`？** → 直传 + 下游慢会疯狂拉起线程或大量阻塞，风险更大。
- **如何判定“队列该多大”？** → 结合**SLA × 目标吞吐**估算最大在途数；超出就拒绝/降级，而不是堆。
- **监控看什么？** → `active/queue/rejected/p95`；看到队列高位稳定 5 分钟，就**降入口/扩下游**而不是加线程。

### `CompletableFuture` 并行编排要做到：fail-fast + 可取消 + 明确降级

**面试官：**

“下单页要并行查：价格、库存、优惠、地址校验。要求**1.2s 总超时**；任何**关键依赖失败要立刻返回（fail-fast）**；其余子任务要**可取消**；每个子任务都要有**明确降级**。你会怎么写？（结合你在凡新/麦克尔斯的做法说说）”

**你：**

“我的套路是：**自定义有界线程池** + **每个子任务 orTimeout + 降级**，再加一段‘**首个失败即取消其他**’的钩子，整体再套一层**deadline**。”

```java
// 1) 专用有界线程池（别用 commonPool）
Executor ioPool = outboundPool("checkout-io"); // 有界 + CallerRuns/Abort

// 2) 子任务：各自 timeout + 明确降级（fallback）
var fPrice = CompletableFuture.supplyAsync(() -> priceApi.get(sku), ioPool)
    .orTimeout(400, MILLISECONDS)
    .exceptionally(e -> Price.fallback());

var fStock = CompletableFuture.supplyAsync(() -> stockApi.get(sku), ioPool)
    .orTimeout(300, MILLISECONDS)
    .exceptionally(e -> Stock.unknown());

var fCoupon = CompletableFuture.supplyAsync(() -> couponApi.check(user), ioPool)
    .orTimeout(300, MILLISECONDS)
    .exceptionally(e -> Coupon.empty());

var fAddr = CompletableFuture.supplyAsync(() -> addrApi.validate(addr), ioPool)
    .orTimeout(250, MILLISECONDS)
    .exceptionally(e -> AddressCheck.skip());

// 3) fail-fast：任一“关键依赖”异常 → 取消其余（best-effort）
List<CompletableFuture<?>> all = List.of(fPrice, fStock, fCoupon, fAddr);
all.forEach(f -> f.whenComplete((r, ex) -> {
    if (ex != null) all.forEach(o -> { if (o != f) o.cancel(true); });
}));
// 关键依赖你可以只挂在价格/库存上；非关键（比如优惠、地址）失败不触发取消

// 4) 总体 deadline（1.2s）
try {
    CompletableFuture.allOf(fPrice, fStock, fCoupon, fAddr)
        .orTimeout(1200, MILLISECONDS).join();
} catch (CompletionException e) {
    // 总体超时或关键失败：日志打点 + 统一降级响应（受理中/稍后刷新）
    fPrice.cancel(true); fStock.cancel(true); fCoupon.cancel(true); fAddr.cancel(true);
}

// 5) 聚合结果（getNow 避免再抛异常）
var result = new CheckoutView(
    fPrice.getNow(Price.fallback()),
    fStock.getNow(Stock.unknown()),
    fCoupon.getNow(Coupon.empty()),
    fAddr.getNow(AddressCheck.skip())
);
```

**工程要点**

- 在线上，**HTTP 客户端也要配置 connect/read 超时**，不然 CF 取消不了真正的阻塞。
- **关键依赖失败即刻取消**能把平均资源占用降下来，凡新促销时我们就是这么做的；麦克尔斯把图片/地址校验标成**非关键**，失败只降级，不触发 fail-fast。
- 观测：按接口维度暴露 `timeout_count / cancelled_count / fallback_ratio / pool_rejected`，一眼看出是**下游慢**还是**线程池饱和**。

**常见坑 & 一句话纠偏**

- 把阻塞 IO 跑在 `commonPool` → **改自定义有界池**；
- 没 `orTimeout/completeOnTimeout` → **加子任务超时**，避免整体被拖死；
- 在子任务里 `get()` 等另外一个 CF → **用组合算子 (`thenCombine/anyOf`)**，别嵌套阻塞；
- 只取消 CF 不设置客户端超时 → **取消信号传不到下游**；
- 降级不明确 → **每个分支都 `.exceptionally(e -> fallback)`**。

### 锁竞争 / 死锁如何 5 分钟内定位并修复？

**面试官：**

“高峰期你们的下单接口 P95 飙升，线程池 `active≈max` 但出活很慢。线程栈里很多 `BLOCKED`/`parking to wait for <...AQS>`。请你说说**如何快速确认是不是锁竞争/死锁**，以及**当场止血 + 代码层修复**？”

**你：**

“我会按 **SOP** 来：**看指标→打线程栈→当场止血→代码修复**。

**① 判型（1 分钟）**

- 线程池：`active≈max`、`queue↑`、`rejected↑`；CPU 不高（<60%），说明多半是**等待**而不是算力。
- 依赖侧 P95 正常 → 更像**锁竞争**；若依赖也慢，则优先处理下游。

**② 线程栈取证（2 分钟）**

```
jcmd <pid> Thread.print -l > tdump1.txt
jcmd <pid> Thread.print -l > tdump2.txt
```

- 看到大量 `BLOCKED (on object monitor)`（`synchronized`）或 `parking to wait for <...AbstractQueuedSynchronizer>`（`ReentrantLock`），并且 dump 顶部若出现
  `Found one Java-level deadlock` → **确定死锁**。
- 记录**锁对象地址**与**调用栈**，确认是否存在**交叉获取顺序**。

**③ 当场止血（1 分钟）**

- 暂时把热点路径换成 **`tryLock(timeout)` + 快速降级/重试**，避免长时间占用线程；
- **缩小临界区**（把远程调用/IO 移出锁内）；
- 对热点 key 做**分段锁**（`stripe = hash(key)%N`），立刻摊薄争用；
- 如果确认死锁且可灰度，**摘流/重启**单实例解除僵持，同时拉低入口 RPS。

**④ 代码修复（复盘落地）**

- **统一加锁顺序**（最根本）：所有线程都按同一顺序获取 `lockA → lockB`；
- **避免嵌套等待**：临界区只做数据结构操作；
- **可中断/超时**：用 `ReentrantLock.lockInterruptibly()` / `tryLock(t,unit)`；
- 读多写少改 **`ReentrantReadWriteLock`**；计数热点用 **`LongAdder`** 替代全局锁。

**反例（易死锁写法）**

```java
// T1 顺序：A -> B，T2 顺序：B -> A  => 经典死锁
synchronized (lockA) {
  // ...
  synchronized (lockB) { /* ... */ }
}
synchronized (lockB) {
  // ...
  synchronized (lockA) { /* ... */ }
}
```

**修正（统一顺序 / 超时退避）**

```java
// 统一获取顺序：按 hash 比较决定 A/B 的先后
Object first = System.identityHashCode(a) < System.identityHashCode(b) ? a : b;
Object second = first == a ? b : a;
synchronized (first) {
  synchronized (second) { /* 临界区仅做内存操作 */ }
}

// 或改 ReentrantLock + 超时，避免长等待卡死线程池
boolean ok = lock.tryLock(80, TimeUnit.MILLISECONDS);
if (!ok) return fallback(); // 快速降级/重试（具幂等）
try { /* 临界区 */ }
finally { lock.unlock(); }
```

**真实口径（结合经历）**

- **凡新**：活动页库存热 key 竞争，早期把**HTTP 调用放在锁内**导致队列暴涨；修复为**缩小临界区 + tryLock(100ms)**，拿不到就降级成“受理中”，线程池不再被拖死。
- **麦克尔斯**：图片处理链路出现互相持有两把锁的交叉顺序，`Thread.print` 直接报 deadlock；统一锁顺序后问题根除，并加了**锁看门狗**（持锁>200ms报警）。

**一句话总结**

> 判断：`BLOCKED/AQS` + CPU 不高 → 锁问题。止血：`tryLock(timeout)`/缩小临界区/分段锁/降级。修复：**统一锁顺序**、可中断/超时获取、读写分离与热点规避。”

### 线程池饱和 + 重试风暴，如何协同治理？

**面试官：**

“高峰期库存接口开始超时，你们的调用方按老规矩**固定间隔重试 3 次**，结果线程池**队列暴涨**、**拒绝数飙升**，还把下游**彻底打穿**。你在（深圳市凡新科技 / 麦克尔斯深圳）会怎么**同时**处理线程池饱和和重试风暴？”

**你：**

“我把它当一个**闭环控制问题**：线程池充当**背压阀**，重试遵守**预算**，熔断在异常时**切断正反馈**。落地四件事：**限流前置 → 有界池背压 → 重试预算 + 退避抖动 → 熔断/降级**。”

1 - 前置限流（入口滴灌）

- BFF/网关用**令牌桶**控进入下游线程池的速率；命中 `429` 明确返回 `Retry-After`。
- 预算估算：在途上限 ≈ `active + queueCap`，例如池 `max=48`、队列 `2000` → **最多 2048** 在途，网关把**每秒入池速率**限制在 `2048 / SLA秒` 左右。

2 - 线程池 = 背压阀（而不是仓库）

- **有界队列**（`ArrayBlockingQueue`），`CallerRuns` 或 `Abort`。
- 一旦 `queueSize` 高位稳定 ≥ 60s，就**主动降入口 RPS**或**扩容**，而不是“继续加线程”。
- 观测四指标：`active / queue / rejected / p95`。

3 - 重试要有“预算”（≤10%）+ 指数退避 + 抖动

- **只对幂等**请求重试；**固定间隔** → 全改 **指数退避 + 抖动**。
- **重试预算**：单位时间内 `retry_count ≤ 0.1 * success_count + 50`（保底 50）。用**原子计数**实现；**预算耗尽就快速失败**。
- 退避序列：`200ms, 500ms, 1.2s, 2.5s, 5s`，并加 full jitter。

**Java 伪码（带预算 + 抖动 + Retry-After）**

```java
boolean allowed = retryBudget.tryConsume(); // 原子扣减，返回是否还有预算
if (!allowed) throw new RetryBudgetExhausted();

Duration wait = nextBackoffWithJitter(attempt); // 指数退避+抖动
if (resp.is429() && resp.retryAfter() != null) {
  wait = max(wait, resp.retryAfter());          // 与 Retry-After 对齐
}
sleep(wait);
```

4 - 熔断 + 半开探测（切断风暴）

- 错误率/超时率越线（例如**错误率 >50% & 请求数>每秒 N**）→ **打开熔断**；
- 熔断打开期间**停止重试**；只在**半开**状态用少量探针请求；失败再回开路。

5 - 实战口径（我会这样说）

- **凡新**：我们把库存外呼池（`core=12,max=48,queue=2000,CallerRuns`）配了**重试预算 10%**；抖动后的退避 + 熔断后**不重试**，峰值时 `rejected` 上来但尾延迟稳定，下游没有被打穿。
- **麦克尔斯**：图片索引链路把“固定 3 次重试”改成**预算 + 抖动 + Retry-After**，并把失败任务挪到**慢车道队列**；风暴当场止住，回收后再**限速回放**。

6 - 一键止血（值班 SOP）

- 立刻把客户端**read/connect timeout**降至 `300–800ms`；
- 将线程池 `max` 和 `queueCap` **下调**，命中 `CallerRuns/Abort` 把压力推回；
- 开启**降级**（返回旧值/受理中）；
- 打开/收紧**熔断阈值**；
- 查询并临时禁止**固定间隔重试**的调用方（脚本批量下发配置）。

7 - 反模式（必须一口气指出）

- **无界队列 + 固定间隔重试** → 高尾延迟 + OOM + 打穿下游；
- **熔断打开仍在重试** → 负反馈；
- **忽略 `Retry-After`** → 和对方节拍打架；
- **写请求无幂等键还重试** → 双扣/乱序副作用。

**一句话总结**

> **有界池 + 背压（CallerRuns/Abort）** 吸收突发，**重试=预算化+退避抖动**，**熔断**在异常期切断正反馈，入口**令牌桶**稳节奏——四管齐下才能把风暴压成可控波动。

### 线程池监控与告警：看哪些指标？阈值怎么定？

**面试官：**

“你在（深圳市凡新科技 / 麦克尔斯深圳）如何给 `ThreadPoolExecutor` 做**可观测**？具体**哪些指标**、**告警阈值**、**看板布局**，以及**值班 SOP**怎么写？”

**你：**

“我用‘**SLO → 指标 → 告警 → 看板 → Runbook**’五件套。”

1 - 指标（Pool 维度 + 任务维度）

**Pool 维度（核心）**

- `pool_active`（当前活跃线程数）
- `pool_max` / `pool_core`
- `queue_size` / `queue_capacity`（求 `queue_fill_ratio = size/cap`）
- `rejected_total`（按原因分：`abort`/`callerRuns`/`discard`…）
- `completed_total`、`task_submitted_total`

**任务维度（直观反映体验）**

- `task_exec_seconds` 直方图（P50/P95/P99）
- `task_wait_seconds`（入队→开始执行的排队时间）
- `timeout_total`、`cancelled_total`（CF/HTTP 客户端统计）
- 依赖侧：外呼 **成功率**、**超时率**、**429/5xx** 比例（对齐重试/熔断）

> 线程命名要可读（如 `outbound-stock-*`），日志和 dump 一眼定位。

2 - 告警（Prometheus/语义）

**饱和预警（持续 2–5m）**

- `active/max > 0.8` **并且** `queue_fill_ratio > 0.7` 且上升
- 或 `rate(rejected_total[1m]) > 0` 连续 3 分钟
- 或 `task_exec_seconds{quantile="0.95"} > SLA×1.5` 持续 5 分钟

**风暴/退化**

- `rate(rejected_total[1m]) > 50`（硬拒绝暴涨）
- `rate(timeout_total[1m]) > X` 且 熔断**关闭** → 提醒**开启熔断**
- `rate(callerRuns_total[1m])` 持续上升 → **上游被背压**（通知降入口）

**示例规则（伪 PromQL）**

```yaml
- alert: PoolSaturation
  expr: (executor_active / executor_max > 0.8)
        and (executor_queue_size / executor_queue_cap > 0.7)
        and on(pool) (deriv(executor_queue_size[2m]) > 0)
  for: 3m
  labels: {severity: warning}
  annotations: {runbook: "rb-threadpool-saturation"}

- alert: PoolRejectSpike
  expr: rate(executor_rejected_total[1m]) > 20
  for: 2m
  labels: {severity: critical}
  annotations: {runbook: "rb-threadpool-reject"}
```

3 - 看板布局（1 屏能判型）

- **Top 行（总体）**：P95 延迟、成功率、重试率、熔断状态。
- **中间（Pool）**：`active/max`、`queue_size/cap`、`rejected`、`callerRuns` 叠图 + 斜率；
- **底部（任务）**：`task_wait_seconds` 与 `task_exec_seconds` 分布；依赖侧 5xx/429/超时趋势。
- 边上放**实例 TopN**（按 `queue_size` 和 `rejected` 排），便于点名。

4 - 采集与埋点（Micrometer 示例）

```java
MeterRegistry r = ...;
ThreadPoolExecutor p = /* 你的有界池 */;
Gauge.builder("executor_active", p, ThreadPoolExecutor::getActiveCount).register(r);
Gauge.builder("executor_queue_size", p, e -> e.getQueue().size()).register(r);
Gauge.builder("executor_queue_cap", p, e -> ((ArrayBlockingQueue<?>)e.getQueue()).remainingCapacity()
                                  + e.getQueue().size()).register(r);
Counter rejected = Counter.builder("executor_rejected_total")
    .tag("pool","outbound-stock").register(r);
// 在自定义 RejectedExecutionHandler 里 rejected.increment();
Timer execTimer = Timer.builder("task_exec_seconds").publishPercentiles(0.5,0.95,0.99).register(r);
// 在 InstrumentedExecutor.afterExecute 里记录耗时；waitTime 可在 beforeExecute 采样
```

5 - 值班 Runbook（5 条当场动作）

1. **判断类型**：看 `active/max`、`queue_fill_ratio`、`rejected`、依赖侧 P95。
2. **当场止血**：
   - 降**客户端 read/connect 超时**到 300–800ms，开启/收紧**熔断**；
   - 降入口 RPS 或开**灰度降级**；
   - 临时**下调 pool.max/queueCap**，让 `CallerRuns/Abort` 生效（把压力推回）。
3. **重试收敛**：把**固定间隔重试**切换为**预算 ≤10% + 指数退避**。
4. **排查根因**：`jcmd Thread.print -l` ×3，确认 I/O/锁/CPU；必要时 JFR 60s。
5. **复盘修复**：无界队列→有界；阻塞任务→自定义池；热点锁→`tryLock(timeout)`/分段；默认 CF 池→分池。

**项目口径（口语）**
“凡新我们把‘队列斜率>0’作为**早预警**，常能在拒绝暴涨前降入口；麦克尔斯把 `callerRuns_total` 暴露出来，一看到上升就知道**上游已被背压**，能协同前端做降级。”

**一句话总结**

> **看“饱和 + 斜率 + 拒绝”三件事**：`active/max`、`queue_fill_ratio`、`rejected`。告警触发时按 Runbook：**限时/熔断/降入口/收紧池/退避重试**；看板一屏判型、指标能回放，问题就很难失控。

### 把并发策略整合落地：一段可直接用于面试的完整口语回答

**面试官：**

“黑五/双 11 高峰时，你如何**端到端**保证下单链路既扛得住突发、又不把下游打穿？请结合你在（深圳市凡新科技 / 麦克尔斯深圳）的实战，说清楚：线程池/队列/拒绝策略、超时与取消、重试与熔断、锁/热点治理、监控与止血动作。最好给出可以落地的参数与结果。”

**你：**

“我把它当一条‘**受控的水路**’来设计：入口限流 → 线程池背压 → 可取消的并行调用 → 预算化重试 + 熔断 → 热点锁治理 → 可观测与值班 SOP。

1. **入口节拍**
   - 网关上**令牌桶**先限速，避免把洪水一次性压到应用。命中 `429` 带 `Retry-After`。
2. **线程池 = 背压阀，不是仓库**
   - 按依赖**分池**（支付/库存/优惠各一组），避免相互拖垮；
   - 用**有界队列** `ArrayBlockingQueue`，队列容量就是**等待预算**；
   - 拒绝策略用 **CallerRuns**（让上游自然减速）或 **Abort**（快速降级）。
   - 在凡新库存外呼上，我们落了：`core=12, max=48, queue=2000, keepAlive=60s, allowCoreTimeout=true, CallerRuns`。
   ```java
   ThreadPoolExecutor pool = new ThreadPoolExecutor(
       12, 48, 60, TimeUnit.SECONDS,
       new ArrayBlockingQueue<>(2000),
       r -> new Thread(r, "outbound-stock-" + r.hashCode()),
       new ThreadPoolExecutor.CallerRunsPolicy()
   );
   pool.allowCoreThreadTimeOut(true);
   ```
3. **并行外呼：fail-fast + 可取消 + 明确降级**
   - `CompletableFuture` 一律用**自定义有界池**，子调用 `orTimeout`，总体加 **deadline**；关键依赖失败**立刻取消 siblings**：
   - 价格/库存 250–400ms 超时；总 **1.2s** deadline；每个分支 `exceptionally` 落到明确的 fallback（例如“受理中”/“未知库存”）。
4. **重试=预算化 + 退避抖动；熔断切回路**
   - 只对**幂等**请求重试；**指数退避 + full jitter**，**预算 ≤10%**（`retry_count ≤ 0.1 * success + 50`）；
   - 熔断打开期间**不重试**，仅半开做少量探测；命中 `429` 对齐 `Retry-After`。
5. **热点与锁**
   - 把**远程调用移出临界区**，热点路径改 `tryLock(80–100ms)+降级`，必要时做**锁分段**（`stripe = hash(key)%64`）；
   - 计数/埋点用 **LongAdder** 替代全局锁；读多写少的结构换 **读写锁**（只允许降级）。
6. **可观测 & 值班 SOP**
   - 指标：`active/max、queue_size/cap、rejected、task_wait/exec p95、timeout、cancelled、依赖侧 5xx/429`；
   - 告警阈值：`active/max>0.8 && queue_fill>0.7 && 斜率>0` 连续 3 分钟；`rejected` 突增；`p95>SLA×1.5`。
   - 止血流程（5 分钟）：
     1. 降 HTTP **read/connect** 超时到 300–800ms；
     2. 开/收紧**熔断**与**降级**；
     3. **下调 pool.max/queueCap** 让背压生效；
     4. 降入口 RPS；
     5. `jcmd Thread.print -l`×3 判断是 I/O 还是锁，必要时 **tryLock+降级** 或热重启单实例解除死锁。

**结果（实话实说）**

- 在**凡新**，活动高峰把库存外呼从“无界队列+固定重试”改为上述方案后，**尾延迟 P95 从 ~1.8s 降到 ~350ms**，即使 `rejected` 有抬头，下游 QPS 仍稳定、不被打穿；
- 在**麦克尔斯**，图片索引链路原先 OOM，是无界排队 + 固定重试导致。我们切到**有界 + Abort + 预算化重试 + 慢车道回放**，**GC 恢复稳定**，用户侧得到“受理中/稍后刷新”的一致体验。”

**一句话总结**

> 限流稳节拍，**有界池+CallerRuns/Abort** 做背压，`CF` **超时+取消+降级**，重试**预算化**并与**熔断**协同，热点锁用 **tryLock/分段/缩小临界区**，配上指标与 SOP——这套在我们两家公司的高峰都扛过实战。”

---

好—这是你的 **≈60s 英文口语稿**（可直接朗读）。贴到当日文档或 `elevator_pitch_en.md` 的 “ThreadPool tuning” 小节即可。

---

## Step 4：1 分钟英文口语

### 1-min Answer — Tuning ThreadPoolExecutor for bursts while protecting downstreams

**Script (≈60s)**
On peak traffic I treat the thread pool as a **back-pressure valve, not a warehouse**.
First, I **bulkhead by dependency**—inventory, payments, coupons each have their own bounded pool. For a typical IO-heavy caller I use something like: core ≈ CPU, max ≈ 4×CPU, and an **ArrayBlockingQueue** sized to my **waiting budget**. The rejection policy is **CallerRuns** or **Abort** so pressure propagates back instead of piling up.

Every downstream call has **hard timeouts** and the orchestration has a **global deadline**. With `CompletableFuture` I run branches on a **custom bounded executor**, add `orTimeout`, and if a key branch fails I **cancel siblings** and degrade gracefully.

**Retries are budgeted**—exponential backoff with jitter, ≤10% of success volume, and we honor `Retry-After`. When error rate spikes we flip the **circuit breaker**: no retries while open, only small probes in half-open.

We watch **active/max**, **queue fill**, **rejections**, and **task wait/exec P95**. If the queue trends up for minutes we **lower incoming RPS** or widen capacity; we don’t “fix latency” by just adding threads.
Finally, we avoid lock hot spots: short critical sections, `tryLock` with timeouts, and per-key striping when needed.

This keeps tail latency flat during bursts **without melting downstreams**.

### **3 sound bites to emphasize**

* “**Bounded queue + CallerRuns/Abort = back-pressure, not backlog.**”
* “**Timeouts + deadline + cancellable fan-out** keep threads free.”
* “**Retry budgets + circuit breaker** turn storms into controlled drizzle.”
