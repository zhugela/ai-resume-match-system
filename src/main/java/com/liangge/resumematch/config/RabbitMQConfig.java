package com.liangge.resumematch.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置
 */
@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.host")
public class RabbitMQConfig {

    // 交换机
    public static final String EXCHANGE_RESUME = "resume.exchange";
    
    // 队列
    public static final String QUEUE_ANALYSIS = "resume.analysis.queue";
    public static final String QUEUE_OPTIMIZE = "resume.optimize.queue";
    public static final String QUEUE_EXPORT = "resume.export.queue";
    public static final String QUEUE_NOTIFY = "resume.notify.queue";
    
    // 路由键
    public static final String ROUTING_ANALYSIS = "resume.analysis";
    public static final String ROUTING_OPTIMIZE = "resume.optimize";
    public static final String ROUTING_EXPORT = "resume.export";
    public static final String ROUTING_NOTIFY = "resume.notify";

    /**
     * 交换机
     */
    @Bean
    public DirectExchange resumeExchange() {
        return new DirectExchange(EXCHANGE_RESUME);
    }

    /**
     * 简历分析队列
     */
    @Bean
    public Queue analysisQueue() {
        return QueueBuilder.durable(QUEUE_ANALYSIS)
                .withArgument("x-dead-letter-exchange", EXCHANGE_RESUME + ".dlx")
                .withArgument("x-dead-letter-routing-key", "resume.analysis.dlq")
                .build();
    }

    /**
     * 简历优化队列
     */
    @Bean
    public Queue optimizeQueue() {
        return QueueBuilder.durable(QUEUE_OPTIMIZE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_RESUME + ".dlx")
                .withArgument("x-dead-letter-routing-key", "resume.optimize.dlq")
                .build();
    }

    /**
     * 导出报告队列
     */
    @Bean
    public Queue exportQueue() {
        return QueueBuilder.durable(QUEUE_EXPORT)
                .withArgument("x-dead-letter-exchange", EXCHANGE_RESUME + ".dlx")
                .withArgument("x-dead-letter-routing-key", "resume.export.dlq")
                .build();
    }

    /**
     * 通知队列
     */
    @Bean
    public Queue notifyQueue() {
        return QueueBuilder.durable(QUEUE_NOTIFY).build();
    }

    /**
     * 绑定分析队列
     */
    @Bean
    public Binding analysisBinding() {
        return BindingBuilder.bind(analysisQueue()).to(resumeExchange()).with(ROUTING_ANALYSIS);
    }

    /**
     * 绑定优化队列
     */
    @Bean
    public Binding optimizeBinding() {
        return BindingBuilder.bind(optimizeQueue()).to(resumeExchange()).with(ROUTING_OPTIMIZE);
    }

    /**
     * 绑定导出队列
     */
    @Bean
    public Binding exportBinding() {
        return BindingBuilder.bind(exportQueue()).to(resumeExchange()).with(ROUTING_EXPORT);
    }

    /**
     * 绑定通知队列
     */
    @Bean
    public Binding notifyBinding() {
        return BindingBuilder.bind(notifyQueue()).to(resumeExchange()).with(ROUTING_NOTIFY);
    }

    /**
     * JSON 消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
