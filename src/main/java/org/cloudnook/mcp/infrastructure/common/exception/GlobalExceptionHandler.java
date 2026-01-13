package org.cloudnook.mcp.infrastructure.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.infrastructure.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常: code={}, message={}", e.getCode(), e.getMessage());

        Result<Void> result = Result.<Void>error(e.getCode(), e.getMessage())
                .withTraceId(getTraceId());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler({BindException.class, IllegalArgumentException.class})
    public ResponseEntity<Result<Void>> handleValidateException(Exception e, HttpServletRequest request) {
        log.error("参数校验异常: message={}", e.getMessage());

        Result<Void> result = Result.<Void>error("400", "参数校验失败: " + e.getMessage())
                .withTraceId(getTraceId());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: message={}", e.getMessage(), e);

        Result<Void> result = Result.<Void>error("500", "系统异常: " + e.getMessage())
                .withTraceId(getTraceId());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 获取 traceId（从请求头或 MDC 中获取）
     */
    private String getTraceId() {
        // TODO: 从请求头或 MDC 中获取 traceId
        // 暂时返回时间戳代替
        return String.valueOf(System.currentTimeMillis());
    }
}
