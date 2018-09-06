package com.juyoufuli.entity.bean;

import com.juyoufuli.entity.StaffInfo;

import java.io.Serializable;
import java.util.Arrays;

public class StaffInfoBean extends StaffInfo implements Serializable {

    /**
     * 工号
     */
    private String jobNum;

    /**
     * 员工组
     */
    private Long[] groups;

    public Long[] getGroups() {
        return groups;
    }

    public void setGroups(Long[] groups) {
        this.groups = groups;
    }

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }

    @Override
    public String toString() {
        return "StaffInfoBean{" + "jobNum='" + jobNum + '\'' + ", groups=" + Arrays.toString(groups) + '}';
    }
}
