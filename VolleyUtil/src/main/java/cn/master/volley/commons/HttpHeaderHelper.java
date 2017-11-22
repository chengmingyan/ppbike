package cn.master.volley.commons;

import android.content.Context;
import android.os.Build;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaderHelper {

	/**
	 * 生成API验证的Header
	 * @return header信息
	 */
	public static Map<String, String> generateAPIVerifyHeader() {
		//算法MD5(SHA256(TimeStamp+PrivateKey)+TimeStamp)
		String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
		String publicKey = "817fed95f9e16bed8b945b77b4fd046a";
		String signature = null;
		try {
			String sha = DataAlgorithmTools.encode(DataAlgorithmTools.ALGORITHM_SHA_256, timeStamp+publicKey);
			signature = DataAlgorithmTools.encode(DataAlgorithmTools.ALGORITHM_MD5, sha+timeStamp);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		Map<String, String> header = new HashMap<>();
		//摘要
		header.put("sign", signature==null?"":signature);
		//时间戳
		header.put("TimeStamp", timeStamp);
		return header;
	}
	
	/**
	 * 生成设备信息的Header
	 * @return
	 * @param context
	 */
	public static Map<String, String> generateDeviceInfoHeader(Context context){
		Map<String, String> header = new HashMap<String, String>();
		//设备型号
		header.put("Device", Build.MODEL);
		header.put("ios", "andriod");
		//渠道号
		header.put("cid", "");
		//系统版本
		header.put("System", Build.VERSION.RELEASE);
		//版本号
		header.put("ver", AppUtils.getVersionName(context));
		return header;
	}
}
