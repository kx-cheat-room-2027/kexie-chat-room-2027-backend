package com.example.chat_server.websocket;

import cn.hutool.core.collection.ConcurrentHashSet;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class MessageService {

    public static final Set<Channel> channels = new ConcurrentHashSet<>();

    public void online(Channel channel) {
        channels.add(channel);
    }

    public void offline(Channel channel) {
        channels.remove(channel);
    }

    public void sendToOthers(Channel sender, String message) {
        for (var destination : channels){
            if(destination != sender){
                destination.writeAndFlush(new TextWebSocketFrame(message));
            }
        }
    }
}
