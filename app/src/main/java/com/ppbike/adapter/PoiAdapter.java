package com.ppbike.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppbike.bean.PoiAddressBean;
import com.ppbike.view.IconView;

import java.util.List;

/**
 * Created by chengmingyan on 16/7/14.
 */
public class PoiAdapter extends BaseAdapter {
    private List<PoiAddressBean> datas ;
    @Override
    public int getCount() {
        if (datas == null)
            return 0;
        return datas.size();
    }

    @Override
    public PoiAddressBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return null;
    }

    public void setDatas(List<PoiAddressBean> datas) {
        this.datas = datas;
    }
    class ItemView{
        public TextView tv_title,tv_distance;
        public IconView icon_select;
    }
}
