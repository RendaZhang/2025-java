# Week 3: Algorithm Enhancement and Preliminary Performance Optimization

---

## Day 1 → Day 3 Recap (Algorithms Track) 🏁

### Day 1 — Dynamic Programming “Boot Camp”

| Focus                   | Key Points                                                                       | Problems Solved                       | Wins                                     |
| ----------------------- | -------------------------------------------------------------------------------- | ------------------------------------- | ---------------------------------------- |
| **4-step DP template**  | `State → Transition → Init → Order`                                              | 509 *Fibonacci*, 70 *Climbing Stairs* | Rolled two-var solution ⇒ **O(1) space** |
| **0/1 Knapsack intro**  | 2-D → 1-D reverse loop                                                           | 416 *Partition Equal Subset Sum*      | Saved \~75 % memory over 2-D             |
| **Grid / LCS practise** | State diagram + table colouring                                                  | 62 *Unique Paths* or 1143 *LCS*       | Visual 5×5 DP table clarified flow       |
| **Takeaway**            | “DP = recurrence + cache” — once template is fixed, only the transition changes. |                                       |                                          |


### Day 2 — Advanced DP Patterns

| Module                    | Highlights                                                                                                      | Best Complexity           | Runtime\*             |
| ------------------------- | --------------------------------------------------------------------------------------------------------------- | ------------------------- | --------------------- |
| **1-D compression**       | Reverse-iterate columns to avoid double-use.                                                                    | –                         | –                     |
| **LIS**                   | ① `O(n²)` DP 43 ms →<br>② `O(n log n)` tails array 2 ms                                                         | *n log n*                 | **-95 %**             |
| **Kadane (Max Subarray)** | 2-state “hold / global” FSM; table tracing.                                                                     | *O(n)* time, *O(1)* space | –                     |
| **Interval DP**           | `len` loop + split-point `k` pattern; practised 1039 *Polygon Triangulation* & 1312 *Min Insertion Palindrome*. | *O(n³)* → memo viable     | –                     |
| **Bit-mask DP**           | 464 *Can I Win* — 2ⁿ states with memo; learned pruning by early-win.                                            | *O(2ⁿ · n)*               | Passed within <100 ms |

\*Measured on LeetCode JDK 17.

**Key Takeaways**

1. Binary-search tails turns LIS from quadratic to near-linear.
2. Interval DP = “outer length, inner split” — same skeleton as matrix-chain and stone merge.
3. Subset problems (`n ≤ 16`) → bitmask + memo beats brute force.

### Day 3 — Backtracking ✚ Greedy

| Topic                     | Core Idea                                              | Result                                                                   |
| ------------------------- | ------------------------------------------------------ | ------------------------------------------------------------------------ |
| **Backtracking template** | `if (end) … for i … choose → dfs → undo`               | Wrote ≤ 6-line skeleton reusable for all permutation / combination tasks |
| **Combinations (77)**     | Size + remaining-elements pruning                      | Calls reduced to exact **C(n,k)**                                        |
| **Permutations (46)**     | Visited-array vs swap-in-place; swap saves O(n) memory | Both AC; swap version ≈ 10 % faster                                      |
| **Combination Sum (39)**  | `if (sum+nums[i]>target) break` + level skip           | Search nodes cut **≈ 50 %**                                              |
| **Interval Greedy (435)** | Sort by earliest end, keep disjoint set                | Proof via exchange argument; deletions minimal                           |

**When Greedy Wins**

> If the problem can be restated as “pick as many intervals as possible” (or dually “remove minimum”), **earliest-finish-time** is both locally and globally optimal.

### Week-to-Date “Aha!” Moments ✨

1. **Reverse iteration** in 0/1 knapsack is the invisible guard-rail that prevents double use.
2. Lower-bound insertion in `tails[]` keeps LIS candidates minimal and **never decreases the final length**.
3. A single `break` after sorting candidates in backtracking can halve the recursion tree.
4. Greedy proofs almost always boil down to an **exchange argument** — “swap my choice with yours and nothing gets worse.”

### Next Steps

* Implement **matrix-power Fibonacci** and compare with iterative O(1).
* Try **BitSet optimisation** for LCS & subset sum.
* Extend backtracking to **N-Queens** with diagonals bitmasking to practise constraint pruning.

---

## Day 4 – JVM & GC

### 1. Heap Layout Refresher
The JVM heap is split into Eden / Survivor / Old plus a metadata space (Metaspace).  
Objects start life in Eden, survive a few minor GCs, and are tenured to the old generation once **age ≥ _MaxTenuringThreshold_** or when the **dynamic age rule** hits 50 % of Survivor space.

