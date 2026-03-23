package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_match_analysis")
public class MatchAnalysis {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    
    private Long jobId;
    
    private BigDecimal overallScore;
    
    private String skillMatchDetails;
    
    private String experienceMatchDetails;
    
    private String educationMatchDetails;
    
    private String missingSkills;
    
    private String suggestedImprovements;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
