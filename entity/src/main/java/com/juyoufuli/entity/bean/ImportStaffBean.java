package com.juyoufuli.entity.bean;

import java.io.Serializable;

public class ImportStaffBean implements Serializable {

    /**
     * user_name:用户名
     */
    private String userName;

    /**
     * gender:性别；0：未知；1：男；2：女；
     */
    private String gender;

    /**
     * birthday:生日
     */
    private String birthday;

    /**
     * ID_num:身份证号
     */
    private String idNum;

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

    /**
     * groupIds:所属组
     */
    private String groups;

    /**
     * duty:职务
     */
    private String duty;

    public String getUserName() {
        return userName;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getIdNum() {
        return idNum;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getGroups() {
        return groups;
    }

    public String getDuty() {
        return duty;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }
}
