package com.ppbike.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.ppbike.R;
import com.ppbike.bean.AliPayResult;
import com.ppbike.bean.CreateOrderResult;
import com.ppbike.bean.PayMethod;
import com.ppbike.bean.PrepaidRequest;
import com.ppbike.bean.PrepaidResult;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.view.BottomDialog.BottomDialog;
import com.ppbike.view.BottomDialog.OnDialogItemClickListener;
import com.ppbike.view.LoadingDialog;

import java.io.IOException;
import java.util.ArrayList;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/22.
 */
public class PayActivity extends ParentActivity {
    public static final String INTENT_RESULT = "result";
    private TextView tv_totalPrice,tv_payMothed;
    private CreateOrderResult result;
    private LoadingDialog loadingDialog;
    private PrepaidRequest request = new PrepaidRequest();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
        result = ((CreateOrderResult)getIntent().getSerializableExtra(INTENT_RESULT));
        tv_totalPrice.setText("￥"+result.getAmount()+"元");
        request.setOrderId(result.getOrderId());
        PayMethod payMethod = result.getTypeList().get(0);
        tv_payMothed.setText(payMethod.getDesc());
    }

    private void initView(){
        findViewById(R.id.icon_close).setOnClickListener(this);
        tv_payMothed = (TextView) findViewById(R.id.tv_payMothed);
        tv_totalPrice = (TextView) findViewById(R.id.tv_totalPrice);

        loadingDialog = new LoadingDialog(this);
    }

    private void onRefresh(){
        loadingDialog.show();
        ResponseHandler handler = new ResponseHandler();
        handler.setOnNeedLoginListener(this);
        handler.setOnSucceedListener(this);
        handler.setOnFailedListener(this);
        String body = null;
        try {
            String token  = UserModel.getInstance(this).getUserBean().getToken();
            body = JacksonJsonUtil.toJson(request);
            RequestModel.obtainPayInfo(handler,body,token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_close:
                finish();
                break;
            case R.id.btn_pay:
                onRefresh();
                break;
            case R.id.layout_payMothed:
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0;i < result.getTypeList().size();i++){
                    list.add(result.getTypeList().get(i).getDesc());
                }
                BottomDialog.showBottomDialog(this,list, new OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(int position, View buttonView, Dialog dialog) {
                        PayMethod payMethod = result.getTypeList().get(position);
                        tv_payMothed.setText(payMethod.getDesc());
                        request.setType(payMethod.getType());
                    }
                });
                break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        ToastUtil.show(this,(String)data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        PrepaidResult result = (PrepaidResult) data;
        if (result.getType() == 1){
            pay_zhifubao(result.getData().getInfo());
        }
        finish();
    }

    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        DialogUtil.resetLoginDialog(this,false);
    }
    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    public void pay_zhifubao(final String info) {

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
                // 调用支付接口
                String result = alipay.pay(info,true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 支付宝支付逻辑
     */
    public static final int SDK_PAY_FLAG = 1;

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    AliPayResult resultObj = new AliPayResult((String) msg.obj);
                    String resultStatus = resultObj.resultStatus;

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        ToastUtil.show(PayActivity.this,"支付成功");
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”
                        // 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtil.show(PayActivity.this,"支付成功");
                            finish();
                        } else {
                            ToastUtil.show(PayActivity.this,"支付失败");
                        }
                    }
                    break;
                }

                default:
                    break;
            }
        }
    };
}
