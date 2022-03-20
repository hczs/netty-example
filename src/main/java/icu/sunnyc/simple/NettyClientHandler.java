package icu.sunnyc.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 15:43
 * @modified ：
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道就绪了，就会触发此方法
     * @param ctx ChannelHandlerContext 上下文对象
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("客户端上下文对象：" + ctx);
        // 数据写入缓存并刷新
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, server! 喵~", CharsetUtil.UTF_8));
    }

    /**
     * 当有通道有读取事件时就会触发此方法
     * @param ctx ChannelHandlerContext 上下文对象
     * @param msg 收到的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("服务端回复的消息：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("服务端地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 出异常后触发此方法
     * @param ctx ChannelHandlerContext 上下文对象
     * @param cause 具体的异常对象
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
