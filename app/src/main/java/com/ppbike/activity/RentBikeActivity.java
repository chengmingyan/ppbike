package com.ppbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.ppbike.R;
import com.ppbike.bean.NearBikeListRequest;
import com.ppbike.bean.PoiAddressBean;
import com.ppbike.util.GPSUtil;
import com.ppbike.util.TimeUtil;
import com.ppbike.view.LoadingDialog;

import java.util.Calendar;

import cn.master.util.utils.RequestCodeUtil;
import cn.master.util.utils.ToastUtil;

/**
 * Created by chengmingyan on 16/6/20.
 */
public class RentBikeActivity extends FragmentActivity implements View.OnClickListener {
    private TextView tv_location,tv_startDate,tv_startTime,tv_endDate,tv_endTime,tv_dateNumber;
    private LoadingDialog loadingDialog;
    private NearBikeListRequest request = new NearBikeListRequest();
    private PoiAddressBean currentPoiAddressBean = new PoiAddressBean();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental);
        initView();
        location();
        initData();
    }

    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("我要租车");
        findViewById(R.id.icon_back).setOnClickListener(this);
        findViewById(R.id.icon_time_left).setVisibility(View.VISIBLE);
        findViewById(R.id.icon_time_right).setVisibility(View.VISIBLE);

        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_startDate = (TextView) findViewById(R.id.tv_startDate);
        tv_startTime = (TextView) findViewById(R.id.tv_startTime);
        tv_endDate = (TextView) findViewById(R.id.tv_endDate);
        tv_endTime = (TextView) findViewById(R.id.tv_endTime);
        tv_dateNumber = (TextView) findViewById(R.id.tv_dateNumber);

        loadingDialog = new LoadingDialog(this);
    }

    private void initData(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,1);
        calendar.set(Calendar.MINUTE,0);
        request.setStime(calendar.getTimeInMillis());
        tv_startDate.setText(TimeUtil.systemTime2LocalTime(calendar.getTimeInMillis(),TimeUtil.LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING));
        tv_startTime.setText(TimeUtil.systemTime2LocalTime(calendar.getTimeInMillis(),"aHH:00"));
        calendar.add(Calendar.DAY_OF_YEAR,2);
        request.setEtime(calendar.getTimeInMillis());
        tv_endDate.setText(TimeUtil.systemTime2LocalTime(calendar.getTimeInMillis(),TimeUtil.LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING));
        tv_endTime.setText(TimeUtil.systemTime2LocalTime(calendar.getTimeInMillis(),"aHH:00"));

    }
    private void location(){
        AMapLocation location = GPSUtil.getInstance(this).getaMapLocation();
        if (location != null){
            tv_location.setText(location.getAddress()+"(当前地址)");
            currentPoiAddressBean.setLo(location.getLongitude());
            currentPoiAddressBean.setLa(location.getLatitude());
            currentPoiAddressBean.setAddress(location.getAddress());
            currentPoiAddressBean.setPoiAddress(location.getAoiName());
            currentPoiAddressBean.setDistrict(location.getDistrict());
        }else{
            loadingDialog.show();
            GPSUtil.getInstance(this).startLocation(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation location) {
                    if (loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    GPSUtil.getInstance(RentBikeActivity.this).stopLocation();
                    if (location == null || location.getErrorCode() != AMapLocation.LOCATION_SUCCESS){
                        tv_location.setText("定位失败");
                    }else{
                        tv_location.setText(location.getAddress()+"(当前地址)");
                        currentPoiAddressBean.setLo(location.getLongitude());
                        currentPoiAddressBean.setLa(location.getLatitude());
                        currentPoiAddressBean.setAddress(location.getAddress());
                        currentPoiAddressBean.setPoiAddress(location.getAoiName());
                        currentPoiAddressBean.setDistrict(location.getDistrict());
                    }
                }
            });
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.layout_location:
            {
                Intent intent = new Intent(this,BikeAddressActivity.class);
                intent.putExtra(BikeAddressActivity.INTENT_BEAN,currentPoiAddressBean);
                startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(BikeAddressActivity.class));
            }
                break;
            case R.id.layout_startTime:
            {
                TimePickerDialog dialog = new TimePickerDialog.Builder()
                        .setCallBack(new OnDateSetListener() {
                                         @Override
                                         public void onDateSet(TimePickerDialog timePickerView, long dateTime) {
                                             tv_startDate.setText(TimeUtil.systemTime2LocalTime(dateTime,TimeUtil.LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING));
                                             tv_startTime.setText(TimeUtil.systemTime2LocalTime(dateTime,"aHH:mm"));
                                             request.setStime(dateTime);
                                             long time = request.getEtime() - request.getStime();
                                             if (time <= 0){
                                                 tv_dateNumber.setText("0");
                                             }else{
                                                 int day = (int)time/(24*60*60*1000);
                                                 tv_dateNumber.setText(String.valueOf(day + 1));
                                             }
                                         }
                                     }
                        )
                        .setTitleStringId("取车时间")
                        .setCyclic(false)
                        .setCurrentTimeMillseconds(request.getStime())
                        .setDayNumber(30)
                        .build();
                 dialog .show(getSupportFragmentManager(), "start");

            }
                break;
            case R.id.layout_endTime:
            {
                TimePickerDialog dialog = new TimePickerDialog.Builder()
                        .setCallBack(new OnDateSetListener() {
                                         @Override
                                         public void onDateSet(TimePickerDialog timePickerView, long dateTime) {
                                                tv_endDate.setText(TimeUtil.systemTime2LocalTime(dateTime,TimeUtil.LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING));
                                                tv_endTime.setText(TimeUtil.systemTime2LocalTime(dateTime,"aHH:mm"));
                                                request.setEtime(dateTime);
                                                long time = request.getEtime() - request.getStime();
                                                if (time <= 0){
                                                    tv_dateNumber.setText("0");
                                                }else{
                                                    int day = (int)time/(24*60*60*1000);
                                                    tv_dateNumber.setText(String.valueOf(day + 1));
                                                }
                                         }
                                     }
                        )
                        .setTitleStringId("还车时间")
                        .setCyclic(false)
                        .setCurrentTimeMillseconds(request.getEtime())
                        .setDayNumber(30)
                        .build();
                dialog .show(getSupportFragmentManager(), "end");

            }
                break;
            case R.id.btn_select:
            {
                if (request.getEtime() <= request.getStime()){
                    ToastUtil.show(this,"归还车辆时间不正确");
                    return;
                }
                request.setDate(Integer.parseInt(tv_dateNumber.getText().toString()));
                Intent intent = new Intent(this,BikeListActivity.class);
                intent.putExtra(BikeListActivity.INTENT_REQUEST,request);
                startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(BikeAddressActivity.class));
            }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode){
            currentPoiAddressBean = (PoiAddressBean) data.getSerializableExtra(BikeAddressActivity.INTENT_BEAN);
            tv_location.setText(currentPoiAddressBean.getAddress() );
        }
    }
}