### 2. Collector Landscape
| Collector | Algorithm | Pause Profile | Best for | Since |
|-----------|-----------|---------------|----------|-------|
| Serial    | Copy / Mark-Compact | Long STW | ≤2 GB desktop | JDK 1.3 |
| Parallel  | Copy + ParOld | 100–300 ms | Batch throughput | JDK 1.4 |
| **G1**    | Region + Mixed | Configurable (`MaxGCPauseMillis`) | 4–32 GB server | JDK 7u4 |
| **ZGC**   | Region + Colored Pointers | ≤10 ms (JDK 17) | 32 GB+ heap | JDK 15 GA |

### 3. Baseline vs Tuned Results _(G1, 512 MB heap)_
| Metric | Before | After | Δ |
|--------|-------:|------:|--:|
| Young GC (avg / max ms) | **80 / 180** | **60 / 110** | ↓31 % |
| Mixed GC count | **4** | **1** | ↓75 % |
| Max pause (ms) | **320** | **210** | ↓34 % |
| Throughput (%) | **96.2** | **97.4** | +1.2 |

_Tuning flags_:  
```bash
-XX:InitiatingHeapOccupancyPercent=45
-XX:G1ReservePercent=15
-XX:G1HeapRegionSize=8m
````

### 4. Tools Used

* **jstat** & **jmap** for live heap snapshots
* **VisualVM** sampler to spot `com.fasterxml.jackson.*` allocations
* **GCViewer** to graph pause distributions

### 5. Key Takeaways

1. Lowering **IHOP** and increasing **G1ReservePercent** delayed mixed GCs and shaved \~30 % off worst-case pauses.
2. Region size matters: smaller regions increase relocation flexibility at the cost of card-table overhead.
3. Always match `-Xms` = `-Xmx` in containers to avoid dynamic expansion artifacts.

### 6. Next Experiments

* Double heap to **1 GB** and retest; expect Mixed GC ≈ 0.
* Try **ZGC** (`-XX:+UseZGC`) on JDK 21 aiming for sub-100 ms pauses.

---

## Day 5 - JVM Performance-Tuning Report · task-manager

| Item | Detail |
|------|--------|
| **JDK / GC** | OpenJDK 17.0.11 + G1GC |
| **Host** | WSL Ubuntu 22.04 · 4 vCPU · 8 GB RAM |
| **Load** | wrk – 500 QPS · 10 min · 4 threads / 500 conns |
| **Endpoints** | `GET /tasks` (JSON list, ~1 KB each) |

### 1 · Baseline vs Tuned Numbers

| Metric | **Baseline**<br>(512 m heap) | **Tuned**<br>(1 g heap + flags) | Δ |
|--------|----------------------------:|--------------------------------:|--:|
| TPS (req/s) | **812** | **982** | + 21 % |
| Avg latency | 118 ms | 92 ms | − 22 % |
| P99 latency | 448 ms | 238 ms | − 47 % |
| Max GC pause | 342 ms (Mixed) | 138 ms (Young) | − 60 % |
| Young / Mixed / Full | 3 080 / 46 / 2 | 2 940 / 14 / 0 | Full GC = 0 |
| Old-gen occupancy | 78 % | 56 % | − 22 pp |
| Throughput (GCViewer) | 96.1 % | 97.8 % | + 1.7 pp |

> **Tuning flags applied**

```bash
-Xms1g -Xmx1g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=150
-XX:InitiatingHeapOccupancyPercent=40
-XX:G1ReservePercent=20
-XX:ParallelGCThreads=4
````

### 2 · What Hurt in the Baseline ❌

| Symptom                        | Evidence                         | Root Cause                            |
| ------------------------------ | -------------------------------- | ------------------------------------- |
| Two Full GCs, 342 ms max pause | `gc_base.log`, GCViewer timeline | Old gen filled to 90 %, IHOP too high |
| Young GC ≈ 5.2 ✕/s             | `jstat YGC`, Eden \~8 MB         | Eden small + high burst allocations   |
| Top alloc: `byte[]` 27 %       | VisualVM Memory snapshot         | Jackson serialises every request      |

### 3 · Why This Flag ✔️

