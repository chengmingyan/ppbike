package com.ppbike.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.NaviPara;
import com.ppbike.R;
import com.ppbike.bean.PoiAddressBean;
import com.ppbike.view.LoadingDialog;

/**
 * Created by chengmingyan on 16/6/20.
 */
public class RouteActivity extends FragmentActivity implements
        AMap.OnMapLoadedListener,View.OnClickListener {

    private AMap aMap;
    private TextView tv_address;
    public final static String INTENT_BEAN = "bean";
    private PoiAddressBean currentPoiAddressBean;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        initView();
        initData();

    }

    private void initView() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("取车地址");
        findViewById(R.id.icon_back).setOnClickListener(this);

        initMapView();

        tv_address = (TextView) findViewById(R.id.tv_address);

        loadingDialog = new LoadingDialog(this);

    }

    private void initMapView(){
        if (aMap == null) {
            aMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            aMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层

            // 自定义系统定位蓝点
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            // 自定义定位蓝点图标
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                    fromResource(R.mipmap.ic_pointer_xhdpi));

            // 将自定义的 myLocationStyle 对象添加到地图上
            aMap.setMyLocationStyle(myLocationStyle);
/**
 * 设置显示地图默认的缩放按钮
 */
            aMap.getUiSettings().setZoomControlsEnabled(false);

            aMap.setOnMapLoadedListener(this);
        }
    }

    private void initData(){
        currentPoiAddressBean = (PoiAddressBean) getIntent().getSerializableExtra(INTENT_BEAN);

        LatLng centerPoint = new LatLng(currentPoiAddressBean.getLa(), currentPoiAddressBean.getLo());
        //设置中心点和缩放比例
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(centerPoint));
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                centerPoint, 18, 0, 30)));
        aMap.clear();
        aMap.addMarker(new MarkerOptions()
                .perspective(true)
                .position(centerPoint)
                .icon(BitmapDescriptorFactory.
                        fromResource(R.mipmap.ic_pointer_xhdpi)));

        tv_address.setText(currentPoiAddressBean.getAddress());
    }


    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.btn_route:
            {
                // 构造导航参数
                NaviPara naviPara = new NaviPara();
                // 设置终点位置
                LatLng centerPoint = new LatLng(currentPoiAddressBean.getLa(), currentPoiAddressBean.getLo());
                naviPara.setTargetPoint(centerPoint);
                // 设置导航策略，这里是避免拥堵
                naviPara.setNaviStyle(NaviPara.DRIVING_AVOID_CONGESTION);
                try {
                    // 调起高德地图导航
                    AMapUtils.openAMapNavi(naviPara, getApplicationContext());
                } catch (com.amap.api.maps.AMapException e) {
                    // 如果没安装会进入异常，调起下载页面
                    AMapUtils.getLatestAMapApp(getApplicationContext());
                }
            }
                break;

        }
    }

}
