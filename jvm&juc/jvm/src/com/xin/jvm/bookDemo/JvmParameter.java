package com.xin.jvm.bookDemo;

/**
 * @author 柒
 * @date 2020-03-24 21:07
 * @Description: jvm参数调优
 *
 *  -Xverify:none   -- 禁止字节码验证
 *  -Xms1000m       -- 初始heap内存 1000m
 *  -Xmn1000m       -- 最大heap内存 1000m
 *  -XX:PermSize=100m   -- 永久代内存 100m
 *  -XX:MaxPermSize=100m    -- 最大永久代内存 100m
 *
 * -XX:+HeapDumpOnOutOfMemoryError      -- 查看堆内存error
 * -XX:+PrintGCDetails      -- 查看GC日志
 *  ....
 *
 *  确定对象是否已经死亡：
 *      1. 引用记数算法
 *      2. 可达性分析算法
 *  内存分配策略
 *      1. 对象优先分配在Eden
 *      2. 大对象（如 byte[]）直接分配到老年代
 *      3. 长期存活的对象分配到老年代
 *  GC收集算法
 *      1. 复制 -- From / To
 *      2. 标记-清除算法
 *      3. 标记-整理算法
 *  GC垃圾收集器
 *      1. Serial / Serial old      -- 单线程垃圾收集器 需要展厅工作线程
 *      2. ParNew                   -- Serial的多线程版本
 *      3. Paralled / Paralled old  -- 关注吞吐量
 *      4. CMS                      -- 并发处理
 *      5. G1
 *
 *
 */
public class JvmParameter {}
