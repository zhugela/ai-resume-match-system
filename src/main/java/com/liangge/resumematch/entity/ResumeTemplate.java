package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_resume_template")
public class ResumeTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String fileUrl;
    private String fileType;
    private String previewUrl;
    private Integer downloadCount;
    private Boolean isPremium;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
