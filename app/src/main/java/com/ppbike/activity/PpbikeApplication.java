package com.ppbike.activity;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.volley.VolleyUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.ppbike.util.MyVolley;

import java.io.InputStream;

import cn.master.volley.commons.VolleyHelper;

/**
 * Created by chengmingyan on 16/6/15.
 */
public class PpbikeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyHelper.init(this);
        MyVolley.init(this);
        Glide.get(this).register(GlideUrl.class, InputStream.class,
                new VolleyUrlLoader.Factory(VolleyHelper.getRequestQueue(this)));
    }
}
