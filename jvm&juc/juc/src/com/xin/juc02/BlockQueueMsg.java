package com.xin.juc02;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockQueueMsg {
    // 线程判断标志位 线程可见 当flag为true时进行生产和消费 为false时 停止
    private volatile boolean FLAG = true ;

    // 资源 原子性
    private AtomicInteger atomicInteger = new AtomicInteger();

    // 阻塞队列 从构造传入具体实现
    private BlockingQueue<Object> blockingQueue = null ;

    public BlockQueueMsg(BlockingQueue<Object> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    // 生产者
    public void product() throws Exception{
        Integer data = null ;
        boolean result ;
        while (FLAG){
            // 当FLAG为true时 进行生产 生产完成之后等待1s 让消费者来取
            data = atomicInteger.incrementAndGet();
            // 生产 向队列插入元素
            result = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if(result){
                // 插入成功
                System.out.println(Thread.currentThread().getName()+"\t"+"生产成功:"+data);
            } else {
                System.out.println(Thread.currentThread().getName()+"\t"+"生产失败:"+data);
            }
            try {TimeUnit.SECONDS.sleep(1);}catch(InterruptedException e){e.printStackTrace();}
        }
        System.out.println("\t生产结束...");
    }

    // 消费者
    public void consumer() throws Exception{
        Object data = null;
        while (FLAG){
            // 消费
            data = blockingQueue.poll(2L, TimeUnit.SECONDS);
            if(data == null){
                // 队列已经没有元素了 不在进行生产和消费 将标志改为false
                FLAG = false ;
                System.out.println(Thread.currentThread().getName()+"\t"+"队列中没有元素，消费失败");
                return;
            } else {
                // 消费成功
                System.out.println(Thread.currentThread().getName()+"\t"+"消费成功:"+data);
            }
        }
    }

    // 关闭
    public void shutdown(){
        FLAG = false ;
    }

    public static void main(String[] args) {
        BlockQueueMsg blockQueueMsg = new BlockQueueMsg(new ArrayBlockingQueue<>(10));
        new Thread(()->{
            try {
                System.out.println("生产者线程启动 ... \n");
                blockQueueMsg.product();
            } catch (Exception e) {
                e.printStackTrace();
            }
        },"productor").start();
        new Thread(()->{
            try {
                System.out.println("消费者线程启动 ... \n");
                blockQueueMsg.consumer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        },"consumer").start();

        try {TimeUnit.SECONDS.sleep(10); } catch (InterruptedException e) {e.printStackTrace(); }
        blockQueueMsg.shutdown();
    }
}
