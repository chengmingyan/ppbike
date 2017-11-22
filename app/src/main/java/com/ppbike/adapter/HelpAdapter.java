package com.ppbike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppbike.R;

/**
 * Created by chengmingyan on 16/6/27.
 */
public class HelpAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView view = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_help,null);
            view = new ItemView();
            view.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            view.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(view);
        }else{
            view = (ItemView) convertView.getTag();
        }
        return convertView;
    }

    class ItemView{
        public TextView tv_title,tv_content;
    }
}
