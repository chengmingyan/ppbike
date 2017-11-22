package com.ppbike.view.SweetAlertDialog;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.TextView;

import com.ppbike.R;

public class SweetAlertDialog extends Dialog implements View.OnClickListener {
	private View mDialogView;
	private final AnimationSet mModalInAnim;
	private final AnimationSet mModalOutAnim;
	private final Animation mOverlayOutAnim;
	private TextView mTitleTextView;
	private TextView mContentTextView;
	private String mTitleText;
	private String mContentText;
	private boolean mShowCancel;
	private boolean mShowContent;
	private String mCancelText;
	private String mConfirmText;
	private int mAlertType;
	private Button mConfirmButton;
	private Button mCancelButton;
	private OnSweetClickListener mCancelClickListener;
	private OnSweetClickListener mConfirmClickListener;
	private boolean mCloseFromCancel;

	public static final int NORMAL_TYPE = 0;
	public static final int PROGRESS_DEFAULT_TYPE = 5;
	public static final int PROGRESS_TYPE = 6;
	private int confirmTextColor;

	public interface OnSweetClickListener {
		void onClick(SweetAlertDialog sweetAlertDialog);
	}

	public SweetAlertDialog(Context context) {
		this(context, NORMAL_TYPE);
	}

	public SweetAlertDialog(Context context, int alertType) {
		super(context, R.style.alert_dialog);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		mAlertType = alertType;
		
		
		mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(
				getContext(), R.anim.modal_in);
		mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(
				getContext(), R.anim.modal_out);
		mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mDialogView.setVisibility(View.GONE);
				mDialogView.post(new Runnable() {
					@Override
					public void run() {
						if (mCloseFromCancel) {
							SweetAlertDialog.super.cancel();
						} else {
							SweetAlertDialog.super.dismiss();
						}
					}
				});
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		// dialog overlay fade out
		mOverlayOutAnim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				WindowManager.LayoutParams wlp = getWindow().getAttributes();
				wlp.alpha = 1 - interpolatedTime;
				getWindow().setAttributes(wlp);
			}
		};
		mOverlayOutAnim.setDuration(120);
		
		
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_sweet_alert);

		mDialogView = getWindow().getDecorView().findViewById(
				android.R.id.content);
		mTitleTextView = (TextView) findViewById(R.id.title_text);
		mContentTextView = (TextView) findViewById(R.id.content_text);
		mConfirmButton = (Button) findViewById(R.id.confirm_button);
		mCancelButton = (Button) findViewById(R.id.cancel_button);
		mConfirmButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);

		setTitleText(mTitleText);
		setTitleTextColor(titleTextColor);
		setTitleTextSize(titleTitleSize);
		setContentText(mContentText);
		setContentTextColor(contentTextColor);
		setContentTextSize(contentTextSize);
		setCancelText(mCancelText);
		setConfirmText(mConfirmText);
		changeAlertType(mAlertType, true);

	}

	private void restore() {
		mConfirmButton.setVisibility(View.VISIBLE);
	}


	private void changeAlertType(int alertType, boolean fromCreate) {
		mAlertType = alertType;
		// call after created views
		if (mDialogView != null) {
			if (!fromCreate) {
				// restore all of views state before switching alert type
				restore();
			}
		}
	}

	public int getAlerType() {
		return mAlertType;
	}

	public void changeAlertType(int alertType) {
		changeAlertType(alertType, false);
	}

	public String getTitleText() {
		return mTitleText;
	}

	public SweetAlertDialog setTitleText(String text) {
		mTitleText = text;
		if (mTitleTextView != null ) {
			if (mTitleText == null) {
				mTitleTextView.setVisibility(View.GONE);
			}else {
				mTitleTextView.setText(mTitleText);
			}
		}
		return this;
	}

	public String getContentText() {
		return mContentText;
	}

	public SweetAlertDialog setContentText(String text) {
		mContentText = text;
		if (mContentTextView != null) {
			showContentText(true);
			if (mContentText != null) {
				mContentTextView.setText(mContentText);
			}else {
				mContentTextView.setVisibility(View.GONE);
			}
		}
		return this;
	}

	public boolean isShowCancelButton() {
		return mShowCancel;
	}

	public SweetAlertDialog showCancelButton(boolean isShow) {
		mShowCancel = isShow;
		if (mCancelButton != null) {
			mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
		}
		return this;
	}

	public boolean isShowContentText() {
		return mShowContent;
	}

	public SweetAlertDialog showContentText(boolean isShow) {
		mShowContent = isShow;
		if (mContentTextView != null) {
			mContentTextView.setVisibility(mShowContent ? View.VISIBLE
					: View.GONE);
		}
		return this;
	}

	public String getCancelText() {
		return mCancelText;
	}

	public SweetAlertDialog setCancelText(String text) {
		mCancelText = text;
		if (mCancelButton != null && mCancelText != null) {
			showCancelButton(true);
			mCancelButton.setText(mCancelText);
		}
		return this;
	}

	public String getConfirmText() {
		return mConfirmText;
	}

	public SweetAlertDialog setConfirmText(String text) {
		mConfirmText = text;
		if (mConfirmButton != null && mConfirmText != null) {
			mConfirmButton.setVisibility(View.VISIBLE);
			mConfirmButton.setText(mConfirmText);
		} else if (mConfirmButton != null) {
			mConfirmButton.setVisibility(View.GONE);
		}
		return this;
	}
	public SweetAlertDialog setConfirmTextColor(int color) {
		confirmTextColor = color;
		if (mConfirmButton != null && color != 0) {
			mConfirmButton.setTextColor(color);
		}
		return this;
	}
	public SweetAlertDialog setCancelClickListener(OnSweetClickListener listener) {
		mCancelClickListener = listener;
		return this;
	}

	public SweetAlertDialog setConfirmClickListener(
			OnSweetClickListener listener) {
		mConfirmClickListener = listener;
		return this;
	}

	protected void onStart() {
		mDialogView.startAnimation(mModalInAnim);
	}

	/**
	 * The real Dialog.cancel() will be invoked async-ly after the animation
	 * finishes.
	 */
	@Override
	public void cancel() {
		dismissWithAnimation(true);
	}

	/**
	 * The real Dialog.dismiss() will be invoked async-ly after the animation
	 * finishes.
	 */
	public void dismissWithAnimation() {
		dismissWithAnimation(false);
	}

	private void dismissWithAnimation(boolean fromCancel) {
		mCloseFromCancel = fromCancel;
		mConfirmButton.startAnimation(mOverlayOutAnim);
		mDialogView.startAnimation(mModalOutAnim);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cancel_button) {
			if (mCancelClickListener != null) {
				mCancelClickListener.onClick(SweetAlertDialog.this);
			} else {
				dismissWithAnimation();
			}
		} else if (v.getId() == R.id.confirm_button) {
			if (mConfirmClickListener != null) {
				mConfirmClickListener.onClick(SweetAlertDialog.this);
			} else {
				dismissWithAnimation();
			}
		}
	}

	private int titleTextColor,contentTextColor,cancelTextColor;
	private float titleTitleSize,contentTextSize;
	public SweetAlertDialog setTitleTextColor(int color) {
		this.titleTextColor = color;
		if (mTitleTextView != null && color != 0) {
			mTitleTextView.setTextColor(color);
		}
		return this;
	}
	public SweetAlertDialog setTitleTextSize(float size) {
		this.titleTitleSize = size;
		if (mTitleTextView != null && size > 0) {
			mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
		}
		return this;
	}
	public SweetAlertDialog setContentTextColor(int color) {
		contentTextColor = color;
		if (mContentTextView != null && color != 0) {
			mContentTextView.setTextColor(color);
		}
		return this;
	}
	public SweetAlertDialog setContentTextSize(float size) {
		this.contentTextSize = size;
		if (mContentTextView != null && size > 0) {
			mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
		}
		return this;
	}
	public SweetAlertDialog setCancelTextColor(int color) {
		cancelTextColor = color;
		if (mCancelButton != null && color != 0) {
			mCancelButton.setTextColor(color);
		}
		return this;
	}
	private boolean isRunningTop(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getClass().getCanonicalName();
		Log.e("------------------", currentPackageName);
		Log.e("-===========--", context.getClass().getCanonicalName());

		return !TextUtils.isEmpty(currentPackageName)
				&& currentPackageName.equals(context.getClass().getCanonicalName());

	}
}