package com.musicbean.widget.play;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class GestureDetectorController implements OnGestureListener, GestureDetector.OnDoubleTapListener {
    private GestureDetector mGestureDetector = null;
    private ScrollViewType currentType = ScrollViewType.NOTHING;
    private IGestureListener mListener;
    private int mWidth;

    public enum ScrollViewType {
        NOTHING, VERTICAL_RIGHT, VERTICAL_LEFT, HORIZONTAL
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (mListener != null) {
            mListener.onSingleClick(e);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (mListener != null) {
            mListener.onDoubleClick(e);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }


    public GestureDetectorController(Context context) {
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setOnDoubleTapListener(this);
        mWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP
                && currentType != ScrollViewType.NOTHING) {
            mListener.onScrollEnd(currentType, event);
            return true;
        } else {
            return mGestureDetector.onTouchEvent(event);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        currentType = ScrollViewType.NOTHING;
        return true;
    }

    public void setGestureListener(IGestureListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mListener == null) {
            return false;
        }
        switch (currentType) {
            case NOTHING: {
                if (Math.abs(distanceY) > Math.abs(distanceX)) {
                    int left = mWidth / 3;
                    currentType = e1.getX() < left ? ScrollViewType.VERTICAL_LEFT
                            : (e1.getX() > left * 2 ? ScrollViewType.VERTICAL_RIGHT : ScrollViewType.NOTHING);
                } else {
                    currentType = ScrollViewType.HORIZONTAL;
                }
                mListener.onScrollBegin(currentType);
            }
            break;
            case VERTICAL_RIGHT:
                mListener.onScrollRight(distanceY, e2.getY() - e1.getY());
                break;
            case VERTICAL_LEFT:
                mListener.onScrollLeft(distanceY, e2.getY() - e1.getY());
                break;
            case HORIZONTAL:
                mListener.onScrollHorizontal(distanceX, e2.getX() - e1.getX());
                break;
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    public interface IGestureListener {

        void onScrollBegin(ScrollViewType type);

        void onScrollEnd(ScrollViewType type, MotionEvent e);

        void onScrollLeft(float distanceY, float moveY);

        void onScrollRight(float distanceY, float moveY);

        void onScrollHorizontal(float distanceX, float moveX);

        void onSingleClick(MotionEvent e);

        void onDoubleClick(MotionEvent e);
    }

}
