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
    - [`synchronized` vs `ReentrantLock`：可中断 / 定时 / 公平 / 条件队列（外加：锁粗化与分段）](#synchronized-vs-reentrantlock%E5%8F%AF%E4%B8%AD%E6%96%AD--%E5%AE%9A%E6%97%B6--%E5%85%AC%E5%B9%B3--%E6%9D%A1%E4%BB%B6%E9%98%9F%E5%88%97%E5%A4%96%E5%8A%A0%E9%94%81%E7%B2%97%E5%8C%96%E4%B8%8E%E5%88%86%E6%AE%B5)
    - [`ThreadPoolExecutor` 七参数、队列取舍与拒绝策略（含反模式与调参示例）](#threadpoolexecutor-%E4%B8%83%E5%8F%82%E6%95%B0%E9%98%9F%E5%88%97%E5%8F%96%E8%88%8D%E4%B8%8E%E6%8B%92%E7%BB%9D%E7%AD%96%E7%95%A5%E5%90%AB%E5%8F%8D%E6%A8%A1%E5%BC%8F%E4%B8%8E%E8%B0%83%E5%8F%82%E7%A4%BA%E4%BE%8B)

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

场景题

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

### `synchronized` vs `ReentrantLock`：可中断 / 定时 / 公平 / 条件队列（外加：锁粗化与分段）

> **锁的选择**：短小互斥→`synchronized`；需要**可中断/定时/多条件/可观测**→`ReentrantLock`。公平锁减吞吐；`unlock` 一定放 `finally`；条件队列要**循环检查**；读多写少可上 `ReentrantReadWriteLock`（只允许**降级**）；热点用**锁分段**，长等待用 **tryLock(timeout)+重试/降级**。

场景题

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

### `ThreadPoolExecutor` 七参数、队列取舍与拒绝策略（含反模式与调参示例）

> **线程池 = 背压阀**：**有界队列 + 合理 core/max + CallerRuns/Abort 推回上游**；按**依赖分池**（Bulkhead），外呼**强制超时**，命中拒绝→**降级+退避**；拒绝把 `LinkedBlockingQueue` 用成**无界**；SynchronousQueue 需配强限流；观测 `active/queue/rejected/p95` 做动态调度。

场景题

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
- **`LinkedBlockingQueue`**：默认**无界**（反模式，可能 OOM）；如用务必**指定上限**。
- **`SynchronousQueue`**：**零容量**直传；适合**低延迟 + 可弹性扩线程**，但**极易打穿下游**，除非有**强兜底**（限流/熔断）。
- **`PriorityBlockingQueue`**：有优先级但**可能饿死**低优先任务；仅在确需优先级时用。

3- 拒绝策略（语义与场景）

- **`CallerRunsPolicy`（首选）**：把任务在**调用线程**执行 → **自然限速**，把压力**传回上游**；适合 BFF/同步链路。
- **`AbortPolicy`**：抛 `RejectedExecutionException` → **快速失败**，让上层走**降级/重试**。
- **`DiscardPolicy/DiscardOldestPolicy`**：静默丢弃/丢最老任务，**不建议**用于关键业务（难排障）。

4 - 反模式 RISK 清单

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
