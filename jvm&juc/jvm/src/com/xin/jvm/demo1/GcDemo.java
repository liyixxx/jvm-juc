package com.xin.jvm.demo1;

/**
 * main 进入后有两个线程： main gc  gc自动调用
 *
 * 1. GC ： 分代收集算法，特点 频繁的收集Young区，较少收集old区，基本不会收集元空间
 *    jvm在进行GC时，大部分回收的都是新生代，不是对三个内存进行一起回收。
 *        因此按照回收区域划分为两种类型的GC ：minor GC 和 major GC / Full GC
 *        普通jc只针对新生代区域，指发生在新生代的垃圾回收操作，minor GC非常频繁，回收速度很快
 *        全局jc只发生在老年代的垃圾回收操作，major GC经常伴随着至少一次的普通jc ,速度很慢
 *
 * 2. GC日志
 *   [GC (Allocation Failure) [PSYoungGen: 1749K->392K(2560K)] 7884K->7167K(9728K), 0.0012909 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 *   GC类型:YoungGC              GC前内存占用    GC后内存占用  JVM堆内存占用   JVM总堆内存      JC执行时间     用户耗时     系统耗时
 *   [Full GC (Ergonomics) [PSYoungGen: 392K->0K(2560K)] [ParOldGen: 6775K->3846K(7168K)] 7167K->3846K(9728K), [Metaspace: 3289K->3289K(1056768K)], 0.0103093 secs] [Times: user=0.05 sys=0.00, real=0.01 secs]
 *   GC类型: FullGC                                       GC前old内存占用  GC后占用
 *
 * 3.
 *
 */
public class GcDemo {

    public static void main(String[] args) {
//        System.gc();
        // 加载优先级 static 》 代码块 》 构造
        new Code();
    }
}

class Code{

    public Code(){
        System.out.println("fangfa gouzao 111 ");
    }
    {
        System.out.println("kuai gouzao 222 ");
    }
    static{
        System.out.println("static gouzao 333");
    }
}

