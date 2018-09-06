package com.juyoufuli.entity.bean;

public class CodeResultBean {

    public CodeResultBean(Integer staffId, String email, String safecode){
        this.staffId = staffId;
        this.email = email;
        this.safecode = safecode;
    }

    /**
     * staff_id:主键
     */
    private Integer staffId;

    /**
     * email:企业邮箱
     */
    private String email;

    /**
     * 安全码
     */
    private String safecode;

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSafecode() {
        return safecode;
    }

    public void setSafecode(String safecode) {
        this.safecode = safecode;
    }
}
