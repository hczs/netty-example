package icu.sunnyc.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 14:56
 * @modified ：
 * netty 简易服务器入门
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        // 创建两个线程池
        // 一个 bossGroup 负责处理连接请求
        // 一个 workerGroup 负责具体的业务逻辑处理
        // 俩都是无限循环
        // bossGroup 和 workerGroup 里面的子线程的线程数，默认是 cpu核数 * 2
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建服务器端的启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            // 设置相关参数
            // 设置两个线程组 bossGroup workerGroup
            serverBootstrap.group(bossGroup, workerGroup)
                    // 使用 NioServerSocketChannel 作为服务端通道的实现
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列得到的连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 设置保持连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 给我们 workerGroup 的 eventLoop 对应的管道设置处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 向这个 workerGroup 关联的 pipeline 里面增加一个 handler
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 这个 SocketChannel 就是对应每个客户端的 SocketChannel
                            // 这里可以使用集合管理所有 SocketChannel，在需要时取出来，进行操作，例如异步的向各个客户端推送消息
                            // 加入我们自定义的 handler
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            // 绑定一个端口 这就相当于服务端启动了
            ChannelFuture channelFuture = serverBootstrap.bind(9090).sync();

            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    System.out.println("服务端已启动，监听端口：9090");
                } else {
                    System.out.println("服务端启动失败！，监听端口：9090");
                }
            });
            // 会先打印这一行，再输出 服务端已启动，表示确实是异步的，而不是同步的
            System.out.println("----------------");
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
