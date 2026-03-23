package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.entity.OperationLog;
import com.liangge.resumematch.mapper.OperationLogMapper;
import com.liangge.resumematch.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    @Async
    public void logOperation(OperationLog log) {
        try {
            operationLogMapper.insert(log);
        } catch (Exception e) {
            // 日志记录失败不应影响主流程
            System.err.println("操作日志记录失败: " + e.getMessage());
        }
    }

    @Override
    public List<OperationLog> getLogs(Long userId, String module, Integer page, Integer pageSize) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(OperationLog::getUserId, userId);
        }
        if (module != null && !module.isEmpty()) {
            wrapper.eq(OperationLog::getModule, module);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        
        Page<OperationLog> pageObj = new Page<>(page, pageSize);
        Page<OperationLog> result = operationLogMapper.selectPage(pageObj, wrapper);
        return result.getRecords();
    }
}
