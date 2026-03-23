package com.liangge.resumematch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangge.resumematch.entity.ExportRecord;

import java.util.List;

public interface ExportService extends IService<ExportRecord> {
    
    /**
     * 导出简历对比报告
     */
    ExportRecord exportComparison(Long resumeId, Long versionId, String exportType);
    
    /**
     * 获取简历的导出记录
     */
    List<ExportRecord> getExportsByResumeId(Long resumeId);
}
