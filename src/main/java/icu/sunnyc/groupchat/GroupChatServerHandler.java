package icu.sunnyc.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author ：hc
 * @date ：Created in 2022/3/21 19:55
 * @modified ：
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {


    /**
     * 为什么用 static 修饰，因为这个 channel 组应该是归属于类，所有对象共享
     * 一个连接会有一个 handler 对象，所以需要搞个全局的 group
     * GlobalEventExecutor.INSTANCE 是全局的事件执行器 单例
     */
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 当有客户端连接加入时触发此方法
     * 将当前 channel 加入到 CHANNEL_GROUP 中
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel currentChannel = ctx.channel();
        // 将此客户端加入聊天的信息推送给其他在线的客户端，channelGroup 的 write 方法会自行遍历所有 channel 并发送消息
        CHANNEL_GROUP.writeAndFlush("客户端 [" + currentChannel.remoteAddress() + "] 加入聊天\n");
        CHANNEL_GROUP.add(currentChannel);
    }

    /**
     * 表示 channel 处于活动状态会触发此方法
     * 提示 xx 上线
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress() + " 上线啦");
    }

    /**
     * channel 处于非活动状态会触发此方法
     * 提示 xx 离线
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress() + " 离线啦");
    }

    /**
     * 当客户端断开连接的时候会触发此方法
     * 告诉所有客户端说这个人离线了
     * 这里不用执行 CHANNEL_GROUP.remove(currentChannel); handlerRemoved 会自己从 CHANNEL_GROUP 把这个 channel 移除
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        Channel currentChannel = ctx.channel();
        CHANNEL_GROUP.writeAndFlush("客户端 [" + currentChannel.remoteAddress() + "] 下线了\n");
        System.out.println("CHANNEL_GROUP size: " + CHANNEL_GROUP.size());
    }

    /**
     * 读取消息，转发给其他客户端
     * @param ctx ChannelHandlerContext
     * @param msg 信息内容
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        Channel currentChannel = ctx.channel();
        // 遍历 CHANNEL_GROUP 发送给其他客户端消息，但是这个转发得排除自己
        CHANNEL_GROUP.forEach(item -> {
            if (item != currentChannel) {
                // 向这个 channel 转发信息
                item.writeAndFlush("[用户] " + currentChannel.remoteAddress() + " : " + msg);
            } else {
                // 自己发的回显一下
                currentChannel.writeAndFlush("[自己]: " + msg);
            }
        });
    }

    /**
     * 出异常就关闭
     * @param ctx ChannelHandlerContext
     * @param cause 异常对象
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
