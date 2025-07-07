# Week 1 - Java Basic Review

______________________________________________________________________

## Day 1 — Java Fundamentals & OOP Quick Refresh

### 1. Data Types

| Category | Types | Stored Value | Notes |
|----------|-------|--------------|-------|
| **Primitive** | `byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean` | Actual value | No `null`, fixed size |
| **Reference** | Objects, Arrays, `String`, etc. | Memory address (reference) | Can be `null`; point to heap objects |

- **Implicit / Widening Conversion**
  ```java
  int x = 10;
  double y = x;        // int → double
  ```
- **Explicit / Narrowing Conversion (Casting)**
  ```java
  double d = 9.8;
  int n = (int) d;     // 9  (fraction truncated)
  ```
- **Wrapper Classes**: `Integer`, `Double`, `Boolean`, …
  *Enable use of primitives in collections & provide utility methods.*
  ```java
  Integer obj = 128;        // autoboxing
  int raw = obj;            // unboxing
  ```

### 2. Control Structures

- **Conditionals**
  ```java
  if (score >= 60) { ... } else { ... }
  switch (day) {
      case MONDAY -> ...
      default     -> ...
  }
  ```
- **Loops**
  *`for`, `while`, `do-while`;* control execution with `break` (exit loop) and `continue` (skip iteration).

### 3. Classes & Objects

```java
class Person {
    String name;
    int age;
    void greet() {
        System.out.printf("Hi, I'm %s, %d years old.%n", name, age);
    }
}
Person p = new Person();
p.name = "Alice";
p.age = 25;
p.greet();
```

*Think of a class as a blueprint; an object is a concrete instance built from it.*

### 4. Encapsulation

```java
class Student {
    private String id;          // hidden
    public String getId() { return id; }
    public void setId(String id) {
        // validation here…
        this.id = id;
    }
}
```

*Hide internal state (`private`) and expose controlled access via getters/setters.*

### 5. Inheritance & Method Overriding

```java
class Animal {
    void makeSound() { System.out.println("Some sound"); }
}
class Dog extends Animal {
    @Override
    void makeSound() { System.out.println("Woof"); }
    void guard()     { System.out.println("Guarding"); }
}
Animal a = new Dog();   // up-cast
a.makeSound();          // prints "Woof"   ← runtime polymorphism
```

*`extends` establishes an **is-a** relationship and enables code reuse; overriding customizes behavior.*

### 6. Polymorphism

| Form | How | Decided | Example |
|------|-----|---------|---------|
| **Overloading** | Same method name, different parameter list *within the same class* | Compile time | `int add(int,int)` vs `double add(double,double)` |
| **Overriding** | Subclass re-implements parent method, same signature | Runtime | `Dog.makeSound()` overrides `Animal.makeSound()` |

> **Tip:** Overloading ≠ overriding. Return type **alone** cannot distinguish overloaded methods.

### 7. Abstract Class vs Interface

| Feature | Abstract Class | Interface |
|---------|----------------|-----------|
| Syntax | `abstract class Shape {}` | `interface Flyable {}` |
| Contains | Fields, constructors, concrete & abstract methods | `public static final` constants, abstract methods (default/static allowed ≥ Java 8) |
| Purpose | Share base code & state (generalization) | Define capability/contract (behavior) |
| Inheritance | Single (a class can extend only one) | Multiple (a class can implement many) |
| Instantiation | Cannot be instantiated | Cannot be instantiated |

```java
abstract class Shape {
    abstract double area();
}
interface Flyable {
    void fly();
}
class Circle extends Shape { ... }
class Bird implements Flyable { ... }
```

______________________________________________________________________

### Common Pitfalls to Watch

1. **`NullPointerException`**: Dereferencing a reference not yet initialized (`null`).
1. **Forgot `break` in `switch`**: Leads to fall-through.
1. **Infinite Loops**: Loop condition never becomes `false`.
1. **Using `==` with objects**: Compares references, not values. Use `equals()`.
1. **Accidental Overload Instead of Override**: Add `@Override` to catch signature mismatches.

