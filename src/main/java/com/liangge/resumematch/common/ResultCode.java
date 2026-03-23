package com.liangge.resumematch.common;

import lombok.Getter;

/**
 * 结果码枚举
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    
    // 认证相关 1000-1099
    UNAUTHORIZED(1001, "未登录"),
    TOKEN_EXPIRED(1002, "Token已过期"),
    TOKEN_INVALID(1003, "Token无效"),
    FORBIDDEN(1004, "没有权限"),
    USER_NOT_FOUND(1005, "用户不存在"),
    USER_EXISTS(1006, "用户已存在"),
    PASSWORD_ERROR(1007, "密码错误"),
    
    // 简历相关 2000-2099
    RESUME_NOT_FOUND(2001, "简历不存在"),
    RESUME_UPLOAD_FAILED(2002, "简历上传失败"),
    RESUME_PARSE_FAILED(2003, "简历解析失败"),
    RESUME_ANALYSIS_FAILED(2004, "简历分析失败"),
    FILE_TYPE_NOT_SUPPORT(2005, "不支持的文件类型"),
    FILE_SIZE_EXCEED(2006, "文件大小超过限制"),
    
    // 岗位相关 3000-3099
    JOB_NOT_FOUND(3001, "岗位不存在"),
    JOB_CLOSED(3002, "岗位已下架"),
    JOB_NOT_OWNER(3003, "无权限操作该岗位"),
    
    // AI相关 4000-4099
    AI_SERVICE_ERROR(4001, "AI服务调用失败"),
    AI_ANALYSIS_TIMEOUT(4002, "AI分析超时");
    
    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
