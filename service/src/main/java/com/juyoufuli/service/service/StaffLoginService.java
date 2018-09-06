package com.juyoufuli.service.service;

import com.juyoufuli.common.result.ResultBean;

/**
 * @author wuyizhe
 * @ProjectName staffcenter-service
 * @Description: 员工登陆
 * @date 2018/8/13 21:48
 */
public interface StaffLoginService {

    /**
     * 验证
     * @param account  账号
     * @param password 密码
     * @param platform 平台
     * @return 登录 U、V 令牌
     */
    ResultBean passport(String account, String password, Integer platform);

    /**
     * 根据 V 获取新的 U V 令牌
     * @param v v盾
     * @param platform 平台
     * @return 新的 U V 令牌
     */
    ResultBean staffInfoByV(String v, Integer platform);
    /**
     * 通过 U 获取工号
     * @param u u盾
     * @return 工号
     */
    ResultBean getStaffId(String u);
    /**
     * 检查 U 的有效性
     * @param u u盾
     * @return 状态
     */
    ResultBean validity(String u);
    /**
     * 通过 U 获取工号
     * @param u u盾
     * @return 工号
     */
    Integer getStaffIdByU(String u);

    /**
     * 根据工号清除所有令牌
     * @param staffId 员工编号
     * @param platform 平台
     */
    void deleteUV(Integer staffId,Integer platform);

    /**
     * 根据工号清除所有令牌
     * @param staffId 员工编号
     */
    void deleteUVAll(Integer staffId);
    /**
     * 退出
     * @param u
     * @return
     */
    public ResultBean quit(String u,Integer platform);
}
