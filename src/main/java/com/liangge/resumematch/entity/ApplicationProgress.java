package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_application_progress")
public class ApplicationProgress {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long jobId;
    private String company;
    private String position;
    private String status;
    private String salary;
    private LocalDateTime interviewTime;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
