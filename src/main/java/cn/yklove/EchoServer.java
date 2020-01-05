package cn.yklove;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author qinggeng
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 80;
        if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() + " <port> . default port = 80");
        } else {
            port = Integer.parseInt(args[0]);
        }
        new EchoServer(port).start();
    }

    private void start() throws Exception {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        // 创建EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        // 创建ServerBootstrap
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(group)
                // 指定所使用的 NIO 传输 Channel
                .channel(NioServerSocketChannel.class)
                // 使用指定的 端口设置套 接字地址
                .localAddress(new InetSocketAddress(port))
                // 添加一个 EchoServerHandler 到子 Channel 的 ChannelPipeline
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        // EchoServerHandler 被标注为 @Shareable ，所以我们可以总是使用同样的实例
                        socketChannel.pipeline().addLast(echoServerHandler);
                    }
                });
        try {
            // 异步地绑定服务器； 调用 sync()方法阻塞 等待直到绑定完成
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            // 获取 Channel 的 CloseFuture， 并 且阻塞当前线 程直到它完成
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 关闭 EventLoopGroup， 释放所有的资源
            group.shutdownGracefully().sync();
        }
    }
}
