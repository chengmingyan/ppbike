package com.ppbike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ppbike.R;
import com.ppbike.adapter.StatePagerAdapter;
import com.ppbike.view.StillViewPager;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by chengmingyan on 16/6/19.
 */
public class RentBikeOrderListFragment extends ViewPagerFragment implements View.OnClickListener{

    private static final String[] TITLE = new String[] { "进行中", "已完成", "已取消"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_rent_orderlist,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
    }

    private void initView(View view){
        view.findViewById(R.id.icon_menu).setOnClickListener(this);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("我的租车订单");
        StatePagerAdapter adapter = new StatePagerAdapter(this.getChildFragmentManager(),TITLE);
        adapter.addFragment(new RentBikeSubOrderListFragment());
        adapter.addFragment(new RentBikeSubOrderListFragment());
        adapter.addFragment(new RentBikeSubOrderListFragment());
        StillViewPager pager = (StillViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setScanScroll(false);

        TabPageIndicator indicator = (TabPageIndicator)view.findViewById(R.id.indicator);
        indicator.setViewPager(pager);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_menu:
                ((SlidingFragmentActivity)getActivity()).getSlidingMenu().toggle();
                break;
        }
    }

    @Override
    public void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause) {

    }
}

