package com.juyoufuli.common.exception;

import com.juyoufuli.common.config.BusinessEnum;

/**
 * 令牌异常
 */
public class TokenException extends ServiceException {

    private static final long serialVersionUID = -5671776901353973487L;

    /**
     * Instantiates a new  exception.
     */
    public TokenException() {
    }

    /**
     * Instantiates a new  exception.
     *
     * @param code      the code
     * @param msgFormat the msg format
     * @param args      the args
     */
    public TokenException(int code, String msgFormat, Object... args) {
        super(code, msgFormat, args);
    }

    /**
     * Instantiates a new exception.
     *
     * @param code the code
     * @param msg  the msg
     */
    public TokenException(int code, String msg) {
        super(code, msg);
    }

    /**
     * Instantiates a new  exception.
     *
     * @param codeEnum the code enum
     */
    public TokenException(BusinessEnum codeEnum) {
        super(codeEnum.getCode(), codeEnum.getMsg());
    }

    /**
     * Instantiates a new  exception.
     *
     * @param codeEnum the code enum
     * @param args     the args
     */
    public TokenException(BusinessEnum codeEnum, Object... args) {
        super(codeEnum, args);
    }
}
