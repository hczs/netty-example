package icu.sunnyc.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 20:45
 * @modified ：
 */
public class ByteBufExample02 {

    public static void main(String[] args) {

        ByteBuf byteBuf = Unpooled.copiedBuffer("hello", CharsetUtil.UTF_8);

        if (byteBuf.hasArray()) {
            byte[] bytes = byteBuf.array();
            // 这段可以理解为拿到之后重新编码
            System.out.println(new String(bytes, StandardCharsets.UTF_8));
            System.out.println("byteBuf: " + byteBuf);

            System.out.println("读下标：" + byteBuf.readerIndex());
            System.out.println("写下标：" + byteBuf.writerIndex());
            System.out.println("容量：" + byteBuf.capacity());

            System.out.println("当前可读取字节数：" + byteBuf.readableBytes());

            // 读取某一段，从0开始读，读三个数 编码用 utf-8
            System.out.println(byteBuf.getCharSequence(0, 3, CharsetUtil.UTF_8));
        }
    }
}
