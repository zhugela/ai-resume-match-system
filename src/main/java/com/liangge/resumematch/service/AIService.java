package com.liangge.resumematch.service;

import com.liangge.resumematch.entity.Job;
import com.liangge.resumematch.entity.MatchRecord;
import com.liangge.resumematch.entity.Resume;

import java.util.List;

public interface AIService {
    Resume getResumeById(Long id);

    /**
     * 分析简历
     */
    Resume analyzeResume(Resume resume);
    
    /**
     * 计算岗位匹配度
     */
    List<MatchRecord> matchJobs(Resume resume, List<Job> jobs);
    
    /**
     * 获取推荐岗位
     */
    List<MatchRecord> getRecommendedJobs(Long userId, Integer limit);
}
