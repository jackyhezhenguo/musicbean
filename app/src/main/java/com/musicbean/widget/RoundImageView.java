package com.musicbean.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.musicbean.R;


public class RoundImageView extends ImageView {

    private Paint mPaint;
    private Paint mEdgePaint;
    private float mEdgeSize;

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        mEdgeSize = a.getDimensionPixelSize(R.styleable.RoundImageView_edgesize, 0);
        a.recycle();
        // TODO Auto-generated constructor stub
    }

    public RoundImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void setEdge(float edge) {
        mEdgeSize = edge > 0 ? edge : 0;
    }

    private void updateImage() {
        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
                    TileMode.CLAMP);
            if (mPaint == null) {
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
            }
            mPaint.setShader(shader);
            if (mEdgePaint == null) {
                mEdgePaint = new Paint();
                mEdgePaint.setAntiAlias(true);
                mEdgePaint.setColor(getResources().getColor(R.color.custom_green));
            }
        }
    }

    private Bitmap getBitmap() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof TransitionDrawable) {
            TransitionDrawable drawable1 = (TransitionDrawable) drawable;
            drawable = drawable1.getDrawable(drawable1.getNumberOfLayers() > 1 ? drawable1.getNumberOfLayers() - 1 : 0);
        }
        if ((drawable instanceof BitmapDrawable) ||
                (drawable.getClass().getSimpleName().equalsIgnoreCase("AsyncDrawable"))) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null) {
                return bitmap;
            }
        }
        return null;
    }

    @Override
    public void setImageResource(int resId) {
        // TODO Auto-generated method stub
        super.setImageResource(resId);
        updateImage();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        // TODO Auto-generated method stub
        super.setImageBitmap(bm);
        updateImage();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        // TODO Auto-generated method stub
        super.setImageDrawable(drawable);
        updateImage();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            int halfWidth = getWidth() / 2;
            int halfHeight = getHeight() / 2;
            int halfBitmapWidth = bitmap.getWidth() / 2, halfBitmapHeight = bitmap
                    .getHeight() / 2;
            float r = Math.min(halfWidth, halfHeight);
            float R = Math.min(halfBitmapWidth, halfBitmapHeight);
            canvas.save();
            // canvas.translate((w - mBitmapWidth) / 2, (h - mBitmapHeight) /
            // 2);
            // scale
            float scale = r / R;
            canvas.scale(scale, scale);// mBitmapWidth / 2, mBitmapHeight / 2);
            canvas.translate(R - halfBitmapWidth, R - halfBitmapHeight);
            // draw
            if (mEdgeSize > 0) {
                canvas.drawCircle(halfBitmapWidth, halfBitmapHeight, R,
                        mEdgePaint);
            }
            canvas.drawCircle(halfBitmapWidth, halfBitmapHeight,
                    (R - mEdgeSize), mPaint);
            canvas.restore();

            return;
        }
        super.onDraw(canvas);
    }
}
