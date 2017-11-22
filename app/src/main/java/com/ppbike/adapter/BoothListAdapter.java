package com.ppbike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ppbike.R;
import com.ppbike.bean.BoothBike;

import java.util.ArrayList;

import cn.master.volley.commons.VolleyHelper;

/**
 * Created by chengmingyan on 16/6/18.
 */
public class BoothListAdapter extends BaseAdapter{
    private ArrayList<BoothBike> datas;

    @Override
    public int getCount() {
        if (datas == null)
            return 0;
        return datas.size();
    }

    @Override
    public BoothBike getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_boothlist,null);
            view = new ItemView();
            view.image = (NetworkImageView) convertView.findViewById(R.id.image);
            view.tv_bikeTitle = (TextView) convertView.findViewById(R.id.tv_bikeTitle);
            view.tv_bikeInfo = (TextView) convertView.findViewById(R.id.tv_bikeInfo);
            view.tv_deposit = (TextView) convertView.findViewById(R.id.tv_deposit);
            view.tv_pPrice = (TextView) convertView.findViewById(R.id.tv_pPrice);
            view.tv_status = (TextView) convertView.findViewById(R.id.tv_status);

            convertView.setTag(view);
        }else{
            view = (ItemView) convertView.getTag();
        }
        BoothBike result = getItem(position);
        view.image.setImageUrl(result.getPicUrl(), VolleyHelper.getImageLoader());
        view.tv_bikeTitle.setText(result.getName());
        view.tv_pPrice.setText("租金：￥" + result.getPprice()+"/天");
        view.tv_deposit.setText("押金：￥" + result.getDeposit()+"/辆");
        switch (result.getStatus()){
            case 1:
                view.tv_status.setText("审核中");
                break;
            case 2:
                view.tv_status.setText("审核不通过");
                break;
            case 3:
                view.tv_status.setText("审核通过");
                break;
            case 4:
                view.tv_status.setText("下架");
                break;
        }
        return convertView;
    }

    public void setDatas(ArrayList<BoothBike> datas) {
        this.datas = datas;
    }

    public void addDatas(ArrayList<BoothBike> bikes) {
        this.datas.addAll(bikes);
    }

    public ArrayList<BoothBike> getDatas() {
        return datas;
    }

    class ItemView{
        public TextView tv_bikeTitle,tv_bikeInfo,tv_deposit,tv_pPrice,tv_status;
        public NetworkImageView image;
    }
}
