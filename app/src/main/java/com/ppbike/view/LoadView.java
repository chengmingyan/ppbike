package com.ppbike.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ppbike.R;


public class LoadView extends LinearLayout {
    private OnStatusChangedListener onStatusChangedListener;
    private ImageView image_tip;
    private ProgressBar bar;
    private TextView tv_tip;
    private Button btn_reload;
    private LinearLayout layout;
    private Status status = Status.nor;
    private OnClickListener onReloadClickListener = null;
    private int background;

    public enum Status {
        network_error, loading, successed, not_data, data_error, nor
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public LoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        final View view = LayoutInflater.from(context).inflate(R.layout.loadview,
                null);
        this.bar = (ProgressBar) view.findViewById(R.id.progressBar1);
        this.image_tip = (ImageView) view.findViewById(R.id.image_tip);
        btn_reload = (Button) view.findViewById(R.id.btn_reload);
        tv_tip = (TextView) view.findViewById(R.id.tv_tip);
        layout = (LinearLayout) view.findViewById(R.id.layout);
        btn_reload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onReloadClickListener != null) {
                    setStatus(Status.loading);
                    onReloadClickListener.onClick(LoadView.this);
                }
            }
        });

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadView, 0, 0);

        netErrImageId = a.getResourceId(R.styleable.LoadView_errorImageId, R.mipmap.ic_nowifi_xhdpi);
        noDateErrImgId = a.getResourceId(R.styleable.LoadView_noDataImageId, R.mipmap.ic_noinfo_xhdpi);
        netErrStrId = a.getResourceId(R.styleable.LoadView_errorString,R.string.network_error);
        noDateErrStrId = a.getResourceId(R.styleable.LoadView_noDataString,R.string.nodata);
        a.recycle();

        this.setVisibility(View.GONE);
        this.addView(view);
        setBackgroundResource(R.color.background_layout);
    }

    public LoadView(Context context) {
        this(context, null);
    }


    public Status getStatus() {
        return status;
    }

    public LoadView setStatus(Status status) {
        this.status = status;
        refresh();
        if (getOnStatusChangedListener() != null) {
            getOnStatusChangedListener().OnStatusChanged(status);
        }
        return null;
    }

    private int netErrImageId  ;
    private int netErrStrId  ;
    private int noDateErrImgId  ;
    private int noDateErrStrId ;
    private int dateErrStrId ;
    private int dateErrImgId ;

    public int getDateErrImgId() {
        return dateErrImgId;
    }

    public void setDateErrImgId(int dateErrImgId) {
        this.dateErrImgId = dateErrImgId;
    }

    public int getNetErrImageId() {
        return netErrImageId;
    }

    public void setNetErrImageId(int netErrImageId) {
        this.netErrImageId = netErrImageId;
    }

    public int getNetErrStrId() {
        return netErrStrId;
    }

    public void setNetErrStrId(int netErrStrId) {
        this.netErrStrId = netErrStrId;
    }

    public int getNoDateErrImgId() {
        return noDateErrImgId;
    }

    public void setNoDateErrImgId(int noDateErrImgId) {
        this.noDateErrImgId = noDateErrImgId;
    }

    public int getNoDateErrStrId() {
        return noDateErrStrId;
    }

    public void setNoDateErrStrId(int noDateErrStrId) {
        this.noDateErrStrId = noDateErrStrId;
    }

    public int getDateErrStrId() {
        return dateErrStrId;
    }

    public void setDateErrStrId(int dateErrStrId) {
        this.dateErrStrId = dateErrStrId;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    private void refresh() {
        btn_reload.setVisibility(View.GONE);
        switch (status) {
            case network_error:
                if (background != 0)
                    setBackgroundResource(background);
                layout.setVisibility(View.VISIBLE);
                if (netErrImageId != 0)
                    image_tip.setImageResource(netErrImageId);
                if (netErrStrId != 0)
                    tv_tip.setText(netErrStrId);
                bar.setVisibility(View.GONE);
                btn_reload.setVisibility(View.VISIBLE);
                this.setVisibility(View.VISIBLE);
                break;
            case loading:
                if (background != 0)
                    setBackgroundResource(background);
                layout.setVisibility(View.GONE);
                bar.setVisibility(View.VISIBLE);
                this.setVisibility(View.VISIBLE);
                break;
            case nor:
            case successed:
                if (background != 0)
                    setBackgroundResource(background);
                layout.setVisibility(View.GONE);
                bar.setVisibility(View.GONE);
                this.setVisibility(View.GONE);
                break;
            case not_data:
                if (background != 0)
                    setBackgroundResource(background);
                layout.setVisibility(View.VISIBLE);
                if (noDateErrImgId != 0)
                    image_tip.setImageResource(noDateErrImgId);
                tv_tip.setText(noDateErrStrId);
                bar.setVisibility(View.GONE);
                this.setVisibility(View.VISIBLE);
                break;
            case data_error:
                if (background != 0)
                    setBackgroundResource(background);
                layout.setVisibility(View.VISIBLE);
                if (dateErrStrId != 0)
                    image_tip.setImageResource(dateErrStrId);
                if (dateErrImgId != 0)
                    tv_tip.setText(dateErrImgId);
                bar.setVisibility(View.GONE);
                btn_reload.setVisibility(View.VISIBLE);
                this.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    public interface OnStatusChangedListener {
        void OnStatusChanged(Status status);
    }

    public void setOnReLoadClickListener(OnClickListener onReLoadClickListener) {
        this.onReloadClickListener = onReLoadClickListener;
    }

    public OnStatusChangedListener getOnStatusChangedListener() {
        return onStatusChangedListener;
    }

    public void setOnStatusChangedListener(
            OnStatusChangedListener onStatusChangedListener) {
        this.onStatusChangedListener = onStatusChangedListener;
    }

}
