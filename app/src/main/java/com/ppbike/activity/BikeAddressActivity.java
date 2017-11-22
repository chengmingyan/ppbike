package com.ppbike.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.ppbike.R;
import com.ppbike.adapter.PoiAddressAdapter;
import com.ppbike.bean.PoiAddressBean;
import com.ppbike.view.IconView;
import com.ppbike.view.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import cn.master.util.utils.ToastUtil;

/**
 * Created by chengmingyan on 16/6/20.
 */
public class BikeAddressActivity extends FragmentActivity implements
        AMap.OnMapLoadedListener,View.OnClickListener {

    private AutoCompleteTextView edit_search;
    private IconView icon_clear;
    private ListView listView;
    private AMap aMap;

    private PoiSearch.Query query;// Poi查询条件类
    List<PoiItem> poiItems = null;
    public final static String INTENT_BEAN = "bean";
    private PoiAddressBean currentPoiAddressBean;
    private LoadingDialog loadingDialog;
    private InputMethodManager inputMethodManager;

    private PoiAddressAdapter poiAddressAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_address);
        initView();
        initData();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    private void initView() {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("取车地址");
        findViewById(R.id.icon_back).setOnClickListener(this);
        edit_search = (AutoCompleteTextView) findViewById(R.id.edit_search);
        edit_search.addTextChangedListener(searchTextWatcher);
        edit_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = parent.getAdapter().getItem(position);
                if (object instanceof String){
                    query = new PoiSearch.Query((String)object, "", currentPoiAddressBean.getCity());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
                    query.setPageSize(20);// 设置每页最多返回多少条poiitem
                    query.setPageNum(1);// 设置查第一页
                    // 查询兴趣点
                    PoiSearch search = new PoiSearch(BikeAddressActivity.this, query);
                    search.setOnPoiSearchListener(searchListener);
                    // 异步搜索
                    search.searchPOIAsyn();
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                    }
                }
            }
        });
        icon_clear = (IconView) findViewById(R.id.icon_clear);
        icon_clear.setOnClickListener(this);
        icon_clear.setVisibility(View.INVISIBLE);

        initMapView();

        listView = (ListView) findViewById(R.id.listView);
        poiAddressAdapter = new PoiAddressAdapter();
        listView.setAdapter(poiAddressAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                poiAddressAdapter.setSelectedPosition(position);
                poiAddressAdapter.notifyDataSetChanged();
                PoiAddressBean bean = (PoiAddressBean) parent.getAdapter().getItem(position);
                LatLng centerPoint = new LatLng(bean.getLa(), bean.getLo());
                //设置中心点和缩放比例
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        centerPoint, 18, 0, 30)));
//                aMap.moveCamera(CameraUpdateFactory.changeLatLng(centerPoint));
                aMap.clear();
                aMap.addMarker(new MarkerOptions()
                        .perspective(true)
                        .position(centerPoint)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            }
        });

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

        query = new PoiSearch.Query(currentPoiAddressBean.getPoiAddress(), "", currentPoiAddressBean.getCity());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);// 设置查第一页
        // 查询兴趣点
        PoiSearch search = new PoiSearch(BikeAddressActivity.this, query);
        search.setOnPoiSearchListener(searchListener);
        // 异步搜索
        search.searchPOIAsyn();

        LatLng centerPoint = new LatLng(currentPoiAddressBean.getLa(), currentPoiAddressBean.getLo());
        //设置中心点和缩放比例
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(centerPoint));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(200));

    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0){
                icon_clear.setVisibility(View.VISIBLE);
            }else{
                icon_clear.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 1){
                query = new PoiSearch.Query(s.toString(), "", currentPoiAddressBean.getCity());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
                query.setPageSize(20);// 设置每页最多返回多少条poiitem
                query.setPageNum(1);// 设置查第一页
                // 查询兴趣点
                PoiSearch search = new PoiSearch(BikeAddressActivity.this, query);
                search.setOnPoiSearchListener(keyListener);
                // 异步搜索
                search.searchPOIAsyn();

            }
        }
    };

    @Override
    public void onMapLoaded() {

    }
    private PoiSearch.OnPoiSearchListener keyListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult poiResult, int code) {
            if (code == 1000){
                List<String> strs = new ArrayList<>();
                List<PoiItem> items = poiResult.getPois();
                if (items != null && items.size() > 0) {
                    PoiItem item = null;
                    for (int i = 0, count = items.size(); i < count; i++) {
                        item = items.get(i);
                        strs.add(item.getTitle());
                    }
                    // 给ListView赋值，显示结果
                    ArrayAdapter<String> array = new ArrayAdapter<>(BikeAddressActivity.this,
                            R.layout.adapter_auto_poi, strs);
                    edit_search.setAdapter(array);
                }
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int code) {

        }
    };

    private PoiSearch.OnPoiSearchListener searchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult poiResult, int code) {
            if(loadingDialog.isShowing())
                loadingDialog.dismiss();
            if (code == 1000){
                boolean isSearch = true;
                List<String> strs = new ArrayList<>();
                if (poiItems == null){
                    poiItems = poiResult.getPois();
                    PoiItem item = new PoiItem(null,new LatLonPoint(currentPoiAddressBean.getLa(),currentPoiAddressBean.getLo()),currentPoiAddressBean.getAddress(),null);
                    poiItems.add(0,item);
                    isSearch = false;
                }else{
                    poiItems = poiResult.getPois();
                }
                if (poiItems != null && poiItems.size() > 0) {
                    PoiItem item = null;
                    ArrayList<PoiAddressBean> list = new ArrayList<>();
                    for (int i = 0, count = poiItems.size(); i < count; i++) {
                        item = poiItems.get(i);
                        strs.add(item.getTitle());
                        PoiAddressBean bean = new PoiAddressBean();
                        bean.setAddress(item.getTitle());
                        bean.setDistance(item.getDistance());
                        bean.setDistrict(item.getDirection());
                        bean.setLa(item.getLatLonPoint().getLatitude());
                        bean.setLo(item.getLatLonPoint().getLongitude());
                        list.add(bean);
                    }
                    LatLng centerPoint = new LatLng(list.get(0).getLa(), list.get(0).getLo());
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
                    // 给ListView赋值，显示结果
                    poiAddressAdapter.setSelectedPosition(0);
                    poiAddressAdapter.setDatas(list,isSearch);
                    poiAddressAdapter.notifyDataSetChanged();
                }
            }else if (1901 == code){
                ToastUtil.show(BikeAddressActivity.this,"未搜索到相关地址信息");
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int code) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.icon_clear:
                edit_search.getText().clear();
                break;
            case R.id.btn_search:
            {
                String key = edit_search.getText().toString().trim();
                if (TextUtils.isEmpty(key)){
                    ToastUtil.show(this,"搜索内容不能为空");
                    return;
                }
                loadingDialog.show();
                query = new PoiSearch.Query(key, "", "北京市");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
                query.setPageSize(20);// 设置每页最多返回多少条poiitem
                query.setPageNum(1);// 设置查第一页
                // 查询兴趣点
                PoiSearch search = new PoiSearch(BikeAddressActivity.this, query);
                search.setOnPoiSearchListener(searchListener);
                // 异步搜索
                search.searchPOIAsyn();
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
                }
            }
                break;
            case R.id.btn_sure:
                PoiAddressBean bean = poiAddressAdapter.getItem(poiAddressAdapter.getSelectedPosition());
                getIntent().putExtra(INTENT_BEAN,bean);
                setResult(RESULT_OK,getIntent());
                finish();
                break;
        }
    }

}
