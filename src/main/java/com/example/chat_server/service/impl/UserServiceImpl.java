package com.example.chat_server.service.impl;

import com.example.chat_server.entity.User;
import com.example.chat_server.mapper.UserMapper;
import com.example.chat_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
import cn.hutool.crypto.digest.BCrypt;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String account, String password) {
        if (account == null || account.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        User user = userMapper.selectByAccount(account);
        if (user == null || user.getPassword() == null) {
            return null;
        }

        if (BCrypt.checkpw(password, user.getPassword())){
            return user;
        }
        return null;
    }

    @Override
    public User register(User user) {
        if (user.getAccount() == null || user.getAccount().isEmpty()
                || user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("账号和密码不能为空");
        }

        User existUser = userMapper.selectByAccount(user.getAccount());
        if (existUser != null) {
            throw new IllegalArgumentException("账号已存在");
        }

        user.setId(UUID.randomUUID().toString().replace("-", ""));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        String encodedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(encodedPassword);
        userMapper.insert(user);
        return user;
    }

    @Override
    public User getUserById(String id) {
        return userMapper.selectById(id);
    }

    @Override
    public User updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        return user;
    }

    @Override
    public void deleteUser(String id) {
        userMapper.deleteById(id);
    }
}

