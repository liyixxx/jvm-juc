package com.xin.juc.demo;

import org.junit.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Ticket{
    private int num = 30 ;

    // ReentrantLock 可重入锁
    Lock lock = new ReentrantLock() ;

    public void  sale(){
        // 加锁
        lock.lock();
        try {
            if(num > 0){
                System.out.println(Thread.currentThread().getName()+"\t卖出第"+(num--)+"票,剩余:"+num);
            }
        }finally {
            // 释放锁
            lock.unlock();
        }
    }
}

@FunctionalInterface
interface lambdaFunc{
    void say();

    default Integer add(Integer x, Integer y){
        return x*y ;
    }

    static Integer max(Integer x, Integer y){
        return x>y?x:y;
    }
}

public class TicketDemo {

    @Test
    public void test1(){
        Ticket ticket = new Ticket();
        // 线程的几种状态：NEW（创建） RUNNABLE（运行） BLOCKED（锁） WAITING(一直等待) TIMED_WAITING(等待一定时间) TERMINATED(终止)

        new Thread(()->{for(int i = 1; i <= 40; i++)ticket.sale();},"A").start();
        new Thread(()->{for(int i = 1; i <= 40; i++)ticket.sale();},"B").start();
        new Thread(()->{for(int i = 1; i <= 40; i++)ticket.sale();},"C").start();
    }

    @Test
    public void test2(){
        // 接口中可以实现方法：default修饰 或者是 静态方法static
        lambdaFunc fun = ()->System.out.println("hello world");
        fun.say();
        System.out.println(fun.add(5, 12));
    }

}
