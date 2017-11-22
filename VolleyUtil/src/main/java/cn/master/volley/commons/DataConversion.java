package cn.master.volley.commons;

import java.util.ArrayList;

/**
 * 数据转换类
 */
public class DataConversion {
	
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
	    'a', 'b', 'c', 'd', 'e', 'f' }; 
	
	/**
	 * 将bytes数组转换为String
	 * @param bytes 数组
	 * @return 如果byte数组为null或长度等于0，则返回null
	 */
	public static String toHexString(byte[] bytes) { 
		if( bytes==null || bytes.length==0 ) {
			return null;
		}
	    StringBuilder sb = new StringBuilder(bytes.length * 2);    
	    for (int i = 0; i < bytes.length; i++) {    
	        sb.append(HEX_DIGITS[(bytes[i] & 0xf0) >>> 4]);    
	        sb.append(HEX_DIGITS[bytes[i] & 0x0f]);    
	    }    
	    return sb.toString();    
	}
	
	
	public static <T>ArrayList<T> toArrayList(T... array){
	    ArrayList<T> list = new ArrayList<T>();
	    for (int i = 0; i < array.length; i++) {
		list.add(array[i]);
	    }
	    return list;
	}
}
