package com.juyoufuli.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.juyoufuli.cloud.service.RedisService;
import com.juyoufuli.common.collect.ListUtils;
import com.juyoufuli.common.collect.MapUtils;
import com.juyoufuli.common.config.BusinessEnum;
import com.juyoufuli.common.config.Constant;
import com.juyoufuli.common.exception.ServiceException;
import com.juyoufuli.common.lang.DateUtils;
import com.juyoufuli.common.result.ResultBean;
import com.juyoufuli.entity.StaffGroup;
import com.juyoufuli.entity.bean.GroupTree;
import com.juyoufuli.entity.bean.StaffInfoBean;
import com.juyoufuli.service.mapper.StaffGroupMapper;
import com.juyoufuli.service.service.StaffGroupService;
import com.juyoufuli.service.service.StaffInfoService;
import com.juyoufuli.service.service.StaffLoginService;
import com.juyoufuli.service.weixin.WeChatSendManager;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StaffGroupServiceImpl implements StaffGroupService {

    private static final Logger logger = LoggerFactory.getLogger(StaffGroupServiceImpl.class);

    @Value("${staffgroup.syncSize}")
    private int               syncSize;
    @Autowired
    private StaffGroupMapper  groupMapper;
    @Autowired
    private RedisService      redisService;
    @Autowired
    private StaffLoginService staffLoginService;
    @Autowired
    private StaffInfoService  staffInfoService;
    @Autowired
    private WeChatSendManager weChatSendManager;

    /**
     * 添加组
     * @param staffId
     * @param groupName
     * @param parentId
     * @return
     */
    @Override
    public ResultBean addStaffGroup(Integer staffId,String groupName, Long parentId) {
        boolean res = false;
        if(null==staffId){
            return new ResultBean<>(BusinessEnum.STAFF_STAFFID_IS_NULL);
        }
        if (null==groupName || "".equals(groupName)) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_NAME_ERROR);
        }

        if(null == parentId ){
            parentId=1L;
        }
        if (parentId <= 0) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_PARENT_ID_ERROR);
        }
        StaffGroup staffGroup = (StaffGroup)redisService.hget(Constant.STAFF_GROUP, Long.toString(parentId));
        //不可把删除状态的ID当做父ID
        if(null==staffGroup||0==staffGroup.getState()||2==staffGroup.getState()){
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_PARENT_ID_ERROR);
        }
        StaffGroup group = new StaffGroup();
        group.setGroupName(groupName);
        group.setParentId(parentId);
        group.setCreator(Long.parseLong(staffId.toString()));
        group.setModifier(Long.parseLong(staffId.toString()));
        //redis中状态为正常的所有组信息
        List<StaffGroup> groupAll = getStaffGroupList();
        if(ListUtils.isEmpty(groupAll)){
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_DATA_IS_NULL);
        }
        //获取组下所有子组
        List<StaffGroup> groupChilds =  getGroupListByParentId(groupAll, parentId);

        if(ListUtils.isEmpty(groupChilds)){
            group.setSort(1);
        }else{
            for(StaffGroup groupChild:groupChilds){
                if(groupName.trim().equals(groupChild.getGroupName().trim())){
                    return new ResultBean<>(BusinessEnum.STAFF_GROUP_NAME_REPETITION);
                }
            }
            group.setSort(groupChilds.get(groupChilds.size()-1).getSort()+1);
        }
        //获取redis自增Id
        long groupIndex = (long) redisService.generate(Constant.STAFF_GROUP_INDEX,1);
        group.setGroupId(groupIndex);
        group.setState(1);
        group.setGmtCreate(new Date());
        group.setGmtModified(new Date());
        redisService.hset(Constant.STAFF_GROUP, Long.toString(group.getGroupId()), group);

        StringBuilder stringBuilder = new StringBuilder();
        //通过组对象递归向上获取组名树
        selectGroupTreeBySubordinate(stringBuilder,group);
        if(0==stringBuilder.length()){
            new ResultBean<>(BusinessEnum.STAFF_GROUP_ADD_ERROR);
        }
        //stringToUnicode()
        redisService.set(Constant.STAFF_GROUP_NAME_+stringBuilder.toString(),group.getGroupId());
        //添加到修改队列
        redisService.leftPush(Constant.STAFF_GROUP_SYNC,group.getGroupId());
        StaffGroup resGroup = new StaffGroup();
        resGroup.setGroupId(group.getGroupId());
        resGroup.setParentId(group.getParentId());
        return new ResultBean<>(BusinessEnum.OK,JSON.toJSON(resGroup));

    }

    /**
     * 修改组名字
     * @param groupId
     * @param groupName
     * @return
     */
    @Override
    public ResultBean updateStaffGroupName(Long groupId, String groupName) {
        if (null==groupId||groupId <= 0) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_ID_ERROR);
        }
        if(groupName == null||"".equals(groupName)){
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_NAME_ERROR);
        }
        StaffGroup staffGroup = (StaffGroup)redisService.hget(Constant.STAFF_GROUP,Long.toString(groupId));
        //1正常 0删除
        if (staffGroup == null || staffGroup.getState() != 1) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_IS_NULL);
        }
        //删除旧STAFF_GROUP_NAME_
        StringBuilder oldGroupName = new StringBuilder();
        selectGroupTreeBySubordinate(oldGroupName,staffGroup);
        redisService.del(Constant.STAFF_GROUP_NAME_+oldGroupName);
        //修改STAFF_GROUP
        staffGroup.setGroupName(groupName);
        redisService.hset(Constant.STAFF_GROUP, Long.toString(groupId), staffGroup);

        //添加新STAFF_GROUP_NAME_
        StringBuilder newGroupName = new StringBuilder();
        selectGroupTreeBySubordinate(newGroupName,staffGroup);
        redisService.set(Constant.STAFF_GROUP_NAME_+newGroupName,groupId);

        //添加到修改队列
        redisService.leftPush(Constant.STAFF_GROUP_SYNC,groupId);
        return new ResultBean<>(BusinessEnum.OK);
    }

    /**
     * 修改组排序
     * @param groupId
     * @param reGroupId
     * @return
     */
    @Override
    public ResultBean updateStaffGroupSort(Long groupId,Long reGroupId){
        if (null==groupId||groupId <= 1) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_ID_ERROR);
        }
        if (null==reGroupId||reGroupId <= 1) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_ID_ERROR);
        }
        StaffGroup staffGroup = (StaffGroup)redisService.hget(Constant.STAFF_GROUP,Long.toString(groupId));
        if (staffGroup == null || staffGroup.getState() != 1) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_IS_NULL);
        }
        StaffGroup reStaffGroup = (StaffGroup)redisService.hget(Constant.STAFF_GROUP,Long.toString(reGroupId));
        if (reStaffGroup == null || reStaffGroup.getState() != 1) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_IS_NULL);
        }
        Integer staffGroupSort = staffGroup.getSort();
        //根目录为1 以此类推 不允许和根目录换位置
        if(null==staffGroupSort||staffGroupSort<=1){
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_UPDATE_ERROR);
        }
        Integer reStaffGroupSort = reStaffGroup.getSort();
        if(null==reStaffGroupSort||reStaffGroupSort<=1){
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_UPDATE_ERROR);
        }
        staffGroup.setSort(reStaffGroupSort);
        reStaffGroup.setSort(staffGroupSort);
        redisService.hset(Constant.STAFF_GROUP, Long.toString(groupId), staffGroup);
        redisService.hset(Constant.STAFF_GROUP, Long.toString(reGroupId), reStaffGroup);
        //添加到修改队列
        redisService.leftPush(Constant.STAFF_GROUP_SYNC,groupId);
        redisService.leftPush(Constant.STAFF_GROUP_SYNC,reGroupId);
        return new ResultBean<>(BusinessEnum.OK);
    }

    /**
     *修改组层级
     * @param groupId
     * @param parentId
     * @return
     */
    @Override
    public ResultBean updateStaffGroupHierarchy(Long groupId, Long parentId) {
        //不允许修改根组
        if (null==groupId||groupId <= 1) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_ID_ERROR);
        }
        //当前组不允许替换为根组
        if (null==parentId||parentId <= 0) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_ID_ERROR);
        }
        StaffGroup staffGroup = (StaffGroup)redisService.hget(Constant.STAFF_GROUP,Long.toString(groupId));
        if (staffGroup == null || staffGroup.getState() != 1) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_IS_NULL);
        }
        if(parentId.equals(staffGroup.getParentId())){
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_NOT_CHANGE);
        }

        //删除旧STAFF_GROUP_NAME_
        StringBuilder oldGroupName = new StringBuilder();
        selectGroupTreeBySubordinate(oldGroupName,staffGroup);
        redisService.del(Constant.STAFF_GROUP_NAME_+oldGroupName);

        staffGroup.setParentId(parentId);
        redisService.hset(Constant.STAFF_GROUP, Long.toString(groupId), staffGroup);

        //添加新STAFF_GROUP_NAME_
        StringBuilder newGroupName = new StringBuilder();
        selectGroupTreeBySubordinate(newGroupName,staffGroup);
        redisService.set(Constant.STAFF_GROUP_NAME_+newGroupName,groupId);

        //添加到修改队列
        redisService.leftPush(Constant.STAFF_GROUP_SYNC,groupId);
        return new ResultBean<>(BusinessEnum.OK);
    }
    /**
     * 修改组状态
     * @param groupId
     * @param state
     * @return
     */
    @Override
    public ResultBean updateStaffGroupState(Long groupId, Integer state) {
        //不允许修改根组
        if (null==groupId||groupId <= 1) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_ID_ERROR);
        }
        if(null==state){
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_STATE_ERROR);
        }
        //查询组对象
        StaffGroup staffGroup = (StaffGroup)redisService.hget(Constant.STAFF_GROUP,Long.toString(groupId));
        if (staffGroup == null||0==staffGroup.getState()|| state==staffGroup.getState()) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_STATUS_IS_ERROR);
        }
        //删除组 目前组只有两个状态1正常 2删除
        if(2==state){
            List<GroupTree> childGroupTree = getGroupTree(groupId);
            if(ListUtils.isNotEmpty(childGroupTree)){
                return new ResultBean<>(BusinessEnum.STAFF_GROUP_NOT_DEL_NOT_NULL);
            }
            //查询该组下的员工 如果不为空返回不能删除非空组
            List<StaffInfoBean> staffInfoBeanList = getStaffIdsBygroupId(groupId);
            if(ListUtils.isNotEmpty(staffInfoBeanList)){
                return new ResultBean<>(BusinessEnum.STAFF_GROUP_NOT_DEL_NOT_NULL);
            }
            //删除旧STAFF_GROUP_NAME_
            StringBuilder oldGroupName = new StringBuilder();
            selectGroupTreeBySubordinate(oldGroupName,staffGroup);
            redisService.del(Constant.STAFF_GROUP_NAME_+oldGroupName);
        }else {
            //将删除修改为正常 添加新STAFF_GROUP_NAME_
            StringBuilder newGroupName = new StringBuilder();
            selectGroupTreeBySubordinate(newGroupName,staffGroup);
            redisService.set(Constant.STAFF_GROUP_NAME_+newGroupName,groupId);
        }
        staffGroup.setState(state);
        redisService.hset(Constant.STAFF_GROUP, Long.toString(groupId), staffGroup);
        //添加到修改队列
        redisService.leftPush(Constant.STAFF_GROUP_SYNC,groupId);
        return new ResultBean<>(BusinessEnum.OK);

    }

    /**
     * 通过组ID查询下级组树
     * @param groupId
     * @return
     */
    @Override
    public ResultBean selectChildTreeByNode(Long groupId) {
        //目前只支持查询聚优集团下的
        if (null==groupId||groupId <=0) {
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_ID_ERROR);
        }
        List<GroupTree> groupTree = new ArrayList<GroupTree>();

        StaffGroup groupInfo = (StaffGroup)redisService.hget(Constant.STAFF_GROUP, Long.toString(groupId));
        if (null!=groupInfo){
            GroupTree groupItem = new GroupTree();
            groupItem.setGroupId(groupInfo.getGroupId());
            groupItem.setGroupName(groupInfo.getGroupName());
            groupItem.setChildren(getGroupTree(groupInfo.getGroupId()));
            groupTree.add(groupItem);
        }
        return new ResultBean<>(BusinessEnum.OK,groupTree);
    }



    /**
     *同步全部数据到redis
     * @return
     */
    @Override
    public ResultBean syncStaffGroupToRedis() {
        try{
        int copies= groupMapper.selectCount();
        if(copies<1){
            return new ResultBean<>(BusinessEnum.STAFF_GROUP_IS_NULL);
        }else if(copies<=syncSize){
            List<StaffGroup> staffGroups = groupMapper.selectAll();
            if(ListUtils.isEmpty(staffGroups)){
                return new ResultBean<>(BusinessEnum.STAFF_GROUP_SYNC_ERROR);
            }
            boolean saveGroupResult = createGroupToRedis(staffGroups);
            boolean saveGroupIndexResult = createGroupIndexToRedis();
            boolean saveGroupNameResult = createGroupNameToRedis(staffGroups);
            if(false==saveGroupResult||false==saveGroupIndexResult||false==saveGroupNameResult){
                new ResultBean(BusinessEnum.STAFF_GROUP_SYNC_ERROR);
            }
        }else{
            int syncNum = copies % syncSize > 0 ? copies / syncSize + 1 : copies / syncSize;
            for (int pageIndex = 1; pageIndex <= syncNum; pageIndex++) {
                int start = (pageIndex - 1) * syncSize;
                List<StaffGroup> staffGroups = groupMapper.selectLimit(start,syncSize);
                boolean saveGroupResult = createGroupToRedis(staffGroups);
                boolean saveGroupNameResult = createGroupNameToRedis(staffGroups);
                if(false==saveGroupResult||false==saveGroupNameResult){
                    new ResultBean(BusinessEnum.STAFF_GROUP_SYNC_ERROR);
                }
            }
            boolean saveGroupIndexResult = createGroupIndexToRedis();
            if(false==saveGroupIndexResult){
                new ResultBean(BusinessEnum.STAFF_GROUP_SYNC_ERROR);
            }
        }
        }catch (Exception e){
            logger.error("syncStaffGroupToRedis is Exception",e);
        }
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 通过ID同步组数据到redis
     * @param groupIds
     * @return
     */
    @Override
    public ResultBean syncStaffGroupToRedisById(Long[] groupIds) {
            if (ArrayUtils.isEmpty(groupIds)) {
                return new ResultBean(BusinessEnum.STAFF_GROUP_ID_IS_NULL);
            }
            List<StaffGroup> staffGroups = groupMapper.selectByIds(groupIds);
            if (ListUtils.isEmpty(staffGroups)) {
                return new ResultBean(BusinessEnum.STAFF_GROUP_IS_NULL);
            }
            boolean saveGroupResult = createGroupToRedis(staffGroups);
            boolean saveGroupNameResult = createGroupNameToRedis(staffGroups);
            if (false == saveGroupResult || false == saveGroupNameResult) {
                new ResultBean(BusinessEnum.STAFF_GROUP_SYNC_ERROR);
            }
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 同步组数据到mysql
     * @return
     */

    @Override
    public ResultBean syncStaffGroupToMysql(Integer type) {
        String startTime="";
        //条数
        long size = 0;
        if(null==type){
            return new ResultBean(BusinessEnum.STAFF_GROUP_NO_TYPE);
        }
        try{
            Date timeNow = new Date();
            startTime = DateUtils.formatDate(DateUtils.PATTERN_DEFAULT_ON_SECOND,timeNow);
            //定时增量任务执行
            if(0==type){
            //添加到修改队列
            size = redisService.lGetListSize(Constant.STAFF_GROUP_SYNC);
            logger.info("增量同步数据总数："+size);
            if(size<1){
                return new ResultBean<>(BusinessEnum.STAFF_GROUP_RDB_NO_CHANGE);
            }else{
                List groupIds = new ArrayList<>();
                for(long l=0;l<size;l++){
                    Integer groupId = (Integer)(redisService.rightPop(Constant.STAFF_GROUP_SYNC));
                    groupIds.add(groupId.longValue());
                }
                Long[] groupIdArr = new Long[groupIds.size()];
                groupIds.toArray(groupIdArr);
                ResultBean resultBean =syncStaffGroupToMysqlById(groupIdArr);
                if(0==resultBean.getCode()){
                    new ResultBean(BusinessEnum.OK);
                }else{
                    new ResultBean(BusinessEnum.STAFF_GROUP_SYNC_ERROR);
                }
            }
            //手动全量执行
            }else{
                logger.info("全量同步数据开始"+DateUtils.formatDate(DateUtils.PATTERN_DEFAULT_ON_SECOND,new Date()));
                Map<Long,StaffGroup> staffGroupMap= redisService.hmget(Constant.STAFF_GROUP);
                if(MapUtils.isEmpty(staffGroupMap)){
                    return new ResultBean<>(BusinessEnum.STAFF_GROUP_IS_NULL);
                }else{
                    for (Map.Entry<Long,StaffGroup> item : staffGroupMap.entrySet()) {
                        StaffGroup group = item.getValue();
                        StaffGroup staffGroupDB = groupMapper.selectByPrimaryKey(group.getGroupId());
                        if (null==staffGroupDB){
                            groupMapper.insertSelective(group);
                        }else{
                            groupMapper.updateByPrimaryKey(group);
                        }

                    }

                }
                logger.info("全量同步数据结束"+DateUtils.formatDate(DateUtils.PATTERN_DEFAULT_ON_SECOND,new Date()));
            }
            }catch(Exception e){
                logger.error("syncStaffGroupToMysql is Exception",e);
                // 发送消息
                StringBuilder resutlStr = new StringBuilder();
                resutlStr.append("服务名称：员工中心组数据同步服务");
                resutlStr.append("\n");
                resutlStr.append("状态：失败");
                resutlStr.append("\n");
                resutlStr.append("执行时间：");
                resutlStr.append(startTime);
                resutlStr.append("\n");
                resutlStr.append("总条数：");
                resutlStr.append(size);
                resutlStr.append("\n");
                resutlStr.append("完成时间：");
                resutlStr.append(DateUtils.formatDate(DateUtils.PATTERN_DEFAULT_ON_SECOND,new Date()));
                weChatSendManager.sendTextMessage(resutlStr.toString());
            }
        return new ResultBean(BusinessEnum.OK);
    }

    @Override
    public ResultBean syncStaffGroupToMysqlById(Long[] groupIds) {
        if (ArrayUtils.isEmpty(groupIds)){
            return new ResultBean(BusinessEnum.STAFF_GROUP_ID_IS_NULL);
        }
        for(Long groudId : groupIds){
            StaffGroup staffGroupRD  = (StaffGroup)redisService.hget(Constant.STAFF_GROUP,Long.toString(groudId));
            if(null!=staffGroupRD){
                StaffGroup staffGroupDB = groupMapper.selectByPrimaryKey(groudId);
                if(null==staffGroupDB){
                    groupMapper.insertSelective(staffGroupRD);
                }else{
                    groupMapper.updateByPrimaryKey(staffGroupRD);
                }
            }
        }

        List<StaffGroup> staffGroups = groupMapper.selectByIds(groupIds);
        if(ListUtils.isEmpty(staffGroups)){
            return new ResultBean(BusinessEnum.STAFF_GROUP_IS_NULL);
        }
        boolean saveGroupResult = createGroupToRedis(staffGroups);
        boolean saveGroupIndexResult = createGroupIndexToRedis();
        boolean saveGroupNameResult = createGroupNameToRedis(staffGroups);
        if(false==saveGroupResult||false==saveGroupIndexResult||false==saveGroupNameResult){
            new ResultBean(BusinessEnum.STAFF_GROUP_SYNC_ERROR);
        }
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 组是否存在
     * @param groupId
     * @return
     */
    @Override
    public boolean isExist(Long groupId) {
        return redisService.hHasKey(Constant.STAFF_GROUP,groupId.toString());
    }

    /**
     * 组是否存在
     * @param groupIds
     * @return
     */
    @Override
    public boolean isExist(Long[] groupIds) {
        for (Long groupId: groupIds){
            if (redisService.hHasKey(Constant.STAFF_GROUP,groupId.toString())){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }

    /**
     * 通过组名字符串添加组用于支持员工批量导入
     * @param staffId
     * @param groupNameStr
     * @return
             */
    @Override
    public long importStaffGroup(Integer staffId, String groupNameStr) {
        if("".equals(groupNameStr)){
            throw new ServiceException("组名不能为空");
        }
        String[] groupNameArr = groupNameStr.split("/");

        long groudId = selectGNStrToParentId(staffId,groupNameArr);
        if(0==groudId){
            throw new ServiceException(BusinessEnum.STAFF_GROUP_ADD_ERROR.getMsg());
        }
        //groupNameArr.length()
        return groudId;
    }

    /**
     * 通过组对象递归向上获取组名树
     * @param sb
     * @param group
     */
    private void selectGroupTreeBySubordinate(StringBuilder sb,StaffGroup group){
        if(null==group||null==group.getGroupId()||null==group.getParentId()){
            return ;
        }
        sb.insert(0,group.getGroupName());
        sb.insert(0,Constant.GROUP_SEPARATOR);
        StaffGroup staffGroup = (StaffGroup)redisService.hget(Constant.STAFF_GROUP,Long.toString(group.getParentId()));
        if(group.getParentId()>1){
            selectGroupTreeBySubordinate(sb,staffGroup);
        }else if(1==group.getParentId()){
            sb.insert(0,staffGroup.getGroupName());
        }else if(0==group.getParentId()) {
            sb.delete(0,1);
            return ;
        }else {
            return ;
        }

    }

    /**
     * 查询组名字符串
     * @param staffId
     * @param groupNameArr
     * @return
     */
    private long selectGNStrToParentId(Integer staffId, String[] groupNameArr) {
        StringBuilder stringBuilder = new StringBuilder();
        ResultBean resultBean;
        long groupId = 1;
        for(int i=0;i<groupNameArr.length;i++) {
            String groupName = groupNameArr[i];
            stringBuilder.append(groupName);
            stringBuilder.append(Constant.GROUP_SEPARATOR);
            if (i > 0) {
                Integer staffGroupId = (Integer) redisService.get(Constant.STAFF_GROUP_NAME_ + stringBuilder.substring(0, stringBuilder.length() - 1));
                if (null == staffGroupId || 0 == staffGroupId) {
                    resultBean = addStaffGroup(staffId, groupName, groupId);
                    if (BusinessEnum.OK.getCode() != resultBean.getCode()) {
                        throw new ServiceException(BusinessEnum.STAFF_GROUP_ADD_ERROR);
                    }
                    JSONObject dd = (JSONObject) resultBean.getData();
                    groupId = (long) dd.get("groupId");
                } else {
                    groupId = staffGroupId;
                }
            }
        }
        return groupId;
    }

    /**
     * 获取组下所有子组公共方法
     * @param grouplist
     * @param groupId
     * @return
     */
    private List<StaffGroup> getGroupListByParentId(List<StaffGroup> grouplist, long groupId){

        List<StaffGroup> childen = new ArrayList<StaffGroup>();

        for (StaffGroup item : grouplist) {
            if (item.getParentId() == groupId) {
                childen.add(item);
            }
        }
        return getSortList(Constant.STAFF_GROUP_RISE,childen);
    }

    /**
     * 通过组ID获取下面组树
     * @param groupId
     * @return
     */
    public List<GroupTree> getGroupTree(long groupId){
        List<GroupTree> groupTree = null;
        List<StaffGroup> groupAll = getStaffGroupList();
        if(ListUtils.isEmpty(groupAll)){
            return null;
        }
        List<StaffGroup> groupChilds =  getGroupListByParentId(groupAll, groupId);
        if (null!=groupChilds&&groupChilds.size() > 0) {
            groupTree = new ArrayList<GroupTree>();
            for (StaffGroup item : groupChilds) {
                GroupTree groupItem = new GroupTree();
                groupItem.setGroupId(item.getGroupId());
                groupItem.setGroupName(item.getGroupName());
                List<StaffGroup> childs = getGroupListByParentId(groupAll, item.getGroupId());
                if(ListUtils.isNotEmpty(childs)){
                    if (childs.size() >0) {
                        groupItem.setChildren(getGroupTree(item.getGroupId()));
                    }
                }
                groupTree.add(groupItem);
            }
        }

        return  groupTree;
    }

    /**
     * 通过组ID获取组下员工Bean
     * @param groupId
     * @return
     */
    private List<StaffInfoBean> getStaffIdsBygroupId(long groupId){
        List<Integer> staffIds = (List<Integer>)redisService.get(Constant.STAFF_GROUP_RELATION+Long.toString(groupId));
        if(ListUtils.isEmpty(staffIds)){
            return null;
        }
        List<StaffInfoBean> staffInfoBeanList = staffInfoService.getInfoByStaffId(staffIds);
        return  staffInfoBeanList;
    }

    /**
     * 更新组Map公共
     * @param staffGroup
     * @return
     */
    private Map<String, Object> updateStaffGroupToMap(StaffGroup staffGroup){
        Map<String, Object> map=new HashMap();
        //根据Id查找信息
        map.put("groupId", staffGroup.getGroupId());
        //修改的组名称
        map.put("groupName", staffGroup.getGroupName());
        //修改的组级别
        map.put("parentId", staffGroup.getParentId());
        //修改组的排序
        map.put("sort", staffGroup.getSort());
        //修改组的状态
        map.put("state", staffGroup.getState());
        return map;
    }

    /**
     * 获取redis内状态为正常的所有组
     * @return
     */
    private List<StaffGroup> getStaffGroupList(){
        List<StaffGroup> groupList = new ArrayList<StaffGroup>();
        Map<Object, Object> groupMap = redisService.hmget(Constant.STAFF_GROUP);
        if(MapUtils.isEmpty(groupMap)){
            return null;
        }
        for (Map.Entry<Object, Object> item : groupMap.entrySet()) {

            StaffGroup group = (StaffGroup) item.getValue();
            // 判断编组状态
            if (group.getState() == 1) {
                groupList.add(group);
            }
        }


        return groupList;
    }

    /**
     * 组排序公共
     * @param option
     * @param list
     * @return
     */
    private static List<StaffGroup> getSortList(String option,List<StaffGroup> list) {

        if(ListUtils.isEmpty(list)){
            return null;
        }

        if(Constant.STAFF_GROUP_RISE.equals(option)) {
            Collections.sort(list, new Comparator<StaffGroup>() {
                @Override
                public int compare(StaffGroup o1, StaffGroup o2) {
                    if (o1.getSort() > o2.getSort()) {
                        return 1;
                    }
                    if (o1.getSort().equals(o2.getSort())) {
                        return 0;
                    }
                    return -1;
                }
            });
        }  else if(Constant.STAFF_GROUP_DESC.equals(option)){
            Collections.sort(list, new Comparator<StaffGroup>() {
                @Override
                public int compare(StaffGroup o1, StaffGroup o2) {

                    if (o1.getSort() < o2.getSort()) {
                        return 1;
                    }
                    if (o1.getSort().equals(o2.getSort())) {
                        return 0;
                    }
                    return -1;
                }
            });
        }else {
            return null;
        }
        return list;
    }

    /**
     *恢复 STAFF_GROUP
     * @param staffGroups
     * @return
     */
    private boolean createGroupToRedis(List<StaffGroup> staffGroups){
        boolean result = false;
        for(StaffGroup staffGroup:staffGroups){
            result =  redisService.hset(Constant.STAFF_GROUP, Long.toString(staffGroup.getGroupId()), staffGroup);
            if(result==false){
                return false;
            }
        }
        return result;
    }

    /**
     *恢复 STAFF_GROUP_INDEX
     */
    private boolean createGroupIndexToRedis(){
        long lastID = groupMapper.selectMaxId();
        return redisService.set(Constant.STAFF_GROUP_INDEX,lastID);
    }

    /**
     * 恢复 STAFF_GROUP_NAME_
     * @param staffGroups
     */
    private boolean createGroupNameToRedis(List<StaffGroup> staffGroups){
        boolean result = true;
        for(StaffGroup staffGroup:staffGroups){
            if(false==result){
                return false;
            }
            StringBuilder stringBuilder = new StringBuilder();
            selectGroupTreeBySubordinate(stringBuilder,staffGroup);
            result = redisService.set(Constant.STAFF_GROUP_NAME_+stringBuilder,staffGroup.getGroupId());
        }
        return result;
    }

    //字符串转换unicode
    private static String stringToUnicode(String str) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            unicode.append("\\u" +Integer.toHexString(c));
        }
        return unicode.toString();
    }

    //unicode 转字符串
    private static String unicodeToString(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            string.append((char) data);
        }
        return string.toString();
    }

}
