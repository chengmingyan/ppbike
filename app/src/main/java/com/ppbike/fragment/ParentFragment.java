package com.ppbike.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import cn.master.volley.models.response.listener.OnFailedListener;
import cn.master.volley.models.response.listener.OnNeedLoginListener;
import cn.master.volley.models.response.listener.OnSucceedListener;

/**
 * Created by chengmingyan on 16/7/8.
 */
public abstract class ParentFragment extends Fragment implements OnNeedLoginListener,OnFailedListener,OnSucceedListener,View.OnClickListener {
}
