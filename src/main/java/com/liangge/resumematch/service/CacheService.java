package com.liangge.resumematch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务 - Caffeine 本地缓存 + Redis 二级缓存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    // ==================== 缓存键前缀 ====================
    private static final String PREFIX_RESUME_ANALYSIS = "resume:analysis:";
    private static final String PREFIX_JOB_MATCH = "job:match:";
    private static final String PREFIX_JOB_TEMPLATE = "job:template:";
    private static final String PREFIX_AI_RESULT = "ai:result:";
    private static final String CACHE_NAME = "resume-cache";

    // ==================== 通用缓存操作 ====================

    /**
     * 获取缓存值（先从本地缓存获取，失败则从 Redis 获取）
     */
    public <T> T get(String key, Class<T> type) {
        try {
            // 1. 先从 Caffeine 本地缓存获取
            Cache cache = cacheManager.getCache(CACHE_NAME);
            if (cache != null) {
                T localValue = cache.get(key, type);
                if (localValue != null) {
                    log.debug("本地缓存命中: {}", key);
                    return localValue;
                }
            }

            // 2. 本地缓存未命中，从 Redis 获取
            Object redisValue = redisTemplate.opsForValue().get(key);
            if (redisValue != null) {
                log.debug("Redis缓存命中: {}", key);
                // 回填到本地缓存
                if (cache != null) {
                    cache.put(key, redisValue);
                }
                return type.cast(redisValue);
            }
        } catch (Exception e) {
            log.warn("缓存读取失败: {}, 原因: {}", key, e.getMessage());
        }
        return null;
    }

    /**
     * 获取缓存值（支持 Callable 回调）
     */
    public <T> T get(String key, Class<T> type, Callable<T> loader) {
        T value = get(key, type);
        if (value == null) {
            try {
                value = loader.call();
                if (value != null) {
                    put(key, value);
                }
            } catch (Exception e) {
                log.error("缓存加载失败: {}", key, e);
            }
        }
        return value;
    }

    /**
     * 设置缓存值（同时写入本地缓存和 Redis）
     */
    public void put(String key, Object value) {
        try {
            // 1. 写入 Redis（1小时过期）
            redisTemplate.opsForValue().set(key, value, 1, TimeUnit.HOURS);

            // 2. 写入本地缓存
            Cache cache = cacheManager.getCache(CACHE_NAME);
            if (cache != null) {
                cache.put(key, value);
            }

            log.debug("缓存写入: {}", key);
        } catch (Exception e) {
            log.warn("缓存写入失败: {}, 原因: {}", key, e.getMessage());
        }
    }

    /**
     * 设置缓存值，指定过期时间
     */
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        try {
            // 写入 Redis
            redisTemplate.opsForValue().set(key, value, timeout, unit);

            // 写入本地缓存
            Cache cache = cacheManager.getCache(CACHE_NAME);
            if (cache != null) {
                cache.put(key, value);
            }

            log.debug("缓存写入(过期{} {}): {}", timeout, unit, key);
        } catch (Exception e) {
            log.warn("缓存写入失败: {}, 原因: {}", key, e.getMessage());
        }
    }

    /**
     * 删除缓存
     */
    public void evict(String key) {
        try {
            // 删除 Redis
            redisTemplate.delete(key);

            // 删除本地缓存
            Cache cache = cacheManager.getCache(CACHE_NAME);
            if (cache != null) {
                cache.evict(key);
            }

            log.debug("缓存删除: {}", key);
        } catch (Exception e) {
            log.warn("缓存删除失败: {}, 原因: {}", key, e.getMessage());
        }
    }

    /**
     * 清空所有缓存
     */
    public void clearAll() {
        try {
            // 清空 Redis（保留前缀）
            redisTemplate.delete(redisTemplate.keys("*"));

            // 清空本地缓存
            Cache cache = cacheManager.getCache(CACHE_NAME);
            if (cache != null) {
                cache.clear();
            }

            log.info("清空所有缓存");
        } catch (Exception e) {
            log.error("清空缓存失败", e);
        }
    }

    // ==================== 简历分析缓存 ====================

    /**
     * 缓存简历分析结果
     */
    public void cacheResumeAnalysis(Long resumeId, Object result) {
        String key = PREFIX_RESUME_ANALYSIS + resumeId;
        put(key, result);
    }

    /**
     * 获取缓存的简历分析结果
     */
    public Object getResumeAnalysis(Long resumeId) {
        String key = PREFIX_RESUME_ANALYSIS + resumeId;
        return get(key, Object.class);
    }

    /**
     * 删除简历缓存
     */
    public void deleteResumeCache(Long resumeId) {
        evict(PREFIX_RESUME_ANALYSIS + resumeId);
    }

    // ==================== 岗位匹配缓存 ====================

    /**
     * 缓存岗位匹配结果
     */
    public void cacheJobMatch(Long resumeId, String jobDescriptionHash, Object result) {
        String key = PREFIX_JOB_MATCH + resumeId + ":" + jobDescriptionHash;
        put(key, result, 30, TimeUnit.MINUTES);
    }

    /**
     * 获取缓存的岗位匹配结果
     */
    public Object getJobMatch(Long resumeId, String jobDescriptionHash) {
        String key = PREFIX_JOB_MATCH + resumeId + ":" + jobDescriptionHash;
        return get(key, Object.class);
    }

    // ==================== 岗位模板缓存 ====================

    /**
     * 缓存岗位模板
     */
    public void cacheJobTemplate(String templateName, Object template) {
        String key = PREFIX_JOB_TEMPLATE + templateName;
        put(key, template, 24, TimeUnit.HOURS);
    }

    /**
     * 获取缓存的岗位模板
     */
    public Object getJobTemplate(String templateName) {
        String key = PREFIX_JOB_TEMPLATE + templateName;
        return get(key, Object.class);
    }

    // ==================== 通用方法 ====================

    /**
     * 生成 JD 的简单 hash（用于缓存 key）
     */
    public String generateJDHash(String jobDescription) {
        return String.valueOf(jobDescription.hashCode());
    }
}
