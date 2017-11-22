package com.ppbike.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ppbike.R;
import com.ppbike.adapter.CommentAdapter;
import com.ppbike.bean.BikeDetailsRequest;
import com.ppbike.bean.BikeDetailsResult;
import com.ppbike.bean.CommentListResult;
import com.ppbike.bean.NearBikeListRequest;
import com.ppbike.bean.PoiAddressBean;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.util.ShareSDKUtil;
import com.ppbike.view.LoadView;
import com.ppbike.view.LoadingDialog;
import com.ppbike.view.StillListView;

import java.io.IOException;

import cn.master.util.utils.ScreenUtil;
import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.commons.VolleyHelper;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/19.
 */
public class BikeDetailsActivity extends ParentActivity {
    public static final String INTENT_REQUEST = "request";
    public static final String INTENT_BIKEID = "bikeId";
    private TextView tv_title;
    private LoadView loadView;
    private LinearLayout layout_evaluation;
    private StillListView listView;
    private ResponseHandler detailsHandler,evaluationHandler;
    private final String TAG_DETAILS = "details",TAG_EVALUATION = "evaluation";
    private BikeDetailsResult result;
    private NearBikeListRequest nearBikeListRequest;
    private long bikeId;
    private Dialog loadingDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_details);
        initView();
        initData();
    }

    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        findViewById(R.id.icon_share).setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("单车详情");

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
                        findViewById(R.id.btn_sure).setVisibility(View.GONE);
                        break;
                    default:
                        findViewById(R.id.layout).setVisibility(View.GONE);
                        findViewById(R.id.btn_sure).setVisibility(View.GONE);
                        break;
                }
            }
        });

        layout_evaluation = (LinearLayout) findViewById(R.id.layout_evaluation);
        listView = (StillListView) findViewById(R.id.listView);
        layout_evaluation.setVisibility(View.GONE);

        loadingDialog = new LoadingDialog(this);
    }

    private void initData(){
        bikeId = getIntent().getLongExtra(INTENT_BIKEID,0);
        nearBikeListRequest = (NearBikeListRequest) getIntent().getSerializableExtra(INTENT_REQUEST);
        detailsHandler = new ResponseHandler(TAG_DETAILS);
        detailsHandler.setOnFailedListener(this);
        detailsHandler.setOnSucceedListener(this);
        detailsHandler.setOnNeedLoginListener(this);

        evaluationHandler = new ResponseHandler(TAG_EVALUATION);
        evaluationHandler.setOnFailedListener(this);
        evaluationHandler.setOnSucceedListener(this);
        evaluationHandler.setOnNeedLoginListener(this);

        loadView.setStatus(LoadView.Status.loading);

    }

    private void setData(BikeDetailsResult result){
        NetworkImageView image = (NetworkImageView) findViewById(R.id.image);
        image.setDefaultImageResId(R.drawable.image_default);
        image.setErrorImageResId(R.drawable.image_default);
        image.setImageUrl(result.getPicUrl(), VolleyHelper.getImageLoader());
        image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.getScreenWidth(this)));
        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(result.getName());
        TextView tv_pPrice = (TextView) findViewById(R.id.tv_pPrice);
        tv_pPrice.setText("租金：￥"+result.getPrice()+"/天");
        TextView tv_deposit = (TextView) findViewById(R.id.tv_deposit);
        tv_deposit.setText("押金：￥"+result.getDeposit()+"/辆");
        TextView tv_count = (TextView) findViewById(R.id.tv_count);
        tv_count.setText("库存："+result.getCount());
        TextView tv_color = (TextView) findViewById(R.id.tv_color);
        tv_color.setText("颜色：" + result.getColor());
        TextView tv_speed = (TextView) findViewById(R.id.tv_speed);
        tv_speed.setText("速别：" + result.getSpeed());
        TextView tv_size = (TextView) findViewById(R.id.tv_size);
        tv_size.setText("车轮尺寸：" + result.getSize());
        TextView tv_brand = (TextView) findViewById(R.id.tv_brand);
        tv_brand.setText("品牌：" + result.getBrand());
        TextView tv_degree = (TextView) findViewById(R.id.tv_degree);
        tv_degree.setText("新旧程度：" + result.getDegree());
        TextView tv_nprice = (TextView) findViewById(R.id.tv_nprice);
        tv_nprice.setText("原价：" + result.getNprice());
        TextView tv_address = (TextView) findViewById(R.id.tv_address);
        tv_address.setText("取车地址：" + result.getPname());
    }

    private void setCommentDatas(CommentListResult result){
        if (result.getCount() == 0)
            return;
        layout_evaluation.setVisibility(View.VISIBLE);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar_total);
        ratingBar.setRating(result.getAverage());
        TextView tv_evaluation = (TextView) findViewById(R.id.tv_evaluation);
        tv_evaluation.setText(" " + result.getAverage()+"分");
        TextView tv_evaluationCount = (TextView) findViewById(R.id.tv_evaluationCount);
        tv_evaluationCount.setText(result.getCount() + "人评价" );
        CommentAdapter adapter = new CommentAdapter(result.getComments());
        listView.setAdapter(adapter);
    }
    private void onRefresh(){
        BikeDetailsRequest request = new BikeDetailsRequest();
        request.setBikeId(getIntent().getLongExtra(INTENT_BIKEID,0));
        request.setId(0);
        try {
            String body = JacksonJsonUtil.toJson(request);
            String token = UserModel.getInstance(this).getUserBean().getToken();
            RequestModel.obtainBikeDetatils(detailsHandler,TAG_DETAILS,body,token);
            RequestModel.obtainBikeDetatilsEvaluation(evaluationHandler,TAG_EVALUATION,body,token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loadView.getStatus() == LoadView.Status.successed){
            loadingDialog.show();
        }
        onRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.icon_share:
                ShareSDKUtil.showShare(this,"人人单车",result.getName(),result.getPicUrl(),"http://www.baidu.com");
                break;
            case R.id.layout_location:
            {
                PoiAddressBean bean = new PoiAddressBean();
                bean.setAddress(result.getPname());
                bean.setLo(result.getLo());
                bean.setLa(result.getLo());
                Intent intent = new Intent(this,RouteActivity.class);
                intent.putExtra(RouteActivity.INTENT_BEAN,bean);
                startActivity(intent);
            }
                break;
            case R.id.layout_link:
            {
                Intent intent = new Intent();
                // 系统默认的action，用来打开默认的电话界面
                intent.setAction(Intent.ACTION_DIAL);
                // 需要拨打的号码
                intent.setData(Uri.parse("tel:" + result.getMobile()));
                startActivity(intent);
            }
                break;
            case R.id.btn_sure:
            {
                Intent intent = new Intent(this,OrderSureActivity.class);
                intent.putExtra(OrderSureActivity.INTENT_BIKE_RESULT,result);
                intent.putExtra(OrderSureActivity.INTENT_REQUEST,nearBikeListRequest);
                startActivity(intent);
            }
                break;

        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        ToastUtil.show(this,(String)data);
        if (TAG_DETAILS.equals(tag)){
            loadView.setStatus(LoadView.Status.network_error);
        }else if (TAG_EVALUATION.equals(tag)){
        }
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        if (data == null){
            loadView.setStatus(LoadView.Status.not_data);
        }else{
            if (TAG_DETAILS.equals(tag)){
                loadView.setStatus(LoadView.Status.successed);
                result = (BikeDetailsResult) data;
                setData(result);
            }else if (TAG_EVALUATION.equals(tag)){
                setCommentDatas((CommentListResult) data);
            }
        }
    }

    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        loadView.setStatus(LoadView.Status.network_error);
        DialogUtil.resetLoginDialog(this,true);
    }
}
