package com.xin.nio;


import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 */
public class BlockingIO {

    @Test
    public void client() throws IOException{
        // 1. 获取网络通道
        SocketChannel socket = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9300));

        // 2. 获取本地通道
        FileChannel channel = FileChannel.open(Paths.get("a.jpg"));

        // 3. 建立缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 4. 进行读写
        while (channel.read(buffer) != -1){
            buffer.flip();
            socket.write(buffer);
            buffer.clear();
        }

        socket.shutdownOutput();

        // 5. 获取服务端响应信息
        int len ;
        while ((len = socket.read(buffer)) != -1){
            buffer.flip();
            System.out.println(new String(buffer.array(),0,len));
            buffer.clear();
        }
        // 6. 关闭通道连接
        channel.close();
        socket.close();
    }

    @Test
    public void server() throws IOException{
        // 1. 获取通道
        ServerSocketChannel server = ServerSocketChannel.open();
        FileChannel channel = FileChannel.open(Paths.get("b.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        // 2. 绑定链接
        server.bind(new InetSocketAddress(9300));
        System.out.println("等待客户端连接...");
        // 3. 获取客户端链接通道
        SocketChannel socket = server.accept();
        String s = socket.getRemoteAddress().toString();
        System.out.println("客户端链接成功... " + s);
        // 4. 分配缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 5. 读写
        while (socket.read(buffer) != -1){
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
        }
        // 6. 响应给客户端
        buffer.put("服务端接收数据成功...".getBytes());
        buffer.flip();
        socket.write(buffer);

        // 7. 关闭通道连接
        socket.close();
        channel.close();
        server.close();
    }
}
