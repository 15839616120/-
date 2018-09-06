package com.juyoufuli.cloud.autoconfigure;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;


@Aspect
@Configuration
@ConditionalOnProperty(value="juyou.aoplog.enable",havingValue="true",matchIfMissing=false)
public class LogAspectConfiguration {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ThreadLocal<Long> START_TIME_THREAD_LOCAL = new NamedThreadLocal<>("ThreadLocal StartTime");


    @Pointcut("execution(public * com.juyoufuli.*.controller..*.*(..))")
    public void log() {
    }


    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        START_TIME_THREAD_LOCAL.set(System.currentTimeMillis()); // 线程绑定变量（该数据只有当前请求的线程可见）
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("URL : " + request.getRequestURL().toString());
        logger.info("HTTP_METHOD : " + request.getMethod());
        //logger.info("IP : " + IpUtils.getRemoteAddr(request));
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("ARGS : " + getQueryString(request));
    }


    @AfterReturning("log()")
    public void doAfterReturning(JoinPoint joinPoint) {
        logger.info("耗时（毫秒） : " + (System.currentTimeMillis() - START_TIME_THREAD_LOCAL.get()));
        START_TIME_THREAD_LOCAL.remove();
    }


    private String getQueryString(HttpServletRequest req) {
        StringBuilder buffer = new StringBuilder();
        Enumeration<String> emParams = req.getParameterNames();
        try {
            while (emParams.hasMoreElements()) {
                String param = emParams.nextElement();
                buffer.append(param).append("=").append(req.getParameter(param)).append("&");
            }
            return buffer.length() > 0 ? buffer.substring(0, buffer.length() - 1) : "";
        } catch (Exception e) {
            logger.error("get args error:", buffer.toString());
        }
        return "";
    }

}