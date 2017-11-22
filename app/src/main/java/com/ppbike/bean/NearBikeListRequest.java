package com.ppbike.bean;

import java.io.Serializable;

/**
 * Created by chengmingyan on 16/7/1.
 */
public class NearBikeListRequest implements Serializable{
    private double lo;
    private double la;
    private long stime;//取车时间
    private long etime;//还车时间

    private int date;//天数
    private int page;
    private int type;//1:按距离排序2：按价格排序

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
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

    public long getStime() {
        return stime;
    }

    public void setStime(long stime) {
        this.stime = stime;
    }

    public long getEtime() {
        return etime;
    }

    public void setEtime(long etime) {
        this.etime = etime;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
