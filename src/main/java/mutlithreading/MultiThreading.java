package mutlithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/*
 MultiThreading: is a process of executing multiple threads simultaneously.
    1. By extending Thread class
    2. By implementing Runnable interface
    When to use Runnable and when to use Thread?
        1. If we want to extend some other class then we should use Runnable interface
        2. If we want to override some method of Thread class then we should use Thread class
    3. By implementing Callable interface
    4. By using ExecutorService
    5. By using FutureTask -> represent the result of an asynchronous computation
    6. By using CompletableFuture -> advantage over FutureTask is that it can be chained
 */

public class MultiThreading {
}

// by extending Thread class
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread is running");
    }
}

// by implementing Runnable interface
class MyRunnable implements Runnable{
    @Override
    public void run() {
        System.out.println("Runnable is running");
    }
}


class Client{
    public static void main(String[] args) {
        Thread myThread = new MyThread();
        myThread.start();

        Runnable myRunnable = new MyRunnable();
//        myRunnable.run();
        Thread thread = new Thread(myRunnable);
        thread.start();
    }
}

class StartVsRunExample {
    public static void main(String[] args) {
        Runnable task = () -> {
            System.out.println("Task running in: " + Thread.currentThread().getName());
        };

        Thread t1 = new Thread(task, "Worker-Thread");

        System.out.println("Calling run() directly:");
        t1.run();  // just a method call, runs in main thread

        System.out.println("\nCalling start():");
        t1.start(); // actually starts a new thread
    }
}

class ThreadJoinExample {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println("Thread 1 starts");
            try { Thread.sleep(20000); } catch (InterruptedException e) {}
            System.out.println("Thread 1 ends");
        });

        Thread t2 = new Thread(() -> {
            System.out.println("Thread 2 starts");
            try { Thread.sleep(30000); } catch (InterruptedException e) {}
            System.out.println("Thread 2 ends");
        });

        t1.start();
        t2.start();

        // Wait for t1 and t2 to finish
        t1.join();
//        t2.join();
        System.out.println("Both threads finished. Main thread exits.");
    }
}

class Exercise1{
    public static void main(String[] args) throws InterruptedException {
        Runnable task =  () -> {
            System.out.println("Task running in: " + Thread.currentThread().getName());
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(3000));
            }catch (InterruptedException exception){
                exception.printStackTrace();
            }
        };

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start();
        }

        for (Thread t: threads){
            t.join();
        }

        System.out.println("All task finished");


//        ExecutorService executorService = Executors.newFixedThreadPool(10);
//        for (int i = 0; i < 5; i++) {
//            executorService.submit(() -> {
//                System.out.println("Task running in: " + Thread.currentThread().getName());
//                try {
//                    Thread.sleep(ThreadLocalRandom.current().nextInt(3000));
//                }catch (InterruptedException exception){
//                    exception.printStackTrace();
//                }
//            });
//        }
    }
}

class RaceConditionDemo {
    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter++; // not thread-safe
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start(); t2.start(); t3.start();

        t1.join(); t2.join(); t3.join();

        System.out.println("Final counter: " + counter);
    }
}

class SyncDemo {
    private static int counter = 0;

    private static synchronized void increment() {
        counter++;
    }

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                increment();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start(); t2.start(); t3.start();

        t1.join(); t2.join(); t3.join();

        System.out.println("Final counter: " + counter); // always 3000
    }
}


class AtomicDemo {
    private static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.incrementAndGet(); // atomic operation
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start(); t2.start(); t3.start();

        t1.join(); t2.join(); t3.join();

        System.out.println("Final counter: " + counter.get()); // always 3000
    }
}

//  volatile in Java
// 	•	Guarantees visibility only.
//      If one thread updates a volatile variable, the new value is immediately visible to all other threads.
//	•	Does not guarantee atomicity (operations like count++ are not safe, because they are read-modify-write).

