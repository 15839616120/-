package com.juyoufuli.task.autoconfigure;

import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * <p>Title:	  SchedulerConfiguration. </p>
 * <p>Description:  Quartz配置. </p>
 *
 * @date 2018/8/15 16:41
 */
@Configuration
public class QuartzConfiguration {

    @Autowired
    private SpringJobFactory springJobFactory;


    @Bean(name = "schedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setAutoStartup(true);
        factory.setStartupDelay(5);//延时5秒启动
        factory.setJobFactory(springJobFactory);
        return factory;
    }


    /*
     * quartz初始化监听器
     */
    @Bean
    public QuartzInitializerListener executorListener() {
        return new QuartzInitializerListener();
    }


    /*
     * 通过SchedulerFactoryBean获取Scheduler的实例
     */
    @Bean(name = "scheduler")
    public Scheduler scheduler(@Autowired SchedulerFactoryBean schedulerFactory) {
        return schedulerFactory.getScheduler();
    }

}