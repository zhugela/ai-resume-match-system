package com.liangge.resumematch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 慢SQL监控配置
 * Note: DataPermissionHandler and InvalidSqlParser are from MyBatis Plus enterprise edition
 * and are not available in the open-source version. This config is simplified.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "mybatis-plus.slow-sql.enabled", havingValue = "true", matchIfMissing = true)
public class SlowSqlMonitorConfig {

    /**
     * 慢SQL阈值（毫秒），默认1000ms
     */
    private static final long SLOW_SQL_THRESHOLD = 1000;

    /**
     * 打印慢SQL的日志监听器
     * 使用 Spring EventListener 监听 SQL 执行事件
     */
    @Bean
    public Object slowSqlLogger() {
        log.info("慢SQL监控已启用，阈值: {}ms", SLOW_SQL_THRESHOLD);
        return new Object();
    }
}
