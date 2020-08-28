package com.xin.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * NIO 的通道
 *
 * 1. 通道：用于源节点和目标节点的链接，在JavaNIO中负责缓冲区中数据的传输
 *      channel本身不存储数据，需要配合缓冲区来使用
 *
 * 2. 通道的一些主要实现类（java.nio.channels.Channel接口）
 *      FileChannel / SocketChannel / ServerSocketChannel / DatagramChannel
 *
 * 3. 获取通道的方式
 *    1. 通过getChannel()方法
 *          本地IO： FileInputStream / FilOutputStream / RandomAccessFile
 *          网络IO： Socket / ServerSocket / DatagramSocket
 *    2. 静态方法FileChannel.open()
 *    3. Files工具类的newByteChannel()
 *
 * 4. 通道之间的数据传输
 *    1. transferTo : 传输到目标通道
 *        inChannel.transferTo(0,inChannel.size(),outChannel);
 *    2. transferFrom: 从目标通道传入
 *        outChannel.transferFrom(inChannel,0,inChannel.size());
 *
 * 5. 分散scatter与聚集gather
 *    1. 分散读取(scattering reads): 将通道中的数据分散到多个缓冲区中
 *    2. 聚集写入(gathering writes): 将多个缓冲区中的数据聚集到通道中
 *
 * 6. 字符集: Charset
 *    1. 编码: str -> byte[]
 *    2. 解码: byte[] -> str
 *
 */
public class NIOChannel {

    public static void main(String[] args) throws Exception {
        // noDirectCopy();
        // directCopy();
        // channelTrans();
        // buffers();
        character();
    }

    // 编码
    private static void character() throws CharacterCodingException {
        Charset gbk = Charset.forName("GBK");
        // 1. 获取编码器
        CharsetEncoder encoder = gbk.newEncoder();
        // 2. 获取解码器
        CharsetDecoder decoder = gbk.newDecoder();
        CharBuffer buffer = CharBuffer.allocate(1024);
        buffer.put("柒玖氵");
        buffer.flip();
        // 3. 编码
        ByteBuffer buffer1 = encoder.encode(buffer);
        for (int i = 0; i < 6; i++) {
            System.out.print(buffer1.get()+" ");
        }
        // 4. 解码
        buffer1.flip();
        CharBuffer buffer2 = decoder.decode(buffer1);
        System.out.println("\n"+buffer2.toString());

        // 5. 使用其他解码 出现乱码
        buffer1.flip();
        Charset utf8 = Charset.forName("UTF-8");
        System.out.println(utf8.decode(buffer1));
    }

    // 分散读取 聚集写入 使用多个缓冲区
    private static void buffers() throws IOException {
        // 随机读写
        RandomAccessFile file = new RandomAccessFile("文献", "rw");
        // 1. 获取通道
        FileChannel channel = file.getChannel();
        // 2. 分配指定大小的缓冲区
        ByteBuffer buffer1 = ByteBuffer.allocate(1024);
        ByteBuffer buffer2 = ByteBuffer.allocate(2 * 1024);
        // 3. 分散读取
        ByteBuffer[] buffers = {buffer1,buffer2};
        channel.read(buffers);
        for (ByteBuffer buffer : buffers) {
            buffer.flip();
            System.out.println(new String(buffer.array(),0,buffer.limit()));
            try {
                TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) {e.printStackTrace(); }
        }
        // 4. 聚集写入
        RandomAccessFile toFile = new RandomAccessFile("文献2", "rw");
        FileChannel channel2 = toFile.getChannel();
        channel2.write(buffers);
    }

    // 通道之间的数据传输
    private static void channelTrans() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("b.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("a.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
        inChannel.transferTo(0,inChannel.size(),outChannel);
        outChannel.transferFrom(inChannel,0,inChannel.size());
    }

    // 使用直接缓冲区完成文件复制(内存映射文件) 提高效率,稳定降低 使用FileChannel.open()方法来获取通道
    private static void directCopy() throws IOException {
        Instant start = Instant.now();
        // 1. 创建通道 create(存在 覆盖) create_New(存在 报错)
        FileChannel readChannel = FileChannel.open(Paths.get("b.jpg"), StandardOpenOption.READ);
        FileChannel writeChannel = FileChannel.open(Paths.get("a.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);

        // 2. 创建直接缓冲区
        MappedByteBuffer inBuffer = readChannel.map(FileChannel.MapMode.READ_ONLY,0,readChannel.size());
        MappedByteBuffer outBuffer = writeChannel.map(FileChannel.MapMode.READ_WRITE,0,readChannel.size());

        // 3. 复制
        inBuffer.get(new byte[inBuffer.limit()]);
        outBuffer.put(new byte[inBuffer.limit()]);
        Instant end = Instant.now();
        System.out.println("耗时： " + ChronoUnit.NANOS.between(start, end));

        // 4. 关闭资源
        writeChannel.close();
        readChannel.close();
    }

    // 使用非直接缓冲区完成文件复制 -- 使用stream来获取通道
    private static void noDirectCopy() throws IOException {
        Instant start = Instant.now();
        // 1. 获取文件流C:\Users\xin\Desktop\浅\java\CodeFile\jvm&juc\nio\a.jpg
        FileInputStream fis = new FileInputStream("C:\\Users\\xin\\Desktop\\浅\\java\\CodeFile\\jvm&juc\\nio\\a.jpg");
        FileOutputStream fos = new FileOutputStream("b.jpg");
        // 2. 获取通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannl = fos.getChannel();
        // 3. 分配缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(10 * 1024);
        // 4. 将通道中的数据存入到缓冲区中
        while(inChannel.read(buffer) != -1){
            // 切换到读模式
            buffer.flip();
            // 将缓冲区数据写入到通道中
            outChannl.write(buffer);
            // 清空缓冲区
            buffer.clear();
        }
        Instant end = Instant.now();
        System.out.println("耗时：" + ChronoUnit.NANOS.between(start,end));
        outChannl.close();
        inChannel.close();
        fos.close();
        fis.close();
    }

}
