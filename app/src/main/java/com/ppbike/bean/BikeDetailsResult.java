package com.ppbike.bean;

import java.io.Serializable;

/**
 * Created by chengmingyan on 16/6/21.
 */
public class BikeDetailsResult implements Serializable{
    private String name;//车名称
    private String color;//
    private String brand;//品牌
    private String speed;//
    private String size;//车辆尺寸
    private String picUrl;//图片地址
    private String pname;//所在位置
    private long bikeId;
    private long price;//租金价格
    private long nprice;//新车价格
    private long deposit;//押金
    private int count;//剩余车辆数
    private double lo;//经度
    private double la;//纬度
    private int degree;//车辆几成新
    private String mobile;//车主电话
    private int status;//1：审核中 ，2审核不通过，3：审核通过，4：下架

    public long getBikeId() {
        return bikeId;
    }

    public void setBikeId(long bikeId) {
        this.bikeId = bikeId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
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

    public long getDeposit() {
        return deposit;
    }

    public void setDeposit(long deposit) {
        this.deposit = deposit;
    }

    public double getLo() {
        return lo;
    }

    public void setLo(double lo) {
        this.lo = lo;
    }

    public double getLa() {
        return la;
    }

    public void setLa(double la) {
        this.la = la;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
