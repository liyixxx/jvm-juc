package com.xin.jvm.bookDemo;

import java.util.concurrent.TimeUnit;

/**
 * @author 柒
 * @date 2020-03-25 9:00
 * @Description: JMM内存模型和线程
 *
 * 内存模型 -- 在特定的操作协议下，对特定的内存或者是cache进行读写访问的过程抽象
 * JMM : Java线程 -- 工作内存  --  save / load  -- 主存
 * 解释: 1. 每个Java线程都有自己的工作内存（线程私有），工作内存保存了线程使用的变量（主存的内存副本）
 *       2. 线程对变量的操作在工作内存进行，不能直接读写主存变量，也不能读取其他线程变量
 *
 * 内存间交互
 *  主存与工作内存的交互 将变量从主存拷贝到工作内存，从工作内存同步写入到主存
 *      lock unlock read load use assign stor write   volatile
 *  volatile
 *      volatile只保证了可见性（其他线程可以观测到该线程的修改） 大多数情况需要结合锁来使用
 *      volatile避免了指令重排
 * 		volatilem没有保证原子性
 * JMM特性
 *      1.原子性 基本数据类型的访问读写是具备原子性的(除了long和double)
 *      2.可见性 一个线程修改了共享变量的值 其他线程可以观测到
 *      3.有序性 在本线程内观察 所有操作都是有序的，观察其他线程时 则是无序的
 *               线程内表现为串行  --   指令重排/主存与工作内存同步延迟 现象
 * 并行发生原则 happens-before
 *      时间先后顺序与先行发生原则之间没有太大的关系，衡量并发安全时必须以先行发生原则为准
 *
 */
class JTest{
    volatile int number = 10 ;

    public void add(){
        number = 100 ;
    }
}

public class JMM_Thread {

    public static void main(String[] args) {
        JTest jTest = new JTest();
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(2);
                jTest.add();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        while (jTest.number == 10){

        }
        System.out.println(Thread.currentThread().getName() + "\t" + jTest.number);
    }
}


