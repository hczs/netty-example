package icu.sunnyc.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 15:26
 * @modified ：
 * 自定义 handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 实际读取客户端数据处理方法
     *
     * @param ctx ChannelHandlerContext 上下文对象，含有管道 pipeline、通道 channel 相关信息
     * @param msg 客户端发送的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        // 注意：这样的处理方式是直接阻塞的，如果这里有耗时业务的话，会一直阻塞在 channelRead 操作上
        System.out.println("server ctx: " + ctx);
        // 将 msg 转换为 ByteBuf 进行读取 注意，这个 ByteBuf 是 Netty 提供的
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送过来的消息是：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());

        // 为了不阻塞，可以使用 taskQueue 这是常规情况
        ctx.channel().eventLoop().execute(() ->{
            System.out.println("瞄1当前线程：" + Thread.currentThread().getName());
            // 休眠十秒，模拟业务处理耗时长的场景
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 处理完毕，向客户端发送消息
            ctx.writeAndFlush(Unpooled.copiedBuffer("客户端，瞄1", CharsetUtil.UTF_8));
        });

        // 如果有俩耗时任务呢，注意 一个 eventLoop，就是一个线程，上面提交了一个任务，需要上面那个任务执行完毕，后面提交的任务才会被处理
        // 这相当于是给一个单线程，分配了俩任务，他干完一个才能干另一个，这个 瞄2 发送到客户端的时间，其实是 10 + 10 = 20 秒后
        // 因为 瞄1 那里执行需要花费十秒钟，注意这个是 execute，而不是 submit
        ctx.channel().eventLoop().execute(() -> {
            System.out.println("瞄2当前线程：" + Thread.currentThread().getName());
            // 休眠十秒，模拟业务处理耗时长的场景
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 处理完毕，向客户端发送消息
            ctx.writeAndFlush(Unpooled.copiedBuffer("客户端，瞄2", CharsetUtil.UTF_8));
        });

        // 还有一种情况是可以加定时任务的，不过定时任务队列和普通的任务队列不一样，这个任务会加入到 scheduleTaskQueue
        // 瞄3 五秒后会执行（错误） 瞄3 20秒后会执行（正确）
        // 因为 eventLoop 是个单线程，优先处理 taskQueue 的任务，处理完毕后才看 scheduleTaskQueue 里面的任务
        // 到执行时间后就立即执行，没到就等到了再执行
        ctx.channel().eventLoop().schedule(() -> {
            System.out.println("瞄3当前线程：" + Thread.currentThread().getName());
            ctx.writeAndFlush(Unpooled.copiedBuffer("客户端，瞄3", CharsetUtil.UTF_8));
        }, 5, TimeUnit.SECONDS);

        System.out.println("已加入任务队列");
    }

    /**
     * 数据读取完毕后的方法
     *
     * @param ctx ChannelHandlerContext 上下文对象
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

        // 将数据写入到缓冲区并刷新提交
        // 一般来讲，会对发送数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端，瞄0", CharsetUtil.UTF_8));
    }

    /**
     * 如果处理的时候发生异常的处理方法
     *
     * @param ctx ChannelHandlerContext 上下文对象
     * @param cause 具体的异常对象
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
