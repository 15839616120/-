package com.juyoufuli.entity.bean;

import java.util.Date;

public class UpdateStaffBean {

    private String u;

    /**
     * 员工Id
     */
    private Integer staffId;

    /**
     * avatar:头像
     */
    private String avatar;

    /**
     * birthday:生日
     */
    private String birthday;

    /**
     * mobile_phone:手机号
     */
    private String mobilePhone;

    /**
     * telephone:座机
     */
    private String telephone;

    /**
     * email:企业邮箱
     */
    private String email;

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
