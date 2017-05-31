package com.musicbean.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.musicbean.R;
import com.musicbean.util.ScreenUtils;


/**
 * Created by boyo on 16/2/27.
 */
public class TabItemButton extends RadioButton {

    private Paint mPaint;

    private int mLinePadding;
    private int mLineWidth;

    public TabItemButton(Context context) {
        this(context, null);
    }

    public TabItemButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.custom_green));
        mLinePadding = ScreenUtils.dp2px(getContext(), 0);
        mLineWidth = ScreenUtils.dp2px(getContext(), 2f);
    }

    public Size getSize() {
        if (mPaint != null) {
            Size s = new Size();
            mPaint.setTextSize(getTextSize());
            String txt = getText().toString();
            s.width = mPaint.measureText(txt);
            Paint.FontMetrics fm = mPaint.getFontMetrics();
            s.height = (float) (Math.ceil(fm.descent - fm.top) + 2);
            return s;
        }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint != null) {

            if (isChecked()) {
                Size size = getSize();
                float width = getWidth();
                float startX = (width - size.width) / 2.0f - mLinePadding;
                float stopX = startX + size.width + mLinePadding * 2;
                float startY = getHeight() - mLineWidth / 2;
                float stopY = startY;
                mPaint.setStrokeWidth(mLineWidth);
                mPaint.setColor(getResources().getColor(R.color.custom_green));
                canvas.drawLine(startX, startY, stopX, stopY, mPaint);
            }
        }
    }

    public class Size {
        float width;
        float height;
    }
}
