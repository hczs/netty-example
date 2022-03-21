package icu.sunnyc.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author ：hc
 * @date ：Created in 2022/3/21 21:08
 * @modified ：
 */
public class MyServer {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 在 bossGroup 中添加一个日志处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 加入一个 Netty 提供的处理器 IdleStateHandler
                            // IdleStateHandler 是处理空闲状态的处理器
                            // 参数说明
                            // 1.readerIdleTime 表示多长时间没有（server读客户端的数据）读数据，就会发送心跳检测包，看看还连着呢没
                            // 2.writerIdleTime 表示多长时间没有写操作了，也会发送一个心跳检测包，检测是否还是连接状态
                            // 3.allIdleTime 表示多长时间既没有读，也没有写，就会发送一个心跳包，看看还连着呢没
                            // 第四个参数是前三个参数的时间单位
                            // 如果出现 读空闲、写空闲或读写空闲，就会触发 IdleStateEvent
                            // 当 IdleStateEvent 触发后，就会传递给管道的下一个 handler 去处理
                            // 调用下一个 handler 哪个方法呢？就是 userEventTriggered 方法，在这个方法里去处理
                            pipeline.addLast(new IdleStateHandler(
                                    3, 5, 7, TimeUnit.SECONDS));
                            // 加入一个对空闲检测（读空闲，写空闲，读写空闲）进一步处理的 handler
                            pipeline.addLast(new MyServerHandler());
                        }
                    });

            // 启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(9090).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
