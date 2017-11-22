package cn.master.volley.models.response.listener;

/**
 * Created by 一搏 on 2016/1/29.
 */
/**
 * 回调接口，操作成功的监听，需设置监听器。
 */
public interface OnSucceedListener {
    /**
     * @param tag     处理标记
     * @param isCache 数据是否来自缓存
     * @param data    数据
     */
    void onSucceed(String tag, boolean isCache, Object data);
}