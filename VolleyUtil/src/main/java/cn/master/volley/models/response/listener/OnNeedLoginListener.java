package cn.master.volley.models.response.listener;

/**
 * Created by 一搏 on 2016/1/29.
 */

/**
 * 回调接口，对用户Token无效或已过期的监听，需设置监听器。
 */
public interface OnNeedLoginListener {
    void onNeedLogin(String tag);
}
