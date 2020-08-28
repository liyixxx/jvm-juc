package com.xin.juc.demo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class PrintNumber{

    // 标志位 1=A 2=B 3=C
    private int flag = 1 ;

    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    public void print5(){
        lock.lock();
        try{
            // 标志位！=1 时 等待
            while (flag != 1){
                c1.await();
            }
            for (int i = 0; i < 5; i++) {
                System.out.println(i);
            }
            // 修改标志位 唤醒目标线程
            flag = 2 ;
            c2.signal();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }

    public void print10(){
        lock.lock();
        try{
            // 标志位！=2 时 等待
            while (flag != 2){
                c2.await();
            }
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
            }
            // 修改标志位 唤醒目标线程
            flag = 3 ;
            c3.signal();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }

    public void print15(){
        lock.lock();
        try{
            // 标志位！=3时 等待
            while (flag != 3){
                c3.await();
            }
            for (int i = 0; i < 15; i++) {
                System.out.println(i);
            }
            // 修改标志位 唤醒目标线程
            flag = 1 ;
            c1.signal();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }
}

class PrintNumber2{
    private int flag = 1 ;
    private Lock lock = new ReentrantLock();
    private Condition c = lock.newCondition();

    private void print(int num){
        for (int i = 0; i <num ; i++) {
            System.out.println(i);
        }
    }

    public void execute(){
        lock.lock();
        try{
            while(flag != 1){
                c.await();
            }
            print(5);
            flag = 2 ;
            c.signal();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }
}

/**
 * @author 柒
 * @date 2020-03-11 15:35
 * @Description: condition
 *
 * condition 定向唤醒特定线程
 */
public class TestCondition {

    public static void main(String[] args) {
        PrintNumber pn = new PrintNumber();
        new Thread(()->{
            for (int i = 0; i < 5; i++) {
                pn.print5();
            }
        },"A").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                pn.print10();
            }
        },"B").start();
        new Thread(()->{
            for (int i = 0; i < 15; i++) {
                pn.print15();
            }
        },"C").start();
    }
}
