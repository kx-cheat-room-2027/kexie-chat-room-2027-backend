package com.example.chat_server.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Server implements SmartLifecycle {

    private static final int port = 8081;
    private final Configuration webSocketInitializer;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;
    private volatile boolean isRunning = false; //多线程观察变量

    @Override
    public void start() {
        new Thread(() -> {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup) // 配置线程组
                        .channel(NioServerSocketChannel.class) //指定使用 NIO 模式的服务器 Socket 通道
                        .option(ChannelOption.SO_BACKLOG, 128) //已完成握手的连接队列最大长度
                        .option(ChannelOption.SO_KEEPALIVE, true) //开启 TCP 心跳保活
                        .handler(new LoggingHandler(LogLevel.INFO)) // 为 bossGroup 添加 日志处理器
                        .childHandler(webSocketInitializer);

                channelFuture = bootstrap.bind(port).sync();
                isRunning = true;
                log.info("Netty WebSocket 服务器已启动，端口号：{}", port);
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("Netty 服务器启动异常", e);
                Thread.currentThread().interrupt();
            } finally {
                stop();
            }
        }, "Websocket").start();
    }

    @Override
    public void stop() {
        if (!isRunning) return;
        if (channelFuture != null) channelFuture.channel().close();
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();

        isRunning = false;
        log.info("Netty WebSocket 服务器已关闭");
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
