package com.juyoufuli.entity.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.juyoufuli.common.lang.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AddStaffBean implements Serializable {

    private String u;

    /**
     * user_name:用户名
     */
    private String userName;

    /**
     * gender:性别；0：未知；1：男；2：女；
     */
    private Byte gender;

    /**
     * avatar:头像
     */
    private String avatar;

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
    private Long[] groupIds;

    /**
     * duty:职务
     */
    private String duty;

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
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

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
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

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public Long[] getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Long[] groupIds) {
        this.groupIds = groupIds;
    }
}
