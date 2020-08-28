package com.xin.juc.demo;

import java.util.concurrent.TimeUnit;

class phone{
    public static synchronized void sendEmial() throws InterruptedException {
        TimeUnit.SECONDS.sleep(4);
        System.out.println("---sendMail---");
    }
    public static synchronized void sendSms() throws InterruptedException {
        System.out.println("---sendSms---");
    }

    public void sayHello(){
        System.out.println("--- say hello ---");
    }
}

/**
 * @author 柒
 * @date 2020-03-11 12:17
 * @Description: 线程锁
 *
 * 1. 一个对象如果有多个synchronized方法，那么当一个线程调用方法时，其他的线程只能等待
 *      锁的是当前对象this而不是synchronized方法
 * 2. 普通方法不会影响锁
 * 3. 多个对象不是同一把锁，锁的是各自对象this
 *
 * 4. synchronized实现同步的基础：Java中的每一个对象都可以作为锁
 *      对于普通的同步方法 锁的是当前的实例对象 this
 *      对于同步方法快 锁的是synchronized括号内配置的对象
 *      对于静态同步方法(static) 锁的是当前类的Class对象
 *      this -- 对象锁 -- new     class -- 全局锁 -- 模板
 *
 * 5.所有的静态同步方法用的是同一把锁 -- Class
 *      静态同步方法与非静态同步方法之间不是使用的同一把锁，两者不会竞争资源
 *      但是当静态同步方法获取锁之后，其他的静态同步方法必须等待该方法释放后才能获取资源
 */
public class LockDemo {

    public static void main(String[] args) throws InterruptedException {
        phone phone = new phone();
        com.xin.juc.demo.phone phone2 = new phone();
        new Thread(()->{
            try {
                phone.sendEmial();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        TimeUnit.MILLISECONDS.sleep(100);
        new Thread(()->{
            try {
//                 phone.sendSms();
                phone.sayHello();
//                 phone2.sendSms();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
