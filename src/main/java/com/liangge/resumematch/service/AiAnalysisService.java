package com.liangge.resumematch.service;

import com.liangge.resumematch.dto.*;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.entity.MatchAnalysis;

import java.util.List;

public interface AiAnalysisService {
    
    /**
     * 分析简历（完整版）
     * - 提取结构化信息
     * - 生成优化建议
     * - 生成面试题
     */
    AnalysisResult analyzeResume(Resume resume, String resumeContent);
    
    /**
     * 优化简历
     */
    AnalysisResult optimizeResume(Resume resume, String resumeContent, String targetJobId, String jobDescription);
    
    /**
     * 匹配岗位
     */
    MatchResult matchWithJob(Resume resume, String resumeContent, MatchRequest matchRequest);
    
    /**
     * 批量匹配多个岗位
     */
    List<MatchResult> matchWithJobs(Resume resume, String resumeContent, List<MatchRequest> matchRequests);
    
    /**
     * 根据ID获取简历
     */
    Resume getResumeById(Long id);
}
