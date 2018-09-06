package com.juyoufuli.cloud.autoconfigure;

import com.juyoufuli.common.config.BusinessEnum;
import com.juyoufuli.common.exception.ServiceException;
import com.juyoufuli.common.lang.FastJsonUtils;
import com.juyoufuli.common.lang.ObjectUtils;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.*;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;


@Configuration
public class FeignConfiguration {

    @Autowired
    private Environment environment;


    /**
     * 将SpringMvc 契约改为feign原生的默认契约
     *
     * @return 默认的feign契约
     */
/*    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }*/


    /**
     * Feign 日志配置
     *
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


    /**
     * 自定义feign连接超时时间
     *
     * @return
     */
    @Bean
    //@ConditionalOnProperty(value={"feign.request.connectTimeoutMillis","feign.request.readTimeoutMillis"})
    //@ConfigurationProperties(prefix = "feign.request")
    public Request.Options feignRequestOptions() {
        return new Request.Options(ObjectUtils.toInteger(environment.getProperty("feign.request.connectTimeoutMillis")),
                ObjectUtils.toInteger(environment.getProperty("feign.request.readTimeoutMillis")));
    }


    /**
     * feign 编码/解码
     *
     * @param stringHttpMessageConverter
     * @param mappingJacksonHttpMessageConverter
     * @return
     */
    @Bean
    public Decoder decoder(
            @Autowired StringHttpMessageConverter stringHttpMessageConverter,
            @Autowired MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter
    ) {

        HttpMessageConverters decodeConverters = new HttpMessageConverters(false,
                Arrays.asList(stringHttpMessageConverter, mappingJacksonHttpMessageConverter));
        ObjectFactory<HttpMessageConverters> objectFactory = () -> decodeConverters;
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }


    /**
     * feign 自定义异常处理
     * @return
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    /**
     * 传递header
     * @return
     */
//    @Bean
//    public RequestInterceptor headerInterceptor() {
//        return template -> {
//            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            HttpServletRequest request = attributes.getRequest();
//            Enumeration<String> headerNames = request.getHeaderNames();
//            if (headerNames != null) {
//                while (headerNames.hasMoreElements()) {
//                    String name = headerNames.nextElement();
//                    String values = request.getHeader(name);
//                    String name = "u";
//                    String values = "a4aaa01a2ed2431da24dffe7a536b671";
//                    template.header(name, values);
//                }
//            }
//        };
//    }

    class FeignErrorDecoder implements ErrorDecoder {

        private final org.slf4j.Logger log = LoggerFactory.getLogger(FeignErrorDecoder.class);

        private final ErrorDecoder defaultErrorDecoder = new Default();

        /**
         * Decode exception.
         *
         * @param methodKey the method key
         * @param response  the response
         * @return the exception
         */
        @Override
        public Exception decode(final String methodKey, final Response response) {
            if (response.status() >= HttpStatus.BAD_REQUEST.value() && response.status() < HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return new HystrixBadRequestException("request exception");
            }

            try {
                HashMap map = FastJsonUtils.toMap(Util.toString(response.body().asReader()));
                Integer code = (Integer) map.get("code");
                String message = (String) map.get("message");
                if (code != null) {
                    BusinessEnum errorEnum = BusinessEnum.getEnum(code);
                    if (errorEnum != null) {
                        throw new ServiceException(errorEnum);
                    } else {
                        throw new ServiceException(BusinessEnum.UNKNOWN_EXCEPTION, message);
                    }
                }
            } catch (IOException e) {
                log.info("Failed to process response body");
            }
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }

}

