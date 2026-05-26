package com.example.chat_server.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String account;   // 账号
    private String password;  // 密码
}
