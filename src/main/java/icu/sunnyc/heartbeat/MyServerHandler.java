package icu.sunnyc.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author ：hc
 * @date ：Created in 2022/3/21 21:25
 * @modified ：
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *
     * @param ctx ChannelHandlerContext
     * @param evt 事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";break;
                case WRITER_IDLE:
                    eventType = "写空闲";break;
                case ALL_IDLE:
                    eventType = "读写空闲";break;
                default:
                    eventType = null;
            }
            System.out.println("事件：" + eventType);
            // 可以根据读空闲、写空闲或读写空闲这些事件做出不同的处理
            // 如果发生空闲，直接关闭通道
            ctx.channel().close();
        }
    }
}
