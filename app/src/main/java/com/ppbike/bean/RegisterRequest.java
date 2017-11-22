package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/6/15.
 */
public class RegisterRequest {
    /**
     * {
     "mobile":"15210319731",
     "pass": "密码",
     "code:  "2468",  短信验证码
     "ycode":"acnd3455"  好友的邀请码
     }
     */
    private String mobile;
    private String code;
    private String pass;
    private String ycode;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getYcode() {
        return ycode;
    }

    public void setYcode(String ycode) {
        this.ycode = ycode;
    }
}
