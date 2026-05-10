package com.example.chat_server.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.chat_server.annotation.Userid;
import com.example.chat_server.entity.Message;
import com.example.chat_server.service.MessageService;
import com.example.chat_server.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 发送消息（第一步）
     *
     * @param fromId 发送方ID
     * @param type 消息类型（text/image/file）
     * @param content 消息内容
     * @return message
     */
    @PostMapping("/send")
    public JSONObject sendMessage(
            @Userid String fromId,
            @RequestParam String type,
            @RequestParam(required = false, defaultValue = "") String content) {

        Message message = messageService.sendMessage(fromId, type, content);

        JSONObject result = ResultUtil.Succeed();
        result.set("data", message);
        return result;
    }

    /**
     * 上传文件到消息
     * 前端上传文件时，携带messageId + 文件流
     * @param messageId 消息ID
     * @param file 文件
     * @return msg 消息
     */
    @PostMapping("/upload-file/{messageId}")
    public JSONObject uploadFile(
            @PathVariable String messageId,
            @RequestParam("file") MultipartFile file) throws Exception {

        Message message = messageService.upload(messageId, file);

        return ResultUtil.Succeed(message);

    }
    /**
     * 获取文件URL
     * @param minioName 文件名
     * @return url 文件URL(7天有效期)
     */
    @GetMapping("/url/file/{minioName}")
    public JSONObject getFileUrlByMinioName(@PathVariable String minioName) throws Exception {
        String url = messageService.getFileUrl(minioName);
        return ResultUtil.Succeed(url);
    }
    /**
     * 获取图片URL
     * @param minioName 文件名
     * @return url 图片URL(7天有效期)
     */
    @GetMapping("/url/image/{minioName}")
    public JSONObject getImageUrlByMinioName(String minioName) throws Exception {
        String url = messageService.getImageUrl(minioName);
        return ResultUtil.Succeed(url);
    }




    /**
     * 获取消息列表（正序）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页消息列表
     */
    @GetMapping("/list/asc")
    public JSONObject getMessageListAsc(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Message> page = messageService.getMessageListAsc(pageNum, pageSize);
        return ResultUtil.Succeed(page);
    }

    /**
     * 获取消息列表（倒序）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页消息列表
     */
    @GetMapping("/list/desc")
    public JSONObject getMessageListDesc(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Message> page = messageService.getMessageListDesc(pageNum, pageSize);
        return ResultUtil.Succeed(page);
    }
}
