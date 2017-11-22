package com.ppbike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.bean.PoiAddressBean;
import com.ppbike.view.IconView;

import java.util.ArrayList;

/**
 * Created by chengmingyan on 16/7/13.
 */
public class PoiAddressAdapter extends BaseAdapter {
    private ArrayList<PoiAddressBean> datas;
    private boolean isSearch;

    public ArrayList<PoiAddressBean> getDatas() {
        return datas;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    private int selectedPosition;
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
        ItemView view = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_poi_address,null);
            view = new ItemView();
            view.icon_location = (IconView) convertView.findViewById(R.id.icon_location);
            view.icon_search = (IconView) convertView.findViewById(R.id.icon_search);
            view.icon_select = (IconView) convertView.findViewById(R.id.icon_select);
            view.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            view.tv_area = (TextView) convertView.findViewById(R.id.tv_area);
            view.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            convertView.setTag(view);
        }else{
            view = (ItemView) convertView.getTag();
        }
        PoiAddressBean bean = getItem(position);
        if (isSearch){
            view.icon_location.setVisibility(View.GONE);
            view.icon_search.setVisibility(View.VISIBLE);
        }else{
            view.icon_location.setVisibility(View.VISIBLE);
            view.icon_search.setVisibility(View.GONE);
        }
        view.tv_address.setText(bean.getAddress());
        view.tv_area.setText(bean.getDistrict());
        if (selectedPosition == position){
            view.icon_select.setVisibility(View.VISIBLE);
            view.tv_distance.setVisibility(View.GONE);
        }else{
            view.icon_select.setVisibility(View.GONE);
            view.tv_distance.setVisibility(View.VISIBLE);
            view.tv_distance.setText(bean.getDistance()+"米");
        }
        return convertView;
    }

    public void setDatas(ArrayList<PoiAddressBean> datas,boolean isSearch) {
        this.datas = datas;
        this.isSearch = isSearch;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    class ItemView{
        public IconView icon_location,icon_search,icon_select;
        public TextView tv_address,tv_area,tv_distance;
    }
}
