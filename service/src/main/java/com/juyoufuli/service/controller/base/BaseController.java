package com.juyoufuli.service.controller.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.juyoufuli.common.config.BusinessEnum;
import com.juyoufuli.common.config.Constant;
import com.juyoufuli.common.exception.TokenException;
import com.juyoufuli.common.lang.StringUtils;
import com.juyoufuli.common.result.ResultBean;
import com.juyoufuli.common.utils.ThreadLocalContext;
import com.juyoufuli.entity.bean.AddStaffBean;
import com.juyoufuli.service.service.StaffLoginService;
import org.apache.ibatis.plugin.Intercepts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class BaseController {
    @Autowired
    private StaffLoginService staffLoginService;
    @Autowired
    private HttpServletRequest request;


    protected Integer getLoginAuthStaffId() {
        Integer staffId = ThreadLocalContext.get(Constant.TOKEN_AUTH_STAFF_ID);
        if (null == staffId) {
            throw new TokenException(BusinessEnum.TOKEN_EXPIRED_ERROR);
        }
        return staffId;
    }

    protected Integer getStaffId() {
        //用的时候做非空校验
        return staffLoginService.getStaffIdByU(request.getParameter("u"));
    }

    protected void validateU() {

    }
}
