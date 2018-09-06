package com.juyoufuli.common.exception;

import com.juyoufuli.common.config.BusinessEnum;

/**
 * 参数校验异常
 */
public class CheckedException extends ServiceException {


    private static final long serialVersionUID = 6890670984732367724L;

    /**
     * Instantiates a new  exception.
     */
    public CheckedException() {
    }

    /**
     * Instantiates a new  exception.
     *
     * @param code      the code
     * @param msgFormat the msg format
     * @param args      the args
     */
    public CheckedException(int code, String msgFormat, Object... args) {
        super(code, msgFormat, args);
    }

    /**
     * Instantiates a new exception.
     *
     * @param code the code
     * @param msg  the msg
     */
    public CheckedException(int code, String msg) {
        super(code, msg);
    }

    /**
     * Instantiates a new  exception.
     *
     * @param codeEnum the code enum
     */
    public CheckedException(BusinessEnum codeEnum) {
        super(codeEnum.getCode(), codeEnum.getMsg());
    }

    /**
     * Instantiates a new  exception.
     *
     * @param codeEnum the code enum
     * @param args     the args
     */
    public CheckedException(BusinessEnum codeEnum, Object... args) {
        super(codeEnum, args);
    }
}
