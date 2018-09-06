package com.juyoufuli.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.juyoufuli.cloud.service.RedisService;
import com.juyoufuli.common.collect.ListUtils;
import com.juyoufuli.common.collect.MapUtils;
import com.juyoufuli.common.config.BusinessEnum;
import com.juyoufuli.common.config.Constant;
import com.juyoufuli.common.lang.StringUtils;
import com.juyoufuli.common.result.ResultBean;
import com.juyoufuli.entity.StaffGroup;
import com.juyoufuli.service.service.GroupCopyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Desc: 练手实现类
 * @Company: 聚优福利
 * @Author  wuyz@huayingcul.com
 * @Date  2018/9/5 15:27
 */
public class GroupCopyServiceImpl implements GroupCopyService {

    @Autowired
    private RedisService redisService;
    /**
     * @Desc: 添加组
     * @Param staffId 操作人id
     * @Param groupName 组名
     * @Param parentId 父id
     * @Author wuyz@huayingcul.com
     * @Date   2018/9/5 15:55
     * @return 
     */
    @Override
    public ResultBean addStaffGroup(Integer staffId, String groupName, Long parentId) {
        if (null==staffId){
            new ResultBean<>(BusinessEnum.STAFF_STAFFID_IS_NULL);
        }
        if (StringUtils.isBlank(groupName)){
            new ResultBean<>(BusinessEnum.STAFF_GROUP_NAME_ERROR);
        }
        if (null==parentId){
            parentId=1L;
        }
        if (parentId<=0){
            return new ResultBean(BusinessEnum.STAFF_GROUP_PARENT_ID_ERROR);
        }
        //获取员工组信息对象
        StaffGroup staffGroup = (StaffGroup)redisService.hget(Constant.STAFF_GROUP, parentId.toString());
        //判断父id所在的组状态是否正常 1代表组状态正常，0和2代表非正常
        if (null==staffGroup||0==staffGroup.getState()||2==staffGroup.getState()){
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_PARENT_ID_ERROR);
        }
        //组对象
        StaffGroup staffGroup1 = new StaffGroup();
        staffGroup1.setGroupName(groupName);
        staffGroup1.setParentId(parentId);
        staffGroup1.setCreator(Long.parseLong(staffId.toString()));
        staffGroup1.setModifier(Long.parseLong(staffId.toString()));

        //查看redis中状态正常的所有组对象
        List<StaffGroup> list = new ArrayList<>();
        Map groupMap = redisService.hmget(Constant.STAFF_GROUP);
        if (MapUtils.isEmpty(groupMap)) {
            return new ResultBean(BusinessEnum.STAFF_GROUP_DATA_IS_NULL);
        }
        for (Object key : groupMap.keySet()){
            StaffGroup staffGroup2 = (StaffGroup)groupMap.get(key);
            //判断编组状态 1代表组状态正常
            if (1==staffGroup2.getState()){
                list.add(staffGroup2);
            }
        }
        if (ListUtils.isEmpty(list)){
            return new ResultBean(BusinessEnum.STAFF_GROUP_DATA_IS_NULL);
        }
        //获取父id下所有子组
        ArrayList<StaffGroup> childens = new ArrayList<>();
        for (StaffGroup staffGroup3:list) {
            if (parentId.equals(staffGroup3.getParentId())){
                childens.add(staffGroup3);
            }
        }
        //设置排序字段
        if (ListUtils.isEmpty(childens)){
            staffGroup1.setSort(1);
        }else {
            //组不能重复
            for (StaffGroup childen: childens) {
                if (groupName.trim().equals(childen.getGroupName())){
                    return new ResultBean<>(BusinessEnum.STAFF_GROUP_NAME_REPETITION);
                }
            }
            staffGroup1.setSort(childens.get(childens.size()-1).getSort()+1);
        }
        // 获取redis自增id
        long groupIndex = (long) redisService.generate(Constant.STAFF_GROUP_INDEX,1);
        staffGroup1.setGroupId(groupIndex);
        staffGroup1.setState(1);
        staffGroup1.setGmtCreate(new Date());
        staffGroup1.setGmtModified(new Date());
        redisService.hset(Constant.STAFF_GROUP, Long.toString(staffGroup1.getGroupId()), staffGroup1);

        // 通过组对象递归向上获取组名树
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.insert(0,staffGroup1.getGroupName());
        stringBuilder.insert(0,Constant.GROUP_SEPARATOR);
        StaffGroup staffGroup4 = (StaffGroup)redisService.hget(Constant.STAFF_GROUP,Long.toString(staffGroup1.getParentId()));
        if (staffGroup1.getParentId()>1){
            stringBuilder.insert(0,staffGroup1.getGroupName());
            stringBuilder.insert(0,Constant.GROUP_SEPARATOR);
        }else if (1==staffGroup1.getParentId()){
            stringBuilder.insert(0,staffGroup4.getGroupName());
        }else if (0==staffGroup1.getParentId()){
            stringBuilder.delete(0,1);
            return new ResultBean(BusinessEnum.STAFF_GROUP_STATUS_IS_ERROR);
        }
        if(0==stringBuilder.length()){
            new ResultBean<>(BusinessEnum.STAFF_GROUP_ADD_ERROR);
        }

        //stringToUnicode()
        redisService.set(Constant.STAFF_GROUP_NAME_+stringBuilder.toString(),staffGroup1.getGroupId());
        //添加到修改队列
        redisService.leftPush(Constant.STAFF_GROUP_SYNC,staffGroup1.getGroupId());
        //返回前台的包装类
        StaffGroup resGroup = new StaffGroup();
        resGroup.setGroupId(staffGroup1.getGroupId());
        resGroup.setParentId(staffGroup1  .getParentId());
        return new ResultBean<>(BusinessEnum.OK,JSON.toJSON(resGroup));
    }
}
