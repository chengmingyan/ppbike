package cn.master.volley.models.response.listener;

/**
 * Created by 一搏 on 2016/1/29.
 */

public interface OnUploadingListener {
    /**
     * @param tag      处理标记
     * @param progress 进度
     */
    void onUploading(String tag, int progress);
}

