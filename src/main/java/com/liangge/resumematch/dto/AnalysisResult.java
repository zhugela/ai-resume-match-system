package com.liangge.resumematch.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AnalysisResult {
    private Integer score;                    // 简历评分
    private String optimizedContent;          // 优化后的内容
    private List<String> skills;              // 提取的技能标签
    private String workExperience;             // 工作经历
    private String education;                  // 教育经历
    private String optimizationSuggestions;   // 优化建议
    private List<String> interviewQuestions; // 生成的面试题
    private Map<String, Object> parsedData;   // 解析的原始数据
}
