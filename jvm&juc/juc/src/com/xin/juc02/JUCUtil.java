package com.xin.juc02;

import java.util.concurrent.*;

/**
 * @author 柒
 * juc 下的工具类
 *
 * CountDownLatch(count)  记数 -1
 * CyclicBarrier(count,runnable)  记数 +1
 * Semaphore(permits,fair)  信号灯
 *
 */
public class JUCUtil {

    public static void main(String[] args) throws InterruptedException {

        // tesCountDownLatch();
        // tesCyclicBarrier();
        Semaphore semaphore = new Semaphore(5, false);
        for (int i = 1; i <= 10; i++) {
            new Thread(()->{
                try {
                    try{TimeUnit.MILLISECONDS.sleep(10); }catch(InterruptedException e){ e.printStackTrace(); }
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + " 占用资源...");
                    try{TimeUnit.SECONDS.sleep(2); }catch(InterruptedException e){ e.printStackTrace(); }
                    System.out.println(Thread.currentThread().getName() + " 释放资源...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            },"thread_"+String.valueOf(i)).start();
        }
    }

    // CyclicBarrier
    private static void tesCyclicBarrier() {
        CyclicBarrier barrier = new CyclicBarrier(5, () -> {
            System.out.println("hello world");
        });
        for (int i = 1; i <= 5; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + " do ...");
                try {
                    try{
                        TimeUnit.MILLISECONDS.sleep(10); }catch(InterruptedException e){ e.printStackTrace(); }
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, MyEnum.getOne_OnSwitch(i).getRetVal()).start();
        }
    }

    // countDownLatch
    private static void tesCountDownLatch() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 1; i <= 6; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + " execute ...");
                countDownLatch.countDown();
                // 结合枚举使用
            }, MyEnum.getOne_OnSwitch(i).getRetVal()).start();
        }

        countDownLatch.await();
        System.out.println(Thread.currentThread().getName() + " finally do ...");
    }

}


enum MyEnum{

    // 使用枚举来定义一些属性字段，可以当成一个简单版的数据库来使用

    ONE(1,"one"),
    TWO(2,"two"),
    THREE(3,"three"),
    FORE(4,"four"),
    FIVE(5,"five"),
    SIX(6,"six");

    public static MyEnum getOne_OnSwitch(Integer id){
        MyEnum[] enums = MyEnum.values();
        for (MyEnum e : enums) {
            if(e.getRetId() == id){
                return e ;
            }
        }
        return null ;
    }

    private Integer retId ;
    private String retVal ;

    public Integer getRetId() {
        return retId;
    }

    public void setRetId(Integer retId) {
        this.retId = retId;
    }

    public String getRetVal() {
        return retVal;
    }

    public void setRetVal(String retVal) {
        this.retVal = retVal;
    }

    MyEnum(Integer retId, String retVal) {
        this.retId = retId;
        this.retVal = retVal;
    }
}