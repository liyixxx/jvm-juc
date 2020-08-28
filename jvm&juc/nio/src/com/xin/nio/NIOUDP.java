package com.xin.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * udp形式传输
 */
public class NIOUDP {

    @Test
    public void send() throws IOException {
        DatagramChannel datagram = DatagramChannel.open();
        datagram.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(LocalDateTime.now().toString().getBytes());
        buffer.flip();
        datagram.send(buffer, new InetSocketAddress("127.0.0.1", 9300));
        buffer.clear();
    }

    @Test
    public void receive() throws IOException {
        DatagramChannel datagram = DatagramChannel.open();
        datagram.configureBlocking(false);
        datagram.bind(new InetSocketAddress(9300));
        Selector selector = Selector.open();
        datagram.register(selector, SelectionKey.OP_READ);
        while (selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    datagram.receive(buffer);
                    buffer.flip();
                    System.out.println(new String(buffer.array(), 0, buffer.limit()));
                    buffer.clear();
                }
            }
            iterator.remove();
        }
    }
}
