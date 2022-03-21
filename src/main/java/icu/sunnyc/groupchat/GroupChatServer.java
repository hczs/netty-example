package icu.sunnyc.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author ：hc
 * @date ：Created in 2022/3/21 19:44
 * @modified ：
 * 群聊系统服务端
 */
public class GroupChatServer {

    /**
     * 监听端口
     */
    private final int port;

    public GroupChatServer(int port) {
        this.port = port;
    }

    /**
     * 处理客户端请求
     */
    public void run() throws InterruptedException {
        NioEventLoopGroup bossGroup  = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup  = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加一系列处理器
                            // 向 pipeline 中加入解码器
                            pipeline.addLast("decoder", new StringDecoder());
                            // 编码器
                            pipeline.addLast("encoder", new StringEncoder());
                            // 自定义处理器
                            pipeline.addLast(new GroupChatServerHandler());
                        }
                    });

            System.out.println("Netty 服务器正在启动...");

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            channelFuture.addListener(feature -> {
                if (feature.isSuccess()) {
                    System.out.println("服务启动成功");
                } else {
                    System.out.println("服务启动失败");
                }
            });

            // 监听关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        GroupChatServer groupChatServer = new GroupChatServer(9090);
        groupChatServer.run();
    }
}
