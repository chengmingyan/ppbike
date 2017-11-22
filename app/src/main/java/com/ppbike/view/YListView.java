/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.ppbike.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.ppbike.R;

public class YListView extends ListView implements OnScrollListener {

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private IXListViewListener mListViewListener;

	// -- header view
	private XListViewHeader mHeaderView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = true;//
	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private FooterView mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;
	private boolean mIsfillscreen;

	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private LinearLayout mHeaderHintLayout;
	private static final int SCROLLBACK_HEADER = 0;
	private static final int SCROLLBACK_FOOTER = 1;

	private static final int SCROLL_DURATION = 400; // scroll back duration
	private static final int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.
														private static final float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.

	/**
	 * @param context
	 */
	public YListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public YListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public YListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
//		上下边界 蓝色或黄色阴影去除方法
		if(Integer.parseInt(Build.VERSION.SDK) >= 9){
			this.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// init header view
		setmHeaderView(new XListViewHeader(context));
		mHeaderViewContent = (RelativeLayout) getmHeaderView().findViewById(
				R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) getmHeaderView().findViewById(
				R.id.xlistview_header_time);
		mHeaderHintLayout = (LinearLayout) getmHeaderView().findViewById(
				R.id.xlist_hint_layout);
		addHeaderView(getmHeaderView());
		
//		measureHeaderLayout(getmHeaderView());

		// init footer view
		mFooterView = new FooterView(context);
		// init header height
		getmHeaderView().getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						ViewTreeObserver observer = getViewTreeObserver();
						if (null != observer) {
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								observer.removeGlobalOnLayoutListener(this);
							} else {
								observer.removeOnGlobalLayoutListener(this);
							}
						}
					}
				});
	}
	   /**
     * measure header layout
     * 
     * @param child
     */
    private void measureHeaderLayout(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
//			mFooterView.hide();
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.GONE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			// mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			// both "pull up" and "click" will invoke load more.
			// mFooterView.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// startLoadMore();
			// }
			// });
		}
		mFooterView.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);

	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		System.err.println(getmHeaderView().getVisiableHeight()
				+ "          4444   " + System.currentTimeMillis());
		handler.sendEmptyMessage(0);
	}

	private final Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mPullRefreshing == true) {
				mPullRefreshing = false;
				resetHeaderHeight();
			}
//			if (getAdapter()!=null && getAdapter().getCount() != 0) {
//				mFooterView.hide();
//			}
		}
	};

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
		}
		mFooterView.setState(FooterView.STATE_NORMAL);
	}
	/**
	 * Stop load more, reset footer view.
	 */
	public void stopLoadMoreNetwork() {
		if (mPullLoading) {
			mPullLoading = false;
		}
		mFooterView.setState(FooterView.STATE_NETWORK);
//        mFooterView.hide();
	}
	/**
	 * setNoMore
	 * true:STATE_NOMORE
	 * false:STATE_NORMAL
	 */
	public void stopNoMore() {
		if (mPullLoading) {
			mPullLoading = false;
		}
//        mFooterView.show();
		mFooterView.setState(FooterView.STATE_NOMORE);
	}
	/**
	 * set last refresh time
	 *
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderHintLayout.setVisibility(View.VISIBLE);
		mHeaderTimeView.setText(time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	public interface RefreshTimeListener {
		/**
		 * 
		 */
		void onRefreshTime(TextView view);
	}

	private RefreshTimeListener refreshTimeListener;

	private void updateHeaderHeight(float delta) {
		getmHeaderView().setVisiableHeight(
				(int) delta + getmHeaderView().getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (getmHeaderView().getVisiableHeight() > mHeaderViewHeight) {
				getmHeaderView().setState(XListViewHeader.STATE_READY);
			} else {
				getmHeaderView().setState(XListViewHeader.STATE_NORMAL);
			}
			if (mHeaderHintLayout.getVisibility() != View.VISIBLE
					|| "".equals(mHeaderTimeView.getText().toString())
					|| "未更新".equals(mHeaderTimeView.getText().toString())) {
				mHeaderHintLayout.setVisibility(View.VISIBLE);
				if (refreshTimeListener != null) {
					refreshTimeListener.onRefreshTime(mHeaderTimeView);
				}
			}
		} else {
			mHeaderHintLayout.setVisibility(View.GONE);
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
		int height = getmHeaderView().getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mFooterView.getState() == FooterView.STATE_NETWORK){
			mFooterView.setState(FooterView.STATE_LOADING);
		}

		mFooterView.setBottomMargin(height);

		setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			System.err.println("isFinished=SCROLLBACK_FOOTER");
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	public void startLoadMore() {
		if (!mEnablePullLoad){
			return;
		}
		if (!mPullLoading&&mFooterView.getState()!=FooterView.STATE_NOMORE) {
			mPullLoading = true;
			mFooterView.setState(FooterView.STATE_LOADING);
			if (mListViewListener != null) {
				mListViewListener.onLoadMore();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (getmHeaderView().getVisiableHeight() > 0 || deltaY > 0)&&mEnablePullRefresh) {
				// the first item is showing, header has shown or pull down.

				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)
					&& mIsfillscreen) {
				// last item, already pulled up or want to pull up.
				updateFooterHeight(-deltaY / OFFSET_RADIO);
				// only invalid the no loadmore function
				// fixed by teewell
//				if (mEnablePullLoad&&!mPullLoading) {
//					mFooterView.show();
//					startLoadMore();
//					resetFooterHeight();
//				}
			}
			break;
		default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& getmHeaderView().getVisiableHeight() > mHeaderViewHeight) {
					startRefresh();
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				resetFooterHeight();
				startLoadMore();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void startRefresh() {
		if (mPullRefreshing) {
			return;
		}
		mPullRefreshing = true;
		mHeaderHintLayout.setVisibility(View.GONE);
		getmHeaderView().setState(XListViewHeader.STATE_REFRESHING);
		if (mListViewListener != null) {
			mListViewListener.onRefresh();
		}
	}

	/**
	 * use to on first time refresh add by Tim
	 */
	public void firstRefresh() {
		getmHeaderView().setVisiableHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60f,getResources().getDisplayMetrics()));
		mHeaderHintLayout.setVisibility(View.GONE);
		startRefresh();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				getmHeaderView().setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		mIsfillscreen = visibleItemCount < totalItemCount
				|| (mPullLoading && totalItemCount != 0);
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	public XListViewHeader getmHeaderView() {
		return mHeaderView;
	}

	public void setmHeaderView(XListViewHeader mHeaderView) {
		this.mHeaderView = mHeaderView;
	}

	public RefreshTimeListener getRefreshTimeListener() {
		return refreshTimeListener;
	}

	public void setRefreshTimeListener(RefreshTimeListener refreshTimeListener) {
		this.refreshTimeListener = refreshTimeListener;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		void onXScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IXListViewListener {
		void onRefresh();

		void onLoadMore();
	}
}
