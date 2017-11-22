package com.ppbike.bean;

import java.util.ArrayList;

/**
 * Created by chengmingyan on 16/6/27.
 */
public class NearRepairShopResult {
    private ArrayList<RepairShopResult> rshop;//修车店json数组

    public ArrayList<RepairShopResult> getRshop() {
        return rshop;
    }

    public void setRshop(ArrayList<RepairShopResult> rshop) {
        this.rshop = rshop;
    }
}
