package com.kun.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.SocketAddress;
import java.util.Iterator;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel newClient = ctx.channel();
        System.out.println(newClient.remoteAddress() + "连接");
        //有新连接  通知其他客户端
        channelGroup.forEach(c -> c.writeAndFlush(String.format("客户端：[%s], 进来了", newClient.remoteAddress())));

        //将新连接的客户端通道加入group
        channelGroup.add(newClient);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel needRemove = ctx.channel();
        Iterator<Channel> iterator = channelGroup.iterator();
        // 循环通知其他客户端  有人离开，然后remove
        while(iterator.hasNext()){
            Channel channel = iterator.next();
            if(channel != needRemove){
                channel.writeAndFlush(String.format("客户端：[%s], 离开了", needRemove.remoteAddress()));
                continue;
            }
            iterator.remove();
        }
    }

    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        Channel channel = ctx.channel();
        //ChannelPipeline pipeline = ctx.pipeline(); //本质是一个双向链接, 出站入站
        //将 msg 转成一个 ByteBuf，类似NIO 的 ByteBuffer
        System.out.println(channel.remoteAddress() + " 发送消息: " + s);
        channelGroup.forEach(c -> {
            if(c == channel){
                //自己
                c.writeAndFlush("[自己]： " + s + "\n");
            }else{
                c.writeAndFlush("[客户端" + channel.remoteAddress() + " ]： " + s + "\n");
            }
        });
    }

    /**
     * 数据读取完毕处理方法
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //ctx.writeAndFlush("欢迎进入聊天室");
    }

    /**
     * 处理异常, 一般是需要关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //cause.printStackTrace();//打印异常
        System.out.println(ctx.channel().remoteAddress() + "通讯异常！");
        ctx.close();
    }

    /**
     * 服务器监听到客户端活动时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" + inComing.remoteAddress() + "]: 在线");
    }

    /**
     * 离线
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" + inComing.remoteAddress() + "]: 离线");
    }

}
