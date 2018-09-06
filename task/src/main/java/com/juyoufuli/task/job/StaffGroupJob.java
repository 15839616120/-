package com.juyoufuli.task.job;

import com.juyoufuli.cloud.feign.StaffGroupFeignClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public class StaffGroupJob implements Job {

    @Autowired
    private StaffGroupFeignClient staffGroupFeignClient;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        staffGroupFeignClient.syncStaffGroupToMysql(0);
    }
}
