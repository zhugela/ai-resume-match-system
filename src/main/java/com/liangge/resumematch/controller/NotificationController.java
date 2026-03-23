package com.liangge.resumematch.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.entity.Notification;
import com.liangge.resumematch.entity.User;
import com.liangge.resumematch.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public Result<Page<Notification>> getNotifications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {
        Long userId = getUserId(authentication);
        return Result.success(notificationService.getNotifications(userId, pageNum, pageSize));
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        notificationService.markAsRead(id, userId);
        return Result.success("已标记为已读");
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(Authentication authentication) {
        Long userId = getUserId(authentication);
        notificationService.markAllAsRead(userId);
        return Result.success("已全部标记为已读");
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(Authentication authentication) {
        Long userId = getUserId(authentication);
        return Result.success(notificationService.getUnreadCount(userId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        notificationService.deleteNotification(id, userId);
        return Result.success("删除成功");
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
