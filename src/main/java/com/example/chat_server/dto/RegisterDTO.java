package com.example.chat_server.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String account;   // 账号（必填）
    private String password;  // 密码（必填）
    private String name;      // 昵称（可选）
    private String sex;       // 性别（可选）
    private String email;     // 邮箱（可选）
}
