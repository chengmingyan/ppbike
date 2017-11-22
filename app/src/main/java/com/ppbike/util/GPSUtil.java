package com.ppbike.util;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chengmingyan on 16/7/14.
 */
public class GPSUtil {
    private static GPSUtil instance;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;

    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;
    //用户页面需要传递的listener
    private AMapLocationListener locationListener;

    private AMapLocation aMapLocation;
    private final static long VALID_TIME = 30*60*1000;
    private long lastLocationtime;

    public static GPSUtil getInstance(Context context) {
        if (instance == null) {
            instance = new GPSUtil(context);
        }
        return instance;
    }

    public GPSUtil(Context context) {
        //初始化定位
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);

        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        //如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会。
//        if (mLocationOption.isOnceLocationLatest()) {
//            mLocationOption.setOnceLocationLatest(true);
//        }

        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

    }

    public void startLocation(AMapLocationListener locationListener){
        this.locationListener = locationListener;
        //启动定位
        mLocationClient.startLocation();
    }

    public void stopLocation(){
        mLocationClient.stopLocation();
    }

    public AMapLocation getaMapLocation(){
        if (System.currentTimeMillis() - lastLocationtime > VALID_TIME){
            return  null;
        }
        return aMapLocation;
    }

    //声明定位回调监听器
    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (locationListener != null)
                locationListener.onLocationChanged(aMapLocation);
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                    GPSUtil.this.aMapLocation = aMapLocation;
                    StringBuffer sb = new StringBuffer();
                    //定位成功回调信息，设置相关消息
                    sb.append(aMapLocation.getLocationType());//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    sb.append(aMapLocation.getLatitude());//获取纬度
                    sb.append(aMapLocation.getLongitude());//获取经度
                    sb.append(aMapLocation.getAccuracy());//获取精度信息
                    lastLocationtime = System.currentTimeMillis();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(aMapLocation.getTime());
                    sb.append(df.format(date));//定位时间
                    sb.append(aMapLocation.getAddress());//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    sb.append(aMapLocation.getCountry());//国家信息
                    sb.append(aMapLocation.getProvince());//省信息
                    sb.append(aMapLocation.getCity());//城市信息
                    sb.append(aMapLocation.getDistrict());//城区信息
                    sb.append(aMapLocation.getStreet());//街道信息
                    sb.append(aMapLocation.getStreetNum());//街道门牌号信息
                    sb.append(aMapLocation.getCityCode());//城市编码
                    sb.append(aMapLocation.getPoiName());
                    sb.append(aMapLocation.getAdCode());//地区编码
                    sb.append(aMapLocation.getAoiName());//获取当前定位点的AOI信息
                    sb.append("网络定位成功");
                    Log.e("AMapSuccess=",sb.toString());
//                    4 39.897536 116.428704 29.0 2016-07-14 14:29:57 北京市东城区东花市大街靠近体坛周报 中国 北京市 北京市 东城区 东花市大街 91号 010体坛周报110101 东花市北里西区 网络定位成功
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };
}
