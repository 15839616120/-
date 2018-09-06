package com.juyoufuli.service.service;

import com.juyoufuli.common.result.ResultBean;
import com.juyoufuli.entity.StaffInfo;
import com.juyoufuli.entity.bean.AddStaffBean;
import com.juyoufuli.entity.bean.ImportResultBean;
import com.juyoufuli.entity.bean.StaffInfoBean;
import com.juyoufuli.entity.bean.UpdateStaffBean;

import java.util.HashMap;
import java.util.List;

public interface StaffInfoService {

    /**
     * 添加员工
     * @param staff 员工
     * @return
     */
    ResultBean addStaff(Integer loginId, AddStaffBean staff);

    /**
     * 导入员工
     * @param staffsJson 员工集合
     * @return
     */
    ResultBean<ImportResultBean> importStaff(Integer loginId, String staffsJson);

    /**
     * 根据员工编号同步员工至 redis
     * @param staffIdsStr
     * @return
     */
    ResultBean syncByStaffIds(String staffIdsStr);

    /**
     * 同步员工到数据库
     */
    void syncDataBase();

    /**
     * 同步所有员工
     * @return
     */
    ResultBean syncAll();

    /**
     * 忘记密码 / 激活帐号
     * @param safecode 安全码
     * @param passWord 密码
     * @return
     */
    ResultBean updatePassWord(String safecode, String passWord);

    /**
     * 修改基础信息
     * @param staff 员工信息
     * @return
     */
    ResultBean updateInfo(Integer loginId, UpdateStaffBean staff);

    /**
     * 修改密码
     * @param password 密码
     * @return
     */
    ResultBean updatePwd(Integer loginId, String password);

    /**
     * 添加员工所属组
     * @param staffId 员工编号
     * @param groupId 组Id
     * @return
     */
    ResultBean addGroup(Integer loginId, Integer staffId, Long[] groupId);

    /**
     * 移除组
     * @param staffId 员工编号
     * @param groupId 组id
     * @return
     */
    ResultBean removeGroup(Integer loginId, Integer staffId, Long[] groupId);

    /**
     * 添加员工所属组（批量）
     * @param staffIds 员工编号
     * @param groupId 组Id
     * @return
     */
    ResultBean addGroup(Integer loginId, Integer[] staffIds, Long groupId);

    /**
     * 修改员工状态
     * @param staffId 员工编号
     * @param state 状态
     * @return
     */
    ResultBean updateState(Integer loginId, Integer staffId, byte state);

    /**
     * 修改员工状态（批量）
     * @return
     */
    ResultBean updateState(Integer loginId, Integer[] staffIds, byte state);

    /**
     * 根据员工编号获取安全码
     * @param staffId 员工编号
     * @return
     */
    ResultBean getSafeCodeByStaffId(Integer staffId);

    /**
     * 验证身份证号
     * @param idNum 身份证号
     * @return
     */
    ResultBean isExistsIdNum(String idNum);

    /**
     * 根据员工编号查询员工信息
     * @param staffId 员工编号
     * @return
     */
    ResultBean getAllByStaffId(Integer staffId);

    /**
     * 根据员工编号查询员工信息
     * @param staffId 员工编号
     * @return
     */
    ResultBean getLv1ByStaffId(Integer staffId);

    /**
     * 根据员工编号查询员工信息
     * @param staffId 员工编号
     * @return
     */
    ResultBean getLv2ByStaffId(Integer staffId);

    /**
     * 根据员工编号查询员工信息（批量）
     * @param staffIds 员工编号集合
     * @return
     */
    ResultBean getAllByStaffId(Integer[] staffIds);

    /**
     * 根据员工编号查询员工信息（批量）
     * @param staffIds 员工编号集合
     * @return
     */
    ResultBean getLv1ByStaffId(Integer[] staffIds);

    /**
     * 根据员工编号查询员工信息（批量）
     * @param staffIds 员工编号集合
     * @return
     */
    ResultBean getLv2ByStaffId(Integer[] staffIds);

    /**
     * 根据组Id查询用户详情
     * @param groupId
     * @return
     */
    ResultBean getByGroupId(Long groupId);

    /**
     * 根据员工编号查询员工信息
     * @param staffId 员工编号
     * @return
     */
    StaffInfoBean getInfoByStaffId(Integer staffId);

    /**
     * 根据员工编号查询员工信息（批量）
     * @param staffIds 员工编号集合
     * @return
     */
    List<StaffInfoBean> getInfoByStaffId(List<Integer> staffIds);

    /**
     * 验证密码是否正确
     * @param pwdText 明文密码
     * @param password 密码
     * @return
     */
    boolean isPwdRight(String pwdText, String salt, String password);
}
