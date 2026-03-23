package com.liangge.resumematch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 语义匹配结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemanticMatchResult {
    /**
     * 岗位ID
     */
    private Long jobId;

    /**
     * 岗位标题
     */
    private String jobTitle;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 相似度分数 (0-1)
     */
    private Double similarityScore;

    /**
     * 匹配说明
     */
    private String matchReason;

    /**
     * 匹配的关键技能
     */
    private List<String> matchedSkills;
}
