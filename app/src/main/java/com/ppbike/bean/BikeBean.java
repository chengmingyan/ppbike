package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/6/17.
 */
public class BikeBean {

    private long bikeId;
    private long userId;
    private String pic;
    private String name;
    private long nPrice;
    private long oPrice;
    private long pPrice;
    private String size;
    private String buyTime;
    private double lo;
    private double la;
    private String pName;
    private String desc;
    private int degree;

    public long getBikeId() {
        return bikeId;
    }

    public void setBikeId(long bikeId) {
        this.bikeId = bikeId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getnPrice() {
        return nPrice;
    }

    public void setnPrice(long nPrice) {
        this.nPrice = nPrice;
    }

    public long getoPrice() {
        return oPrice;
    }

    public void setoPrice(long oPrice) {
        this.oPrice = oPrice;
    }

    public long getpPrice() {
        return pPrice;
    }

    public void setpPrice(int pPrice) {
        this.pPrice = pPrice;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(String buyTime) {
        this.buyTime = buyTime;
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

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}
