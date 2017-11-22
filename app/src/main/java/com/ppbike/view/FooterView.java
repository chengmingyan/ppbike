package com.ppbike.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ppbike.R;


public class FooterView extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_NOMORE = 1;
    public final static int STATE_LOADING = 2;
    public final static int STATE_NETWORK = 3;

    private View mContentView;
    private View mHintView;
    private TextView tv_hint;
    private View mProgressBar;

    private int mState = STATE_NORMAL;

    public FooterView(Context context) {
        super(context);
        initView(context);
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {

        LinearLayout mLayout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.view_footer, null);
        addView(mLayout);
        mLayout.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        mContentView = mLayout.findViewById(R.id.footer_content);
        mProgressBar = mLayout.findViewById(R.id.footer_progressbar);
        mHintView = mLayout.findViewById(R.id.layout_tip);
        tv_hint = (TextView) mLayout.findViewById(R.id.footer_hint_text);

        setState(STATE_NORMAL);
    }

    /**
     * Set footer view state
     *
     * @param state
     * @see #STATE_LOADING
     * @see #STATE_NOMORE
     */
    public void setState(int state) {
        if (state == mState) return;

        switch (state) {
            case STATE_NOMORE:
                mHintView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                tv_hint.setText("已经刷到底了");
                break;
            case STATE_NETWORK:
                mHintView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                tv_hint.setText("网络不给力，请重试");
                break;
            case STATE_LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                mHintView.setVisibility(View.GONE);
                break;
            default:
                mProgressBar.setVisibility(View.INVISIBLE);
                mHintView.setVisibility(View.GONE);
                break;

        }

        mState = state;
    }

    public void setBottomMargin(int height) {
        if (height < 0)
            return;
        LayoutParams lp = (LayoutParams) mContentView
                .getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LayoutParams lp = (LayoutParams) mContentView
                .getLayoutParams();
        return lp.bottomMargin;
    }

    public int getState(){
    return mState;
}
    /**
     * normal status
     */
    public void normal() {
        mHintView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * loading status
     */
    public void loading() {
        mHintView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * hide footer when disable pull load more
     */
    public void hide() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.height = 0;
        mContentView.setLayoutParams(lp);
    }

    /**
     * show footer
     */
    public void show() {
        LayoutParams lp = (LayoutParams) mContentView.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mContentView.setLayoutParams(lp);
    }
}
