package com.xin.juc.demo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @author 柒
 * @date 2020-03-11 11:30
 * @Description: list
 *
 * 1. exception: java.util.ConcurrentModificationException  并发修改异常
 * 2. 原因 : 多线程争抢同一资源进行同时读写
 * 3. 解决 (3种)
 *
 * CopyOnWriteArrayList<>() list线程不安全的解决代替
 * CopyOnWriteCopyOnWriteArraySet set
 * ConConcurrentHashMap<>() map
 *
 * 4. 优化
 */
public class TestList {
    public static void main(String[] args) {
        // clt + 7 ：查看方法
        listNotSafe();
        // setNotSafe();
        // mapNotSafe();

    }

    private static void mapNotSafe() {
        // hashMap结构：数组 + 链表 + 红黑树(链表长度>8 且整个长度>64)
//        Map<String,String> map = new HashMap<>();
        Map<String,String> map = new ConcurrentHashMap<>();

        for (int i = 1; i <= 100; i++) {
            new Thread(() -> {
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0, 5));
                System.out.println(map);
            }, String.valueOf(i)).start();
        }
    }

    private static void setNotSafe() {
        // set 底层为hashmap set.add --> map.put(T,final object(new object))
        // set.add 时 只管key = T ，value=静态常量
//        Set<String> set = new HashSet<>();
        Set<String> set = new CopyOnWriteArraySet<>();
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                set.add(UUID.randomUUID().toString().substring(0, 5));
                System.out.println(set);
            }, String.valueOf(i)).start();
        }
    }

    private static void listNotSafe() {
        // arraylist object[10] 默认长度为10 大于75%扩容 >>2 位运算
        // hashmap 默认长度16 扩容一倍
        // 线程休眠
        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace();}
        /**
         * arrayList map set 是线程不安全 实例:
         * java.util.ConcurrentModificationException  并发修改异常
         *
         * 1. new ArrayList<>();
         * 2. Collections.synchronizedList(new ArrayList<>());
         * 3. new CopyOnWriteArrayList<>();  Java8 新api
         *
         * CopyOnWriteArrayList
         */
        List<String> list = new ArrayList<>();
//        List<String> list = new Vector<>();
//        List<String> list = Collections.synchronizedList(new ArrayList<>());
//        List<String> list = new CopyOnWriteArrayList<>();
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0, 5));
                System.out.println(list);
            }, String.valueOf(i)).start();
        }
    }

}
