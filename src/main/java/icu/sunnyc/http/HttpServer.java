package icu.sunnyc.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 18:58
 * @modified ：
 *
 * 注意：每次请求都会对应一个自己独立的 handler 对象和 pipeLine
 * 因为 http 协议 用一次就断掉
 */
public class HttpServer {

    public static void main(String[] args) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建服务器端的启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 这回参数不设置那么多了，简单设置下 boss、worker、channel 和 handler
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());
            // 异步启动，绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
