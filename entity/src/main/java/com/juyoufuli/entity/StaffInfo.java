package com.juyoufuli.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *  员工表
 * @table staff_info
 * @author  zhanghongwei
 * @date 2018-08-16 15:07:28
 */
public class StaffInfo implements Serializable {
    /**
     * staff_id:主键
     */
    private Integer staffId;

    /**
     * password:密码
     *             
     */
    private String password;

    /**
     * pwd_salt:
     */
    private String pwdSalt;

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
     * creator:创建人
     */
    private Integer creator;

    /**
     * modifier:修改人
     */
    private Integer modifier;

    /**
     * gmt_create:创建时间
     */
    private Date gmtCreate;

    /**
     * gmt_modified:更新时间
     */
    private Date gmtModified;

    /**
     * biz_state:业务状态：1、在职；2、离职；3、停职；
     */
    private Byte bizState;

    /**
     * state:状态
     */
    private Byte state;

    /**
    staff_info
     */
    private static final long serialVersionUID = -1029987992878927872L;

    /**
     * 获取主键
     *
     * @return staff_id - 主键
     */
    public Integer getStaffId() {
        return staffId;
    }

    /**
     * 设置主键
     *
     * @param staffId 主键
     */
    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    /**
     * 获取密码
            
     *
     * @return password - 密码
            
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
            
     *
     * @param password 密码
            
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * @return pwd_salt
     */
    public String getPwdSalt() {
        return pwdSalt;
    }

    /**
     * @param pwdSalt
     */
    public void setPwdSalt(String pwdSalt) {
        this.pwdSalt = pwdSalt == null ? null : pwdSalt.trim();
    }

    /**
     * 获取用户名
     *
     * @return user_name - 用户名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名
     *
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * 获取性别；0：未知；1：男；2：女；
     *
     * @return gender - 性别；0：未知；1：男；2：女；
     */
    public Byte getGender() {
        return gender;
    }

    /**
     * 设置性别；0：未知；1：男；2：女；
     *
     * @param gender 性别；0：未知；1：男；2：女；
     */
    public void setGender(Byte gender) {
        this.gender = gender;
    }

    /**
     * 获取头像
     *
     * @return avatar - 头像
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像
     *
     * @param avatar 头像
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar == null ? null : avatar.trim();
    }

    /**
     * 获取生日
     *
     * @return birthday - 生日
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * 设置生日
     *
     * @param birthday 生日
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取身份证号
     *
     * @return ID_num - 身份证号
     */
    public String getIdNum() {
        return idNum;
    }

    /**
     * 设置身份证号
     *
     * @param idNum 身份证号
     */
    public void setIdNum(String idNum) {
        this.idNum = idNum == null ? null : idNum.trim();
    }

    /**
     * 获取手机号
     *
     * @return mobile_phone - 手机号
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * 设置手机号
     *
     * @param mobilePhone 手机号
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone == null ? null : mobilePhone.trim();
    }

    /**
     * 获取座机
     *
     * @return telephone - 座机
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * 设置座机
     *
     * @param telephone 座机
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    /**
     * 获取企业邮箱
     *
     * @return email - 企业邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置企业邮箱
     *
     * @param email 企业邮箱
     */
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    /**
     * 获取职务
     *
     * @return duty - 职务
     */
    public String getDuty() {
        return duty;
    }

    /**
     * 设置职务
     *
     * @param duty 职务
     */
    public void setDuty(String duty) {
        this.duty = duty == null ? null : duty.trim();
    }

    /**
     * 获取创建人
     *
     * @return creator - 创建人
     */
    public Integer getCreator() {
        return creator;
    }

    /**
     * 设置创建人
     *
     * @param creator 创建人
     */
    public void setCreator(Integer creator) {
        this.creator = creator == null ? null : creator;
    }

    /**
     * 获取修改人
     *
     * @return modifier - 修改人
     */
    public Integer getModifier() {
        return modifier;
    }

    /**
     * 设置修改人
     *
     * @param modifier 修改人
     */
    public void setModifier(Integer modifier) {
        this.modifier = modifier == null ? null : modifier;
    }

    /**
     * 获取创建时间
     *
     * @return gmt_create - 创建时间
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 设置创建时间
     *
     * @param gmtCreate 创建时间
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * 获取更新时间
     *
     * @return gmt_modified - 更新时间
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * 设置更新时间
     *
     * @param gmtModified 更新时间
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     * 获取业务状态：1、在职；2、离职；3、停职；
     *
     * @return biz_state - 业务状态：1、在职；2、离职；3、停职；
     */
    public Byte getBizState() {
        return bizState;
    }

    /**
     * 设置业务状态：1、在职；2、离职；3、停职；
     *
     * @param bizState 业务状态：1、在职；2、离职；3、停职；
     */
    public void setBizState(Byte bizState) {
        this.bizState = bizState;
    }

    /**
     * 获取状态
     *
     * @return state - 状态
     */
    public Byte getState() {
        return state;
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(Byte state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "StaffInfo{" + "staffId=" + staffId + ", password='" + password + '\'' + ", pwdSalt='" + pwdSalt + '\'' + ", userName='" + userName + '\'' + ", gender=" + gender + ", avatar='" + avatar + '\'' + ", birthday=" + birthday + ", idNum='" + idNum + '\'' + ", mobilePhone='" + mobilePhone + '\'' + ", telephone='" + telephone + '\'' + ", email='" + email + '\'' + ", duty='" + duty + '\'' + ", creator=" + creator + ", modifier=" + modifier + ", gmtCreate=" + gmtCreate + ", gmtModified=" + gmtModified + ", bizState=" + bizState + ", state=" + state + '}';
    }
}