package com.xin.juc02;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阻塞队列
 *
 * 常用的阻塞队列:
 *    1. ArrayBlockingQueue<T>() 数组形式的阻塞队列
 *    2. LinkedBlockingDeque<T>(capacity) 链表形式的阻塞队列 默认长度为Integer.MAX_VALUE
 *    3. SynchronousQueue<T>(fair) 同步队列，只存储一个元素的队列，可以选择是公平锁还是非公平锁
 *
 * API：
 *    1. add / remove : 添加/取出 不符合条件时会报异常
 *         java.lang.IllegalStateException: Queue full
 *         java.util.NoSuchElementException
 *    2. offer / poll : 不会报异常 添加失败返回false ， 取出失败返回null
 *    3. put(无返回值) / take : 阻塞 ， 一直等待知道添加/取出成功
 *    4. offer(T,timout,TimeUnit) / poll(T,timout,TimeUnit) : 等待特定时间
 *
 * 线程通信
 *    线程-资源类；判断-等待/执行-唤醒
 *    1. synchronized : 会引发虚假唤醒
 *    2. lock : while判断
 *    3. BlockQueue : 使用标志位（volatile修饰）来控制是生产还是消费
 * 
 */
public class BlockQueue {

    public static void main(String[] args) throws InterruptedException {
        // tesBlockQueue();
        // threadMsgByLock();
        tesQueue();
    }

    // 使用阻塞队列来进行线程通信
    private static void tesQueue() {
        MsgByQueue msgByQueue = new MsgByQueue(new ArrayBlockingQueue<>(10));
        new Thread(()->{
            try {
                System.out.println("生产线程启动"+"\n");
                msgByQueue.product();
            } catch (Exception e) {
                e.printStackTrace();
            }
        },"prod").start();

        new Thread(()->{
            try {
                System.out.println("消费线程启动"+"\n");
                msgByQueue.consumer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        },"consumer").start();

        try {TimeUnit.SECONDS.sleep(10);}catch(InterruptedException e){e.printStackTrace();}
        msgByQueue.stop();
    }

    // 线程通信 通过lock
    private static void threadMsgByLock() {
        Number number = new Number();
        new Thread(()->{
            for (int i = 0; i < 5; i++) {
                number.incre2();
            }
        },"Thread-A").start();

        new Thread(()->{
            for (int i = 0; i < 5; i++) {
                number.desc2();
            }
        },"Thread-B").start();
    }

    // 阻塞队列的常用API
    private static void tesBlockQueue() {
        // 队列的容量
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(3);
        new LinkedBlockingDeque<String>();
        // 单个元素的队列
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>(false);

        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName()+"\t"+"put A ...");
                synchronousQueue.put("A");
                System.out.println(Thread.currentThread().getName()+"\t"+"put B ...");
                synchronousQueue.put("B");
                System.out.println(Thread.currentThread().getName()+"\t"+"put C ...");
                synchronousQueue.put("C");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"thread_A").start();

        new Thread(()->{
            try {
                try { TimeUnit.MILLISECONDS.sleep(2000);}catch(InterruptedException e){e.printStackTrace();}
                System.out.println(Thread.currentThread().getName()+"\t"+"get A ...");
                synchronousQueue.take();
                try {TimeUnit.MILLISECONDS.sleep(2000);}catch(InterruptedException e){e.printStackTrace();}
                System.out.println(Thread.currentThread().getName()+"\t"+"get B ...");
                synchronousQueue.take();
                try {TimeUnit.MILLISECONDS.sleep(2000);}catch(InterruptedException e){e.printStackTrace();}
                System.out.println(Thread.currentThread().getName()+"\t"+"get C ...");
                synchronousQueue.take();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        },"thread_B").start();
    }

}

// 线程通信 synchronized ， lock
class Number{

    public int num = 0 ;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public synchronized void incre() throws InterruptedException {
        while (num != 0){
            this.wait();
        }
        num ++ ;
        // 虚假唤醒
        notifyAll();
    }

    public synchronized void desc() throws InterruptedException {
        while (num == 0 ){
            this.wait();
        }
        num -- ;
        notifyAll();
    }

    public void incre2(){
        lock.lock();
        try {
            while (num != 0 ){
                condition.await();
            }
            num ++ ;
            System.out.println(num);
            condition.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void desc2(){
        lock.lock();
        try {
            while (num == 0 ){
                condition.await();
            }
            num -- ;
            System.out.println(num);
            condition.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}

// 线程通信（生产者消费者模型） 使用阻塞队列
class MsgByQueue{

    // 原子类
    private AtomicInteger atomicInteger = new AtomicInteger();
    // 标志位 需要是线程可见的
    private volatile boolean FLAG = true ;

    private BlockingQueue<String> blockQueue = null ;

    // 构造方法 转递接口
    public MsgByQueue(BlockingQueue<String> blockQueue) {
        this.blockQueue = blockQueue ;
    }

    // 生产者
    public void product()throws Exception{
        String data = null;
        boolean res ;
        // 当标志位为true时 进行生产
        while (FLAG){
            data = atomicInteger.incrementAndGet()+"";
            // 生产 等待时长为2s
            res = blockQueue.offer(data, 2L, TimeUnit.SECONDS);
            if(res){
                System.out.println(Thread.currentThread().getName()+"\t"+"生产成功:"+data);
            }else {
                System.out.println(Thread.currentThread().getName()+"\t"+"生产失败:"+data);
            }
            try {TimeUnit.SECONDS.sleep(1);}catch(InterruptedException e){e.printStackTrace();}
        }
        System.out.println("\t生产结束...");
    }

    // 消费者
    public void consumer()throws Exception{
        String res = null ;
        while (FLAG){
            // 消费
            res = blockQueue.poll(2L, TimeUnit.SECONDS);
            // 判断 poll 没有取到元素时会返回null
            if(res == null || res.equalsIgnoreCase("")){
                FLAG = false ;
                System.out.println(Thread.currentThread().getName()+"\t"+"队列中没有元素，消费失败");
                return ;
            }
            System.out.println(Thread.currentThread().getName()+"\t"+"消费成功:"+res);
        }
    }

    // 主动停止
    public void stop(){
        FLAG = false ;
    }

}