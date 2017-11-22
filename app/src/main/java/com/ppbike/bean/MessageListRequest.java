package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/6/29.
 */
public class MessageListRequest {
    private long msgid;//当前最大的消息id，初始化第一页的话传0
    private int type;//1系统消息，2：活动消息

    public long getMsgid() {
        return msgid;
    }

    public void setMsgid(long msgid) {
        this.msgid = msgid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
