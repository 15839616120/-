package com.juyoufuli.common.exception;

import com.juyoufuli.common.config.BusinessEnum;

/**
 * 业务异常.
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 4804162009028672009L;
    /**
     * 异常码
     */
    protected int code;


    public ServiceException() {
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(int code, String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
        this.code = code;
    }

    public ServiceException(BusinessEnum codeEnum, Object... args) {
        super(String.format(codeEnum.getMsg(), args));
        this.code = codeEnum.getCode();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
