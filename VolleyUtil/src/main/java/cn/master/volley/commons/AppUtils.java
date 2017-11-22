package cn.master.volley.commons;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/*
 * 跟App相关的辅助类
 * 新增的与项目相关的方法请继承该类
 * 
 * */
public class AppUtils {

	private AppUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");

	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [获取应用程序版本名称信息]
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取应用的版本号
	 * @param context
	 * @return
	 * 应用当前的版本号
	 */
	public static int getVersionCode(Context context){
		int versionCode = 0;
		try {
			ApplicationInfo applicationInfo = context.getApplicationInfo();
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(applicationInfo.packageName, 0);
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	
	/**
	 * 获取应用的包名
	 * @param context
	 * @return
	 * 应用的包名
	 */
	public static String getPackageName(Context context){
		return context.getApplicationInfo().packageName;
	}
	/**
	 * 获取Manifest里面meta-data配置的值
	 * <p>2014-11-14</p>
	 * @return
	 * @author RANDY.ZHANG
	 */
	public static String getApplicationMetaData(Context context,String name,String defaultValue) {
		String value = "";
		if (defaultValue != null)
			value = defaultValue;
		try {
			value = context.getPackageManager().getApplicationInfo(getPackageName(context),
					PackageManager.GET_META_DATA).metaData.getString(name);
			Log.e("getApplicationMetaData",""+value);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			if (defaultValue != null)
				value = defaultValue;
		}
		return value;
	}
}
