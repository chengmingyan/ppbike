package cn.master.volley.models.response.listener;

/**
 * Created by chengmingyan on 16/6/17.
 */

import com.android.volley.Response;

/** Callback interface for delivering parsed responses. */
public interface IsCacheListener<T> extends Response.Listener<T>{
    /** Called when a response is received. */
    /**
     * @param isCache 是否为缓存数据
     * @param response 数据
     */
    void onResponse(boolean isCache, T response);
}