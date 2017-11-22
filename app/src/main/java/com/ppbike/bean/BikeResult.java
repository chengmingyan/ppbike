package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/7/1.
 */
public class BikeResult {
    /**
     * bikeId : 3
     * brand :  捷安特          品牌
     * color : 红色
     * count : 20
     * deposit : 100000     押金
     * distance : 1.193km
     * name : 捷安特xt100     单车名称
     * nprice : 154000        新车价格
     * picUrl : https://baidu.com
     * price : 5000           每日租金
     * size : 27寸
     * speed : 27
     */
    private long bikeId;
    private String brand;
    private String color;
    private int count;
    private long deposit;
    private String distance;
    private String name;
    private long nprice;
    private String picUrl;
    private long price;
    private long pprice;
    private String size;
    private String speed;

    public long getBikeId() {
        return bikeId;
    }

    public void setBikeId(long bikeId) {
        this.bikeId = bikeId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getDeposit() {
        return deposit;
    }

    public void setDeposit(long deposit) {
        this.deposit = deposit;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNprice() {
        return nprice;
    }

    public void setNprice(long nprice) {
        this.nprice = nprice;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public long getPprice() {
        return pprice;
    }

    public void setPprice(long pprice) {
        this.pprice = pprice;
    }
}
