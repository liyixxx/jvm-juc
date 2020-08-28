package com.xin.juc.demo2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class MyCache{
    private volatile Map<String,Object> map = new HashMap<>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void put(String key , Object value){
        readWriteLock.writeLock().lock();
        try{
            System.out.println("开始写入...");
            map.put(key,value);
            System.out.println("写入完成..." + key);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            readWriteLock.writeLock().unlock();
        }
    }

    public void get(String key){
        readWriteLock.readLock().lock();
        try{
            System.out.println("开始读取...");
            Object value = map.get(key);
            System.out.println("读取完成..." + value);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            readWriteLock.readLock().unlock();
        }
    }
}

/**
 * @author 柒
 * @date 2020-03-12 11:47
 * @Description: 读写锁
 *
 * 保证数据的原子性，读写/写读/写写 需要添加锁
 *  如果只在读/写的一方加锁 依旧会发生数据异常
 * 提高并发读取效率，读读 不需要添加锁
 */
public class ReadWriteLockDemo {

    public static void main(String[] args) {
        MyCache myCache = new MyCache();

        for (int i = 0; i < 5; i++) {
            final int v = i ;
            new Thread(()->{
                myCache.put(Thread.currentThread().getName(),v);
            },String.valueOf(i)).start();
        }

        for (int i = 0; i < 5; i++) {
            final int v = i ;
            new Thread(()->{
                myCache.get(Thread.currentThread().getName());
            },String.valueOf(i)).start();
        }
    }

}
