package com.jzxiang.pickerview;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.jzxiang.pickerview.adapters.NumericWheelAdapter;
import com.jzxiang.pickerview.config.PickerConfig;
import com.jzxiang.pickerview.wheel.OnWheelChangedListener;
import com.jzxiang.pickerview.wheel.OnWheelScrollListener;
import com.jzxiang.pickerview.wheel.WheelView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jzxiang on 16/4/20.
 */
public class TimeWheel {
    Context mContext;

    WheelView  day, hour, minute;
    NumericWheelAdapter mDayAdapter, mHourAdapter, mMinuteAdapter;

    PickerConfig pickerConfig;
    public static final String FORMAT = "%02d";
    public ArrayList<String> daySource,minuteSource,hourSource;
    private int dayPosition,hourPosition,minutePosition;
    private int minDay,minHour,minMinute;
    private long minTimeInMillis;
    OnWheelChangedListener dayListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateDays(newValue);
        }
    };
    OnWheelChangedListener hourListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateHours(newValue);
        }
    };
    OnWheelChangedListener minuteListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateMinutes(newValue);
        }
    };

    public TimeWheel(View view, PickerConfig pickerConfig) {
        this.pickerConfig = pickerConfig;

        mContext = view.getContext();
        initialize(view);
    }

    public void initialize(View view) {
        initView(view);

        daySource = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.add(Calendar.MINUTE,15);
        calendar.set(Calendar.SECOND,0);
        minTimeInMillis = calendar.getTimeInMillis();
        pickerConfig.currentTimeMillseconds = Math.max(pickerConfig.currentTimeMillseconds,minTimeInMillis);
        minMinute = calendar.get(Calendar.MINUTE)/15;
        minHour = calendar.get(Calendar.HOUR_OF_DAY);
        minDay = 0;
        SimpleDateFormat format = new SimpleDateFormat(pickerConfig.mDay);
        if (today == calendar.get(Calendar.DAY_OF_YEAR)) {
            daySource.add("今天");
        }
        else {
            daySource.add(format.format(calendar.getTime()));
        }

        for (int i = 1;i < pickerConfig.dayNumber;i++){
            calendar.add(Calendar.DAY_OF_YEAR,1);
            daySource.add(format.format(calendar.getTime()));
        }

        hourSource = getHourSource();
        minuteSource = getMinuteSource();

        calendar.setTimeInMillis(pickerConfig.currentTimeMillseconds);
        String currentDay = format.format(calendar.getTime());
        for (int i = 0;i<daySource.size();i++){
            if (currentDay.equals(daySource.get(i))){
                dayPosition = i;
            }
        }
        minutePosition = calendar.get(Calendar.MINUTE)/15;
        hourPosition = calendar.get(Calendar.HOUR_OF_DAY);

        mDayAdapter = new NumericWheelAdapter(mContext, daySource);
        mDayAdapter.setConfig(pickerConfig);
        day.setViewAdapter(mDayAdapter);
        day.setCurrentItem(dayPosition);

        mHourAdapter = new NumericWheelAdapter(mContext, hourSource);
        mHourAdapter.setConfig(pickerConfig);
        hour.setViewAdapter(mHourAdapter);
        hour.setCurrentItem(hourPosition);

        mMinuteAdapter = new NumericWheelAdapter(mContext,minuteSource);
        mMinuteAdapter.setConfig(pickerConfig);
        minute.setViewAdapter(mMinuteAdapter);
        minute.setCurrentItem(minutePosition);

    }

    private ArrayList<String> getHourSource(){
        if (hourSource == null){
            hourSource = new ArrayList<>();
            for (int j = 0 ; j < 24;j++){
                hourSource.add(String.format(FORMAT, j));
            }
        }
        return hourSource;
    }

    private ArrayList<String> getMinuteSource(){
        if (minuteSource == null){
            minuteSource = new ArrayList<>();
            for (int j = 0 ; j < 4;j++){
                minuteSource.add(String.format(FORMAT, j*15));
            }
        }
        return minuteSource;
    }

    void initView(View view) {
        day = (WheelView) view.findViewById(R.id.day);
        hour = (WheelView) view.findViewById(R.id.hour);
        minute = (WheelView) view.findViewById(R.id.minute);

//        day.addChangingListener(dayListener);
//        minute.addChangingListener(minuteListener);
//        hour.addChangingListener(hourListener);
        day.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                updateDays(wheel.getCurrentItem());
            }
        });
        hour.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                updateHours(wheel.getCurrentItem());
            }
        });
        minute.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                updateMinutes(wheel.getCurrentItem());
            }
        });
    }

    void updateDays(int newValue) {
        if (minDay == newValue){
            dayPosition = newValue;
            updateHours(hourPosition);
        }else{
            dayPosition = newValue;
        }

    }

    void updateHours(int newValue) {
        if (minDay == dayPosition){
            hourPosition = Math.max(minHour,newValue);
            hour.setCurrentItem(hourPosition,true);
            updateMinutes(minutePosition);
        }else{
            hourPosition = newValue;
        }

    }

    void updateMinutes(int newValue) {
        if (minDay == dayPosition && minHour == hourPosition){
            minutePosition = Math.max(minMinute,newValue);
            minute.setCurrentItem(minutePosition,true);
        }else{
            minutePosition = newValue;
        }
    }

    public long getTime(){
        Calendar calendar = Calendar.getInstance();
        Log.e("------",dayPosition+","+hourPosition+","+minutePosition);
        calendar.setTimeInMillis(minTimeInMillis);
        calendar.add(Calendar.DAY_OF_YEAR,dayPosition);
        calendar.set(Calendar.HOUR_OF_DAY,hourPosition);
        calendar.set(Calendar.MINUTE,minutePosition*15);
        calendar.set(Calendar.SECOND,0);
//        SimpleDateFormat localFormater = new SimpleDateFormat(pickerConfig.mDay+"HHmm");
//        localFormater.setTimeZone(TimeZone.getDefault());
//        try {
//
//            String dayString = daySource.get(dayPosition);
//            if (dayPosition == 0){
//                SimpleDateFormat format = new SimpleDateFormat(pickerConfig.mDay);
//                calendar.setTimeInMillis(minTimeInMillis);
//                dayString = format.format(calendar.getTime());
//            }
//            Date date = localFormater.parse(dayString+hourSource.get(hourPosition)+minuteSource.get(minutePosition));
//            calendar.setTime(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return calendar.getTimeInMillis();
    }
}
