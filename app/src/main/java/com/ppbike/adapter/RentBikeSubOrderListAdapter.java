package com.ppbike.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ppbike.R;
import com.ppbike.activity.RentOrderDetailsActivity;
import com.ppbike.bean.RentBikeOrderList;
import com.ppbike.bean.UpdateOrderRequest;
import com.ppbike.listener.UpdateOrderListener;
import com.ppbike.util.TimeUtil;

import java.util.ArrayList;

import cn.master.volley.commons.VolleyHelper;

/**
 * Created by chengmingyan on 16/6/18.
 */
public class RentBikeSubOrderListAdapter extends BaseAdapter{
    private ArrayList<RentBikeOrderList> datas;
    private UpdateOrderListener listener;
    public RentBikeSubOrderListAdapter (UpdateOrderListener listener){
        this.listener = listener;
    }
    @Override
    public int getCount() {
        if (datas == null)
            return 0;
        return datas.size();
    }

    @Override
    public RentBikeOrderList getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rentbike_sub_orderlist,null);
            view = new ItemView();
            view.image = (NetworkImageView) convertView.findViewById(R.id.image);
            view.tv_bikeTitle = (TextView) convertView.findViewById(R.id.tv_bikeTitle);
            view.tv_bikeInfo = (TextView) convertView.findViewById(R.id.tv_bikeInfo);
            view.tv_oPrice = (TextView) convertView.findViewById(R.id.tv_deposit);
            view.tv_pPrice = (TextView) convertView.findViewById(R.id.tv_pPrice);

            view.tv_startDate = (TextView) convertView.findViewById(R.id.tv_startDate);
            view.tv_startTime = (TextView) convertView.findViewById(R.id.tv_startTime);
            view.tv_dateNumber = (TextView) convertView.findViewById(R.id.tv_dateNumber);
            view.tv_endDate = (TextView) convertView.findViewById(R.id.tv_endDate);
            view.tv_endTime = (TextView) convertView.findViewById(R.id.tv_endTime);

            view.tv_totalPrice = (TextView) convertView.findViewById(R.id.tv_totalPrice);
            view.btn_sure = (Button) convertView.findViewById(R.id.btn_sure);
            view.btn_link = (Button) convertView.findViewById(R.id.btn_link);
            convertView.setTag(view);
        }else{
            view = (ItemView) convertView.getTag();
        }

        final  RentBikeOrderList bean = getItem(position);
        view.tv_bikeTitle.setText(bean.getName());
        view.image.setImageUrl(bean.getPicUrl(), VolleyHelper.getImageLoader());
        view.tv_dateNumber.setText(String.valueOf(bean.getDates()));
        view.tv_startDate.setText(TimeUtil.systemTime2LocalTime(bean.getStime(),TimeUtil.LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING));
        view.tv_startTime.setText(TimeUtil.systemTime2LocalTime(bean.getStime(),"aHH:00"));
        view.tv_endDate.setText(TimeUtil.systemTime2LocalTime(bean.getEtime(),TimeUtil.LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING));
        view.tv_endTime.setText(TimeUtil.systemTime2LocalTime(bean.getEtime(),"aHH:00"));
        view.btn_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateOrderRequest request = new UpdateOrderRequest();
                request.setOrderId(bean.getOrderId());
                request.setType(1);
                listener.updateOrder(request);
            }
        });
        view.btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateOrderRequest request = new UpdateOrderRequest();
                request.setOrderId(bean.getOrderId());
                request.setType(1);
                listener.updateOrder(request);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), RentOrderDetailsActivity.class);
                intent.putExtra(RentOrderDetailsActivity.INTENT_ORDERID,bean.getOrderId());
                parent.getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    public void setDatas(ArrayList<RentBikeOrderList> datas) {
        this.datas = datas;
    }

    public void addDatas(ArrayList<RentBikeOrderList> orders) {
        this.datas.addAll(orders);
    }

    class ItemView{
        public TextView tv_bikeTitle,tv_bikeInfo,tv_oPrice,tv_pPrice;
        public NetworkImageView image;
        public TextView tv_startDate,tv_startTime,tv_dateNumber,tv_endDate,tv_endTime;
        public TextView tv_totalPrice;
        public Button btn_sure,btn_link;
    }
}
