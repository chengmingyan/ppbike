package com.shelwee.update;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.shelwee.update.dialog.VersionDialog;
import com.shelwee.update.listener.OnUpdateListener;
import com.shelwee.update.pojo.UpdateInfo;
import com.shelwee.update.utils.NetWorkUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

import cn.master.volley.commons.VolleyHelper;
import cn.master.volley.models.response.handler.ResponseHandler;
import cn.master.volley.models.response.listener.OnFailedListener;
import cn.master.volley.models.response.listener.OnSucceedListener;
import cn.master.volley.models.response.listener.ResponseListener;

/**
 * Created by ShelWee on 14-5-8.<br/>
 * Usage:
 * 
 * <pre>
 * UpdateManager updateManager = new UpdateManager.Builder(this)
 * 		.checkUrl(&quot;http://localhost/examples/version.jsp&quot;)
 * 		.isAutoInstall(false)
 * 		.build();
 * updateManager.check();
 * </pre>
 * 
 * @author ShelWee(<a href="http://www.shelwee.com">http://www.shelwee.com</a>)
 * @version 0.1 beta
 */
public class UpdateHelper implements OnSucceedListener, OnFailedListener {

	private Context mContext;
	private String checkUrl;
	private boolean isAutoInstall;
	private boolean isHintVersion;
	private OnUpdateListener updateListener;
	private NotificationManager notificationManager;
	private NotificationCompat.Builder ntfBuilder;

	private static final int UPDATE_NOTIFICATION_PROGRESS = 0x1;
	private static final int COMPLETE_DOWNLOAD_APK = 0x2;
	private static final int DOWNLOAD_NOTIFICATION_ID = 0x3;
	private static final String PATH = Environment
			.getExternalStorageDirectory().getPath();
	private static final String SUFFIX = ".apk";
	private static final String APK_PATH = "APK_PATH";
	private static final String APP_NAME = "APP_NAME";
	private static final String APP_VERSIONNAME = "APP_VERSIONNAME";
	private SharedPreferences preferences_update;
	
	private ProgressDialog pd;
	public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";

	private HashMap<String, String> cache = new HashMap<String, String>();
	
	private Handler myHandler = new Handler();
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_NOTIFICATION_PROGRESS:
				showDownloadNotificationUI((UpdateInfo) msg.obj, msg.arg1);
				break;
			case COMPLETE_DOWNLOAD_APK:

