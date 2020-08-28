package com.xin.jvm.jvm02;

/**
 * linux命令和参数
 *  1. 使用javac 编译Java文件
 *      不加包名，只有入口程序： javac Demo.java  --> java Demo
 *      有包名： javac -d . Demo.java   .表示当前文件夹
 *              java -cp ./ com.xin.test.Demo 运行
 *    root 用户下配置了jdk ， 给普通用户赋予java操作权限:
 *    sudo chmod -R 755 java安装目录
 *    sudo chown -R username java安装目录
 *
 * 2. 查看整机性能 top / uptime
 *      top 查看所有性能参数
 *          按1 查看cpu消耗
 *          load average : 负载均衡 ，如果三个值之和/3 大于60% 表示负载均衡有压力
 *      uptime： 查看负载消耗
 *
 * 3. 查看CPU vmstat mpstat
 *      vmstat -n 2 3 ： 每隔2秒打印一次 一共打印三次
 *      查看所有cpu核信息 : mpstat -P ALL 2
 *      根据pid查看cpu信息 : pidstat -u | -p pid
 *      procs
 *          r : 运行和等待cpu的进程数
 *          b : 等待资源的线程数，比如正在等待磁盘IO等
 *      cpu
 *          us: 用户进程消耗cpu的百分比
 *          sy: 系统进程消耗cpu的百分比
 *          us + sy > 80% ，说明可能存在cpu不足
 *
 * 4. 查看内存 free
 *      free -m : 按mb查看
 *      pidstat -p pid -r 3 : 每间隔3s 查看pid进程的内存占用
 *
 * 5. 磁盘io iostat
 *      iostat -xdk 2 3
 *          util : 每秒有多少的时间用于IO操作，接近100%时表明磁盘带宽到极限
 *          await : IO等待时间
 *      pidstat -d 采样间隔秒数 -p 进程号
 *          pidstat -d 2 -p 5600
 *
 * 6. 网络io ifstat 2
 *
 * 7. cpu占用过高的问题查看方法 ： 结合lunux和java命令分析
 *      1. 使用top命令 定位cpu占比最高的应用
 *      2. 使用jps-l 或者 ps -ef | grep java 找到占用cpu最高的Java进程
 *      3. 定位到具体的线程和代码 : ps -mp 进程id -o THREAD,tid,time
 *          -m : 显示所有线程
 *          -p : pid进程使用的cpu时间
 *          -o : 用户自定义的显示格式
 *          THREAD tid time : 显示线程id 时间
 *      4. 将线程id转换为16进制的引文小写格式 : printf "%x\n"  线程id
 *      5. jstack 进程ID | grep tid(16进制线程ID小写英文) -A60
 *
 */
public class LinuxParam {

    public static void main(String[] args) {

    }
}
