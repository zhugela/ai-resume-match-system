package com.liangge.resumematch.dto;

import lombok.Data;

import java.util.List;

/**
 * 语义匹配请求
 */
@Data
public class SemanticMatchRequest {
    /**
     * 简历ID
     */
    private Long resumeId;

    /**
     * 岗位ID列表
     */
    private List<Long> jobIds;

    /**
     * 简历文本内容（可选，用于直接匹配）
     */
    private String resumeText;

    /**
     * 岗位描述列表（可选）
     */
    private List<String> jobDescriptions;

    /**
     * 返回top结果数量
     */
    private Integer topK = 5;
}
