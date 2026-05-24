package com.example.chat_server.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.chat_server.annotation.Userid;
import com.example.chat_server.dto.LoginDTO;
import com.example.chat_server.dto.RegisterDTO;
import com.example.chat_server.vo.UserVO;
import com.example.chat_server.annotation.UrlFree;
import com.example.chat_server.utils.ResultUtil;
import com.example.chat_server.entity.User;
import com.example.chat_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.hutool.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

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
    public JSONObject login(@RequestBody LoginDTO loginDTO) {
        String account = loginDTO.getAccount();
        String password = loginDTO.getPassword();

        User user = userService.login(account, password);
        if(user != null){
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("account", user.getAccount());
            claims.put("name", user.getName());
            claims.put("email", user.getEmail());
            claims.put("portrait", user.getPortrait());

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
    @PostMapping(value = "/register-with-avatar", consumes = {"multipart/form-data"})
    public JSONObject registerWithAvatar(
            @RequestParam("account") String account,
            @RequestParam("password") String password,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            User user = new User();
            user.setAccount(account);
            user.setPassword(password);
            user.setName(name);
            user.setSex(sex);
            user.setEmail(email);

            User registeredUser = userService.register(user, file);

            UserVO userVO = BeanUtil.copyProperties(registeredUser, UserVO.class);

            return ResultUtil.Succeed("注册成功", userVO);
        } catch (IllegalArgumentException e) {
            return ResultUtil.Fail(e.getMessage());
        } catch (Exception e) {
            return ResultUtil.Fail("注册失败：" + e.getMessage());
        }
    }

    //JSON 格式注册接口（不上传头像）
    @UrlFree
    @PostMapping("/register")
    public JSONObject register(@RequestBody RegisterDTO registerDTO) {
        try {
            // 将 DTO 转换为 User 实体
            User user = new User();
            user.setAccount(registerDTO.getAccount());
            user.setPassword(registerDTO.getPassword());
            user.setName(registerDTO.getName());
            user.setSex(registerDTO.getSex());
            user.setEmail(registerDTO.getEmail());

            User registeredUser = userService.register(user, null);

            UserVO userVO = BeanUtil.copyProperties(registeredUser, UserVO.class);

            return ResultUtil.Succeed("注册成功", userVO);
        } catch (IllegalArgumentException e) {
            return ResultUtil.Fail(e.getMessage());
        } catch (Exception e) {
            return ResultUtil.Fail("注册失败：" + e.getMessage());
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
    public JSONObject deleteUser(@PathVariable String id, @Userid String currentUserId) {
        // 验证是否是本人或管理员
        if (!currentUserId.equals(id)) {
            return ResultUtil.Forbidden();
        }
        userService.deleteUser(id);
        return ResultUtil.Succeed();
    }

    // 上传用户头像
    @PostMapping("/upload-avatar/{userId}")
    public JSONObject uploadAvatar(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) throws Exception {

        User user = userService.uploadAvatar(userId, file);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        return ResultUtil.Succeed("头像上传成功", userVO);
    }

    // 获取用户头像URL
    @GetMapping("/avatar/{userId}")
    public JSONObject getUserAvatar(@PathVariable String userId) throws Exception {
        String avatarUrl = userService.getUserAvatarUrl(userId);
        return ResultUtil.Succeed(avatarUrl);
    }
}
