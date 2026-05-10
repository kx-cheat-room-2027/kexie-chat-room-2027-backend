package com.example.chat_server.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Configuration extends ChannelInitializer<SocketChannel> {

    private final Handler nettyWebSocketHandler;

    @Override
    protected void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        // 心跳检测
        pipeline.addLast(new IdleStateHandler(30, 0, 0));
        // HTTP编码解码器
        pipeline.addLast(new HttpServerCodec());
        // 支持大数据流
        pipeline.addLast(new ChunkedWriteHandler());
        // 对HTTPMessage进行聚合操作，合并成FullHttpRequest/FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
        // WebSocket 升级
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 自定义的 handler
        pipeline.addLast(nettyWebSocketHandler);
    }
}
