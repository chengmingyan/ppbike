package com.ppbike.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ppbike.R;
import com.ppbike.bean.FeedbackRequest;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.view.LoadingDialog;

import java.io.IOException;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * Created by chengmingyan on 16/6/27.
 */
public class FeedbackFragment extends ParentFragment{
    private Dialog loadingDialog;
    private ResponseHandler handler;
    private FormEditText edit_feedback;
    private TextView tv_count;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback,null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        initData();
    }

    private void initData(){
        handler = new ResponseHandler();
        handler.setOnSucceedListener(this);
        handler.setOnFailedListener(this);
        handler.setOnNeedLoginListener(this);
    }

    private void initView(View view) {
        view.findViewById(R.id.icon_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SlidingFragmentActivity)getActivity()).getSlidingMenu().toggle();
            }
        });
        view.findViewById(R.id.btn_submit).setOnClickListener(this);
        edit_feedback = (FormEditText)view.findViewById(R.id.edit_feedback);
        tv_count = (TextView)view.findViewById(R.id.tv_count);
        edit_feedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_count.setText(s.length() + "/300");
            }
        });
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        if (loadingDialog != null){
            loadingDialog.dismiss();
        }
        ToastUtil.show(getActivity(),(String)data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (loadingDialog != null){
            loadingDialog.dismiss();
        }
        ToastUtil.show(getActivity(),"感谢您的建议！");
        edit_feedback.setText("");
    }

    @Override
    public void onNeedLogin(String tag) {
        if (loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
        DialogUtil.showLoginDialog(getActivity(),"请您先登录，\n以便我们及时回复您的反馈",false);
    }

    @Override
    public void onClick(View v) {
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.show();
        FeedbackRequest request = new FeedbackRequest();
        request.setView(edit_feedback.getText().toString().trim());
        try {
            String json = JacksonJsonUtil.toJson(request);
            RequestModel.feedback(handler,json, UserModel.getInstance(getActivity()).getUserBean().getToken());
        } catch (IOException e) {
            e.printStackTrace();
            loadingDialog.dismiss();
        }
    }

}
