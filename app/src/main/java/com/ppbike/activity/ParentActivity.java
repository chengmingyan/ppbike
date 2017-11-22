package com.ppbike.activity;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import cn.master.volley.models.response.listener.OnFailedListener;
import cn.master.volley.models.response.listener.OnNeedLoginListener;
import cn.master.volley.models.response.listener.OnSucceedListener;

/**
 * Created by chengmingyan on 16/6/15.
 */
public abstract class ParentActivity extends FragmentActivity implements View.OnClickListener,OnSucceedListener,OnFailedListener,OnNeedLoginListener{
}
