package com.juyoufuli.entity;

import com.juyoufuli.entity.base.BaseEntity;

import java.io.Serializable;

/**
 *  员工组
 * @table staff_group
 * @author  admin
 * @date 2018-04-25 14:18:29
 */
public class StaffGroup extends BaseEntity implements Serializable {
    /**
     * group_id:组id
     */
    private Long groupId;

    /**
     * group_name:组名
     */
    private String groupName;

    /**
     * parent_id:父id
     */
    private Long parentId;

    /**
     * sort:排序
     */
    private Integer sort;

    /**
     * creator:创建人
     */
    private Long creator;

    /**
     * modifier:修改人
     */
    private Long modifier;

    /**
    staff_group
     */
    private static final long serialVersionUID = 1L;

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
     * 获取组名
     *
     * @return group_name - 组名
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * 设置组名
     *
     * @param groupName 组名
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    /**
     * 获取父id
     *
     * @return parent_id - 父id
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 设置父id
     *
     * @param parentId 父id
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取排序
     *
     * @return sort - 排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序
     *
     * @param sort 排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取创建人
     *
     * @return creator - 创建人
     */
    public Long getCreator() {
        return creator;
    }

    /**
     * 设置创建人
     *
     * @param creator 创建人
     */
    public void setCreator(Long creator) {
        this.creator = creator;
    }

    /**
     * 获取修改人
     *
     * @return modifier - 修改人
     */
    public Long getModifier() {
        return modifier;
    }

    /**
     * 设置修改人
     *
     * @param modifier 修改人
     */
    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }

}