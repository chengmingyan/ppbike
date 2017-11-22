package com.ppbike.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by chengmingyan on 16/7/15.
 */
public class CreateOrderResult implements Serializable{
    private String orderId;
    private int status;//status 1创建订单，2等待付款，3，支付成功，4确认取车，5确认还车,6订单取消
    private ArrayList<PayMethod> typeList;//支付方式列表
    private long amount;//订单总额

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ArrayList<PayMethod> getTypeList() {
        return typeList;
    }

    public void setTypeList(ArrayList<PayMethod> typeList) {
        this.typeList = typeList;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
