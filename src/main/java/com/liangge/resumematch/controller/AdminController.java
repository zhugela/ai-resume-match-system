package com.liangge.resumematch.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.entity.AiCallLog;
import com.liangge.resumematch.entity.AsyncTask;
import com.liangge.resumematch.entity.User;
import com.liangge.resumematch.mapper.AiCallLogMapper;
import com.liangge.resumematch.mapper.AsyncTaskMapper;
import com.liangge.resumematch.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员接口
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final UserMapper userMapper;
    private final AiCallLogMapper aiCallLogMapper;
    private final AsyncTaskMapper asyncTaskMapper;

    /**
     * 获取系统统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户总数
        stats.put("totalUsers", userMapper.selectCount(null));
        
        // 今日活跃用户
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        stats.put("todayActiveUsers", userMapper.selectCount(
            new LambdaQueryWrapper<User>().ge(User::getCreatedAt, startOfDay)
        ));
        
        // AI 调用总数
        stats.put("totalAiCalls", aiCallLogMapper.selectCount(null));
        
        // 今日 AI 调用
        stats.put("todayAiCalls", aiCallLogMapper.selectCount(
            new LambdaQueryWrapper<AiCallLog>().ge(AiCallLog::getCreatedAt, startOfDay)
        ));
        
        // 失败调用数
        stats.put("failedAiCalls", aiCallLogMapper.selectCount(
            new LambdaQueryWrapper<AiCallLog>().eq(AiCallLog::getStatus, "FAILED")
        ));
        
        // 任务总数
        stats.put("totalTasks", asyncTaskMapper.selectCount(null));
        
        return Result.success(stats);
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public Result<List<User>> getUsers() {
        return Result.success(userMapper.selectList(null));
    }

    /**
     * 获取 AI 调用日志
     */
    @GetMapping("/ai-logs")
    public Result<List<AiCallLog>> getAiLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "100") int limit) {
        
        LambdaQueryWrapper<AiCallLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(AiCallLog::getUserId, userId);
        }
        wrapper.orderByDesc(AiCallLog::getCreatedAt).last("LIMIT " + limit);
        
        return Result.success(aiCallLogMapper.selectList(wrapper));
    }

    /**
     * 获取任务列表
     */
    @GetMapping("/tasks")
    public Result<List<AsyncTask>> getTasks(
            @RequestParam(required = false) String status) {
        
        LambdaQueryWrapper<AsyncTask> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(AsyncTask::getStatus, status);
        }
        wrapper.orderByDesc(AsyncTask::getCreatedAt).last("LIMIT 100");
        
        return Result.success(asyncTaskMapper.selectList(wrapper));
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/users/{userId}/role")
    public Result<Void> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setRole(role);
        userMapper.updateById(user);
        
        return Result.success();
    }

    /**
     * 获取 AI 模型配置
     */
    @GetMapping("/ai-config")
    public Result<Map<String, String>> getAiConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("provider", "minimax");
        config.put("model", "Minimax M2.5");
        // 注意：不要返回真实的 API Key
        return Result.success(config);
    }
}
