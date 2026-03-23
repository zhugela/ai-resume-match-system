package com.liangge.resumematch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_salary_data")
public class SalaryData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String company;
    private String position;
    private String location;
    private String experience;
    private Integer salaryLow;
    private Integer salaryHigh;
    private Integer salaryMid;
    private Integer sampleCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
