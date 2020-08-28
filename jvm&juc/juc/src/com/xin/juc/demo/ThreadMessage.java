package com.xin.juc.demo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// synchronized
class Aircondition {
    private  int num = 0 ;

    public synchronized void increment () throws InterruptedException {
        // 判断
        while (num != 0){
            this.wait();
        }
        // 执行
        num++ ;
        System.out.println(Thread.currentThread().getName() +"\t" + num);
        // 通知
        this.notifyAll();
    }

    public synchronized void decrement () throws InterruptedException {
        // 判断
        while (num == 0){
            this.wait();
        }
        // 执行
        num-- ;
        System.out.println(Thread.currentThread().getName() +"\t" + num);
        // 通知 唤醒
        this.notifyAll();
    }
}

// lock
class Aircondition2{
    private int num = 0 ;

    // 使用lock时 不在使用wait 和 notify
    private Lock lock  = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increment(){
       lock.lock();
       try{
           while (num != 0 ){
               // wait
               condition.await();
           }
           num ++ ;
           System.out.println(Thread.currentThread().getName()+"\t"+num);

           // 消息
           condition.signalAll();
       }catch(Exception e){
           e.printStackTrace();
       }finally{
           lock.unlock();
       }
    }

    public void decrement(){
        lock.lock();
        try{
            while (num == 0 ){
                // wait
                condition.await();
            }
            num -- ;
            System.out.println(Thread.currentThread().getName()+"\t"+num);

            // 消息
            condition.signalAll();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }

}

/**
 * @author 柒
 * @date 2020-03-11 14:30
 * @Description:线程间通信
 *
 * 使用while 回执判断 防止虚假唤醒
 */
public class ThreadMessage {

    public static void main(String[] args) {
//        sync();

        lock();
    }

    private static void lock() {
        Aircondition2 aircondition = new Aircondition2();
        new Thread(()->{
            for (int i = 1; i <=10 ; i++) {
                aircondition.increment();
            }
        },"A").start();

        new Thread(()->{
            for (int i = 1; i <=10 ; i++) {
                aircondition.decrement();
            }
        },"B").start();

        new Thread(()->{
            for (int i = 1; i <=10 ; i++) {
                aircondition.increment();
            }
        },"C").start();

        new Thread(()->{
            for (int i = 1; i <=10 ; i++) {
                aircondition.decrement();
            }
        },"D").start();
    }

    private static void sync() {
        Aircondition aircondition = new Aircondition();

        new Thread(()->{
            for (int i = 1; i <= 10; i++) {
                try {
                    aircondition.increment();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },"A").start();

        new Thread(()->{
            for (int i = 1; i <= 10; i++) {
                try {
                    aircondition.decrement();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },"B").start();

        new Thread(()->{
            for (int i = 1; i <= 10; i++) {
                try {
                    aircondition.increment();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },"C").start();

        new Thread(()->{
            for (int i = 1; i <= 10; i++) {
                try {
                    aircondition.decrement();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },"D").start();
    }

}
