package com.example.chat_server.service;

import com.example.chat_server.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User login(String account, String password);

    /**
     * 用户注册方法
     * @param user 包含用户注册信息的User对象
     * @return 注册成功返回User对象，可能包含更新后的用户信息
     */
    User register(User user, MultipartFile avatarFile) throws Exception;

    User getUserById(String id);

    void deleteUser(String id);

    User updateUser(User user);

    User uploadAvatar(String userId, MultipartFile file) throws Exception;
    String getUserAvatarUrl(String userId) throws Exception;
}
