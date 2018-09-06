package com.juyoufuli.cloud.autoconfigure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.juyoufuli.common.lang.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


@Configuration
public class CoreConfiguration {


    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }

    @Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
        return new ByteArrayHttpMessageConverter();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {

        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .timeZone(TimeZone.getTimeZone("Asia/Shanghai"))
                .modules(new ParameterNamesModule())
                .modules(new Jdk8Module())
                .modules(new JavaTimeModule())
                .build();

        objectMapper.setDateFormat(new SimpleDateFormat(DateUtils.PATTERN_DEFAULT_ON_SECOND));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJacksonHttpMessageConverter.setObjectMapper(objectMapper);
        mappingJacksonHttpMessageConverter.setDefaultCharset(Charset.forName("UTF-8"));
        return mappingJacksonHttpMessageConverter;
    }


    @Bean
    @ConditionalOnProperty(
            value = {"spring.rest.connection.connection-request-timeout",
                    "spring.rest.connection.connect-timeout",
                    "spring.rest.connection.read-timeout"}
    )
    @ConfigurationProperties(prefix = "spring.rest.connection")
    public HttpComponentsClientHttpRequestFactory customHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(@Autowired(required = false) HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        RestTemplate restTemplate;
        if (null == customHttpRequestFactory) {
            restTemplate = new RestTemplate();
        } else {
            restTemplate = new RestTemplate(customHttpRequestFactory);
        }
        return restTemplate;
    }

}
