package com.ppbike.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.ppbike.R;

/**
 * Created by chengmingyan on 16/6/22.
 */
public class EvaluationActivity extends ParentActivity {
    private FormEditText edit_evaluation;
    private RatingBar ratingBar_bike,ratingBar_custom;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        initView();
    }

    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("订单详情");
        ratingBar_bike = (RatingBar) findViewById(R.id.ratingBar_bike);
        ratingBar_custom = (RatingBar) findViewById(R.id.ratingBar_custom);
        edit_evaluation = (FormEditText) findViewById(R.id.edit_evaluation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.btn_sure:
                finish();
                break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {

    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {

    }

    @Override
    public void onNeedLogin(String tag) {

    }
}
