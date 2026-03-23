package com.liangge.resumematch.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.entity.Feedback;

public interface FeedbackService {
    /** 提交反馈 */
    void submitFeedback(Long userId, String content, String type);
    /** 获取用户反馈列表 */
    Page<Feedback> getUserFeedbacks(Long userId, Integer pageNum, Integer pageSize);
    /** 获取所有反馈（管理员） */
    Page<Feedback> getAllFeedbacks(Integer pageNum, Integer pageSize, String status);
    /** 回复反馈 */
    void replyFeedback(Long feedbackId, String reply);
    /** 更新反馈状态 */
    void updateStatus(Long feedbackId, String status);
}
