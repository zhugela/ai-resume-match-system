package com.liangge.resumematch.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liangge.resumematch.entity.OperationLog;
import com.liangge.resumematch.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogInterceptor implements HandlerInterceptor {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;

    // 需要记录日志的模块
    private static final List<String> LOGGED_MODULES = Arrays.asList(
        "/api/job", "/api/resume", "/api/favorite", "/api/notification",
        "/api/feedback", "/api/analysis", "/api/match"
    );

    // 排除的路径
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/auth/login", "/api/auth/register", "/api/statistics",
        "/api/job/list", "/api/job/"  // GET请求的列表和详情
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // do nothing
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            String uri = request.getRequestURI();
            
            // 检查是否需要记录
            boolean shouldLog = LOGGED_MODULES.stream().anyMatch(uri::startsWith);
            boolean isExcluded = EXCLUDED_PATHS.stream().anyMatch(uri::startsWith);
            boolean isGet = "GET".equalsIgnoreCase(request.getMethod());
            
            if (!shouldLog || isExcluded || isGet) {
                return;
            }

            Long startTime = (Long) request.getAttribute("startTime");
            long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

            OperationLog logEntry = new OperationLog();
            logEntry.setUserId(1L); // TODO: 从认证获取
            logEntry.setUsername("user");
            logEntry.setModule(extractModule(uri));
            logEntry.setOperation(extractOperation(request.getMethod(), uri));
            logEntry.setMethod(request.getMethod());
            logEntry.setUrl(uri);
            logEntry.setParams(request.getQueryString());
            logEntry.setIp(getClientIp(request));
            logEntry.setUserAgent(request.getHeader("User-Agent"));
            logEntry.setDuration(duration);
            logEntry.setStatus(ex == null ? "SUCCESS" : "FAIL");
            logEntry.setErrorMessage(ex != null ? ex.getMessage() : null);
            logEntry.setCreateTime(LocalDateTime.now());

            operationLogService.logOperation(logEntry);
        } catch (Exception e) {
            log.error("操作日志记录失败", e);
        }
    }

    private String extractModule(String uri) {
        if (uri.startsWith("/api/job")) return "JOB";
        if (uri.startsWith("/api/resume")) return "RESUME";
        if (uri.startsWith("/api/favorite")) return "FAVORITE";
        if (uri.startsWith("/api/notification")) return "NOTIFICATION";
        if (uri.startsWith("/api/feedback")) return "FEEDBACK";
        if (uri.startsWith("/api/analysis")) return "ANALYSIS";
        if (uri.startsWith("/api/match")) return "MATCH";
        return "OTHER";
    }

    private String extractOperation(String method, String uri) {
        if ("POST".equalsIgnoreCase(method)) return "CREATE";
        if ("PUT".equalsIgnoreCase(method)) return "UPDATE";
        if ("DELETE".equalsIgnoreCase(method)) return "DELETE";
        if ("GET".equalsIgnoreCase(method)) return "QUERY";
        return "OTHER";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
