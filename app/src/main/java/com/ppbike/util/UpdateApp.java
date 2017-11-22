/**
 * 更新app
 * 注意修改filename及urlStr
 */
package com.ppbike.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ppbike.R;
import com.ppbike.bean.AppVersionResult;
import com.ppbike.model.RequestModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.master.util.utils.ToastUtil;
import cn.master.volley.models.response.handler.ResponseHandler;
import cn.master.volley.models.response.listener.OnFailedListener;
import cn.master.volley.models.response.listener.OnSucceedListener;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class UpdateApp implements OnSucceedListener, OnFailedListener {

    private String path = "ppbike/apk/";// 下载的apk在sdcard中的位置
    private String fileName;// 下载后apk的名字
    protected static final int DOWNLOADCOMPLETE = 1;
    protected static final int INSTALAPKFILE = 2;
    /* 下载中 */
    private static final int DOWNLOAD = 3;
    private DownloadManager dm;
    private File file;
    private Activity context;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;

    public UpdateApp(Activity context) {
        this.context = context;
    }

    private Handler updatehandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DOWNLOADCOMPLETE: {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_update_app);
                    TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
                    tv_title.setText("提示");
                    TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
                    tv_content.setText(R.string.update_download_complete_ask);
                    dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            instalApk();
                        }
                    });
                }
                break;
                case INSTALAPKFILE: {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_update_app);
                    TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
                    tv_title.setText("提示");
                    TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
                    tv_content.setText(R.string.update_apk_file_exists);
                    dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            instalApk();
                        }
                    });
                }
                break;
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = updatehandler.obtainMessage();
            msg.what = DOWNLOADCOMPLETE;
            // 这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
            updatehandler.sendMessage(msg);
        }
    };

    /**
     * 判断是否有更新
     *
     * @return
     * @throws IOException
     */
    private void checkUpdate() throws IOException {
        ResponseHandler handler = new ResponseHandler();
        handler.setOnSucceedListener(this);
        handler.setOnFailedListener(this);
        RequestModel.updateVersion(handler, "check_version");
    }

    // 是否显示没有更新的提示信息
    private boolean isShowPromptInfoTheNoUpdate = false;

    /**
     * 设置当没有更新的时候，是否需要提示信息
     */
    public void setIsShowPromptInfoTheNoUpdate(boolean isShow) {
        this.isShowPromptInfoTheNoUpdate = isShow;
    }

    /**
     * 获取下个版本VersionName
     *
     * @return versionName
     */
//	public int getVersion() {
//		try {
//			// 获取packagemanager的实例
//			PackageManager packageManager = context.getPackageManager();
//			// getPackageName()是你当前类的包名，0代表是获取版本信息
//			PackageInfo packInfo = packageManager.getPackageInfo(
//					context.getPackageName(), 0);
//			int versioncode = packInfo.versionCode;
//			return versioncode;
//		} catch (NumberFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return 0;
//	}

    /**
     * 获取版本名
     */
    public String getVersionName() {
        PackageManager packageManager = context.getApplication().getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getApplication().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packInfo == null ? "" : packInfo.versionName;
    }

    public void update() {

        updatehandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    checkUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void instalApk() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 调用系统DownloadManager下载文件
     *
     * @param url
     */
    private void Download(String url) {
        file = new File(Environment.getExternalStorageDirectory(), fileName);
        Log.e("--",
                fileName + "--" + file.getAbsolutePath() + "--"
                        + file.getName() + "--" + file.getPath());
        if (file.exists()) {
            updatehandler.sendEmptyMessage(INSTALAPKFILE);
            return;
        } else {
            dm = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }

        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url.toString()));
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
                | Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        // 设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
                .getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        // 在通知栏中显示
        request.setShowRunningNotification(true);
        request.setVisibleInDownloadsUi(true);
        request.setTitle(fileName);
        // sdcard的目录下的download文件夹
        request.setDestinationInExternalPublicDir(path, fileName);
        request.setTitle(fileName);
        long id = dm.enqueue(request);
        context.registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onFailed(String tag, int resultCode, Object data) {

    }

    @Override
    public void onSucceed(String tag, boolean isCache, Object data) {
        if (data == null)
            return;
        final AppVersionResult bean = (AppVersionResult) data;
        switch (bean.getUp()) {
            case 0:
                if (isShowPromptInfoTheNoUpdate) {
                    ToastUtil.show(context, R.string.tip_this_is_the_latest_version);
                }
                break;
            case 1: {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_update_app);
                TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
                tv_title.setText("发现新版本：" + getVersionName() + " ——> " + bean.getNo());
                TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
                tv_content.setText(bean.getDesc());
                dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String names[] = bean.getUrl().split("/");
                        fileName = names[names.length - 1];
                        Download(bean.getUrl());
                    }
                });
            }
            break;

            case 2: {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_update_app);
                TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
                tv_title.setText("发现新版本：" + getVersionName() + " ——> " + bean.getNo());
                TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
                tv_content.setText(bean.getDesc());
                dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (bean.getUp() == 2) {
                    dialog.setCancelable(false);
                }
            }
            break;
        }

    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog(String url) {
        // 构造软件下载对话框
        mDownloadDialog = new Dialog(context);
        mDownloadDialog.setContentView(R.layout.dialog_update_app_progress);
        mProgress = (ProgressBar) mDownloadDialog.findViewById(R.id.update_progress);
        mDownloadDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownloadDialog.dismiss();
                // 设置取消状态
                cancelUpdate = true;
            }
        });
        // 取消更新
        // 启动新线程下载软件
        new downloadApkThread(url).start();
    }

    /**
     * 下载文件线程
     *
     * @author coolszy
     * @date 2012-4-26
     * @blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread {
        private String apkUrl;

        public downloadApkThread(String url) {
            this.apkUrl = url;
        }

        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    URL url = new URL(apkUrl);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    // 判断文件是否存在
                    if (file.exists()) {
                        file.delete();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        updatehandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            updatehandler.sendEmptyMessage(DOWNLOADCOMPLETE);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    }

    ;
}
