package com.ppbike.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.ppbike.R;

/**
 * Created by chengmingyan on 16/6/15.
 */
public class LoadingDialog extends Dialog{

    RotateAnimation rotateAni;
    private View loading_progress;
    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_dialog, null);

        loading_progress = v.findViewById(R.id.icon_dialog);
        rotateAni = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAni.setDuration(1000);
        rotateAni.setRepeatCount(-1);
        LinearInterpolator lirInterpolator = new LinearInterpolator();
        rotateAni.setInterpolator(lirInterpolator);
        rotateAni.setFillAfter(true);
        rotateAni.setRepeatMode(Animation.RESTART);

        setContentView(v);
    }

    @Override
    public void show() {
        super.show();
        loading_progress.post(new Runnable() {
            @Override
            public void run() {
                rotateAni.start();
                loading_progress.setAnimation(rotateAni);
            }
        });
    }

    @Override
    public void dismiss() {
        rotateAni.cancel();
        super.dismiss();
    }
}
