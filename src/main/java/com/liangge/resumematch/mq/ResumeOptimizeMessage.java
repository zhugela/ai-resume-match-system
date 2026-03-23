package com.liangge.resumematch.mq;

import lombok.Data;
import java.io.Serializable;

/**
 * 简历优化消息
 */
@Data
public class ResumeOptimizeMessage implements Serializable {
    private Long taskId;
    private Long resumeId;
    private Long userId;
    private String jobDescription;
    private Long targetJobId;
}
