package com.ppbike.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.activity.LoginActivity;
import com.ppbike.activity.MessageDetailsActivity;
import com.ppbike.adapter.MessageListAdapter;
import com.ppbike.bean.MessageListRequest;
import com.ppbike.bean.MessageListResult;
import com.ppbike.bean.MessageResult;
import com.ppbike.model.RequestModel;
import com.ppbike.model.UserModel;
import com.ppbike.util.DialogUtil;
import com.ppbike.util.TimeUtil;
import com.ppbike.view.LoadView;
import com.ppbike.view.YListView;

import java.io.IOException;
import java.util.ArrayList;

import cn.master.util.utils.RequestCodeUtil;
import cn.master.util.utils.ToastUtil;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.response.handler.ResponseHandler;


public class MessageListFragment extends ParentFragment implements AdapterView.OnItemClickListener{
    public static final String INTENT_TYPE = "type";
    private YListView listView;
    private LoadView loadView;
    private MessageListAdapter adapter;
    private ResponseHandler handler;
    private final String TAG_SYSTEM = "system", TAG_ACTIVITY = "activity";
    private ArrayList<MessageResult> datas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.include_ylist, null);
        return contextView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        initData();
        if (getUserVisibleHint()){
            initData();
        }
    }

    private void initView(View view) {
        listView = (YListView) view.findViewById(R.id.listView);
        adapter = new MessageListAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setRefreshTimeListener(new YListView.RefreshTimeListener() {
            @Override
            public void onRefreshTime(TextView view) {
                long time = getContext().getSharedPreferences("time", Context.MODE_PRIVATE).getLong(MessageListFragment.this.getClass().getSimpleName()+getArguments().getInt(INTENT_TYPE,1),0l);
                view.setText(TimeUtil.obtainLastUpdateTimeStatuString(time));
            }
        });

        loadView = (LoadView) view.findViewById(R.id.loadView);
        loadView.setOnReLoadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadView.setStatus(LoadView.Status.loading);
                initData();
            }
        });
        loadView.setStatus(LoadView.Status.loading);
    }

    private void initData() {
        int type = getArguments().getInt("type", 1);
        String tag = TAG_SYSTEM;
        if (type != 1) {
            tag = TAG_ACTIVITY;
        }
        String token = UserModel.getInstance(getActivity()).getUserBean().getToken();
        if (type == 1 && TextUtils.isEmpty(token)){
            DialogUtil.showLoginDialog(getActivity(),"您需要登录，\n才能查看更多信息",true);
            return;
        }
        handler = new ResponseHandler(tag);
        handler.setOnFailedListener(this);
        handler.setOnSucceedListener(this);
        handler.setOnNeedLoginListener(this);
        long msgid = 0;
        if (datas != null) {
            for (MessageResult result : datas) {
                if (result.getMsgid() > msgid) {
                    msgid = result.getMsgid();
                }
            }
        }
        MessageListRequest request = new MessageListRequest();
        request.setMsgid(msgid);
        request.setType(type);
        try {
            String body = JacksonJsonUtil.toJson(request);
            RequestModel.obtainMessageList(handler, tag, body, token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getAdapter().getItem(position);
        if (object instanceof MessageResult) {
            MessageResult message = (MessageResult) object;
            Intent intent = new Intent(getActivity(), MessageDetailsActivity.class);
            intent.putExtra(MessageDetailsActivity.INTENT_OBJECT,message);
            startActivity(intent);

        }
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {
        loadView.setStatus(LoadView.Status.network_error);
        ToastUtil.show(getActivity(), (String) data);
    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (data == null) {
            loadView.setStatus(LoadView.Status.not_data);
            return;
        }
        MessageListResult request = (MessageListResult) data;
        datas = request.getMsg();
        adapter.setDatas(datas);
        adapter.notifyDataSetChanged();
        if (datas.size() == 0) {
            loadView.setStatus(LoadView.Status.not_data);
        } else {
            loadView.setStatus(LoadView.Status.successed);
        }
        getContext().getSharedPreferences("time", Context.MODE_PRIVATE).edit().putLong(MessageListFragment.this.getClass().getSimpleName()+getArguments().getInt(INTENT_TYPE,1),System.currentTimeMillis()).commit();

    }

    @Override
    public void onNeedLogin(String tag) {
        DialogUtil.resetLoginDialog(getActivity(), true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeUtil.getInstance().obtainRequestCode(LoginActivity.class)) {
            if (resultCode == Activity.RESULT_OK) {
                initData();
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}