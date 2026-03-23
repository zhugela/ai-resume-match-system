package com.liangge.resumematch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangge.resumematch.entity.AiCallLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI 调用日志服务
 */
public interface AiCallLogService extends IService<AiCallLog> {
    
    /**
     * 记录 AI 调用
     */
    void logCall(Long userId, Long resumeId, String callType, String prompt, 
                 String response, Integer duration, Integer tokens, String status, 
                 String errorMessage, String model);
    
    /**
     * 获取用户的 AI 调用统计
     */
    Map<String, Object> getUserStatistics(Long userId);
    
    /**
     * 获取今日调用统计
     */
    Map<String, Object> getTodayStatistics();
    
    /**
     * 获取最近的调用记录
     */
    List<AiCallLog> getRecentLogs(int limit);
}
