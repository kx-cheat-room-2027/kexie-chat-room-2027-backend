package com.example.chat_server.service.impl;

import com.example.chat_server.config.MinioConfig;
import com.example.chat_server.entity.User;
import com.example.chat_server.mapper.UserMapper;
import com.example.chat_server.service.MinioService;
import com.example.chat_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;
import cn.hutool.crypto.digest.BCrypt;
import org.springframework.web.multipart.MultipartFile;


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
    public User register(User user, MultipartFile avatarFile) throws Exception {
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

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String originalFilename = avatarFile.getOriginalFilename();
            int dotIndex = originalFilename.lastIndexOf(".");
            String fileExtension = (dotIndex > 0) ? originalFilename.substring(dotIndex) : ".png";
            String avatarFileName = "avatar_" + user.getId() + "_" + UUID.randomUUID() + fileExtension;

            minioService.upload(avatarFile, minioConfig.getImageBucket(), avatarFileName);
            user.setPortrait(avatarFileName);
        } else {
            user.setPortrait("default_avatar.png");
        }

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

    @Autowired
    private MinioService minioService;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    public User uploadAvatar(String userId, MultipartFile file) throws Exception {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        //如果上传的文件没有扩展名，lastIndexOf(".") 会返回 -1，导致 substring(-1) 抛出异常。
        String originalFilename = file.getOriginalFilename();
        int dotIndex = originalFilename.lastIndexOf(".");
        String fileExtension = (dotIndex > 0) ? originalFilename.substring(dotIndex) : ".png"; // 默认png
        String avatarFileName = "avatar_" + userId + "_" + UUID.randomUUID() + fileExtension;

        minioService.upload(file, minioConfig.getImageBucket(), avatarFileName);

        user.setPortrait(avatarFileName);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);

        return user;
    }

    @Override
    public String getUserAvatarUrl(String userId) throws Exception {
        User user = userMapper.selectById(userId);
        if (user == null || user.getPortrait() == null || user.getPortrait().isEmpty() || "default_avatar.png".equals(user.getPortrait())) {
            return null;
        }

        return minioService.getFileUrl(user.getPortrait(), minioConfig.getImageBucket(), 60 * 24 * 7);
    }
}

