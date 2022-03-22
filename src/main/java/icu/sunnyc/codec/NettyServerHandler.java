package icu.sunnyc.codec;

import icu.sunnyc.codec.MyDataInfo.MyMessage;
import icu.sunnyc.codec.MyDataInfo.MyMessage.DataType;
import icu.sunnyc.codec.MyDataInfo.Student;
import icu.sunnyc.codec.MyDataInfo.Teacher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * @author ：hc
 * @date ：Created in 2022/3/20 15:26
 * @modified ：
 * 自定义 handler
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessage msg) {
        DataType dataType = msg.getDataType();

        if (dataType == DataType.STUDENT_TYPE) {
            Student student = msg.getStudent();
            System.out.println("学生信息：id=" + student.getId() + "  name=" + student.getName());
        }

        if (dataType == DataType.TEACHER_TYPE) {
            Teacher teacher = msg.getTeacher();
            System.out.println("教师信息：name=" + teacher.getName() + "  subject=" + teacher.getSubject());
        }
    }
}
