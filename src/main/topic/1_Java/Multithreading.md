# Java Multithreading & Concurrency Mastery

## Table of Contents
1. Overview
2. Phase 1 — Basic Threading
3. Phase 2 — Volatile vs Synchronized
4. Phase 3 — ReentrantLock
5. Phase 4 — CountDownLatch
6. Phase 5 — CyclicBarrier
7. Phase 6 — Semaphore
8. Phase 7 — ForkJoin & Parallel Algorithms
9. Phase 8 — Asynchronous Composition (`CompletableFuture`)
10. Summary

---

## Overview
This repository contains a step-by-step guide on mastering Java multithreading and concurrency. Each phase contains:
- Problem statement
- Discussion of naive/initial approaches
- Robust solutions using Java concurrency constructs
- Example code

---

## Phase 1 — Basic Threading

**Problem:** Demonstrate the difference between `Thread.start()` and `Thread.run()`.

```java
class ThreadExample {
    public static void main(String[] args) {
        Runnable task = () -> System.out.println("Running in " + Thread.currentThread().getName());

        Thread t1 = new Thread(task);
        t1.start(); // creates new thread
        t1.run();   // runs in main thread
    }
}
```

**Exercise:** Control execution order using `join()`.

```java
Thread t1 = new Thread(task, "Thread1");
Thread t2 = new Thread(task, "Thread2");

t1.start();
t1.join(); // wait for t1
t2.start();
t2.join(); // wait for t2
System.out.println("Main thread finished.");
```

---

## Phase 2 — Volatile vs Synchronized

**Problem:** Multiple threads increment a counter; final value is inconsistent even with `volatile`.

```java
volatile int counter = 0;

Runnable task = () -> {
    for (int i = 0; i < 1000; i++) {
        counter++; // not atomic
    }
};
```

**Solution with synchronized:**

```java
synchronized(this) {
    counter++;
}
```

---

## Phase 3 — ReentrantLock

**Problem:** Demonstrate advanced lock features.

```java
ReentrantLock lock = new ReentrantLock(true); // fair lock

lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}
```

**Example: Recursive reentrancy**

```java
void recursiveLock(int count) {
    lock.lock();
    try {
        if (count > 0) recursiveLock(count - 1);
    } finally {
        lock.unlock();
    }
}
```

---

## Phase 4 — CountDownLatch

**Problem:** Wait for multiple services before proceeding.

**With CountDownLatch:**

```java
CountDownLatch latch = new CountDownLatch(3);

Runnable service = () -> {
    latch.countDown();
};

latch.await(); // main thread waits
```

---

## Phase 5 — CyclicBarrier

**Problem:** Multiple threads must synchronize at phases.

```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("Phase completed"));

Runnable worker = () -> {
    System.out.println(Thread.currentThread().getName() + " phase 1");
    barrier.await();
    System.out.println(Thread.currentThread().getName() + " phase 2");
    barrier.await();
};
```

---

## Phase 6 — Semaphore

**Problem:** Limit concurrent access to resources.

```java
Semaphore semaphore = new Semaphore(3);

semaphore.acquire(); // block if no spot
semaphore.release(); // free spot
```

**Combined Example (Semaphore + CountDownLatch):**

```java
Semaphore semaphore = new Semaphore(2);
CountDownLatch latch = new CountDownLatch(5);

Runnable task = () -> {
    semaphore.acquire();
    // work
    semaphore.release();
    latch.countDown();
};

latch.await(); // main thread waits
```

---

## Phase 7 — ForkJoin & Parallel Algorithms

**Problem:** Implement parallel merge sort using ForkJoinPool.

```java
class ParallelMergeSort extends RecursiveTask<int[]> {
    protected int[] compute() {
        if (array.length <= 2) return Arrays.sort(array);
        int mid = array.length / 2;
        ParallelMergeSort leftTask = new ParallelMergeSort(left);
        ParallelMergeSort rightTask = new ParallelMergeSort(right);
        leftTask.fork();
        int[] rightResult = rightTask.compute();
        int[] leftResult = leftTask.join();
        return merge(leftResult, rightResult);
    }
}
```

---

## Phase 8 — Asynchronous Composition (`CompletableFuture`)

**Problem:** Sequential blocking I/O calls reduce efficiency.

```java
CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() -> fetchUser());
CompletableFuture<String> ordersFuture = userFuture.thenApply(u -> fetchOrders(u));
CompletableFuture<String> paymentFuture = userFuture.thenApply(u -> fetchPayment(u));

CompletableFuture.allOf(ordersFuture, paymentFuture).join();
```

**Error Handling:**

```java
.exceptionally(ex -> { System.out.println(ex.getMessage()); return null; })
```

---

## Summary

- **Thread basics:** start(), run(), join()
- **Atomicity & visibility:** volatile vs synchronized
- **Advanced locking:** ReentrantLock, fairness, reentrancy
- **Coordination utilities:** CountDownLatch, CyclicBarrier, Semaphore
- **Parallel algorithms:** ForkJoinPool, RecursiveTask
- **Async composition:** CompletableFuture, chaining, error handling