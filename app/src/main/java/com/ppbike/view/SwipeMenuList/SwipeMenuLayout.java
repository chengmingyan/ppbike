package com.ppbike.view.SwipeMenuList;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * 
 * @author baoyz
 * @date 2014-8-23
 * 
 */
public class SwipeMenuLayout extends FrameLayout {
	
	private static final int DURATION_TIME  = 250;

	private static final int CONTENT_VIEW_ID = 1;
	private static final int LEFT_MENU_VIEW_ID = 2;
	private static final int RIGHT_MENU_VIEW_ID = 3;

	private static final int STATE_CLOSE = 0;
	private static final int STATE_OPEN = 1;

	private View mContentView;
	private SwipeMenuView leftMenuView;
	private SwipeMenuView rightMenuView;
	private int mDownX;
	private int state = STATE_CLOSE;
	private GestureDetectorCompat mGestureDetector;
	private OnGestureListener mGestureListener;
	private boolean isFling;
	private final int MIN_FLING = dp2px(15);
	private final int MAX_VELOCITYX = -dp2px(500);
	private ScrollerCompat mOpenScroller;
	private ScrollerCompat mCloseScroller;
	private int mBaseX;
	private int position;
	private Interpolator mCloseInterpolator;
	private Interpolator mOpenInterpolator;

	public SwipeMenuLayout(View contentView, SwipeMenuView leftMenuView,
			SwipeMenuView rigthMenuView) {
		this(contentView, leftMenuView, rigthMenuView, null, null);
	}

	public SwipeMenuLayout(View contentView, SwipeMenuView leftMenuView,
			SwipeMenuView rigthMenuView, Interpolator closeInterpolator,
			Interpolator openInterpolator) {
		super(contentView.getContext());
		if (contentView == null) {
			throw new IllegalArgumentException("contentView can not null");
		}
		mCloseInterpolator = closeInterpolator;
		mOpenInterpolator = openInterpolator;
		mContentView = contentView;
		this.leftMenuView = leftMenuView;
		this.rightMenuView = rigthMenuView;
		if (leftMenuView != null) {

			leftMenuView.setLayout(this);
		}
		if (rightMenuView != null) {

			rightMenuView.setLayout(this);
		}
		init();
	}

	private SwipeMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private SwipeMenuLayout(Context context) {
		super(context);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
		if (leftMenuView != null) {
			leftMenuView.setPosition(position);
		}
		if (rightMenuView != null) {
			rightMenuView.setPosition(position);
		}
	}

	public void setData(Object data) {
		if (leftMenuView != null) {
			leftMenuView.setData(data);
		}
		if (rightMenuView != null) {
			rightMenuView.setData(data);
		}
	}

	private void init() {
		setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		mGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				isFling = false;
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO
				if (Math.abs(e1.getX() - e2.getX()) > MIN_FLING
						&& velocityX < MAX_VELOCITYX) {
					isFling = true;
				}
				// Log.i("byz", MAX_VELOCITYX + ", velocityX = " + velocityX);
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		};
		mGestureDetector = new GestureDetectorCompat(getContext(),
				mGestureListener);

		if (mCloseInterpolator != null) {
			mCloseScroller = ScrollerCompat.create(getContext(),
					mCloseInterpolator);
		} else {
			mCloseScroller = ScrollerCompat.create(getContext());
		}
		if (mOpenInterpolator != null) {
			mOpenScroller = ScrollerCompat.create(getContext(),
					mOpenInterpolator);
		} else {
			mOpenScroller = ScrollerCompat.create(getContext());
		}

		LayoutParams contentParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mContentView.setLayoutParams(contentParams);
		if (mContentView.getId() < 1) {
			mContentView.setId(CONTENT_VIEW_ID);
		}
		addView(mContentView);