| Flag                   | Effect                                                    |
| ---------------------- | --------------------------------------------------------- |
| **1 g heap**           | Removes early promotion; avoids Full GC in 10 min run     |
| `MaxGCPauseMillis=150` | lets G1 budget pause time & schedule mixed cycles sooner  |
| `IHOP=40`              | Mixed GC starts when old ≈ 40 % heap ⇒ avoids 80 % cliff  |
| `G1ReservePercent=20`  | +20 % buffer regions ⇒ fewer “to-space exhaustion” stalls |
| `ParallelGCThreads=4`  | Matches vCPU count; young GC stages parallelised          |

### 4 · Remaining Hotspots

* **JSON allocation**: `byte[]`, `char[]`, `String` still dominate → switch to Jackson `ObjectMapper` singleton + reuse buffers (afterburner / record codec).
* **Thread pools**: CPU sampler shows `ForkJoinPool.commonPool` busy 85 %; evaluate queue sizing.

### 5 · Next Experiments 🔭

1. **ZGC on JDK 21** (`-XX:+UseZGC -Xmx1g`) to chase < 100 ms P99.
2. Pin heap inside Docker cgroup & test `-XX:+UseContainerSupport`.
3. Profile on **WSL2 vs native Linux** to measure hyper-V overhead.

### 6 · English Summary (≈ 180 words)

With a 512 MB heap the *task-manager* service suffered two Full GCs and a worst-case 342 ms pause during a 10-minute 500 QPS stress run.
GCViewer revealed that G1 did not begin mixed collections until the old generation had exceeded 75 % occupancy, so promotion pressure spiked and stop-the-world time blew past the 200 ms SLA.
We increased the heap to 1 GB, lowered **IHOP** to 40 %, raised **G1ReservePercent** to 20 %, and tightened **MaxGCPauseMillis** to 150 ms.
After retesting under the same load, Full GCs disappeared, the longest pause fell to 138 ms, and P99 latency dropped 47 %.
Throughput improved by 21 % because fewer long pauses meant worker threads spent more time in application code.
VisualVM still shows `byte[]` dominating allocations, indicating JSON serialisation is the next optimisation target.
Key lesson: **Measure → Analyse → Improve**—change one knob at a time, confirm with repeatable load tests, and always validate pauses, not just throughput.
Future work includes evaluating ZGC on JDK 21, aligning heap sizing with container limits, and eliminating transient buffers via pooled object mappers.

---

## Day 6 - MySQL Advanced Indexing & Tuning

### Problem

A hot query on **tasks** scanned ~50 k rows and dominated the slow log
(124 ms average, 380 ms P99). InnoDB showed a 94 % buffer-pool hit rate
and a full table scan (`index_name = NULL`) for that statement.

### Analysis

- `EXPLAIN JSON` revealed `key=null`, `rows=50432`, and no
  `using_index_condition`.
- `optimizer_trace` confirmed the optimizer rejected the only
  available index due to low selectivity on the **status** column.
- `performance_schema.table_io_waits_summary_by_index_usage` reported
  18 k reads with `index_name=NULL` during the 10-minute baseline run.

### Fix

