package com.xin.jvm.bookDemo;

/**
 * 死锁
 */
public class DeathLock implements Runnable{

    private String a ;
    private String b ;

    public DeathLock(String a, String b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        synchronized (a){
            System.out.println(Thread.currentThread().getName()+" 持有锁:"+a+" ，并尝试获取锁"+b);
            synchronized (b){
                System.out.println(Thread.currentThread().getName()+" 持有锁:"+b+" ，并尝试获取锁"+a);
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new DeathLock("lockA","lockB"),"ThreadA").start();
        new Thread(new DeathLock("lockB","lockA"),"ThreadB").start();
    }
}
