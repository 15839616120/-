package com.juyoufuli.service.service.impl;

import com.juyoufuli.cloud.service.RedisService;
import com.juyoufuli.common.codec.EncodeUtils;
import com.juyoufuli.common.codec.Md5Utils;
import com.juyoufuli.common.collect.ListUtils;
import com.juyoufuli.common.config.*;
import com.juyoufuli.common.idgen.IdGenerate;
import com.juyoufuli.common.lang.DateUtils;
import com.juyoufuli.common.lang.FastJsonUtils;
import com.juyoufuli.common.lang.StringUtils;
import com.juyoufuli.common.result.ResultBean;
import com.juyoufuli.common.text.PinyinUtils;
import com.juyoufuli.common.text.ValidatorUtils;
import com.juyoufuli.entity.RStaffGroup;
import com.juyoufuli.entity.StaffAccount;
import com.juyoufuli.entity.StaffInfo;
import com.juyoufuli.entity.bean.*;
import com.juyoufuli.service.mapper.RStaffGroupMapper;
import com.juyoufuli.service.mapper.StaffAccountMapper;
import com.juyoufuli.service.mapper.StaffInfoMapper;
import com.juyoufuli.service.service.StaffGroupService;
import com.juyoufuli.service.service.StaffInfoService;
import com.juyoufuli.service.service.StaffLoginService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
public class StaffInfoServiceImpl implements StaffInfoService {

    private static Logger logger = LoggerFactory.getLogger(StaffInfoServiceImpl.class);


    @Autowired
    private RedisService redisService;
    @Autowired
    private StaffLoginService staffLoginService;
    @Autowired
    private StaffGroupService staffGroupService;

    @Autowired
    private StaffAccountMapper staffAccountMapper;
    @Autowired
    private StaffInfoMapper staffInfoMapper;
    @Autowired
    private RStaffGroupMapper rStaffGroupMapper;

    @Value("${staffinfo.safecodeTime}")
    private Long safeCodeTime;
    @Value("${staffinfo.syncSize}")
    private int syncSize;

    /**
     * 添加员工
     * @param staff 员工
     * @return
     */
    @Override
    public ResultBean<CodeResultBean> addStaff(Integer loginId, AddStaffBean staff) {
        // 格式验证
        ResultBean resultBean = this.validateStaff(staff);
        if (null != resultBean){
            return resultBean;
        }
        // 验证组
        if (ArrayUtils.isEmpty(staff.getGroupIds())){
            return new ResultBean(BusinessEnum.STAFF_GROUP_ID_ERROR);
        }else if(!staffGroupService.isExist(staff.getGroupIds())){
            return new ResultBean(BusinessEnum.STAFF_GROUP_ID_ERROR);
        }
        // 验证身份证是否存在
        Integer idNumStaffId = (Integer) redisService.get(Constant.STAFF_ID_NUM + staff.getIdNum());
        if (null != idNumStaffId){
            return new ResultBean(BusinessEnum.ID_CARD_VERIFY_EXIST);
        }
        StaffInfoBean item = this.newInfoBean(staff);
        // 修改在职状态
        item.setBizState((byte) StaffStateEnum.BEON.getCode());
        // 保存至 redis
        this.addStaff(item,loginId);
        // 生成激活码
        String safecode = this.createSatecode(item.getStaffId());
        return new ResultBean(BusinessEnum.OK, new CodeResultBean(item.getStaffId(),item.getEmail(),safecode));
    }

