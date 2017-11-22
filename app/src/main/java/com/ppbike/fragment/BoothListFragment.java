package com.ppbike.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ppbike.R;
import com.ppbike.activity.BoothDetailsActivity;
import com.ppbike.activity.UploadBikeActivity;
import com.ppbike.adapter.BoothListAdapter;
import com.ppbike.bean.BoothBike;
import com.ppbike.bean.BoothListRequest;
import com.ppbike.bean.BoothListResult;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.util.TimeUtil;
import com.ppbike.view.LoadView;
import com.ppbike.view.YListView;

import java.io.IOException;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;
import cn.master.volley.models.response.listener.OnNeedLoginListener;

/**
 * Created by chengmingyan on 16/6/23.
 */
public class BoothListFragment extends ViewPagerNetworkFragment implements AdapterView.OnItemClickListener,OnNeedLoginListener
    ,YListView.IXListViewListener,YListView.RefreshTimeListener {
    private BoothListAdapter adapter ;
    private YListView listView;
    private LoadView loadView;
    private ResponseHandler refreshHandler,loadMorehandler;
    private final String TAG_REFRESH = "refresh",TAG_LOADMORE = "loadMore";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_booth_list,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        initData();
        loadView.setStatus(LoadView.Status.loading);
    }

    private void initView(View view){
        view.findViewById(R.id.icon_menu).setOnClickListener(this);
        view.findViewById(R.id.icon_add).setOnClickListener(this);

        listView = (YListView) view.findViewById(R.id.listView);
        adapter = new BoothListAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setXListViewListener(this);
        listView.setRefreshTimeListener(this);
        listView.setPullLoadEnable(true);
        listView.setRefreshTimeListener(new YListView.RefreshTimeListener() {
            @Override
            public void onRefreshTime(TextView view) {
                long time = getContext().getSharedPreferences("time", Context.MODE_PRIVATE).getLong(BoothListFragment.this.getClass().getSimpleName() ,0l);
                view.setText(TimeUtil.obtainLastUpdateTimeStatuString(time));
            }
        });

        loadView = (LoadView) view.findViewById(R.id.loadView);
        loadView.setOnReLoadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadView.setStatus(LoadView.Status.loading);
                onRefresh();
            }
        });
        loadView.setOnStatusChangedListener(new LoadView.OnStatusChangedListener() {
            @Override
            public void OnStatusChanged(LoadView.Status status) {
                switch (status){
                    case successed:
                        listView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        listView.setVisibility(View.GONE);
                        break;

                }
            }
        });
    }
    private void initData(){
        refreshHandler = new ResponseHandler(TAG_REFRESH);
        refreshHandler.setOnFailedListener(this);
        refreshHandler.setOnNeedLoginListener(this);
        refreshHandler.setOnSucceedListener(this);

        loadMorehandler = new ResponseHandler(TAG_LOADMORE);
        loadMorehandler.setOnFailedListener(this);
        loadMorehandler.setOnNeedLoginListener(this);
        loadMorehandler.setOnSucceedListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_menu:
                ((SlidingFragmentActivity)getActivity()).getSlidingMenu().toggle();
                break;
            case R.id.icon_add:
            {
                Intent intent = new Intent(getActivity(),UploadBikeActivity.class);
                intent.putExtra(UploadBikeActivity.INTENT_TITLE,"添加单车");
                startActivity(intent);
            }
                break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (TAG_REFRESH.equals(tag)){

            if (loadView.getStatus() != LoadView.Status.successed){
                loadView.setStatus(LoadView.Status.network_error);
            }else{
                listView.stopRefresh();
            }
        }
        else if (TAG_LOADMORE.equals(tag))
            listView.stopLoadMoreNetwork();
        ToastUtil.show(getActivity(),(String)data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        BoothListResult result = (BoothListResult) data;
        if (TAG_REFRESH.equals(tag)){
            listView.stopRefresh();
            if (result.getBikes() == null || result.getBikes().size() == 0){
                loadView.setStatus(LoadView.Status.not_data);
            }else{
                loadView.setStatus(LoadView.Status.successed);
                adapter.setDatas(result.getBikes());
                adapter.notifyDataSetChanged();
            }
            getContext().getSharedPreferences("time", Context.MODE_PRIVATE).edit().putLong(BoothListFragment.this.getClass().getSimpleName() ,System.currentTimeMillis()).commit();

        }else{
            if (result.getBikes().size() == 0){
                listView.stopNoMore();
            }else{
                adapter.addDatas(result.getBikes());
                adapter.notifyDataSetChanged();
                listView.stopLoadMore();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getAdapter().getItem(position);
        if (object instanceof BoothBike){
            Intent intent = new Intent(getActivity(),BoothDetailsActivity.class);
            intent.putExtra(BoothDetailsActivity.INTENT_BIKEID,((BoothBike) object).getBikeId());
            startActivity(intent);
        }
    }

    @Override
    public void onNeedLogin(String tag) {
        if (TAG_REFRESH.equals(tag))
            listView.stopRefresh();
        if (TAG_LOADMORE.equals(tag))
             listView.stopLoadMore();
        DialogUtil.showLoginDialogCanReturnHome(this,"请您先登录",true);
    }

    @Override
    public void onRefresh(){
        BoothListRequest request = new BoothListRequest();
        request.setBikeId(0);
        try {
            String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
            String body = JacksonJsonUtil.toJson(request);
            RequestModel.obtainBoothList(refreshHandler,TAG_REFRESH,body,token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore() {
        BoothListRequest request = new BoothListRequest();
        request.setBikeId(adapter.getDatas().get(adapter.getCount()-1).getBikeId());
        try {
            String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
            String body = JacksonJsonUtil.toJson(request);
            RequestModel.obtainBoothList(loadMorehandler,TAG_LOADMORE,body,token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefreshTime(TextView view) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            onRefresh();
        }else{
            DialogUtil.showLoginDialogCanReturnHome(this,"请您先登录",true);
        }
    }

    @Override
    public void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause) {
        if (isVisibleToUser){
            String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
            if(TextUtils.isEmpty(token)){
                DialogUtil.showLoginDialogCanReturnHome(this,"检测到您还未登录?",true);
            }else{
                onRefresh();
            }
        }
    }
}
