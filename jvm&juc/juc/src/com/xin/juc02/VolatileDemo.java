package com.xin.juc02;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 柒
 * jmm 内存模型 - volatile
 *  jmm是一种抽象的内存机制
 *      三大原则：原子性  可见性  有序性
 *  volatile是一种轻量级的同步机制
 *      它保证了可见性和有序性 但是不保证原子性
 *
 * 保证可见性
 *
 * 不保证原子性
 *      不可分割 -- 完整性
 *      解决：使用juc下的原子类
 */
public class VolatileDemo {

    public static void main(String[] args) {
        // jucAtomic();
        // Visibility();
        MyData myData = new MyData();
        for (int i = 0; i < 20; i++) {
            new Thread(()->{
                for (int j = 0; j < 1000; j++) {
                    myData.add();
                    myData.atomicAdd();
                }
            },"thread-"+String.valueOf(i)).start();
        }
        try {TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) {e.printStackTrace(); }
        System.out.println("res = " + myData.number);
        System.out.println("res = " + myData.atomicInteger.get());
    }

    // juc 下的原子类
    private static void jucAtomic() {
        MyData data = new MyData();
        for (int i = 0; i < 30; i++) {
            new Thread(()->{
                for (int j = 0; j < 1000; j++) {
                    data.add();
                    data.atomicAdd();
                }
            },"thread_"+i).start();
        }

        // 默认两个线程：main 和 gc
        while(Thread.activeCount()>2){Thread.yield();}
        System.out.println(Thread.currentThread().getName() + " , int.number = " + data.number);
        System.out.println(Thread.currentThread().getName() + " , atomicInt.number = " + data.atomicInteger);
    }

    // volatile保证可见性
    private static void Visibility() {
        MyData data = new MyData();
        new Thread(()->{
            System.out.println(Thread.currentThread().getName()+"  come in ");
            try {
                TimeUnit.SECONDS.sleep(2); }catch(InterruptedException e){ e.printStackTrace(); }
                data.addTo();
                System.out.println("update success , number = " + data.number);
        },"workThread").start();

        while(data.number == 0 ){
            // 如果没有可见性 那么main线程就无法得知数据已经被修改 进而一直处于循环中
        }

        System.out.println(Thread.currentThread().getName() + " success , number = " + data.number);
    }

}

class MyData{
    volatile int number = 0 ;

    // 相当于 int xx = 0 ; 不设置默认为0
    AtomicInteger atomicInteger = new AtomicInteger();

    public void addTo(){
        number = 10 ;
    }

    public void add(){
        number ++ ;
    }

    public void atomicAdd(){
        // 原子性的 i++（先get 在 increment）
        // getandadd  i++  addAndGet  ++i
        atomicInteger.getAndIncrement();
    }

}
