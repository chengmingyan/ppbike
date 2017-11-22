package com.ppbike.bean;

import java.io.Serializable;

/**
 * Created by chengmingyan on 16/6/29.
 */
public class MessageResult implements Serializable{

    public static final String[] TYPE_VALUES = {"系统消息","活动消息","用户反馈"};
    /**
     * content : 打发打发打发点发送的范德萨的说法是短发是顶顶顶顶发反反复复凤飞飞凤飞飞凤飞飞凤飞飞凤飞飞
     * createTime : 1466574746000
     * msgid : 2
     * title : 阿萨德法师打发掉发的所发生的法师打发斯蒂芬
     * type : 1
     */

    private String content;
    private long createTime;
    private long msgid;
    private String title;
    private int type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getMsgid() {
        return msgid;
    }

    public void setMsgid(long msgid) {
        this.msgid = msgid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
