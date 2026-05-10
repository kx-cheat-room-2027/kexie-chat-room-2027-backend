package com.example.chat_server.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO对象存储配置类
 * 用于配置MinIO客户端连接参数和创建MinioClient实例
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {
    /**
     * MinIO服务器地址
     */
    private String endpoint;
    
    /**
     * 访问密钥ID
     */
    private String accessKey;
    
    /**
     * 访问密钥密码
     */
    private String secretKey;
    
    /**
     * 默认存储桶名称
     */
    private String bucket;
    
    /**
     * 图片专用存储桶名称
     */
    private String imageBucket;
    
    /**
     * 文件专用存储桶名称
     */
    private String fileBucket;
    
    /**
     * 创建并配置MinioClient实例
     * 
     * @return MinioClient MinIO客户端实例，用于执行对象存储操作
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
