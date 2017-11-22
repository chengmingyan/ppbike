package com.ppbike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ppbike.R;
import com.ppbike.bean.RepairShopResult;

import java.util.ArrayList;

import cn.master.volley.commons.VolleyHelper;

/**
 * Created by chengmingyan on 16/7/1.
 */
public class RepairListAdapter extends BaseAdapter {
    private ArrayList<RepairShopResult> datas;

    @Override
    public int getCount() {
        if (datas == null)
            return 0;
        return datas.size();
    }

    @Override
    public RepairShopResult getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_repair_list,null);
            view = new ItemView();
            view.image = (NetworkImageView) convertView.findViewById(R.id.image);
            view.image.setDefaultImageResId(R.drawable.image_default);
            view.image.setErrorImageResId(R.drawable.image_default);
            view.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            view.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            view.tv_addName = (TextView) convertView.findViewById(R.id.tv_addName);
            view.icon_right = convertView.findViewById(R.id.icon_right);
            convertView.setTag(view);
        }else{
            view = (ItemView) convertView.getTag();
        }
        RepairShopResult result = getItem(position);
        view.image.setImageUrl(result.getPicUrl(), VolleyHelper.getImageLoader());
        view.tv_address.setText(result.getAddress());
        view.tv_distance.setText(result.getDistance());
        view.tv_addName.setText(result.getAddNick());
        return convertView;
    }

    public void setDatas(ArrayList<RepairShopResult> datas) {
        this.datas = datas;
    }

    class ItemView{
        public NetworkImageView image;
        public TextView tv_address,tv_distance,tv_addName;
        public View icon_right;
    }
}
