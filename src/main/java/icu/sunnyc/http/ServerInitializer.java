package icu.sunnyc.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 18:58
 * @modified ：
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        // 获取管道
        ChannelPipeline pipeline = ch.pipeline();
        // HttpServerCodec 是 netty 提供的 http 请求的编解码器
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
        // 添加自定义处理器
        pipeline.addLast("ServerHandler", new ServerHandler());
    }
}
