package com.liangge.resumematch.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * 文件上传安全工具类
 */
public class FileSecurityUtil {

    // 允许的文件扩展名
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".pdf", ".doc", ".docx", ".txt"
    );

    // 允许的MIME类型
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain"
    );

    // 最大文件大小 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 验证文件是否安全
     */
    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 检查文件扩展名
        String lowerName = fileName.toLowerCase();
        boolean hasValidExtension = ALLOWED_EXTENSIONS.stream()
                .anyMatch(lowerName::endsWith);
        
        if (!hasValidExtension) {
            throw new IllegalArgumentException("不支持的文件类型，仅支持 PDF、DOC、DOCX、TXT");
        }

        // 检查MIME类型
        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("文件类型不匹配");
        }
    }

    /**
     * 安全生成文件名（防止路径穿越）
     */
    public static String sanitizeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return "resume";
        }

        // 移除路径分隔符
        String fileName = originalFileName.replaceAll("[/\\\\:]", "_");

        // 移除危险字符
        fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        // 限制长度
        if (fileName.length() > 100) {
            String ext = getExtension(fileName);
            fileName = fileName.substring(0, 100 - ext.length()) + ext;
        }

        return fileName.toLowerCase();
    }

    /**
     * 获取文件扩展名
     */
    public static String getExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : "";
    }
}
