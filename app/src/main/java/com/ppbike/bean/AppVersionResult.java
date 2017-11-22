package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/6/17.
 */
public class AppVersionResult {
    /**
     * 0不需要升级

     1可选升级

     2强制升级
     */
    private int up;
    /**
     * 版本号
     */
    private String no;
    private String desc;
    private String url;

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
