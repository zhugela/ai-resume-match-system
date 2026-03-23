package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liangge.resumematch.common.BusinessException;
import com.liangge.resumematch.entity.Feedback;
import com.liangge.resumematch.mapper.FeedbackMapper;
import com.liangge.resumematch.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;

    @Override
    @Transactional
    public void submitFeedback(Long userId, String content, String type) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setContent(content);
        feedback.setType(type);
        feedback.setStatus("PENDING");
        feedbackMapper.insert(feedback);
    }

    @Override
    public Page<Feedback> getUserFeedbacks(Long userId, Integer pageNum, Integer pageSize) {
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, userId)
                .orderByDesc(Feedback::getCreateTime);
        return feedbackMapper.selectPage(page, wrapper);
    }

    @Override
    public Page<Feedback> getAllFeedbacks(Integer pageNum, Integer pageSize, String status) {
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.orderByDesc(Feedback::getCreateTime);
        return feedbackMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void replyFeedback(Long feedbackId, String reply) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BusinessException("反馈不存在");
        }
        feedback.setReply(reply);
        feedback.setStatus("RESOLVED");
        feedbackMapper.updateById(feedback);
    }

    @Override
    @Transactional
    public void updateStatus(Long feedbackId, String status) {
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) {
            throw new BusinessException("反馈不存在");
        }
        feedback.setStatus(status);
        feedbackMapper.updateById(feedback);
    }
}
