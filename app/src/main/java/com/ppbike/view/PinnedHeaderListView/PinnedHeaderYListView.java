package com.ppbike.view.PinnedHeaderListView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;

import com.ppbike.view.YListView;

public class PinnedHeaderYListView extends YListView {

	private AbsListView.OnScrollListener mOnScrollListener;

	/**
	 * 头部点击事件监听
	 * @author chengmingyan
	 *
	 */
	public interface PinnedSectionedHeaderOnClickListener{
		boolean headerOnClickListener(MotionEvent ev, int headerViewWidth, int hearderViewHeight);
	}
	private PinnedSectionedHeaderAdapter mAdapter;
	private View mCurrentHeader;
	private int mCurrentHeaderViewType = 0;
	private float mHeaderOffset;
	private boolean mShouldPin = true;
	private int mCurrentSection = 0;
	private int mWidthMode;
	private int mHeightMode;
	private final Context context;
	private PinnedSectionedHeaderOnClickListener headerOnClickListener;
	
	public PinnedHeaderYListView(Context context) {
		super(context);
		this.context = context;
		super.setOnScrollListener(this);
	}

	public PinnedHeaderYListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		super.setOnScrollListener(this);
	}

	public PinnedHeaderYListView(Context context, AttributeSet attrs,
								 int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		super.setOnScrollListener(this);
	}

	public void setPinHeaders(boolean shouldPin) {
		mShouldPin = shouldPin;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		mCurrentHeader = null;
		mAdapter = (PinnedSectionedHeaderAdapter) adapter;
		super.setAdapter(adapter);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		if (mOnScrollListener != null) {
			mOnScrollListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}

		widthSpec = 0;
		heightSpec = 0;
		
		if (mAdapter == null || mAdapter.getCount() == 0 || !mShouldPin
				|| (firstVisibleItem < getHeaderViewsCount())) {
			mCurrentHeader = null;
			mHeaderOffset = 0.0f;
			for (int i = firstVisibleItem; i < firstVisibleItem
					+ visibleItemCount; i++) {
				View header = getChildAt(i);
				if (header != null) {
					header.setVisibility(VISIBLE);
				}
			}
			return;
		}

		firstVisibleItem -= getHeaderViewsCount();

		int section = mAdapter.getSectionForPosition(firstVisibleItem);
		int viewType = mAdapter.getSectionHeaderViewType(section);
		mCurrentHeader = getSectionHeaderView(section,
				mCurrentHeaderViewType != viewType ? null : mCurrentHeader);
		ensurePinnedHeaderLayout(mCurrentHeader);
		mCurrentHeaderViewType = viewType;

		mHeaderOffset = 0.0f;

		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
			if (mAdapter.isSectionHeader(i)) {
				View header = getChildAt(i - firstVisibleItem);
				float headerTop = header.getTop();
				float pinnedHeaderHeight = mCurrentHeader.getMeasuredHeight();
				header.setVisibility(VISIBLE);
				if (pinnedHeaderHeight >= headerTop && headerTop > 0) {
					mHeaderOffset = headerTop - header.getHeight();
				} else if (headerTop <= 0) {
					header.setVisibility(INVISIBLE);
				}
			}
		}

		invalidate();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	private View getSectionHeaderView(int section, View oldView) {
		boolean shouldLayout = section != mCurrentSection || oldView == null;

		View view = mAdapter.getSectionHeaderView(section, oldView, this);
		if (shouldLayout) {
			// a new section, thus a new header. We should lay it out again
			ensurePinnedHeaderLayout(view);
			mCurrentSection = section;
		}
		return view;
	}

	private int widthSpec;
	private int heightSpec;
	private void ensurePinnedHeaderLayout(View header) {
		if (header.isLayoutRequested()) {
			int widthSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
					mWidthMode);

			int heightSpec;
			ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
			if (layoutParams != null && layoutParams.height > 0) {
				heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height,
						View.MeasureSpec.EXACTLY);
			} else {
				heightSpec = View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED);
			}
			header.measure(widthSpec, heightSpec);
			header.layout(0, 0, header.getMeasuredWidth(),
					header.getMeasuredHeight());
			this.widthSpec = header.getMeasuredWidth();
			this.heightSpec = header.getMeasuredHeight();
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getY() <= heightSpec && getHeaderOnClickListener() != null) {
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	float lasty= -1;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
		    lasty = ev.getY();
		    if (ev.getY() <= heightSpec && getHeaderOnClickListener() != null) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (ev.getY() <= heightSpec&&lasty<=heightSpec&& getHeaderOnClickListener()!=null) {
			    lasty = -1;
				return headerOnClickListener.headerOnClickListener(ev,widthSpec, heightSpec);
			}
			break;
		default:
			break;
		}
			
		return super.onTouchEvent(ev);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mAdapter == null || !mShouldPin || mCurrentHeader == null)
			return;
		int saveCount = canvas.save();
		canvas.translate(0, mHeaderOffset);
		canvas.clipRect(0, 0, getWidth(), mCurrentHeader.getMeasuredHeight()); // needed
		// for
		// <
		// HONEYCOMB
		mCurrentHeader.draw(canvas);
		canvas.restoreToCount(saveCount);
	}

	@Override
	public void setOnScrollListener(AbsListView.OnScrollListener l) {
		mOnScrollListener = l;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mWidthMode = View.MeasureSpec.getMode(widthMeasureSpec);
		mHeightMode = View.MeasureSpec.getMode(heightMeasureSpec);
	}

	public void setOnItemClickListener(PinnedHeaderYListView.OnItemClickListener listener) {
		super.setOnItemClickListener(listener);
	}

	public PinnedSectionedHeaderOnClickListener getHeaderOnClickListener() {
		return headerOnClickListener;
	}

	public void setHeaderOnClickListener(PinnedSectionedHeaderOnClickListener headerOnClickListener) {
		this.headerOnClickListener = headerOnClickListener;
	}

	public abstract static class OnItemClickListener implements
			AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int rawPosition, long id) {
			SectionedBaseXListAdapter adapter;
			if (adapterView.getAdapter().getClass()
					.equals(HeaderViewListAdapter.class)) {
				HeaderViewListAdapter wrapperAdapter = (HeaderViewListAdapter) adapterView
						.getAdapter();
				adapter = (SectionedBaseXListAdapter) wrapperAdapter
						.getWrappedAdapter();
			} else {
				adapter = (SectionedBaseXListAdapter) adapterView.getAdapter();
			}
			int section = adapter.getSectionForPosition(rawPosition);
			int position = adapter.getPositionInSectionForPosition(rawPosition);

			if (position == -1) {
				onSectionClick(adapterView, view, section, id);
			} else {
				onItemClick(adapterView, view, section, position, id);
			}
		}

		public abstract void onItemClick(AdapterView<?> adapterView, View view,
				int section, int position, long id);

		public abstract void onSectionClick(AdapterView<?> adapterView,
				View view, int section, long id);

	}
}
