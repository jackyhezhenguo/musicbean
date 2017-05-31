package com.musicbean.widget.cropimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ClipShadowView extends View {
    private final int SHADOW_COLOR = 0x7f000000;
    private int mClipWidth = 0;
    private int mClipHeight = 0;
    private Bitmap mRectBitmap; // 用于背景缓存
    private Paint mEmptyPaint = new Paint();

    public ClipShadowView(Context context) {
        super(context);
    }

    public ClipShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipShadowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshRectBitmap();
    }

    /**
     * 刷新全局背景
     */
    private void refreshRectBitmap() {
        Bitmap recBitmap = mRectBitmap;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Paint transparentPaint;
        mRectBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(mRectBitmap);
        RectF clipRect = getClipRect();
        paint.setColor(SHADOW_COLOR);
        temp.drawRect(0, 0, temp.getWidth(), temp.getHeight(), paint);

        Paint arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setColor(Color.WHITE);
        arcPaint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.SRC_OVER));
        temp.drawRect(clipRect, arcPaint);
        //temp.drawCircle((clipRect.left + clipRect.right) / 2,
        //        (clipRect.top + clipRect.bottom) / 2,
        //        ((clipRect.right - clipRect.left) / 2) + 2, arcPaint);

        transparentPaint = new Paint();
        transparentPaint.setAntiAlias(true);
        transparentPaint.setColor(getResources().getColor(
                android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(
                PorterDuff.Mode.CLEAR));
        temp.drawRect(clipRect, transparentPaint);
        //temp.drawCircle((clipRect.left + clipRect.right) / 2,
        //        (clipRect.top + clipRect.bottom) / 2,
        //        (clipRect.right - clipRect.left) / 2, transparentPaint);


        if (recBitmap != null) {
            recBitmap.recycle();
            recBitmap = null;
        }
    }

    /**
     * 获取截取区域位置信息
     *
     * @return
     */
    public RectF getClipRect() {
        RectF result = new RectF();
        int width = this.getWidth();
        int height = this.getHeight();
        if (mClipWidth != 0 && mClipHeight != 0) {
            int x = (int) ((width - mClipWidth) / 2);
            int y = (int) ((height - mClipHeight) / 2);
            // int y = 1;
            if (x > 0 && y > 0) {
                result.set(x, y, x + mClipWidth, y + mClipHeight);
            } else {
                Log.e("ClipView", "Clip cal err");
            }
        }
        return result;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRectBitmap != null) {
            canvas.drawBitmap(mRectBitmap, 0, 0, mEmptyPaint);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRectBitmap != null) {
            mRectBitmap.recycle();
        }
    }

    /**
     * 设置宽高
     *
     * @param clipViewWidth
     * @param clipViewHeight
     */
    public void setSize(int clipViewWidth, int clipViewHeight) {
        mClipWidth = clipViewWidth;
        mClipHeight = clipViewHeight;
    }

}
