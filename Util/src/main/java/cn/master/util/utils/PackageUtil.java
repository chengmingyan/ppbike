/**
 * 
 */ 
package cn.master.util.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** 
 * @author 程明炎 E-mail: 345021109@qq.com
 * @version 创建时间：2014年7月4日 上午9:43:48 
 * 类说明 
 */

/**
 * @author sxx
 *
 */
public class PackageUtil {
	public static int getApplicationVersionCode(Context context){
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
	public static String getApplicationVersionName(Context context){
		String versionName = null;
		try {
			ApplicationInfo applicationInfo = context.getApplicationInfo();
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(applicationInfo.packageName, 0);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	public static String getApplicationPackageName(Context context){
		return context.getApplicationInfo().packageName;
	}

	public static boolean isMIUI(){
		return Build.MANUFACTURER.equals("Xiaomi");
	}
}
 