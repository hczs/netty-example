package icu.sunnyc.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 18:59
 * @modified ：
 * 接收、发送信息
 * SimpleChannelInboundHandler 这个玩意是继承了 ChannelInboundHandlerAdapter
 * 添加了泛型，就是可以专门处理特定类型的消息了，而不用强转
 */
public class ServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        // 判断 msg 是不是一个 http request
        if (msg instanceof HttpRequest) {
            System.out.println("msg 类型：" + msg.getClass());
            System.out.println("客户端地址：" + ctx.channel().remoteAddress());
            // 打印每次处理的 pipeline 对象和 handler
            System.out.println("pipeline: " + ctx.pipeline().hashCode() + "  handler: " + this.hashCode() );
            // 获取 HttpRequest 对象
            HttpRequest request = (HttpRequest) msg;
            // 过滤不做请求的对象
            String uri = request.uri();
            if ("/favicon.ico".equals(uri)) {
                // 不做响应
                System.out.println("请求了favicon.ico，不做响应");
                return;
            }

            // 回复响应消息
            ByteBuf responseMessage = Unpooled.copiedBuffer("我是服务器！", CharsetUtil.UTF_8);
            // 构造一个 http 响应对象 DefaultFullHttpResponse 注意使用 full 这个，构造方法里带 byteBuf 参数
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, responseMessage);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
            // responseMessage.readableBytes() 返回消息的可读字节数 int
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseMessage.readableBytes());
            ctx.writeAndFlush(response);
        }
    }
}
