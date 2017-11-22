package com.ppbike.view.SwipeMenuList;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

/**
 * 
 * @author baoyz
 * @date 2014-8-24
 * 
 */
public abstract class SwipeMenuAdapter implements WrapperListAdapter,
		SwipeMenuView.OnSwipeItemClickListener {

	private final ListAdapter mAdapter;
	private final Context mContext;
	private SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener;

	public SwipeMenuAdapter(Context context, ListAdapter adapter) {
		mAdapter = adapter;
		mContext = context;
	}

	@Override
	public int getCount() {
		return mAdapter.getCount();
	}

	@Override
	public Object getItem(int position) {
		return mAdapter.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return mAdapter.getItemId(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SwipeMenuLayout layout = null;
		if (convertView == null) {
			View contentView = mAdapter.getView(position, convertView, parent);
			SwipeMenuView leftMenuView = null;
			SwipeMenu leftMenu = new SwipeMenu(mContext);
			leftMenu.setSwipeMenuDirection(SwipeMenu.SwipeMenuDirection.left);
			createMenu(leftMenu);
			if (leftMenu.getMenuItems() != null && leftMenu.getMenuItems().size()>0) {
				 leftMenuView = new SwipeMenuView(leftMenu,
						(SwipeMenuListView) parent);
				 leftMenuView.setOnSwipeItemClickListener(this);
			}
			SwipeMenuView rightMenuView = null;
			SwipeMenu rightMenu = new SwipeMenu(mContext);
			rightMenu.setSwipeMenuDirection(SwipeMenu.SwipeMenuDirection.right);
			createMenu(rightMenu);
			if (rightMenu.getMenuItems() != null && rightMenu.getMenuItems().size()>0) {
				rightMenuView = new SwipeMenuView(rightMenu,
						(SwipeMenuListView) parent);
				rightMenuView.setOnSwipeItemClickListener(this);
			}
			SwipeMenuListView listView = (SwipeMenuListView) parent;
			layout = new SwipeMenuLayout(contentView, leftMenuView,rightMenuView,
					listView.getCloseInterpolator(),
					listView.getOpenInterpolator());
			layout.setPosition(position);
			layout.setData(mAdapter.getItem(position));
		} else {
			layout = (SwipeMenuLayout) convertView;
			layout.closeMenu();
			layout.setPosition(position);
			layout.setData(mAdapter.getItem(position));
			View view = mAdapter.getView(position, layout.getContentView(),
					parent);
		}
		return layout;
	}

	
	public abstract void createMenu(SwipeMenu menu) ;

	@Override
	public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
		if (onMenuItemClickListener != null) {
			onMenuItemClickListener.onMenuItemClick(view,view.getPosition(), menu,
					index);
		}
	}

	public void setOnMenuItemClickListener(
			SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener) {
		this.onMenuItemClickListener = onMenuItemClickListener;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mAdapter.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mAdapter.unregisterDataSetObserver(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return mAdapter.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(int position) {
		return mAdapter.isEnabled(position);
	}

	@Override
	public boolean hasStableIds() {
		return mAdapter.hasStableIds();
	}

	@Override
	public int getItemViewType(int position) {
		return mAdapter.getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return mAdapter.getViewTypeCount();
	}

	@Override
	public boolean isEmpty() {
		return mAdapter.isEmpty();
	}

	@Override
	public ListAdapter getWrappedAdapter() {
		return mAdapter;
	}

}
