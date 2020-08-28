package com.xin.jvm.jvm02;

import java.util.Random;

/**
 * GC垃圾回收器
 *  四大类：Serial(串行垃圾回收器) parallel(并行垃圾回收器) CMS(并发垃圾回收器) G1
 *  查看垃圾回收器： 指定参数 -XX:+PrintCommandLineFlags   或者直接使用java -XX:+PrintCommandLineFlags命令
 *
 *  7种收集器
 *  新生代：
 *  1. serial : 串行垃圾回收器，发生在新生代，使用-XX:+UseSerialGC来指定
 *
 *  2. parNew : 并行垃圾回收器 ，运行在新生代，使用多个线程来进行垃圾回收 使用-XX:+UseParNewGC
 *              常用于配合养老区的CMS工作
 *         parNew只负责young区，老年代默认还是serial 但是已经不推荐使用了， 所以会报警告：
 *              Java HotSpot(TM) 64-Bit Server VM warning: Using the ParNew young collector
 *              with the Serial old collector is deprecated and will likely be removed in a future release
 *  3. parallel : 并行收集器 作用于新生代和老年代，关注于程序的吞吐量 使用-XX:+UseParallelGC来进行配置修改
 *                使用parallel会自动激活parallel old
 *  老年代：
 *  4. parallel old : parallel old 的老年代版本，可以使用-XX:+UseParallelOldGC来指定，自动激活parallel
 *
 *  5. CMS : 并发收集器 使用在养老区 标记清楚算法的实现 使用-XX:+UseConcMarkSweepGC来指定 他会自动激活parNew作为新生代的收集器
 *           主要过程： 1. 初始标记 ： 第一次标记需要GC的对象，会暂停用户现场
 *                     2. 并发标记 ： 跟踪初始标记的对象 ： 并发执行
 *                     3. 重新标记 ： 重新进行标记 ： 按暂停用户现场
 *                     4. 并发清除 ： GC，并发执行
 *  6. serial old : 串行垃圾回收器回收老年代  很少使用  CMS的后备处理方案
 *
 *  G1垃圾回收器  使用-XX:+UseG1GC 来指定
 *      宏观上不在区分新生代和老年代，将内存划分为多个内存区域，每个区域为1M~32M(为2的幂次)，每个区域区分新生代和老年代
 *      GC收集过程和CMS类似，分为四步： 初始标记 -- 并发标记 -- 最终标记 -- 筛选回收
 *      对比CMS：
 *          1. 不会产生内存碎片
 *          2. 可以预测垃圾收集时间
 *
 *  垃圾收集器 结合spring boot应用使用 进行参数调优(重点)
 *      maven clean --> maven package --> 调优：
 *          1. 项目内：在idea中配置
 *          2. 项目外(核心) ： 使用 java -server jvm调优参数 -jar jar/war包
 *          eg： java -server -Xms512m -Xmx512m -Xss1024k -XX:+UseG1GC -jar springboot.war
 *
 */
public class GcGarbageCollection {

    public static void main(String[] args) {
        // -Xms10m -Xmx10m -XX:+PrintGCDetails
        /*
            UseSerialGC 自动激活SerialOldGC  很少使用
            UseParNewGC 新生代的并行GC
            UseParallelGC 新生代GC 会自动激活ParallelOldGC 关注于吞吐量 适合对停顿要求不高的系统
            UseConcMarkSweepGC 老年代GC 并发 自动激活ParNewGC 适合需要及时响应的系统
         */
        String str = "";
        while (true) {
            str += str + new Random().nextInt(777777);
            str.intern();
        }
    }

}