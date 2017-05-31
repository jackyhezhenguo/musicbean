package com.musicbean.ui.live;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.musicbean.R;

import java.lang.ref.WeakReference;

/**
 * Created by boyo on 16/7/3.
 */
public abstract class BottomPopupWindow extends PopupWindow {

    protected WeakReference<Activity> mActivityRef;

    public BottomPopupWindow(Activity context) {
        super(context);

        mActivityRef = new WeakReference<>(context);

        View view = LayoutInflater.from(context).inflate(getContentViewId(), null);
        setContentView(view);

        setSize();

        setAnimationStyle(R.style.AnimationPreviewBottom);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0x00ffffff));
        setOutsideTouchable(true);

        initViews();
    }

    protected abstract void setSize();

    protected abstract int getContentViewId();

    protected void initViews() {

    }

    public void show() {
        if (!isShowing()) {
            showAtLocation(mActivityRef.get().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        }
    }
}
