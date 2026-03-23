package com.liangge.resumematch.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.dto.JobPublishRequest;
import com.liangge.resumematch.entity.Job;
import com.liangge.resumematch.entity.User;
import com.liangge.resumematch.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public Result<Job> publish(@RequestBody JobPublishRequest request, Authentication authentication) {
        Long hrId = getCurrentUserId(authentication);
        return Result.success(jobService.publish(hrId, request));
    }

    @GetMapping("/list")
    public Result<Page<Job>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location) {
        return Result.success(jobService.list(pageNum, pageSize, keyword, location));
    }

    @GetMapping("/{id}")
    public Result<Job> getById(@PathVariable Long id) {
        return Result.success(jobService.getById(id));
    }

    @PutMapping("/{id}")
    public Result<Job> update(@PathVariable Long id, @RequestBody JobPublishRequest request, Authentication authentication) {
        Long hrId = getCurrentUserId(authentication);
        return Result.success(jobService.update(id, hrId, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        Long hrId = getCurrentUserId(authentication);
        jobService.delete(id, hrId);
        return Result.success();
    }

    /**
     * 从SecurityContext获取当前用户ID
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("用户未登录");
        }
        
        if (authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        
        throw new RuntimeException("无效的用户信息");
    }
}
