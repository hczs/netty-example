package icu.sunnyc.websocket;

import icu.sunnyc.simple.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jdk.nashorn.internal.runtime.SpillProperty;

/**
 * @author ：hc
 * @date ：Created in 2022/3/21 21:39
 * @modified ：
 */
public class WebSocketServer {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 因为是基于 http 协议，所以需要 http 编解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 以块的方式读写，添加 块处理handler
                            pipeline.addLast(new ChunkedWriteHandler());
                            // http 数据传输过程中其实是分段，HttpObjectAggregator 就是可以将多个分段聚合
                            // 这就是为什么浏览器发送大量数据时，就会发出多次 http 请求
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            // websocket 数据是以帧（frame）的形式传递，所以需要处理器去处理
                            // websocketFrame 下面有六个子类
                            // 浏览器请求时：ws://127.0.0.1:9090/hello 表示请求的 uri
                            // WebSocketServerProtocolHandler 核心功能是将 http 协议升级为 ws 协议 保持长连接
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            // 自定义 handler，业务逻辑处理
                            pipeline.addLast(new WebSocketHandler());
                        }
                    });

            System.out.println("服务器启动中...");

            ChannelFuture channelFuture = serverBootstrap.bind(9090).sync();

            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    System.out.println("服务端已启动，端口：9090");
                } else {
                    System.out.println("服务端启动失败");
                }
            });
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
