package com.musicbean.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by boyo on 16/6/14.
 */
public class WrapContentGridView extends GridView {

    public WrapContentGridView(Context context) {
        super(context);
    }

    public WrapContentGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapContentGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
