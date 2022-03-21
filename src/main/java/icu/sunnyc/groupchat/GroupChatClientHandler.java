package icu.sunnyc.groupchat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author ：hc
 * @date ：Created in 2022/3/21 20:30
 * @modified ：
 * 客户端处理类
 */
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 客户端收到消息很简单，就打印就行了
     * @param ctx ChannelHandlerContext
     * @param msg 消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println(msg.trim());
    }
}
