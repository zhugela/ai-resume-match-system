package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangge.resumematch.entity.AsyncTask;
import com.liangge.resumematch.mapper.AsyncTaskMapper;
import com.liangge.resumematch.service.AsyncTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AsyncTaskServiceImpl extends ServiceImpl<AsyncTaskMapper, AsyncTask> implements AsyncTaskService {

    @Override
    public AsyncTask createTask(Long userId, String taskType, Long resumeId, 
                                String jobDescription, Long targetJobId) {
        AsyncTask task = new AsyncTask();
        task.setTaskId(UUID.randomUUID().toString().replace("-", ""));
        task.setUserId(userId);
        task.setTaskType(taskType);
        task.setResumeId(resumeId);
        task.setJobDescription(jobDescription);
        task.setTargetJobId(targetJobId);
        task.setStatus("PENDING");
        task.setProgress(0);
        
        save(task);
        log.info("创建异步任务: taskId={}, type={}, userId={}", task.getTaskId(), taskType, userId);
        
        return task;
    }

    @Override
    public void updateTaskStatus(String taskId, String status, String result, String errorMessage) {
        AsyncTask task = getTaskByTaskId(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setResult(result);
            task.setErrorMessage(errorMessage);
            task.setProgress(100);
            
            if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
                task.setCompletedAt(LocalDateTime.now());
            }
            
            updateById(task);
            log.info("更新任务状态: taskId={}, status={}", taskId, status);
        }
    }

    @Override
    public void updateTaskProgress(String taskId, Integer progress) {
        AsyncTask task = getTaskByTaskId(taskId);
        if (task != null) {
            task.setProgress(progress);
            task.setStatus("PROCESSING");
            updateById(task);
        }
    }

    @Override
    public AsyncTask getTaskByTaskId(String taskId) {
        return getOne(new LambdaQueryWrapper<AsyncTask>()
                .eq(AsyncTask::getTaskId, taskId));
    }

    @Override
    public List<AsyncTask> getUserTasks(Long userId) {
        return list(new LambdaQueryWrapper<AsyncTask>()
                .eq(AsyncTask::getUserId, userId)
                .orderByDesc(AsyncTask::getCreatedAt));
    }
}
