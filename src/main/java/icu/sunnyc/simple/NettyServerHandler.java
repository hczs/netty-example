package icu.sunnyc.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 15:26
 * @modified ：
 * 自定义 handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 实际读取客户端数据处理方法
     *
     * @param ctx ChannelHandlerContext 上下文对象，含有管道 pipeline、通道 channel 相关信息
     * @param msg 客户端发送的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        System.out.println("server ctx: " + ctx);
        // 将 msg 转换为 ByteBuf 进行读取 注意，这个 ByteBuf 是 Netty 提供的
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送过来的消息是：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 数据读取完毕后的方法
     *
     * @param ctx ChannelHandlerContext 上下文对象
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

        // 将数据写入到缓冲区并刷新提交
        // 一般来讲，会对发送数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~", CharsetUtil.UTF_8));
    }

    /**
     * 如果处理的时候发生异常的处理方法
     *
     * @param ctx ChannelHandlerContext 上下文对象
     * @param cause 具体的异常对象
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
