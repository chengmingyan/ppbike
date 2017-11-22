package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/6/16.
 */
public class PerfectInfomationRequest {
    private String nick;//昵称
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

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