// 🔥 Rule of Thumb:
//	•	Use volatile for flags / state variables.
//	•	Use synchronized (or java.util.concurrent locks/atomics) for compound operations.

class VolatileExample {
    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            System.out.println("Worker thread started");
            while (running) {
                // busy work
            }
            System.out.println("Worker thread stopped");
        });

        worker.start();

        Thread.sleep(1000); // Let worker run for a while
        System.out.println("Main thread stopping worker...");
        running = false;  // Change visible to worker because it's volatile
    }
}

class SynchronizedExample {
    private static int counter = 0;

    public static synchronized void increment() {
        counter++; // atomic now
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) increment();
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) increment();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final counter: " + counter);
    }
}

class VolatileNotEnough {
    private static volatile int counter = 0; // volatile ensures visibility

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter++;
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter++;
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final counter: " + counter); // ❌ not always 2000
    }
}

class BankAccount {
    private int balance = 1000;
    private final ReentrantLock lock = new ReentrantLock(true); // fair lock

    public void withdraw(int amount) {
        lock.lock();
        try {
            if (balance >= amount) {
                System.out.println(Thread.currentThread().getName() + " is withdrawing " + amount);
                balance -= amount;
                System.out.println("Remaining balance: " + balance);
            } else {
                System.out.println(Thread.currentThread().getName() + " insufficient balance!");
            }
        } finally {
            lock.unlock();
        }
    }
}

class ATMExample {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();

        Runnable task = () -> {
            for (int i = 0; i < 3; i++) {
                account.withdraw(300);
            }
        };

        Thread t1 = new Thread(task, "ATM-1");
        Thread t2 = new Thread(task, "ATM-2");
        Thread t3 = new Thread(task, "ATM-3");

        t1.start();
        t2.start();
        t3.start();
    }
}



class Library {
    private String book = "Java Basics";
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void readBook() {
        rwLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " reading: " + book);
            Thread.sleep(500); // simulate time to read
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void updateBook(String newBook) {
        rwLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " updating book to: " + newBook);
            this.book = newBook;
            Thread.sleep(1000); // simulate update
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}

class LibraryExample {
    public static void main(String[] args) {
        Library library = new Library();

        Runnable reader = library::readBook;
        Runnable writer = () -> library.updateBook("Advanced Java");

        new Thread(reader, "Reader-1").start();
        new Thread(reader, "Reader-2").start();
        new Thread(writer, "Librarian").start();
        new Thread(reader, "Reader-3").start();
    }
}

class ATM {
    private final ReentrantLock lock = new ReentrantLock();

    public void useATM(String user) {
        if (lock.tryLock()) {  // non-blocking
            try {
                System.out.println(user + " is using the ATM");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println(user + " couldn't access ATM, will try later.");
        }
    }
}

class TryLockExample {
    public static void main(String[] args) {
        ATM atm = new ATM();

        Runnable task = () -> atm.useATM(Thread.currentThread().getName());

        new Thread(task, "Person-1").start();
        new Thread(task, "Person-2").start();
    }
}
// 👉 Unlike lock(), which blocks forever until lock is acquired, tryLock() lets you:
//	•	Try once and continue if lock isn’t available.
//	•	Try with timeout: wait for some time before giving up.

class SurgeryRoom {
    private final ReentrantLock lock = new ReentrantLock();

    public void performSurgery() {
        try {
            System.out.println(Thread.currentThread().getName() + " waiting for surgery room...");
            lock.lockInterruptibly();  // wait, but can be interrupted
            try {
                System.out.println(Thread.currentThread().getName() + " doing surgery");
                Thread.sleep(3000);
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " was interrupted while waiting!");
        }
    }
}

class InterruptibleLockExample {
    public static void main(String[] args) throws InterruptedException {
        SurgeryRoom room = new SurgeryRoom();

        Thread doctor1 = new Thread(room::performSurgery, "Doctor-1");
        Thread doctor2 = new Thread(room::performSurgery, "Doctor-2");

        doctor1.start();
        doctor2.start();

        Thread.sleep(1000);
        doctor2.interrupt(); // cancel doctor2's waiting
    }
}
// 👉 Without lockInterruptibly(), Doctor-2 would wait forever.
//With it, Doctor-2 gracefully exits when interrupted.


class ReentrantDemo {
    private final ReentrantLock lock = new ReentrantLock();

