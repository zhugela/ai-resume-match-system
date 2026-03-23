package com.liangge.resumematch.aspect;

import com.liangge.resumematch.entity.AsyncTask;
import com.liangge.resumematch.service.AsyncTaskService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequestLogAspect {

    private final AsyncTaskService asyncTaskService;

    /**
     * 切入点：所有 Controller 的方法
     */
    @Pointcut("execution(* com.liangge.resumematch.controller..*.*(..))")
    public void controllerPointcut() {}

    /**
     * 记录请求日志
     */
    @Around("controllerPointcut()")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        String method = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        String uri = request != null ? request.getRequestURI() : "unknown";
        String ip = request != null ? getClientIp(request) : "unknown";
        
        log.info("==> 请求: {} {} - IP: {}", method, uri, ip);
        
        Object result = null;
        try {
            result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            
            log.info("<== 响应: {} - 耗时: {}ms", uri, costTime);
            
            // 记录 AI 调用耗时
            if (method.contains("AiAnalysis") || method.contains("Analysis")) {
                log.info("📊 AI调用耗时: {}ms - 方法: {}", costTime, method);
            }
            
            return result;
        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("<!> 异常: {} - 耗时: {}ms - 错误: {}", uri, costTime, e.getMessage());
            throw e;
        }
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
