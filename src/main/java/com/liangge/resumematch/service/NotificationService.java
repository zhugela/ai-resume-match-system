package com.liangge.resumematch.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.entity.Notification;
import java.util.List;

public interface NotificationService {
    /** 发送通知 */
    void sendNotification(Long userId, String type, String title, String content);
    /** 获取通知列表 */
    Page<Notification> getNotifications(Long userId, Integer pageNum, Integer pageSize);
    /** 标记已读（带用户权限校验） */
    void markAsRead(Long notificationId, Long userId);
    /** 标记全部已读 */
    void markAllAsRead(Long userId);
    /** 获取未读数量 */
    Long getUnreadCount(Long userId);
    /** 删除通知（带用户权限校验） */
    void deleteNotification(Long notificationId, Long userId);
}
