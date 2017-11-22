package com.ppbike.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.bean.CommentResult;
import com.ppbike.util.TimeUtil;

import java.util.List;

/**
 * Created by chengmingyan on 16/7/15.
 */
public class CommentAdapter extends BaseAdapter {

    private List<CommentResult> datas;
    public  CommentAdapter (List<CommentResult> datas){
        this.datas = datas;
    }
    @Override
    public int getCount() {
        if (datas == null)
            return 0;
        return datas.size();
    }

    @Override
    public CommentResult getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comment,null);
            view = new ItemView();
            view.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            view.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            view.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            convertView.setTag(view);
        }else{
            view = (ItemView) convertView.getTag();
        }
        CommentResult result = getItem(position);
        view.ratingBar.setRating(result.getStart());
        view.tv_content.setText(result.getComment());
        view.tv_name.setText(result.getNick()+ " "+ TimeUtil.utc2Local(result.getCreatetime(),TimeUtil.LOCAL_DETAIL_TIME_PATTERN_STRING));
        return convertView;
    }
    class ItemView{
        public TextView tv_name,tv_time,tv_content;
        public RatingBar ratingBar;
    }
}
