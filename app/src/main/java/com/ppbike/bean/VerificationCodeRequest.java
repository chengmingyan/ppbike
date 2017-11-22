package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/6/16.
 */
public class VerificationCodeRequest {
    /**
     * 1注册
     2找回密码
     3修改手机号-给原手机号发送
     4修改手机号-给新手机号发送
     */
    public static final int TYPE_REGISTER = 1;
    public static final int TYPE_MODIFY_PASSWORD = 2;
    public static final int TYPE_SET_PASSWORD = 3;
    public static final int TYPE_UPDATEPHONE_FROM_NEW = 4;

    /**
     * {
     "type": 1,
     "mobile": "13112345678"
     }

     */
    private String mobile;
    private int type;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
