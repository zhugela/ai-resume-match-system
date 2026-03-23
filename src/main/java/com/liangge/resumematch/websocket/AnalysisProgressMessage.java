package com.liangge.resumematch.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分析进度消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisProgressMessage {
    /**
     * 简历ID
     */
    private Long resumeId;

    /**
     * 进度百分比 (0-100)
     */
    private Integer progress;

    /**
     * 当前阶段
     */
    private String stage;

    /**
     * 状态描述
     */
    private String message;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 预定义阶段常量
     */
    public static final String STAGE_INIT = "INIT";
    public static final String STAGE_PARSING = "PARSING";
    public static final String STAGE_AI_ANALYSIS = "AI_ANALYSIS";
    public static final String STAGE_MATCHING = "MATCHING";
    public static final String STAGE_COMPLETED = "COMPLETED";
    public static final String STAGE_FAILED = "FAILED";

    /**
     * 创建初始进度消息
     */
    public static AnalysisProgressMessage init(Long resumeId) {
        return AnalysisProgressMessage.builder()
                .resumeId(resumeId)
                .progress(0)
                .stage(STAGE_INIT)
                .message("开始分析...")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建解析进度消息
     */
    public static AnalysisProgressMessage parsing(Long resumeId, int progress) {
        return AnalysisProgressMessage.builder()
                .resumeId(resumeId)
                .progress(progress)
                .stage(STAGE_PARSING)
                .message("正在解析简历内容...")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建AI分析进度消息
     */
    public static AnalysisProgressMessage aiAnalysis(Long resumeId, int progress) {
        return AnalysisProgressMessage.builder()
                .resumeId(resumeId)
                .progress(progress)
                .stage(STAGE_AI_ANALYSIS)
                .message("AI正在分析简历...")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建匹配进度消息
     */
    public static AnalysisProgressMessage matching(Long resumeId, int progress) {
        return AnalysisProgressMessage.builder()
                .resumeId(resumeId)
                .progress(progress)
                .stage(STAGE_MATCHING)
                .message("正在匹配岗位...")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建完成进度消息
     */
    public static AnalysisProgressMessage completed(Long resumeId) {
        return AnalysisProgressMessage.builder()
                .resumeId(resumeId)
                .progress(100)
                .stage(STAGE_COMPLETED)
                .message("分析完成！")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建失败进度消息
     */
    public static AnalysisProgressMessage failed(Long resumeId, String errorMessage) {
        return AnalysisProgressMessage.builder()
                .resumeId(resumeId)
                .progress(-1)
                .stage(STAGE_FAILED)
                .message("分析失败: " + errorMessage)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}