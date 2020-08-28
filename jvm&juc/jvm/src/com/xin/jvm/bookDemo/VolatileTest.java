package com.xin.jvm.bookDemo;

import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author 柒
 * @date 2020-03-24 22:04
 * @Description: volatile 测试
 * <p>
 *
 * volatile 只能保证可见性 jmm -- 通信
 * JMM : 原子性 - 可见性 - 有序性
 */
public class VolatileTest {

    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static Lock lock = new ReentrantLock();


    public static volatile int race = 0;

    public static void increase() {
        race++;
    }

    private static final int THREAD_COUNT = 20;

    public static void main(String[] args) throws InterruptedException {

    }

    private static void volatilelock() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    lock.lock();
                    try {
                        increase();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                    /*increase();*/
                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println("race = " + race);
    }

    private static void readwritelock() {
        //        CopyOnWriteArrayList<String> strings = new CopyOnWriteArrayList<>();
        Vector<String> strList = new Vector<String>();
        for (int i = 0; i < 50; i++) {
            int finalI = i;
            new Thread(() -> {
                readWriteLock.writeLock().lock();
                try {
                    strList.add(UUID.randomUUID().toString().substring(1, 4));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    readWriteLock.writeLock().unlock();
                }
                readWriteLock.readLock().lock();
                try {
                    System.out.println(strList.get(finalI));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    readWriteLock.readLock().unlock();
                }
            }).start();
        }
    }

}
