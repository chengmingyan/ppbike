package com.ppbike.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.adapter.RentOrderListAdapter;
import com.ppbike.bean.RentBikeOrderListRequest;
import com.ppbike.bean.RentBikeOrderListResult;
import com.ppbike.bean.UpdateOrderRequest;
import com.ppbike.listener.UpdateOrderListener;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.util.TimeUtil;
import com.ppbike.view.LoadView;
import com.ppbike.view.LoadingDialog;
import com.ppbike.view.YListView;

import java.io.IOException;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;


public class BikeRentOrderListFragment extends ViewPagerNetworkFragment implements YListView.RefreshTimeListener,YListView.IXListViewListener,  UpdateOrderListener {
	public static final String INTENT_TYPE = "type";
	private YListView listView ;
	private LoadView loadView;
	private RentOrderListAdapter adapter;
	private ResponseHandler refreshHandler, loadMoreHandler,updateOrderHandler;
	private final String TAG_REFRESH = "refresh",TAG_LOADMORE = "loadMore",TAG_UPDATE_ORDER = "update_order";
	private int type = 1,page = 2;
	private LoadingDialog loadingDialog ;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contextView = inflater.inflate(R.layout.include_ylist, container, false);
		return contextView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView(getView());
		initData();
	}
	private void initView(View view){
		listView = (YListView) view.findViewById(R.id.listView);
		adapter = new RentOrderListAdapter(this);
		listView.setAdapter(adapter);
		listView.setPullLoadEnable(true);
		listView.setRefreshTimeListener(new YListView.RefreshTimeListener() {
			@Override
			public void onRefreshTime(TextView view) {
				long time = getContext().getSharedPreferences("time", Context.MODE_PRIVATE).getLong(BikeRentOrderListFragment.this.getClass().getSimpleName()+getArguments().getInt(INTENT_TYPE,1),0l);
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

		loadingDialog = new LoadingDialog(getActivity());
		loadView.setStatus(LoadView.Status.loading);
	}
	private void initData(){
		type = getArguments().getInt(INTENT_TYPE,1);

		refreshHandler = new ResponseHandler(TAG_REFRESH);
		refreshHandler.setOnFailedListener(this);
		refreshHandler.setOnNeedLoginListener(this);
		refreshHandler.setOnSucceedListener(this);

		loadMoreHandler = new ResponseHandler(TAG_LOADMORE);
		loadMoreHandler.setOnFailedListener(this);
		loadMoreHandler.setOnNeedLoginListener(this);
		loadMoreHandler.setOnSucceedListener(this);

		updateOrderHandler = new ResponseHandler(TAG_UPDATE_ORDER);
		updateOrderHandler.setOnFailedListener(this);
		updateOrderHandler.setOnNeedLoginListener(this);
		updateOrderHandler.setOnSucceedListener(this);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onFailed(String tag, int resultCode, Object data) {
		if (loadingDialog.isShowing())
			loadingDialog.show();
		if (TAG_REFRESH.equals(tag)){
			if (loadView.getStatus() != LoadView.Status.successed){
				loadView.setStatus(LoadView.Status.network_error);
			}
			listView.stopRefresh();
		}else if (TAG_LOADMORE.equals(tag)){
			listView.stopLoadMoreNetwork();
		}
		ToastUtil.show(getActivity(),(String)data);
	}

	@Override
	public void onNeedLogin(String tag) {
		if (loadingDialog.isShowing())
			loadingDialog.show();
		if (TAG_REFRESH.equals(tag)){
			listView.stopRefresh();
		}else if (TAG_LOADMORE.equals(tag)){
			listView.stopLoadMore();
		}
		DialogUtil.showLoginDialogCanReturnHome( this,"检测到您未登录",true);
	}
	@Override
	public void onSucceed(String tag, boolean isCache, Object data) {
		RentBikeOrderListResult result = (RentBikeOrderListResult) data;
		if (TAG_REFRESH.equals(tag)){
			if (loadingDialog.isShowing())
				loadingDialog.show();
			if (data == null || result.getOrders() == null || result.getOrders().size() == 0){
				loadView.setStatus(LoadView.Status.not_data);
			}else{
				loadView.setStatus(LoadView.Status.successed);
				adapter.setDatas(result.getOrders());
				adapter.notifyDataSetChanged();
				listView.stopRefresh();
				page = 2;
			}
			getContext().getSharedPreferences("time", Context.MODE_PRIVATE).edit().putLong(BikeRentOrderListFragment.this.getClass().getSimpleName()+getArguments().getInt(INTENT_TYPE,1),System.currentTimeMillis()).commit();
		}else if (TAG_LOADMORE.equals(tag)){
			if (data == null || result.getOrders() == null || result.getOrders().size() == 0){
				listView.stopNoMore();
			}else {
				adapter.addDatas(result.getOrders());
				adapter.notifyDataSetChanged();
				listView.stopRefresh();
				page++;
			}
		}else if (TAG_UPDATE_ORDER.equals(tag)){
			onRefresh();
		}
	}

	@Override
	public void onRefresh() {
		RentBikeOrderListRequest request = new RentBikeOrderListRequest();
		request.setType(type);
		request.setPage(1);
		try {
			String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
			String body = JacksonJsonUtil.toJson(request);
			RequestModel.obtainBikeRentOrderList(refreshHandler,TAG_REFRESH,body,token);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLoadMore() {
		RentBikeOrderListRequest request = new RentBikeOrderListRequest();
		request.setType(type);
		request.setPage(page);
		try {
			String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
			String body = JacksonJsonUtil.toJson(request);
			RequestModel.obtainRentBikeOrderList(loadMoreHandler,TAG_LOADMORE,body,token);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRefreshTime(TextView view) {

	}

	@Override
	public void updateOrder(UpdateOrderRequest result) {
		try {
			loadingDialog.show();
			String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
			String body = JacksonJsonUtil.toJson(result);
			RequestModel.updateOrder(updateOrderHandler,TAG_UPDATE_ORDER,body,token);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause) {
		if (isVisibleToUser)
			onRefresh();
	}
}