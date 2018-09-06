package com.juyoufuli.entity.bean;

import java.util.Date;
import java.util.List;

public class StaffBaseInfoBean {

    /**
     * staff_id:主键
     */
    private Integer staffId;

    /**
     * job_num:工号
     */
    private String jobNum;

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
    private Date birthday;

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
     * duty:职务
     */
    private String duty;

    /**
     * biz_state:业务状态：1、在职；2、离职；3、停职；
     */
    private Byte bizState;

    /**
     * 员工所属组
     */
    private List<Long> groupIds;

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
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

    public Byte getBizState() {
        return bizState;
    }

    public void setBizState(Byte bizState) {
        this.bizState = bizState;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }
}
