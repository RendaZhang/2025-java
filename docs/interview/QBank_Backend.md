<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [后端问题库](#%E5%90%8E%E7%AB%AF%E9%97%AE%E9%A2%98%E5%BA%93)
  - [MySQL 的存储引擎有哪些？它们之间有什么区别？](#mysql-%E7%9A%84%E5%AD%98%E5%82%A8%E5%BC%95%E6%93%8E%E6%9C%89%E5%93%AA%E4%BA%9B%E5%AE%83%E4%BB%AC%E4%B9%8B%E9%97%B4%E6%9C%89%E4%BB%80%E4%B9%88%E5%8C%BA%E5%88%AB)
  - [说说 TCP 的三次握手？](#%E8%AF%B4%E8%AF%B4-tcp-%E7%9A%84%E4%B8%89%E6%AC%A1%E6%8F%A1%E6%89%8B)
  - [说说 TCP 四次挥手？](#%E8%AF%B4%E8%AF%B4-tcp-%E5%9B%9B%E6%AC%A1%E6%8C%A5%E6%89%8B)
  - [Redis 中如何实现分布式锁？](#redis-%E4%B8%AD%E5%A6%82%E4%BD%95%E5%AE%9E%E7%8E%B0%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81)
    - [1) 加锁（原子）](#1-%E5%8A%A0%E9%94%81%E5%8E%9F%E5%AD%90)
    - [2) 解锁（必须校验 owner，且要原子）](#2-%E8%A7%A3%E9%94%81%E5%BF%85%E9%A1%BB%E6%A0%A1%E9%AA%8C-owner%E4%B8%94%E8%A6%81%E5%8E%9F%E5%AD%90)
    - [3) 续约（可选，但常见）](#3-%E7%BB%AD%E7%BA%A6%E5%8F%AF%E9%80%89%E4%BD%86%E5%B8%B8%E8%A7%81)
    - [4) 关键注意点 / 面试加分](#4-%E5%85%B3%E9%94%AE%E6%B3%A8%E6%84%8F%E7%82%B9--%E9%9D%A2%E8%AF%95%E5%8A%A0%E5%88%86)
    - [5) 单点 vs RedLock（进阶）](#5-%E5%8D%95%E7%82%B9-vs-redlock%E8%BF%9B%E9%98%B6)
    - [6) fencing token（强力加分）](#6-fencing-token%E5%BC%BA%E5%8A%9B%E5%8A%A0%E5%88%86)
    - [1) Acquire (atomic)](#1-acquire-atomic)
    - [2) Release (must verify ownership, atomically)](#2-release-must-verify-ownership-atomically)
    - [3) Renewal (optional)](#3-renewal-optional)
    - [4) Important caveats / bonus points](#4-important-caveats--bonus-points)
    - [5) Single Redis vs RedLock](#5-single-redis-vs-redlock)
    - [6) Fencing token (strong bonus)](#6-fencing-token-strong-bonus)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 后端问题库

---

## MySQL 的存储引擎有哪些？它们之间有什么区别？

**MySQL 常见的存储引擎主要有：InnoDB、MyISAM、Memory、CSV、Archive，以及 NDB（Cluster）等。实际业务里最常用的是 InnoDB。**

它们的核心区别主要在这些维度：

1. **事务与崩溃恢复**

* **InnoDB**：支持事务（ACID）、崩溃恢复能力强（redo/undo），适合绝大多数 OLTP 场景。
* **MyISAM**：不支持事务，崩溃后容易出现表损坏，需要修复。
* **Memory/CSV/Archive**：一般不做强事务能力诉求（Memory 为内存表，重启数据丢失）。

2. **锁粒度与并发**

* **InnoDB**：默认行级锁（配合 MVCC），高并发写入更友好。
* **MyISAM**：表级锁，并发写入性能差一些，但读多写少的简单场景可能还行。

3. **外键与约束**

* **InnoDB**：支持外键约束（以及更完整的约束/一致性能力）。
* **MyISAM**：不支持外键。

4. **索引与存储结构**

* **InnoDB**：主键是**聚簇索引**（数据行跟主键索引组织在一起）；二级索引叶子存主键值，需要“回表”。
* **MyISAM**：索引和数据分离（非聚簇），索引叶子存数据文件的指针。

5. **使用场景总结**

* **InnoDB**：默认首选——事务、高并发、强一致、需要崩溃恢复。
* **MyISAM**：历史引擎，适合极少写、以读为主且不需要事务/外键的场景（但现在很多场景也会用 InnoDB 替代）。
* **Memory**：临时数据、会话级缓存、小表快速查找（注意重启丢数据、容量受内存限制）。
* **Archive**：高压缩、追加写、审计/归档日志类（查询能力有限）。
* **CSV**：数据交换/导入导出方便，但不适合高性能在线查询。

**一句话收尾：线上 OLTP 基本选 InnoDB；其他引擎是针对特殊场景的功能型选择。**

**Common MySQL storage engines include InnoDB, MyISAM, Memory, CSV, Archive, and NDB (Cluster). In real-world OLTP systems, InnoDB is the default and most widely used.**

Key differences are:

1. **Transactions & crash recovery**

* **InnoDB**: Fully supports ACID transactions and strong crash recovery via redo/undo logs.
* **MyISAM**: No transactions; tables can be corrupted after crashes and may require repair.
* **Memory/CSV/Archive**: Typically not chosen for full transactional guarantees (Memory is volatile and loses data on restart).

2. **Locking & concurrency**

* **InnoDB**: Row-level locking + MVCC, better for high-concurrency writes.
* **MyISAM**: Table-level locking, weaker write concurrency.

3. **Foreign keys & constraints**

* **InnoDB**: Supports foreign keys and stronger consistency features.
* **MyISAM**: Does not support foreign keys.

4. **Indexing & storage layout**

* **InnoDB**: Uses a **clustered index** on the primary key; secondary indexes store the primary key and may require a lookup back to the clustered index.
* **MyISAM**: Stores indexes separately from data; index leaves store pointers to data rows.

5. **Typical use cases**

* **InnoDB**: General-purpose OLTP—transactions, durability, high concurrency.
* **MyISAM**: Legacy, read-heavy workloads without transactions (less common today).
* **Memory**: Fast temporary tables / session data (volatile, memory-limited).
* **Archive**: Highly compressed append-only archival/audit logs.
* **CSV**: Data interchange/import/export rather than online query performance.

**Bottom line: choose InnoDB by default for production OLTP; other engines are niche/special-purpose.**

## 说说 TCP 的三次握手？

TCP 建立连接用 **三次握手**，目的是：**双方确认彼此的收发能力，并同步初始序列号（ISN）**，从而可靠地开始数据传输。

1. **第一次握手：客户端 → 服务端（SYN）**
   客户端发送 `SYN=1, seq=x`，表示“我想建立连接，我的初始序列号是 x”。

2. **第二次握手：服务端 → 客户端（SYN+ACK）**
   服务端收到后回复 `SYN=1, ACK=1, seq=y, ack=x+1`：

* `ACK=x+1` 表示确认收到客户端的 SYN（序列号 x）
* `seq=y` 是服务端自己的初始序列号

3. **第三次握手：客户端 → 服务端（ACK）**
   客户端再发 `ACK=1, seq=x+1, ack=y+1`，确认收到服务端的 SYN（序列号 y）。
   至此双方都确认：对方能收能发、ISN 同步完成，连接进入 **ESTABLISHED**。

TCP uses a **three-way handshake** to establish a connection. The goals are to **confirm bidirectional reachability** and **synchronize initial sequence numbers (ISNs)**.

1. **Client → Server: SYN**
   Client sends `SYN=1, seq=x` to request a connection and announce its ISN.

2. **Server → Client: SYN + ACK**
   Server replies `SYN=1, ACK=1, seq=y, ack=x+1`, acknowledging the client’s SYN and providing the server’s ISN.

3. **Client → Server: ACK**
   Client sends `ACK=1, seq=x+1, ack=y+1` to acknowledge the server’s SYN.
   After this, both sides enter **ESTABLISHED**.

**Q1：为什么不是两次握手？**
两次的话，服务端无法确认“客户端收到了服务端的 SYN”。第三次 ACK 能确保服务端知道客户端已收到并确认了服务端的初始序列号，从而避免历史/延迟报文导致的错误建连等问题。

**Q2：握手过程中有哪些状态？**

* 客户端：`SYN_SENT` →（收到 SYN+ACK）→ `ESTABLISHED`
* 服务端：`LISTEN` →（收到 SYN）→ `SYN_RCVD` →（收到 ACK）→ `ESTABLISHED`

**Q3：SYN Flood 相关队列？**
服务端收到 SYN 后会进入半连接阶段，常见实现里会涉及 **半连接队列（SYN queue）**；攻击会占满它。应对手段：**SYN cookies**、调大 backlog、限速、前置防护等。

---

## 说说 TCP 四次挥手？

TCP 断开连接用 **四次挥手**，本质是：**双方的发送方向要分别关闭**（全双工），所以通常需要四个报文段完成。

假设客户端主动关闭：

1. **第一次挥手：客户端 → 服务端（FIN）**
   客户端发送 `FIN=1, seq=u`，表示“我这边不再发送数据了”（关闭客户端→服务端这个方向的发送）。

2. **第二次挥手：服务端 → 客户端（ACK）**
   服务端回复 `ACK=1, ack=u+1`，确认收到 FIN。此时客户端进入 **FIN_WAIT_2**，服务端进入 **CLOSE_WAIT**。
   注意：这时连接还没完全断，服务端可能还有数据要发给客户端。

3. **第三次挥手：服务端 → 客户端（FIN）**
   当服务端也发送完数据后，发 `FIN=1, seq=v`，表示“我这边也不再发送数据了”。

4. **第四次挥手：客户端 → 服务端（ACK）**
   客户端回复 `ACK=1, ack=v+1`，确认服务端的 FIN。客户端进入 **TIME_WAIT**（等待一段时间后关闭），服务端收到 ACK 后进入 **CLOSED**。

TCP connection termination is a **four-way handshake** because TCP is **full-duplex**: each direction is closed independently.

Assume the client actively closes:

1. **Client → Server: FIN**
   Client sends `FIN=1, seq=u` to indicate it will not send more data.

2. **Server → Client: ACK**
   Server replies `ACK=1, ack=u+1`. The server may still have data to send, so the connection is only half-closed.

3. **Server → Client: FIN**
   After the server finishes sending remaining data, it sends `FIN=1, seq=v`.

4. **Client → Server: ACK**
   Client replies `ACK=1, ack=v+1`, then enters **TIME_WAIT** before fully closing. The server transitions to **CLOSED** after receiving the final ACK.

**Q1：为什么是四次，不是三次？**
因为服务端收到客户端 FIN 后，只能先 ACK 表示“我知道你不发了”，但服务端可能还有数据没发完，需要等它发完后再发 FIN，所以 ACK 和 FIN 往往分开，形成四次。

**Q2：TIME_WAIT 为什么必须存在？**
主要两个目的：

1. **保证最后一个 ACK 能到达**：如果客户端发完 ACK 就立刻关闭，万一 ACK 丢了，服务端会重发 FIN；客户端还在 TIME_WAIT 才能再次 ACK。
2. **防止旧连接的延迟报文污染新连接**：等待 **2MSL** 让网络中可能残留的旧报文过期，避免被同四元组的新连接误接收。

**Q3：哪些状态最常见？**

* 主动关闭方：`FIN_WAIT_1` → `FIN_WAIT_2` → `TIME_WAIT` → `CLOSED`
* 被动关闭方：`CLOSE_WAIT` → `LAST_ACK` → `CLOSED`

---

## Redis 中如何实现分布式锁？

Redis 实现分布式锁，最常见的正确姿势是：**用 `SET key value NX PX ttl` 原子加锁 + 唯一 token 标识锁持有者 + Lua 脚本原子解锁**，并配合**过期时间防死锁**。

### 1) 加锁（原子）

* 使用：

   * `SET lock:order:123 <token> NX PX 30000`
* 语义：

   * `NX`：仅当 key 不存在才设置（保证互斥）
   * `PX ttl`：设置过期时间（防止持有者崩溃导致死锁）
   * `token`：用 UUID/雪花等唯一值标识“锁的主人”

加锁失败就说明有人持有锁：通常做**重试 + 随机退避**，或者直接失败返回（取决于业务）。

### 2) 解锁（必须校验 owner，且要原子）

不能直接 `DEL lockKey`，否则可能误删别人的锁（比如锁过期后被别人拿到，你再删除就删错了）。

用 Lua 保证“比较 + 删除”原子性：

```lua
if redis.call('GET', KEYS[1]) == ARGV[1] then
  return redis.call('DEL', KEYS[1])
else
  return 0
end
```

### 3) 续约（可选，但常见）

如果业务执行时间可能超过 TTL，需要**续约**（watchdog）：

* 持有锁的线程定期检查 token 仍属于自己，然后 `PEXPIRE` 延长 TTL（通常也用 Lua 保证安全）。
* 常见实现：Redisson 的“看门狗”机制。

### 4) 关键注意点 / 面试加分

* **锁的粒度**：`lock:resourceId`，避免一把大锁拖垮并发。
* **超时与退避**：自旋别太猛，使用随机退避避免惊群。
* **不要把 Redis 锁当成强一致的最终真理**：主从切换/网络分区可能出现极端情况下的“锁丢失/双持有”。

### 5) 单点 vs RedLock（进阶）

* **单 Redis 实例**：简单、性能好，但 Redis 故障/切主时存在极端一致性风险。
* **RedLock（多 Redis 节点、取多数派）**：在多个相互独立的 Redis 上加锁，拿到多数才算成功，用于提高容错性；但在网络分区、时钟/延迟等条件下仍有争议，很多公司更倾向用 **etcd/ZooKeeper** 做强一致锁，或者用 **fencing token** 来兜底。

### 6) fencing token（强力加分）

为防止“旧持有者恢复后继续写”的问题（例如 GC 停顿、网络抖动），可以在拿锁时额外获取一个单调递增的 **fencing token**（如 `INCR lock:token`），写下游资源时带上 token，让下游只接受更大的 token，从业务层避免“过期持有者写入”。

To implement a distributed lock with Redis, the common correct approach is: **atomic acquire with `SET key value NX PX ttl` + a unique owner token + atomic release via Lua**, with **TTL to prevent deadlocks**.

### 1) Acquire (atomic)

Use:

* `SET lock:order:123 <token> NX PX 30000`

Meaning:

* `NX`: only set if the key doesn’t exist (mutual exclusion)
* `PX ttl`: expiration to avoid deadlocks if the owner crashes
* `token`: a unique value (UUID) identifying the lock owner

If acquire fails, use retry with **backoff + jitter** or fail fast depending on the use case.

### 2) Release (must verify ownership, atomically)

Never do `DEL` directly, otherwise you might delete someone else’s lock after expiration/reacquire.

Use a Lua script to “check-and-delete” atomically:

```lua
if redis.call('GET', KEYS[1]) == ARGV[1] then
  return redis.call('DEL', KEYS[1])
else
  return 0
end
```

### 3) Renewal (optional)

If the critical section may exceed TTL, implement **lock renewal** (watchdog): periodically extend TTL only if the token still matches.

### 4) Important caveats / bonus points

* Choose proper lock granularity (`lock:<resourceId>`)
* Avoid busy spinning; use backoff + jitter
* Redis locks are not a perfect “strong-consistency” primitive under failover/partitions in edge cases

### 5) Single Redis vs RedLock

* **Single Redis**: simple and fast, but has edge-case risks during failover.
* **RedLock**: acquire on multiple independent Redis nodes and require quorum; improves fault tolerance, but still debated under certain network/latency assumptions. For strong consistency, many teams prefer **etcd/ZooKeeper**, or add **fencing tokens**.

### 6) Fencing token (strong bonus)

Use a monotonic token (e.g., `INCR`) and include it in downstream writes so the downstream system rejects stale owners, preventing “old owner writes” after pauses.

---
