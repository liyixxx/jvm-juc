package com.xin.juc02;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author 柒
 * 锁
 * 1. 公平锁和非公平锁
 *  公平锁：在并发环境中每个线程在获取锁时都会先查看该锁的等待队列，如果为空就占有锁，否则就会进入到等待队列，按照FIFO规则等待
 *  非公平锁：在并发环境下每个线程会尝试直接占领锁，如果占领失败，就会采取类似公平锁的方式，非公平锁的执行效率更高
 *
 *  synchronized 和 reentrantLock 都是默认都是非公平锁
 *  reentrantLock可以在构造时传入一个Boolean值，来指定时公平锁还是非公平锁 默认为false
 *
 * 2. 可重入锁/递归锁
 *  指的是同一个线程，如果外层函数获得了一把锁，内层的函数也就可以获取该锁，
 *  在同一个线程，外层函数获取到锁之后，进入到内层方法会自动的获取锁
 *  synchronized 和 reentrantLock 都是可重入锁
 *
 * 3. 自旋锁
 *  尝试获取锁的线程不会立即阻塞 而是会采取循环的方式取尝试获取锁
 *  优势：减少线程上下文切换小号
 *  缺点：循环占用CPU
 *
 * 4. 读写锁
 *      读 共享 --》 读 + 读 共享操作  --》 高效的并发读操作
 *      写 独占 --》 读 + 写 写 + 读  独占操作
 *
 */
public class JLock {

    public static void main(String[] args) {
        // tesRecursiveLock();
         wrSpinLock();
        // rawLock();
    }

    // 读写锁测试
    private static void rawLock() {
        RAWLock rawLock = new RAWLock();

        for (int i = 0; i < 20; i++) {
            int res = i;
            new Thread(()->{
                rawLock.set(Thread.currentThread().getName(),res);
            },"thread-"+String.valueOf(i)).start();
        }
        while(Thread.activeCount()>2){Thread.yield();}
        for (int i = 0; i < 20; i++) {
            int res = i;
            new Thread(()->{
                rawLock.get(Thread.currentThread().getName());
            },"thread-"+String.valueOf(i)).start();
        }
    }

    // 手动实现自旋锁
    private static void wrSpinLock() {
        Spinlock spinlock = new Spinlock();

        new Thread(()->{
            spinlock.lock();
            try{TimeUnit.SECONDS.sleep(7); }catch(InterruptedException e){ e.printStackTrace(); }
            spinlock.unLock();
        },"thread_A").start();

        try{TimeUnit.SECONDS.sleep(1); }catch(InterruptedException e){ e.printStackTrace(); }

        new Thread(()->{
            spinlock.lock();
            try{TimeUnit.SECONDS.sleep(1); }catch(InterruptedException e){ e.printStackTrace(); }
            spinlock.unLock();
        },"thread_B").start();
    }

    // 递归锁测试
    private static void tesRecursiveLock() {
        RecursiveLock recursiveLock = new RecursiveLock();

        // synchronized 递归锁测试
        new Thread(()->{
            recursiveLock.tesSyncOut();
        },"thread_syn_A").start();
        new Thread(()->{
            recursiveLock.tesSyncOut();
        },"thread_syn_B").start();

        try{ TimeUnit.SECONDS.sleep(1); }catch(InterruptedException e){ e.printStackTrace(); }
        System.out.println();

        // reentrantLock 递归锁测试
        new Thread(()->{
            recursiveLock.tesLockOut();
        },"thread_relock_C").start();
        new Thread(()->{
            recursiveLock.tesLockOut();
        },"thread_relock_D").start();
    }

}

// 读写锁
class RAWLock{

    volatile Map<String,Object> map = new HashMap<String,Object>();
    ReentrantReadWriteLock rawlock = new ReentrantReadWriteLock();

    public void set(String key , Object value){
        rawlock.writeLock().lock();
        try{
            System.out.println(Thread.currentThread().getName() + " 开始写入 ...");
            map.put(key,value);
            System.out.println(Thread.currentThread().getName() + " 写入完成 ...");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            rawlock.writeLock().unlock();
        }
    }

    public void get(String key){
        rawlock.readLock().lock();
        try{
            System.out.println(Thread.currentThread().getName() + " 开始读取 ...");
            Object res = map.get(key);
            try{TimeUnit.MILLISECONDS.sleep(100); }catch(InterruptedException e){ e.printStackTrace(); }
            System.out.println(Thread.currentThread().getName() + " 读取完成 ..." + res.toString());
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            rawlock.readLock().unlock();
        }
    }
}

// 自旋锁
class Spinlock{
    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void lock(){
        System.out.println(Thread.currentThread().getName()+" come in ....");
        Thread thread = Thread.currentThread();
        while (!atomicReference.compareAndSet(null,thread)){
            // 如果期望值不为null，说明依旧有线程在使用，就一直循环等待，直到获取到thread
        }
        System.out.println(Thread.currentThread().getName()+" 获取到资源 ， 执行方法...");
    }

    public void unLock(){
        Thread thread = Thread.currentThread();
        atomicReference.compareAndSet(thread,null);
        System.out.println(Thread.currentThread().getName()+" 释放了资源 ...");
    }
}

// 可重入锁 or 递归锁
// 使用多锁时 需要保证lock 和 unlock 对应，加多少把锁就需要释放多少把锁
class RecursiveLock {

    private Lock lock = new ReentrantLock(false);

    public synchronized void tesSyncOut(){
        System.out.println(Thread.currentThread().getName()+"\t 外层函数 invoke tesSyncOut ");
        tesSynIn();
    }

    public synchronized void tesSynIn(){
        System.out.println(Thread.currentThread().getName()+"\t 内层函数 invoke tesSyncIn ");
    }

    public void tesLockOut(){
        lock.lock();
        lock.lock();
        try{
            System.out.println(Thread.currentThread().getName()+"\t 外层函数 invoke tesLockOut ");
            tesLockIn();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }

    private void tesLockIn() {
        lock.lock();
        try{
            System.out.println(Thread.currentThread().getName()+"\t 内层函数 invoke tesLockIn ");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
            lock.unlock();
        }
    }
}
