package com.liangge.resumematch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangge.resumematch.entity.ResumeVersion;

import java.util.List;

public interface ResumeVersionService extends IService<ResumeVersion> {
    
    /**
     * 保存简历版本
     */
    ResumeVersion saveVersion(Long resumeId, String originalContent, String optimizedContent, 
                             String optimizationSuggestions, String interviewQuestions,
                             Double matchScore, Long targetJobId);
    
    /**
     * 获取简历的所有版本
     */
    List<ResumeVersion> getVersionsByResumeId(Long resumeId);
    
    /**
     * 获取指定版本详情
     */
    ResumeVersion getVersionById(Long versionId);
    
    /**
     * 获取最新版本
     */
    ResumeVersion getLatestVersion(Long resumeId);
}
