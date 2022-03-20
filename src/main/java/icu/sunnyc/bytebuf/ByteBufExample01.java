package icu.sunnyc.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 20:36
 * @modified ：
 * 关于 netty 数据容器 ByteBuf 的使用
 * 读写
 */
public class ByteBufExample01 {

    public static void main(String[] args) {

        // 创建一个 ByteBuf
        // 其实就是创建了个 byte 数组，大小为 10，即 byte[10]
        ByteBuf byteBuf = Unpooled.buffer(10);

        for (int i = 0; i < 10; i++) {
            byteBuf.writeByte(i);
        }

        // NIO 的 buffer 需要 flip 反转，进行读写切换，但是 Netty 的不用
        // 因为人家维护了一个 readerIndex 和 一个 writerIndex 读写下标分开了
        // 这样也就分为了三个区
        // 0 - readerIndex 已读区域
        // readerIndex - writerIndex 可读区域
        // writerIndex - capacity 可写区域
        for (int i = 0; i < byteBuf.capacity(); i++) {
            System.out.println(byteBuf.readByte());
        }

        // 再次读会下标越界异常，因为 readerIndex 已经到头（writerIndex）了
        for (int i = 0; i < byteBuf.capacity(); i++) {
            System.out.println(byteBuf.readByte());
        }

    }
}
