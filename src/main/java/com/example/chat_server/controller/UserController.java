package com.example.chat_server.controller;

import com.example.chat_server.annotation.UrlFree;
import com.example.chat_server.utils.ResultUtil;
import com.example.chat_server.entity.User;
import com.example.chat_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.hutool.json.JSONObject;

import java.util.HashMap;
import com.example.chat_server.utils.JwtUtil;
import java.util.Map;



@RestController
@RequestMapping("/api/user")


public class UserController {
    @Autowired
    private UserService userService;

    @UrlFree
    @PostMapping("/login")
    public JSONObject login(@RequestBody Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");

        User user = userService.login(account,password);
        if(user != null){
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("account", user.getAccount());
            claims.put("name", user.getName());
            claims.put("email", user.getEmail());
            String token = JwtUtil.createToken(claims);

            User safeUser = new User();
            safeUser.setId(user.getId());
            safeUser.setAccount(user.getAccount());
            safeUser.setName(user.getName());
            safeUser.setPortrait(user.getPortrait());
            safeUser.setSex(user.getSex());
            safeUser.setEmail(user.getEmail());
            safeUser.setCreateTime(user.getCreateTime());
            safeUser.setUpdateTime(user.getUpdateTime());

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("token", token);
            resultData.put("user", safeUser);
            return ResultUtil.Succeed("登录成功",resultData);
        }else{
            return ResultUtil.Fail("账号或密码错误");
        }
    }

    @UrlFree
    @PostMapping("/register")
    public JSONObject register(@RequestBody User user) {
        User registeredUser = userService.register(user);
        // 创建不包含密码的安全用户对象
        User safeUser = new User();
        safeUser.setId(registeredUser.getId());
        safeUser.setAccount(registeredUser.getAccount());
        safeUser.setName(registeredUser.getName());
        safeUser.setPortrait(registeredUser.getPortrait());
        safeUser.setSex(registeredUser.getSex());
        safeUser.setEmail(registeredUser.getEmail());
        safeUser.setCreateTime(registeredUser.getCreateTime());
        safeUser.setUpdateTime(registeredUser.getUpdateTime());

        return ResultUtil.Succeed("注册成功", safeUser);
    }

    @UrlFree
    @GetMapping("/{id}")
    public JSONObject getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResultUtil.Succeed(user);
        } else {
            return ResultUtil.Fail("用户不存在");
        }
    }

    @DeleteMapping("/{id}")
    public JSONObject deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResultUtil.Succeed();
    }
}