		if (leftMenuView != null) {

			leftMenuView.setId(LEFT_MENU_VIEW_ID);
			leftMenuView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			addView(leftMenuView);
		}
		if (rightMenuView != null) {

			rightMenuView.setId(RIGHT_MENU_VIEW_ID);
			rightMenuView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			addView(rightMenuView);
		}

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public boolean onSwipe(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		if (leftMenuView == null &&  rightMenuView == null) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = (int) event.getX();
			isFling = false;
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.i("byz", "downX = " + mDownX + ", moveX = " + event.getX());
			int dis = (int) (mDownX - event.getX());
			if (state == STATE_OPEN) {
				if (rightMenuView !=null && mContentView.getLeft() < 0) {//右菜单展开
					dis += rightMenuView.getWidth();
					//双向拨动时,以下判断是必需的,滑到起始位置时,数据归零
					if (dis <= 0) {
						state = STATE_CLOSE;
						mDownX = (int) event.getX();
						dis = 0;
					}
				}else if (leftMenuView != null && mContentView.getLeft() > 0) {//左菜单展开
					dis -= leftMenuView.getWidth();
					if (dis >= 0) {
						state = STATE_CLOSE;
						mDownX = (int) event.getX();
						dis = 0;
					}
				}
			}
			swipe(dis);
			break;
		case MotionEvent.ACTION_UP:
			int distance = (int) (mDownX - event.getX());
			if ( leftMenuView != null && distance <0 && mContentView.getLeft() >= leftMenuView.getWidth()/2) {
				smoothOpenMenu();
			}else if (rightMenuView != null && distance >0 && mContentView.getLeft() <= - rightMenuView.getWidth()/2) {
				smoothOpenMenu();
			}else {
				smoothCloseMenu();
				return false;
			}
			
			
/*原代码,我觉得不好	
 * 		if (isFling
					|| (mDownX - event.getX()) > (rightMenuView.getWidth() / 2)) {
				// open
				smoothOpenMenu();
			} else {
				// close
				smoothCloseMenu();
				return false;
			}*/
			break;
		}
		return true;
	}

	public boolean isOpen() {
		return state == STATE_OPEN;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	/**大于0向左滑,小于0向右滑
	 * @param dis
	 */
	private void swipe(int dis) {
		if (dis == 0 && mContentView.getLeft() == 0 ) {
			return;
		}
		
		//向左滑最大距离等于右menu宽度
		if (dis>0 && rightMenuView != null) {
			if (dis > rightMenuView.getWidth()) {
				dis = rightMenuView.getWidth();
			}
		}else if(dis > 0){
			//设置不可滑动
				dis = 0;
		}
		//向右滑动,dis<0,最小值为左menu宽度的负值
		if (dis <0 && leftMenuView != null) {
			if (dis < -leftMenuView.getWidth()) {
				dis = -leftMenuView.getWidth();
			}
		}else if (dis < 0) {
			dis = 0;
		}
//		Log.e("swipe",dis+"");
	
		mContentView.layout(-dis, mContentView.getTop(),
				mContentView.getWidth() - dis, getMeasuredHeight());
		if (rightMenuView != null) {
			
			rightMenuView.layout(mContentView.getWidth() - dis,
					rightMenuView.getTop(),
					mContentView.getWidth() + rightMenuView.getWidth() - dis,
					rightMenuView.getBottom());
		}
		if (leftMenuView != null) {
			leftMenuView.layout(-leftMenuView.getWidth()-dis,leftMenuView.getTop(), -dis, leftMenuView.getBottom());
		}
	}

	@Override
	public void computeScroll() {
		if (state == STATE_OPEN) {
			if (mOpenScroller.computeScrollOffset()) {
				if (mContentView.getLeft()>0) {
					swipe(-mOpenScroller.getCurrX());
				}else {
					swipe(mOpenScroller.getCurrX());
				}
				postInvalidate();
			}
		} else if(state == STATE_CLOSE){
//			Log.e("mCloseScroller.getCurrX()---", "="+mCloseScroller.getCurrX());
			if (mCloseScroller.computeScrollOffset()) {
				//左菜单关闭
				if (mContentView.getLeft()>0) {
					swipe(mBaseX-mCloseScroller.getCurrX());
				}else {//右菜单关闭
					swipe(mBaseX - mCloseScroller.getCurrX());
				}
				postInvalidate();
			}
		}
	}

	public void smoothCloseMenu() {
		state = STATE_CLOSE;
		mBaseX = -mContentView.getLeft();
		//startScroll的使用方式:x轴已滑动的偏移量,y轴已滑动的偏移量,x偏移总量(正值向右,负值向左),y轴偏移总量,动画时间(不写默认250ms)
		mCloseScroller.startScroll(0, 0, mBaseX, 0, DURATION_TIME);
		postInvalidate();
	}

	public void smoothOpenMenu() {
		state = STATE_OPEN;
		//根据mContentView.getLeft()的正负值来判断,是向左展开还是向右展开,正值向右展开,否则向左
		if (mContentView.getLeft() > 0) {
			mOpenScroller.startScroll(mContentView.getLeft(), 0,
					leftMenuView.getWidth(), 0, DURATION_TIME);
		}else {
			mOpenScroller.startScroll(-mContentView.getLeft(), 0,
					rightMenuView.getWidth(), 0, DURATION_TIME);
		}
		postInvalidate();
	}

	public void closeMenu() {
		if (mCloseScroller.computeScrollOffset()) {
			mCloseScroller.abortAnimation();
		}
		if (state == STATE_OPEN) {
			state = STATE_CLOSE;
			swipe(0);
		}
	}

	public void openMenu() {
		if (state == STATE_CLOSE) {
			state = STATE_OPEN;
			if (leftMenuView != null) {
				swipe(-leftMenuView.getWidth());
			}
			if (rightMenuView != null) {
				swipe(rightMenuView.getWidth());
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (leftMenuView != null) {
			
			leftMenuView.measure(MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
							getMeasuredHeight(), MeasureSpec.EXACTLY));
		}
		if (rightMenuView != null) {
			rightMenuView.measure(MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
							getMeasuredHeight(), MeasureSpec.EXACTLY));
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mContentView.layout(0, 0, getMeasuredWidth(),
				mContentView.getMeasuredHeight());
		if (leftMenuView != null) {
			leftMenuView.layout(-leftMenuView.getMeasuredWidth(), 0,0,
					mContentView.getMeasuredHeight());
		}
		if (rightMenuView != null) {
			rightMenuView.layout(getMeasuredWidth(), 0, getMeasuredWidth()
					+ rightMenuView.getMeasuredWidth(),
					mContentView.getMeasuredHeight());
		}
	}

/*	public void setMenuHeight(int measuredHeight) {
		Log.i("byz", "pos = " + position + ", height = " + measuredHeight);
		LayoutParams params = (LayoutParams) rightMenuView.getLayoutParams();
		if (params.height != measuredHeight) {
			params.height = measuredHeight;
			rightMenuView.setLayoutParams(rightMenuView.getLayoutParams());
		}
	}*/

	public View getContentView() {
		return mContentView;
	}
}
