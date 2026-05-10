package com.example.chat_server.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private String id;
    private String account;
    private String name;
    private String portrait;
    private String sex;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
