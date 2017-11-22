package com.ppbike.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.andreabaccega.formedittextvalidator.PatternValidator;
import com.andreabaccega.widget.FormEditText;
import com.ppbike.R;
import com.ppbike.bean.UserBean;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.MD5;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/15.
 */
public class ModifyPasswordActivity extends ParentActivity {


    private FormEditText edit_oldPassword, edit_password,edit_password_again;
    private Dialog loadingDialog;
    private ResponseHandler handelr;
    private final String TAG_MODIFY = "modify" ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        initView();
        initData();
    }
    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("修改密码");
        edit_oldPassword = (FormEditText) findViewById(R.id.edit_oldPassword);
        edit_password = (FormEditText) findViewById(R.id.edit_password);
        edit_password_again = (FormEditText) findViewById(R.id.edit_password_again);

        loadingDialog = new LoadingDialog(this);

        edit_oldPassword.addValidator(new PatternValidator("密码必须是6-16字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,16}$")));
        edit_password.addValidator(new PatternValidator("密码必须是6-16字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,16}$")));
        edit_password_again.addValidator(new PatternValidator("密码必须是6-16字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,16}$")));

    }

    private void initData(){
        handelr = new ResponseHandler(TAG_MODIFY);
        handelr.setOnFailedListener(this);
        handelr.setOnSucceedListener(this);
        handelr.setOnNeedLoginListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;
            case R.id.btn_sure: {
                if (!edit_oldPassword.testValidity() || !edit_password.testValidity() || !edit_password_again.testValidity()){
                    return;
                }
                String oldPassword = edit_oldPassword.getText().toString();
                String newPassword = edit_password.getText().toString();
                String passwordAgain = edit_password_again.getText().toString();
                edit_password_again.addValidator(new PatternValidator("两次输入的密码不一致", Pattern.compile(newPassword)));

                if (!edit_password_again.testValidity()){
                    edit_password_again.addValidator(new PatternValidator("密码必须是6-16字母或数字", Pattern.compile("^[0-9a-zA-Z]{6,16}$")));
                    return;
                }
                if (!newPassword.equals(passwordAgain)){
                    edit_password_again.setError("两次输入的密码不一致");
                    return;
                }
                loadingDialog.show();
                String body = null;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("pass", MD5.crypto(oldPassword));
                    jsonObject.put("newpass",MD5.crypto(oldPassword));
                    body = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                UserBean userBean = UserModel.getInstance(this).getUserBean();
                String token = userBean.getToken();
                RequestModel.modifyPassword(handelr, TAG_MODIFY, body,token);
            }
                break;

        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        switch (tag){
            case TAG_MODIFY:
                break;
        }
        ToastUtil.show(this,(String)data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        switch (tag){
            case TAG_MODIFY:
                ToastUtil.show(this,"修改成功");
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
        DialogUtil.resetLoginDialog(this,false);
    }
}
