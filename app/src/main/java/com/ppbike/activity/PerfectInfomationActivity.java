package com.ppbike.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.andreabaccega.formedittextvalidator.PatternValidator;
import com.andreabaccega.formedittextvalidator.PersonFullNameValidator;
import com.andreabaccega.widget.FormEditText;
import com.ppbike.R;
import com.ppbike.bean.PerfectInfomationRequest;
import com.ppbike.bean.UserBean;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.view.LoadingDialog;

import java.io.IOException;
import java.util.regex.Pattern;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/16.
 */
public class PerfectInfomationActivity extends ParentActivity {
    private Dialog loadingDialog;
    private FormEditText edit_name, edit_number;
    private ResponseHandler handelr;
    private final String TAG_INFO = "info";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfect_infomation);
        initView();
        initData();
    }
    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("完善实名信息");
        edit_name = (FormEditText) findViewById(R.id.edit_name);
        edit_number = (FormEditText) findViewById(R.id.edit_number);

        edit_name.addValidator(new PersonFullNameValidator("请输入有效的姓名"));
        edit_number.addValidator(new PatternValidator("请输入有效的证件号码", Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$")));

        loadingDialog = new LoadingDialog(this);

    }

    private void initData(){
        handelr = new ResponseHandler(TAG_INFO);
        handelr.setOnFailedListener(this);
        handelr.setOnSucceedListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.btn_submit:
                if (!edit_name.testValidity() || !edit_number.testValidity()) {
                    return;
                }
                String name = edit_name.getText().toString();
                String number = edit_number.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(number)){
                    ToastUtil.show(this,"请完善信息");
                    return;
                }
                loadingDialog.show();
                PerfectInfomationRequest bean = new PerfectInfomationRequest();
                bean.setName(name);
                bean.setIcard(number);
                String body = null;
                try {
                    body = JacksonJsonUtil.toJson(bean);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UserBean userBean = UserModel.getInstance(this).getUserBean();
                String token = userBean.getToken();
                RequestModel.updateUserInfomation(handelr, TAG_INFO, body,token);
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
        String name = edit_name.getText().toString();
        String number = edit_number.getText().toString();
        PerfectInfomationRequest bean = new PerfectInfomationRequest();
        bean.setName(name);
        bean.setIcard(number);
        UserModel.getInstance(this).savePerfectionInfomation(bean);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        DialogUtil.resetLoginDialog(this,false);
    }
}
