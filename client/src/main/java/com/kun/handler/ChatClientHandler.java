package com.kun.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.time.LocalDateTime;

public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 当客户端连接服务器完成就会触发该方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接服务器成功");
        System.out.println("服务端的地址： " + ctx.channel().remoteAddress());
    }

    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now + " | " + s);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }



}
