package com.ppbike.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by chengmingyan on 10/22/15.
 */
public class StillListView extends ListView {
    public StillListView(Context context) {
        super(context);
    }

    public StillListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StillListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
