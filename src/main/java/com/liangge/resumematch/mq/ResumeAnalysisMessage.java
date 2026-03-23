package com.liangge.resumematch.mq;

import lombok.Data;
import java.io.Serializable;

/**
 * 简历分析消息
 */
@Data
public class ResumeAnalysisMessage implements Serializable {
    private Long taskId;
    private Long resumeId;
    private Long userId;
    private String filePath;
}
