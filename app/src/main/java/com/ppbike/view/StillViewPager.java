/**
 * 
 */ 
package com.ppbike.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/** 
 * @author 程明炎 E-mail: 345021109@qq.com
 * @version 创建时间：2014年7月5日 上午9:31:14 
 * 类说明 
 */
public class StillViewPager extends ViewPager {
	private boolean isCanScroll = true;
	 /**
	 * @param context
	 * @param attrs
	 */
	public StillViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	  
    public void setScanScroll(boolean isCanScroll){  
        this.isCanScroll = isCanScroll;  
    }  
	    
  //触摸没有反应就可以了
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isCanScroll) {
            return super.onTouchEvent(event);
        }
  
        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isCanScroll) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }
}
 