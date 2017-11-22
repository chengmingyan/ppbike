package com.ppbike.bean;

import java.io.Serializable;

/**
 * Created by chengmingyan on 16/6/27.
 */
public class RepairShopResult implements Serializable {
    /**
     * {"addNick":"8MDoFoua","address":"东城区贡院西街光华长安大厦","distance":"2.328km","name":"","picUrl":"","shopId":54}
     */
    private String name;
    private String distance;
    private long shopId;
    private String picUrl;
    private String address;
    private String addNick;//上传人
    private String mobile;
    private String bname;
    private double lo;
    private double la;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddNick() {
        return addNick;
    }

    public void setAddNick(String addNick) {
        this.addNick = addNick;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
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
}
