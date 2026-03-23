package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_resume_version")
public class ResumeVersion {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    
    private Integer versionNumber;
    
    private String originalContent;
    
    private String optimizedContent;
    
    private String optimizationSuggestions;
    
    private String interviewQuestions;
    
    private BigDecimal matchScore;
    
    private Long targetJobId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
