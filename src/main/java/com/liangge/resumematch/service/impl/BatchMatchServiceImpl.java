package com.liangge.resumematch.service.impl;

import com.liangge.resumematch.dto.BatchMatchRequest;
import com.liangge.resumematch.dto.BatchMatchResult;
import com.liangge.resumematch.entity.Job;
import com.liangge.resumematch.entity.MatchRecord;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.mapper.JobMapper;
import com.liangge.resumematch.mapper.ResumeMapper;
import com.liangge.resumematch.service.AIService;
import com.liangge.resumematch.service.BatchMatchService;
import com.liangge.resumematch.service.SemanticMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 批量匹配服务实现 - 使用 CompletableFuture 并发调用AI
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchMatchServiceImpl implements BatchMatchService {

    private final ResumeMapper resumeMapper;
    private final JobMapper jobMapper;
    private final AIService aiService;
    private final SemanticMatchService semanticMatchService;

    /**
     * 批量匹配简历与多个岗位
     */
    @Override
    public BatchMatchResult batchMatch(BatchMatchRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("开始批量匹配: resumeId={}, jobCount={}", 
                request.getResumeId(), request.getJobIds() != null ? request.getJobIds().size() : 0);

        // 1. 验证简历
        Resume resume = resumeMapper.selectById(request.getResumeId());
        if (resume == null) {
            throw new IllegalArgumentException("简历不存在: " + request.getResumeId());
        }

        // 2. 获取岗位列表
        List<Job> jobs = jobMapper.selectBatchIds(request.getJobIds());
        if (jobs.isEmpty()) {
            return BatchMatchResult.builder()
                    .resumeId(request.getResumeId())
                    .totalCount(0)
                    .successCount(0)
                    .matches(new ArrayList<>())
                    .durationMs(0L)
                    .build();
        }

        // 3. 使用语义匹配或传统匹配
        List<MatchRecord> records;
        if (Boolean.TRUE.equals(request.getUseSemanticMatch())) {
            records = matchWithSemantic(resume, jobs);
        } else {
            records = matchWithAI(resume, jobs, request.getConcurrency());
        }

        // 4. 转换为返回结果
        List<BatchMatchResult.MatchDetail> details = records.stream()
                .map(this::convertToDetail)
                .toList();

        // 5. 按匹配分数排序
        details = details.stream()
                .sorted((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()))
                .toList();

        long duration = System.currentTimeMillis() - startTime;

        BatchMatchResult result = BatchMatchResult.builder()
                .resumeId(request.getResumeId())
                .totalCount(jobs.size())
                .successCount((int) details.stream()
                        .filter(d -> !"FAILED".equals(d.getStatus()))
                        .count())
                .matches(details)
                .durationMs(duration)
                .build();

        log.info("批量匹配完成: resumeId={}, total={}, success={}, duration={}ms",
                request.getResumeId(), result.getTotalCount(), result.getSuccessCount(), duration);

        return result;
    }

    /**
     * 使用AI进行批量匹配（CompletableFuture 并发）
     */
    private List<MatchRecord> matchWithAI(Resume resume, List<Job> jobs, Integer concurrency) {
        int poolSize = concurrency != null && concurrency > 0 ? concurrency : 10;
        poolSize = Math.min(poolSize, jobs.size());

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        try {
            List<CompletableFuture<MatchRecord>> futures = jobs.stream()
                    .map(job -> CompletableFuture.supplyAsync(() -> {
                        try {
                            return matchSingleJob(resume, job);
                        } catch (Exception e) {
                            log.error("匹配岗位 {} 失败: {}", job.getId(), e.getMessage());
                            return createFailedRecord(resume.getId(), job.getId(), e.getMessage());
                        }
                    }, executor))
                    .toList();

            // 等待所有任务完成
            CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allDone.get(5, TimeUnit.MINUTES); // 最多等待5分钟

            return futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

        } catch (Exception e) {
            log.error("批量匹配失败: {}", e.getMessage());
            throw new RuntimeException("批量匹配失败", e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 使用语义匹配
     */
    private List<MatchRecord> matchWithSemantic(Resume resume, List<Job> jobs) {
        List<MatchRecord> records = new ArrayList<>();

        for (Job job : jobs) {
            try {
                var semanticResult = semanticMatchService.matchSingle(resume.getId(), job.getId());

                MatchRecord record = new MatchRecord();
                record.setResumeId(resume.getId());
                record.setJobId(job.getId());
                record.setMatchScore(BigDecimal.valueOf(semanticResult.getSimilarityScore() * 100));
                record.setStatus("COMPLETED");
                record.setMatchDetails(semanticResult.getMatchReason());

                records.add(record);
            } catch (Exception e) {
                log.error("语义匹配岗位 {} 失败: {}", job.getId(), e.getMessage());
                records.add(createFailedRecord(resume.getId(), job.getId(), e.getMessage()));
            }
        }

        return records;
    }

    /**
     * 匹配单个岗位
     */
    private MatchRecord matchSingleJob(Resume resume, Job job) {
        List<Job> singleJobList = List.of(job);
        List<MatchRecord> records = aiService.matchJobs(resume, singleJobList);

        if (records.isEmpty()) {
            return createFailedRecord(resume.getId(), job.getId(), "无匹配结果");
        }

        return records.get(0);
    }

    /**
     * 创建失败的匹配记录
     */
    private MatchRecord createFailedRecord(Long resumeId, Long jobId, String errorMessage) {
        MatchRecord record = new MatchRecord();
        record.setResumeId(resumeId);
        record.setJobId(jobId);
        record.setStatus("FAILED");
        record.setMatchScore(BigDecimal.ZERO);
        record.setMatchDetails(errorMessage);
        return record;
    }

    /**
     * 转换为详情对象
     */
    private BatchMatchResult.MatchDetail convertToDetail(MatchRecord record) {
        Job job = jobMapper.selectById(record.getJobId());

        return BatchMatchResult.MatchDetail.builder()
                .jobId(record.getJobId())
                .jobTitle(job != null ? job.getTitle() : "未知岗位")
                .company(job != null ? job.getCompany() : "未知公司")
                .matchScore(record.getMatchScore())
                .matchLevel(calculateMatchLevel(record.getMatchScore()))
                .matchDetails(record.getMatchDetails())
                .status(record.getStatus())
                .build();
    }

    /**
     * 计算匹配等级
     */
    private String calculateMatchLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "EXCELLENT";
        } else if (score.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "GOOD";
        } else if (score.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }
}
