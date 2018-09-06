package com.juyoufuli.service.controller;

import com.juyoufuli.common.result.ResultBean;
import com.juyoufuli.service.annotation.Auth;
import com.juyoufuli.service.controller.base.BaseController;
import com.juyoufuli.service.service.StaffGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("staffGroup")
public class StaffGroupController extends BaseController {
    @Autowired
    private StaffGroupService staffGroupService;

    /**
     *添加组
     * @param groupName
     * @param parentId
     * @return
     */
    @Auth
    @RequestMapping(value="addStaffGroup",method=RequestMethod.GET)
    public ResultBean addStaffGroup(String groupName, Long parentId){
        return staffGroupService.addStaffGroup(getLoginAuthStaffId(),groupName,parentId);
    }

    /**
     *修改组名
     * @param groupId
     * @param groupName
     * @return
     */
    @Auth
    @RequestMapping(value="updateStaffGroupName",method=RequestMethod.GET)
    public ResultBean updateStaffGroupName(Long groupId, String groupName){
        return staffGroupService.updateStaffGroupName(groupId,groupName);
    }

    /**
     *修改组顺序
     * @param groupId
     * @param reGroupId
     * @return
     */
    @Auth
    @RequestMapping(value="updateStaffGroupSort",method=RequestMethod.GET)
    public ResultBean updateStaffGroupSort(Long groupId,Long reGroupId){
        return staffGroupService.updateStaffGroupSort(groupId,reGroupId);
    }

    /**
     *修改组层级
     * @param groupId
     * @param parentId
     * @return
     */
    @Auth
    @RequestMapping(value="updateStaffGroupHierarchy",method=RequestMethod.GET)
    public ResultBean updateStaffGroupHierarchy(Long groupId,Long parentId){
        return staffGroupService.updateStaffGroupHierarchy(groupId,parentId);
    }

    /**
     *修改组状态
     * @param groupId
     * @param state
     * @return
     */
    @Auth
    @RequestMapping(value="updateStaffGroupState",method=RequestMethod.GET)
    public ResultBean updateStaffGroupState(Long groupId,int state){
        return staffGroupService.updateStaffGroupState(groupId,state);
    }

    /**
     *查询某节点下的所有组
     * @param groupId
     * @return
     */
    @Auth
    @RequestMapping(value="selectChildTreeByNode",method=RequestMethod.GET)
    public ResultBean selectChildTreeByNode(Long groupId){
        return staffGroupService.selectChildTreeByNode(groupId);
    }

    /**
     *同步数据到redis通过groupIds
     * @param groupIds
     * @return
     */
    @RequestMapping(value="syncStaffGroupToRedisById",method=RequestMethod.GET)
    ResultBean syncStaffGroupToRedisById(Long[] groupIds){
        return staffGroupService.syncStaffGroupToRedisById(groupIds);
    }
    /**
     *同步数据到redis
     * @param
     * @return
     */
    @RequestMapping(value="syncStaffGroupToRedis",method=RequestMethod.GET)
    ResultBean syncStaffGroupToRedis(){
        return staffGroupService.syncStaffGroupToRedis();
    }
    /**
     *同步数据到mysql
     * @param
     * @return
     */
    @RequestMapping(value="syncStaffGroupToMysql",method=RequestMethod.GET)
    ResultBean syncStaffGroupToMysql(Integer type){
        return staffGroupService.syncStaffGroupToMysql(type);
    }
    /**
     * 同步组数据到mysql
     * @return
     */
    @RequestMapping(value="syncStaffGroupToMysqlById",method=RequestMethod.GET)
    ResultBean syncStaffGroupToMysqlById(Long[] groupIds){
        return staffGroupService.syncStaffGroupToMysqlById(groupIds);
    }
    /**
     * 此方法不对外暴露 用于测试
     * @param staffId
     * @param groupNameStr
     * @return
     */
    @RequestMapping(value="importStaffGroup",method=RequestMethod.GET)
    public long importStaffGroup(Integer staffId, String groupNameStr){
        return staffGroupService.importStaffGroup(staffId,groupNameStr);
    }
}
