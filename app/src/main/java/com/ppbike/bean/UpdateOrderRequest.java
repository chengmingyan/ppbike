package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/7/22.
 */
public class UpdateOrderRequest {
    private String orderId;
    private int type;//1：取消订单2：确认取车 3：确认还车

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
