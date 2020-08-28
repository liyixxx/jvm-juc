package com.xin.juc02;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author 柒
 * @date 2020-03-27 21:22
 * @Description: CAS AtomicReference ABA List Lock JucUtil
 */
public class ReviewCode {

    // volatile不会保证原子性
    static volatile Integer number = 0;
    // juc提供的原子类
    static AtomicInteger atomicInteger = new AtomicInteger();
    // 原子引用
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(10, 1);

    public static void main(String[] args) {
        // testCAS();
        // tesCAS_Unsafe();
        // tesCAS_Reference_ABA();
        // tesCAS_ABA_Stamp();
        // tesList();
        // tesRecursiveLock();
        // tesSpinlock();
        tesWorLock();
    }

    // 读写锁
    private static void tesWorLock() {
        WORLock worLock = new WORLock();
        for (int i = 0; i < 10; i++) {
            final String finalI = String.valueOf(i);
            new Thread(() -> {
                worLock.write(finalI, UUID.randomUUID().toString().substring(1, 6));
            }, "thread - " + String.valueOf(i)).start();
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 10; i++) {
            final String finalI = String.valueOf(i);
            new Thread(() -> {
                worLock.read(finalI);
            }, "thread - " + String.valueOf(i)).start();
        }
    }

    // 自旋锁
    private static void tesSpinlock() {
        Spinlock spinlock = new Spinlock();
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                spinlock.lock();
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                spinlock.unLock();
            }, String.valueOf("thread - " + i)).start();
        }
    }

    // 递归锁
    private static void tesRecursiveLock() {
        ReentrantLock lock = new ReentrantLock();
        TesRecursiveLock tesLock = new TesRecursiveLock();
        new Thread(() -> {
            tesLock.OutMethod();
        }, "A").start();
        new Thread(() -> {
            tesLock.OutMethod();
        }, "B").start();


        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();

        new Thread(() -> {
            tesLock.reOutLock();
        }, "C").start();

        new Thread(() -> {
            tesLock.reOutLock();
        }, "D").start();
    }

    // 集合不安全
    private static void tesList() {
        new Vector<>();
        Collections.synchronizedList(new ArrayList<>());
        Collections.synchronizedSet(new HashSet<>());
        Collections.synchronizedMap(new HashMap<String, Object>());
        List<Integer> integers = new CopyOnWriteArrayList<>();
        Set<Integer> set = new CopyOnWriteArraySet<>();
        ConcurrentHashMap<String, Object> maps = new ConcurrentHashMap<>();
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                integers.add((int) (Math.random() * 10));
                System.out.println(integers);
            }, "thread_" + String.valueOf(i)).start();
        }
    }

    // 使用原子引用,版本来解决ABA问题
    public static void tesCAS_ABA_Stamp() {
        new Thread(() -> {
            atomicStampedReference.getStamp();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            atomicStampedReference.compareAndSet(10, 20,
                    atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            atomicStampedReference.compareAndSet(20, 10,
                    atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t 最终修改版本:" + atomicStampedReference.getStamp());
            System.out.println(Thread.currentThread().getName() + "\t 最终修改的值:" + atomicStampedReference.getReference());
        }, "thread_A").start();

        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean flag = atomicStampedReference.compareAndSet(10, 100,
                    stamp, atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t 最终修改版本:" + atomicStampedReference.getStamp());
            System.out.println(Thread.currentThread().getName() + "\t 修改是否成功:" + flag);
            System.out.println(Thread.currentThread().getName() + "\t 最终修改的值:" + atomicStampedReference.getReference());
        }, "thread_B").start();

    }

    // ABA问题
    public static void tesCAS_Reference_ABA() {
        new Thread(() -> {
            // 0->10 10->0 产生ABA问题
            atomicInteger.compareAndSet(0, 10);
            atomicInteger.compareAndSet(10, 0);
            System.out.println(Thread.currentThread().getName() + "\t" + atomicInteger.get());
        }, "thread_A").start();

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            atomicInteger.compareAndSet(0, 5);
            System.out.println(Thread.currentThread().getName() + "\t" + atomicInteger.get());
        }, "thread_B").start();
    }

    // Unsafe CAS的核心类 调用底层操作系统资源来执行
    public static void tesCAS_Unsafe() {
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // 自增
                    atomicInteger.getAndIncrement();
                    number++;
                }
            }, "thread-" + String.valueOf(i)).start();
        }
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(atomicInteger.get() + "\t" + number);
    }

    // cas 期望值和当前线程比较 想要才进行操作 不同什么都不做
    public static void tesCAS() {
        System.out.println(atomicInteger.compareAndSet(0, 10) + "\t" + atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(0, 15) + "\t" + atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(10, 20) + "\t" + atomicInteger.get());
    }

}

// 递归锁
class TesRecursiveLock {

    private Lock lock = new ReentrantLock(false);

    public synchronized void OutMethod() {
        System.out.println(Thread.currentThread().getName() + "\t" + "outMethod...");
        InnerMethod();
    }

    public synchronized void InnerMethod() {
        System.out.println(Thread.currentThread().getName() + "\t" + "innerMethod...");
    }

    public void reOutLock() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t" + "reOutLockMethod...");
            reInnerLock();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void reInnerLock() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t" + "reInnerLockMethod...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}


// 自旋锁
class TesSpinlock {

    private AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void lock() {
        // 所有线程同时进来
        System.out.println(Thread.currentThread().getName() + " come in ....");
        Thread thread = Thread.currentThread();
        while (!atomicReference.compareAndSet(null, thread)) {
            // 期望值为null --> 当前没有线程在执行 --> 执行逻辑
            // 期望值不为Null --> 已经有线程在执行 --> 等待
        }
        System.out.println(Thread.currentThread().getName() + " 获取到资源 ， 执行方法...");
    }

    public void unlock() {
        Thread thread = Thread.currentThread();
        // 如果有线程 释放
        atomicReference.compareAndSet(thread, null);
        System.out.println(Thread.currentThread().getName() + " 释放了资源 ...");
    }

}

// 读写锁
class WORLock {

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Map<String, String> map = new HashMap<>();

    public void read(String key) {
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 开始读取 ...");
            String res = map.get(key);
            try{TimeUnit.MILLISECONDS.sleep(100); }catch(InterruptedException e){ e.printStackTrace(); }
            System.out.println(Thread.currentThread().getName() + " 读取完成 ..." + res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void write(String key, String value) {
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 开始写入 ...");
            map.put(key,value);
            System.out.println(Thread.currentThread().getName() + " 写入完成 ...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

}