package com.example.chat_server.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.chat_server.vo.UserVO;
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

            UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("token", token);
            resultData.put("user", userVO);
            return ResultUtil.Succeed("登录成功",resultData);
        }else{
            return ResultUtil.Fail("账号或密码错误");
        }
    }

    @UrlFree
    @PostMapping("/register")
    public JSONObject register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);

            UserVO userVO = BeanUtil.copyProperties(registeredUser, UserVO.class);

            return ResultUtil.Succeed("注册成功", userVO);
        } catch (IllegalArgumentException e) {
            // 捕获业务层抛出的异常（如账号已存在），直接返回给前端
            return ResultUtil.Fail(e.getMessage());
        }
    }


    @UrlFree
    @GetMapping("/public/{id}")
    public JSONObject getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);

        if (user != null) {
            UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);

            return ResultUtil.Succeed(userVO);
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
