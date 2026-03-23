package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_async_task")
public class AsyncTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String taskId;          // 任务唯一标识
    
    private Long userId;            // 用户ID
    
    private String taskType;        // 任务类型: RESUME_ANALYSIS, RESUME_OPTIMIZE, JOB_MATCH
    
    private Long resumeId;          // 关联的简历ID
    
    private String jobDescription;  // 岗位描述（用于优化/匹配）
    
    private Long targetJobId;       // 目标岗位ID
    
    private String status;           // 状态: PENDING, PROCESSING, COMPLETED, FAILED
    
    private String result;          // 结果（JSON）
    
    private String errorMessage;    // 错误信息
    
    private Integer progress;       // 进度 0-100
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    private LocalDateTime completedAt;  // 完成时间
}
