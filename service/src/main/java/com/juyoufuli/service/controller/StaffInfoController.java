package com.juyoufuli.service.controller;

import com.juyoufuli.common.config.BusinessEnum;
import com.juyoufuli.common.result.ResultBean;
import com.juyoufuli.entity.bean.AddStaffBean;
import com.juyoufuli.entity.bean.UpdateStaffBean;
import com.juyoufuli.service.annotation.Auth;
import com.juyoufuli.service.controller.base.BaseController;
import com.juyoufuli.service.service.StaffInfoService;
import com.juyoufuli.service.service.StaffLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("info")
public class StaffInfoController extends BaseController {


    @Autowired
    private StaffInfoService staffInfoService;
    @Autowired
    private StaffLoginService staffLoginService;
    /**
     * 添加员工
     * @param staff 员工信息
     * @return
     */
    @Auth
    @RequestMapping(value="add",method=RequestMethod.POST)
    public ResultBean add(@RequestBody AddStaffBean staff){
        return staffInfoService.addStaff(getLoginAuthStaffId(),staff);
    }

    /**
     * 批量导入员工
     * @param staffsJson
     * @return
     */
    @Auth
    @RequestMapping(value = "import",method = RequestMethod.GET)
    public ResultBean importStaff(String staffsJson){
        return staffInfoService.importStaff(this.getLoginAuthStaffId(),staffsJson);
    }

    /**
     * 根据员工编号同步至 redis
     * @param staffIds
     * @return
     */
    @RequestMapping(value="sync",method = RequestMethod.GET)
    public ResultBean sync(String staffIds){
        return staffInfoService.syncByStaffIds(staffIds);
    }

    /**
     * 全部员工同步至 redis
     * @return
     */
    @RequestMapping(value="syncAll",method = RequestMethod.GET)
    public ResultBean syncAll(){
        return staffInfoService.syncAll();
    }

    /**
     * 获取安全码
     * @param staffId
     * @return
     */
    @RequestMapping(value="getCode",method = RequestMethod.GET)
    public ResultBean getCode(Integer staffId){
        return staffInfoService.getSafeCodeByStaffId(staffId);
    }

    /**
     * 激活帐号 / 忘记密码
     * @param safecode 安全码
     * @param passWord 密码
     * @return
     */
    @RequestMapping(value="activate",method = RequestMethod.GET)
    public ResultBean activate(String safecode, String passWord){
        return staffInfoService.updatePassWord(safecode,passWord);
    }

    /**
     * 修改基础信息
     * @return
     */
    @Auth
    @PostMapping("updateInfo")
    public ResultBean updateInfo(@RequestBody UpdateStaffBean staff){
        return staffInfoService.updateInfo(getLoginAuthStaffId(),staff);
    }

    /**
     * 修改密码
     * @return
     */
    @Auth
    @RequestMapping(value="updatePwd",method = RequestMethod.GET)
    public ResultBean updatePwd(String password){
        return staffInfoService.updatePwd(getLoginAuthStaffId(),password);
    }

    /**
     * 添加员工所属组
     * @return
     */
    @Auth
    @RequestMapping(value="addGroup",method = RequestMethod.GET)
    public ResultBean addGroup(Integer staffId, Long[] groupId){
        return staffInfoService.addGroup(getLoginAuthStaffId(),staffId,groupId);
    }

    /**
     * 移除员工所属组
     * @return
     */
    @Auth
    @RequestMapping(value="removeGroup",method = RequestMethod.GET)
    public ResultBean removeGroup(Integer staffId, Long[] groupId){
        return staffInfoService.removeGroup(getLoginAuthStaffId(),staffId,groupId);
    }

    /**
     * 添加员工所属组（批量）
     * @return
     */
    @Auth
    @RequestMapping(value="addGroupBatch",method = RequestMethod.GET)
    public ResultBean addGroupBatch(Integer[] staffIds, Long groupId){
        return staffInfoService.addGroup(getLoginAuthStaffId(),staffIds, groupId);
    }

