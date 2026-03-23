package com.liangge.resumematch.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {

    /**
     * 上传文件
     * @param bucket 存储桶
     * @param objectName 对象名
     * @param inputStream 输入流
     * @param contentType 内容类型
     * @return 文件访问URL
     */
    String upload(String bucket, String objectName, InputStream inputStream, String contentType);

    /**
     * 上传 MultipartFile
     */
    String uploadFile(String bucket, String objectName, MultipartFile file);

    /**
     * 删除文件
     */
    boolean delete(String bucket, String objectName);

    /**
     * 获取文件访问URL
     */
    String getUrl(String bucket, String objectName);

    /**
     * 判断文件是否存在
     */
    boolean exists(String bucket, String objectName);
}
