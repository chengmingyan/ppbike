package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/7/15.
 */
public class CreateOrderRequest extends NearBikeListRequest {

    private long bikeId;
    private int count;//	辆数
    private long money;//租金
    private long deposit;//押金

    public long getBikeId() {
        return bikeId;
    }

    public void setBikeId(long bikeId) {
        this.bikeId = bikeId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getDeposit() {
        return deposit;
    }

    public void setDeposit(long deposit) {
        this.deposit = deposit;
    }
}
