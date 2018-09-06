package com.juyoufuli.common.result;

import com.juyoufuli.common.config.BusinessEnum;
import com.juyoufuli.common.lang.StringUtils;

import java.io.Serializable;

/**
 * <p>
 * Description: [统一返回对象包装类]
 * </p>
 * Created on 2017年9月21日
 * @author <a href="mailto: weiyong@huayingcul1.com">魏勇</a>
 */
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = -4865193979977575465L;


    public static final int SUCCESS = 0;

    public static final int UNKNOWN_EXCEPTION = -99;


    /**
     * 返回的信息
     */
    private String msg = "success";

    /**
     * 接口返回码, 0表示成功, 其他看对应的定义
     * 0   : 成功
     * >0 : 表示已知的异常
     * <0 : 表示未知的异常
     */
    private int code = SUCCESS;

    /**
     * 返回的数据
     */
    private T data;

    public ResultBean() {
        super();
    }

    public ResultBean(T data) {
        super();
        this.data = data;
    }

    public ResultBean(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public ResultBean(Throwable e) {
        super();
        this.msg = e.toString();
        this.code = UNKNOWN_EXCEPTION;
    }

    public ResultBean(BusinessEnum businessEnum) {
        super();
        this.code = businessEnum.getCode();
        this.msg = businessEnum.getMsg();
    }

    public ResultBean(BusinessEnum businessEnum, T data) {
        super();
        this.code = businessEnum.getCode();
        this.msg = businessEnum.getMsg();
        this.data = data;
    }

    public static <T> ResultBean<T> error() {
        return new ResultBean<>(BusinessEnum.INNER_EXCEPTION);
    }

    public static <T> ResultBean<T> error(String message) {
        return new ResultBean<>(BusinessEnum.INNER_EXCEPTION.getCode(), StringUtils.isBlank(message) ? BusinessEnum.INNER_EXCEPTION.getMsg() : message);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}