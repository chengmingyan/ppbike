package com.ppbike.view.SwipeMenuList;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author baoyz
 * @date 2014-8-23
 * 
 */
public class SwipeMenu {
	public enum SwipeMenuDirection{
		left,right
	}
	private final Context mContext;
	private final List<SwipeMenuItem> mItems;
//	private int mViewType;
	private SwipeMenuDirection swipeMenuDirection = SwipeMenuDirection.right;

	public SwipeMenu(Context context) {
		mContext = context;
		mItems = new ArrayList<SwipeMenuItem>();
	}

	public Context getContext() {
		return mContext;
	}

	public void addMenuItem(SwipeMenuItem item) {
		mItems.add(item);
	}

	public void removeMenuItem(SwipeMenuItem item) {
		mItems.remove(item);
	}

	public List<SwipeMenuItem> getMenuItems() {
		return mItems;
	}

	public SwipeMenuItem getMenuItem(int index) {
		return mItems.get(index);
	}

//	public int getViewType() {
//		return mViewType;
//	}
//
//	public void setViewType(int viewType) {
//		this.mViewType = viewType;
//	}

	public SwipeMenuDirection getSwipeMenuDirection() {
		return swipeMenuDirection;
	}

	public void setSwipeMenuDirection(SwipeMenuDirection swipeMenuDirection) {
		this.swipeMenuDirection = swipeMenuDirection;
	}

}
