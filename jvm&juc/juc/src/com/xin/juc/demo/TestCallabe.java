package com.xin.juc.demo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class ThreadExtend extends Thread{
    @Override
    public void run() {
        System.out.println("thread by extends Thread : " + Thread.currentThread().getName());
    }
}

class RunnableImpl implements Runnable{

    @Override
    public void run() {
        System.out.println("thread by implement Runnable : "+ Thread.currentThread().getName());
    }
}

class CallableImpl implements Callable<Integer>{

    @Override
    public Integer call() throws Exception {
        System.out.println("thread by implement Callable<T> " + Thread.currentThread().getName());
        return 1024 ;
    }
}

/**
 * @author 柒
 * @date 2020-03-11 16:13
 * @Description: callable接口
 * 池：线程池 durid数据库连接池
 *
 * 线程的四种创建方式:
 *      继承 Thread类
 *      实现 Runnable接口
 *      实现 callable接口
 *      使用线程池
 */
public class TestCallabe {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // new Thread(new RunnableImpl(),"als").start();
        // new Thread(new ThreadExtend(),"als").start();

        // FutureTask 可传参数 callable 或者 runnable
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new CallableImpl());
        new Thread(futureTask,"als").start();
        // get方法通常刚在最后
        Integer res = futureTask.get();
        System.out.println(res);
    }
}
