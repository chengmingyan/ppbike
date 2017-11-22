package com.ppbike.bean;

import java.io.Serializable;

/**
 * Created by chengmingyan on 16/6/18.
 */
public class UserBean implements Serializable{
    public static final int DSTATUS_NOT_PERFECT = 1;
    private String token;
    /**
     * 1未完善信息，2已经完善信息
     */
    private int dstatus = DSTATUS_NOT_PERFECT;
    /**
     *  自身的邀请code，别人注册时可以填写此邀请码
     */
    private String mcode;
    private String nick;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getDstatus() {
        return dstatus;
    }

    public void setDstatus(int dstatus) {
        this.dstatus = dstatus;
    }

    public String getMcode() {
        return mcode;
    }

    public void setMcode(String mcode) {
        this.mcode = mcode;
    }

    private String name;
    private String icard;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcard() {
        return icard;
    }

    public void setIcard(String icard) {
        this.icard = icard;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    private String picUrl;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
