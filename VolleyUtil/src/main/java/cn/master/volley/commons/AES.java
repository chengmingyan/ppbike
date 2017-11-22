// Copyright (C) 2012-2013 UUZZ All rights reserved
package cn.master.volley.commons;

import android.text.TextUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 类 名: AES<br/>
 * 描 述: AES加解密类<br/>
 * 作 者: wanglei<br/>
 * 创 建： 2013-6-25<br/>
 *
 * 历 史: (版本) 作者 时间 注释
 */
public class AES {
	/** 公钥 */
	public static final String PUBLIC_KEY = "817fed95f9e16bed8b945b77b4fd046a";
	
	/** 加密算法 */
	public static final String ALGORITHM = "AES";
	
	/** ECB加密模式 */
	public static final String PATTERN_ECB = "ECB";
	
	/** CBC加密模式 */
	public static final String PATTERN_CBC = "CBC";
	
	/** NoPadding填充方式 */
	public static final String PADDING_NO = "NoPadding";
	
	/** PKCS5Padding填充方式 */
	public static final String PADDING_PKCS5 = "PKCS5Padding";
	
	/** 128 */
	private static final int ONE_TWO_EIGHT = 128;

	/**
	 * 描 述： 生成加密密钥<br/>
	 * 作 者：李长跃<br/>
	 * 历 史: (版本) 作者 时间 注释 <br/>
	 * 
	 * @return 加密密钥
	 */
	public static String getKey() {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
			kgen.init(ONE_TWO_EIGHT); // 192 and 256 bits may not be available
			SecretKey skey = kgen.generateKey();
			return BinHexOct.asHex(skey.getEncoded());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 描 述：加密<br/>
	 * 作 者：费玖玲<br/>
	 * 历 史: (版本) 作者 时间 注释 <br/>
	 * @param original 原始数据
	 * @param key 密钥
	 * @param pattern 加密模式
	 * @return 密文数据
	 * @throws Exception
	 */
	public static byte[] encrypt(String original, String key, String pattern) throws Exception {
		if (TextUtils.isEmpty(original)) {
			return null;
		}
		Cipher cipher = null;
		SecretKeySpec skeySpec = new SecretKeySpec(BinHexOct.asBin(key), ALGORITHM);
		if (PATTERN_CBC.equals(pattern)) {
			cipher = Cipher.getInstance(ALGORITHM + "/" + PATTERN_CBC + "/" + PADDING_PKCS5);
			IvParameterSpec iv = new IvParameterSpec(BinHexOct.asBin(key));// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		} else {
			cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		}
		return cipher.doFinal(original.getBytes("utf-8"));
	}
	
	/**
	 * 描 述：解密<br/>
	 * 作 者：费玖玲<br/>
	 * 历 史: (版本) 作者 时间 注释 <br/>
	 * @param encrypted 密文数据
	 * @param key 密钥
	 * @param pattern 加密模式
	 * @return 明文数据
	 * @throws Exception
	 */
	public static String decrypt(byte[] encrypted, String key, String pattern) throws Exception {
		if (encrypted == null || encrypted.length == 0) {
			return null;
		}
		Cipher cipher = null;
		SecretKeySpec skeySpec = new SecretKeySpec(BinHexOct.asBin(key), ALGORITHM);
		if (PATTERN_CBC.equals(pattern)) {
			cipher = Cipher.getInstance(ALGORITHM + "/" + PATTERN_CBC + "/" + PADDING_PKCS5);
			IvParameterSpec iv = new IvParameterSpec(BinHexOct.asBin(key));// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		} else {
			cipher = Cipher.getInstance(ALGORITHM + "/" + PATTERN_ECB + "/" + PADDING_NO);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		}
		byte[] byteDecrypted = cipher.doFinal(encrypted);
		return new String(byteDecrypted, "utf-8").trim();
	}
}