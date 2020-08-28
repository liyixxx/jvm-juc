package com.xin.juc.demo2;

import java.util.concurrent.*;

/**
 * @author 柒
 * @date 2020-03-12 13:33
 * @Description: 阻塞队列
 */
public class QueueDemo {

    public static void main(String[] args) throws InterruptedException {
        // 初始时需要指定边界值

        // 数组结构队列
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(3);
        // 链表结构的队列
        LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>(5);
        // 双端队列 边界值默认=Integer.MAX_VALUE
        LinkedBlockingDeque<Integer> linkedBlockingDeque = new LinkedBlockingDeque<>(10);
        waitMin(queue);

    }

    private static void waitMin(BlockingQueue<String> queue) throws InterruptedException {
        // offer - 等待特定时间
        // poll - 等待特定时间 没有返回null
        System.out.println(queue.offer("a", 3, TimeUnit.SECONDS));
        System.out.println(queue.offer("a", 3, TimeUnit.SECONDS));
        System.out.println(queue.offer("a", 3, TimeUnit.SECONDS));
        System.out.println(queue.poll(3, TimeUnit.SECONDS));
        System.out.println(queue.poll(3, TimeUnit.SECONDS));
        System.out.println(queue.poll(3, TimeUnit.SECONDS));
        System.out.println(queue.poll(3, TimeUnit.SECONDS));
    }

    private static void waitAlong(BlockingQueue<String> queue) throws InterruptedException {
        // put void 超过会一直等待
        // take 队列没有元素 会一直等待
        queue.put("a");
        queue.put("b");
        queue.put("c");
        System.out.println(queue.take());
        System.out.println(queue.take());
        System.out.println(queue.take());
        System.out.println(queue.take());
    }

    private static void spec(BlockingQueue<String> queue) {
        // offer 返回Boolean 超过队列容量时 返回false
        // pull 返回元素 队列没有元素时 返回null
        System.out.println(queue.offer("a"));
        System.out.println(queue.offer("a"));
        System.out.println(queue.offer("a"));
        System.out.println(queue.offer("a"));
        System.out.println(queue.poll());
        System.out.println(queue.poll());
        System.out.println(queue.poll());
        System.out.println(queue.poll());
    }

    private static void exceptionMethod(BlockingQueue<String> queue) {
        // add 当元素以达到边界值 在进行添加时 会报Queue full 异常
        // remove 当队列没有内容 进行读取 会报 NoSuchElementException 异常
        // java.lang.IllegalStateException: Queue full
        System.out.println(queue.add("a"));
        System.out.println(queue.add("b"));
        System.out.println(queue.add("c"));
        System.out.println(queue.remove());
        System.out.println(queue.remove());
        System.out.println(queue.remove());
        // 检查队首元素
        System.out.println(queue.element());
    }

}
