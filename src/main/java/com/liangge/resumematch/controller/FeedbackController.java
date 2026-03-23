package com.liangge.resumematch.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.dto.FeedbackRequest;
import com.liangge.resumematch.entity.Feedback;
import com.liangge.resumematch.entity.User;
import com.liangge.resumematch.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public Result<Void> submit(@RequestBody FeedbackRequest request, Authentication authentication) {
        Long userId = getUserId(authentication);
        feedbackService.submitFeedback(userId, request.getContent(), request.getType());
        return Result.success("反馈提交成功");
    }

    @GetMapping("/list")
    public Result<Page<Feedback>> getUserFeedbacks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        return Result.success(feedbackService.getUserFeedbacks(userId, pageNum, pageSize));
    }

    // 管理员接口
    @GetMapping("/admin/list")
    public Result<Page<Feedback>> getAllFeedbacks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        return Result.success(feedbackService.getAllFeedbacks(pageNum, pageSize, status));
    }

    @PutMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @RequestParam String reply) {
        feedbackService.replyFeedback(id, reply);
        return Result.success("回复成功");
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        feedbackService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 从SecurityContext获取当前用户ID
     */
    private Long getUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("用户未登录");
        }
        
        if (authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        
        throw new RuntimeException("无效的用户信息");
    }
}
