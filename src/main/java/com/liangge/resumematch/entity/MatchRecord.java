package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_match_record")
public class MatchRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    
    private Long jobId;
    
    /**
     * 匹配度 0-100
     */
    private BigDecimal matchScore;
    
    /**
     * 匹配详情 JSON
     */
    private String matchDetails;
    
    /**
     * 状态: NEW-新增, VIEWED-已查看, INTERESTED-感兴趣, IGNORED-忽略
     */
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
