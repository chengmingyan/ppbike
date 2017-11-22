package cn.master.volley.commons;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 数据算法类</br>
 * MD5 SHA 等
 */
public class DataAlgorithmTools {
	
	@SuppressWarnings("unused")
	private static final String TAG = DataAlgorithmTools.class.getSimpleName();
	
	/**
	 * SHA256 算法
	 */
	public static final String ALGORITHM_SHA_256 = "SHA-256";
	/**
	 * MD5算法
	 */
	public static final String ALGORITHM_MD5 = "MD5";
	
	/**
	 * 按指定算法进行编码
	 * @param algorithm 指定算法
	 * @param source 源
	 * @return 如果source为Null，则返回 null
	 * @throws NoSuchAlgorithmException 没有找找指定的算法
	 */
	public static String encode( String algorithm, String source ) throws NoSuchAlgorithmException {
		if( source==null ) {
    		return null;
    	}
		MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] origBytes = source.getBytes(); 
        md.update(origBytes); 
        byte[] digestRes = md.digest(); 
        return DataConversion.toHexString(digestRes);
	}
}
