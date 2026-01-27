<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [JAVA 问题库](#java-%E9%97%AE%E9%A2%98%E5%BA%93)
  - [说说 JAVA 中 HashMap 的原理？](#%E8%AF%B4%E8%AF%B4-java-%E4%B8%AD-hashmap-%E7%9A%84%E5%8E%9F%E7%90%86)
    - [1) put 的核心流程](#1-put-%E7%9A%84%E6%A0%B8%E5%BF%83%E6%B5%81%E7%A8%8B)
    - [2) get 的核心流程](#2-get-%E7%9A%84%E6%A0%B8%E5%BF%83%E6%B5%81%E7%A8%8B)
    - [3) 为什么容量是 2 的幂？](#3-%E4%B8%BA%E4%BB%80%E4%B9%88%E5%AE%B9%E9%87%8F%E6%98%AF-2-%E7%9A%84%E5%B9%82)
    - [4) 扩容（resize）在做什么？](#4-%E6%89%A9%E5%AE%B9resize%E5%9C%A8%E5%81%9A%E4%BB%80%E4%B9%88)
    - [5) 时间复杂度与最坏情况](#5-%E6%97%B6%E9%97%B4%E5%A4%8D%E6%9D%82%E5%BA%A6%E4%B8%8E%E6%9C%80%E5%9D%8F%E6%83%85%E5%86%B5)
    - [6) 重要注意点（面试官常追问）](#6-%E9%87%8D%E8%A6%81%E6%B3%A8%E6%84%8F%E7%82%B9%E9%9D%A2%E8%AF%95%E5%AE%98%E5%B8%B8%E8%BF%BD%E9%97%AE)
    - [1) Core logic of `put`](#1-core-logic-of-put)
    - [2) Core logic of `get`](#2-core-logic-of-get)
    - [3) Why capacity is a power of two?](#3-why-capacity-is-a-power-of-two)
    - [4) What happens during resize?](#4-what-happens-during-resize)
    - [5) Complexity](#5-complexity)
    - [6) Common caveats](#6-common-caveats)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# JAVA 问题库

---

## 说说 JAVA 中 HashMap 的原理？

**HashMap 是基于“哈希表”的 key-value 容器**。底层在 JDK8 里主要是一个 `Node<K,V>[] table` 数组，每个桶里存的是链表或红黑树，用来在冲突时继续存放多个元素。

### 1) put 的核心流程

1. **计算 hash**：对 `key.hashCode()` 做扰动（高位参与低位），减少冲突。
2. **定位桶下标**：`index = (n - 1) & hash`（n 是数组长度，且必须是 2 的幂）。
3. **判断桶是否为空**：

    * 空：直接放入新节点。
    * 不空：说明发生**哈希冲突**，遍历该桶的链表/树：

        * 找到相同 key（`hash` 相同且 `equals` 为 true）→ **覆盖 value**
        * 没找到 → **尾插**（JDK8 一般是尾插）
4. **可能树化**：当桶里节点数 ≥ **8** 且 table 长度 ≥ **64** 时，把链表转换为**红黑树**（treeify）以把最坏查找从 O(n) 降到 O(log n)。（如果 table 还小于 64，优先扩容而不是树化）
5. **检查是否需要扩容**：当 `size > threshold` 时扩容。

    * `threshold = capacity * loadFactor`，默认 `capacity=16`，`loadFactor=0.75`。

### 2) get 的核心流程

1. 同样先算 hash，再算桶下标。
2. 桶为空返回 null；不为空则在链表/红黑树里按：

    * 先比 hash，再比 `equals`，命中即返回 value。

### 3) 为什么容量是 2 的幂？

这样用 `(n - 1) & hash` 就能快速取模，性能高；并且在扩容时（容量翻倍）元素迁移更快：新下标要么不变，要么 `+ oldCap`，由 hash 的某一位决定。

### 4) 扩容（resize）在做什么？

扩容会创建更大的数组（通常 *2 倍*），然后把旧桶里的节点重新分配到新桶。
JDK8 的迁移优化点：不用重新计算完整 hash，通过 `(hash & oldCap)` 判断元素留在原位置还是移动到 `index + oldCap`。

### 5) 时间复杂度与最坏情况

* 平均：`put/get` 期望 **O(1)**
* 冲突严重时：链表 **O(n)**
* JDK8 树化后：最坏接近 **O(log n)**

### 6) 重要注意点（面试官常追问）

* **允许一个 null key**：`null` key 会落在固定桶（hash 视为 0）。
* **非线程安全**：并发 put 可能数据丢失、结构破坏。需要线程安全用 `ConcurrentHashMap`，或外部加锁。
* **key 必须稳定且正确实现 `equals/hashCode`**：

    * hashCode 不稳定或 equals/hashCode 不一致会导致“取不出来”或冲突激增。
    * 不建议用可变对象（如把参与 hash 的字段改了）做 key。

**HashMap is a hash-table based key-value container.**
In JDK 8, it is backed by an array `Node<K,V>[] table`. Each bucket stores entries as a **linked list**, and when collisions become heavy, it can be converted into a **red-black tree**.

### 1) Core logic of `put`

1. **Compute hash**: it applies a bit-mixing (spread) on `key.hashCode()` so high bits affect low bits and reduce collisions.
2. **Find bucket index**: `index = (n - 1) & hash` where `n` is the table length (power of two).
3. **Insert into bucket**:

    * If bucket is empty, insert a new node.
    * Otherwise handle **collision** by traversing the list/tree:

        * If an existing key matches (`hash` matches and `equals` is true), **replace the value**.
        * If not found, append a new node (JDK 8 typically appends at the tail).
4. **Treeification**: if the number of nodes in a bucket reaches **8** and the table size is at least **64**, the bucket may be converted into a **red-black tree** to improve worst-case lookup from O(n) to O(log n). (If table is smaller than 64, it prefers resizing first.)
5. **Resize check**: when `size > threshold`, resize happens.

    * `threshold = capacity * loadFactor`, default `capacity=16`, `loadFactor=0.75`.

### 2) Core logic of `get`

Compute the hash, locate the bucket, then search within the linked list / tree by comparing hash and `equals`, returning the value once found.

### 3) Why capacity is a power of two?

It enables a fast modulo via bit masking `(n - 1) & hash`, and also makes resizing efficient: after doubling, an entry either stays in the same index or moves to `index + oldCap`, depending on one specific hash bit.

### 4) What happens during resize?

HashMap allocates a larger table (typically *double* the size) and re-distributes nodes. In JDK 8, re-bucketing can be optimized by checking `(hash & oldCap)` instead of recomputing the full modulo.

### 5) Complexity

* Average `put/get`: expected **O(1)**
* Worst-case with heavy collisions: **O(n)** (list)
* After treeification: close to **O(log n)** (tree)

### 6) Common caveats

* Allows **one null key** (treated as hash 0).
* **Not thread-safe**; use `ConcurrentHashMap` or external synchronization.
* Keys must correctly implement **`equals()` and `hashCode()`**, and should be **immutable** regarding fields involved in hashing.

---
