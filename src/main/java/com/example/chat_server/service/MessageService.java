package com.example.chat_server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.chat_server.entity.Message;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService extends IService<Message> {

    /**
     * 发送消息（第一步）
     *
     * @param fromId 发送方ID
     * @param type 消息类型（text/image/file）
     * @param content 消息内容
     * @return 消息ID
     */
    Message sendMessage(String fromId , String type, String content);

    /**
     * 根据消息ID上传文件到MinIO（第二步）
     *
     * @param messageId 消息ID
     * @param file      文件
     * @return message 消息
     */
    Message upload(String messageId, MultipartFile file) throws Exception;


    String getImageUrl(String minioName) throws Exception;

    /**
     * 获取消息列表（正序）
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 消息列表
     */
    Page<Message> getMessageListAsc(int pageNum, int pageSize);

    /**
     * 获取消息列表（倒序）
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 消息列表
     */
    Page<Message> getMessageListDesc(int pageNum, int pageSize);

    String getFileUrl(String minioName) throws Exception;
}