```sql
CREATE INDEX idx_status_time_title
  ON tasks(created_time, status, title);   -- covering & high-selectivity
ANALYZE TABLE tasks;
````

### Result

| Metric        |  Before |     After |  Delta |
| ------------- | ------: | --------: | -----: |
| Rows examined |  50 432 |     1 476 | − 97 % |
| Avg latency   |  124 ms |     72 ms | − 42 % |
| P99 latency   |  380 ms |    180 ms | − 53 % |
| TPS           | 812 r/s | 1 020 r/s | + 26 % |

`using_index=true` and `using_index_condition=true` indicate both
covering-index and ICP are now in effect. The buffer-pool hit rate rose
to 97 %, confirming fewer random reads.

### Takeaways

1. **Covering index + proper column order = biggest single win** for
   OLTP read latency.
2. Always validate with `EXPLAIN JSON` + `optimizer_trace` to see why an
   index was (not) chosen.
3. After schema changes, run `ANALYZE TABLE` so the optimizer picks up
   fresh statistics.
4. Monitor buffer-pool metrics; a full scan can look like a memory
   shortage when it’s actually a missing index.

### Next Steps

* Evaluate **range partitioning** by year on `created_time` once the
  table exceeds 5 M rows.
* Consider a Redis read-through cache for `/tasks/{id}` to cut latency
  to sub-20 ms.
* Enable `performance_schema.events_statements_history_long` for
  continuous top-N query tracking.

---

## Week-3 Cheat Sheet 🗂️ (Algorithms · JVM · MySQL)

### 1. Algorithms

| Topic | Formula / Key Rule |
|-------|--------------------|
| DP 4-step | **State → Trans → Init → Order** |
| 1-D compression | iterate `j ↓` to avoid reuse |
| Kadane | `cur = max(a, cur+a)` |
| LIS O(n log n) | `tails[lower_bound(x)] = x` |
| Greedy proof | exchange later-ending interval |

### 2. JVM GC

| Item | Value / CLI |
|------|-------------|
| G1 IHOP | `-XX:InitiatingHeapOccupancyPercent=40` |
| Pause target | `-XX:MaxGCPauseMillis=150` |
| Heap sizing | `-Xms = -Xmx = 1g` |
| Log flag (JDK17) | `-Xlog:gc*:file=gc.log:time` |
| Max-pause check | watch **Mixed/Full** > 200 ms |

### 3. MySQL

| Concept | One-liner |
|---------|-----------|
| Covering index | `SELECT cols ⊆ index cols ⇒ no back-table` |
| ICP | index condition filtered in storage engine |
| Selectivity | `cardinality / table_rows ↑ ⇒ put left` |
| Buffer-pool hit | `(read_requests - rnd_next) / read_requests` ≥ 95 % |
| EXPLAIN rows | baseline 50 k → tuned < 2 k |

---

## Week-3 Engineering Digest

*Algorithms ⋅ JVM GC ⋅ Database Tuning*

### 1 · What I Set Out to Do

The goal for this sprint was two-fold: **sharpen algorithmic muscle memory** (DP-family, backtracking, greedy) and **push the real-world “task-manager” service through a full performance loop**—measure, analyse, tune, validate. The stack covered JVM (G1 GC), MySQL /InnoDB and a thin Redis layer.

### 2 · Highlights

| Area                      | Milestone                                                                                                                                                           | Impact                                 |
| ------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------- |
| **Dynamic Programming**   | Implemented LIS with a binary-search “tails” array. Runtime dropped from 43 ms (O(n²)) to 2 ms (O(n log n)).                                                        | ↓ 95 % execution time                  |
| **Backtracking + Greedy** | Introduced pruning (`break` + level-skip) in *Combination Sum*; search nodes cut by ≈ 50 %. Proved earliest-finish-time heuristic for interval scheduling (LC 435). | cleaner call tree & formal correctness |
| **JVM GC**                | Raised heap to 1 GB, lowered **IHOP** to 40 %, added 20 % reserve. Max G1 pause fell from 350 ms to 140 ms; Full GCs disappeared in a 10 min, 500 QPS test.         | tail-latency ↓ 60 %                    |
| **MySQL**                 | Added covering index *(created\_time, status, title)*; rows examined from 50 k → 1.5 k. P99 latency on `/tasks` shrank 53 %.                                        | TPS +26 %                              |
| **Knowledge Base**        | Created *week3-cheatsheet.md* (2 pages) + detailed GC / SQL dashboards.                                                                                             | quick revision asset                   |

### 3 · Key Challenges Encountered

* **Bit-mask DP** still takes me > 30 minutes under interview pressure.
* Balancing index width vs. write amplification—early attempt with *(status, title, created\_time)* looked “covering” but had poor selectivity and hurt inserts.
* VisualVM sampling on WSL needed extra JMX flags; cost me an hour of trial-and-error.

### 4 · What I Learned

1. **Reverse iteration** is the invisible guard-rail in 0/1 knapsack: forget it and you double-count items.
2. A single `break` in a sorted backtracking loop can save half the recursion tree—pruning is ROI--heavy.
3. In G1, lowering IHOP is the fastest way to tame long pauses **without over-allocating memory**; heap first, fancy flags later.
4. Always couple `EXPLAIN JSON` with `optimizer_trace`; the latter explains *why* an index was skipped.
5. Performance work is a scientific loop—**measure → analyse → improve**—and only counts when numbers move.

### 5 · Next Week (Week 4) Road-map

* **English communication sprint**: record two mock interview videos and write daily 150-word tech briefs.
* **Spring Cloud tracing**: integrate OpenTelemetry to visualise cross-service latency.
* Revisit bit-mask DP; target < 20 min for 16-state problems.
* Prepare three STAR-format stories (GC tuning, SQL optimisation, Redis cache) for behavioural interviews.

> **Total code submitted:** 25 Java classes, 4 SQL migrations
> **Average daily study time:** 6 h 25 min

**Ship fast, learn faster—Week 3 closed. 🚀**

---