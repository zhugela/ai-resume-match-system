package com.liangge.resumematch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 批量匹配结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchMatchResult {
    /**
     * 简历ID
     */
    private Long resumeId;

    /**
     * 总匹配数
     */
    private Integer totalCount;

    /**
     * 成功匹配数
     */
    private Integer successCount;

    /**
     * 匹配详情列表
     */
    private List<MatchDetail> matches;

    /**
     * 耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 匹配详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchDetail {
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
         * 匹配分数
         */
        private BigDecimal matchScore;

        /**
         * 匹配等级
         */
        private String matchLevel;

        /**
         * 匹配详情
         */
        private String matchDetails;

        /**
         * 状态
         */
        private String status;
    }
}