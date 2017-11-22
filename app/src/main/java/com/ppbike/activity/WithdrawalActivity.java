package com.ppbike.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.adapter.BalanceAdapter;
import com.ppbike.util.DialogUtil;
import com.ppbike.view.LoadView;
import com.ppbike.view.PinnedHeaderListView.PinnedHeaderYListView;
import com.ppbike.view.YListView;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.models.response.listener.OnNeedLoginListener;

/**
 * Created by chengmingyan on 16/7/6.
 */
public class WithdrawalActivity extends ParentActivity implements AdapterView.OnItemClickListener,
        YListView.IXListViewListener,YListView.RefreshTimeListener,OnNeedLoginListener{
    private PinnedHeaderYListView listView;
    private LoadView loadView;
    private BalanceAdapter adapter ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        initView();
    }

    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("提现记录");
        listView = (PinnedHeaderYListView) findViewById(R.id.listView);
        listView.setPullLoadEnable(false);
        listView.setOnItemClickListener(this);
        listView.setXListViewListener(this);
        listView.setRefreshTimeListener(this);

        adapter = new BalanceAdapter();
        listView.setAdapter(adapter);

        loadView = (LoadView) findViewById(R.id.loadView);
        loadView.setOnReLoadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadView.setStatus(LoadView.Status.loading);
                onRefresh();
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadView.getStatus() != LoadView.Status.successed){
            loadView.setStatus(LoadView.Status.network_error);
        }
        ToastUtil.show(this,(String)data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onRefreshTime(TextView view) {

    }

    @Override
    public void onNeedLogin(String tag) {
        DialogUtil.resetLoginDialog(this,true);
    }
}
