package com.xin.jvm.jvm02;

import java.lang.ref.*;
import java.util.WeakHashMap;

/**
 * GCROOT：可达性分析算法
 *  jvm判断对象是否为垃圾的方式：引用计数算法(不再使用) GCROOT(可达性分析)
 *
 *  1. 枚举根节点做可达性分析（根搜索路径）,通过一系列名为”GC Roots”的对象作为起始点，从这个被称为GC Roots的对象开始向下搜索，
 *     如果一个对象到SC Roots没有任何引用链相连时，则说明此对象不可用。也即给定一个集合的引用作为根出发，通过引用关系遍历对象
 *     图，能被遍历到的(可到达的)对象就被判定为存活;没有被遍历到的就自然被判定为垃圾，会被GC所回收。
 *  2. GC ROOT是一组对象的集合，满足条件的可以作为ROOT根节点
 *  3. 可以作为GC ROOT的对象
 *      1. 虚拟机栈（栈帧中的本地变量表）中引用的对象
 *      2. 方法区中类静态属性引用的对象
 *      3. 方法区中常量引用的对象
 *      4. 本地方法栈中JNI(native方法)引用的对象
 *
 * 四大引用（强引用 / 软引用 / 弱引用 / 虚引用）
 *      1. 强引用： 无论怎样都不会被GC回收
 *      2. 软引用： 当系统内存充足时不会被回收 不足时会回收
 *      3. 弱引用： GC发生就会被回收
 *      4. 虚引用： GC发生就会被回收 需要结合引用队列来使用 通常用来查看对象的生命周期
 *                 虚引用调用get方法时返回null
 */
public class ObjectRefer {

    public static void main(String[] args) throws InterruptedException {
        // 强引用
        // tesStrongRef();

        // 软引用
        // softRef();

        // 弱引用
        // weakRef();

        // weakHashMap();

        // refQueue();

        // 虚引用
        phantomRef();
    }

    // 虚引用
    private static void phantomRef() throws InterruptedException {
        // 虚引用的get方法返回null
        Object object = new Object();
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        PhantomReference<Object> phantomReference = new PhantomReference<>(object,referenceQueue);

        System.out.println(object + "\t" + referenceQueue.poll() + "\t" + phantomReference.get());

        object = null ;
        System.gc();
        Thread.sleep(500);

        System.out.println(object + "\t" + referenceQueue.poll() + "\t" + phantomReference.get());
    }

    // 引用队列
    private static void refQueue() throws InterruptedException {
        Object object = new Object() ;
        // 引用队列 发生GC时 引用会进入到引用队列中
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();

        WeakReference<Object> weakReference = new WeakReference<>(object,referenceQueue);

        System.out.println(object + "\t" + referenceQueue.poll() + "\t" + weakReference.get());

        object = null ;
        System.gc();
        Thread.sleep(500);

        System.out.println(object + "\t" + referenceQueue.poll() + "\t" + weakReference.get());
    }

    // weakHashMap 当weakHashMap的key为null时，发生GC时会被回收
    private static void weakHashMap() {
        WeakHashMap<Integer , String> weakHashMap = new WeakHashMap<>();
        Integer key = new Integer(1);
        String value = "weak";

        weakHashMap.put(key,value);
        System.out.println("\t" + weakHashMap);

        key = null ;
        System.out.println("\t" + weakHashMap);

        System.gc();
        System.out.println("\t" + weakHashMap);

    }

    // 弱引用
    private static void weakRef() {
        Object obj1 = new Object() ;
        WeakReference weakReference = new WeakReference(obj1) ;
        System.out.println(obj1 + "\t" + weakReference.get());
        obj1 = null ;
        System.gc();
        System.out.println(obj1 + "\t" + weakReference.get());
    }

    // 软引用
    private static void softRef() {
        Object obj1 = new Object() ;  // 强引用
        SoftReference softReference = new SoftReference(obj1); // 软引用
        System.out.println(obj1+"\t"+softReference.get());
        obj1 = null;
        // System.gc();
        try {
            // -Xmx5m -Xms5m -XX:+PrintGCDetails
            Byte[] bytes = new Byte[1024 * 1024 * 10];
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 内存不足时 软引用对象会被回收
            System.out.println(obj1+"\t"+softReference.get());
        }
        System.out.println(obj1+"\t"+softReference.get());

    }

    // 强引用
    private static void tesStrongRef() {
        Object obj1 = new Object(); // 强引用
        Object obj2 = obj1 ;
        obj1 = null ;
        System.gc();
        System.out.println(obj2); // 只要还有一个对象这个实例 就不会被回收
    }

}

// 可作为GCROOT的对象
class GCROOT{
    // 静态属性引用的对象
    private static ObjectRefer refer = new ObjectRefer() ;
    // 常量引用的对象
    private final ObjectRefer refer2 = new ObjectRefer();

    public static void main(String [] args){
        stackRef();
        // 4. Thread的start0方法
        new Thread(()->{}).start();
    }

    public static void stackRef(){
        // 1. 虚拟机栈引用的对象
        ObjectRefer refer = new ObjectRefer();
    }
}