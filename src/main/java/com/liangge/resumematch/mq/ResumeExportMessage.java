package com.liangge.resumematch.mq;

import lombok.Data;
import java.io.Serializable;

/**
 * 简历导出消息
 */
@Data
public class ResumeExportMessage implements Serializable {
    private Long taskId;
    private Long resumeId;
    private Long versionId;
    private Long userId;
    private String exportType; // WORD, PDF
}
