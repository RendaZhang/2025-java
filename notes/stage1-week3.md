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

