package com.ppbike.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.andreabaccega.formedittextvalidator.PatternValidator;
import com.andreabaccega.widget.FormEditText;
import com.ppbike.R;
import com.ppbike.bean.LoginRequest;
import com.ppbike.bean.LoginResult;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.view.LoadingDialog;

import java.io.IOException;
import java.util.regex.Pattern;

import cn.master.util.utils.RequestCodeUtil;
import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.commons.MD5;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/15.
 */
public class LoginActivity extends ParentActivity {
    public final static String INTENT_PHONE = "phone";
    private FormEditText edit_phone, edit_password;
    private Dialog loadingDialog;
    private ResponseHandler loginHandelr;
    private final String TAG_LOGIN = "login";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.icon_back).setOnClickListener(this);
        findViewById(R.id.btn_show_password).setOnClickListener(this);
        findViewById(R.id.btn_hide_password).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("登录");
        edit_phone = (FormEditText) findViewById(R.id.edit_phone);
        edit_password = (FormEditText) findViewById(R.id.edit_password);
        // 隐藏密码
        edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        loadingDialog = new LoadingDialog(this);
        edit_phone.addValidator(new PatternValidator("请输入有效的手机号码", Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|70)\\d{8}$")));
        edit_password.addValidator(new PatternValidator("密码必须是6-16字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,16}$")));

    }

    private void initData() {
        loginHandelr = new ResponseHandler(TAG_LOGIN);
        loginHandelr.setOnFailedListener(this);
        loginHandelr.setOnSucceedListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String phone = getIntent().getStringExtra(INTENT_PHONE);
        if (!TextUtils.isEmpty(phone)) {
            edit_phone.setText(phone);
            edit_password.setFocusable(true);
            edit_password.setFocusableInTouchMode(true);
            edit_password.requestFocus();
            edit_password.requestFocusFromTouch();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.btn_hide_password: {
                // 显示密码
                edit_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                findViewById(R.id.btn_hide_password).setVisibility(View.GONE);
                findViewById(R.id.btn_show_password).setVisibility(View.VISIBLE);
                int length = edit_password.length();
                edit_password.setSelection(length);
            }
            break;
            case R.id.btn_show_password: {
                // 隐藏密码
                edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                findViewById(R.id.btn_hide_password).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_show_password).setVisibility(View.GONE);
                int length = edit_password.length();
                edit_password.setSelection(length);
            }
            break;
            case R.id.btn_login: {
                if (!edit_phone.testValidity() || !edit_password.testValidity()) {
                    return;
                }
                String phone = edit_phone.getText().toString();
                String password = edit_password.getText().toString();
                if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                    ToastUtil.show(this, "请完善信息");
                    return;
                }
                loadingDialog.show();
                LoginRequest bean = new LoginRequest();
                bean.setMobile(phone);
                bean.setPassword(MD5.crypto(password));
                String body = null;
                try {
                    body = JacksonJsonUtil.toJson(bean);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                RequestModel.login(loginHandelr, TAG_LOGIN, body);
            }
            break;
            case R.id.btn_register: {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(RegisterActivity.class));
            }
            break;
            case R.id.btn_forgetPassword: {
                Intent intent = new Intent(this, SetPasswordActivity.class);
                startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(SetPasswordActivity.class));

            }
            break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        ToastUtil.show(this, (String) data);
    }

    /**
     * 响应消息：
     * {
     * "token":"afadfadfasdfasdf",
     * "dstatus":1        1未完善信息，2已经完善信息
     * }
     *
     * @param tag     处理标记
     * @param isCache 数据是否来自缓存
     * @param data    数据
     */
    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        LoginResult bean = (LoginResult) data;
        UserModel.getInstance(this).saveLogin(bean,edit_phone.getText().toString());
        Intent intent = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeUtil.getInstance().obtainRequestCode(RegisterActivity.class)){
            if (resultCode == RESULT_OK){
                String phone = data.getStringExtra(INTENT_PHONE);
                edit_phone.setText(phone);
                edit_password.setFocusable(true);
                edit_password.setFocusableInTouchMode(true);
                edit_password.requestFocus();
            }
        }else if (requestCode == RequestCodeUtil.getInstance().obtainRequestCode(SetPasswordActivity.class)){
            if (resultCode == RESULT_OK){
                String phone = data.getStringExtra(INTENT_PHONE);
                edit_phone.setText(phone);
                edit_password.setFocusable(true);
                edit_password.setFocusableInTouchMode(true);
                edit_password.requestFocus();
            }
        }
    }

    @Override
    public void onNeedLogin(String tag) {

    }
}
