package com.liangge.resumematch.mq;

import com.liangge.resumematch.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 简历消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.rabbitmq.host")
public class ResumeMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送简历分析消息
     */
    public void sendAnalysisMessage(ResumeAnalysisMessage message) {
        log.info("发送分析消息: resumeId={}", message.getResumeId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_RESUME,
            RabbitMQConfig.ROUTING_ANALYSIS,
            message
        );
    }

    /**
     * 发送简历优化消息
     */
    public void sendOptimizeMessage(ResumeOptimizeMessage message) {
        log.info("发送优化消息: resumeId={}", message.getResumeId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_RESUME,
            RabbitMQConfig.ROUTING_OPTIMIZE,
            message
        );
    }

    /**
     * 发送导出消息
     */
    public void sendExportMessage(ResumeExportMessage message) {
        log.info("发送导出消息: resumeId={}", message.getResumeId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_RESUME,
            RabbitMQConfig.ROUTING_EXPORT,
            message
        );
    }

    /**
     * 发送通知消息
     */
    public void sendNotifyMessage(String message) {
        log.info("发送通知消息: {}", message);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_RESUME,
            RabbitMQConfig.ROUTING_NOTIFY,
            message
        );
    }
}
