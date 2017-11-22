package com.ppbike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ppbike.R;
import com.ppbike.bean.BikeDetailsRequest;
import com.ppbike.bean.BikeDetailsResult;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.view.LoadView;

import java.io.IOException;

import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.commons.VolleyHelper;
import cn.master.volley.models.response.handler.ResponseHandler;
import cn.master.volley.models.response.listener.OnNeedLoginListener;

/**
 * Created by chengmingyan on 16/6/17.
 */
public class UploadResultActivity extends ParentActivity implements OnNeedLoginListener{
    public static final String INTENT_BIKEID = "bikeId";
    private LoadView loadView;
    private TextView tv_status,tv_bikeTitle,tv_bikeInfo,tv_bikeCount,tv_deposit,tv_pPrice;
    private NetworkImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_result);
        initView();
        loadView.setStatus(LoadView.Status.loading);
        onRefresh();
    }

    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("上传结果");
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_bikeTitle = (TextView) findViewById(R.id.tv_bikeTitle);
        tv_bikeInfo = (TextView) findViewById(R.id.tv_bikeInfo);
        tv_deposit = (TextView) findViewById(R.id.tv_deposit);
        tv_pPrice = (TextView) findViewById(R.id.tv_pPrice);
        image = (NetworkImageView) findViewById(R.id.image);

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
    }
    private void onRefresh(){
        ResponseHandler handler = new ResponseHandler();
        handler.setOnNeedLoginListener(this);
        handler.setOnSucceedListener(this);
        handler.setOnFailedListener(this);

        BikeDetailsRequest request = new BikeDetailsRequest();
        request.setBikeId(getIntent().getIntExtra(INTENT_BIKEID,0));
        try {
            String body = JacksonJsonUtil.toJson(request);
            String token = UserModel.getInstance(this).getUserBean().getToken();
            RequestModel.obtainBikeDetatils(handler,null,body,token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private  void setData(BikeDetailsResult result){
        image.setImageUrl(result.getPicUrl(), VolleyHelper.getImageLoader());
        tv_bikeTitle.setText(result.getName());
        tv_bikeInfo.setText(result.getBrand() + " " + result.getSpeed() + " " + result.getColor() + " " + result.getDegree());
        tv_bikeCount.setText("库存："+result.getCount());
        tv_deposit.setText("押金：￥"+result.getDeposit());
        tv_pPrice.setText("租金：￥"+result.getPrice() + "/天");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.btn_continue: {
                Intent intent = new Intent(this,UploadBikeActivity.class);
                startActivity(intent);
                finish();
            }
            break;
            case R.id.btn_booth: {
                Intent intent = new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(MainActivity.INTENT_PAGE,MainActivity.PAGE_BOOTH);
                startActivity(intent);
                finish();
            }
            break;
            case R.id.btn_home: {
                Intent intent = new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(MainActivity.INTENT_PAGE,MainActivity.PAGE_HOME);
                startActivity(intent);
                finish();
            }
            break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        loadView.setStatus(LoadView.Status.network_error);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        loadView.setStatus(LoadView.Status.successed);
        BikeDetailsResult result = (BikeDetailsResult) data;
        setData(result);
    }

    @Override
    public void onNeedLogin(String tag) {
        loadView.setStatus(LoadView.Status.network_error);
        DialogUtil.resetLoginDialog(this,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode){
            loadView.setStatus(LoadView.Status.loading);
            onRefresh();
        }
    }
}
