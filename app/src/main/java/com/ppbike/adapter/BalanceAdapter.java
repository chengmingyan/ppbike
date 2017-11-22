package com.ppbike.adapter;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.view.PinnedHeaderListView.SectionedBaseXListAdapter;

/**
 * Created by chengmingyan on 16/7/6.
 */
public class BalanceAdapter extends SectionedBaseXListAdapter {

    @Override
    public Object getItem(int section, int position) {
        return null;
    }

    @Override
    public long getItemId(int section, int position) {
        return 0;
    }

    @Override
    public int getSectionCount() {
        return 3;
    }

    @Override
    public int getCountForSection(int section) {
        return 5;
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        ItemView view = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_balance,null);
            view = new ItemView();
            view.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            view.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            view.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            view.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
            view.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(view);
        }else{
            view = (ItemView) convertView.getTag();
        }
        return  convertView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        convertView = new TextView(parent.getContext());
        convertView.setPadding(20, 12, 5, 5);
        ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        ((TextView) convertView).setGravity(Gravity.LEFT | Gravity.TOP);
        ((TextView) convertView).setGravity(Gravity.CENTER_VERTICAL);
        convertView.setBackgroundColor(Color.WHITE);
        ((TextView) convertView).setText("今日");
        convertView.setFocusable(false);
        convertView.setFocusableInTouchMode(false);
        return convertView;
    }

    class ItemView{
        public TextView tv_date,tv_time,tv_type,tv_info,tv_price;
    }
}
