package com.example.chat_server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.chat_server.config.MinioConfig;
import com.example.chat_server.entity.Message;
import com.example.chat_server.entity.MsgContent;
import com.example.chat_server.exception.BaseException;
import com.example.chat_server.mapper.MessageMapper;
import com.example.chat_server.service.MessageService;
import com.example.chat_server.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private MinioService minioService;

    @Autowired
    private MinioConfig minioConfig;
    //发送消息
    @Override
    @Transactional
    public Message sendMessage(String fromId , String type, String content) {

        //封装消息内容
        //TODO 发送方头像,昵称根据formId查询填充到msgContent中
        MsgContent msgContent = MsgContent.builder()
                .fromUserId(fromId)//发送方ID
                .type(type)//消息类型
                .content(content)//消息内容,如果是文件消息,则content为文件名
                .build();

        Message message = new Message();
        message.setFromId(fromId);
        message.setMsgContent(msgContent);
        message.setIsShowTime(true);
        //TODO 时间填充createTime , updateTime

        save(message);

        return message;
    }
    //上传
    @Override
    @Transactional
    public Message upload(String messageId, MultipartFile file) throws Exception {
        //根据messageId查询,定位文件/图像所在消息
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BaseException("消息不存在");
        }
        //获取唯一名字作为minio内部文件名
        String MinioName = generateMinioName(message, file);
        String bucketName = "";
        // 3. 根据根据message获取文件类型分流,将文件file用MinioName上传桶
        uploadToBucket(message, file, MinioName);

        // 4. 更新消息内容,替换Minio名字, 返回消息 ,前端下次可再次获取
        message.getMsgContent().setContent(MinioName);
        //更新消息到数据库
        messageMapper.updateById(message);
        return message;

    }

    //获取文件URL
    @Override
    public String getFileUrl(String minioName) throws Exception {
        return minioService.getFileUrl(minioName , minioConfig.getFileBucket() ,60 * 24 * 7);
    }
    //获取图片URL
    @Override
    public String getImageUrl(String minioName) throws Exception {
        return minioService.getFileUrl(minioName , minioConfig.getImageBucket() ,60 * 24 * 7);
    }


    @Override
    public Page<Message> getMessageListAsc(int pageNum, int pageSize) {
        return this.lambdaQuery()
                .orderByAsc(Message::getCreateTime)
                .page(new Page<>(pageNum, pageSize));
    }

    @Override
    public Page<Message> getMessageListDesc(int pageNum, int pageSize) {
        return this.lambdaQuery()
                .orderByDesc(Message::getCreateTime)
                .page(new Page<>(pageNum, pageSize));
    }


//---------------------------------------------------一些抽取复用方法-----------------------------------------------------------------


    //根据文件类型分流上传桶
    private void uploadToBucket(Message message, MultipartFile file, String MinioName) throws Exception {
        String bucketName = "";

        String type = message.getMsgContent().getType();
        if (Objects.equals(type, "file")) {
            bucketName = minioConfig.getFileBucket();

        } else if (Objects.equals(type, "image")) {
            bucketName = minioConfig.getImageBucket();

        }
        minioService.upload(file, bucketName, MinioName);
    }



    //生成唯一对象文件/图像名
    private String generateMinioName(Message message , MultipartFile file) {
        //默认前端传过来的文件名称没有后缀
        MsgContent msgContent = message.getMsgContent();//msg -> msgcontent -> content(文件名)
        String Filename = msgContent.getContent();//文件/图像名

        // 生成唯一文件/图像名：
        // Filename + UUID + 原始扩展名
        // 密码规则大概率要改🫠
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String MinioName = Filename + UUID.randomUUID().toString() + fileExtension;

        return MinioName;
    }

}
