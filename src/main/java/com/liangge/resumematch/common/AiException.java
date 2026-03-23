package com.liangge.resumematch.common;

/**
 * AI 调用异常
 */
public class AiException extends RuntimeException {
    public AiException(String message) {
        super(message);
    }

    public AiException(String message, Throwable cause) {
        super(message, cause);
    }
}
