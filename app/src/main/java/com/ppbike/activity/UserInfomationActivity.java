package com.ppbike.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.bean.PerfectInfomationRequest;
import com.ppbike.bean.UserBean;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.view.CircleImageView;
import com.ppbike.view.LoadView;
import com.ppbike.view.LoadingDialog;

import cn.master.util.utils.RequestCodeUtil;
import cn.master.util.utils.ToastUtil;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/16.
 */
public class UserInfomationActivity extends ParentActivity {
    private Dialog loadingDialog;
    private EditText edit_name, edit_number;
    private CircleImageView image_user;
    private TextView tv_nikename,tv_phone;
    private LoadView loadView;
    private ResponseHandler infoHandelr;
    private final String TAG_INFO = "info";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infomation);
        initView();
        initData();
    }
    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_number = (EditText) findViewById(R.id.edit_number);
        edit_name.setFocusable(false);
        edit_number.setFocusable(false);
        tv_nikename = (TextView) findViewById(R.id.tv_nikename);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        image_user = (CircleImageView) findViewById(R.id.image_user);
        image_user.setImageResource(R.mipmap.ic_headimg_xhdpi);

        loadingDialog = new LoadingDialog(this);

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
                        findViewById(R.id.btn_prefectInfomation).setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void initData(){
        infoHandelr = new ResponseHandler(TAG_INFO);
        infoHandelr.setOnFailedListener(this);
        infoHandelr.setOnSucceedListener(this);

        loadView.setStatus(LoadView.Status.loading);
        onRefresh();
    }

    private void onRefresh(){
        UserBean userBean = UserModel.getInstance(this).getUserBean();
        String token = userBean.getToken();
        RequestModel.obtainUserInfomation(infoHandelr,TAG_INFO,token);
        tv_phone.setText(userBean.getPhone());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.btn_prefectInfomation:
            {
                Intent intent = new Intent(this,PerfectInfomationActivity.class);
                startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(PerfectInfomationActivity.class));
            }
                break;
            case R.id.layout_headPhoto:

                break;
            case R.id.layout_nikename:
            {
                Intent intent = new Intent(this,UpdateNickNameActivity.class);
                startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(UpdateNickNameActivity.class));
            }
                break;

            case R.id.layout_password:
            {
                Intent intent = new Intent(this,ModifyPasswordActivity.class);
                startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(ModifyPasswordActivity.class));
            }
                break;
        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        switch (tag) {
            case TAG_INFO:
                if (loadView.getStatus() != LoadView.Status.successed){
                    loadView.setStatus(LoadView.Status.network_error);
                }
                break;
        }
        ToastUtil.show(this,(String)data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        switch (tag){
            case TAG_INFO:
                if (data == null)
                    loadView.setStatus(LoadView.Status.not_data);
                else{
                    loadView.setStatus(LoadView.Status.successed);
                    PerfectInfomationRequest bean = (PerfectInfomationRequest) data;
                    if (!TextUtils.isEmpty(bean.getNick()))
                        tv_nikename.setText(bean.getNick());
                    if (!TextUtils.isEmpty(bean.getName())){
                        edit_name.setText(bean.getName());
                    }
                    if (!TextUtils.isEmpty(bean.getIcard())){
                        edit_number.setText(bean.getIcard());
                    }
                    UserBean userBean = UserModel.getInstance(UserInfomationActivity.this).getUserBean();
                    if (1 == userBean.getDstatus()){
                        findViewById(R.id.btn_prefectInfomation).setVisibility(View.VISIBLE);
                    }
                    tv_phone.setText(userBean.getPhone());
                    UserModel.getInstance(this).savePerfectionInfomation(bean);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeUtil.getInstance().obtainRequestCode(PerfectInfomationActivity.class)){
            if (RESULT_OK == resultCode){
                loadingDialog.show();
                String token = UserModel.getInstance(this).getUserBean().getToken();
                RequestModel.obtainUserInfomation(infoHandelr,TAG_INFO,token);
            }
        }else if (requestCode == RequestCodeUtil.getInstance().obtainRequestCode(UpdateNickNameActivity.class)){
            if (RESULT_OK == resultCode){
                loadingDialog.show();
                String token = UserModel.getInstance(this).getUserBean().getToken();
                RequestModel.obtainUserInfomation(infoHandelr,TAG_INFO,token);
            }
        }else if (resultCode == RESULT_OK){
            onRefresh();
        }
    }

    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        DialogUtil.resetLoginDialog(this,true);
    }
}
