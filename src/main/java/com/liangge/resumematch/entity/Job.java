package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_job")
public class Job {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long hrId;
    
    private String title;
    
    private String company;
    
    private String location;
    
    private String salaryRange;
    
    /**
     * 岗位类型: FULL_TIME-全职, PART_TIME-兼职, INTERNSHIP-实习, CONTRACT-合同
     */
    private String jobType;
    
    private String description;
    
    private String requirements;
    
    /**
     * 必备技能 JSON数组
     */
    private String requiredSkills;
    
    /**
     * 加分技能 JSON数组
     */
    private String preferredSkills;
    
    /**
     * 状态: DRAFT-草稿, OPEN-开放, CLOSED-已关闭
     */
    private String status;
    
    /**
     * 浏览次数
     */
    private Integer views;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
