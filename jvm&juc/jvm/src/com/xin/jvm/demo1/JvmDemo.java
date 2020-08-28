package com.xin.jvm.demo1;

/**
 * jvm：运行在操作系统上，它与硬件没有直接的交互关系
 * sun.misc.Launcher : jvm操作的入口
 * 基本类型传值 引用类型传地址
 *
 * class loader 类加载过程
 *      类的加载由类加载器完成，加载指的是从系统硬盘中将类的class文件读入到内存，并为之创建一个Class
 *          （模板）对象。在进行声明（new）时，通过模板实例化一个具体对象（this）
 *
 * 主要的类加载器
 *      Bootstrap ClassLoader : 引导类加载器。不是由Java语言编写，
 *          不继承自java.lang.ClassLoader。负责加载核心Java库如Object/String... 打印信息是null
 *      Extension ClassLoader : 扩展类加载器，在<JAVA_HOME>/jre/lib/ext，用来加载Java发展中所使用的新的技术
 *          如lambda...
 *      Application ClassLoader : 应用层序加载器,用来加载用户只定义的类，如User...
 *      除了Bootstrap ClassLoader , 每一个类装载器都有一个父装载器
 *
 * 双亲委派机制
 *      当一个类收到类加载请求时，不会尝试自己去加载这个类，而是把请求委派给父类完成(ClassLoader.getParent())，每一个
 *          层次的类加载器都是如此，一直会找到最顶层的类装载器Bootstrap ClassLoader，只有当父类加载器反馈无法加载该类
 *          时(类路径下没有需要加载的Class)，子类的加载器才会自己去加载。
 *      所以当自己建立一个java.lang.String类，并且尝试去加载时会报异常：
 *         public static void main(String[] args)
 *         否则 JavaFX 应用程序类必须扩展javafx.application.Application
 *
 * native 本地方法区和本地方法栈
 *       可以在类中添加方法声明，添加native关键字，native修饰的方法回去调用底层操作系统的方法库（调用C）
 *       线程是操作系统级的，和语言无关。
 *       Thread.start --> 调用的是start0方法 --> private native void start0(); --> 调用C语言函数库来执行。
 *
 * 方法区 method Area
 *      提供线程运行时的内存区域，线程共享且存在垃圾回收机制
 *      方法区是一个规范，有不同的实现(永久代 / 元空间)
 *      实例变量(static)存储在堆内存中 和方法去无关
 *
 * Java栈
 *      栈主要负责管理运行 ，堆负责存储
 *      函数的栈内存会分配 : 8中基本类型 + 引用类型 + 实例变量
 *      栈存储元素 :
 *          本地变量 -- 输入参数和输出参数以及方法内的变量
 *          栈操作 -- 记录出栈 / 入栈 的操作
 *          栈帧数据 -- 包括类文件 / 方法等等  栈帧 -- 方法
 *      原理 : 先进后出 main是栈底
 *      栈溢出错误 : Exception in thread "main" java.lang.StackOverflowError
 *      栈 / 堆 / 方法区的关系
 *         HotSpot 是使用指针的方式来访问对象：Java 堆中会存放访问类元数据的地址，reference 存储的就直接是对象的地址
 *
 * Java堆 heap
 *       a-栈    A-堆
 *      A a = new A();
 * 1. 一个jvm实例只存在一个堆内容，大小可以调节。堆内存在逻辑上分为三部分：
 *      young / new ： 新生区 新生区分为Eden(伊甸区) 幸存者0区和幸存者1区
 *      old / tenure ：养老去
 *      Perm   ： 永久区 Java8之后为元空间
 *      物理上只有新生区和养老区，内存比例为1：2 , 新生区中Eden:S0:S1 = 8:1:1
 * 2. 新生区： 新生区是类的诞生，成长，消亡的区域，类在这里产生，应用，最后被垃圾回收器收集，结束生命。
 *      当伊甸园的空间用完时，程序又需要创建对象，JVM 的垃圾回收器将对伊甸园区进行垃圾回收(MinorGC)，将伊甸
 *      园区中的不再被其他对象所引用的对象进行销毁。然后将伊甸园中的剩余对象移动到幸存0区。若幸存0区
 *      也满了，再对该区进行垃圾回收，然后移动到1区。那如果1 区也满了就再移动到养老区。
 * 3. 养老区：当对象在新生区经历过多次（默认 15 次）GC 依然幸存则进入养老区。
 *      若养老区也满了，那么这个时候将产生 MajorGC（FullGC），进行养老区的内存清理。若养老区执行了 FullGC 之后发现
 *      依然无法进行对象的保存，就会产生 OOM 异常“OutOfMemoryError”。
 * 4.java.lang.OutOfMemoryError: Java heap space
 *      1. 虚拟机堆内存设置不够，可以通过参数-Xms,-Xmx来调整，在实际中两者需要调为相当，避免gc和应用程序争抢内存。
 *      2. 代码中创建了大量类并且无法回收
 * 复制 -- 清空  -- 互换
 * method area jvm规范 -- 元空间(使用本机物理内存)是方法区的实现量对象并且长时间不能被垃圾回收
 * idea配置EG：-Xms1024m -Xmx1024m -XX:+PrintGCDetails
 *
 */
public class JvmDemo {

    public static void main(String[] args) {
        // ctrl + alt + 方向键  回到上次操作的位置
        // System.out.println(Runtime.getRuntime().availableProcessors());
        long maxMemory=Runtime.getRuntime().maxMemory();//返回 Java 虚拟机试图使用的最大内存量。
        long totalMemory=Runtime.getRuntime().totalMemory();//返回 Java 虚拟机中的内存总量。
        System.out.println("MAX_MEMORY="+maxMemory+"（字节）、"+(maxMemory/(double)1024/1024)+"MB");
        System.out.println("TOTAL_MEMORY="+totalMemory+"（字节）、 "+(totalMemory/(double)1024/1024)+"MB");

        String str = "xin";
//        while(true){
//            str += str + new Random().nextInt(8888888);
//        }
}


    public static void loaderName(){
        Object o = new Object(); // boot strap 没有parent
        o = new JvmDemo();
        System.out.println(o.getClass().getClassLoader());
        System.out.println(o.getClass().getClassLoader().getParent());
        System.out.println(o.getClass().getClassLoader().getParent().getParent());
    }

}