______________________________________________________________________

### Quick Interview Flash Cards

- **Autoboxing**: Java auto-converts between primitives and wrappers (`int` ↔ `Integer`).
- **`final` vs `finally` vs `finalize()`**
  - `final`: constant or non-overridable.
  - `finally`: always runs after `try/catch`.
  - `finalize()`: legacy GC callback—avoid relying on it.
- **Polymorphism Benefits**: Code to interfaces → easier extension & testing.

______________________________________________________________________

## Day 2 - Technical Recap – Java Collections & Exception Handling

______________________________________________________________________

### 1 Java Collections Framework

| Interface | Key Implementations | Ordering | Duplicate Rules | Typical Use-Cases |
|-----------|--------------------|-----------|-----------------|-------------------|
| **List** | `ArrayList`, `LinkedList`, `Vector/Stack`, `CopyOnWriteArrayList` | Maintains insertion order | Allows duplicates (incl. multiple `null`s) | Ordered sequences, random access (`ArrayList`), queue/stack (`LinkedList`) |
| **Set** | `HashSet`, `LinkedHashSet`, `TreeSet` | HashSet → no order<br>LinkedHashSet → insertion order<br>TreeSet → natural / comparator order | No duplicates (at most one `null` in HashSet / LinkedHashSet; `null` disallowed in TreeSet) | De-duplication, membership tests, ordered sets |
| **Map** | `HashMap`, `LinkedHashMap`, `TreeMap`, `ConcurrentHashMap` | HashMap → no order<br>LinkedHashMap → insertion / access order<br>TreeMap → key order | Keys unique (one `null` in HashMap/LinkedHashMap); values may repeat | Key-value lookup, caches (LRU with LinkedHashMap), ordered dictionaries |

#### Fast Reminders

- **ArrayList vs LinkedList**

  - Random read O(1) vs O(n)
  - Mid-list insert/remove O(n) vs O(1) (after locating node)

- **HashSet / HashMap**

  - Backed by hash table; avg `add/get/remove` ≈ O(1).
  - Counts on good `hashCode()` / `equals()`.

- **TreeSet / TreeMap**

  - Red-black tree; operations O(log n).
  - Disallow `null` keys/values when ordering can’t be established.

- **Map Traversal** → prefer `entrySet()` or `forEach((k,v) -> …)` to avoid double look-ups.

______________________________________________________________________

### 2 Java Exception Model

| Category | Base Class | Compile-time Enforcement | Typical Examples |
|----------|------------|--------------------------|------------------|
| **Checked** | `Exception` (but **not** `RuntimeException`) | *Must* be caught or declared with `throws` | `IOException`, `SQLException` |
| **Unchecked / Runtime** | `RuntimeException` | No mandatory handling | `NullPointerException`, `IllegalArgumentException`, `ArithmeticException` |
| **Error** | `Error` | Never handle; JVM-level problems | `OutOfMemoryError`, `StackOverflowError` |

#### Core Syntax

```java
try {
    // risky code
} catch (SpecificException e) {
    // recovery / logging
} finally {
    // always runs → close resources
}
```

- `throw new MyException("msg");` // actively launch
- `void m() throws IOException { … }` // promise to caller

#### Custom Exceptions

```java
public class AgeOutOfRangeException extends RuntimeException {
    public AgeOutOfRangeException(String msg) { super(msg); }
}
```

Choose **extends `Exception`** when you want callers *forced* to handle; **extends `RuntimeException`** when the error is programmer-side and optional to catch.

______________________________________________________________________

### 3 Interview Nuggets

1. **Why `ArrayList` is fast for random access?**
   Continuous memory → index math `base + i`.

1. **How does `HashMap` handle collisions?**
   JDK 8+: bucket starts as linked list, converts to red-black tree when > 8 elements & table ≥ 64.

