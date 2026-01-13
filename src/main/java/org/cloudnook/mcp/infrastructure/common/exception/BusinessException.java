package org.cloudnook.mcp.infrastructure.common.exception;

import lombok.Getter;

/**
 * 业务异常
 * 所有业务异常统一使用此类
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码（可选，默认为 500）
     */
    private final String code;

    /**
     * 错误信息
     */
    private final String message;

    public BusinessException(String message) {
        this("500", message);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        this("500", message, cause);
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
