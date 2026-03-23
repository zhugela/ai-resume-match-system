package com.liangge.resumematch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${websocket.path:/ws/progress}")
    private String websocketPath;

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单的内存消息代理
        // 客户端订阅 /topic/xxx 接收广播消息
        // 客户端订阅 /user/queue/xxx 接收点对点消息
        registry.enableSimpleBroker("/topic", "/queue");
        
        // 客户端发送消息的前缀
        registry.setApplicationDestinationPrefixes("/app");
        
        // 用户消息前缀
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 注册 STOMP 端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(websocketPath)
                .setAllowedOriginPatterns(allowedOrigins.split(","))
                .withSockJS(); // 启用 SockJS 降级
    }
}