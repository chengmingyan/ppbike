package com.shelwee.update.pojo;


/**
 * Created by ShelWee on 14-5-8.
 */
public class UpdateInfo {
    /**
     * 版本号
     */
    private String no;
    private String desc;
    private String url;
    private String size;
    /**
     * 0不需要升级

     1可选升级

     2强制升级
     */
    private int up;

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
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

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }
}
