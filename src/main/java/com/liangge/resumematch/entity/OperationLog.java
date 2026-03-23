package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@TableName("t_operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String username;
    
    /**
     * 操作模块: USER, JOB, RESUME, MATCH, FAVORITE, NOTIFICATION, FEEDBACK
     */
    private String module;
    
    /**
     * 操作类型: CREATE, UPDATE, DELETE, QUERY, LOGIN, EXPORT
     */
    private String operation;
    
    private String method;
    
    private String url;
    
    private String params;
    
    private String result;
    
    /**
     * 操作状态: SUCCESS, FAIL
     */
    private String status;
    
    private String errorMessage;
    
    private String ip;
    
    private String userAgent;
    
    private Long duration;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
