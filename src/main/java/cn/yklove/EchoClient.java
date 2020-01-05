package cn.yklove;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author qinggeng
 */
public class EchoClient {

    private final int port;
    private final String host;

    public EchoClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void start() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                // 指定 EventLoopGroup 以 处理客户端事件；需要适 用于 NIO 的实现
                .group(eventLoopGroup)
                // 适用于 NIO 传输的 Channel 类型
                .channel(NioSocketChannel.class)
                // 设置服务器的 InetSocketAddress
                .remoteAddress(new InetSocketAddress(host,port))
                // 在创建Channel 时， 向 ChannelPipeline 中添加一个 EchoClientHandler 实例
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new EchoClientHandler());
                    }
                });
        try {
            // 连接到远程节点，阻 塞等待直到连接完成
            ChannelFuture channelFuture = bootstrap.connect().sync();
            // 阻塞，直到 Channel 关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 关闭线程池并且 释放所有的资源
            eventLoopGroup.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new EchoClient( 80,"0.0.0.0").start();
    }
}
