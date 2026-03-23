package com.liangge.resumematch.controller;

import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.dto.BatchMatchRequest;
import com.liangge.resumematch.dto.BatchMatchResult;
import com.liangge.resumematch.dto.SemanticMatchRequest;
import com.liangge.resumematch.dto.SemanticMatchResult;
import com.liangge.resumematch.service.BatchMatchService;
import com.liangge.resumematch.service.SemanticMatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 批量匹配控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
@Tag(name = "批量匹配", description = "简历批量匹配岗位接口")
public class BatchMatchController {

    private final BatchMatchService batchMatchService;
    private final SemanticMatchService semanticMatchService;

    /**
     * 批量匹配
     */
    @PostMapping("/batch")
    @Operation(summary = "批量匹配", description = "一份简历匹配多个岗位，支持并发调用AI")
    public Result<BatchMatchResult> batchMatch(@RequestBody BatchMatchRequest request) {
        log.info("批量匹配请求: resumeId={}, jobCount={}", 
                request.getResumeId(), 
                request.getJobIds() != null ? request.getJobIds().size() : 0);
        
        BatchMatchResult result = batchMatchService.batchMatch(request);
        return Result.success(result);
    }

    /**
     * 语义匹配
     */
    @PostMapping("/semantic")
    @Operation(summary = "语义匹配", description = "基于向量相似度的语义匹配")
    public Result<List<SemanticMatchResult>> semanticMatch(@RequestBody SemanticMatchRequest request) {
        log.info("语义匹配请求: resumeId={}, jobCount={}", 
                request.getResumeId(), 
                request.getJobIds() != null ? request.getJobIds().size() : 0);
        
        List<SemanticMatchResult> results = semanticMatchService.matchResumeWithJobs(request);
        return Result.success(results);
    }

    /**
     * 单个语义匹配
     */
    @GetMapping("/semantic/{resumeId}/{jobId}")
    @Operation(summary = "单个语义匹配", description = "单个简历与单个岗位的语义匹配")
    public Result<SemanticMatchResult> semanticMatchSingle(
            @PathVariable Long resumeId,
            @PathVariable Long jobId) {
        log.info("单个语义匹配: resumeId={}, jobId={}", resumeId, jobId);
        
        SemanticMatchResult result = semanticMatchService.matchSingle(resumeId, jobId);
        return Result.success(result);
    }
}
