package cn.master.volley.models.response.listener;

/**
 * Created by 一搏 on 2016/1/29.
 */

/**
 * 回调接口，操作失败的监听，需设置监听器。
 */
public interface OnFailedListener {
    /**
     * 回调方法
     *  @param tag        请求标记
     * @param resultCode 如果不为0，则该值为服务器返回的失败状态码
     * @param data {@see Wrapper2}
     */
    void onFailed(String tag, int resultCode, Object data);
}
