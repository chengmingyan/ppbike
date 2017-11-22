package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/6/21.
 */
public class UploadBikeRequest {
    private String pic;//	车辆图片先zip然后base64
    private String name;//车辆品牌信号
    private long nprice;//新车价格
    private long deposit;//押金
    private long pprice;//每天的租金
    private String size;//车辆尺寸
    private String speed;//速别
    private double lo;//经度
    private double la;//纬度
    private String pname;//所在位置
    private int count;//数量
    private String color;//
    private int degree;//车辆几成新
    private String brand;//品牌

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPprice() {
        return pprice;
    }

    public void setPprice(long pprice) {
        this.pprice = pprice;
    }

    public long getNprice() {
        return nprice;
    }

    public void setNprice(long nprice) {
        this.nprice = nprice;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public long getDeposit() {
        return deposit;
    }

    public void setDeposit(long deposit) {
        this.deposit = deposit;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
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
}
