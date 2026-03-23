package com.liangge.resumematch.service;

import com.liangge.resumematch.dto.ResumeScoreDTO;

public interface ResumeScoreService {
    /** 为简历生成评分 */
    ResumeScoreDTO generateScore(Long resumeId);
    /** 获取简历评分 */
    ResumeScoreDTO getScore(Long resumeId);
    /** 对比两份简历 */
    ResumeScoreDTO compareResumes(Long resumeId1, Long resumeId2);
}
