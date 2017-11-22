package com.ppbike.bean;

import java.io.Serializable;

/**
 * Created by chengmingyan on 16/7/21.
 */
public class PayMethod implements Serializable{
    private int type;
    private String desc;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
