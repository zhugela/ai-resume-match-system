package com.liangge.resumematch.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分析进度 WebSocket 推送服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisProgressWebSocket {

    private final SimpMessagingTemplate messagingTemplate;

    // 存储当前活跃的分析任务
    private final ConcurrentHashMap<Long, Boolean> activeAnalysis = new ConcurrentHashMap<>();

    /**
     * 发送进度消息（广播）
     */
    public void sendProgress(Long resumeId, AnalysisProgressMessage message) {
        String destination = "/topic/analysis/" + resumeId;
        messagingTemplate.convertAndSend(destination, message);
        log.debug("WebSocket 推送进度: resumeId={}, progress={}%", resumeId, message.getProgress());
    }

    /**
     * 发送进度消息（指定用户）
     */
    public void sendProgressToUser(String userId, Long resumeId, AnalysisProgressMessage message) {
        String destination = "/queue/analysis/" + resumeId;
        messagingTemplate.convertAndSendToUser(userId, destination, message);
        log.debug("WebSocket 推送进度(用户): userId={}, resumeId={}, progress={}%", 
                userId, resumeId, message.getProgress());
    }

    /**
     * 开始分析
     */
    public void startAnalysis(Long resumeId) {
        activeAnalysis.put(resumeId, true);
        sendProgress(resumeId, AnalysisProgressMessage.init(resumeId));
    }

    /**
     * 更新解析进度
     */
    public void updateParsingProgress(Long resumeId, int progress) {
        if (isActive(resumeId)) {
            sendProgress(resumeId, AnalysisProgressMessage.parsing(resumeId, progress));
        }
    }

    /**
     * 更新AI分析进度
     */
    public void updateAiAnalysisProgress(Long resumeId, int progress) {
        if (isActive(resumeId)) {
            sendProgress(resumeId, AnalysisProgressMessage.aiAnalysis(resumeId, progress));
        }
    }

    /**
     * 更新匹配进度
     */
    public void updateMatchingProgress(Long resumeId, int progress) {
        if (isActive(resumeId)) {
            sendProgress(resumeId, AnalysisProgressMessage.matching(resumeId, progress));
        }
    }

    /**
     * 完成分析
     */
    public void completeAnalysis(Long resumeId) {
        sendProgress(resumeId, AnalysisProgressMessage.completed(resumeId));
        activeAnalysis.remove(resumeId);
    }

    /**
     * 分析失败
     */
    public void failAnalysis(Long resumeId, String errorMessage) {
        sendProgress(resumeId, AnalysisProgressMessage.failed(resumeId, errorMessage));
        activeAnalysis.remove(resumeId);
    }

    /**
     * 检查分析是否活跃
     */
    public boolean isActive(Long resumeId) {
        return activeAnalysis.containsKey(resumeId);
    }

    /**
     * 取消分析
     */
    public void cancelAnalysis(Long resumeId) {
        activeAnalysis.remove(resumeId);
        log.info("取消分析任务: resumeId={}", resumeId);
    }
}