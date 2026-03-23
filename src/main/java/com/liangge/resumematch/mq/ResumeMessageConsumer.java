package com.liangge.resumematch.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangge.resumematch.config.RabbitMQConfig;
import com.liangge.resumematch.dto.AnalysisResult;
import com.liangge.resumematch.entity.AsyncTask;
import com.liangge.resumematch.entity.Resume;
import com.liangge.resumematch.service.*;
import com.liangge.resumematch.utils.ResumeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 简历消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.rabbitmq.host")
public class ResumeMessageConsumer {

    private final AiAnalysisService aiAnalysisService;
    private final AsyncTaskService asyncTaskService;
    private final ExportService exportService;
    private final ResumeParser resumeParser;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理简历分析消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_ANALYSIS)
    public void handleAnalysis(ResumeAnalysisMessage message) {
        log.info("收到简历分析消息: taskId={}, resumeId={}", message.getTaskId(), message.getResumeId());
        
        try {
            asyncTaskService.updateTaskProgress(message.getTaskId().toString(), 10);
            
            // 获取简历
            Resume resume = aiAnalysisService.getResumeById(message.getResumeId());
            if (resume == null) {
                throw new RuntimeException("简历不存在: " + message.getResumeId());
            }
            
            asyncTaskService.updateTaskProgress(message.getTaskId().toString(), 30);
            
            // 解析简历内容
            String content = resumeParser.parseToText(resume.getFileUrl());
            
            asyncTaskService.updateTaskProgress(message.getTaskId().toString(), 50);
            
            // 调用 AI 分析
            AnalysisResult result = aiAnalysisService.analyzeResume(resume, content);
            
            asyncTaskService.updateTaskProgress(message.getTaskId().toString(), 90);
            
            // 保存结果
            String resultJson = objectMapper.writeValueAsString(result);
            asyncTaskService.updateTaskStatus(message.getTaskId().toString(), "COMPLETED", resultJson, null);
            
            log.info("简历分析完成: taskId={}", message.getTaskId());
            
        } catch (Exception e) {
            log.error("简历分析失败: taskId={}, error={}", message.getTaskId(), e.getMessage(), e);
            asyncTaskService.updateTaskStatus(message.getTaskId().toString(), "FAILED", null, e.getMessage());
        }
    }

    /**
     * 处理简历优化消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_OPTIMIZE)
    public void handleOptimize(ResumeOptimizeMessage message) {
        log.info("收到简历优化消息: taskId={}, resumeId={}", message.getTaskId(), message.getResumeId());
        
        try {
            asyncTaskService.updateTaskProgress(message.getTaskId().toString(), 10);
            
            Resume resume = aiAnalysisService.getResumeById(message.getResumeId());
            if (resume == null) {
                throw new RuntimeException("简历不存在: " + message.getResumeId());
            }
            
            asyncTaskService.updateTaskProgress(message.getTaskId().toString(), 30);
            
            String content = resumeParser.parseToText(resume.getFileUrl());
            
            asyncTaskService.updateTaskProgress(message.getTaskId().toString(), 50);
            
            // 调用 AI 优化
            AnalysisResult result = aiAnalysisService.optimizeResume(
                resume, content, 
                message.getTargetJobId() != null ? message.getTargetJobId().toString() : null,
                message.getJobDescription()
            );
            
            asyncTaskService.updateTaskProgress(message.getTaskId().toString(), 90);
            
            String resultJson = objectMapper.writeValueAsString(result);
            asyncTaskService.updateTaskStatus(message.getTaskId().toString(), "COMPLETED", resultJson, null);
            
            log.info("简历优化完成: taskId={}", message.getTaskId());
            
        } catch (Exception e) {
            log.error("简历优化失败: taskId={}, error={}", message.getTaskId(), e.getMessage(), e);
            asyncTaskService.updateTaskStatus(message.getTaskId().toString(), "FAILED", null, e.getMessage());
        }
    }

    /**
     * 处理导出消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_EXPORT)
    public void handleExport(ResumeExportMessage message) {
        log.info("收到导出消息: taskId={}, resumeId={}", message.getTaskId(), message.getResumeId());
        
        try {
            asyncTaskService.updateTaskProgress(message.getTaskId().toString(), 10);
            
            // 执行导出
            exportService.exportComparison(message.getResumeId(), message.getVersionId(), message.getExportType());
            
            asyncTaskService.updateTaskProgress(message.getTaskId().toString(), 100);
            asyncTaskService.updateTaskStatus(message.getTaskId().toString(), "COMPLETED", "导出成功", null);
            
            log.info("导出完成: taskId={}", message.getTaskId());
            
        } catch (Exception e) {
            log.error("导出失败: taskId={}, error={}", message.getTaskId(), e.getMessage(), e);
            asyncTaskService.updateTaskStatus(message.getTaskId().toString(), "FAILED", null, e.getMessage());
        }
    }
}
