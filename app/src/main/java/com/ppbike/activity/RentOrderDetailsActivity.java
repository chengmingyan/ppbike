package com.ppbike.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ppbike.R;
import com.ppbike.bean.OrderDetailsRequest;
import com.ppbike.bean.OrderDetailsResult;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.util.TimeUtil;
import com.ppbike.view.LoadView;
import com.ppbike.view.LoadingDialog;

import java.io.IOException;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.commons.VolleyHelper;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/19.
 */
public class RentOrderDetailsActivity extends ParentActivity {

    public static final String INTENT_ORDERID = "orderId";
    private ResponseHandler refreshHandler,updateOrderHandler;
    private final String TAG_REFRESH = "refresh",TAG_UPDATE_ORDER = "update_order";
    private LoadView loadView;
    private LoadingDialog loadingDialog;
    private OrderDetailsResult result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_details);
        initView();
        initData();
        onRefresh();
    }

    private void initView(){
        loadView = (LoadView) findViewById(R.id.loadView);
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
                        findViewById(R.id.layout).setVisibility(View.VISIBLE);
                        break;
                    default:
                        findViewById(R.id.layout).setVisibility(View.GONE);
                        break;
                }
            }
        });

        loadingDialog = new LoadingDialog(this);
    }

    private void initData(){
        refreshHandler = new ResponseHandler(TAG_REFRESH);
        refreshHandler.setOnNeedLoginListener(this);
        refreshHandler.setOnSucceedListener(this);
        refreshHandler.setOnFailedListener(this);

        updateOrderHandler = new ResponseHandler(TAG_UPDATE_ORDER);
        updateOrderHandler.setOnNeedLoginListener(this);
        updateOrderHandler.setOnSucceedListener(this);
        updateOrderHandler.setOnFailedListener(this);

        loadView.setStatus(LoadView.Status.loading);
    }

    private void setData(){
        NetworkImageView image = (NetworkImageView) findViewById(R.id.image);
        image.setErrorImageResId(R.drawable.image_default);
        image.setErrorImageResId(R.drawable.image_default);
        image.setImageUrl(result.getPicUrl(), VolleyHelper.getImageLoader());

        TextView tv_bikeTitle = (TextView) findViewById(R.id.tv_bikeTitle);
        tv_bikeTitle.setText(result.getName());

        TextView tv_bikeInfo = (TextView) findViewById(R.id.tv_bikeInfo);
//        tv_bikeInfo.setText(result.getColor() + "、" + result.getSpeed() + "、" + result.getBrand() + "、" + result.getSize());

        TextView tv_deposit = (TextView) findViewById(R.id.tv_deposit);
        tv_deposit.setText("押金：￥" + result.getDeposit()/100 );

        TextView tv_pPrice = (TextView) findViewById(R.id.tv_pPrice);
        tv_pPrice.setText("租金：￥" + result.getMoney()/100 );

        TextView tv_startDate = (TextView) findViewById(R.id.tv_startDate);
        TextView tv_startTime = (TextView) findViewById(R.id.tv_startTime);
        TextView tv_endDate = (TextView) findViewById(R.id.tv_endDate);
        TextView tv_endTime = (TextView) findViewById(R.id.tv_endTime);
        TextView tv_dateNumber = (TextView) findViewById(R.id.tv_dateNumber);
        tv_startDate.setText(TimeUtil.systemTime2LocalTime(result.getStime(),TimeUtil.LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING));
        tv_startTime.setText(TimeUtil.systemTime2LocalTime(result.getStime(),"aHH:00"));
        tv_endDate.setText(TimeUtil.systemTime2LocalTime(result.getEtime(),TimeUtil.LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING));
        tv_endTime.setText(TimeUtil.systemTime2LocalTime(result.getEtime(),"aHH:00"));
        tv_dateNumber.setText(String.valueOf(result.getDates()));

        TextView tv_address = (TextView) findViewById(R.id.tv_address);
        tv_address.setText("取车地址："+result.getAddress() );

        TextView tv_count = (TextView) findViewById(R.id.tv_count);
        tv_count.setText("共"+result.getCount()+"辆，合计" );

        TextView tv_totalPrice = (TextView) findViewById(R.id.tv_totalPrice);
        tv_totalPrice.setText("￥"+(result.getMoney()+result.getDeposit())/100);

    }
    private void onRefresh(){
        OrderDetailsRequest request = new OrderDetailsRequest();
        request.setOrderId(getIntent().getStringExtra(INTENT_ORDERID));
        try {
            String token = UserModel.getInstance(this).getUserBean().getToken();
            String body = JacksonJsonUtil.toJson(request);
            RequestModel.obtainRentBikeOrderDetails(refreshHandler,TAG_REFRESH,body,token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;

        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        if (TAG_REFRESH.equals(tag)){
            loadView.setStatus(LoadView.Status.network_error);
        }else if (TAG_UPDATE_ORDER.equals(tag)){

        }
        ToastUtil.show(this,(String)data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {

        if (TAG_REFRESH.equals(tag)){
            if (loadingDialog.isShowing())
                loadingDialog.dismiss();
            result = (OrderDetailsResult) data;
            loadView.setStatus(LoadView.Status.successed);
            setData();
        }else if (TAG_UPDATE_ORDER.equals(tag)){
            onRefresh();
        }
    }

    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        if (TAG_REFRESH.equals(tag)){

        }else if (TAG_UPDATE_ORDER.equals(tag)){

        }
        DialogUtil.resetLoginDialog(this,true);
    }
}