1. **Fail-Fast vs Fail-Safe Iterators?**
   *Fail-Fast* (`ArrayList`, `HashMap`) → `ConcurrentModificationException`.
   *Fail-Safe* (`CopyOnWriteArrayList`, `ConcurrentHashMap`) works on snapshot / structural copy.

1. **Try-with-Resources** (Java 7+) auto-closes `Closeable` / `AutoCloseable`:

   ```java
   try (BufferedReader br = new BufferedReader(new FileReader(p))) {
       return br.readLine();
   }
   ```

1. **Best Practice** – never swallow exceptions:

   ```java
   catch (Exception e) {
       log.error("failed", e);
       throw e;           // re-throw or wrap
   }
   ```

______________________________________________________________________

### 4 Quick Cheatsheet

```text
Collection API    → add, remove, contains, size, clear
List              → get(index), set(index,val)
Set               → uniqueness, no positional ops
Map               → put(k,v), get(k), remove(k), entrySet()
Checked Exception → must handle
Runtime Exception → programmer error, optional to handle
finally           → always executes (except System.exit)
throw / throws    → throw = inside method, throws = method signature
```

______________________________________________________________________

## Day 3 – Java Multithreading Basics

______________________________________________________________________

### 🎯 Learning Goals

| Area | What we covered | Why it matters in interviews |
|------|-----------------|------------------------------|
| **Thread creation** | `Thread` inheritance, `Runnable`, `Callable` + `Future` | Classic “How many ways to create a thread? Pros/cons?” |
| **Executor framework** | `ExecutorService`, fixed vs. cached pools, custom `ThreadPoolExecutor` | Shows you understand resource management & throughput |
| **Synchronization** | `synchronized`, monitor locks, `ReentrantLock` (lock/tryLock/timeout) | Typical “How do you make this code thread-safe?” |
| **Race conditions & atomicity** | Built an unsafe counter, then fixed it with `synchronized`, `ReentrantLock`, `AtomicInteger` | Demonstrates problem->diagnosis->solution thinking |
| **Lock-free approach** | CAS & `AtomicInteger` | Often appears as “What is CAS? When prefer atomic classes over locks?” |

______________________________________________________________________

### 1 Thread Creation Patterns

```java
// ① extends Thread
class MyThread extends Thread {
    public void run() { System.out.println("Thread run"); }
}

// ② implements Runnable
class MyTask implements Runnable {
    public void run() { System.out.println("Runnable run"); }
}
new Thread(new MyTask()).start();

// ③ implements Callable + Future
Callable<Integer> job = () -> 42;
ExecutorService pool = Executors.newSingleThreadExecutor();
Future<Integer> f = pool.submit(job);
System.out.println(f.get());
pool.shutdown();
```

**Why `Runnable` / `Callable` > `Thread`?**
*Avoids single-inheritance limit, decouples task from execution mechanics, plays nicely with thread pools.*

______________________________________________________________________

### 2 Executor Framework Highlights

| Factory | Threads created | Typical use |
|---------|-----------------|-------------|
| `newFixedThreadPool(n)` | ≤ *n* (workers reused) | CPU-bound or rate-limited I/O tasks |
| `newCachedThreadPool()` | Unbounded (idle 60 s killed) | Many short-lived tasks, bursty traffic |
| `ThreadPoolExecutor` | Fully customizable (core, max, queue, policy) | Production tuning / back-pressure |

```java
ExecutorService fixed = Executors.newFixedThreadPool(3);
ExecutorService cached = Executors.newCachedThreadPool();

ThreadPoolExecutor custom = new ThreadPoolExecutor(
    2, 4, 60, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(2),
    Executors.defaultThreadFactory(),
    new ThreadPoolExecutor.AbortPolicy()   // reject when saturated
);
```

______________________________________________________________________

### 3 Synchronization Techniques

