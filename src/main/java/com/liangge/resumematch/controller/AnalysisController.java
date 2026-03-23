package com.liangge.resumematch.controller;

import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.dto.ResumeScoreDTO;
import com.liangge.resumematch.service.ResumeScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final ResumeScoreService resumeScoreService;

    /**
     * 生成简历评分
     */
    @PostMapping("/score/{resumeId}")
    public Result<ResumeScoreDTO> generateScore(@PathVariable Long resumeId) {
        return Result.success(resumeScoreService.generateScore(resumeId));
    }

    /**
     * 获取简历评分
     */
    @GetMapping("/score/{resumeId}")
    public Result<ResumeScoreDTO> getScore(@PathVariable Long resumeId) {
        return Result.success(resumeScoreService.getScore(resumeId));
    }

    /**
     * 对比两份简历
     */
    @GetMapping("/compare")
    public Result<ResumeScoreDTO> compareResumes(
            @RequestParam Long resumeId1,
            @RequestParam Long resumeId2) {
        return Result.success(resumeScoreService.compareResumes(resumeId1, resumeId2));
    }
}
