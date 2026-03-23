package com.liangge.resumematch.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("参数校验失败: {} - {}", request.getRequestURI(), errors);
        return Result.error(400, "参数校验失败");
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("参数绑定失败: {} - {}", request.getRequestURI(), errors);
        return Result.error(400, "参数绑定失败");
    }

    /**
     * 处理AI调用异常
     */
    @ExceptionHandler(AiException.class)
    public Result<?> handleAiException(AiException e, HttpServletRequest request) {
        log.error("AI调用异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(500, "AI服务调用失败，请稍后重试");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数: {} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        
        // 根据不同错误返回不同消息
        String message = e.getMessage();
        if (message != null) {
            if (message.contains("用户未登录")) {
                return Result.error(401, "请先登录");
            } else if (message.contains("无权限")) {
                return Result.error(403, "权限不足");
            } else if (message.contains("不存在")) {
                return Result.error(404, message);
            }
        }
        
        return Result.error(500, "操作失败，请稍后重试");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(500, "系统异常，请稍后重试");
    }
}
