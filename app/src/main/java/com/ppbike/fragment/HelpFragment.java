package com.ppbike.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ppbike.R;
import com.ppbike.activity.HelpDetailsActivity;
import com.ppbike.adapter.HelpAdapter;

/**
 * Created by chengmingyan on 16/6/27.
 */
public class HelpFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView listView;
    private HelpAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help,null);
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
        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new HelpAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), HelpDetailsActivity.class);
        startActivity(intent);
    }
}
