package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangge.resumematch.entity.AiCallLog;
import com.liangge.resumematch.mapper.AiCallLogMapper;
import com.liangge.resumematch.service.AiCallLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiCallLogServiceImpl extends ServiceImpl<AiCallLogMapper, AiCallLog> implements AiCallLogService {

    @Override
    public void logCall(Long userId, Long resumeId, String callType, String prompt,
                       String response, Integer duration, Integer tokens, String status,
                       String errorMessage, String model) {
        AiCallLog aiCallLog = new AiCallLog();
        aiCallLog.setUserId(userId);
        aiCallLog.setResumeId(resumeId);
        aiCallLog.setCallType(callType);

        // 截取 prompt 和 response 避免过长
        if (prompt != null && prompt.length() > 2000) {
            aiCallLog.setPrompt(prompt.substring(0, 2000));
        } else {
            aiCallLog.setPrompt(prompt);
        }

        if (response != null && response.length() > 2000) {
            aiCallLog.setResponse(response.substring(0, 2000));
        } else {
            aiCallLog.setResponse(response);
        }

        aiCallLog.setDuration(duration);
        aiCallLog.setTokens(tokens);
        aiCallLog.setStatus(status);
        aiCallLog.setErrorMessage(errorMessage);
        aiCallLog.setModel(model);

        save(aiCallLog);

        log.info("AI调用日志: type={}, duration={}ms, status={}", callType, duration, status);
    }

    @Override
    public Map<String, Object> getUserStatistics(Long userId) {
        List<AiCallLog> logs = list(new LambdaQueryWrapper<AiCallLog>()
                .eq(AiCallLog::getUserId, userId));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCalls", logs.size());
        stats.put("successCalls", logs.stream().filter(l -> "SUCCESS".equals(l.getStatus())).count());
        stats.put("failedCalls", logs.stream().filter(l -> "FAILED".equals(l.getStatus())).count());
        stats.put("totalDuration", logs.stream().mapToInt(l -> l.getDuration() != null ? l.getDuration() : 0).sum());
        stats.put("totalTokens", logs.stream().mapToInt(l -> l.getTokens() != null ? l.getTokens() : 0).sum());
        
        return stats;
    }

    @Override
    public Map<String, Object> getTodayStatistics() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        
        List<AiCallLog> logs = list(new LambdaQueryWrapper<AiCallLog>()
                .ge(AiCallLog::getCreatedAt, startOfDay));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCalls", logs.size());
        stats.put("successCalls", logs.stream().filter(l -> "SUCCESS".equals(l.getStatus())).count());
        stats.put("failedCalls", logs.stream().filter(l -> "FAILED".equals(l.getStatus())).count());
        stats.put("totalDuration", logs.stream().mapToInt(l -> l.getDuration() != null ? l.getDuration() : 0).sum());
        
        return stats;
    }

    @Override
    public List<AiCallLog> getRecentLogs(int limit) {
        return list(new LambdaQueryWrapper<AiCallLog>()
                .orderByDesc(AiCallLog::getCreatedAt)
                .last("LIMIT " + limit));
    }
}
