package com.xin.jvm.jvm02;

import sun.misc.VM;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * oom错误
 *   1. java.lang.StackOverflowError : 栈溢出
 *   2. java.lang.OutOfMemoryError: Java heap space  : OOM-堆内存溢出
 *   3. java.lang.OutOfMemoryError: GC overhead limit exceeded : OOM-jvm频繁发生GC，但是回收效果很差
 *   4. java.lang.OutOfMemoryError: Direct buffer memory : OOM-物理内存溢出，当使用物理内存而不是堆内存时容易造成该错误(NIO)
 *   5. Java.lang.OutOfMemeoryError:unable to create new native thread : OOM-无法创建更多的线程了 高并发业务常见错误
 *   6. Java.lang.OutOfMemeoryError:Metaspace : OOM-元空间溢出
 */
public class OOM {

    static class T{}

    public static void main(String[] args) throws InterruptedException {
        // stackoverflow();
        // heapspaceoverflow();
        // gcoverhead();
        // directbuffer();
        // unablecreatethread();
        // metaspaceflow();
    }

    // 不要在windows下运行 会卡死...
    // Java.lang.OutOfMemeoryError:unable to create new native thread
    private static void unablecreatethread() {
        // linux下 非root用户默认能创建的最大线程数为1024 root用户没有上限
        for (int i = 0; ; i++) {
            System.out.println(" ----------------------- i = " + i);
            new Thread(()->{
                try {TimeUnit.SECONDS.sleep(Integer.MAX_VALUE); } catch (InterruptedException e) {e.printStackTrace(); }
            },String.valueOf(i)).start();
        }
    }

    // java.lang.OutOfMemoryError: Direct buffer memory
    private static void directbuffer() throws InterruptedException {
        // -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:MaxDirectMemorySize=5m
        System.out.println("配置的最大物理内存(MaxDirectMemory)为："+ VM.maxDirectMemory()/1024/1024 + "MB");
        Thread.sleep(300);
        // 使用物理内存
        ByteBuffer.allocateDirect(6 * 1024 * 1024);
    }

    // java.lang.OutOfMemoryError: GC overhead limit exceeded
    private static void gcoverhead() {
        // -Xms10m -Xmx10m -XX:+PrintGCDetails
        Integer i = 0 ;
        List<String> list = new ArrayList<>();
        try {
            while (true){
                String val = String.valueOf(++i).intern();
                list.add(val);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e ;
        }finally {
            System.out.println("---------------------------  i = " + i);
        }
    }

    // java.lang.OutOfMemoryError: Java heap space
    private static void heapspaceoverflow() {
        // -Xms10m -Xmx10m
        byte[] bytes = new byte[20 * 1024 * 1024];
    }

    // java.lang.StackOverflowError
    private static void stackoverflow() {
        stackoverflow();
    }

}
