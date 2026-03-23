package com.liangge.resumematch.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置
 */
@Configuration
@EnableCaching
public class CaffeineConfig {

    /**
     * 本地缓存管理器（Caffeine）
     */
    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Caffeine 缓存配置
     */
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                // 最多缓存1000条
                .maximumSize(1000)
                // 写入后30分钟过期
                .expireAfterWrite(30, TimeUnit.MINUTES)
                // 记录缓存命中率
                .recordStats();
    }
}
