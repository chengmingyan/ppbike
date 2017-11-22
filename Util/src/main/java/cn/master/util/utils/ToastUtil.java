package cn.master.util.utils;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

/**
 * Created by 一搏 on 2015/11/22.
 */
public class ToastUtil {
    private static TipsToast tipsToast;
    private static TipsToast voteToast;

    /**
     * 自定义taost
     *
     * @param context
     * @param iconResId
     *            图片
     * @param msgResId
     */
    public static void showTips(Context context, int iconResId, int msgResId) {
        showTips(context, iconResId, context.getResources().getString(msgResId));
    }

    public static void showTips(Context context, int iconResId, String tips) {
        if (tipsToast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                tipsToast.cancel();
            }
        }
        if (tipsToast == null){

            tipsToast = TipsToast.makeText(context,
                    tips, TipsToast.LENGTH_SHORT);
        }
        tipsToast.show();
        if (iconResId > 0) {
            tipsToast.setIcon(iconResId);
        }
        tipsToast.setText(tips);
    }


    private static Toast toast;
    public static int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static int LENGTH_LONG = Toast.LENGTH_LONG;

    /**
     * 显示提示信息
     * @param context
     * @param resId        消息资源的ID
     */
    public static void show(Context context, int resId) {
        show(context,resId, ToastUtil.LENGTH_SHORT);
    }

    /**
     * 显示提示信息
     * @param resId        消息资源的ID
     * @param duration    显示的时长
     * @param context
     */
    public static void show(Context context,int resId, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, resId, duration);
        } else {
            toast.setText(resId);
            toast.setDuration(duration);
        }
        toast.show();
    }

    /**
     * 显示提示信息Toast.show(
     * @param context
     * @param text        消息文本
     */
    public static Toast show(Context context, CharSequence text) {
       return show(context,text,  ToastUtil.LENGTH_SHORT);
    }

    /**
     * 显示提示信息
     * @param text        消息文本
     * @param context
     * @param duration    显示的时长
     */
    public static Toast show(Context context,CharSequence text,  int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, text, duration);
        } else {
            toast.setText(text);
            toast.setDuration(duration);
        }
        toast.show();
        return toast;
    }

    /**
     * 取消（撤销）当前的提示信息
     */
    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
