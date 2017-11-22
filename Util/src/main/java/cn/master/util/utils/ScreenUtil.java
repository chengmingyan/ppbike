package cn.master.util.utils;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class ScreenUtil {

	
	/**
	 * 获取屏幕宽度，与密度有关
	 * @return
	 */
	public static int getScreenWidth(Context c) {
		return c.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取屏幕高度，与密度有关
	 * @return
	 */
	public static int getScreenHeight(Context c) {
		return c.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 获取屏幕密度
	 * @return
	 */
	public static float getScreenDensity(Context c) {
		return c.getResources().getDisplayMetrics().density;
	}
	
	/**
	 * 
	 * @param iv
	 * @param d view 宽度与屏幕宽度
	 * @param e view 高度与宽度
	 * @param scaletype
	 */
	public static void setViewWH(View iv, float d, float e,
			ScaleType scaletype) {
		setViewWH(iv, d, e, scaletype, null);
	}

	/**
	 * 適用於view沒有父容器的情況 如通過new 初始化的view
	 * 
	 * @param iv
	 * @param d
	 * @param e
	 * @param scaletype
	 * @param parent
	 */
	public static int[] setViewWH(View iv, float d, float e,
			ScaleType scaletype, Class<?> parent) {
		int[] a = new int[2];
		if (null == parent) {
			if (null == iv.getParent()) {
				throw new IllegalStateException(
						"this view not has parent,Please specify");
			} else {
				parent = iv.getParent().getClass();
			}
		}
		int width = (int) (getScreenWidth(iv.getContext()) * d);
		if (FrameLayout.class.equals(parent)) {
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					width, (int) (width * e));
			a[0] = width;
			a[1] = (int) (width * e);
			iv.setLayoutParams(params);
		} else if (LinearLayout.class.equals(parent)) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					width, (int) (width * e));
			iv.setLayoutParams(params);
			a[0] = width;
			a[1] = (int) (width * e);
		} else if (AbsListView.class.equals(parent)) {
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					width, (int) (width * e));
			iv.setLayoutParams(params);
			a[0] = width;
			a[1] = (int) (width * e);
		} else {
			android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(
					width, (int) (width * e));
			iv.setLayoutParams(params);
			a[0] = width;
			a[1] = (int) (width * e);
		}
		if (ImageView.class.equals(iv.getClass()) && null != scaletype) {
			((ImageView) iv).setScaleType(scaletype);
		}
		return a;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context c,float dpValue) {
		return (int) (dpValue * getScreenDensity(c) + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context c,float pxValue) {
		return (int) (pxValue / getScreenDensity(c) + 0.5f);
	}
}
