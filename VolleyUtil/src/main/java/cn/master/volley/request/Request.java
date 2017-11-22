package cn.master.volley.request;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apaches.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.master.volley.commons.AES;
import cn.master.volley.commons.AppUtils;
import cn.master.volley.commons.MD5;
import cn.master.volley.models.response.listener.IsCacheListener;

/**
 * Sketch Project Studio
 * Created by Angga on 27/04/2016 12.05.
 */
public class Request extends com.android.volley.Request<String> {
    private final IsCacheListener<String> mlistener;
    private final Context context;
    private final String cmd;
    private String paramJson;
    private final String token;

    /**
     * Http response status is 304 , is true
     */
    private boolean notModified = false;

    private final String boundary = "apiclient-" + System.currentTimeMillis();

    public Request(Context context, int method, String url, String cmd, String paramJson, String token, IsCacheListener<String> listener, Response.ErrorListener errorListener) {
        super(method, url,errorListener);
        this.mlistener = listener;
        this.context = context;
        this.cmd = cmd;
        this.paramJson = paramJson;
        this.token = token;

        if (TextUtils.isEmpty(paramJson)){
            this.paramJson = "{}";
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        this.notModified = response.notModified;
        Map<String,String> headers = response.headers;
        String res = headers.get("res");
        if ("0".equals(res)){
            String parsed = null;
            try {
                parsed = new String(response.data, getParamsEncoding());
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        }
        return Response.error(new VolleyError(response));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        Map<String, String> header = new HashMap<String, String>();
        //设备型号
        header.put("ios", "2");
        //渠道号
        header.put("cid", "android");
        //版本号
        header.put("ver", AppUtils.getVersionName(context));
        if (!TextUtils.isEmpty(token))
            header.put("token", token);
        header.put("cmd", cmd);
        try {
            header.put("sign", MD5.crypto(new String(Base64.encodeBase64(AES.encrypt(paramJson, AES.PUBLIC_KEY, null)))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return header;
    }

    /**
     * 正确数据处理
     * @param response The parsed response returned by
     */
    @Override
    protected void deliverResponse(String response) {
        mlistener.onResponse(notModified, response);
    }

    /**
     * 异常数据处理
     * @param error Error details
     */
    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            bos.write(Base64.encodeBase64(AES.encrypt(paramJson, "817fed95f9e16bed8b945b77b4fd046a", null)));
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}