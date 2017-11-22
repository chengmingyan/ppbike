package com.ppbike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
 * Created by chengmingyan on 16/6/16.
 */
public class BikeRentOrderFragment extends ViewPagerFragment implements View.OnClickListener{

    private static final String[] TITLE = new String[] { "全部", "待发车", "待收车"};

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
        tv_title.setText("我的出租订单");

        StatePagerAdapter adapter = new StatePagerAdapter(this.getChildFragmentManager(),TITLE);
        Bundle bundle = new Bundle();
        bundle.putInt(BikeRentOrderListFragment.INTENT_TYPE,1);
        Fragment fragment = new BikeRentOrderListFragment();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment);
        bundle = new Bundle();
        bundle.putInt(BikeRentOrderListFragment.INTENT_TYPE,2);
        fragment = new BikeRentOrderListFragment();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment);
        bundle = new Bundle();
        bundle.putInt(BikeRentOrderListFragment.INTENT_TYPE,3);
        fragment = new BikeRentOrderListFragment();
        fragment.setArguments(bundle);
        adapter.addFragment(fragment);
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
