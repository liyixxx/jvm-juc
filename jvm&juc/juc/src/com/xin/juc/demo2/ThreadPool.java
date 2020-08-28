package com.xin.juc.demo2;

import java.util.concurrent.*;

/**
 * @author 柒
 * @date 2020-03-12 14:20
 * @Description:线程池
 *
 *  OOM : 内存溢出异常
 *
 * 不使用Executors来创建线程池，而是使用ThreadPoolExecutor
 * 主要参数：
 *      1. corePoolSize : 核心线程
 *      2. maxPoolSize : 最大线程
 *      3. keepAliveTime : 线程存活时间
 *      4. TimeUnit : aliveTime单位
 *      5. blockingQueue : 队列
 *      6. threadFactory : 线程创建工厂
 *      7. rejectedHandler : 拒绝策略
 *
 * 线程执行流程
 *  1. 判断线程数是否大于corePoolSize ， 大于则将多余线程放入BlockingQueue中
 *  2. 判断线程数是否大于当前容量(corePoolSize + BlockingQueue.capaity) ，大于则进行扩容，扩容到最大线程数maximumPoolSize
 *  3. 判断线程数是否大于线程池总容量(maximumPoolSize + BlockingQueue.capaity) ，大于则采取拒绝策略
 *  4. 如果一个线程在keepAliveTime时间内没有请求，并且当前线程>maxPoolSize ,那么该线程会被销毁
 *
 * 线程池拒绝策略:
 *  ThreadPoolExecutor.AbortPolicy() 默认拒绝策略  超过线程池容量时 直接报异常
 *  ThreadPoolExecutor.CallerRunsPolicy() 回调 超过容量时 多余的请求会被返回到调用线程
 *  ThreadPoolExecutor.DiscardPolicy() 丢弃
 *  ThreadPoolExecutor.DiscardOldestPolicy  丢弃最旧的线程
 */
public class ThreadPool {

    public static void main(String[] args) {
//        System.out.println(Integer.MAX_VALUE);
        int processors = Runtime.getRuntime().availableProcessors();
        // 自定义线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,
                4,
                2,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        BlockingQueue <Object> queue = new ArrayBlockingQueue<>(3);
        // 线程最大容量 = maximunPoolSize + Queue.capacity
        try {
            for (int i = 0; i < 9; i++) {
                threadPoolExecutor.execute(()->{
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+"\t 执行...");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPoolExecutor.shutdown();
        }
    }

    private static void executors() {
        // 单线程 一个线程池只有一个线程
        ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
        // 固定线程 一个线程池里由多个固定的线程
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        // 可扩展线程池 根据需求改变线程池的线程数
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        // 线程池的三种创建本质都是new ThreadPoolExecutor 只不过传入的参数不同

        /**
        ThreadPoolExecutor的构造方法 :
        public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), defaultHandler);
        }

        public ThreadPoolExecutor(int corePoolSize, 常用线程数
                              int maximumPoolSize,  最大线程数
                              long keepAliveTime,   线程存活时间
                              TimeUnit unit,        线程存活时间的单位:ms/m/min ...
                              BlockingQueue<Runnable> workQueue,    阻塞队列 当线程数高于最大线程数时 多余的请求线程会进入到阻塞队列中 等待调度
                              ThreadFactory threadFactory,          线程创建工厂 默认
                              RejectedExecutionHandler handler) {   拒绝策略 当阻塞队列达到最大，并且工作线程大于最大线程数 进行拒绝
        固定线程 :
        new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
         cache :
         new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
       */

        try {
            for (int i = 0; i < 20; i++) {
                cachedThreadPool.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + "\t 执行...");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭线程池资源
            cachedThreadPool.shutdown();
        }
    }

}