    public void outer() {
        lock.lock();
        try {
            System.out.println("Outer lock acquired by " + Thread.currentThread().getName());
            inner();
        } finally {
            lock.unlock();
        }
    }

    public void inner() {
        lock.lock();
        try {
            System.out.println("Inner lock acquired by " + Thread.currentThread().getName());
        } finally {
            lock.unlock();
        }
    }
}

class ReentrancyExample {
    public static void main(String[] args) {
        ReentrantHoldCountDemo demo = new ReentrantHoldCountDemo();
        demo.outer();
    }
}
// 👉 Same thread calls outer() → which calls inner().
//Both can acquire lock without deadlock.
//If it wasn’t reentrant, this would deadlock immediately.

class FairnessDemo {
    private final ReentrantLock lock;

    public FairnessDemo(boolean fair) {
        this.lock = new ReentrantLock(fair);
    }

    public void access() {
        for (int i = 0; i < 3; i++) {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " got the lock");
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}

//class FairUnfairLock {
//    public static void main(String[] args) {
//        FairnessDemo2 demo = new FairnessDemo2(true); // try false too!
//
//        Runnable task = demo::access;
//
//        for (int i = 1; i <= 3; i++) {
//            new Thread(task, "Thread-" + i).start();
//        }
//    }
//}
// 👉 With fair=true, threads acquire lock in order (Thread-1 → Thread-2 → Thread-3).
//👉 With fair=false, order is unpredictable (higher throughput but less fairness).



class ReentrantHoldCountDemo {
    private final ReentrantLock lock = new ReentrantLock();

    public void outer() {
        lock.lock();
        try {
            System.out.println("Outer acquired, hold count = " + lock.getHoldCount());
            inner();
        } finally {
            lock.unlock();
        }
    }

    public void inner() {
        lock.lock();
        try {
            System.out.println("Inner acquired, hold count = " + lock.getHoldCount());
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReentrantHoldCountDemo demo = new ReentrantHoldCountDemo();
        demo.outer();
    }
}

class FairnessDemo2 {
    private final ReentrantLock fairLock;
    private final ReentrantLock nonFairLock;

    FairnessDemo2() {
        fairLock = new ReentrantLock(true);   // fair
        nonFairLock = new ReentrantLock(false); // non-fair (default)
    }

    // Worker method
    private void worker(ReentrantLock lock) {
        for (int i = 0; i < 2; i++) {  // each thread tries 2 times
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() +
                        " acquired lock (fair=" + lock.isFair() + ")");
                try {
                    Thread.sleep(200); // simulate work
                } catch (InterruptedException e) {}
            } finally {
                lock.unlock();
            }
        }
    }

    public void testLocks() {
        System.out.println("=== Non-Fair Lock Demo ===");
        runTest(nonFairLock);

        System.out.println("\n=== Fair Lock Demo ===");
        runTest(fairLock);
    }

    private void runTest(ReentrantLock lock) {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            int id = i;
            threads[i] = new Thread(() -> worker(lock), "T" + id);
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) {}
        }
    }

    public static void main(String[] args) {
        new FairnessDemo2().testLocks();
    }
}

// 🔹 Key Synchronizers We’ll Cover
//	1.	CountDownLatch → wait until other threads finish (one-time use).
//	2.	CyclicBarrier → wait until all threads reach a barrier, then proceed (reusable).
//	3.	Semaphore → limit number of threads accessing a resource.
//	4.	Phaser → advanced barrier for dynamic number of threads/phases.


