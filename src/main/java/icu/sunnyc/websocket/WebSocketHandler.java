package icu.sunnyc.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

/**
 * @author ：hc
 * @date ：Created in 2022/3/21 21:50
 * @modified ：
 *
 * TextWebSocketFrame 表示一个文本帧
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        System.out.println("服务器收到消息：" + msg.text());

        // 回复浏览器
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间：" + LocalDateTime.now() + "：" + msg.text()));
    }

    /**
     * 客户端连接触发
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // id 表示唯一值 asLongText 唯一 asShortText 不唯一
        System.out.println("触发 handlerAdded: " + ctx.channel().id().asLongText());
        System.out.println("触发 handlerAdded: " + ctx.channel().id().asShortText());
    }

    /**
     * 断连触发
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("触发 handlerRemoved: " + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
