package com.ppbike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.bean.MessageResult;
import com.ppbike.util.TimeUtil;

import java.util.ArrayList;

/**
 * Created by chengmingyan on 16/6/18.
 */
public class MessageListAdapter extends BaseAdapter{
    private ArrayList<MessageResult> datas;

    @Override
    public int getCount() {
        if (datas == null){
            return  0;
        }
        return datas.size();
    }

    @Override
    public MessageResult getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message,null);
            view = new ItemView();
            view.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            view.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            view.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            view.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(view);
        }else{
            view = (ItemView) convertView.getTag();
        }
        MessageResult message = getItem(position);
        view.tv_type.setText(MessageResult.TYPE_VALUES[message.getType() - 1]);
        view.tv_title.setText(message.getTitle());
        view.tv_content.setText(message.getContent());
        view.tv_time.setText(TimeUtil.utc2Local(message.getCreateTime(),TimeUtil.LOCAL_DETAIL_TIME_PATTERN_STRING));
        return convertView;
    }

    public void setDatas(ArrayList<MessageResult> datas) {
        this.datas = datas;
    }

    class ItemView{
        public TextView tv_type,tv_title,tv_content,tv_time;
    }
}
