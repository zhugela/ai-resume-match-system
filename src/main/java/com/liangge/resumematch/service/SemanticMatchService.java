package com.liangge.resumematch.service;

import com.liangge.resumematch.dto.SemanticMatchRequest;
import com.liangge.resumematch.dto.SemanticMatchResult;

import java.util.List;

/**
 * 语义匹配服务 - 基于向量相似度匹配
 */
public interface SemanticMatchService {

    /**
     * 简历与岗位的语义匹配
     *
     * @param request 匹配请求
     * @return 匹配结果列表（按相似度排序）
     */
    List<SemanticMatchResult> matchResumeWithJobs(SemanticMatchRequest request);

    /**
     * 单个简历与单个岗位的语义匹配
     *
     * @param resumeId 简历ID
     * @param jobId    岗位ID
     * @return 匹配结果
     */
    SemanticMatchResult matchSingle(Long resumeId, Long jobId);
}