//🔹 Problem Without CountDownLatch
//Let’s demonstrate waiting for multiple services to start using just synchronized and wait/notify.

class ServiceManager {
    private int servicesUp = 0;
    private final int totalServices;

    public ServiceManager(int totalServices) {
        this.totalServices = totalServices;
    }

    public synchronized void serviceUp() {
        servicesUp++;
        System.out.println(Thread.currentThread().getName() + " is UP.");
        if (servicesUp == totalServices) {
            notifyAll(); // wake main thread
        }
    }

    public synchronized void waitForServices() throws InterruptedException {
        while (servicesUp < totalServices) {
            wait(); // wait until notified
        }
    }
}

class WithoutLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        ServiceManager manager = new ServiceManager(3);

        Runnable serviceTask = () -> {
            try {
                Thread.sleep((long) (Math.random() * 2000));
                manager.serviceUp();
            } catch (InterruptedException e) {}
        };

        new Thread(serviceTask, "Database").start();
        new Thread(serviceTask, "Cache").start();
        new Thread(serviceTask, "Logger").start();

        System.out.println("Main thread waiting for services...");
        manager.waitForServices();
        System.out.println("✅ All services are UP. Main thread proceeds.");
    }
}

// Problems Here
//	1.	Manual bookkeeping → you must track servicesUp.
//	2.	Risk of deadlock/bugs if you miss notifyAll().
//	3.	Harder to extend (what if you want timeout? retry? multiple waiters?).
//	4.	Not reusable → you’d have to write this boilerplate for every coordination task.

// The same example with CountDownLatch is way cleaner:

class WithLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        Runnable serviceTask = () -> {
            try {
                Thread.sleep((long) (Math.random() * 2000));
                System.out.println(Thread.currentThread().getName() + " is UP.");
            } catch (InterruptedException e) {}
            latch.countDown();
        };

        new Thread(serviceTask, "Database").start();
        new Thread(serviceTask, "Cache").start();
        new Thread(serviceTask, "Logger").start();

        System.out.println("Main thread waiting for services...");
        latch.await(); // cleaner than wait/notify
        System.out.println("✅ All services are UP. Main thread proceeds.");
    }
}

//🔹 Problem Using synchronized + wait/notify
//
//Suppose one service never signals ready (e.g., stuck or crashed). The main thread could wait forever, because it’s manually waiting on a condition.
class ServiceManagerTimeout {
    private int servicesUp = 0;
    private final int totalServices;

    public ServiceManagerTimeout(int totalServices) {
        this.totalServices = totalServices;
    }

    public synchronized void serviceUp() {
        servicesUp++;
        System.out.println(Thread.currentThread().getName() + " is UP.");
        notifyAll(); // wake waiting threads
    }

    public synchronized void waitForServices() throws InterruptedException {
        while (servicesUp < totalServices) {
            wait(); // wait indefinitely if a service never signals
        }
    }
}

class WaitWithoutTimeout {
    public static void main(String[] args) throws InterruptedException {
        ServiceManagerTimeout manager = new ServiceManagerTimeout(3);

        // Only 2 services start; one never signals
        Runnable serviceTask = () -> {
            try {
                Thread.sleep((long) (Math.random() * 1000));
                manager.serviceUp();
            } catch (InterruptedException e) {}
        };

        new Thread(serviceTask, "Database").start();
        new Thread(serviceTask, "Cache").start();

        System.out.println("Main thread waiting for services...");
        manager.waitForServices(); // ❌ will block forever because third service never comes
        System.out.println("All services are UP. Main thread proceeds.");
    }
}

// 🔹 Solution Using CountDownLatch.await(timeout, unit)
//
//CountDownLatch lets you wait with a timeout. Even if a service never signals, main thread can continue after a given time.

class LatchWithTimeoutDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        Runnable serviceTask = () -> {
            try {
                Thread.sleep((long) (Math.random() * 1000));
                System.out.println(Thread.currentThread().getName() + " is UP.");
            } catch (InterruptedException e) {}
            latch.countDown();
        };

        new Thread(serviceTask, "Database").start();
        new Thread(serviceTask, "Cache").start();
        // Logger never starts (simulate failure)

        System.out.println("Main thread waiting for services with timeout...");
        boolean allServicesReady = latch.await(2, TimeUnit.SECONDS); // wait max 2 seconds

        if (allServicesReady) {
            System.out.println("✅ All services are UP. Main thread proceeds.");
        } else {
            System.out.println("⚠ Timeout! Some services did not start. Proceeding with caution.");
        }
    }
}

// 🔹 What is a Semaphore?
//	•	A Semaphore controls access to a limited number of resources.
//	•	Think of it as N permits:
//	•	A thread must acquire a permit to proceed (acquire()).
//	•	When done, it releases the permit (release()).
//	•	If no permits are available, threads wait until one becomes free.

// magine:
//	•	3 parking spots
//	•	6 cars trying to park concurrently
//
//Without Semaphore, if we just use synchronized or ReentrantLock, all cars could “park” at the same time — there’s nothing limiting access, so you could have 6 cars in 3 spots (logical error).

class ParkingLotWithoutSemaphore {
    private int availableSpots = 3;

    public synchronized void park(int carId) {
        if (availableSpots > 0) {
            System.out.println("Car " + carId + " parked.");
            availableSpots--;
            try {
                Thread.sleep((long) (Math.random() * 2000));
            } catch (InterruptedException e) {}
            System.out.println("Car " + carId + " leaving.");
            availableSpots++;
        } else {
            System.out.println("Car " + carId + " found no spot!");
        }
    }
}

class ParkingWithoutSemaphore {
    public static void main(String[] args) {
        ParkingLotWithoutSemaphore lot = new ParkingLotWithoutSemaphore();

        for (int i = 1; i <= 6; i++) {
            int id = i;
            new Thread(() -> lot.park(id)).start();
        }
    }
}

// ✅ Problems
//	1.	Access control is manual (availableSpots counter).
//	2.	If multiple threads check availableSpots at the same time, race conditions may occur.
//	3.	Complex to handle waiting for a spot. Cars that arrive while full either fail or busy-wait.

// 🔹 Solution Using Semaphore
//
//With Semaphore, controlling limited resources becomes automatic and thread-safe.

class ParkingLotWithSemaphore implements Runnable {
    private static final Semaphore semaphore = new Semaphore(3); // 3 spots
    private final int carId;

    ParkingLotWithSemaphore(int carId) {
        this.carId = carId;
    }

    @Override
    public void run() {
        try {
            System.out.println("Car " + carId + " trying to park...");
            semaphore.acquire(); // wait if no spot available
            System.out.println("Car " + carId + " parked.");
            Thread.sleep((long) (Math.random() * 2000));
            System.out.println("Car " + carId + " leaving.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release(); // free the spot
        }
    }
}

class ParkingWithSemaphore {
    public static void main(String[] args) {
        for (int i = 1; i <= 6; i++) {
            new Thread(new ParkingLotWithSemaphore(i)).start();
        }
    }
}

// ✅ Advantages
//	1.	Automatic access control — no manual availableSpots counter.
//	2.	Threads block automatically if no permits are available (acquire()).
//	3.	FIFO fairness can be enabled: new Semaphore(3, true).
//	4.	Eliminates race conditions, simpler & safer than synchronized.


// by using executorService
class Client1{
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i=0;i<10;i++){
            int finalI = i;
            executorService.submit(()-> System.out.println("Thread "+ finalI +" is running"));
        }
    }
}

// by implementing Callable interface
class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "Callable Thread is running";
    }
}

