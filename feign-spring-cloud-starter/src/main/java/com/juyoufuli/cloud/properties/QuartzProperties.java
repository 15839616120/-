package com.juyoufuli.cloud.properties;

import com.juyoufuli.common.collect.ListUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;


@ConfigurationProperties(prefix = "juyou.quartz")
public class QuartzProperties {

    //private final List<QuartzProperties.Quartz> multi = new ArrayList<>();
    private final List<Map<String, String>> multi = ListUtils.newArrayList();

    public QuartzProperties() {
    }

    public static QuartzProperties create() {
        return new QuartzProperties();
    }

    public Quartz build() {
        return new QuartzProperties.Quartz();
    }

    public class Quartz {

        public Quartz() {
        }

        /**
         * 任务名称
         */
        private String jobName;

        /**
         * 任务分组
         */
        private String jobGroup;

        /**
         * 任务描述
         */
        private String description;

        /**
         * 执行类(类包名)
         */
        private String jobClassName;

        /**
         * 执行时间(cron表达式)
         */
        private String cronExpression;


        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public String getJobGroup() {
            return jobGroup;
        }

        public void setJobGroup(String jobGroup) {
            this.jobGroup = jobGroup;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getJobClassName() {
            return jobClassName;
        }

        public void setJobClassName(String jobClassName) {
            this.jobClassName = jobClassName;
        }

        public String getCronExpression() {
            return cronExpression;
        }

        public void setCronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
        }
    }

    public List<Map<String, String>> getMulti() {
        return multi;
    }
}
