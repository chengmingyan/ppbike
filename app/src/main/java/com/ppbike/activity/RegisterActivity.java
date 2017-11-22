package com.ppbike.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.andreabaccega.formedittextvalidator.PatternValidator;
import com.andreabaccega.widget.FormEditText;
import com.ppbike.R;
import com.ppbike.bean.RegisterRequest;
import com.ppbike.bean.VerificationCodeRequest;
import com.ppbike.model.RequestModel;
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
public class RegisterActivity extends ParentActivity {

    private FormEditText edit_phone,edit_code,edit_password,edit_password_again;
    private EditText edit_ycode;
    private Button btn_code,btn_register,btn_agreement;
    private CheckBox checkBox_agree;
    private Dialog loadingDialog;
    private CountdownView remainTime;
    private ResponseHandler codeHandelr,registerHandler;
    private final String TAG_CODE = "code",TAG_REGISTER = "register";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initData();
    }
    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("注册");
        edit_phone = (FormEditText) findViewById(R.id.edit_phone);
        edit_code = (FormEditText) findViewById(R.id.edit_code);
        edit_password = (FormEditText) findViewById(R.id.edit_password);
        edit_password_again = (FormEditText) findViewById(R.id.edit_password_again);
        edit_ycode = (EditText) findViewById(R.id.edit_ycode);
        btn_code = (Button) findViewById(R.id.btn_code);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_agreement = (Button) findViewById(R.id.btn_agreement);
        checkBox_agree = (CheckBox) findViewById(R.id.checkBox_agree);

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
        edit_password.addValidator(new PatternValidator("密码必须是6-12字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,12}$")));
        edit_password_again.addValidator(new PatternValidator("密码必须是6-12字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,12}$")));

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
                bean.setType(VerificationCodeRequest.TYPE_REGISTER);
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
                    edit_password_again.addValidator(new PatternValidator("密码必须是6-12字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,12}$")));
                    return;
                }

                if (!checkBox_agree.isChecked()){
                    ToastUtil.show(this,"请勾选协议");
                    return;
                }

                loadingDialog.show();
                RegisterRequest bean = new RegisterRequest();
                bean.setMobile(phone);
                bean.setCode(code);
                bean.setPass(MD5.crypto(password));
                bean.setYcode(edit_ycode.getText().toString().trim());
                String body = null;
                try {
                    body = JacksonJsonUtil.toJson(bean);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                RequestModel.register(registerHandler, TAG_REGISTER, body);
            }
                break;
            case R.id.btn_agreement:

                break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
        loadingDialog.dismiss();
        switch (tag){
            case TAG_CODE:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        remainTime.stop();
                        remainTime.setVisibility(View.GONE);
                        btn_code.setVisibility(View.VISIBLE);
                    }
                },1000);
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
