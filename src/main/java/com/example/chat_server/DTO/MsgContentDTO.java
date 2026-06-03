package com.example.chat_server.DTO;

import lombok.Data;

@Data
public class MsgContentDTO {
    private String toId;
    //消息内容类型
    private String type;
    //消息内容
    private String content;
}