    /**
     * 修改员工状态
     * @return
     */
    @Auth
    @RequestMapping(value="updateState",method = RequestMethod.GET)
    public ResultBean updateState(Integer staffId, byte state){
        return staffInfoService.updateState(getLoginAuthStaffId(),staffId, state);
    }

    /**
     * 修改员工状态（批量）
     * @return
     */
    @Auth
    @RequestMapping(value="updateStateBatch",method = RequestMethod.GET)
    public ResultBean updateStateBatch(Integer[] staffIds, byte state){
        return staffInfoService.updateState(getLoginAuthStaffId(),staffIds, state);
    }

    /**
     * 修改员工状态（批量）
     * @return
     */
    @Auth
    @RequestMapping(value="getCodeByEmail",method = RequestMethod.GET)
    public ResultBean getCodeByEmail(Integer[] staffIds, byte state){
        return staffInfoService.updateState(getLoginAuthStaffId(),staffIds, state);
    }

    /**
     * 验证身份证号
     * @return
     */
    @RequestMapping(value="isExistsIdNum",method = RequestMethod.GET)
    public ResultBean isExistsIdNum(String idNum){
        return staffInfoService.isExistsIdNum(idNum);
    }

    /**
     * 根据员工编号查询员工信息
     * @return
     */
    @RequestMapping(value="getByStaffId",method = RequestMethod.GET)
    public ResultBean getByStaffId(Integer staffId){
        return staffInfoService.getAllByStaffId(staffId);
    }

    /**
     * 根据员工编号查询员工信息
     * @return
     */
    @RequestMapping(value="getLv1ByStaffId",method = RequestMethod.GET)
    public ResultBean getLv1ByStaffId(Integer staffId){
        return staffInfoService.getLv1ByStaffId(staffId);
    }

    /**
     * 根据员工编号查询员工信息
     * @return
     */
    @RequestMapping(value="getLv2ByStaffId",method = RequestMethod.GET)
    public ResultBean getLv2ByStaffId(Integer staffId){
        return staffInfoService.getLv2ByStaffId(staffId);
    }

    /**
     * 根据员工编号查询员工信息（批量）
     * @return
     */
    @RequestMapping(value="getAllByStaffIds",method = RequestMethod.GET)
    public ResultBean getAllByStaffId(Integer[] staffIds){
        return staffInfoService.getAllByStaffId(staffIds);
    }

    /**
     * 根据员工编号查询员工信息（批量）
     * @return
     */
    @RequestMapping(value="getLv1ByStaffIds",method = RequestMethod.GET)
    public ResultBean getLv1ByStaffId(Integer[] staffIds){
        return staffInfoService.getLv1ByStaffId(staffIds);
    }

    /**
     * 根据组Id查询用户
     * @return
     */
    @RequestMapping(value="getByGroupId",method = RequestMethod.GET)
    public ResultBean getByGroupId(Long groupId){
        return staffInfoService.getByGroupId(groupId);
    }

    /**
     * 根据员工编号查询员工信息（批量）
     * @return
     */
    @RequestMapping(value="getLv2ByStaffIds",method = RequestMethod.GET)
    public ResultBean getLv2ByStaffId(Integer[] staffIds){
        return staffInfoService.getLv2ByStaffId(staffIds);
    }

    /**
     * <p>Title syncDataBase.同步员工至 Redis </p>
     * <p>Description: </p>
     * @param
     * @author <a href="zhanghw@huayingcul1.com"/>张宏伟</a>
     * @date 2018-08-30 17:33
     * @return com.juyoufuli.common.result.ResultBean
     */
    @RequestMapping(value="syncDataBase",method = RequestMethod.GET)
    public ResultBean syncDataBase(){
        staffInfoService.syncDataBase();
        return new ResultBean(BusinessEnum.OK);
    }
}
