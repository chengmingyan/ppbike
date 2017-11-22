package com.ppbike.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ppbike.R;
import com.ppbike.bean.BikeDetailsResult;
import com.ppbike.bean.CreateOrderRequest;
import com.ppbike.bean.CreateOrderResult;
import com.ppbike.bean.NearBikeListRequest;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.util.TimeUtil;
import com.ppbike.view.IconView;
import com.ppbike.view.LoadingDialog;

import java.io.IOException;

import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.commons.VolleyHelper;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/17.
 */
public class OrderSureActivity extends ParentActivity {
    public static final String INTENT_REQUEST = "request";
    public static final String INTENT_BIKE_RESULT = "bikeResult";
    private TextView tv_count,tv_number,tv_totalPrice;
    private IconView icon_add,icon_reduction;
    private CreateOrderRequest request = new CreateOrderRequest();
    private int count;
    private Dialog loadingDialog;
    private BikeDetailsResult bikeDetailsResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_sure);
        initView();
        initData();
    }

    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("订单确认");
        findViewById(R.id.icon_back).setOnClickListener(this);
        icon_add = (IconView) findViewById(R.id.icon_add);
        icon_add.setOnClickListener(this);
        icon_add.setVisibility(View.GONE);
        icon_reduction = (IconView) findViewById(R.id.icon_reduction);
        icon_reduction.setOnClickListener(this);
        icon_reduction.setVisibility(View.GONE);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_number = (TextView) findViewById(R.id.tv_number);

        findViewById(R.id.tv_bikeCount).setVisibility(View.GONE);

        tv_totalPrice = (TextView) findViewById(R.id.tv_totalPrice);
        loadingDialog = new LoadingDialog(this);
    }

    private void initData(){
        NearBikeListRequest nearBikeListRequest = (NearBikeListRequest) getIntent().getSerializableExtra(INTENT_REQUEST);
        request.setEtime(nearBikeListRequest.getEtime());
        request.setLa(nearBikeListRequest.getLa());
        request.setStime(nearBikeListRequest.getStime());
        request.setLo(nearBikeListRequest.getLo());
        request.setDate(nearBikeListRequest.getDate());
        bikeDetailsResult = (BikeDetailsResult) getIntent().getSerializableExtra(INTENT_BIKE_RESULT);
        request.setBikeId(bikeDetailsResult.getBikeId());

        NetworkImageView image = (NetworkImageView) findViewById(R.id.image);
        image.setErrorImageResId(R.drawable.image_default);
        image.setErrorImageResId(R.drawable.image_default);
        image.setImageUrl(bikeDetailsResult.getPicUrl(), VolleyHelper.getImageLoader());

        TextView tv_bikeTitle = (TextView) findViewById(R.id.tv_bikeTitle);
        tv_bikeTitle.setText(bikeDetailsResult.getName());

        TextView tv_bikeInfo = (TextView) findViewById(R.id.tv_bikeInfo);
        tv_bikeInfo.setText(bikeDetailsResult.getColor() + "、" + bikeDetailsResult.getSpeed() + "、" + bikeDetailsResult.getBrand() + "、" + bikeDetailsResult.getSize());

        TextView tv_deposit = (TextView) findViewById(R.id.tv_deposit);
        tv_deposit.setText("押金：￥" + bikeDetailsResult.getDeposit()/100 + "/辆");

        TextView tv_pPrice = (TextView) findViewById(R.id.tv_pPrice);
        tv_pPrice.setText("租金：￥" + bikeDetailsResult.getPrice()/100 + "/天");

        TextView tv_startDate = (TextView) findViewById(R.id.tv_startDate);
        TextView tv_startTime = (TextView) findViewById(R.id.tv_startTime);
        TextView tv_endDate = (TextView) findViewById(R.id.tv_endDate);
        TextView tv_endTime = (TextView) findViewById(R.id.tv_endTime);
        TextView tv_dateNumber = (TextView) findViewById(R.id.tv_dateNumber);
        tv_startDate.setText(TimeUtil.systemTime2LocalTime(nearBikeListRequest.getStime(),TimeUtil.LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING));
        tv_startTime.setText(TimeUtil.systemTime2LocalTime(nearBikeListRequest.getStime(),"aHH:00"));
        tv_endDate.setText(TimeUtil.systemTime2LocalTime(nearBikeListRequest.getEtime(),TimeUtil.LOCAL_TIME_WITHOUT_YEAR_PATTERN_STRING));
        tv_endTime.setText(TimeUtil.systemTime2LocalTime(nearBikeListRequest.getEtime(),"aHH:00"));
        tv_dateNumber.setText(String.valueOf(nearBikeListRequest.getDate()));

        count = bikeDetailsResult.getCount();
        tv_count.setText("(库存" + count + "辆)");
        if (count > 1){
            icon_add.setVisibility(View.VISIBLE);
        }else{
            icon_add.setVisibility(View.GONE);
        }
        request.setCount(1);
        tv_number.setText("1");
        long money =  bikeDetailsResult.getPrice()*request.getDate();
        long deposit =  bikeDetailsResult.getDeposit();
        tv_totalPrice.setText("￥"+(money+deposit)/100);
    }

    private void onRefresh(){
        ResponseHandler handler = new ResponseHandler();
        handler.setOnNeedLoginListener(this);
        handler.setOnSucceedListener(this);
        handler.setOnFailedListener(this);
        String body = null;
        try {
            long money = request.getCount()*bikeDetailsResult.getPrice()*request.getDate();
            long deposit = request.getCount()*bikeDetailsResult.getDeposit() ;
            request.setDeposit(deposit);
            request.setMoney(money);
            String token = UserModel.getInstance(this).getUserBean().getToken();
            body = JacksonJsonUtil.toJson(request);
            Log.e("json==",body);
            RequestModel.createOrder(handler,body,token);
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
            case R.id.icon_add:
            {
                int number = request.getCount();
                number++;
                request.setCount(number);
                tv_number.setText(String.valueOf(number));
                long money =  number*bikeDetailsResult.getPrice()*request.getDate() ;
                long deposit =  number*bikeDetailsResult.getDeposit() ;
                tv_totalPrice.setText("￥"+(money+deposit));
                icon_reduction.setVisibility(View.VISIBLE);
                if (number >= count){
                    icon_add.setVisibility(View.GONE);
                }else{
                    icon_add.setVisibility(View.VISIBLE);
                }
            }
                break;
            case R.id.icon_reduction:
            {
                int number = request.getCount();
                number--;
                request.setCount(number);
                tv_number.setText(String.valueOf(number));
                long money =  number*bikeDetailsResult.getPrice()*request.getDate() ;
                long deposit =  number*bikeDetailsResult.getDeposit() ;
                tv_totalPrice.setText("￥"+(money+deposit));
                icon_add.setVisibility(View.VISIBLE);
                if (number == 1){
                    icon_reduction.setVisibility(View.GONE);
                }else{
                    icon_reduction.setVisibility(View.VISIBLE);
                }
            }
                break;
            case R.id.btn_sure: {
                loadingDialog.show();
                onRefresh();
            }
            break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        CreateOrderResult result = (CreateOrderResult) data;
        Intent intent = new Intent(this,PayActivity.class);
        intent.putExtra(PayActivity.INTENT_RESULT,result);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        DialogUtil.resetLoginDialog(this,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode){
            loadingDialog.show();
            onRefresh();
        }else{
            DialogUtil.resetLoginDialog(this,true);
        }
    }
}
