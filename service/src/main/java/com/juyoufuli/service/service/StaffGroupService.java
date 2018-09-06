package com.juyoufuli.service.service;

import com.juyoufuli.common.result.ResultBean;
import com.juyoufuli.entity.bean.StaffBaseInfoBean;

import java.util.List;

public interface StaffGroupService {
    /**
     * 添加组
     * @param staffId
     * @param groupName
     * @param parentId
     * @return
     */
    ResultBean addStaffGroup(Integer staffId,String groupName, Long parentId);
    /**
     * 修改组名字
     * @param groupId
     * @param groupName
     * @return
     */
    ResultBean updateStaffGroupName(Long groupId, String groupName);
    /**
     * 修改组排序
     * @param groupId
     * @param reGroupId
     * @return
     */
    ResultBean updateStaffGroupSort(Long groupId,Long reGroupId);
    /**
     *修改组层级
     * @param groupId
     * @param parentId
     * @return
     */
    ResultBean updateStaffGroupHierarchy(Long groupId,Long parentId);
    /**
     * 修改组状态
     * @param groupId
     * @param state
     * @return
     */
    ResultBean updateStaffGroupState(Long groupId,Integer state);
    /**
     * 通过组ID查询下级组树
     * @param groupId
     * @return
     */
    ResultBean selectChildTreeByNode(Long groupId);
    /**
     *同步全部数据到redis
     * @return
     */
    ResultBean syncStaffGroupToRedis();
    /**
     * 通过ID同步数据到redis
     * @param groupIds
     * @return
     */
    ResultBean syncStaffGroupToRedisById(Long[] groupIds);
    /**
     * 同步组数据到mysql
     * @return
     */
    ResultBean syncStaffGroupToMysql(Integer type);
    /**
     * 同步组数据到mysql
     * @return
     */
    ResultBean syncStaffGroupToMysqlById(Long[] groupIds);
    /**
     * 组是否存在
     * @param groupId
     * @return
     */
    boolean isExist(Long groupId);
    /**
     * 组是否存在
     * @param groupId
     * @return
     */
    boolean isExist(Long[] groupId);

    /**
     * 通过组名字符串添加组用于支持员工批量导入
     * @param staffId
     * @param groupName
     * @return
     */
    long importStaffGroup(Integer staffId,String groupName);

}
