package com.example.chat_server.service.impl;


import com.example.chat_server.config.MinioConfig;
import com.example.chat_server.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Service
public class MinioServiceImpl implements MinioService {
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient minioClient;

    /**
     * 通用上传方法 - 上传到指定桶
     * @param file 要上传的文件
     * @param bucketName 目标存储桶名称
     * @param objectName 文件对象名称（UUID.扩展名）
     * @return 文件在MinIO中的对象名称（UUID.扩展名）
     */
    public String upload(MultipartFile file, String bucketName , String objectName) throws Exception {
        // 1. 检查桶是否存在，不存在则创建
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }


        // 3. 执行上传
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)              // 目标桶
                        .object(objectName)              // 对象名
                        .stream(file.getInputStream(), file.getSize(), -1)  // 文件流
                        .contentType(file.getContentType())  // MIME类型
                        .build());
        return objectName;
    }

    /**
     * 上传图片 - 自动使用image-bucket配置的桶
     */
    @Override
    public String uploadImage(MultipartFile file , String objectName) throws Exception {
        return this.upload(file, minioConfig.getImageBucket() , objectName);
    }

    /**
     * 上传文件 - 自动使用file-bucket配置的桶
     */
    @Override
    public String uploadFile(MultipartFile file , String objectName) throws Exception {
        return this.upload(file, minioConfig.getFileBucket() , objectName);
    }

    /**
     * 获取文件临时访问URL（预签名URL）
     * @param objectName 文件对象名称
     * @param bucketName 存储桶名称
     * @param expiry 有效期（分钟）
     * @return 可访问的完整URL
     */
    @Override
    public String getFileUrl(String objectName, String bucketName, int expiry) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(expiry, TimeUnit.MINUTES)
                        .build());
    }

    /**
     * 删除文件
     * @param objectName 文件对象名称
     * @param bucketName 存储桶名称
     */
    @Override
    public void deleteFile(String objectName, String bucketName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }
}
