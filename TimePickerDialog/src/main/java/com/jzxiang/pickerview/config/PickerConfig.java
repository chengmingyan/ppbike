package com.jzxiang.pickerview.config;

import com.jzxiang.pickerview.listener.OnDateSetListener;


/**
 * Created by jzxiang on 16/5/15.
 */
public class PickerConfig {

    public int mThemeColor = DefaultConfig.COLOR;

    public String mCancelString = DefaultConfig.CANCEL;
    public String mSureString = DefaultConfig.SURE;
    public String mTitleString = DefaultConfig.TITLE;
    public int mToolBarTVColor = DefaultConfig.TOOLBAR_TV_COLOR;

    public int mWheelTVNormalColor = DefaultConfig.TV_NORMAL_COLOR;
    public int mWheelTVSelectorColor = DefaultConfig.TV_SELECTOR_COLOR;
    public int mWheelTVSize = DefaultConfig.TV_SIZE;
    public boolean cyclic = DefaultConfig.CYCLIC;

    public String mDay = DefaultConfig.DAY_FORMAT;

    /**
     * The max timeMillseconds
     * parms  day
     */
    public int dayNumber = 30;

    /**
     * The default selector timeMillseconds
     */
    public long currentTimeMillseconds = System.currentTimeMillis();

    public OnDateSetListener mCallBack;
}
