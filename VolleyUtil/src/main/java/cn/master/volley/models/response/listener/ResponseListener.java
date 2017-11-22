package cn.master.volley.models.response.listener;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;

import org.apaches.commons.codec.binary.Base64;

import java.net.URLDecoder;
import java.util.Map;

import cn.master.volley.commons.AES;
import cn.master.volley.commons.JacksonJsonUtil;
import cn.master.volley.models.pojo.Wrapper;
import cn.master.volley.models.response.handler.ResponseHandler;

/**
 * 监听返回信息，统一处理错误信息
 *
 * @param <T> 一个与 {@link Wrapper#getData()} 数据结构匹配的数据实体类，由调用者指定
 */
public class ResponseListener<T> implements IsCacheListener<String>, ErrorListener {

    private static final String TAG = ResponseListener.class.getSimpleName();
    private ResponseHandler mHandler = null;
    private ResolveJson<T> mJsonInterface = null;
    private Class<T> tClass;
    /**
     * 构造方法
     *
     * @param handler
     */
    public ResponseListener(ResponseHandler handler) {
        this.mHandler = handler;
    }
    /**
     * 构造方法
     *
     * @param handler
     */
    public ResponseListener(ResponseHandler handler,Class<T> tClass) {
        this.mHandler = handler;
        this.tClass = tClass;
    }
    /**
     * 构造方法
     *
     * @param handler
     */
    public ResponseListener(ResponseHandler handler, ResolveJson<T> jsonInterface) {
        this.mHandler = handler;
        this.mJsonInterface = jsonInterface;
    }

    @Override
    public void onResponse(final String response) {
        onResponse(false, response);
    }

    @Override
    public void onResponse(final boolean isCache, final String response) {
        if (mHandler == null) {
            return;
        }
        //开启线程用来处理数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataProcessing(isCache, response);
            }
        }).start();
    }

    /**
     * 数据处理（Json解析及数据分发）
     *
     * @param response
     */
    @SuppressWarnings("unchecked")
    private void dataProcessing(boolean isCache, String response) {
        try {
            response = AES.decrypt(Base64.decodeBase64(response),AES.PUBLIC_KEY,null);
            Message msg = new Message();
            Log.e(TAG,"dataProcessing: response="+response);
            msg.obj = response;
            if (tClass != null) {
                T object = JacksonJsonUtil.getObjectMapper().readValue(response, tClass);
                msg.obj = object;
            }
            msg.what = ResponseHandler.SUCCEED;
            //arg1 参数用来存储是否为缓存数据
            msg.arg1 = isCache ? 1 : 0;
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, response);
            mHandler.sendEmptyMessage(ResponseHandler.ERROR);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mHandler == null) {
            return;
        }
        Message msg = new Message();
        msg.what = ResponseHandler.FAIL;
        try{

            Map<String,String> headers = error.networkResponse.headers;
            String res = headers.get("res");
            if (!TextUtils.isEmpty(res)){
                msg.arg1 = Integer.parseInt(headers.get("res"));
                if (msg.arg1 == ResponseHandler.INVALID_USER){
                    msg.what = msg.arg1;
                }
            }else{
                return;
            }
            String content = headers.get("msg");
            if (!TextUtils.isEmpty(content)){
                content = URLDecoder.decode(content,"UTF-8");
            }
            msg.obj = content;
            Log.e(TAG,"errorCode = "+msg.arg1+"\nerrorContent = "+content);
        }catch (Exception e){

        }
        mHandler.sendMessage(msg);
    }

}

