package com.xin.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * 非阻塞式
 */
public class NonBlockingIO {

    @Test
    public void client() throws IOException {
        // 1. 获取通道
        SocketChannel socket = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9300));
        // 2. 切换到非阻塞模式
        socket.configureBlocking(false);
        // 3. 分配缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 4. 发送数据到服务端
        String date = LocalDateTime.now().toString();
        buffer.put(date.getBytes());
        buffer.flip();
        socket.write(buffer);
        buffer.clear();
        // 5. 关闭通道
        socket.close();
    }

    @Test
    public void server() throws IOException {
        // 1. 获取通道
        ServerSocketChannel server = ServerSocketChannel.open();
        // 2. 切换到非阻塞模式
        server.configureBlocking(false);
        // 3. 绑定链接
        server.bind(new InetSocketAddress(9300));
        // 4. 获取选择器
        Selector selector = Selector.open();
        // 5. 注册通道到选择器上，并且指定监听事件(接收)
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务端启动成功 .... 等待客户端链接....");
        // 6. 轮询式的获取选择器上已经就绪的事件
        while (selector.select() > 0) {
            // 7. 获取当前选择器所有注册的选择键
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                // 8. 获取就绪事件
                SelectionKey key = iterator.next();
                // 9. 判断事件类型
                if (key.isAcceptable()) {
                    // 10. 接收
                    SocketChannel socket = server.accept();
                    System.out.println("客户端" + socket.getLocalAddress().toString() + "链接成功....");
                    // 11. 切换非阻塞模式
                    socket.configureBlocking(false);
                    // 12. 注册到选择器上
                    socket.register(selector, SelectionKey.OP_READ);
                    // socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_ACCEPT);
                } else if (key.isReadable()) {
                    // 13. 获取通道
                    SocketChannel socket = (SocketChannel) key.channel();
                    // 14. 读数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len = socket.read(buffer)) > 0) {
                        buffer.flip();
                        System.out.println(new String(buffer.array(), 0, len));
                        buffer.clear();
                    }
                }
                // 15. 取消选择键
                iterator.remove();
            }
        }
    }
}
