package icu.sunnyc.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 15:37
 * @modified ：
 * netty 客户端
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        // 客户端需要一个事件循环组就行了
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

        // 注意，这个和服务端不同
        Bootstrap bootstrap = new Bootstrap();

        try {
            // 设置相关参数
            // 设置线程池
            bootstrap.group(nioEventLoopGroup)
                    // NioSocketChannel 做客户端通道底层实现类
                    .channel(NioSocketChannel.class)
                    // 加入自己的处理器
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 加入自己的处理器
                            ch.pipeline().addLast(new NettyClientHandler());
                        }
                    });

            // 启动客户端连接服务端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9090).sync();

            // 监听关闭通道
            channelFuture.channel().closeFuture().sync();
        } finally {
            nioEventLoopGroup.shutdownGracefully();
        }





    }
}
