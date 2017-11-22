package com.ppbike.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.ppbike.R;
import com.ppbike.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;

import cn.master.util.utils.ScreenUtil;

/**
 * Created by chengmingyan on 16/7/28.
 */
public class TimePopupWindow extends PopupWindow {

    private  PickerView dayView,hourView,minuteView;
    private int dayPosition,hourPosition,minutePosition;
    private OnTimeUpdateListener listener;
    private ArrayList<String> dayArray,hourArray,minuteArray;
    public interface OnTimeUpdateListener {
        void update(long time);
    }
    public TimePopupWindow(Context context,OnTimeUpdateListener listener) {
        super(context);
        this.listener = listener;
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        int mScreenWidth = ScreenUtil.getScreenWidth(context);
        int mScreenHeight = ScreenUtil.getScreenHeight(context);

        setWidth(mScreenWidth);
        setHeight(mScreenHeight/3);

        setBackgroundDrawable(new ColorDrawable(0x66000000));

        setContentView(LayoutInflater.from(context).inflate(R.layout.windows_time_packer, null));

        initView(getContentView());
    }

    private void initView(View contentView){
        Button btn_enter = (Button) contentView.findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String day = dayArray.get(dayPosition);
//                if (dayPosition == 0){
//                    day = TimeUtil.systemTime2LocalTime(calendar.getTimeInMillis(),"MM月dd日 EE");
//                }
                long time = TimeUtil.localTime2utcTime(day+" "+hourArray.get(hourPosition)+minuteArray.get(minutePosition),"MM月dd日EE HHmm");
                listener.update(time);
                dismiss();
            }

        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,1);
        calendar.set(Calendar.MINUTE,0);
        hourPosition = calendar.get(Calendar.HOUR_OF_DAY) - 1;
        dayArray = getDays(calendar.getTimeInMillis());
        hourArray = getHours();
        minuteArray = getMinutes();

        dayView = (PickerView) contentView.findViewById(R.id.pickerView_province);
        dayView.setData(dayArray);
        dayView.setSelected(dayPosition);
        dayView.setmMaxTextSize(17);
        dayView.setmMinTextSize(17);
        dayView.setMARGIN_ALPHA(3.3f);
        dayView.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(PickerView v, String text) {
                for(int i = 0; i < dayArray.size();i++){
                    if (text.equals(dayArray.get(i))){
                        dayPosition = i;
                        break;
                    }
                }
            }
        });

        hourView = (PickerView) contentView.findViewById(R.id.pickerView_city);
        hourView.setData(hourArray);
        hourView.setSelected(hourPosition);
        dayView.setmMaxTextSize(17);
        dayView.setmMinTextSize(17);
        dayView.setMARGIN_ALPHA(3.3f);
        hourView.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(PickerView v, String text) {
                for(int i = 0; i < hourArray.size();i++){
                    if (text.equals(hourArray.get(i))){
                        hourPosition = i;
                        break;
                    }
                }
            }
        });
        minuteView = (PickerView) contentView.findViewById(R.id.pickerView_region);
        minuteView.setData(minuteArray);
        minuteView.setSelected(minutePosition);
        dayView.setmMaxTextSize(17);
        dayView.setmMinTextSize(17);
        dayView.setMARGIN_ALPHA(3.3f);
        minuteView.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(PickerView v, String text) {
                for(int i = 0; i < minuteArray.size();i++){
                    if (text.equals(minuteArray.get(i))){
                        minutePosition = i;
                        break;
                    }
                }
            }
        });
    }

    public void show(View view,long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String day = TimeUtil.systemTime2LocalTime(calendar.getTimeInMillis(),"MM月dd日 EE");
        for(int i = 0; i < dayArray.size();i++){
            if (day.equals(dayArray.get(i))){
                dayPosition = i;
                break;
            }
        }
        hourPosition = calendar.get(Calendar.HOUR_OF_DAY) - 1;
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        for(int i = 0; i < minuteArray.size();i++){
            if (minute.equals(minuteArray.get(i))){
                minutePosition = i;
                break;
            }
        }
        dayView.setSelected(dayPosition);
        hourView.setSelected(hourPosition);
        minuteView.setSelected(minutePosition);

        showAsDropDown(view);
    }

    private ArrayList<String> getDays(long time){
        ArrayList<String> days = new ArrayList<>();
//        days.add("今天");
        Calendar calendar = Calendar.getInstance();
        for(int i = 0;i < 30; i++){
            String day = TimeUtil.systemTime2LocalTime(calendar.getTimeInMillis(),"MM月dd日 EE");
            days.add(day);
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        return days;
    }

    private ArrayList<String> getHours(){
        if (hourArray == null){
            hourArray = new ArrayList<>();
            for(int i = 0;i < 24; i++){
                if (i < 10){
                    hourArray.add("0"+i);
                }else{
                    hourArray.add(String.valueOf(i));
                }
            }
        }
        return hourArray;
    }

    private ArrayList<String> getMinutes(){
        if (minuteArray == null){
            minuteArray = new ArrayList<>();
            minuteArray.add("00");
            minuteArray.add("15");
            minuteArray.add("30");
            minuteArray.add("45");
        }
        return minuteArray;
    }
}
