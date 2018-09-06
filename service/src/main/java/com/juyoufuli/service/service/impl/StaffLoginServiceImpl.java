package com.juyoufuli.service.service.impl;

import com.juyoufuli.cloud.service.RedisService;
import com.juyoufuli.common.config.BusinessEnum;
import com.juyoufuli.common.config.Constant;
import com.juyoufuli.common.idgen.IdGenerate;
import com.juyoufuli.common.result.ResultBean;
import com.juyoufuli.entity.bean.LoginTokenBean;
import com.juyoufuli.entity.bean.StaffInfoBean;
import com.juyoufuli.service.service.StaffInfoService;
import com.juyoufuli.service.service.StaffLoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author wuyizhe
 * @ProjectName staffcenter-service
 * @Description: 员工登录
 * @date 2018/8/13 21:48
 */
@Service
public class StaffLoginServiceImpl implements StaffLoginService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private StaffInfoService staffInfoService;


    //u有效时间
    @Value("${staffCenterRedis.utime}")
    private long utime;
    //v有效时间
    @Value("${staffCenterRedis.vtime}")
    private long vtime;

    /**
     * 验证
     * @param account  账号
     * @param password 密码
     * @param platform 平台
     * @return 登录 U、V 令牌
     */
    @Override
    public ResultBean passport(String account, String password, Integer platform) {
        //必填参数不能为空
        if (StringUtils.isBlank(account)){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_ACCOUNT_NULL);
        }
        if (StringUtils.isBlank(password)){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_PASSWORD_NULL);
        }
        if (platform==null){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_PLATFORM_NULL);
        }
        //根据帐号获取用户
        Integer staffId = (Integer)redisService.get(Constant.STAFF_ACCOUNT + account);
        //验证用户
        if(null == staffId){
            return new ResultBean(BusinessEnum.STAFF_NOT_ACCOUNT);
        }
        //获取员工信息
        StaffInfoBean staffInfo = staffInfoService.getInfoByStaffId(staffId);
        if (null == staffInfo) {
            return new ResultBean(BusinessEnum.STAFF_NOT_USERINFO);
        }
        //验证密码
        if(!staffInfoService.isPwdRight(password,staffInfo.getPwdSalt(),staffInfo.getPassword())){
            return new ResultBean(BusinessEnum.STAFF_PWD_INCORRECT);
        }
        // 删除原始令牌
        this.deleteUV(staffId, platform);
        //员工登录验证实体
        LoginTokenBean loginToken = new LoginTokenBean();
        //生成令牌 U V
        loginToken.setU(IdGenerate.uuid());
        loginToken.setV(IdGenerate.uuid());
        //保存redis,设置过期时间 604800L   18144000L
        redisService.set(Constant.STAFF_LOGIN_U + loginToken.getU(),staffId,utime);
        redisService.set(Constant.STAFF_LOGIN_V + loginToken.getV(),loginToken.getU(),vtime);
        redisService.set(Constant.STAFF_LOGIN_V_ID + loginToken.getV(),staffId,vtime);
        redisService.hset(Constant.STAFF_LOGIN + staffId,Integer.toString(platform),loginToken.getV());

        return new ResultBean(BusinessEnum.OK,loginToken);
    }

    /**
     * 根据 U 获取员工id
     * @param u u盾
     * @param platform 平台
     * @return 员工信息
     */
    //@Override
    public ResultBean staffInfoByU(String u, Integer platform) {
        //必填参数不能为空
        if(StringUtils.isBlank(u)){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_U_NULL);
        }
        if (platform == null){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_PLATFORM_NULL);
        }
        //获取redis中 U令牌信息
        Integer staffId = (Integer)redisService.get(Constant.STAFF_LOGIN_U + u);
        if (null == staffId){
            return new ResultBean(BusinessEnum.STAFF_TOKEN_INVALID);
        }
        //员工信息
        StaffInfoBean staffInfo = staffInfoService.getInfoByStaffId(staffId);
        //实际返回的员工包装类对象staffInfoVo

        return new ResultBean(BusinessEnum.OK,staffInfo);
    }

    /**
     * 根据 V 获取新的 U V 令牌
     * @param v v盾
     * @param platform 平台
     * @return 新的 U V 令牌
     */
    @Override
    public ResultBean staffInfoByV(String v, Integer platform) {
        //必填参数不能为空
        if(StringUtils.isBlank(v)){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_V_NULL);
        }
        if (platform==null){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_PLATFORM_NULL);
        }
        //获取redis中 V 令牌信息
        String u = (String)redisService.get(Constant.STAFF_LOGIN_V + v);
        Integer staffId = (Integer)redisService.get(Constant.STAFF_LOGIN_V_ID + v);
        //清除redis中的 V U
        redisService.del(Constant.STAFF_LOGIN_U + u);
        redisService.del(Constant.STAFF_LOGIN_V + v);
        redisService.del(Constant.STAFF_LOGIN_V_ID + v);
        redisService.hdel(Constant.STAFF_LOGIN + staffId, Integer.toString(platform));
        //生成新盾
        LoginTokenBean loginToken = new LoginTokenBean();
        loginToken.setU(IdGenerate.uuid());
        loginToken.setV(IdGenerate.uuid());
        //设置过期时间，保存redis  604800L   18144000L
        redisService.set(Constant.STAFF_LOGIN_U + loginToken.getU(),staffId,utime);
        redisService.set(Constant.STAFF_LOGIN_V + loginToken.getV(),loginToken.getU(),vtime);
        redisService.set(Constant.STAFF_LOGIN_V_ID + loginToken.getV(),staffId,vtime);
        redisService.hset(Constant.STAFF_LOGIN + staffId,Integer.toString(platform),loginToken.getV());

        return new ResultBean(BusinessEnum.OK,loginToken);
    }

    /**
     * 通过 U 获取工号
     * @param u u盾
     * @return 工号
     */
    @Override
    public ResultBean getStaffId(String u){
        if(StringUtils.isBlank(u)){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_U_NULL);
        }

        //工号
        Integer staffId = (Integer)redisService.get(Constant.STAFF_LOGIN_U + u);
        if (null == staffId){
            return new ResultBean(BusinessEnum.STAFF_TOKEN_INCORRECT);
        }
        return new ResultBean(BusinessEnum.OK,staffId);
}

    /**
     * 检查 U 的有效性
     * @param u u盾
     * @return 状态
     */
    @Override
    public ResultBean validity(String u) {
        if(StringUtils.isBlank(u)){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_U_NULL);
        }
        //工号
        Integer staffId = (Integer)redisService.get(Constant.STAFF_LOGIN_U + u);
        if (null == staffId){
            return new ResultBean(BusinessEnum.STAFF_TOKEN_INVALID);
        }
        return new ResultBean(BusinessEnum.OK);
    }

    /**
     * 退出登录
     * @return
     */
    @Override
    public ResultBean quit(String u,Integer platform){
        //必填参数不能为空

        if(StringUtils.isBlank(u)){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_U_NULL);
        }
        if (platform==null){
            return new ResultBean(BusinessEnum.STAFF_LOGIN_PLATFORM_NULL);
        }
        // 获取员工编号
        Integer staffId = (Integer) redisService.get(Constant.STAFF_LOGIN_U + u);
        if (null == staffId){
            return new ResultBean(BusinessEnum.STAFF_TOKEN_INVALID);
        }
        String loginV = (String) redisService.hget(Constant.STAFF_LOGIN + staffId, platform.toString());
        if (StringUtils.isBlank(loginV)){
            return new ResultBean(BusinessEnum.STAFF_TOKEN_INVALID);
        }
        String loginU = (String) redisService.get(Constant.STAFF_LOGIN_V + loginV);
        if (!u.equals(loginU)){
            return new ResultBean(BusinessEnum.STAFF_TOKEN_NOT_SAME);
        }
        //清除redis中的 V U
        redisService.del(Constant.STAFF_LOGIN_U + loginU);
        redisService.del(Constant.STAFF_LOGIN_V + loginV);
        redisService.del(Constant.STAFF_LOGIN_V_ID + loginV);
        redisService.hdel(Constant.STAFF_LOGIN + staffId, platform.toString());

        return new ResultBean(BusinessEnum.OK,"退出成功");
    }

    /**
     * 通过 U 获取工号
     * @param u u盾
     * @return 工号
     */
    @Override
    public Integer getStaffIdByU(String u) {
        return (Integer) redisService.get(Constant.STAFF_LOGIN_U + u);
    }

    /**
     * 根据工号清除令牌
     */
    @Override
    public void deleteUV(Integer staffId,Integer platform){
        //查出 U V
        String v = (String)redisService.hget(Constant.STAFF_LOGIN + staffId, platform.toString());
        String u = (String)redisService.get(Constant.STAFF_LOGIN_V + v);
        //清除redis中的 V U
        redisService.del(Constant.STAFF_LOGIN_U + u);
        redisService.del(Constant.STAFF_LOGIN_V + v);
        redisService.del(Constant.STAFF_LOGIN_V_ID + v);
        redisService.hdel(Constant.STAFF_LOGIN + staffId, platform.toString());
    }

    /**
     * 根据工号清除所有令牌
     */
    @Override
    public void deleteUVAll(Integer staffId){
        // 查询员工下所有平台登录令牌
        HashMap<String,String> tokens = (HashMap<String,String>)redisService.hmget(Constant.STAFF_LOGIN + staffId);
        for (String key: tokens.keySet()){
            String u = (String)redisService.get(Constant.STAFF_LOGIN_V + tokens.get(key));
            redisService.del(Constant.STAFF_LOGIN_U + u);
            redisService.del(Constant.STAFF_LOGIN_V + tokens.get(key));
            redisService.del(Constant.STAFF_LOGIN_V_ID + tokens.get(key));
            redisService.hdel(Constant.STAFF_LOGIN + staffId, key);
        }
    }
}


