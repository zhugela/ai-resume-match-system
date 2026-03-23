package com.liangge.resumematch.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class MatchRequest {
    @NotNull(message = "简历 ID 不能为空")
    private Long resumeId;
    
    private Long jobId;                 // 岗位 ID
    
    @NotBlank(message = "岗位描述不能为空")
    private String jobDescription;
    
    private String jobTitle;        // 岗位标题
    private String company;         // 公司名称
    private String location;        // 工作地点
    private String salaryRange;     // 薪资范围
}
