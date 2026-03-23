package com.liangge.resumematch.dto;

import lombok.Data;

import java.util.List;

/**
 * 批量匹配请求
 */
@Data
public class BatchMatchRequest {
    /**
     * 简历ID
     */
    private Long resumeId;

    /**
     * 岗位ID列表
     */
    private List<Long> jobIds;

    /**
     * 是否使用AI语义匹配
     */
    private Boolean useSemanticMatch = false;

    /**
     * 并发数限制（默认10）
     */
    private Integer concurrency = 10;
}
