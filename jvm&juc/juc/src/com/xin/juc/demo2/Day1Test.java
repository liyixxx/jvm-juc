package com.xin.juc.demo2;


import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Day1Test {


    // 线程间通信 product/consumer
    @Test
    public void test3() throws InterruptedException {
        Aircondition condition = new Aircondition();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                condition.increment();
            }
        },"A").start();

        TimeUnit.MILLISECONDS.sleep(100);
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                condition.decrement();
            }
        },"B").start();
    }

    // 集合线程安全
    @Test
    public void test2() {
//        List<String> list = new ArrayList<>();
//        Set<String> set = new HashSet<>();
//        Map<String, String> map = new HashMap<>();

        List<String> list = new CopyOnWriteArrayList<>();
        Set<String>   set = new CopyOnWriteArraySet<>();
        Map<String, String>  map = new ConcurrentHashMap<>();

        for (int i = 0; i < 20; i++) {
            new Thread(()->{
                list.add("alx");
                System.out.println(list);
            }).start();
        }

    }

    // 线程的创建
    @Test
    public void test1() throws ExecutionException, InterruptedException {
        // new Thread(new ExtendsThread(),"thread1").start();
        // new Thread(new ImplRunnable(),"thread2").start();
        // 与Runnable不同：方法名(call/run) 是否有返回值 是否抛出异常

        FutureTask<String> futureTask = new FutureTask<String>(new ImplCallable());
        new Thread(futureTask, "thread3").start();
        new Thread(futureTask, "thread4").start();
        // get()放在最后 多个线程调用同一个futureTask，内容会被复用，直接获取返回值，不在执行call
        System.out.println(futureTask.get());
    }

}


class Aircondition {

    private Integer num = 0 ;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increment(){
        lock.lock();
        try{
            while (num != 0){
                condition.await();
            }
            System.out.println("num = "+(++num));
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
            while (num == 0){
                condition.await();
            }
            System.out.println("num = "+(--num));
            condition.signalAll();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }

}

class ExtendsThread extends Thread {
    @Override
    public void run() {
        System.out.println("extend thread");
    }
}

class ImplRunnable implements Runnable {

    @Override
    public void run() {
        System.out.println("implement Runnable");
    }
}

class ImplCallable implements Callable<String> {

    @Override
    public String call() throws Exception {
        return "implement callable";
    }
}


