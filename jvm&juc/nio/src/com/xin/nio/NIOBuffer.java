package com.xin.nio;

import sun.misc.VM;

import java.nio.ByteBuffer;

/**
 * NIO : non blockinng io
 * 通道负责链接  缓冲区负责存储
 *
 * 1. 缓冲区 : 存储数据，数组形式，存储不同的数据类型，提供了对应类型的缓冲区
 *      ByteBuffer CharBuffer IntBuffer ....
 *      管理方式几乎一致，通过allocate()获取缓冲区
 *
 * 2. buffer的常用属性
 *      capacity : 容量，缓冲区最大存储容量
 *      limit : 缓冲区可以操作数据的大小
 *      position : 位置，缓冲区正在操作的位置
 *      mark : 标记，记录当前position的位置，通过reset回复到mark位置
 *
 * 3. 缓冲区常用API
 *      put : 存数据
 *      flip : 切换到读模式
 *      get : 读取数据
 *      rewind : 重复读
 *      clear : 清空缓冲区，但不会删除里面的数据
 *      mark : 标记当前position位置
 *      reset : 回到mark标记的position位置
 *
 */
public class NIOBuffer {

    public static void main(String[] args) throws InterruptedException {
        // bufferApi();

        directBuffer();

    }

    // 直接缓冲区 -- 调用操作系统层
    private static void directBuffer() throws InterruptedException {
        System.out.println("配置的最大物理内存(MaxDirectMemory)为："+ VM.maxDirectMemory()/1024/1024 + "MB");
        Thread.sleep(300);
        // -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:MaxDirectMemorySize=5m
        // 报的OOM错误不同
        // ByteBuffer buffer = ByteBuffer.allocateDirect(7 * 1024 * 1024); java.lang.OutOfMemoryError: Direct buffer memory
        // ByteBuffer buffer = ByteBuffer.allocate(20 * 1024 * 1024); java.lang.OutOfMemoryError: Java heap space
        ByteBuffer allocate = ByteBuffer.allocate(5 * 1024);
        ByteBuffer buffer = ByteBuffer.allocateDirect(5 * 1024);

    }

    // buffer 的常用API和属性
    private static void bufferApi() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String data = "abcde" ;

        /**
         * buffer 的几个属性
         * Invariants: mark <= position <= limit <= capacity
         *     private int mark = -1;       标记，记录当前position的位置，通过reset回复到mark位置
         *     private int position = 0;    位置，缓冲区正在操作的位置
         *     private int limit;       缓冲区可以操作数据的大小
         *     private int capacity;    容量，缓冲区最大存储容量
         */

        System.out.println("init(pos/limit/cap) : " + buffer.position() + "\t" + buffer.limit() + "\t" + buffer.capacity());

        // 1.put() : 存放数据，position的位置发生改变
        buffer.put(data.getBytes());
        System.out.println("put(pos/limit/cap) : " + buffer.position() + "\t" + buffer.limit() + "\t" + buffer.capacity());

        // 2.flip() : 切换到读取模式 : position = 0 , limit = 存放数据是position的位置 pos-limit为可以读取的数据内容
        buffer.flip();
        System.out.println("flip(pos/limit/cap) : " + buffer.position() + "\t" + buffer.limit() + "\t" + buffer.capacity());

        // 3.get() : 读取数据，position移动
        byte[] dst=new byte[buffer.limit()];
        buffer.get(dst); // 读取byte[]数据
        System.out.println(new String(dst,0,dst.length));//abcd
        System.out.println("get(pos/limit/cap) : " + buffer.position() + "\t" + buffer.limit() + "\t" + buffer.capacity());


        // 4.rewind() : 重复读 修改position为0
        buffer.rewind();
        System.out.println("rewind(pos/limit/cap) : " + buffer.position() + "\t" + buffer.limit() + "\t" + buffer.capacity());

        // 5.clear() : 情空缓冲区，但不会清除里面的数据
        buffer.clear();
        System.out.println("clear(pos/limit/cap) : " + buffer.position() + "\t" + buffer.limit() + "\t" + buffer.capacity());
        System.out.println((char) buffer.get());

        // 6.mark() : 标记
        buffer.mark();
        System.out.println("mark_pre(pos/limit/cap) : " + buffer.position() + "\t" + buffer.limit() + "\t" + buffer.capacity());
        buffer.get(new byte[buffer.limit()],2,2);
        System.out.println("mark_pos(pos/limit/cap) : " + buffer.position() + "\t" + buffer.limit() + "\t" + buffer.capacity());

        // 7.reset() : 回到mark标记的位置
        buffer.reset();
        System.out.println("reset(pos/limit/cap) : " + buffer.position() + "\t" + buffer.limit() + "\t" + buffer.capacity());
    }

}
