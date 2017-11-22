package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/7/21.
 */
public class PrepaidRequest {
    private String orderId;
    private int type;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
