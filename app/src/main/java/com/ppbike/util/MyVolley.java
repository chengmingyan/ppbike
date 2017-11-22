package com.ppbike.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ppbike.bean.BitmapLruCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class that is used to provide references to initialized
 * RequestQueue(s) and ImageLoader(s)
 * 
 * @author Ognyan Bankov
 * 
 */
public class MyVolley {
	private static final int MAX_IMAGE_CACHE_ENTIRES = 100;

	private static RequestQueue mRequestQueue;
	private static RequestQueue mRequestQueueNoCache;
	private static ImageLoader mImageLoader;
	private static ImageLoader mImageLoaderNoCache;
	private static final BitmapLruCache imagecache = new BitmapLruCache(
			MAX_IMAGE_CACHE_ENTIRES);

	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		mRequestQueueNoCache = newRequestQueueNoCache(context, null);
		mImageLoader = new ImageLoader(mRequestQueue, imagecache);
		mImageLoaderNoCache = new ImageLoader(mRequestQueue,
				new MyImageCacheNoCache());
	}

	public static ImageLoader getmImageLoaderNoCache() {
		return mImageLoaderNoCache;
	}

	public static class MyImageCacheNoCache extends NoCache implements
			ImageCache {
		public MyImageCacheNoCache() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public Bitmap getBitmap(String url) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			// TODO Auto-generated method stub

		}

	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	public static void Post(String url, final HashMap<String, String> params,
							Listener<String> listener, ErrorListener errorListener, Object tag) {
		StringRequest myReq = new StringRequest(Method.POST, url, listener,
				errorListener) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				if (null == params || params.isEmpty()) {
					return super.getParams();
				}
				return params;
			}
		};
		if (null != tag) {
			myReq.setTag(tag);
		}
		myReq.setRetryPolicy(new RetryPolicy() {

			@Override
			public void retry(VolleyError error) throws VolleyError {
				// TODO Auto-generated method stub

			}

			@Override
			public int getCurrentTimeout() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getCurrentRetryCount() {
				// TODO Auto-generated method stub
				return 0;
			}
		});
		myReq.setShouldCache(false);
		mRequestQueue.add(myReq);
	}

	public static void Post(String url, Listener<String> listener,
			ErrorListener errorListener) {
		Post(url, null, listener, errorListener, null);
	}

	public static void Post(String url, HashMap<String, String> params,
							Listener<String> listener, ErrorListener errorListener) {
		Post(url, params, listener, errorListener, null);
	}

	public static void Post(String url, Listener<String> listener,
							ErrorListener errorListener, Object tag) {
		Post(url, null, listener, errorListener, tag);
	}

	public static void Get(String url, Listener<String> listener,
			ErrorListener errorListener) {
		Get(url, listener, errorListener, null);
	}

	public static void Get(String url, Listener<String> listener,
						   ErrorListener errorListener, Object tag) {

		StringRequest myReq = new StringRequest(Method.GET, url, listener,
				errorListener);
		if (null != tag) {
			myReq.setTag(tag);
		}
		myReq.setRetryPolicy(new RetryPolicy() {

			@Override
			public void retry(VolleyError error) throws VolleyError {
				// TODO Auto-generated method stub

			}

			@Override
			public int getCurrentTimeout() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getCurrentRetryCount() {
				// TODO Auto-generated method stub
				return 0;
			}
		});
		myReq.setShouldCache(false);
		mRequestQueue.add(myReq);
	}

	/**
	 * Returns instance of ImageLoader initialized with {@see FakeImageCache}
	 * which effectively means that no memory caching is used. This is useful
	 * for images that you know that will be show only once.
	 * 
	 * @return
	 */
	public static ImageLoader getImageLoader() {
		if (mImageLoader != null) {
			return mImageLoader;
		} else {
			throw new IllegalStateException("ImageLoader not initialized");
		}
	}

	public static void cancelAll(final Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	private static void setBitmap(ImageView view, Bitmap bitmap) {
		view.setVisibility(View.VISIBLE);
		Bitmap previousbitmap = view.getDrawingCache();
		if (previousbitmap != null && !previousbitmap.isRecycled())
		{
			previousbitmap.recycle();
			previousbitmap = null;
		}
		view.setImageBitmap(bitmap);
	}

	final int setbitmap = 1;
	final String key_view = "view";
	final String key_bitmap = "bitmap";
	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			HashMap<String, Object> objMap = (HashMap<String, Object>) msg.obj;

			switch (msg.what) {
			case setbitmap:

				setBitmap((ImageView) objMap.get(key_view),
						(Bitmap) objMap.get(key_bitmap));
				break;

			default:
				break;
			}
			return false;
		}

	});

	public static RequestQueue newRequestQueueNoCache(Context context,
													  HttpStack stack) {

		String userAgent = "volley/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {
		}

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See:
				// http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				stack = new HttpClientStack(
						AndroidHttpClient.newInstance(userAgent));
			}
		}

		Network network = new BasicNetwork(stack);

		RequestQueue queue = new RequestQueue(new NoCache(), network);
		queue.start();

		return queue;
	}

	public static RequestQueue getmRequestQueueNoCache() {
		if (mRequestQueueNoCache != null) {
			return mRequestQueueNoCache;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}
}
