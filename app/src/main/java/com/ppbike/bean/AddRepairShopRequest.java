package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/6/27.
 */
public class AddRepairShopRequest {
    /**
     * lo
     经度
     la
     纬度
     name
     修车店名称
     adress
     修车店地址
     bname
     老板名称
     mobile
     老板联系电话
     */
    private double lo;
    private double la;
    private String name;
    private String adress;
    private String bname;
    private String mobile;
    private String pic;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
