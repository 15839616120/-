package com.juyoufuli.service.controller.advice;

import com.juyoufuli.common.exception.CheckedException;
import com.juyoufuli.common.exception.ServiceException;
import com.juyoufuli.common.exception.TokenException;
import com.juyoufuli.common.result.ResultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;
/**
 * 用于处理所有的异常
 */
@RestControllerAdvice
public class ExceptionAdvice {
    private static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultBean<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.error("缺少请求参数", e);
        return new ResultBean<>(-400, e.getMessage());
    }
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultBean<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("参数解析失败", e);
        return new ResultBean<>(-400, e.getMessage());
    }
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultBean<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("参数验证失败", e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        return new ResultBean<>(-400, message);
    }
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResultBean<String> handleBindException(BindException e) {
        logger.error("参数绑定失败", e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        return new ResultBean<>(-400, message);
    }
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResultBean<String> handleServiceException(ConstraintViolationException e) {
        logger.error("参数验证失败", e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        return new ResultBean<>(-400, message);
    }
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ResultBean<String> handleValidationException(ValidationException e) {
        logger.error("参数验证失败", e);
        return new ResultBean<>(-400, e.getMessage());
    }
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(CheckedException.class)
    public ResultBean<String> handleCheckException(CheckedException e) {
        logger.error("参数非法异常={}", e.getMessage(), e);
        return new ResultBean<>(e.getCode() == 0 ? -400 : e.getCode(), e.getMessage());
    }
    /**
     * 401 - Unauthorized
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(TokenException.class)
    public ResultBean<String> handleTokenException(TokenException e) {
        logger.error("令牌非法异常={}", e.getMessage(), e);
        return new ResultBean<>(e.getCode() == 0 ? -401 : e.getCode(), e.getMessage());
    }
    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultBean<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.error("不支持当前请求方法", e);
        return new ResultBean<>(-405, e.getMessage());
    }
    /**
     * 406 - Not Acceptable
     */
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResultBean<String> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        logger.error("不支持当前返回类型", e);
        return new ResultBean<>(-406, e.getMessage());
    }
    /**
     * 415 - Unsupported Media Type
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResultBean<String> handleHttpMediaTypeNotSupportedException(Exception e) {
        logger.error("不支持当前媒体类型", e);
        return new ResultBean<>(-415, e.getMessage());
    }
    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ServiceException.class)
    public ResultBean<String> handleServiceException(ServiceException e) {
        logger.error("业务异常={}", e.getMessage(), e);
        return new ResultBean<>(e.getCode() == 0 ? -500 : e.getCode(), e.getMessage());
    }
    /**
     * 500 - Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResultBean<String> handleException(Exception e) {
        logger.error("服务运行异常", e);
        return new ResultBean<>(-500, e.getMessage());
    }

}
