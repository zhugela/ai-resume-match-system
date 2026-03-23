package com.liangge.resumematch.service;

import com.liangge.resumematch.entity.OperationLog;
import java.util.List;

public interface OperationLogService {
    /** 记录操作日志 */
    void logOperation(OperationLog log);
    /** 获取操作日志列表 */
    List<OperationLog> getLogs(Long userId, String module, Integer page, Integer pageSize);
}
