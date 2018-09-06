package com.juyoufuli.service.config;

import com.juyoufuli.cloud.service.RedisService;
import com.juyoufuli.common.config.Constant;
import com.juyoufuli.common.lang.StringUtils;
import com.juyoufuli.common.utils.ThreadLocalContext;
import com.juyoufuli.service.annotation.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;


@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private static final String[]     INCLUDE_URLS = {"/staffGroup","/info"};
    @Autowired
    private RedisService redisService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();
        //是否需要过滤
        if (!isNeedFilter(uri)) {
            return true;
        }


        //是否带有Auth注解
        if (isHaveAccess(handler)) {
            String token = request.getHeader(Constant.AUTHORIZATION_U_VALUE);
            if (StringUtils.isBlank(token)) {
                handleException(response);
                return false;
            }
            Integer staffId = (Integer) redisService.get(Constant.STAFF_LOGIN_U + token);
            ThreadLocalContext.put(Constant.TOKEN_AUTH_STAFF_ID, staffId);
        }
        return true;
    }

    public boolean isNeedFilter(String uri) {
        for (String includeUrl : INCLUDE_URLS) {
            if (uri.contains(includeUrl)) {
                return true;
            }
        }
        return false;
    }


    private void handleException(HttpServletResponse res) throws IOException {
        res.resetBuffer();
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"code\":-401 ,\"msg\" :\"token不正确\"}");
        res.flushBuffer();
        res.getWriter().close();
    }


    private boolean isHaveAccess(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Auth auth = AnnotationUtils.findAnnotation(method, Auth.class);
        return auth != null;
    }

}
  