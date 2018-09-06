package com.juyoufuli.entity.bean;

import java.util.Date;
import java.util.List;

public class StaffInfoLv1Bean {

    /**
     * staff_id:主键
     */
    private Integer staffId;

    /**
     * user_name:用户名
     */
    private String userName;

    /**
     * avatar:头像
     */
    private String avatar;

    /**
     * biz_state:业务状态：1、在职；2、离职；3、停职；
     */
    private Byte bizState;

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Byte getBizState() {
        return bizState;
    }

    public void setBizState(Byte bizState) {
        this.bizState = bizState;
    }
}
