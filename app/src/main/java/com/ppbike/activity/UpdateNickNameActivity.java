package com.ppbike.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

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

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/16.
 */
public class UpdateNickNameActivity extends ParentActivity {
    private Dialog loadingDialog;
    private FormEditText edit_name ;
    private ResponseHandler handelr;
    private final String TAG_INFO = "info";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);
        initView();
        initData();
    }
    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("修改昵称");
        edit_name = (FormEditText) findViewById(R.id.edit_name);

        edit_name.addValidator(new PersonFullNameValidator("请输入有效的昵称"));

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
                if (!edit_name.testValidity()) {
                    return;
                }
                String name = edit_name.getText().toString();
                loadingDialog.show();
                PerfectInfomationRequest bean = new PerfectInfomationRequest();
                bean.setNick(name);
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
        ToastUtil.show(UpdateNickNameActivity.this,(String)data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        setResult(RESULT_OK,getIntent());
        finish();
    }

    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        DialogUtil.resetLoginDialog(this,false);
    }
}
