package com.juyoufuli.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *  用户账号表
 * @table staff_account
 * @author  zhanghongwei
 * @date 2018-08-16 15:07:28
 */
public class StaffAccount implements Serializable {

    /**
     * id:
     */
    private Long id;

    /**
     * staff_id:员工Id
     */
    private Integer staffId;

    /**
     * account:账户
     */
    private String account;

    /**
     * account_type:账户类型
     */
    private Byte accountType;

    /**
     * gmt_create:创建时间
     */
    private Date gmtCreate;

    /**
     * gmt_modified:更新时间
     */
    private Date gmtModified;

    /**
     * state:状态
     */
    private Byte state;

    /**
    staff_account
     */
    private static final long serialVersionUID = -1029987992906944512L;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取员工Id
     *
     * @return staff_id - 员工Id
     */
    public Integer getStaffId() {
        return staffId;
    }

    /**
     * 设置员工Id
     *
     * @param staffId 员工Id
     */
    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    /**
     * 获取账户
     *
     * @return account - 账户
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置账户
     *
     * @param account 账户
     */
    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    /**
     * 获取账户类型
     *
     * @return account_type - 账户类型
     */
    public Byte getAccountType() {
        return accountType;
    }

    /**
     * 设置账户类型
     *
     * @param accountType 账户类型
     */
    public void setAccountType(Byte accountType) {
        this.accountType = accountType;
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
}