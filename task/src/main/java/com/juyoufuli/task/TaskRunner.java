package com.juyoufuli.task;

import com.juyoufuli.cloud.properties.QuartzProperties;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@EnableConfigurationProperties(QuartzProperties.class)
public class TaskRunner implements ApplicationRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(TaskRunner.class);

    @Autowired
    @Qualifier("scheduler")
    private Scheduler scheduler;
    @Autowired
    private QuartzProperties quartzProperties;


    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void run(ApplicationArguments args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SchedulerException {
        LOGGER.info("------>> quartz init...");
        QuartzProperties.Quartz quartz;
        for (Map<String, String> map : quartzProperties.getMulti()) {
            quartz = QuartzProperties.create().build();
            quartz.setJobName(map.get("jobName"));
            quartz.setJobGroup(map.get("jobGroup"));
            quartz.setDescription(map.get("description"));
            quartz.setJobClassName(map.get("jobClassName"));
            quartz.setCronExpression(map.get("cronExpression"));
            Class cls = Class.forName(quartz.getJobClassName());
            cls.newInstance();
            //构建job信息
            JobDetail job = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(), quartz.getJobGroup())
                    .withDescription(quartz.getDescription()).build();
            // 触发时间点
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger" + quartz.getJobName(), quartz.getJobGroup())
                    .startNow().withSchedule(cronScheduleBuilder).build();
            //交由Scheduler安排触发
            scheduler.scheduleJob(job, trigger);
            LOGGER.info("------>> " + quartz.getJobGroup() + "." + quartz.getJobName() + " starting...");
        }
    }

}