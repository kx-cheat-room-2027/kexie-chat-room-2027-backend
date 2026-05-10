package com.example.chat_server.websocket;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Sharable
@RequiredArgsConstructor
public class Handler
        extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final MessageService webSocketService;

    private void offLine(ChannelHandlerContext context) {
        webSocketService.offline(context.channel());
        context.channel().close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        offLine(context);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        offLine(context);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) { //处理心跳
            if (idleStateEvent.state() == IdleState.READER_IDLE) offLine(context);
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) { //处理连接
            webSocketService.online(context.channel());
        }
        super.userEventTriggered(context, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, TextWebSocketFrame msg) {
        webSocketService.sendToOthers(context.channel(), msg.text());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        log.error("WebSocket异常", cause);
        context.channel().close();
    }
}
