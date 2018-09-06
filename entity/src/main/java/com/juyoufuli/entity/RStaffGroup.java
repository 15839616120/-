package com.juyoufuli.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *  员工与员工组关系
 * @table r_staff_group
 * @author  zhanghongwei
 * @date 2018-08-16 15:07:28
 */
public class RStaffGroup implements Serializable {

    /**
     * id:自增id
     */
    private Long id;

    /**
     * staff_id:主键
     */
    private Integer staffId;

    /**
     * group_id:组id
     */
    private Long groupId;

    /**
     * gmt_create:创建时间
     */
    private Date gmtCreate;

    /**
     * gmt_modified:修改时间
     */
    private Date gmtModified;

    /**
     * state:状态
     */
    private Byte state;

    /**
    r_staff_group
     */
    private static final long serialVersionUID = -1029987992932159488L;

    /**
     * 获取自增id
     *
     * @return id - 自增id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置自增id
     *
     * @param id 自增id
     */
    public void setId(Long id) {
        this.id = id;
    }

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
     * 获取组id
     *
     * @return group_id - 组id
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * 设置组id
     *
     * @param groupId 组id
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
     * 获取修改时间
     *
     * @return gmt_modified - 修改时间
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * 设置修改时间
     *
     * @param gmtModified 修改时间
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