package com.kun;

import com.kun.handler.ChatClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class ClientApplication {

    public static void main(String[] args) throws Exception {
        //客户端需要一个事件循环组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建客户端启动对象
            //注意客户端使用的不是 ServerBootstrap 而是 Bootstrap
            Bootstrap bootstrap = new Bootstrap();
            //设置相关参数
            bootstrap.group(group) //设置线程组
                    .channel(NioSocketChannel.class) // 使用 NioSocketChannel 作为客户端的通道实现
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            //加入处理器
                            channel.pipeline().addLast(new StringEncoder());
                            channel.pipeline().addLast(new StringDecoder());
                            channel.pipeline().addLast(new ChatClientHandler());
                        }
                    });
            //启动客户端去连接服务器端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8665).sync();
            System.out.println("netty client start");

            Channel channel = channelFuture.channel();
            //循环监听控制台输入 并发送消息
            Scanner scanner = new Scanner(System.in);

            while(scanner.hasNext()){
                String next = scanner.next();
                channel.writeAndFlush(next);
            }

            //对关闭通道进行监听
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}
