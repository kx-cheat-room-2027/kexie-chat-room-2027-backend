package com.example.chat_server.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {

    String upload(MultipartFile file, String bucketName , String objectName) throws Exception;

    String uploadImage(MultipartFile file , String objectName) throws Exception;

    String uploadFile(MultipartFile file , String objectName) throws Exception;

    String getFileUrl(String objectName, String bucketName, int expiry) throws Exception;

    void deleteFile(String objectName, String bucketName) throws Exception;
}
