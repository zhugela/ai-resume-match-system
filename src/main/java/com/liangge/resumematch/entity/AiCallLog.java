package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_ai_call_log")
public class AiCallLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;            // 用户ID
    
    private Long resumeId;          // 简历ID
    
    private String callType;        // 调用类型: ANALYZE, OPTIMIZE, MATCH
    
    private String prompt;          // 请求 prompt（截取）
    
    private String response;        // 响应（截取）
    
    private Integer duration;       // 调用耗时（毫秒）
    
    private Integer tokens;         // 消耗 token 数
    
    private String status;          // 状态: SUCCESS, FAILED
    
    private String errorMessage;    // 错误信息
    
    private String model;           // 使用的模型
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
