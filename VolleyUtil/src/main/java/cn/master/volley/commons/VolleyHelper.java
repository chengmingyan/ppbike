package cn.master.volley.commons;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;

import org.apache.http.protocol.HTTP;

import java.io.File;

import cn.master.util.FileSizeUtil;
import cn.master.volley.models.response.listener.IsCacheListener;
import cn.master.volley.request.Request;

/**
 * Volley 网络请求类
 */
public class VolleyHelper {

    public static final String TAG = VolleyHelper.class.getSimpleName();
    /**
     * 网络超时15秒
     */
    private static final int SOCKET_TIMEOUT = 15000;
    /**
     * 如果请求失败后可以重试，最大重试次数
     */
    private static final int MAX_RETRIES = 5;
    /**
     * 数据的最大缓存（10M）
     */
    private static final int DATA_MAX_CACHE_SIZE = 10 * 1024 * 1024;
    /**
     * 图片L1 （内存）的最大缓存（10M）
     */
    private static final int IMAGE_L1_MAX_CACHE_SIZE = 10 * 1024 * 1024;
    /**
     * 图片L2（磁盘）的最大缓存（20M）
     */
    private static final int IMAGE_L2_MAX_CACHE_SIZE = 20 * 1024 * 1024;
    /**
     * 默认的磁盘缓存文件夹名
     */
    private static final String DEFAULT_CACHE_DIR = "volley";
    /**
     * 数据缓存的文件夹名
     */
    private static final String DATA_CACHE_DIR = "volley_data";
    /**
     * 图片缓存的文件夹名
     */
    private static final String IMAGE_CACHE_DIR = "volley_img";
    /**
     * 应用中数据的请求队列
     */
    private static RequestQueue REQUEST_QUEUE_DATA = null;
    /**
     * 图片加载对象
     */
    private static ImageLoader IMAGE_LOADER = null;
    private static Context context;

    public static void init(Context context) {
        if (REQUEST_QUEUE_DATA == null) {
            REQUEST_QUEUE_DATA = newRequestQueue(context);
        }
        if (IMAGE_LOADER == null) {
            IMAGE_LOADER = newImageLoader(context);
        }
        VolleyHelper.context = context;

    }

    public static RequestQueue getRequestQueue(Context context) {
        if (REQUEST_QUEUE_DATA == null) {
            init(context);
        }
        return REQUEST_QUEUE_DATA;
    }

    /**
     * 创建一个数据请求的队列
     *
     * @param context
     */
    private static RequestQueue newRequestQueue(Context context) {
        File cacheDir = new File(context.getCacheDir(), DATA_CACHE_DIR);
        DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir, DATA_MAX_CACHE_SIZE);
        RequestQueue requestQueue = newRequestQueue(diskBasedCache, null, context);
        return requestQueue;
    }

    /**
     * 创建一个ImageLoader对象
     *
     * @param context
     */
    private static ImageLoader newImageLoader(Context context) {
        BitmapLruCache bitmapLruCache = new BitmapLruCache(IMAGE_L1_MAX_CACHE_SIZE);
        File cacheDir = new File(context.getCacheDir(), IMAGE_CACHE_DIR);
        DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir, IMAGE_L2_MAX_CACHE_SIZE);
        RequestQueue requestQueue = newRequestQueue(diskBasedCache, null, context);
        return new ImageLoader(requestQueue, bitmapLruCache);
    }

    /**
     * 可添加 HTTP header，添加TAG， 优先级普通，失败后不重试
     *
     * @param url           请求地址
     * @param tag           给请求添加标签，用于取消请求
     * @param cmd           请求头的cmd
     * @param paramJson     请求参数,json类型
     * @param listener      请求成功监听器
     * @param errorListener 请求失败监听器
     */
    public static void post(Object tag,
                            String url,
                            final String cmd,
                            final String paramJson,
                            String token,
                            IsCacheListener<String> listener,
                            ErrorListener errorListener) {
        Log.e(TAG, "requestPost: url = " + url);
        //创建一个请求
        Request request = new Request(context, Method.POST, url, cmd, paramJson,token, listener, errorListener);

        //为请求添加Tag
        if (tag != null)
            request.setTag(tag);
        //设置重试策略
//        if (isRetry) {
//            request.setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        } else {
        request.setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        }
        REQUEST_QUEUE_DATA.add(request);
    }


    /**
     * 取消请求
     *
     * @param tag 请求的标签
     */
    public static void cancelRequest(Object tag) {
        REQUEST_QUEUE_DATA.cancelAll(tag);
    }

    /**
     * 根据URL获取缓存数据
     *
     * @param url 缓存使用的url，最终通过url的hashcode为CacheKey进行缓存
     * @return cache data 如果不存在或获取失败将返回 null
     */
    public static String getDataByCache(String url) {
        Entry entry = getCache(url);
        if (entry == null) {
            return null;
        }
        String data = null;
        try {
//			data = new String1(entry.data,HttpHeaderParser.parseCharset(entry.responseHeaders));
            data = new String(entry.data, HTTP.UTF_8);
        } catch (Exception e) {


        }
        return data;
    }

    /**
     * 根据URL获取缓存中的 ETag
     *
     * @param url 缓存使用的url，最终通过url的hashcode为CacheKey进行缓存
     * @return 如果不存在返回Null
     */
    public static String getETagByCache(String url) {
        Entry entry = getCache(url);
        if (entry == null) {
            return null;
        }
        return entry.etag;
    }

    /**
     * 根据URL获取缓存
     *
     * @param url 缓存使用的url，最终通过url的hashcode为CacheKey进行缓存
     * @return 如果不存在，将返回null
     */
    private static Entry getCache(String url) {
        Cache cache = REQUEST_QUEUE_DATA.getCache();
        return cache.get(url);
    }

    /**
     * 删除指定缓存
     *
     * @param url
     */
    public static void removeCache(String url) {
        REQUEST_QUEUE_DATA.getCache().remove(url);
    }

    /**
     * 清除所有缓存
     */
    public static void removeAllCache(Context c) {
        deleteFilesByDirectory(c.getCacheDir());
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(c.getExternalCacheDir());
        }
    }

    public static String getCacheSize(Context c) {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                return FileSizeUtil.getAutoFileOrFilesSize(c.getCacheDir().getAbsolutePath(), c.getExternalCacheDir().getAbsolutePath());
            }
            return FileSizeUtil.getAutoFileOrFilesSize(c.getCacheDir().getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (item.isDirectory()) {
                    deleteFilesByDirectory(item);
                } else {
                    System.out.println(item.getAbsoluteFile() + "=====" + item.delete());
                }
            }
        }
    }

    /**
     * 获取ImageLoader对象，该对象可重用
     *
     * @return
     */
    public static ImageLoader getImageLoader() {
        return IMAGE_LOADER;
    }

    /**
     * 图片的 L1 缓存类
     */
    public static class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {

        public BitmapLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }
    }

    /**
     * 创建一个队列
     *
     * @param cache   磁盘缓存，如果为null，则会使用默认的磁盘缓存对象，大小为5M
     * @param stack
     * @param context
     * @return
     */
    public static RequestQueue newRequestQueue(DiskBasedCache cache, HttpStack stack, Context context) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetwork(stack);
        if (cache == null) {
            cache = new DiskBasedCache(cacheDir);
        }

        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        return queue;
    }
}
