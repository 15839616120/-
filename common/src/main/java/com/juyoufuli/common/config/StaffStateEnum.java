package com.juyoufuli.common.config;

public enum StaffStateEnum {

    BEON(1, "在职"),
    LEAVE(2,"离职"),
    BANDH(3,"停职");

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

    StaffStateEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static boolean isExists(int code){
        for (StaffStateEnum item: StaffStateEnum.values()){
            if (item.getCode() == code){
                return true;
            }
        }
        return false;
    }
}