    /**
     * 导入员工
     * @param staffsJson 员工集合
     * @return
     */
    @Override
    public ResultBean<ImportResultBean> importStaff(Integer loginId, String staffsJson) {
        // 解析传入数据
        List<ImportStaffBean> importStaffs = FastJsonUtils.toList(staffsJson,ImportStaffBean.class);
        if (ListUtils.isEmpty(importStaffs)){
            return new ResultBean(BusinessEnum.IMPORT_DATA_NULL);
        }
        List<ImportStaffBean> errorData = new ArrayList<>();
        List<CodeResultBean> codeResult = new ArrayList<>();
        // 遍历添加员工
        for (ImportStaffBean importItem: importStaffs){
            // 参数校验
            if (!this.importStaffValidate(importItem)){
                // 检出错误对象，添加到错误集合中，并从导入集合中删除
                errorData.add(importItem);
                continue;
            }
            // 验证身份证
            Integer staffId = (Integer)(redisService.get(Constant.STAFF_ID_NUM + importItem.getIdNum()));
            StaffInfoBean info;
            if (staffId == null){
                // 新增
                info = this.newInfoBean(importItem,loginId);
                // 修改在职状态
                info.setBizState((byte) StaffStateEnum.BEON.getCode());
                this.addStaff(info,loginId);
                // 生成激活码
                String safecode = this.createSatecode(info.getStaffId());
                codeResult.add(new CodeResultBean(info.getStaffId(),info.getEmail(),safecode));
            } else {
                // 修改
                info = this.getInfoByStaffId(staffId);
                // 赋值导入的信息
                info.setUserName(importItem.getUserName());
                info.setGender((byte)GenderEnum.getGender(importItem.getGender()).getCode());
                info.setAvatar(Constant.EMPTY);
                try {
                    info.setBirthday(DateUtils.parseDate(DateUtils.PATTERN_ISO_ON_DATE,importItem.getBirthday()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                info.setIdNum(importItem.getIdNum());
                info.setMobilePhone(importItem.getMobilePhone());
                info.setTelephone(importItem.getTelephone());
                info.setEmail(importItem.getEmail());
                info.setDuty(importItem.getDuty());
                // 组Id 转换
                String[] groupNames = StringUtils.split(importItem.getGroups(),'#');
                List<Long> groupIds = new ArrayList<>();
                for (String groupName: groupNames){
                    Long groupId = staffGroupService.importStaffGroup(loginId,groupName);
                    groupIds.add(groupId);
                }
                Long[] groupIdsArray = new Long[groupIds.size()];
                info.setGroups(groupIds.toArray(groupIdsArray));
                this.updateStaff(info,loginId);
            }
        }
        // 返回结果
        ImportResultBean result = new ImportResultBean();
        result.setErrorData(errorData);
        result.setCodeResults(codeResult);
        return new ResultBean(BusinessEnum.OK,result);
    }

    /**
     * 根据员工编号同步员工至 redis
     * @param staffIdsStr
     * @return
     */
    @Override
    public ResultBean syncByStaffIds(String staffIdsStr) {
        // 拆分数组
        String[] staffIds = StringUtils.split(staffIdsStr, ",");
        if (ArrayUtils.isEmpty(staffIds)){
            return new ResultBean(BusinessEnum.JOB_NUM_VERIFY_ERROR);
        }
        // 根据员工编号查询
        List<StaffInfoBean> staffInfos = this.newInfoBeanArray(staffInfoMapper.selectByIds(staffIds));
        if (ListUtils.isEmpty(staffInfos)){
            return new ResultBean(BusinessEnum.JOB_NUM_VERIFY_ERROR);
        }
        // 保存
        this.syncStaff(staffInfos);
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 同步所有员工至 redis
     * @return
     */
    @Override
    public ResultBean syncAll() {
        // 查询总条数
        int staffSum = staffInfoMapper.selectCount(null);
        // 计算同步次数
        int syncNum = staffSum % syncSize > 0 ? staffSum / syncSize + 1 : staffSum / syncSize;
        for (int pageIndex = 1; pageIndex <= syncNum; pageIndex++) {
            int start = (pageIndex - 1) * syncSize ;
            List<StaffInfoBean> staffInfos = this.newInfoBeanArray(staffInfoMapper.selectLimit(start,syncSize));
            // 保存
            this.syncStaff(staffInfos);
        }
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 同步员工到数据库
     */
    @Override
    public void syncDataBase() {
        // 获取同步队列中的总数
        long listSize = redisService.lGetListSize(Constant.STAFF_SYNC);
        logger.info("同步数据总数：" + listSize);
        if (listSize < 1){
            return;
        }
        for (int i = 0; i < listSize; i++) {
            // 获取员工编号
            Integer staffId = Integer.parseInt(redisService.rightPop(Constant.STAFF_SYNC).toString());
            // 查询员工信息
            StaffInfoBean staffInfo = this.getInfoByStaffId(staffId);
            // 查询员工是否存在
            int total = staffInfoMapper.selectCount(staffId);
            if (total == 0){
                // 新增
                if (staffInfoMapper.insert(staffInfo) == 0){
                    logger.error("同步数据至mysql失败-插入：" + staffId);
                }
                // 生成帐号
                String account = PinyinUtils.getFirstSpell(staffInfo.getUserName()) + staffInfo.getJobNum();
                // 插入帐号信息
                staffAccountMapper.insert(this.newAccount(staffInfo.getStaffId(),account,(byte) AccountTypeEnum.ACCOUNT.getCode()));
                // 插入组信息
                for (Long groupId: staffInfo.getGroups()){
                    rStaffGroupMapper.insert(this.newRelation(staffInfo.getStaffId(),groupId));
                }
            }else{
                // 修改
                if (staffInfoMapper.update(staffInfo) == 0){
                    logger.error("同步数据至mysql失败-更新：" + staffId);
                }
                // 修改组信息
                List<Long> relation = rStaffGroupMapper.selectBystaffId(staffInfo.getStaffId());
                List<Long> groups = Arrays.asList(staffInfo.getGroups());
                // redis 中不存在删除
                for (Long groupId: relation){
                    if (groups.contains(groupId)){
                        rStaffGroupMapper.delete(staffInfo.getStaffId(),groupId);
                    }
                }
                // mysql 中不存在新增
                for (Long groupId:groups){
                    if (relation.contains(groupId)){
                        rStaffGroupMapper.insert(this.newRelation(staffInfo.getStaffId(),groupId));
                    }
                }
            }
        }
    }

    /**
     * 忘记密码 / 激活帐号
     * @param safecode 安全码
     * @param passWord 密码
     * @return
     */
    @Override
    public ResultBean updatePassWord(String safecode, String passWord) {
        // 参数格式验证
        if (StringUtils.isBlank(safecode)){
            return new ResultBean(BusinessEnum.STAFF_SAFECODE_EXISTS);
        }
        if (StringUtils.isBlank(passWord)){
            return new ResultBean(BusinessEnum.PWD_NOT_NULL);
        }
        // 获取安全码对应员工编号
        Integer staffId = (Integer) redisService.get(Constant.STAFF_SAFECODE + safecode);
        // 安全码是否正确
        if (null == staffId){
            return new ResultBean(BusinessEnum.STAFF_SAFECODE_ERROR);
        }
        // 清除安全码
        redisService.del(Constant.STAFF_SAFECODE + safecode);
        // 根据员工编号获取员工
        StaffInfoBean staffInfo = this.getInfoByStaffId(staffId);
        // 查询员工是否存在
        if (null == staffInfo){
            return new ResultBean(BusinessEnum.JOB_NUM_VERIFY_ERROR);
        }
        // 修改员工密码
        staffInfo.setPassword(this.createPwd(passWord,staffInfo.getPwdSalt()));
        // 清除登录令牌
        staffLoginService.deleteUVAll(staffId);
        // 保存信息
        this.updateStaff(staffInfo,staffId);
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 更新员工信息
     * @param loginId 登录令牌
     * @param staff 员工信息
     * @return
     */
    @Override
    public ResultBean updateInfo(Integer loginId, UpdateStaffBean staff) {
        // 参数校验
        if (StringUtils.isBlank(staff.getEmail())) {
            return new ResultBean<>(BusinessEnum.EMAIL_NOT_NULL);
        } else if (!ValidatorUtils.isEmail(staff.getEmail())) {
            return new ResultBean<>(BusinessEnum.EMAIL_VERIFY_ERROR);
        }
        // 手机号验证
        if (StringUtils.isBlank(staff.getMobilePhone())) {
            return new ResultBean(BusinessEnum.PHONE_NOT_NULL);
        }else if (!ValidatorUtils.isMobileExact(staff.getMobilePhone())){
            return new ResultBean<>(BusinessEnum.PHONE_VERIFY_ERROR);
        }
        Date birthday = new Date(0);
        // 生日验证
        if (StringUtils.isNotBlank(staff.getBirthday())){
            try {
                birthday = DateUtils.parseDate(DateUtils.PATTERN_ISO_ON_DATE,staff.getBirthday());
            } catch (ParseException e) {
                return new ResultBean<>(BusinessEnum.STAFF_BIRTHDAY_ERROR);
            }
        }
        StaffInfoBean staffInfo;
        // 传入员工编号修改传入员工否则修改登录员工
        if (null != staff.getStaffId()){
            staffInfo = this.getInfoByStaffId(staff.getStaffId());
        }else{
            staffInfo = this.getInfoByStaffId(loginId);
        }
        // 修改信息
        staffInfo.setAvatar(staff.getAvatar());
        staffInfo.setBirthday(birthday);
        staffInfo.setMobilePhone(staff.getMobilePhone());
        staffInfo.setEmail(staff.getEmail());
        staffInfo.setTelephone(staff.getTelephone());
        // 保存信息
        this.updateStaff(staffInfo,loginId);
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 修改密码
     * @param password 密码
     * @return
     */
    @Override
    public ResultBean updatePwd(Integer loginId, String password) {
        // 获取当前登录用户
        StaffInfoBean loginInfo = this.getInfoByStaffId(loginId);
        // 重新生成密码
        loginInfo.setPassword(this.createPwd(password,loginInfo.getPwdSalt()));
        // 保存信息
        this.updateStaff(loginInfo,loginId);
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 添加员工所属组
     * @param staffId 员工编号
     * @return
     */
    @Override
    public ResultBean addGroup(Integer loginId, Integer staffId, Long[] groupIds) {
        // 获取当前登录用户
        StaffInfoBean loginInfo = this.getInfoByStaffId(loginId);
        // 验证参数格式
        if (null == staffId){
            return new ResultBean(BusinessEnum.JOB_NUM_VERIFY_ERROR);
        }
        if (ArrayUtils.isEmpty(groupIds)){
            return new ResultBean(BusinessEnum.STAFF_GROUP_IS_NULL);
        }else if (!staffGroupService.isExist(groupIds)){
            return new ResultBean(BusinessEnum.STAFF_GROUP_IS_NULL);
        }
        // 获取修改用户
        StaffInfoBean info = this.getInfoByStaffId(staffId);
        if (info == null){
            return new ResultBean(BusinessEnum.JOB_NUM_VERIFY_ERROR);
        }
        List<Long> groups = new ArrayList<>(Arrays.asList( info.getGroups()));
        for (Long groupId: groupIds){
            if (!groups.contains(groupId)){
                groups.add(groupId);
            }
        }
        // 修改组信息
        Long[] groupsArray = new Long[groups.size()];
        info.setGroups(groups.toArray(groupsArray));
        // 保存
        this.addGroup(info.getStaffId(),info.getGroups());
        this.updateStaff(info,loginId);
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 移除组
     * @param staffId 员工编号
     * @param delGroupIds 组id
     * @return
     */
    @Override
    public ResultBean removeGroup(Integer loginId, Integer staffId, Long[] delGroupIds){
        // 验证参数格式
        if (null == staffId){
            return new ResultBean(BusinessEnum.JOB_NUM_VERIFY_ERROR);
        }
        if (ArrayUtils.isEmpty(delGroupIds)){
            return new ResultBean(BusinessEnum.STAFF_GROUP_IS_NULL);
        }else if (!staffGroupService.isExist(delGroupIds)){
            return new ResultBean(BusinessEnum.STAFF_GROUP_IS_NULL);
        }
        // 获取修改用户
        StaffInfoBean info = this.getInfoByStaffId(staffId);
        List<Long> groups = new ArrayList<>(Arrays.asList(info.getGroups()));
        // 验证用户是否属于该组
        for (Long groupId: delGroupIds){
            if (!groups.contains(groupId)){
                return new ResultBean(BusinessEnum.GROUPS_VERIFY_ERROR);
            }
        }
        // 一个员工至少属于一个组
        if ((groups.size() - delGroupIds.length) < 1){
            return new ResultBean(BusinessEnum.STAFF_GROUP_RETAIN_ONE);
        }
        // 移除组下员工
        this.delGroup(staffId, delGroupIds);
        // 移除员工所属组
        for (Long groupId: delGroupIds){
            if (groups.contains(groupId)){
                groups.remove(groupId);
            }
        }
        Long[] groupIdsArray = new Long[groups.size()];
        info.setGroups((groups.toArray(groupIdsArray)));
        this.updateStaff(info,loginId);
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 添加员工所属组（批量）
     * @param staffIds 员工编号
     * @param groupId 组Id
     * @return
     */
    @Override
    public ResultBean addGroup(Integer loginId, Integer[] staffIds, Long groupId) {
        // 参数验证
        if (ArrayUtils.isEmpty(staffIds)){
            return new ResultBean(BusinessEnum.JOB_NUM_VERIFY_ERROR);
        }
        if (null == groupId){
            return new ResultBean(BusinessEnum.STAFF_GROUP_IS_NULL);
        }else if(staffGroupService.isExist(groupId)){
            return new ResultBean(BusinessEnum.STAFF_GROUP_IS_NULL);
        }
        // 修改
        List<StaffInfoBean> infos = this.getInfoByStaffId(Arrays.asList(staffIds));
        for (StaffInfoBean info: infos){
            List<Long> groups = Arrays.asList(info.getGroups());
            groups.add(groupId);
            Long[] groupIdsArray = new Long[groups.size()];
            info.setGroups((groups.toArray(groupIdsArray)));
            // 保存
            this.addGroup(info.getStaffId(),info.getGroups());
            this.updateStaff(info,loginId);
        }
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 修改员工状态
     * @param staffId 员工编号
     * @param state 状态
     * @return
     */
    @Override
    public ResultBean updateState(Integer loginId, Integer staffId, byte state) {
        // 获取员工
        StaffInfoBean info = this.getInfoByStaffId(staffId);
        if (null == info){
            return new ResultBean(BusinessEnum.JOB_NUM_VERIFY_ERROR);
        }
        if (!StaffStateEnum.isExists(state)){
            return new ResultBean(BusinessEnum.STAFF_STATE_ERROR);
        }
        info.setBizState(state);
        // 保存信息
        this.updateStaff(info,loginId);
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 修改员工状态（批量）
     * @return
     */
    @Override
    public ResultBean updateState(Integer loginId, Integer[] staffIds, byte state) {
        if (!StaffStateEnum.isExists(state)){
            return new ResultBean(BusinessEnum.STAFF_STATE_ERROR);
        }
        for (Integer staffId: staffIds){
            // 获取员工
            StaffInfoBean info = this.getInfoByStaffId(staffId);
            if (null == info){
                return new ResultBean(BusinessEnum.JOB_NUM_VERIFY_ERROR);
            }
            info.setBizState(state);
            // 保存信息
            this.updateStaff(info,loginId);
        }
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 根据员工编号获取安全码
     * @param staffId 员工编号
     * @return
     */
    @Override
    public ResultBean getSafeCodeByStaffId(Integer staffId) {
        // 获取信息
        StaffInfo staffInfo = this.getInfoByStaffId(staffId);
        if (null == staffInfo){
            return new ResultBean(BusinessEnum.JOB_NUM_VERIFY_ERROR);
        }
        if (StringUtils.isBlank(staffInfo.getEmail())){
            return new ResultBean(BusinessEnum.STAFF_EMAIL_NULL);
        }
        return new ResultBean(BusinessEnum.OK,new CodeResultBean(staffInfo.getStaffId(),staffInfo.getEmail(),this.createSatecode(staffInfo.getStaffId())));
    }

    /**
     * 创建安全码
     * @param staffId
     * @return
     */
    private String createSatecode(Integer staffId){
        // 生成安全码
        String safeCode = IdGenerate.uuid();
        // 保存
        redisService.set(Constant.STAFF_SAFECODE + safeCode,staffId,safeCodeTime);
        return safeCode;
    }

    /**
     * 验证身份证号
     * @param idNum 身份证号
     * @return
     */
    @Override
    public ResultBean isExistsIdNum(String idNum) {
        // 根据身份证查询员工
        Integer staffId = (Integer) redisService.get(Constant.STAFF_ID_NUM + idNum);
        boolean result = null != staffId;
        return new ResultBean(BusinessEnum.OK,result);
    }

    /**
     * 根据员工编号获取员工信息
     * @return
     */
    @Override
    public ResultBean getAllByStaffId(Integer staffId) {
        StaffInfoBean info = this.getInfoByStaffId(staffId);
        StaffInfoLv3Bean result = new StaffInfoLv3Bean();
        result.setStaffId(info.getStaffId());
        result.setJobNum(info.getJobNum());
        result.setUserName(info.getUserName());
        result.setGender(info.getGender());
        result.setAvatar(info.getAvatar());
        result.setBirthday(info.getBirthday());
        result.setIdNum(info.getIdNum());
        result.setMobilePhone(info.getMobilePhone());
        result.setTelephone(info.getTelephone());
        result.setEmail(info.getEmail());
        result.setDuty(info.getDuty());
        result.setCreator(info.getCreator());
        result.setModifier(info.getModifier());
        result.setGmtCreate(info.getGmtCreate());
        result.setGmtModified(info.getGmtModified());
        result.setBizState(info.getBizState());
        result.setGroupIds(rStaffGroupMapper.selectBystaffId(info.getStaffId()));
        return new ResultBean(BusinessEnum.OK,result);
    }

    /**
     * 根据员工编号获取员工信息
     * @return
     */
    @Override
    public ResultBean getLv1ByStaffId(Integer staffId) {
        StaffInfoBean info = this.getInfoByStaffId(staffId);
        StaffInfoLv1Bean result = new StaffInfoLv1Bean();
        result.setStaffId(info.getStaffId());
        result.setUserName(info.getUserName());
        result.setAvatar(info.getAvatar());
        result.setBizState(info.getBizState());;
        return new ResultBean(BusinessEnum.OK,result);
    }

    /**
     * 根据员工编号获取员工信息
     * @return
     */
    @Override
    public ResultBean getLv2ByStaffId(Integer staffId) {
        StaffInfoBean info = this.getInfoByStaffId(staffId);
        StaffInfoLv2Bean result = new StaffInfoLv2Bean();
        result.setStaffId(info.getStaffId());
        result.setJobNum(info.getJobNum());
        result.setUserName(info.getUserName());
        result.setAvatar(info.getAvatar());
        result.setMobilePhone(info.getMobilePhone());
        result.setTelephone(info.getTelephone());
        result.setEmail(info.getEmail());
        result.setDuty(info.getDuty());
        result.setBizState(info.getBizState());
        result.setGroupIds(rStaffGroupMapper.selectBystaffId(info.getStaffId()));
        return new ResultBean(BusinessEnum.OK,result);
    }

    /**
     * 根据员工编号获取员工信息
     * @return
     */
    @Override
    public ResultBean getAllByStaffId(Integer[] staffIds) {
        List<StaffInfoBean> info = this.getInfoByStaffId(Arrays.asList(staffIds));
        List<StaffInfoLv3Bean> result = new ArrayList<>();
        for (StaffInfoBean item: info){
            StaffInfoLv3Bean resultItem = new StaffInfoLv3Bean();
            resultItem.setStaffId(item.getStaffId());
            resultItem.setJobNum(item.getJobNum());
            resultItem.setUserName(item.getUserName());
            resultItem.setGender(item.getGender());
            resultItem.setAvatar(item.getAvatar());
            resultItem.setBirthday(item.getBirthday());
            resultItem.setIdNum(item.getIdNum());
            resultItem.setMobilePhone(item.getMobilePhone());
            resultItem.setTelephone(item.getTelephone());
            resultItem.setEmail(item.getEmail());
            resultItem.setDuty(item.getDuty());
            resultItem.setCreator(item.getCreator());
            resultItem.setModifier(item.getModifier());
            resultItem.setGmtCreate(item.getGmtCreate());
            resultItem.setGmtModified(item.getGmtModified());
            resultItem.setBizState(item.getBizState());
            resultItem.setGroupIds(rStaffGroupMapper.selectBystaffId(item.getStaffId()));
            result.add(resultItem);
        }
        return new ResultBean(BusinessEnum.OK,result);
    }

    /**
     * 根据员工编号获取员工信息
     * @return
     */
    @Override
    public ResultBean getLv1ByStaffId(Integer[] staffIds) {
        List<StaffInfoBean> info = this.getInfoByStaffId(Arrays.asList(staffIds));
        List<StaffInfoLv1Bean> result = new ArrayList<>();
        for (StaffInfoBean item: info){
            StaffInfoLv1Bean resultItem = new StaffInfoLv1Bean();
            resultItem.setStaffId(item.getStaffId());
            resultItem.setUserName(item.getUserName());
            resultItem.setAvatar(item.getAvatar());
            resultItem.setBizState(item.getBizState());
            result.add(resultItem);
        }
        return new ResultBean(BusinessEnum.OK,result);
    }

    /**
     * 根据员工编号获取员工信息
     * @return
     */
    @Override
    public ResultBean getLv2ByStaffId(Integer[] staffIds) {
        List<StaffInfoBean> info = this.getInfoByStaffId(Arrays.asList(staffIds));
        List<StaffInfoLv2Bean> result = new ArrayList<>();
        for (StaffInfoBean item: info){
            StaffInfoLv2Bean resultItem = new StaffInfoLv2Bean();
            resultItem.setStaffId(item.getStaffId());
            resultItem.setJobNum(item.getJobNum());
            resultItem.setUserName(item.getUserName());
            resultItem.setAvatar(item.getAvatar());
            resultItem.setMobilePhone(item.getMobilePhone());
            resultItem.setTelephone(item.getTelephone());
            resultItem.setEmail(item.getEmail());
            resultItem.setDuty(item.getDuty());
            resultItem.setBizState(item.getBizState());
            resultItem.setGroupIds(rStaffGroupMapper.selectBystaffId(item.getStaffId()));
            result.add(resultItem);
        }
        return new ResultBean(BusinessEnum.OK,result);
    }

    /**
     * 根据组Id查询用户
     * @param groupId
     * @return
     */
    @Override
    public ResultBean getByGroupId(Long groupId) {
        List<Integer> staffIds = (List<Integer>)redisService.get(Constant.STAFF_GROUP + Long.toString(groupId));
        return new ResultBean<>(BusinessEnum.OK,staffIds);
    }

    /**
     * 根据编号获取员工信息
     * @param staffId 员工编号
     * @return
     */
    @Override
    public StaffInfoBean getInfoByStaffId(Integer staffId) {
        // 验证参数
        if (null == staffId){
            return null;
        }
        // 获取员工信息
        StaffInfoBean staffInfo = (StaffInfoBean) redisService.hget(Constant.STAFF_INFO,staffId.toString());
        return staffInfo;
    }

    /**
     * 根据编号获取员工信息
     * @param staffIds 员工编号集合
     * @return
     */
    @Override
    public List<StaffInfoBean> getInfoByStaffId(List<Integer> staffIds) {
        // 验证参数
        if (ListUtils.isEmpty(staffIds)){
            return null;
        }
        List<StaffInfoBean> list = new ArrayList<>();
        // 获取员工信息
        for (Integer item: staffIds){
            StaffInfoBean staffInfo = (StaffInfoBean) redisService.hget(Constant.STAFF_INFO,item.toString());
            list.add(staffInfo);
        }
        return list;
    }

    /**
     * 验证密码是否正确
     * @param pwdText 明文密码
     * @param password 密码
     * @return
     */
    @Override
    public boolean isPwdRight(String pwdText, String salt, String password) {
        if (StringUtils.isEmpty(password)){
            return false;
        }
        String newPwd = this.createPwd(pwdText, salt);
        return password.equals(newPwd);
    }

    /**
     * 员工信息转换
     * @param staff
     */
    private StaffInfoBean newInfoBean(AddStaffBean staff){
        StaffInfoBean item = new StaffInfoBean();
        item.setUserName(staff.getUserName());
        item.setGender(staff.getGender());
        item.setAvatar(staff.getAvatar());
        try {
            item.setBirthday(DateUtils.parseDate(DateUtils.PATTERN_ISO_ON_DATE,staff.getBirthday()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        item.setIdNum(staff.getIdNum());
        item.setMobilePhone(staff.getMobilePhone());
        item.setTelephone(staff.getTelephone());
        item.setEmail(staff.getEmail());
        item.setDuty(staff.getDuty());
        item.setGroups(staff.getGroupIds());
        return item;
    }

    /**
     * <p>Title newInfoBean.员工信息转换 </p>
     * <p>Description: </p>
     * @param importStaff 导入实体
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 15:48
     * @return com.juyoufuli.entity.bean.StaffInfoBean
     */
    private StaffInfoBean newInfoBean(ImportStaffBean importStaff, Integer creator){
        StaffInfoBean staffInfo = new StaffInfoBean();
        staffInfo.setUserName(importStaff.getUserName());
        staffInfo.setGender((byte)GenderEnum.getGender(importStaff.getGender()).getCode());
        staffInfo.setAvatar(Constant.EMPTY);
        try {
            staffInfo.setBirthday(DateUtils.parseDate(DateUtils.PATTERN_ISO_ON_DATE,importStaff.getBirthday()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        staffInfo.setIdNum(importStaff.getIdNum());
        staffInfo.setMobilePhone(importStaff.getMobilePhone());
        staffInfo.setTelephone(importStaff.getTelephone());
        staffInfo.setEmail(importStaff.getEmail());
        staffInfo.setDuty(importStaff.getDuty());
        // 组Id 转换
        String[] groupNames = StringUtils.split(importStaff.getGroups(),'#');
        List<Long> groupIds = new ArrayList<>();
        for (String groupName: groupNames){
            Long groupId = staffGroupService.importStaffGroup(creator,groupName);
            groupIds.add(groupId);
        }
        Long[] groupIdsArray = new Long[groupIds.size()];
        staffInfo.setGroups(groupIds.toArray(groupIdsArray));
        return staffInfo;
    }

    /**
     * 实体转换
     * @param staff
     * @return
     */
    private StaffInfoBean newInfoBean(StaffInfo staff){
        StaffInfoBean item = new StaffInfoBean();
        item.setStaffId(staff.getStaffId());
        item.setJobNum(this.createJobNum(staff.getStaffId()));
        item.setUserName(staff.getUserName());
        item.setGender(staff.getGender());
        item.setAvatar(staff.getAvatar());
        item.setBirthday(staff.getBirthday());
        item.setIdNum(staff.getIdNum());
        item.setMobilePhone(staff.getMobilePhone());
        item.setTelephone(staff.getTelephone());
        item.setEmail(staff.getEmail());
        item.setDuty(staff.getDuty());
        item.setModifier(staff.getModifier());
        item.setBizState(staff.getBizState());
        item.setState(staff.getState());
        item.setPassword(staff.getPassword());
        item.setPwdSalt(staff.getPwdSalt());
        item.setGmtCreate(staff.getGmtCreate());
        item.setGmtModified(staff.getGmtModified());
        return item;
    }

    /**
     * 实体转换
     * @param staffs
     * @return
     */
    private List<StaffInfoBean> newInfoBeanArray(List<StaffInfo> staffs){
        List<StaffInfoBean> list = new ArrayList<>();
        for (StaffInfo item: staffs){
            list.add(this.newInfoBean(item));
        }
        return list;
    }

    /**
     * 创建帐号实体
     * @return
     */
    public StaffAccount newAccount(int staffId, String account, byte accountType){
        StaffAccount model = new StaffAccount();
        model.setAccount(account);
        model.setStaffId(staffId);
        model.setAccountType(accountType);
        model.setGmtCreate(new Date());
        model.setGmtModified(model.getGmtCreate());
        model.setState((byte) 0);
        return model;
    }

    /**
     * 创建关系实体
     * @param staffId 员工编号
     * @param groupId 组Id
     * @return
     */
    private RStaffGroup newRelation(Integer staffId, Long groupId){
        RStaffGroup rStaffGroup = new RStaffGroup();
        rStaffGroup.setStaffId(staffId);
        rStaffGroup.setGroupId(groupId);
        rStaffGroup.setGmtCreate(new Date());
        rStaffGroup.setGmtModified(rStaffGroup.getGmtCreate());
        rStaffGroup.setState((byte) 0);
        return rStaffGroup;
    }

    /**
     * <p>Title createStaff.新增员工 </p>
     * <p>Description: </p>
     * @param info 员工信息
     * @param creator 创建人
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 14:50
     * @return void
     */
    private void addStaff(StaffInfoBean info, Integer creator){
        // 创建人创建时间
        info.setCreator(creator);
        info.setGmtCreate(new Date());
        // 修改人修改时间
        info.setModifier(creator);
        info.setGmtModified(new Date());
        // 自增Id
        int staffId = (int) redisService.incr(Constant.STAFF_INFO_INDEX, 1);
        info.setStaffId(staffId);
        // 生成工号
        info.setJobNum(this.createJobNum(info.getStaffId()));
        // 生成帐号
        String account = PinyinUtils.getFirstSpell(info.getUserName()) + info.getJobNum();
        // 密码盐
        info.setPwdSalt(this.createSalt());
        info.setPassword(Constant.EMPTY);
        // 状态
        info.setState((byte) 0);
        // 存储信息
        redisService.hset(Constant.STAFF_INFO,info.getStaffId().toString(),info);
        redisService.set(Constant.STAFF_ID_NUM + info.getIdNum(), info.getStaffId());
        redisService.set(Constant.STAFF_ACCOUNT + account, info.getStaffId());
        this.addGroup(info.getStaffId(), info.getGroups());
        redisService.leftPush(Constant.STAFF_SYNC, info.getStaffId());
    }

    /**
     * <p>Title updateStaff.修改员工（不修改员工组信息） </p>
     * <p>Description: </p>
     * @param info 组信息
     * @param modifier 修改人
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 15:00
     * @return void
     */
    private void updateStaff(StaffInfoBean info, Integer modifier){
        // 修改人修改时间
        info.setModifier(modifier);
        info.setGmtModified(new Date());
        // 存储信息
        redisService.hset(Constant.STAFF_INFO,info.getStaffId().toString(),info);
        redisService.leftPush(Constant.STAFF_SYNC, info.getStaffId());
    }

    /**
     * <p>Title addGroup.员工添加组（追加）</p>
     * <p>Description: </p>
     * @param staffId 员工Id
     * @param groupIds 组Id
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 14:58
     * @return void
     */
    private void addGroup(Integer staffId, Long[] groupIds){
        for (Long groupId: groupIds){
            // 取出组对应员工
            List<Integer> staffIds = (List<Integer>) redisService.get(Constant.STAFF_GROUP_RELATION + groupId);
            if (ListUtils.isEmpty(staffIds)){
                staffIds = new ArrayList<>();
            }
            // 员工是否已存在
            if (!staffIds.contains(staffId)){
                staffIds.add(staffId);
            }
            redisService.set(Constant.STAFF_GROUP_RELATION + groupId,staffIds);
        }
    }

    /**
     * <p>Title replaceGroup.员工添加组（覆盖） </p>
     * <p>Description: </p>
     * @param staffId 员工Id
     * @param groupIds 组Id
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 15:16
     * @return void
     */
    private void addGroup(Integer staffId, Long[] groupIds, Long[] delGroupIds){
        this.delGroup(staffId,delGroupIds);
        this.addGroup(staffId,groupIds);
    }

    /**
     * <p>Title delGroup.删除员工所属组 </p>
     * <p>Description: </p>
     * @param staffId
     * @param delGroupIds
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 17:02
     * @return void
     */
    private void delGroup(Integer staffId, Long[] delGroupIds){
        for (Long groupId: delGroupIds){
            String groupKey = Constant.STAFF_GROUP_RELATION + groupId;
            List<Integer> staffIds = (List<Integer>) redisService.get(groupKey);
            staffIds.remove(staffId);
            redisService.set(groupKey, staffIds);
        }
    }

    /**
     * <p>Title syncStaff.同步员工信息 </p>
     * <p>Description: </p>
     * @param staffInfos 员工信息集合
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 16:47
     * @return void
     */
    private void syncStaff(List<StaffInfoBean> staffInfos){
        for (StaffInfoBean info: staffInfos){
            List<Long> groupIds = rStaffGroupMapper.selectBystaffId(info.getStaffId());
            Long[] groupIdArray = new Long[groupIds.size()];
            groupIds.toArray(groupIdArray);
            info.setGroups(groupIdArray);
            // 保存员信息
            redisService.hset(Constant.STAFF_INFO,info.getStaffId().toString(),info);
            redisService.set(Constant.STAFF_ID_NUM + info.getIdNum(), info.getStaffId());
            // 保存帐号信息
            List<String> accounts = staffAccountMapper.getAccountByStaffId(info.getStaffId());
            for (String account: accounts){
                redisService.set(Constant.STAFF_ACCOUNT + account, info.getStaffId());
            }
            // 保存所属组信息
            this.addGroup(info.getStaffId(), groupIdArray);
        }
    }

    /**
     * <p>Title createJobNum.生成员工编号 </p>
     * <p>Description: </p>
     * @param staffId 员工Id
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 15:22
     * @return java.lang.String
     */
    private String createJobNum(Integer staffId){
        StringBuilder jobNum = new StringBuilder();
        jobNum.append(Constant.STAFF_JOB_NUM_PREFIX);
        jobNum.append(String.format("%5d", staffId).replace(" ", "0"));
        return jobNum.toString();
    }

    /**
     * <p>Title createSalt.生成密码盐 </p>
     * <p>Description: </p>
     * @param
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 15:23
     * @return java.lang.String
     */
    private String createSalt(){
        // 生成随机数
        double random = Math.random();
        // md5 加密
        String md5Random = Md5Utils.md5(Double.toString(random));
        // 截取前十位
        String salt = StringUtils.substring(md5Random,0,10);
        return salt;
    }

    /**
     * <p>Title createPwd.生成密码 </p>
     * <p>Description: </p>
     * @param password 明文密码
     * @param salt 盐
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 15:23
     * @return java.lang.String
     */
    private String createPwd(String password, String salt){
        // md5 加密
        String md5Pwd = Md5Utils.md5(password);
        // 加盐
        String pwdSalt = md5Pwd + salt;
        return Md5Utils.md5(pwdSalt);
    }

    /**
     * 员工信息格式验证
     */
    private ResultBean validateStaff(AddStaffBean staff){
        // 员工姓名验证
        if (StringUtils.isBlank(staff.getUserName())) {
            return new ResultBean<>(BusinessEnum.STAFF_USERNAME_NULL);
        }
        // 性别验证
        if (staff.getGender() == null) {
            return new ResultBean<>(BusinessEnum.GENDER_VERIFY_ERROR);
        }else if (!GenderEnum.isExists(staff.getGender())){
            return new ResultBean<>(BusinessEnum.GENDER_VERIFY_ERROR);
        }
        // 身份证号验证
        if (StringUtils.isBlank(staff.getIdNum())) {
            return new ResultBean<>(BusinessEnum.ID_CARD_NOT_NULL);
        } else if (!ValidatorUtils.isIdCard(staff.getIdNum())) {
            return new ResultBean<>(BusinessEnum.ID_CARD_VERIFY_ERROR);
        }
        // 手机号验证
        if (StringUtils.isBlank(staff.getMobilePhone())) {
            return new ResultBean<>(BusinessEnum.PHONE_NOT_NULL);
        } else if (!ValidatorUtils.isMobileExact(staff.getMobilePhone())) {
            return new ResultBean<>(BusinessEnum.PHONE_VERIFY_ERROR);
        }
        // 企业邮箱验证
        if (StringUtils.isBlank(staff.getEmail())) {
            return new ResultBean<>(BusinessEnum.EMAIL_NOT_NULL);
        } else if (!ValidatorUtils.isEmail(staff.getEmail())) {
            return new ResultBean<>(BusinessEnum.EMAIL_VERIFY_ERROR);
        }
        // 职务验证
        if (StringUtils.isBlank(staff.getDuty())) {
            return new ResultBean<>(BusinessEnum.DUTY_NOT_NULL);
        }
        return null;
    }

    /**
     * 导入数据验证
     * @param item
     * @return
     */
    private boolean importStaffValidate(ImportStaffBean item){
        // 姓名验证
        if (StringUtils.isBlank(item.getUserName())){
            return false;
        }
        // 性别验证
        if (!GenderEnum.isExists(item.getGender())){
            return false;
        }
        // 生日校验
        if (StringUtils.isBlank(item.getBirthday())){
            return false;
        }
        Date birthday;
        try {
            birthday = DateUtils.parseDate(DateUtils.PATTERN_ISO_ON_DATE,item.getBirthday());
        } catch (ParseException e) {
            return false;
        }
        // 身份证号验证
        if (StringUtils.isBlank(item.getIdNum())) {
            return false;
        } else if (!ValidatorUtils.isIdCard(item.getIdNum())) {
            return false;
        }
        // 手机号验证
        if (StringUtils.isBlank(item.getMobilePhone())) {
            return false;
        } else if (!ValidatorUtils.isMobileExact(item.getMobilePhone())) {
            return false;
        }
        // 企业邮箱验证
        if (StringUtils.isBlank(item.getEmail())) {
            return false;
        } else if (!ValidatorUtils.isEmail(item.getEmail())) {
            return false;
        }
        // 职务验证
        if (StringUtils.isBlank(item.getDuty())) {
            return false;
        }
        // 所属组验证
        if (StringUtils.isBlank(item.getGroups())){
            return false;
        }
        return true;
    }
}
