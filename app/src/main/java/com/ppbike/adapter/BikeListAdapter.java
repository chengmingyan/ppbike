package com.ppbike.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ppbike.R;
import com.ppbike.bean.BikeResult;

import java.util.ArrayList;

import cn.master.volley.commons.VolleyHelper;

/**
 * Created by chengmingyan on 16/6/18.
 */
public class BikeListAdapter extends BaseAdapter{
    private ArrayList<BikeResult> datas;

    @Override
    public int getCount() {
        if (datas == null){
            return  0;
        }
        return datas.size();
    }

    @Override
    public BikeResult getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ItemView view = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bikelist,null);
            view = new ItemView();
            view.image = (NetworkImageView) convertView.findViewById(R.id.image);
            view.image.setDefaultImageResId(R.drawable.image_default);
            view.image.setErrorImageResId(R.drawable.image_default);
            view.tv_bikeTitle = (TextView) convertView.findViewById(R.id.tv_bikeTitle);
            view.tv_bikeInfo = (TextView) convertView.findViewById(R.id.tv_bikeInfo);
            view.tv_deposit = (TextView) convertView.findViewById(R.id.tv_deposit);
            view.tv_pPrice = (TextView) convertView.findViewById(R.id.tv_pPrice);
            view.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
            view.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(view);
        }else{
            view = (ItemView) convertView.getTag();
        }
        final BikeResult result = getItem(position);
        view.image.setImageUrl(result.getPicUrl(), VolleyHelper.getImageLoader());
        view.tv_bikeTitle.setText(result.getName());

        String info = "";
        if (!TextUtils.isEmpty(result.getColor()))
            info += result.getColor();
        if (!TextUtils.isEmpty(result.getSpeed()))
            info += "、" + result.getSpeed();
        if (!TextUtils.isEmpty(result.getBrand()))
            info += "、" + result.getBrand();
        if (!TextUtils.isEmpty(result.getSize()))
            info += "、" + result.getSize();

        view.tv_bikeInfo.setText(info);
        view.tv_location.setText(result.getDistance());
        view.tv_count.setText("库存："+result.getCount());
        view.tv_deposit.setText("押金：￥" + result.getDeposit()/100 + "/辆");
        view.tv_pPrice.setText("租金：￥" + result.getPrice()/100 + "/天");
        return convertView;
    }

    public void setDatas(ArrayList<BikeResult> datas) {
        this.datas = datas;
    }

    public void addDatas(ArrayList<BikeResult> list) {
        datas.addAll(list);
    }

    class ItemView{
        public NetworkImageView image;
        public TextView tv_bikeTitle,tv_bikeInfo,tv_location,tv_deposit,tv_pPrice,tv_count;
    }
}
