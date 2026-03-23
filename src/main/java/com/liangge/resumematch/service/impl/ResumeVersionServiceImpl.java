package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangge.resumematch.entity.ResumeVersion;
import com.liangge.resumematch.mapper.ResumeVersionMapper;
import com.liangge.resumematch.service.ResumeVersionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ResumeVersionServiceImpl extends ServiceImpl<ResumeVersionMapper, ResumeVersion> implements ResumeVersionService {

    @Override
    public ResumeVersion saveVersion(Long resumeId, String originalContent, String optimizedContent,
                                     String optimizationSuggestions, String interviewQuestions,
                                     Double matchScore, Long targetJobId) {
        // 获取当前最新版本号
        Integer latestVersion = getLatestVersionNumber(resumeId);
        int newVersionNumber = (latestVersion != null ? latestVersion : 0) + 1;

        ResumeVersion version = new ResumeVersion();
        version.setResumeId(resumeId);
        version.setVersionNumber(newVersionNumber);
        version.setOriginalContent(originalContent);
        version.setOptimizedContent(optimizedContent);
        version.setOptimizationSuggestions(optimizationSuggestions);
        version.setInterviewQuestions(interviewQuestions);
        version.setMatchScore(matchScore != null ? BigDecimal.valueOf(matchScore) : null);
        version.setTargetJobId(targetJobId);

        save(version);
        return version;
    }

    @Override
    public List<ResumeVersion> getVersionsByResumeId(Long resumeId) {
        return list(new LambdaQueryWrapper<ResumeVersion>()
                .eq(ResumeVersion::getResumeId, resumeId)
                .orderByDesc(ResumeVersion::getVersionNumber));
    }

    @Override
    public ResumeVersion getVersionById(Long versionId) {
        return getById(versionId);
    }

    @Override
    public ResumeVersion getLatestVersion(Long resumeId) {
        return getOne(new LambdaQueryWrapper<ResumeVersion>()
                .eq(ResumeVersion::getResumeId, resumeId)
                .orderByDesc(ResumeVersion::getVersionNumber)
                .last("LIMIT 1"));
    }

    private Integer getLatestVersionNumber(Long resumeId) {
        ResumeVersion latest = getLatestVersion(resumeId);
        return latest != null ? latest.getVersionNumber() : null;
    }
}
