package com.juyoufuli.service.controller;

import com.juyoufuli.common.result.ResultBean;
import com.juyoufuli.service.annotation.Auth;
import com.juyoufuli.service.controller.base.BaseController;
import com.juyoufuli.service.service.StaffLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuyizhe
 * @ProjectName staffcenter-service
 * @Description: 员工验证登录
 * @date 2018/8/15 16:46
 */
@RestController
@RequestMapping("login")
public class StaffLoginController extends BaseController {

    @Autowired
    private StaffLoginService staffLoginService;

    @Auth
    @RequestMapping(value = "test")
    public Integer test(){

        return getLoginAuthStaffId();
    }

    /**
     * 验证
     * @param account  账号
     * @param password 密码
     * @param platform 平台
     * @return 登录 U、V 令牌
     */
    @RequestMapping("passport")
    public ResultBean passport(String account, String password, Integer platform){
        return staffLoginService.passport(account, password, platform);
    }
    /**
     * 根据 V 获取新的 U V 令牌
     * @param v v盾
     * @param platform 平台
     * @return 新的 U V 令牌
     */
    @RequestMapping("getByV")
    public ResultBean staffInfoByV(String v, Integer platform){
        return staffLoginService.staffInfoByV(v, platform);
    }
    /**
     * 通过 U 获取工号
     * @param u u盾
     * @return 工号
     */
    @RequestMapping("getByU")
    public ResultBean jobNum(String u){
        return staffLoginService.getStaffId(u);
    }
    /**
     * 检查 U 的有效性
     * @param u u盾
     * @return 状态
     */
    @RequestMapping("valid")
    public ResultBean valid(String u){
        return staffLoginService.validity(u);
    }
    /**
     * 退出
     * @param u 盾
     * @return
     */
    @RequestMapping("quit")
    public ResultBean quit(String u,Integer platform){
        return staffLoginService.quit(u, platform);
    }
}
