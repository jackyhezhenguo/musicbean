package com.musicbean.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.musicbean.App;
import com.musicbean.util.ScreenUtils;

/**
 * Created by boyo on 16/7/19.
 */
public class LevelDrawable extends Drawable {

    private String mUserLevel;
    private Bitmap mBitmap;
    private Paint mPaint;

    private int startx;
    private int starty;

    public LevelDrawable(Bitmap bmp, int level) {
        int width = ScreenUtils.dp2px(App.getContext(), 35);
        int height = ScreenUtils.dp2px(App.getContext(), 12);
        setBounds(0, 0, width, height);
        mBitmap = bmp;
        mPaint = new Paint();
        mPaint.setTextSize(ScreenUtils.dp2px(App.getContext(), 10));
        mPaint.setColor(Color.WHITE);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);

        mUserLevel = level + "";
        Rect rect = new Rect();
        mPaint.getTextBounds(mUserLevel, 0, mUserLevel.length(), rect);

        startx = (int) ((ScreenUtils.dp2px(App.getContext(), 16) - rect.width()) / 2f + ScreenUtils.dp2px(App.getContext(), 16));
        starty = (int) ((height + rect.height()) / 2f);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        canvas.drawText(mUserLevel, startx, starty, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
