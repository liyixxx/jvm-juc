package com.xin.juc.demo2;

import java.util.concurrent.*;

/**
 * @author 柒
 * @date 2020-03-12 10:57
 * @Description:juc工具
 *
 * CountDownLatch : 保证多线程能顺利执行完之后在执行主线程 构造需要一个计数器 表名线程数
 *  countDown 计数器 ： 每个线程执行完之后计数器-1
 *  await 等待 ： 只有当计数器=0时才放行
 *
 * CyclicBarrier : 构造参数(计数器，Thread)
 *      和CountDownLatch不同，CyclicBarrier计数器做加法
 *      当其他线程执行后，计数器+1 ，和初始值相同时，Thread线程执行
 *      await : 等待
 *
 * Semaphore : 信号灯，用于资源的共享和线程并发控制，由于资源数确定，在同一时间能够允许的并发线程数也是确定的
 *      semaphore.acquire : 抢占资源
 *      semaphore.release : 释放资源
 */
public class JucUtilDemo {

    public static void main(String[] args) throws InterruptedException {

         // countDownLatchTest();
         // cyclicBarrierTest();
          semaphoneTest();
    }

    // semaphone
    private static void semaphoneTest() {
        Semaphore semaphore = new Semaphore(3); // 资源数 =1 时 = synchronized
        for (int i = 0; i < 7; i++) {
            new Thread(()->{
                try {
                    // 抢占资源
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() +" 抢占到了资源...");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() +" 释放了资源...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 释放资源
                    semaphore.release();
                }
            },String.valueOf(i)).start();
        }
    }

    // cyclicBarrier
    private static void cyclicBarrierTest() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(6,()->{
            System.out.println("主线程执行");
        });

        for (int i = 0; i < 6; i++) {
            final int tempInt = i ;
            new Thread(()->{
                try {
                    System.out.println(tempInt+" 执行");
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            },String.valueOf(i)).start();
        }
    }

    // countDownLatch
    private static void countDownLatchTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 0; i < 6; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+" execute .....");
                countDownLatch.countDown();
            },String.valueOf(i)).start();
        }
        countDownLatch.await();
        System.out.println(Thread.currentThread().getName()+"execute .......");
    }


}