| Technique | Code sketch | Pros | Cons |
|-----------|-------------|------|------|
| `synchronized` | `public synchronized void inc(){count++;}` | Simple, JVM optimized | Blocked threads not interruptible<br>coarse-grained |
| `ReentrantLock` | `lock.lock(); try{...} finally{lock.unlock();}` | `tryLock`, timeouts, interruptible | Manual unlock, more boilerplate |
| Atomic variable | `counter.incrementAndGet()` | Lock-free, high throughput under low contention | Only for single-variable invariants |

______________________________________________________________________

### 4 Race-Condition Demo & Fixes

```text
UnsafeCounter (no sync)  ➜  Final = 70 k  (< 100 k)
SafeCounterSync          ➜  Final = 100 k
SafeCounterLock          ➜  Final = 100 k
SafeCounterAtomic        ➜  Final = 100 k
```

**Key takeaway:** `count++` = read ➜ add 1 ➜ write (three steps).
Without mutual exclusion, two threads can interleave between read/write.

______________________________________________________________________

### 5 CAS in a Nutshell

1. Read current value
1. Compute new value
1. **Compare-And-Swap** – if memory still holds old value, write new; otherwise retry
   *CPU provides CAS as an atomic instruction (`cmpxchg`, etc.) → forms basis of `java.util.concurrent.atomic.*`.*

______________________________________________________________________

### 6 Interview Cheat Sheet

- **Q:** *How many ways to start a thread in Java?*
  **A:** 3 core: extend `Thread`; implement `Runnable` (plus `Thread`); implement `Callable` + submit to `ExecutorService` (gives `Future`).

- **Q:** *Why thread pools?*
  **A:** Reuse worker threads → lower creation cost, cap max concurrency, centralised lifecycle & queue.

- **Q:** *Difference between `synchronized` and `Lock`?*
  | `synchronized` | `ReentrantLock` |
  |----------------|-----------------|
  | implicit lock per object | explicit object |
  | cannot timeout/interrupt | `tryLock`, timed lock |
  | auto-release | must `unlock()` |

- **Q:** *When would you choose `AtomicInteger`?*
  **A:** Updating a single counter/flag at very high frequency where lock overhead hurts, and invariants are limited to that variable.

______________________________________________________________________

### 7 Further Practice

| LeetCode ID | Title | Concept |
|-------------|-------|---------|
| 1115 | Print FooBar Alternately | `wait/notify`, `Semaphore` |
| 1116 | Print Zero, Even, Odd | multi-thread coordination |
| 1195 | FizzBuzz Multithreaded | condition ordering |

______________________________________________________________________

### 📌 Daily Takeaways

1. **Always identify shared state first**; decide if you need lock, atomic, or nothing.
1. **ExecutorService > new Thread()** – unifies task submission & scaling.
1. Pick the **lightest** tool that satisfies correctness:
   `Atomic` < `synchronized` < `Lock` < higher-level constructs (`BlockingQueue`, `CompletableFuture`, etc.).
1. Measure under realistic load – contention changes the best choice.

______________________________________________________________________

### 💡 Next Steps

- Dive into concurrent collections (`ConcurrentHashMap`, `BlockingQueue`).
- Explore high-level synchronizers (`CountDownLatch`, `CyclicBarrier`, `Semaphore`).
- Review JVM memory model & `volatile` semantics for visibility guarantees.

______________________________________________________________________

## Day 4 - Tooling Review & Hands-on Practice

______________________________________________________________________

### ✅ Module 1 – Git Version Control

#### 🌱 Core Ideas

| Term | Purpose |
|------|---------|
| **Working Directory** | Your editable project files |
| **Staging Area (Index)** | Where changes wait to be committed |
| **Local Repository** | Permanent history after `commit` |
| **Remote Repository** | Gitee repo for team collaboration |

#### 🔧 Must-Know Commands

```bash
git init / add / commit
git status / log --oneline
git branch / switch / merge
git stash / stash pop
git rebase <branch>
git push / pull
```

