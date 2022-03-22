package icu.sunnyc.codec;

import icu.sunnyc.codec.MyDataInfo.MyMessage;
import icu.sunnyc.codec.MyDataInfo.MyMessage.DataType;
import icu.sunnyc.codec.MyDataInfo.Student;
import icu.sunnyc.codec.MyDataInfo.Teacher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Random;

/**
 * @author ：hc
 * @date ：Created in 2022/3/20 15:43
 * @modified ：
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道就绪了，就会触发此方法
     * @param ctx ChannelHandlerContext 上下文对象
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        MyMessage myMessage = null;
        // 随机发送一个对象到服务端
        int res = new Random().nextInt(2);
        if (res == 0) {
            myMessage = MyMessage.newBuilder().setDataType(DataType.STUDENT_TYPE)
                    .setStudent(Student.newBuilder().setId(1).setName("小明").build())
                    .build();
        }

        if (res == 1) {
            myMessage = MyMessage.newBuilder().setDataType(DataType.TEACHER_TYPE)
                    .setTeacher(Teacher.newBuilder().setName("王五").setSubject("数学").build())
                    .build();
        }
        ctx.writeAndFlush(myMessage);
    }

    /**
     * 当有通道有读取事件时就会触发此方法
     * @param ctx ChannelHandlerContext 上下文对象
     * @param msg 收到的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("服务端回复的消息：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("服务端地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 出异常后触发此方法
     * @param ctx ChannelHandlerContext 上下文对象
     * @param cause 具体的异常对象
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
