package com.liangge.resumematch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangge.resumematch.dto.AnalysisResult;
import com.liangge.resumematch.dto.MatchResult;
import com.liangge.resumematch.entity.AsyncTask;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.service.impl.AiAnalysisServiceImpl;
import com.liangge.resumematch.utils.ResumeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 异步任务执行器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncTaskExecutor {

    private final AsyncTaskService asyncTaskService;
    private final AiAnalysisServiceImpl aiAnalysisService;
    private final ResumeParser resumeParser;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 执行简历分析任务
     */
    @Async("taskExecutor")
    public void executeResumeAnalysis(AsyncTask task) {
        log.info("开始执行简历分析任务: taskId={}", task.getTaskId());
        
        try {
            asyncTaskService.updateTaskProgress(task.getTaskId(), 10);
            
            // 获取简历
            Resume resume = aiAnalysisService.getResumeById(task.getResumeId());
            if (resume == null) {
                throw new RuntimeException("简历不存在: " + task.getResumeId());
            }
            
            asyncTaskService.updateTaskProgress(task.getTaskId(), 30);
            
            // 解析简历内容
            String content = resumeParser.parseToText(resume.getFileUrl());
            
            asyncTaskService.updateTaskProgress(task.getTaskId(), 50);
            
            // 调用 AI 分析
            AnalysisResult result = aiAnalysisService.analyzeResume(resume, content);
            
            asyncTaskService.updateTaskProgress(task.getTaskId(), 90);
            
            // 保存结果
            String resultJson = objectMapper.writeValueAsString(result);
            asyncTaskService.updateTaskStatus(task.getTaskId(), "COMPLETED", resultJson, null);
            
            log.info("简历分析任务完成: taskId={}", task.getTaskId());
            
        } catch (Exception e) {
            log.error("简历分析任务失败: taskId={}, error={}", task.getTaskId(), e.getMessage(), e);
            asyncTaskService.updateTaskStatus(task.getTaskId(), "FAILED", null, e.getMessage());
        }
    }

    /**
     * 执行简历优化任务
     */
    @Async("taskExecutor")
    public void executeResumeOptimize(AsyncTask task) {
        log.info("开始执行简历优化任务: taskId={}", task.getTaskId());
        
        try {
            asyncTaskService.updateTaskProgress(task.getTaskId(), 10);
            
            Resume resume = aiAnalysisService.getResumeById(task.getResumeId());
            if (resume == null) {
                throw new RuntimeException("简历不存在: " + task.getResumeId());
            }
            
            asyncTaskService.updateTaskProgress(task.getTaskId(), 30);
            
            String content = resumeParser.parseToText(resume.getFileUrl());
            
            asyncTaskService.updateTaskProgress(task.getTaskId(), 50);
            
            // 调用 AI 优化
            AnalysisResult result = aiAnalysisService.optimizeResume(
                resume, content, 
                task.getTargetJobId() != null ? task.getTargetJobId().toString() : null,
                task.getJobDescription()
            );
            
            asyncTaskService.updateTaskProgress(task.getTaskId(), 90);
            
            String resultJson = objectMapper.writeValueAsString(result);
            asyncTaskService.updateTaskStatus(task.getTaskId(), "COMPLETED", resultJson, null);
            
            log.info("简历优化任务完成: taskId={}", task.getTaskId());
            
        } catch (Exception e) {
            log.error("简历优化任务失败: taskId={}, error={}", task.getTaskId(), e.getMessage(), e);
            asyncTaskService.updateTaskStatus(task.getTaskId(), "FAILED", null, e.getMessage());
        }
    }

    /**
     * 执行岗位匹配任务
     */
    @Async("taskExecutor")
    public void executeJobMatch(AsyncTask task) {
        log.info("开始执行岗位匹配任务: taskId={}", task.getTaskId());
        
        try {
            asyncTaskService.updateTaskProgress(task.getTaskId(), 10);
            
            Resume resume = aiAnalysisService.getResumeById(task.getResumeId());
            if (resume == null) {
                throw new RuntimeException("简历不存在: " + task.getResumeId());
            }
            
            asyncTaskService.updateTaskProgress(task.getTaskId(), 30);
            
            String content = resumeParser.parseToText(resume.getFileUrl());
            
            asyncTaskService.updateTaskProgress(task.getTaskId(), 50);
            
            // 构建匹配请求
            var request = new com.liangge.resumematch.dto.MatchRequest();
            request.setJobDescription(task.getJobDescription());
            request.setResumeId(task.getResumeId());
            
            // 调用 AI 匹配
            MatchResult result = aiAnalysisService.matchWithJob(resume, content, request);
            
            asyncTaskService.updateTaskProgress(task.getTaskId(), 90);
            
            String resultJson = objectMapper.writeValueAsString(result);
            asyncTaskService.updateTaskStatus(task.getTaskId(), "COMPLETED", resultJson, null);
            
            log.info("岗位匹配任务完成: taskId={}", task.getTaskId());
            
        } catch (Exception e) {
            log.error("岗位匹配任务失败: taskId={}, error={}", task.getTaskId(), e.getMessage(), e);
            asyncTaskService.updateTaskStatus(task.getTaskId(), "FAILED", null, e.getMessage());
        }
    }
}
