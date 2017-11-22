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

import cn.master.volley.commons.AppUtils;

/**
 * Created by chengmingyan on 16/6/27.
 */
public class AboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
    }


    private void initView(View view) {
        view.findViewById(R.id.icon_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SlidingFragmentActivity)getActivity()).getSlidingMenu().toggle();
            }
        });
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("关于我们");
        TextView tv_versionName = (TextView) view.findViewById(R.id.tv_versionName);
        tv_versionName.setText("版本：v" + AppUtils.getVersionName(getActivity()));
    }
}
