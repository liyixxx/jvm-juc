package com.xin.juc02;

import java.util.concurrent.*;

/**
 * 线程池：ThreadPoolExecutor
 * 1. Executors
 *     Executors.newFixedThreadPool(5);
 *     Executors.newSingleThreadExecutor();
 *     Executors.newCachedThreadPool();
 * 2.ThreadPoolExecutor
 *     使用线程池工具类创建的线程池，本质上是new 的ThreadPoolExecutor()对象，传递的不同参数
 *
 * 死锁
 *   1. 两个或两个以上的线程在执行的过程中，因争夺资源而造成的一种互相等待的现象，如果没有外力干涉，
 *      那么这些线程将会一直等待而无法推进下去。
 *   2. 产生死锁的主要原因
 *      系统资源不足，资源分配不合理...
 *   3. 死锁demo
 *   4. 故障排查和检测
 *       jps -l ：定位java运行的进程编号
 *       jstack 进程编号：查看栈信息
 */
public class ThreadPool {

    public static void main(String[] args) {
        // tesThreadPoolExecutor();

        String lockA = "lockA";
        String lockB = "lockB";

        new Thread(new DeathLock(lockA, lockB),"threadA").start();
        new Thread(new DeathLock(lockB, lockA),"threadB").start();
    }

    // 线程池
    private static void tesThreadPoolExecutor() {
        // System.out.println(Runtime.getRuntime().availableProcessors());

        // 固定线程数
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(5);
        // 单线程池
        Executors.newSingleThreadExecutor();
        // 可扩展
        Executors.newCachedThreadPool(Executors.defaultThreadFactory());
        // 自定义
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
                Runtime.getRuntime().availableProcessors(),
                2,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        try {
            for (int i = 1; i <= 10; i++) {
                int finalI = i;
                threadPoolExecutor.execute(()->{
                    System.out.println(Thread.currentThread().getName()+":"+finalI +"\t执行...");
                    try {TimeUnit.SECONDS.sleep(2);}catch(InterruptedException e){e.printStackTrace();}
                });
            }
        }catch (Exception e ){
            e.printStackTrace();
        }finally{
            threadPoolExecutor.shutdown();
        }
    }

}

class DeathLock implements Runnable{

    private String lockA ;
    private String lockB ;

    public DeathLock(String lockA, String lockB) {
        this.lockA = lockA;
        this.lockB = lockB;
    }

    @Override
    public void run() {
        // 死锁
        synchronized (lockA){
            System.out.println(Thread.currentThread().getName()+"\t"+"持有锁:"+lockA+"，尝试获取锁:"+lockB);
            try {TimeUnit.SECONDS.sleep(1);}catch(InterruptedException e){e.printStackTrace();}
            synchronized (lockB){
                System.out.println(Thread.currentThread().getName()+"\t"+"持有锁:"+lockB+"，尝试获取锁:"+lockA);
            }
        }
    }
}