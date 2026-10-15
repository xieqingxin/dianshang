package com.ycyx.shop.common.exception;

import com.ycyx.shop.common.result.Result;
import com.ycyx.shop.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：统一捕获异常，禁止业务代码散落 try-catch
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.failed(e.getCode(), e.getMessage());
    }

    /** 参数校验异常（@RequestBody） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ResultCode.PARAM_ERROR.getMessage());
        return Result.failed(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /** 参数绑定异常（表单/Query） */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ResultCode.PARAM_ERROR.getMessage());
        return Result.failed(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /** 权限不足 */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Result.failed(ResultCode.FORBIDDEN);
    }

    /** 兜底异常 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 打印完整堆栈，便于排查
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.failed(ResultCode.FAILED);
    }
}