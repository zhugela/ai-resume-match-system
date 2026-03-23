package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 简历评分表（升级版分析结果存储）
 */
@Data
@Getter
@Setter
@TableName("t_resume_score")
public class ResumeScore {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long resumeId;
    
    /**
     * 综合评分 (0-100)
     */
    private Double overallScore;
    
    /**
     * 技能匹配度 (0-100)
     */
    private Double skillScore;
    
    /**
     * 经验匹配度 (0-100)
     */
    private Double experienceScore;
    
    /**
     * 学历匹配度 (0-100)
     */
    private Double educationScore;
    
    /**
     * 雷达图数据 JSON
     * 格式: {"skill": 85, "experience": 70, "education": 90, "communication": 75, "leadership": 60}
     */
    private String radarData;
    
    /**
     * 详细分析结果 JSON
     */
    private String analysisDetail;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
