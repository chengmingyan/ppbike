package com.ppbike.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jauker.widget.BadgeView;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ppbike.R;
import com.ppbike.activity.MessageActivity;
import com.ppbike.activity.UploadBikeActivity;
import com.ppbike.activity.RentBikeActivity;
import com.ppbike.view.IconView;

/**
 * Created by chengmingyan on 16/6/16.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    private BadgeView badgeView;
    private IconView icon_message;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
    }
    private void initView(View view){
        view.findViewById(R.id.icon_menu).setOnClickListener(this);
        view.findViewById(R.id.btn_rent).setOnClickListener(this);
        view.findViewById(R.id.btn_rental).setOnClickListener(this);
        badgeView = new BadgeView(getActivity());
        badgeView.setHeight(15);
        badgeView.setWidth(15);
        badgeView.setBadgeMargin(0, 2, 0, 0);
        badgeView.setBackgroundResource(R.drawable.hot_point);
        badgeView.setTextColor(getActivity().getResources().getColor(android.R.color.transparent));

        icon_message = (IconView) view.findViewById(R.id.icon_message);
        icon_message.setOnClickListener(this);
        badgeView.setBadgeCount(1);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_menu:
                ((SlidingFragmentActivity)getActivity()).getSlidingMenu().toggle();
                break;
            case R.id.btn_rent:
            {
                Intent intent = new Intent(getActivity(),UploadBikeActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_rental:
            {
                Intent intent = new Intent(getActivity(),RentBikeActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.icon_message:
            {
                Intent intent = new Intent(getActivity(),MessageActivity.class);
                startActivity(intent);
            }
                break;
        }
    }
}
