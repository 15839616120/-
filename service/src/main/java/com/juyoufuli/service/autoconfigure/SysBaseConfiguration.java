package com.juyoufuli.service.autoconfigure;


import com.juyoufuli.service.weixin.service.SendMessageBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SysBaseConfiguration {
    @Bean
    public SendMessageBean getSendMessageBean(){
        return new SendMessageBean();
    }

}
