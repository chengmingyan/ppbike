package com.ppbike.util;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.ppbike.activity.LoginActivity;
import com.ppbike.activity.MainActivity;
import com.ppbike.model.UserModel;
import com.ppbike.view.SweetAlertDialog.SweetAlertDialog;

import cn.master.util.utils.RequestCodeUtil;

/**
 * Created by chengmingyan on 16/7/5.
 */
public class DialogUtil {

    public static SweetAlertDialog resetLoginDialog(final FragmentActivity context, final boolean finishActivityByCancel) {
        SweetAlertDialog dialog = new SweetAlertDialog(context);
        dialog.setTitleText("提示");
        dialog.setContentText("您的账号已在其他设备上登录，是否重新登录？");
        dialog.setConfirmText("重新登录");
        dialog.setCancelText("取消");
        dialog.setCancelable(false);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(LoginActivity.class));
            }
        });
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                UserModel.getInstance(context).loginout();
                if (finishActivityByCancel) {
                    context.finish();
                }
            }
        });
        dialog.show();
        return dialog;
    }

    public static void showLoginDialog(final FragmentActivity context, String content, final boolean finishActivityByCancel) {
        SweetAlertDialog dialog = new SweetAlertDialog(context);
        dialog.setTitleText("提示");
        dialog.setContentText(content);
        dialog.setConfirmText("登录");
        dialog.setCancelText("取消");
        dialog.setCancelable(false);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(LoginActivity.class));
            }
        });
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                UserModel.getInstance(context).loginout();
                if (finishActivityByCancel) {
                    context.finish();
                }
            }
        });
        dialog.show();
    }

    public static void showLoginDialogCanReturnHome(final FragmentActivity context, final String content, final boolean finishActivityByCancel) {
        SweetAlertDialog dialog = new SweetAlertDialog(context);
        dialog.setTitleText("提示");
        dialog.setContentText(content);
        dialog.setConfirmText("登录");
        dialog.setCancelText("回到首页");
        dialog.setCancelable(false);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(LoginActivity.class));
            }
        });
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                UserModel.getInstance(context).loginout();
                if (finishActivityByCancel) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(MainActivity.INTENT_PAGE,MainActivity.PAGE_HOME);
                    context.startActivity(intent);
                }
            }
        });
        dialog.show();
    }
    public static void showLoginDialogCanReturnHome(final Fragment context, final String content, final boolean finishActivityByCancel) {
        SweetAlertDialog dialog = new SweetAlertDialog(context.getActivity());
        dialog.setTitleText("提示");
        dialog.setContentText(content);
        dialog.setConfirmText("登录");
        dialog.setCancelText("回到首页");
        dialog.setCancelable(false);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                Intent intent = new Intent(context.getActivity(), LoginActivity.class);
                context.startActivityForResult(intent, RequestCodeUtil.getInstance().obtainRequestCode(LoginActivity.class));
            }
        });
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismiss();
                UserModel.getInstance(context.getActivity()).loginout();
                if (finishActivityByCancel) {
                    if (context.getActivity() instanceof MainActivity){
                        MainActivity mainActivity = (MainActivity) context.getActivity();
                        mainActivity.setShowPage(MainActivity.PAGE_HOME,true);
                    }
                }
            }
        });
        dialog.show();
    }
}
