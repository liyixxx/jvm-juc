package com.xin.jvm.jvm02;

/**
 * jvm 参数调优/监控
 *  1. 参数三大类型：
 *       标配参数：java -version java -help ...
 *       X参数: java -Xint -version ...
 *       XX参数: -XX PrintGCDetails ...
 *  2. 常用命令
 *      1. jps  -l 查看Java进程
 *          注意：需要C:\Users\xin\AppData\Local\Temp\hsperfdata_xin 赋予操作权限，不然无法执行jps命令
 *      2. jinfo -flag [option] pid  查看某个Java进程是否开启了某个option
 *          eg: jinfo -flag PringGCDetails 18644 ：查看18644Java进程是否开启了PringGCDetails
 *              jinfo -flag MetaspaceSize xxx : 查看元空间的内存大小
 *              jinfo -flags pid: 查看全部默认
 *      3. java -XX:+PrintFlagsInitial  : 查看初始状态
 *         java -XX:+PrintFlagsFinal -version : 查看修改之后的状态
 *              =   没有被用户或者是jvm修改过
 *              :=  该选项被用户或者是jvm修改过
 *         java -XX:+PrintCommandLineFlags -version : 用来查看当前使用的GC垃圾回收器
 *  3. XX参数
 *      1. boolean类型 -XX: +/- xxx  +表示开启 -表示关闭
 *          eg: -XX: +PringGCDetails(打印GC收集细节) -XX: +UseSerialGC (使用串行垃圾收集器)
 *      2. KV设值类型 -XX: key=value
 *          eg: -XX: MetaspaceSize=128m : 设置元空间内存大小
 *  4. 常用参数
 *      -Xms: 初始大小内存 默认为系统内存的1/64
 *      -Xmx: 最大内存 默认为系统内存的1/4        -Xmx和-Xms 需要配置相同
 *      -XX:+PrintGCDetails : 打印GC日志  (**GC垃圾回收日志的各个字段的含义**)
 *      -Xss: 设置单个线程的大小 默认为512k~1024k
 *      -Xmn: 设置新生代大小 一般使用默认值
 *      -XX:MetaspaceSize : 设置原空间大小，默认为21810376字节
 *      -XX:MaxTenuringThreshold=15 : 设置垃圾的最大年龄 超过则进入老年代
 *      (了解) -XX:SuvivoRatio=8 设置新生代中eden:s0:s1 的比例 默认为8:1:1
 *             -XX:NewRatio=2 设置新生代：老年代的比例 默认为1：2
 */
public class TuningJvm {

    public static void main(String[] args) throws Exception {

        System.out.println("hello world");
        //C:\Program Files\Java\jdk1.8.0_221\jre\bin
        System.out.println(System.getProperties());

        /**
        -XX:+PrintCommandLineFlags打印默认配置：
            -XX:InitialHeapSize=265650752 初始化堆空间
            -XX:MaxHeapSize=4250412032    最大堆空间
            -XX:+PrintCommandLineFlags    打印参数信息
            -XX:+UseCompressedClassPointers
            -XX:+UseCompressedOops
            -XX:-UseLargePagesIndividualAllocation
            -XX:+UseParallelGC    使用并行垃圾回收器

         修改参数： -Xms256m -Xmx256m -Xss1024k -XX:MetaspaceSize=128m -XX:+PrintCommandLineFlags -XX:+UseSerialGC :
             -XX:InitialHeapSize=268435456
             -XX:MaxHeapSize=268435456
             -XX:MetaspaceSize=134217728  ：元空间(jvm默认的元空间大小为21810376字节)
             -XX:+PrintCommandLineFlags
             -XX:ThreadStackSize=1024    : 栈线程空间(512k ~ 1024k 为0时采用的是系统的默认值)
             -XX:+UseCompressedClassPointers
             -XX:+UseCompressedOops
             -XX:-UseLargePagesIndividualAllocation
             -XX:+UseSerialGC  : 串行垃圾回收器
         */


    }
}
