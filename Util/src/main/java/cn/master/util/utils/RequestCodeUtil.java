package cn.master.util.utils;

import java.util.HashMap;

public class RequestCodeUtil {
	private static int index = 9999;
	public static HashMap<String, Integer> requestcodes = new HashMap<String, Integer>();
	private static final RequestCodeUtil instance = new RequestCodeUtil();

	public static RequestCodeUtil getInstance() {
		return instance;
	}

	/**
	 * 
	 * @param Tclass
	 *            将要跳转的Activity.class
	 * @return
	 */
	public int obtainRequestCode(Class<?> Tclass) {
		return obtainRequestCode(Tclass.getCanonicalName());
	}
	/**
	 * 
	 * @param action
	 *            将要跳转的action
	 * @return
	 */
	public int obtainRequestCode(String action){
		if (requestcodes.get(action) == null) {
			requestcodes.put(action, index);
			index--;
		}
		return requestcodes.get(action);
	}
}
