package cn.master.volley.models.response.handler;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.lang.ref.SoftReference;

import cn.master.volley.models.response.listener.OnFailedListener;
import cn.master.volley.models.response.listener.OnNeedLoginListener;
import cn.master.volley.models.response.listener.OnSucceedListener;
import cn.master.volley.models.response.listener.OnUploadingListener;

/**
 * 网络响应处理类，建议每个网络请求单独创建一个{@link ResponseHandler}}对象
 */
public class ResponseHandler extends Handler {

    private String mTag = null;

    public ResponseHandler() {

    }

    /**
     * @param tag 添加处理标记，当回调 方法时，用于区分不同的处理逻辑
     */
    public ResponseHandler(String tag) {
        this.mTag = tag;
    }

    /**
     * 成功
     */
    public static final int SUCCEED = 0;
    /**
     * 上传中
     */
    public static final int UPLOADING = 0x0001;
    /**
     * 失败
     */
    public static final int FAIL = 0x0002;
    /**
     * 错误
     */
    public static final int ERROR = 0x0003;
    /**
     * 无效的用户
     */
    public static final int INVALID_USER = 200004;

    protected SoftReference<OnSucceedListener> onSucceedListenerSR = null;
    protected SoftReference<OnFailedListener> onFailedListenerSR = null;
    protected SoftReference<OnNeedLoginListener> onNeedLoginListenerSR = null;
    protected SoftReference<OnUploadingListener> onUploadingListenerSR = null;
    protected SoftReference<Dialog> needCloseTheDialogSR = null;


    /**
     * 设置（网络请求后）操作成功的监听器
     *
     * @param listener
     */
    public void setOnSucceedListener(OnSucceedListener listener) {
        onSucceedListenerSR = new SoftReference<OnSucceedListener>(listener);
    }

    /**
     * 设置（网络请求后）操作失败的监听器
     *
     * @param listener
     */
    public void setOnFailedListener(OnFailedListener listener) {
        onFailedListenerSR = new SoftReference<OnFailedListener>(listener);
    }

    /**
     * 设置（网络请求后）用户已过期或无效时的监听器
     *
     * @param listener
     */
    public void setOnNeedLoginListener(OnNeedLoginListener listener) {
        onNeedLoginListenerSR = new SoftReference<OnNeedLoginListener>(listener);
    }

    public void setOnUploadingListener(OnUploadingListener listener) {
        onUploadingListenerSR = new SoftReference<>(listener);
    }

    /**
     * 设置一个当网络请求结束后，需要关闭的对话框
     * （注意：由于可能出现网络请求结束时，Activity被finish掉了的情况，
     * Activity中需要在onDestroy方法中，手动关闭该Dialog，避免先distroy Activity，后dismiss Dialog）
     */
    public void setOnNeedCloseTheDialog(Dialog dialog) {
        this.needCloseTheDialogSR = new SoftReference<Dialog>(dialog);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SUCCEED: {
                OnSucceedListener succeedListener = onSucceedListenerSR == null ? null : onSucceedListenerSR.get();
                if (succeedListener == null) {
                    break;
                }
                if (succeedListener instanceof Fragment) {
                    Fragment fragment = (Fragment) succeedListener;
                    if (!fragment.isAdded()) {
                        break;
                    }
                } else if (succeedListener instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) succeedListener;
                    if (activity.isFinishing()) {
                        break;
                    }
                } else if (succeedListener instanceof Activity) {
                    Activity activity = (Activity) succeedListener;
                    if (activity.isFinishing()) {
                        break;
                    }
                }
                succeedListener.onSucceed(mTag, msg.arg1 == 0 ? false : true, msg.obj);
                break;
            }
            case INVALID_USER: {
                OnNeedLoginListener needLoginListener = onNeedLoginListenerSR == null ? null : onNeedLoginListenerSR.get();
                if (needLoginListener == null) {
                    this.removeMessages(INVALID_USER);
                    Message msga = new Message();
                    msga.copyFrom(msg);
                    msga.what = FAIL;
                    this.sendMessage(msga);
                    break;
                }
                if (needLoginListener instanceof Fragment) {
                    Fragment fragment = (Fragment) needLoginListener;
                    if (!fragment.isAdded()) {
                        break;
                    }
                } else if (needLoginListener instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) needLoginListener;
                    if (activity.isFinishing()) {
                        break;
                    }
                } else if (needLoginListener instanceof Activity) {
                    Activity activity = (Activity) needLoginListener;
                    if (activity.isFinishing()) {
                        break;
                    }
                }
                needLoginListener.onNeedLogin(mTag);
                break;
            }

            case UPLOADING: {
                OnUploadingListener needLoginListener = onUploadingListenerSR == null ? null : onUploadingListenerSR.get();
                if (needLoginListener == null) {
                    break;
                }
                if (needLoginListener instanceof Fragment) {
                    Fragment fragment = (Fragment) needLoginListener;
                    if (!fragment.isAdded()) {
                        break;
                    }
                } else if (needLoginListener instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) needLoginListener;
                    if (activity.isFinishing()) {
                        break;
                    }
                } else if (needLoginListener instanceof Activity) {
                    Activity activity = (Activity) needLoginListener;
                    if (activity.isFinishing()) {
                        break;
                    }
                }
                needLoginListener.onUploading(mTag, msg.arg1);
                break;
            }

            default:
                OnFailedListener dataErrorListener = onFailedListenerSR == null ? null : onFailedListenerSR.get();
                if (dataErrorListener == null) {
                    break;
                }
                if (dataErrorListener instanceof Fragment) {
                    Fragment fragment = (Fragment) dataErrorListener;
                    if (!fragment.isAdded()) {
                        break;
                    }
                } else if (dataErrorListener instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) dataErrorListener;
                    if (activity.isFinishing()) {
                        break;
                    }
                } else if (dataErrorListener instanceof Activity) {
                    Activity activity = (Activity) dataErrorListener;
                    if (activity.isFinishing()) {
                        break;
                    }
                }
                dataErrorListener.onFailed(mTag, msg.arg1, msg.obj);
                break;
        }
        closeDialog();
    }

    /**
     * 关闭指定的 Dialog
     */
    private void closeDialog() {
        Dialog dialog = needCloseTheDialogSR == null ? null : needCloseTheDialogSR.get();
        if (dialog != null && dialog.getContext() != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
