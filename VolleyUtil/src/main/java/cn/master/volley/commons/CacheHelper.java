package cn.master.volley.commons;

import android.util.Log;

import cn.master.volley.models.pojo.Wrapper;
import cn.master.volley.models.response.listener.ResolveJson;

/**
 * 读取缓存数据的辅助类
 */
public class CacheHelper {
	
	private static final String TAG = CacheHelper.class.getSimpleName();
	
	/**
	 * 获取缓存数据
	 * @param url
	 * @param resolveJson Json 数据转换对象
	 */
	public static <T> T getCache(String url, ResolveJson<T> resolveJson){
		if (url==null) {
			return null;
		}
		String cache = VolleyHelper.getDataByCache(url);
		if( cache==null )
			return null;
		T data = null;
		try {
			Wrapper<T> wrapper = resolveJson.resolve(cache);
			if( wrapper!=null && wrapper.isSuccess()) {
				data = wrapper.getData();
			}
		} catch (Exception e) {
			Log.e(TAG, "Cache processing failed. " + Log.getStackTraceString(new Throwable()) + e.getMessage());
		}
		return data;
	}
}
