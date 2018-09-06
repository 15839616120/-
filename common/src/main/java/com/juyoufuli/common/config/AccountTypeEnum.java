package com.juyoufuli.common.config;

public enum AccountTypeEnum {

    ACCOUNT(1,"帐号"),
    EMAIL(2,"邮箱"),
    PHONE(3,"手机号");


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private int code;
    private String desc;

    AccountTypeEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

}
