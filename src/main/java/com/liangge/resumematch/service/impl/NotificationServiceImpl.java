package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.entity.Notification;
import com.liangge.resumematch.mapper.NotificationMapper;
import com.liangge.resumematch.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public void sendNotification(Long userId, String type, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    @Override
    public Page<Notification> getNotifications(Long userId, Integer pageNum, Integer pageSize) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreateTime);
        return notificationMapper.selectPage(page, wrapper);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification != null) {
            // 权限校验：只能标记自己的通知
            if (!notification.getUserId().equals(userId)) {
                throw new RuntimeException("无权限操作");
            }
            notification.setIsRead(1);
            notificationMapper.updateById(notification);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        List<Notification> unreadList = notificationMapper.selectList(wrapper);
        unreadList.forEach(n -> {
            n.setIsRead(1);
            notificationMapper.updateById(n);
        });
    }

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        return notificationMapper.selectCount(wrapper);
    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification != null) {
            // 权限校验：只能删除自己的通知
            if (!notification.getUserId().equals(userId)) {
                throw new RuntimeException("无权限操作");
            }
            notificationMapper.deleteById(notificationId);
        }
    }
}
