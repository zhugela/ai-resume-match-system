package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_resume")
public class Resume {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String fileName;
    
    private String fileUrl;
    
    private String fileType;
    
    private Long fileSize;
    
    /**
     * AI解析后的数据 JSON
     */
    private String parsedData;
    
    /**
     * 提取的技能标签 JSON数组
     */
    private String skills;
    
    /**
     * 工作经历
     */
    private String workExperience;
    
    /**
     * 教育经历
     */
    private String education;
    
    /**
     * 分析状态: PENDING-待处理, PROCESSING-处理中, COMPLETED-已完成, FAILED-失败
     */
    private String analysisStatus;
    
    /**
     * 简历评分
     */
    private Integer score;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