// 🔹 Scenario: Limited Workers + Wait for Completion
//
//Imagine:
//	•	5 tasks to process
//	•	Only 2 workers can run concurrently (limited resources → use Semaphore)
//	•	Main thread must wait until all tasks are done before proceeding → use CountDownLatch

class TaskWorker implements Runnable {
    private final int taskId;
    private final Semaphore semaphore;
    private final CountDownLatch latch;

    TaskWorker(int taskId, Semaphore semaphore, CountDownLatch latch) {
        this.taskId = taskId;
        this.semaphore = semaphore;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire(); // limited concurrent tasks
            System.out.println("Task " + taskId + " started by " + Thread.currentThread().getName());
            Thread.sleep((long) (Math.random() * 2000)); // simulate work
            System.out.println("Task " + taskId + " finished by " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release(); // free the resource
            latch.countDown();   // signal task completion
        }
    }
}

class SemaphoreLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        int totalTasks = 5;
        Semaphore semaphore = new Semaphore(2); // only 2 tasks at a time
        CountDownLatch latch = new CountDownLatch(totalTasks);

        for (int i = 1; i <= totalTasks; i++) {
            new Thread(new TaskWorker(i, semaphore, latch), "WorkerThread-" + i).start();
        }

        System.out.println("Main thread waiting for all tasks to finish...");
        latch.await(); // wait for all tasks
        System.out.println("✅ All tasks finished. Main thread proceeds.");
    }
}

// 	1.	Semaphore limits concurrent tasks (like a thread pool with limited threads).
//	2.	CountDownLatch allows the main thread to wait until all tasks finish.
//	3.	Combines resource control + coordination elegantly.
//	4.	Very close to real-world patterns: e.g., DB connections, API throttling, batch jobs.

class Client2{
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Callable<String> myCallable = new MyCallable();
//        System.out.println(myCallable.call());
        System.out.println(executorService.submit(myCallable).get());
    }
}


// by using FutureTask -> represent the result of an asynchronous computation
class Client3{
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Callable<String> myCallable = new MyCallable();
        Future<String>  future = executorService.submit(myCallable);
        System.out.println(future.get());
    }
}

// by using CompletableFuture -> advantage over FutureTask is that it can be chained
class Client4{
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                return new MyCallable().call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService);
        
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                return new MyCallable().call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService);
        
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                return new MyCallable().call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executorService);
        
        // chain all three futures
        CompletableFuture.allOf(future1, future2, future3)
                .thenRunAsync(() -> System.out.println("All futures are completed"), executorService);
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(()->"Hello", executorService)
                .thenApplyAsync(s->s+" World", executorService);
        System.out.println(completableFuture.get());
    }
}

// 4. Exercises & interview problems (practice list)
//	•	Print numbers alternately using two threads (odd/even).
//	•	Producer-consumer (multiple producers, consumers).
//	•	Implement thread-safe counter under high concurrency.
//	•	Detect and fix deadlock in a small program.
//	•	Dining philosophers (solutions: resource hierarchy, arbitrator).
//	•	Implement concurrent linked list or queue.
//	•	Use CompletableFuture to fetch three services and combine results.
//Solve each, then add heavy stress-tests (hundreds of threads) to surface issues.
//

// 3. Practical project ideas (in increasing complexity)
//	1.	Threaded producer-consumer with metrics (BlockingQueue + metrics).
//	2.	Thread-pool web-crawler (bounded thread pool + politeness).
//	3.	Concurrent LRU cache (use LinkedHashMap + synchronization or ConcurrentHashMap + eviction strategy).
//	4.	Parallel file-search across directories using ForkJoin.
//	5.	Custom ThreadPoolExecutor: implement a bounded queue + rejection policy + monitoring.
//	6.	Rate-limiter (token bucket using Semaphore).
//	7.	Mini streaming aggregator: ingest simulated events from multiple threads and maintain sliding-window aggregates safely.
//	8.	Lock-free data structure (advanced — concurrent stack or queue using CAS).