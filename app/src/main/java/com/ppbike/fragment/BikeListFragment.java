package com.ppbike.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.ppbike.R;
import com.ppbike.activity.BikeDetailsActivity;
import com.ppbike.activity.BikeListActivity;
import com.ppbike.adapter.BikeListAdapter;
import com.ppbike.bean.BikeListResult;
import com.ppbike.bean.BikeResult;
import com.ppbike.bean.NearBikeListRequest;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.util.GPSUtil;
import com.ppbike.util.TimeUtil;
import com.ppbike.view.LoadView;
import com.ppbike.view.YListView;

import java.io.IOException;
import java.util.ArrayList;

import cn.master.util.utils.RequestCodeUtil;
import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;


public class BikeListFragment extends ViewPagerNetworkFragment implements AdapterView.OnItemClickListener,
		YListView.IXListViewListener {
	public static final String INTENT_TYPE = "type";
	private YListView listView ;
	private LoadView loadView;
	private BikeListAdapter adapter;
	private ResponseHandler refreshHandler,loadMoreHandler;
	private final String TAG_REFRESH = "refresh",TAG_LOADMORE = "loadMore";
	private int page = 1;
	private NearBikeListRequest request;

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
		listView.setPullLoadEnable(true);
		listView.setXListViewListener(this);
		adapter = new BikeListAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setRefreshTimeListener(new YListView.RefreshTimeListener() {
			@Override
			public void onRefreshTime(TextView view) {
				long time = getContext().getSharedPreferences("time", Context.MODE_PRIVATE).getLong(BikeListFragment.this.getClass().getSimpleName()+getArguments().getInt(INTENT_TYPE,1),0l);
				view.setText(TimeUtil.obtainLastUpdateTimeStatuString(time));
			}
		});

		loadView = (LoadView) view.findViewById(R.id.loadView);
		loadView.setOnReLoadClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadView.setStatus(LoadView.Status.loading);

			}
		});
	}
	private void initData(){
		request = (NearBikeListRequest) getActivity().getIntent().getSerializableExtra(BikeListActivity.INTENT_REQUEST);
		request.setType(getArguments().getInt(INTENT_TYPE,1));
		refreshHandler = new ResponseHandler(TAG_REFRESH);
		refreshHandler.setOnSucceedListener(this);
		refreshHandler.setOnFailedListener(this);
		refreshHandler.setOnNeedLoginListener(this);
		loadMoreHandler = new ResponseHandler(TAG_LOADMORE);
		loadMoreHandler.setOnSucceedListener(this);
		loadMoreHandler.setOnFailedListener(this);
		loadMoreHandler.setOnNeedLoginListener(this);

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
	public void onRefresh(){

		request.setPage(1);
		try {
			String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
			String body = JacksonJsonUtil.toJson(request);
			RequestModel.obtainNearBikeList(refreshHandler,TAG_REFRESH,body,token);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLoadMore() {
		request.setPage(page);
		try {
			String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
			String body = JacksonJsonUtil.toJson(request);
			RequestModel.obtainNearBikeList(loadMoreHandler,TAG_REFRESH,body,token);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object object = parent.getAdapter().getItem(position);
		if (object instanceof BikeResult){

			Intent intent = new Intent(getActivity(),BikeDetailsActivity.class);
			intent.putExtra(BikeDetailsActivity.INTENT_REQUEST,request);
			intent.putExtra(BikeDetailsActivity.INTENT_BIKEID,((BikeResult) object).getBikeId());
			startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(BikeDetailsActivity.class));
		}
	}

	@Override
	public void onFailed(String tag, int resultCode, Object data) {
		if (TAG_REFRESH.equals(tag)){
			listView.stopRefresh();
			if (loadView.getStatus() != LoadView.Status.successed){
				loadView.setStatus(LoadView.Status.network_error);
			}
		}else if (TAG_LOADMORE.equals(tag)){
			listView.stopLoadMoreNetwork();
		}
		ToastUtil.show(getActivity(),(String)data);
	}

	@Override
	public void onSucceed(String tag, boolean isCache, Object data) {
		if (TAG_REFRESH.equals(tag)){
			if (data == null)
				loadView.setStatus(LoadView.Status.not_data);
			else{
				loadView.setStatus(LoadView.Status.successed);
				listView.stopRefresh();
				BikeListResult result = (BikeListResult) data;
				ArrayList<BikeResult> list = result.getBikes();
				adapter.setDatas(list);
				adapter.notifyDataSetChanged();
			}
			page = 2;
			getContext().getSharedPreferences("time", Context.MODE_PRIVATE).edit().putLong(BikeListFragment.this.getClass().getSimpleName()+getArguments().getInt(INTENT_TYPE,1),System.currentTimeMillis()).commit();

		}else if (TAG_LOADMORE.equals(tag)){
			if (data == null){
				listView.stopNoMore();
			}
			else{
				BikeListResult result = (BikeListResult) data;
				ArrayList<BikeResult> list = result.getBikes();getContext().getSharedPreferences("time", Context.MODE_PRIVATE).edit().putLong(BikeListFragment.this.getClass().getSimpleName()+getArguments().getInt(INTENT_TYPE,1),System.currentTimeMillis()).commit();
				if (list == null || list.size() == 0){
					listView.stopNoMore();
				}else{
					adapter.addDatas(list);
					adapter.notifyDataSetChanged();
					page ++;
					listView.stopLoadMore();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onNeedLogin(String tag) {
		if (TAG_REFRESH.equals(tag)){
			listView.stopRefresh();
		}else if(TAG_LOADMORE.equals(tag)){
			listView.stopLoadMore();
		}
		DialogUtil.resetLoginDialog(getActivity(),true);
	}

	@Override
	public void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause) {
		if (isVisibleToUser)
			location();
	}
}