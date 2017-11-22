package com.ppbike.bean;

/**
 * Created by chengmingyan on 16/7/21.
 */
public class RentBikeOrderListRequest {
    private int type;//1进行中2已完成3已取消
    private int page;//

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
