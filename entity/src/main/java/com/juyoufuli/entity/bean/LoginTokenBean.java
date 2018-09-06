package com.juyoufuli.entity.bean;

/**
 * @author wuyizhe
 * @ProjectName staffcenter-service
 * @Description: 员工登录验证返回实体
 * @date 2018/8/16 9:05
 */
public class LoginTokenBean {

    //U盾牌
    private String U;
    //V盾牌
    private String V;

    public LoginTokenBean() {
    }

    public LoginTokenBean(String u, String v) {
        U = u;
        V = v;
    }

    public String getU() {
        return U;
    }

    public String getV() {
        return V;
    }

    public void setU(String u) {
        U = u;
    }

    public void setV(String v) {
        V = v;
    }

    @Override
    public String toString() {
        return "LoginToken{" + "U='" + U + '\'' + ", V='" + V + '\'' + '}';
    }
}
