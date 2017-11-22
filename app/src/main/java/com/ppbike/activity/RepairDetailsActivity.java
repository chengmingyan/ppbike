package com.ppbike.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.android.volley.toolbox.NetworkImageView;
import com.ppbike.R;
import com.ppbike.bean.RepairShopRequest;
import com.ppbike.bean.RepairShopResult;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.view.LoadView;

import java.io.IOException;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.commons.VolleyHelper;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/7/11.
 */
public class RepairDetailsActivity extends ParentActivity implements LocationSource,
        AMapLocationListener {

    public static final String INTENT_SHOP = "shop";
    private LoadView loadView;
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
//    private RepairShopDetailsResult result;
    private RepairShopResult result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_details);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        initView();

        loadView.setStatus(LoadView.Status.successed);
//        onRefresh();

        result = (RepairShopResult) getIntent().getSerializableExtra(INTENT_SHOP);
        onSucceed(null,false,result);
    }

    private void initView() {
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("修车铺详情");
        loadView = (LoadView) findViewById(R.id.loadView);
        loadView.setOnReLoadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadView.setStatus(LoadView.Status.loading);
                onRefresh();
            }
        });
    }

    private void onRefresh() {
        ResponseHandler handler = new ResponseHandler();
        handler.setOnFailedListener(this);
        handler.setOnSucceedListener(this);
        handler.setOnNeedLoginListener(this);
        RepairShopRequest request = new RepairShopRequest();
//        request.setShopId(getIntent().getIntExtra(INTENT_SHOPID, 0));
        try {
            String body = JacksonJsonUtil.toJson(request);
            String token = UserModel.getInstance(this).getUserBean().getToken();
            RequestModel.obtainRepairDetails(handler, body, token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.btn_link:
                Intent intent = new Intent();
                // 系统默认的action，用来打开默认的电话界面
                intent.setAction(Intent.ACTION_DIAL);
                // 需要拨打的号码
                intent.setData(Uri.parse("tel://" + result.getMobile()));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        loadView.setStatus(LoadView.Status.network_error);
        findViewById(R.id.layout).setVisibility(View.GONE);
        ToastUtil.show(this, (String) data);
    }

    @Override
    public void onNeedLogin(String tag) {
        DialogUtil.resetLoginDialog(this, true);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        result = (RepairShopResult) data;
        loadView.setStatus(LoadView.Status.successed);
        findViewById(R.id.layout).setVisibility(View.VISIBLE);
        NetworkImageView image = (NetworkImageView) findViewById(R.id.image);
        image.setDefaultImageResId(R.drawable.image_default);
        image.setErrorImageResId(R.drawable.image_default);
        TextView tv_address = (TextView) findViewById(R.id.tv_address);
        TextView tv_distance = (TextView) findViewById(R.id.tv_distance);
        TextView tv_addName = (TextView) findViewById(R.id.tv_addName);
        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        image.setImageUrl(result.getPicUrl(), VolleyHelper.getImageLoader());
        tv_address.setText(result.getAddress());
        tv_distance.setText(result.getDistance());
        tv_addName.setText(result.getAddNick());
        tv_name.setText(result.getBname());
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                ToastUtil.show(this, errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

}
