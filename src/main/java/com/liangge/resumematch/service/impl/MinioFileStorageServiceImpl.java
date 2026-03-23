package com.liangge.resumematch.service.impl;

import com.liangge.resumematch.config.MinioConfig;
import com.liangge.resumematch.service.FileStorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * MinIO 文件存储服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public String upload(String bucket, String objectName, InputStream inputStream, String contentType) {
        try {
            // 确保桶存在
            ensureBucketExists(bucket);
            
            // 上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(inputStream, -1, 10485760)
                    .contentType(contentType)
                    .build()
            );
            
            log.info("文件上传成功: bucket={}, object={}", bucket, objectName);
            
            // 返回访问 URL
            return getUrl(bucket, objectName);
            
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(String bucket, String objectName, MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return upload(bucket, objectName, file.getInputStream(), contentType);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String bucket, String objectName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build()
            );
            log.info("文件删除成功: bucket={}, object={}", bucket, objectName);
            return true;
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getUrl(String bucket, String objectName) {
        // 如果是本地 MinIO，直接返回路径
        if (minioConfig.getEndpoint().contains("localhost") || minioConfig.getEndpoint().contains("127.0.0.1")) {
            return minioConfig.getEndpoint() + "/" + bucket + "/" + objectName;
        }
        // 生产环境可能需要生成预签名 URL
        return minioConfig.getEndpoint() + "/" + bucket + "/" + objectName;
    }

    @Override
    public boolean exists(String bucket, String objectName) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 确保桶存在，不存在则创建
     */
    private void ensureBucketExists(String bucket) {
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!exists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build()
                );
                log.info("创建存储桶: {}", bucket);
            }
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("创建存储桶失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建存储桶失败: " + e.getMessage());
        }
    }

    /**
     * 生成唯一文件名
     */
    public static String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }
}
