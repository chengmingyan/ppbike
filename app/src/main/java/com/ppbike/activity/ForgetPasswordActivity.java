package com.ppbike.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.bean.RegisterRequest;
import com.ppbike.bean.VerificationCodeRequest;
import com.ppbike.model.RequestModel;
import com.ppbike.view.LoadingDialog;
import com.ppbike.view.countdownview.CountdownView;

import java.io.IOException;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/15.
 */
public class ForgetPasswordActivity extends ParentActivity {

    private EditText edit_phone,edit_code ;
    private Button btn_code;
    private Dialog loadingDialog;
    private ResponseHandler handelr;
    private final String TAG_CODE = "code" ;
    private CountdownView remainTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initView();
        initData();
    }
    private void initView(){
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("注册");
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_code = (EditText) findViewById(R.id.edit_code);
        btn_code = (Button) findViewById(R.id.btn_code);

        loadingDialog = new LoadingDialog(this);

        remainTime = (CountdownView) findViewById(R.id.remainTime);
        remainTime.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                remainTime.setVisibility(View.GONE);
                btn_code.setVisibility(View.VISIBLE);
            }
        });

    }

    private void initData(){
        handelr = new ResponseHandler(TAG_CODE);
        handelr.setOnFailedListener(this);
        handelr.setOnSucceedListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.btn_code: {
                String phone = edit_phone.getText().toString();
                if (TextUtils.isEmpty(phone)){
                    ToastUtil.show(this,"请输入正确的手机号");
                    return;
                }
                remainTime.setVisibility(View.VISIBLE);
                btn_code.setVisibility(View.GONE);
                long time = (long) 30 * 1000 ;
                remainTime.start(time);
                VerificationCodeRequest bean = new VerificationCodeRequest();
                bean.setMobile(edit_phone.getText().toString());
                bean.setType(VerificationCodeRequest.TYPE_MODIFY_PASSWORD);
                String body = null;
                try {
                    body = JacksonJsonUtil.toJson(bean);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                RequestModel.obtainVerificationCode(handelr, TAG_CODE, body);
            }
                break;
            case R.id.btn_forgetPassword: {
                String phone = edit_phone.getText().toString();
                String code = edit_code.getText().toString();
                if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code) ){
                    ToastUtil.show(this,"请完善信息");
                    return;
                }

                RegisterRequest bean = new RegisterRequest();
                bean.setMobile(phone);
                bean.setCode(code);

                Intent intent = new Intent(this,ModifyPasswordActivity.class);
                startActivity(intent);
                finish();
            }
                break;

        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
        loadingDialog.dismiss();
        switch (tag){
            case TAG_CODE:
                remainTime.stop();
                remainTime.setVisibility(View.GONE);
                btn_code.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
        loadingDialog.dismiss();
        switch (tag){
            case TAG_CODE:

                break;
        }
    }

    @Override
    public void onNeedLogin(String tag) {

    }
}
