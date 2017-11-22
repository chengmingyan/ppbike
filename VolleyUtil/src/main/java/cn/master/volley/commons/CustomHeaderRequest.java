package cn.master.volley.commons;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.master.volley.models.response.listener.IsCacheListener;


/**
 * 自定义请求头的Request
 */
public class CustomHeaderRequest extends StringRequest {
	
	private static final String TAG = CustomHeaderRequest.class.getSimpleName();
	
	private final IsCacheListener<String> mlistener;
	private final Context context;

	/**
	 * Http response status is 304 , is true
	 */
	private boolean notModified = false;

	public CustomHeaderRequest(int method, String url,
			IsCacheListener<String> listener, ErrorListener errorListener,Context context) {
		super(method, url, null, errorListener);
		this.mlistener = listener;
		this.context = context;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		this.notModified = response.notModified ;

		String parsed = null;
        try {
    		parsed = new String(response.data, getParamsEncoding());
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
	}



	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
			Map<String, String> headers = new HashMap<String, String>();
//			Map<String, String> apiVerifyHeader = HttpHeaderHelper.generateAPIVerifyHeader();
//			Map<String, String> verifyUpdateHeader = generateVerifyUpdateHeader();
//			Map<String, String> deviceInfoHeader = HttpHeaderHelper.generateDeviceInfoHeader(context);
//
//			headers.putAll(apiVerifyHeader);
//			headers.putAll(verifyUpdateHeader);
//			headers.putAll(deviceInfoHeader);

		Map<String, String> header = new HashMap<String, String>();
		//设备型号
		header.put("ios", "andriod");
		//渠道号
		header.put("cid", "");
		//版本号
		header.put("ver", AppUtils.getVersionName(context));
		header.put("cmd","100");

			return header;
	}

	/**
	 * 生成验证更新的Header
	 * @return 验证更新的header
	 */
	private Map<String, String> generateVerifyUpdateHeader() {
		String eTag = VolleyHelper.getETagByCache(getUrl());
		Map<String, String> header = new HashMap<String, String>();
		//匹配标记，用做更新数据
		header.put("If-None-Match",eTag==null?"":eTag );
		return header;
	}

	@Override
	protected void deliverResponse(String response) {
		mlistener.onResponse(notModified, response);
	}
	
}