				if (UpdateHelper.this.isAutoInstall) {
					if(pd!=null) pd.dismiss();
					showInstallDialog();
					installApk(Uri.parse("file://" + cache.get(APK_PATH)));
				} else {
					if (ntfBuilder == null) {
						ntfBuilder = new NotificationCompat.Builder(mContext);
					}
					ntfBuilder.setSmallIcon(mContext.getApplicationInfo().icon)
							.setContentTitle(cache.get(APP_NAME))
							.setContentText("下载完成，点击安装").setTicker("任务下载完成");
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(
							Uri.parse("file://" + cache.get(APK_PATH)),
							"application/vnd.android.package-archive");
					PendingIntent pendingIntent = PendingIntent.getActivity(
							mContext, 0, intent, 0);
					ntfBuilder.setContentIntent(pendingIntent);
					if (notificationManager == null) {
						notificationManager = (NotificationManager) mContext
								.getSystemService(Context.NOTIFICATION_SERVICE);
					}
					notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
							ntfBuilder.build());
				}
				break;
			}
		}
	};
	
	/** 带按钮的通知栏点击广播接收 */
	public void initButtonReceiver(){
		ButtonBroadcastReceiver bReceiver = new ButtonBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		mContext.registerReceiver(bReceiver, intentFilter);
	}

	@Override
	public void onFailed(String tag, int resultCode, Object data) {

	}

	@Override
	public void onSucceed(String tag, boolean isCache, Object data) {
		if (data == null)
			return;
		UpdateInfo updateInfo = (UpdateInfo) data;
		if (updateInfo.getUp() > 0) {
			if ( updateInfo.getUp()==2) {
				isAutoInstall = true;
			}
			String versionName = preferences_update.getString(APP_VERSIONNAME,"");
			String apkPath = preferences_update.getString(APK_PATH,"");
			if (versionName.equals(updateInfo.getNo())&&new File(apkPath).exists()){
				cache.put(APK_PATH,apkPath);
				cache.put(APP_NAME,preferences_update.getString(APP_NAME,getAppName()));
				handler.sendEmptyMessage(COMPLETE_DOWNLOAD_APK);
			}else{
				showUpdateUI(updateInfo);
			}
		} else {
			preferences_update.edit().clear();
			if (isHintVersion) {
				Toast.makeText(mContext, "当前已是最新版", Toast.LENGTH_LONG).show();
			}
		}
		if (UpdateHelper.this.updateListener != null) {
			UpdateHelper.this.updateListener.onFinishCheck(updateInfo);
		}
	}

	/**
	 *	 广播监听按钮点击时间 
	 */
	public class ButtonBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(ACTION_BUTTON)){
				//通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
				collapseStatusBar(context);
				myHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						installApk(Uri.parse("file://" + cache.get(APK_PATH)));
					}
				}, 100);
			}
		}
	}
	
	public static void collapseStatusBar(Context context) {
        try {
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;
            if (Build.VERSION.SDK_INT <= 16) {
                collapse = statusBarManager.getClass().getMethod("collapse");
            } else {
                collapse = statusBarManager.getClass().getMethod("collapsePanels");
            }
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
	}
	
	private UpdateHelper(Builder builder) {
		this.mContext = builder.context;
		this.checkUrl = builder.checkUrl;
		this.isAutoInstall = builder.isAutoInstall;
		this.isHintVersion = builder.isHintNewVersion;
		preferences_update = mContext.getSharedPreferences("Updater",
				Context.MODE_PRIVATE);
	}

	/**
	 * 检查app是否有新版本，check之前先Builer所需参数
	 */
	public void check() {
		check(null);
	}

	public void check(OnUpdateListener updateListener) {
		initButtonReceiver();
		if (updateListener != null) {
			this.updateListener = updateListener;
		}
		if (mContext == null) {
			Log.e("NullPointerException", "The context must not be null.");
			return;
		}
		ResponseHandler handler = new ResponseHandler();
		handler.setOnSucceedListener(this);
		handler.setOnFailedListener(this);
		ResponseListener<UpdateInfo> listener = new ResponseListener<>(handler,UpdateInfo.class);
		VolleyHelper.post(null,checkUrl,"103",null,null,listener,listener);
		if (UpdateHelper.this.updateListener != null) {
			UpdateHelper.this.updateListener.onStartCheck();
		}
	}
	
	/**
	 * 2014-10-27新增流量提示框，当网络为数据流量方式时，下载就会弹出此对话框提示
	 */
	private void showInstallDialog(){
		final VersionDialog dialog = new VersionDialog(mContext, "重新安装", "退出应用",VersionDialog.STYLE_INSTALL);
		dialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				installApk(Uri.parse("file://" + cache.get(APK_PATH)));
			}
		});
		dialog.show();
	}
	
	/**
	 * 2014-10-27新增流量提示框，当网络为数据流量方式时，下载就会弹出此对话框提示
	 * @param updateInfo
	 */
	private void showNetDialog(final UpdateInfo updateInfo){

		final VersionDialog dialog;
		if (updateInfo.getUp()==1) {
			dialog = new VersionDialog(mContext, "继续下载", "取消下载",VersionDialog.STYLE_WIFI_NOMAL);
		}else {
			dialog = new VersionDialog(mContext, "继续下载", "退出应用",VersionDialog.STYLE_WIFI_FORCEL);
		}
		dialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				AsyncDownLoad asyncDownLoad = new AsyncDownLoad();
				asyncDownLoad.execute(updateInfo);
			}
		});
		dialog.show();
		
	}

	/**
	 * 弹出提示更新窗口
	 * 
	 * @param updateInfo
	 */
	private void showUpdateUI(final UpdateInfo updateInfo) {
		
		if (updateInfo.getUp()==1) {
			//普通更新
			final VersionDialog dialog = new VersionDialog(mContext, updateInfo.getDesc(),updateInfo.getSize(),"V"+updateInfo.getNo(), "立刻更新", "我再想想",VersionDialog.STYLE_UPDATE_NOMAL);
			dialog.setOnPositiveListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					NetWorkUtils netWorkUtils = new NetWorkUtils(mContext);
					int type = netWorkUtils.getNetType();
					if (type != 1) {
						showNetDialog(updateInfo);
					}else {
						AsyncDownLoad asyncDownLoad = new AsyncDownLoad();
						asyncDownLoad.execute(updateInfo);
					}
				}
			});
			dialog.show();
		}else {
			//强制更新
			final VersionDialog dialog = new VersionDialog(mContext, updateInfo.getDesc(),updateInfo.getSize(),"V"+updateInfo.getNo(), "立刻更新", "退出应用",VersionDialog.STYLE_UPDATE_FORCE);
			dialog.setOnPositiveListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					NetWorkUtils netWorkUtils = new NetWorkUtils(mContext);
					int type = netWorkUtils.getNetType();
					if (type != 1) {
						showNetDialog(updateInfo);
					}else {
						AsyncDownLoad asyncDownLoad = new AsyncDownLoad();
						asyncDownLoad.execute(updateInfo);
					}
				}
			});
			dialog.show();
		}
	}
	
	/**
	 * 通知栏弹出下载提示进度
	 * 
	 * @param updateInfo
	 * @param progress
	 */
	private void showDownloadNotificationUI(UpdateInfo updateInfo,final int progress) {
		if (mContext != null) {
			if (updateInfo.getUp()==1) {
				// 普通更新，通知栏提示
				String contentText = new StringBuffer().append(progress)
						.append("%").toString();
				PendingIntent contentIntent = PendingIntent.getActivity(
						mContext, 0, new Intent(),
						PendingIntent.FLAG_CANCEL_CURRENT);
				if (notificationManager == null) {
					notificationManager = (NotificationManager) mContext
							.getSystemService(Context.NOTIFICATION_SERVICE);
				}
				if (ntfBuilder == null) {
					ntfBuilder = new NotificationCompat.Builder(mContext)
							.setSmallIcon(mContext.getApplicationInfo().icon)
							.setTicker("开始下载...")
							.setContentTitle(getAppName())
							.setContentIntent(contentIntent);
				}
				ntfBuilder.setContentText(contentText);
				ntfBuilder.setProgress(100, progress, false);
				notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,ntfBuilder.build());
			}else {
				// 强制更新，对话框提示
				if (pd==null) {
					pd = new ProgressDialog(mContext);    //进度条对话框  
				}
			    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
			    pd.setMessage("正在下载更新");  
			    pd.setProgress(progress);
			    pd.setCancelable(false);
			    pd.show();
			}
		}
	}
	
	/**
	 * 获取当前app版本
	 * 
	 * @return
	 * @throws android.content.pm.PackageManager.NameNotFoundException
	 */
	private PackageInfo getPackageInfo() {
		PackageInfo pinfo = null;
		if (mContext != null) {
			try {
				pinfo = mContext.getPackageManager().getPackageInfo(
						mContext.getPackageName(), 0);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return pinfo;
	}

	private String getAppName() {
		PackageInfo packageInfo = getPackageInfo();
		if (packageInfo!=null) {
			return packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
		}else {
			return null;
		}
	}

	/**
	 * 异步下载app任务
	 */
	private class AsyncDownLoad extends AsyncTask<UpdateInfo, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(UpdateInfo... params) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(params[0].getUrl());
				HttpResponse response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					Log.e("Exception", "APK路径出错，请检查服务端配置接口。");
					return false;
				} else {
					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();
					long total = entity.getContentLength();
					String apkName =  params[0].getNo() + SUFFIX;
					cache.put(APP_NAME, getAppName()+"V"+params[0].getNo());
					cache.put(APK_PATH, PATH + File.separator + params[0].getNo() + File.separator + apkName);
					File savePath = new File(PATH + File.separator
							+ params[0].getNo());
					if (!savePath.exists())
						savePath.mkdirs();
					File apkFile = new File(savePath, apkName);
//					if (apkFile.exists()) {
//						return true;
//					}
					if (apkFile.exists()) {
						apkFile.delete();
					}
					FileOutputStream fos = new FileOutputStream(apkFile);
					byte[] buf = new byte[1024];
					int count = 0;
					int length = -1;
					while ((length = inputStream.read(buf)) != -1) {
						fos.write(buf, 0, length);
						count += length;
						int progress = (int) ((count / (float) total) * 100);
						if (progress % 5 == 0) {
							handler.obtainMessage(UPDATE_NOTIFICATION_PROGRESS,
									progress, -1, params[0]).sendToTarget();
						}
						if (UpdateHelper.this.updateListener != null) {
							UpdateHelper.this.updateListener
									.onDownloading(progress);
						}
					}
					inputStream.close();
					fos.close();
				}
				SharedPreferences.Editor editor = preferences_update.edit();
				editor.putString(APP_VERSIONNAME,params[0].getNo());
				editor.putString(APP_NAME,cache.get(APP_NAME));
				editor.putString(APK_PATH,cache.get(APK_PATH));
				editor.commit();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean flag) {
			if (flag) {
				handler.obtainMessage(COMPLETE_DOWNLOAD_APK).sendToTarget();
				if (UpdateHelper.this.updateListener != null) {
					UpdateHelper.this.updateListener.onFinshDownload();
				}
			} else {
				Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
				Log.e("Error", "下载失败。");
			}
		}
	}

	public static class Builder {
		private Context context;
		private String checkUrl;
		private boolean isAutoInstall = true;
		private boolean isHintNewVersion = false;

		public Builder(Context ctx) {
			this.context = ctx;
		}

		/**
		 * 检查是否有新版本App的URL接口路径
		 * 
		 * @param checkUrl
		 * @return
		 */
		public Builder checkUrl(String checkUrl) {
			this.checkUrl = checkUrl;
			return this;
		}
		/**
		 * 是否需要自动安装, 不设置默认自动安装
		 * 
		 * @param isAuto
		 *            true下载完成后自动安装，false下载完成后需在通知栏手动点击安装
		 * @return
		 */
		public Builder isAutoInstall(boolean isAuto) {
			this.isAutoInstall = isAuto;
			return this;
		}
		
		/**
		 * 当没有新版本时，是否Toast提示
		 * @param isHint 
		 * @return true提示，false不提示
		 */
		public Builder isHintNewVersion(boolean isHint){
			this.isHintNewVersion = isHint;
			return this;
		}
		
		/**
		 * 构造UpdateManager对象
		 * 
		 * @return
		 */
		public UpdateHelper build() {
			return new UpdateHelper(this);
		}
	}

	private void installApk(Uri data) {
		if (mContext != null) {
			UpdateHelper.this.updateListener.onInstallApk();
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(data, "application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(i);
			if (notificationManager != null) {
				notificationManager.cancel(DOWNLOAD_NOTIFICATION_ID);
			}
		} else {
			Log.e("NullPointerException", "The context must not be null.");
		}

	}
}