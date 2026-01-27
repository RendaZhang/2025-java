<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [后端问题库](#%E5%90%8E%E7%AB%AF%E9%97%AE%E9%A2%98%E5%BA%93)
  - [MySQL 的存储引擎有哪些？它们之间有什么区别？](#mysql-%E7%9A%84%E5%AD%98%E5%82%A8%E5%BC%95%E6%93%8E%E6%9C%89%E5%93%AA%E4%BA%9B%E5%AE%83%E4%BB%AC%E4%B9%8B%E9%97%B4%E6%9C%89%E4%BB%80%E4%B9%88%E5%8C%BA%E5%88%AB)

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

---
