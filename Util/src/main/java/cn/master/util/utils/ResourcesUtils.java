package cn.master.util.utils;

import android.content.Context;

/**
 * Android 资源工具类
 */
public class ResourcesUtils {
	/**
	 * 根据资源名获取资源内容
	 *
	 * @param context
	 * @param resName 资源名称
	 * @return 如果该资源不存在，返回null
	 */
	public static String getString(Context context, String resName) {
		int resId = context.getResources().getIdentifier("status_"+resName, "string",
				context.getApplicationInfo().packageName);
		if( resId==0 )
			return null;
		return context.getString(resId);
	}
}
