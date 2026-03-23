package com.liangge.resumematch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangge.resumematch.entity.AsyncTask;

import java.util.List;

public interface AsyncTaskService extends IService<AsyncTask> {
    
    /**
     * 创建异步任务
     */
    AsyncTask createTask(Long userId, String taskType, Long resumeId, String jobDescription, Long targetJobId);
    
    /**
     * 更新任务状态
     */
    void updateTaskStatus(String taskId, String status, String result, String errorMessage);
    
    /**
     * 更新任务进度
     */
    void updateTaskProgress(String taskId, Integer progress);
    
    /**
     * 获取任务状态
     */
    AsyncTask getTaskByTaskId(String taskId);
    
    /**
     * 获取用户任务列表
     */
    List<AsyncTask> getUserTasks(Long userId);
}
