package com.juyoufuli.common.config;

public enum GenderEnum {

    MAN(0,"男"),
    WOMAN(1,"女"),
    UN(2,"未知");

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

    GenderEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public static boolean isExists(int code){
        for (GenderEnum item: GenderEnum.values()){
            if (item.getCode() == code){
                return true;
            }
        }
        return false;
    }

    public static boolean isExists(String desc){
        for (GenderEnum item: GenderEnum.values()){
            if (item.getDesc().equals(desc)){
                return true;
            }
        }
        return false;
    }

    public static GenderEnum getGender(String desc){
        switch (desc){
            case "男":
                return MAN;
            case "女":
                return WOMAN;
            case "未知":
                return UN;
            default:
                return null;
        }
    }
}
