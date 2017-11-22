package com.ppbike.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ppbike.R;
import com.ppbike.activity.AddRepairActivity;
import com.ppbike.activity.RepairDetailsActivity;
import com.ppbike.adapter.RepairListAdapter;
import com.ppbike.bean.NearRepairShopRequest;
import com.ppbike.bean.NearRepairShopResult;
import com.ppbike.bean.RepairShopResult;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.util.GPSUtil;
import com.ppbike.util.TimeUtil;
import com.ppbike.view.LoadView;
import com.ppbike.view.SwipeMenuList.SwipeMenu;
import com.ppbike.view.SwipeMenuList.SwipeMenuCreator;
import com.ppbike.view.SwipeMenuList.SwipeMenuItem;
import com.ppbike.view.SwipeMenuList.SwipeMenuListView;
import com.ppbike.view.SwipeMenuList.SwipeMenuView;
import com.ppbike.view.YListView;

import java.io.IOException;
import java.util.ArrayList;

import cn.master.util.utils.RequestCodeUtil;
import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;
import cn.master.volley.models.response.listener.OnNeedLoginListener;

/**
 * Created by chengmingyan on 16/7/1.
 */
public class RepairFragment extends ViewPagerNetworkFragment implements SwipeMenuListView.OnMenuItemClickListener,
        AdapterView.OnItemClickListener, YListView.IXListViewListener, YListView.RefreshTimeListener,
        SwipeMenuCreator ,OnNeedLoginListener{

    private SwipeMenuListView listView;
    private LoadView loadView;
    private RepairListAdapter adapter;
    private ResponseHandler refreshHandler;
    private NearRepairShopRequest request = new NearRepairShopRequest();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_repair_list,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        initData();
        loadView.setStatus(LoadView.Status.loading);
    }

    private  void initView(View view){
        view.findViewById(R.id.icon_menu).setOnClickListener(this);
        view.findViewById(R.id.icon_add).setOnClickListener(this);
        listView = (SwipeMenuListView) view.findViewById(R.id.listView);
        // step 2. listener item click event
        listView.setOnMenuItemClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setXListViewListener(this);
        listView.setRefreshTimeListener(this);
        listView.setPullLoadEnable(false);

        listView.setMenuCreator(this);
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
//                View view = listView.getChildAt(position+1 - listView.getFirstVisiblePosition());
//                if (view != null){
//                    view.findViewById(R.id.icon_right).setVisibility(View.INVISIBLE);
//                }
            }

            @Override
            public void onSwipeEnd(int position) {

            }

        });
        listView.setRefreshTimeListener(new YListView.RefreshTimeListener() {
            @Override
            public void onRefreshTime(TextView view) {
                long time = getContext().getSharedPreferences("time", Context.MODE_PRIVATE).getLong(RepairFragment.this.getClass().getSimpleName() ,0l);
                view.setText(TimeUtil.obtainLastUpdateTimeStatuString(time));
            }
        });

        adapter = new RepairListAdapter();
        listView.setAdapter(adapter);

        loadView = (LoadView) view.findViewById(R.id.loadView);
        loadView.setOnStatusChangedListener(new LoadView.OnStatusChangedListener() {
            @Override
            public void OnStatusChanged(LoadView.Status status) {
                switch (status) {
                    case successed:
                        listView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        listView.setVisibility(View.GONE);
                        break;
                }
            }
        });
        loadView.setOnReLoadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location();
            }
        });
    }

    private void initData(){
        refreshHandler = new ResponseHandler();
        refreshHandler.setOnSucceedListener(this);
        refreshHandler.setOnFailedListener(this);
        loadView.setStatus(LoadView.Status.loading);

    }
    private void location(){
        AMapLocation location = GPSUtil.getInstance(getActivity()).getaMapLocation();
        if (location != null){
            request.setLa(location.getLatitude());
            request.setLo(location.getLongitude());
            onRefresh();
        }else{
            GPSUtil.getInstance(getActivity()).startLocation(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    GPSUtil.getInstance(getActivity()).stopLocation();
                    if (aMapLocation == null || aMapLocation.getErrorCode() != AMapLocation.LOCATION_SUCCESS){
                        loadView.setStatus(LoadView.Status.network_error);
                    }else{
                        request.setLa(aMapLocation.getLatitude());
                        request.setLo(aMapLocation.getLongitude());
                        onRefresh();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_menu:
                ((SlidingFragmentActivity)getActivity()).getSlidingMenu().toggle();
                break;
            case R.id.icon_add:
            {
                Intent intent = new Intent(getActivity(),AddRepairActivity.class);
                startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(AddRepairActivity.class));
            }
                break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadView.getStatus() != LoadView.Status.successed)
            loadView.setStatus(LoadView.Status.network_error);
        ToastUtil.show(getActivity(),(String)data);
        listView.stopRefresh();
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (data == null){
            loadView.setStatus(LoadView.Status.not_data);
        }else{
            listView.stopRefresh();
            loadView.setStatus(LoadView.Status.successed);
            NearRepairShopResult result = (NearRepairShopResult) data;
            ArrayList<RepairShopResult> datas = result.getRshop();
            adapter.setDatas(datas);
            adapter.notifyDataSetChanged();
        }
        getContext().getSharedPreferences("time", Context.MODE_PRIVATE).edit().putLong(RepairFragment.this.getClass().getSimpleName() ,System.currentTimeMillis()).commit();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getAdapter().getItem(position);
        if (object instanceof RepairShopResult){
            RepairShopResult result = (RepairShopResult) object;
            Intent intent = new Intent(getActivity(),RepairDetailsActivity.class);
            intent.putExtra(RepairDetailsActivity.INTENT_SHOP,result);
            startActivityForResult(intent,RequestCodeUtil.getInstance().obtainRequestCode(RepairDetailsActivity.class));
        }
    }

    @Override
    public void onRefresh() {

         try {
            String body = JacksonJsonUtil.toJson(request);
            String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
            RequestModel.obtainRepairList(refreshHandler,body,token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefreshTime(TextView view) {

    }

    @Override
    public boolean onMenuItemClick(SwipeMenuView view, int position, SwipeMenu menu, int index) {
        switch (index) {
            case 0:

                break;
        }
        return false;
    }

    private void createRightMenu(SwipeMenu menu) {
        SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
        deleteItem.setBackground(R.color.button_background);
        deleteItem.setWidth(dp2px(90));
        deleteItem.setTitle("编辑");
        deleteItem.setTitleColor(R.color.text_menu);
        menu.addMenuItem(deleteItem);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    @Override
    public void create(SwipeMenu menu) {
        // Create different menus depending on the view type
        switch (menu.getSwipeMenuDirection()) {
            case left:
                break;
            case right:
                 createRightMenu(menu);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeUtil.getInstance().obtainRequestCode(AddRepairActivity.class)){
            if (Activity.RESULT_OK == resultCode){
                listView.firstRefresh();
            }

        }
    }

    @Override
    public void onNeedLogin(String tag) {
        listView.stopRefresh();
        DialogUtil.showLoginDialogCanReturnHome(getActivity(),"检测到您还未登录?",true);
    }

    @Override
    public void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause) {
        if (isVisibleToUser){
            location();
        }
    }
}
