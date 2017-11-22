package com.ppbike.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andreabaccega.formedittextvalidator.PatternValidator;
import com.andreabaccega.widget.FormEditText;
import com.ppbike.R;
import com.ppbike.bean.RegisterRequest;
import com.ppbike.bean.VerificationCodeRequest;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.view.LoadingDialog;
import com.ppbike.view.countdownview.CountdownView;

import java.io.IOException;
import java.util.regex.Pattern;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.commons.MD5;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/15.
 */
public class SetPasswordActivity extends ParentActivity {

    private FormEditText edit_phone,edit_code,edit_password,edit_password_again;
    private Button btn_code,btn_register;
    private Dialog loadingDialog;
    private CountdownView remainTime;
    private ResponseHandler codeHandelr,registerHandler;
    private final String TAG_CODE = "code",TAG_REGISTER = "register";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        initView();
        initData();
    }
    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("设置新密码");
        edit_phone = (FormEditText) findViewById(R.id.edit_phone);
        edit_code = (FormEditText) findViewById(R.id.edit_code);
        edit_password = (FormEditText) findViewById(R.id.edit_password);
        edit_password_again = (FormEditText) findViewById(R.id.edit_password_again);
        btn_code = (Button) findViewById(R.id.btn_code);
        btn_register = (Button) findViewById(R.id.btn_register);

        loadingDialog = new LoadingDialog(this);

        remainTime = (CountdownView) findViewById(R.id.remainTime);
        remainTime.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                remainTime.setVisibility(View.GONE);
                btn_code.setVisibility(View.VISIBLE);
            }
        });

        edit_phone.addValidator(new PatternValidator("请输入有效的手机号码", Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|70)\\d{8}$")));
        edit_password.addValidator(new PatternValidator("密码必须是6-16字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,16}$")));
        edit_password_again.addValidator(new PatternValidator("密码必须是6-16字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,16}$")));
        btn_register.setText("提交");
    }

    private void initData(){
        codeHandelr = new ResponseHandler(TAG_CODE);
        codeHandelr.setOnFailedListener(this);
        codeHandelr.setOnSucceedListener(this);

        registerHandler = new ResponseHandler(TAG_REGISTER);
        registerHandler.setOnFailedListener(this);
        registerHandler.setOnSucceedListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.btn_code: {
                if (!edit_phone.testValidity()){
                    return;
                }
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
                bean.setType(VerificationCodeRequest.TYPE_SET_PASSWORD);
                String body = null;
                try {
                    body = JacksonJsonUtil.toJson(bean);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                RequestModel.obtainVerificationCode(codeHandelr, TAG_CODE, body);
            }
                break;
            case R.id.btn_register: {
                if (!edit_phone.testValidity() || !edit_code.testValidity() || !edit_password.testValidity() || !edit_password_again.testValidity()){
                    return;
                }
                String phone = edit_phone.getText().toString();
                String code = edit_code.getText().toString();
                String password = edit_password.getText().toString();
                edit_password_again.addValidator(new PatternValidator("两次输入的密码不一致", Pattern.compile(password)));

                if (!edit_password_again.testValidity()){
                    edit_password_again.addValidator(new PatternValidator("密码必须是6-16字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,16}$")));
                    return;
                }

                loadingDialog.show();
                RegisterRequest bean = new RegisterRequest();
                bean.setMobile(phone);
                bean.setCode(code);
                bean.setPass(MD5.crypto(password));
                String body = null;
                try {
                    body = JacksonJsonUtil.toJson(bean);
                    String token = UserModel.getInstance(this).getUserBean().getToken();
                    RequestModel.forgetPassword(registerHandler, TAG_REGISTER, body,token);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
            case TAG_REGISTER:

                break;
        }
        ToastUtil.show(this,(String)data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
        loadingDialog.dismiss();
        switch (tag){
            case TAG_CODE:
                edit_code.setFocusable(true);
                edit_code.setFocusableInTouchMode(true);
                edit_code.requestFocus();
                break;
            case TAG_REGISTER:
                getIntent().putExtra(LoginActivity.INTENT_PHONE,edit_phone.getText().toString());
                setResult(RESULT_OK,getIntent());
                finish();
                break;
        }
    }

    @Override
    public void onNeedLogin(String tag) {

    }
}
