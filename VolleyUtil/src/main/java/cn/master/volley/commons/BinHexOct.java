// Copyright (C) 2012-2014 UUZZ All rights reserved
package cn.master.volley.commons;

/** 
 * 类 名: BinHexOct<br/>
 * 描 述: 进制转换<br/>
 * 作 者: 王雷<br/>
 * 创 建： 2014-2-13<br/>
 *
 * 历 史: (版本) 作者 时间 注释 <br/>
 */
public class BinHexOct {
	
	/**
	 * 常量数据0xff
	 */
	private static final int C_0XFF = 0xff;
	
	/**
	 * 常量数据0x10
	 */
	private static final int C_0X10 = 0x10;
	
	/**
	 * 常量数据16
	 */
	private static final int C_SIXTEEN = 16;
	
	/**
	 * 描 述：二进制字节转化为十六进制<br/>
	 * 作 者：王雷<br/>
	 * 历 史: (版本) 作者 时间 注释 <br/>
	 * @param bin 字节数据
	 * @return 十六进制字符数据
	 */
	public static String asHex(byte[] bin) {
		StringBuffer bfHex = new StringBuffer(bin.length * 2);
		int i;
		for (i = 0; i < bin.length; i++) {
			if (((int) bin[i] & C_0XFF) < C_0X10) {
				bfHex.append("0");
			}
			bfHex.append(Long.toString((int) bin[i] & C_0XFF, C_SIXTEEN));
		}
		return bfHex.toString();
	}

	/**
	 * 描 述：十六进制转为二制进字节<br/>
	 * 作 者：王雷<br/>
	 * 历 史: (版本) 作者 时间 注释 <br/>
	 * @param hex 十六进制字符数据
	 * @return 字节数据
	 */
	public static byte[] asBin(String hex) {
		if (hex.length() < 1) {
			return null;
		}
		byte[] bin = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length() / 2; i++) {
			int high = Integer.parseInt(hex.substring(i * 2, i * 2 + 1), C_SIXTEEN);
			int low = Integer.parseInt(hex.substring(i * 2 + 1, i * 2 + 2), C_SIXTEEN);
			bin[i] = (byte) (high * C_SIXTEEN + low);
		}
		return bin;
	}
}
