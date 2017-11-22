package com.ppbike.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.adapter.MainPagerAdapter;
import com.ppbike.fragment.MessageListFragment;
import com.ppbike.view.StillViewPager;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by chengmingyan on 16/6/18.
 */
public class MessageActivity extends ParentActivity {

    private static final String[] TITLE = new String[] { "消息", "活动"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
    }

    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("通知");

        MainPagerAdapter adapter = new MainPagerAdapter(this.getSupportFragmentManager(),TITLE);
        Bundle bundle = new Bundle();
        bundle.putInt(MessageListFragment.INTENT_TYPE,1);
        Fragment fragment = new MessageListFragment();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment);
        bundle = new Bundle();
        bundle.putInt(MessageListFragment.INTENT_TYPE,2);
        fragment = new MessageListFragment();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment);
        StillViewPager pager = (StillViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setScanScroll(false);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);

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

    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {

    }

    @Override
    public void onNeedLogin(String tag) {

    }
}

