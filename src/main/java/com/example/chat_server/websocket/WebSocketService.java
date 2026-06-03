package com.example.chat_server.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.example.chat_server.entity.User;
import com.example.chat_server.service.UserService;
import com.example.chat_server.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

//WebSocket连接使用的方法
@Service
public class WebSocketService {


    @Resource
    UserService userService;

    @Data
    public static class WsContent {
        private String type;
        private Object content;
    }

    public static String Msg = "msg";
    //上线
    public static String Online = "online";

    public static String offline = "offline";

    //核心数据结构（在线用户存储）
    public static final ConcurrentHashMap<String, Channel> Online_User = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Channel, String> Online_Channel = new ConcurrentHashMap<>();

    //用户上线
    public void online(Channel channel, String token) {
        try {
            Claims claims = JwtUtil.parseToken(token);
            String userId = (String) claims.get("userId");

            Online_User.put(userId, channel);
            Online_Channel.put(channel, userId);

            //通知全体
            User user = userService.getUserById(userId);
            sendAll(userId,user,Online);
        } catch (Exception e) {
            channel.close();
        }
    }

    //用户离线
    public void offline(Channel channel) {
        String userId = Online_Channel.get(channel);  // 通过channel找userId
        if (StrUtil.isNotBlank(userId)) { //移除
            Online_User.remove(userId);
            Online_Channel.remove(channel);
            //通知全体
            User user = userService.getUserById(userId);
            sendAll(userId,user,offline);
        }
    }

    //发送消息（私有）
    private void sendMsg(Channel channel, Object msg, String type) {
        WsContent wsContent = new WsContent();
        wsContent.setType(type);
        wsContent.setContent(msg);
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsContent)));
    }


    // 发送给所有在线用户(除了自己)
    public void sendAll(String userId,Object msg,String type) {
        Online_Channel.forEach((channel, Id) -> {
            if(!Objects.equals(Id, userId)){
                sendMsg(channel, msg, type);
            }
        });
    }

}
