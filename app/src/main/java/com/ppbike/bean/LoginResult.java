package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/6/27.
 */
public class LoginResult {

    /**
     * token : afadfadfasdfasdf
     * dstatus : 1      1未完善信息，2已经完善信息
     * mcode : ab123489     自身的邀请code，别人注册时可以填写此邀请码
     *   "nick":"qiushanyutian"   昵称
     */

    private String token;
    private int dstatus;
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

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
