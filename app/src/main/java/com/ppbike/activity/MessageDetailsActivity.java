package com.ppbike.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.bean.MessageResult;
import com.ppbike.util.TimeUtil;

/**
 * Created by chengmingyan on 16/6/23.
 */
public class MessageDetailsActivity extends ParentActivity {

    public static final String INTENT_OBJECT = "messageResult";
    private TextView tv_time,tv_type,tv_messageTitle,tv_content;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        initView();
        initData();
    }

    private void initView(){
        findViewById(R.id.icon_back).setOnClickListener(this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("通知内容");
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_messageTitle = (TextView) findViewById(R.id.tv_messageTitle);
        tv_content = (TextView) findViewById(R.id.tv_content);
    }
    private void initData(){
        MessageResult message = (MessageResult)getIntent().getSerializableExtra(INTENT_OBJECT);
        tv_type.setText(MessageResult.TYPE_VALUES[message.getType() - 1]);
        tv_messageTitle.setText(message.getTitle());
        tv_content.setText(message.getContent());
        tv_time.setText(TimeUtil.utc2Local(message.getCreateTime(),TimeUtil.LOCAL_DETAIL_TIME_PATTERN_STRING));
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {

    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_back:
                finish();
                break;

        }
    }

    @Override
    public void onNeedLogin(String tag) {

    }
}
