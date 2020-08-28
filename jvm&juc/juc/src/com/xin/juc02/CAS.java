package com.xin.juc02;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author 柒
 * @date 2020-03-27 10:08
 * @Description: CAS
 * Atomic* 保证了原子性
 *
 * CAS:compareAndSet(expect,update)  比较并交换
 *      如果获取的期望值和主内存中的一致（expect），那么就可以进行修改，写回到主内存
 *      如果获取的期望值和主内存不一致，那么就要将自己工作线程的值和主内存的值进行同步
 *
 * CAS核心类Unsafe(sun.misc)在rt.jar中，Java自带的一个底层类
 *  unsafe类所有方法都是native修饰的，直接调用操作系统的底层资源来处理任务
 *  AtomicInteger.
 *      public final int getAndIncrement() {
 *           return unsafe.getAndAddInt(this, valueOffset, 1);
 *      }
 *      this 当前对象 AtomicInteger
 *      valueOffset:地址偏移量 值的内存地址
 *      1  ， 添加量 每次进行加1
 *
 * Unsafe:
 *  public final int getAndAddInt(Object var1, long var2, int var4) {
        int var5;
        do {
            var5 = this.getIntVolatile(var1, var2);
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
        return var5;
     }
 *  先获取在修改 ， var5 从当前对象的内存地址获取数据，这样保证了获取的数据的唯一性
 *      在每次获取前判断自己获取的值和主内存的值是否相同，如果不同，那么将自己工作内存的值和主内存保持一致
 *      如果相同，就进行修改
 *
 *  没有通过同步来保证数据一致性，更适合高并发，但是如果一个线程长时间获取的值都是无效的，那么就会一直处于
 *  循环中，对CPU造成巨大压力，并且同时只能保证一个原子操作，多个共享变量的修改人需要加锁
 *
 *  CAS会引发ABA问题：
 *  主内存共享资源A，有两个线程得到副本，线程1将A改为B后，又将B改回A写回到主内存，那么线程2由于CAS的判断（只是比较值
 *      是否相等，不考虑是否有中间操作）那么依旧可以获取到A来进行修改，引发ABA问题。
 *  解决：
 *  原子引用：juc提供的原子类除了基本类型以外，还提供了一种原子引用AtomicReference<T>，将内容改为原子类，具备原子性。
 *      同时提供了一种带有时间戳的原子引用用AtomicStampedReference来解决CAS的ABA问题。
 *  使用版本号/时间错来管理，线程1得到共享变量A时，此时A的版本为1，如果将其改为B，版本号+1，再改为A，这样版本号就变成了3
 *      适用版本号来记录共享变量的改变，同时线程2也会拿到共享变量初始，版本为1，对共享变量A操作，还需要判断版本号，由于线程1的版本号和线程2
 *      版本号不同，所以会修改失败。
 *
 *  故障现象 -- 导致原因  -- 解决方案 -- 优化建议
 */
public class CAS {

    private static AtomicReference<Integer> atomicReference ;
    private static AtomicStampedReference<Integer> atomicStampedReference ;

    public static void main(String[] args) {
        // tesCAS();
        // CAS_ABA();
        CAS_StampReference();
    }

    // 使用AtomicStampedReference解决CAS的ABA问题
    private static void CAS_StampReference() {
        // 和普通原子引用不同 在初始时还需要指定版本号
        atomicStampedReference = new AtomicStampedReference<>(10,1);

        new Thread(()->{
            // 获取版本号
            int stamp = atomicStampedReference.getStamp();
            // 等待其他线程获取到版本号
            try{TimeUnit.SECONDS.sleep(1); }catch(InterruptedException e){ e.printStackTrace(); }
            // 期待值 更新值  期待版本号  更新版本号
            atomicStampedReference.compareAndSet(10,20,atomicStampedReference.getStamp(),atomicStampedReference.getStamp()+1);
            atomicStampedReference.compareAndSet(20,10,atomicStampedReference.getStamp(),atomicStampedReference.getStamp()+1);
            System.out.println(Thread.currentThread().getName()+"最后版本号:" + atomicStampedReference.getStamp()+" 修改后结果:" +atomicStampedReference.getReference());
        },"thread_A").start();

        new Thread(()->{
            int stamp = atomicStampedReference.getStamp();
            try{TimeUnit.SECONDS.sleep(3); }catch(InterruptedException e){ e.printStackTrace(); }
            boolean flag = atomicStampedReference.compareAndSet(10, 2020, 1, 2);
            System.out.println("是否修改成功:" + flag);
            System.out.println(Thread.currentThread().getName()+"最后版本号:" + atomicStampedReference.getStamp()+" 修改后结果:" +atomicStampedReference.getReference());
        },"thread_B").start();
    }

    // CAS产生ABA问题
    private static void CAS_ABA() {
        atomicReference = new AtomicReference<>(10);
        new Thread(()->{
            // 产生ABA
            atomicReference.compareAndSet(10,20);
            atomicReference.compareAndSet(20,10);
        },"thread_A").start();

        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(1); }catch(InterruptedException e){ e.printStackTrace(); }
            // 期望值=10 线程A产生了ABA 由于CAS之观测结果 所以依旧可以获取到 并能执行
            boolean flag = atomicReference.compareAndSet(10, 11);
            System.out.println(Thread.currentThread().getName()+" 修改是否成功:"+flag);
            System.out.println("update后 值为 : " + atomicReference.get());
        },"thread_B").start();
    }

    // 测试CAS
    private static void tesCAS() {
        // 原始值
        AtomicInteger atomicInteger = new AtomicInteger(2);

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                atomicInteger.getAndIncrement();
            },String.valueOf(i)).start();
        }

        // 期望值=0 主内存初始值=0 一致 修改后的值（update）=10
        System.out.println(atomicInteger.compareAndSet(7, 10) + " atomic.get = " + atomicInteger.get());

        // 期望值=0 但是已经修改 主内存实际值=10 此时期望值就和主内存不一致了 需要将值和主内存进行同步
        System.out.println(atomicInteger.compareAndSet(2, 5) + " atomic.get = " + atomicInteger.get());
    }

}