#### 🧪 Achievements

- Initialized a local repo and linked it to **private Gitee repo** via SSH.
- Practiced branching, merging, conflict resolution.
- Mastered `stash` for shelving work and `rebase` for tidy history.

______________________________________________________________________

### ✅ Module 2 – Maven Build Tool

#### 🌱 Key Concepts

- **`pom.xml`** = project “recipe” (dependencies, plugins, build rules).
- **Lifecycle**: `validate → compile → test → package → install → deploy`.
- **Repositories**: Central, Local (`~/.m2`), Private.
- **Dependency tree** + conflict resolution via `<exclusions>`.

#### 🔧 Essential Commands

```bash
mvn compile | test | package | install
mvn dependency:tree
```

#### 🧪 Achievements

- Generated a Maven project (`maven-demo`) & produced a `.jar`.
- Added **Guava** dependency; wrote sample code.
- Simulated version clash with Hadoop → fixed using `exclusions`.

______________________________________________________________________

### ✅ Module 3 – MySQL Ops & Optimization

#### 🌱 Highlights

- DDL / DML / JOIN fundamentals.
- **Indexes** = accelerate lookup; watch for functions / type cast that kill them.
- **EXPLAIN**: check `type`, `key`, `rows`, `Extra`.
- Slow-query log = performance radar.

#### 🔧 Sample Commands

```sql
CREATE INDEX idx_email ON users(email);
EXPLAIN SELECT * FROM users WHERE email='alice@example.com';
```

#### 🧪 Achievements

- Ran MySQL in **Docker + WSL** with volume persistence.
- Built `users` & `orders` tables; executed CRUD + JOIN.
- Observed index hit vs. miss with `EXPLAIN`.

______________________________________________________________________

### ✅ Module 4 – IntelliJ IDEA Productivity

#### 🌱 Killer Shortcuts (Windows/Linux)

| Action | Keys |
|--------|------|
| Find Class/File | `Ctrl+N / Ctrl+Shift+N` |
| Global Search | `Ctrl+Shift+F` |
| Auto-import Fix | `Alt+Enter` |
| Reformat Code | `Ctrl+Alt+L` |
| Rename | `Shift+F6` |
| Debug Step | `F7 / F8 / F9` |

#### 🧪 Achievements

- Navigated, refactored, formatted via shortcuts.
- Integrated Git panel for commit & push.
- Debugged `calculateSum()` with breakpoints, conditional break, variable edit.

______________________________________________________________________

### ✅ Module 5 – Docker Basics & Java Containerization

#### 🌱 Key Notions

| Term | Meaning |
|------|---------|
| **Image** | Read-only template |
| **Container** | Running instance of an image |
| **Dockerfile** | Script to build an image |
| **Port Mapping** | `-p host:container` |

#### 🔧 Essential Commands

```bash
docker pull nginx
docker run -d -p 8080:80 nginx
docker build -t 2025-java-app .
docker run -d -p 8081:8080 2025-java-app
docker ps / stop / rm / rmi
```

#### 🧪 Achievements

- Launched **Nginx** container on port 8080.
- Wrote Dockerfile, built image for Spring-Boot JAR, and ran it on 8081.

______________________________________________________________________

### 🏁 One-Sentence Takeaway

> **Today you didn’t just learn tools—you assembled a production-grade workflow ready for real-world Java projects.**

______________________________________________________________________

### 🔜 Next-Step Roadmap

| Track | Focus |
|-------|-------|
| Java Core | Concurrency, Collections, JVM internals |
| Frameworks | Spring Boot / JPA / MyBatis |
| Deployment | Docker Compose, CI/CD pipelines |
| Algorithms | LeetCode medium daily practice |
| English | Tech résumé & interview Q&A in English |

______________________________________________________________________

**Keep coding, keep shipping.** 🚀

______________________________________________________________________
