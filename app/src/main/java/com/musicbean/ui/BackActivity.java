package com.musicbean.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.HttpUtil;

public class BackActivity extends BaseActivity {

    protected TextView mTitleTextView = null;
    protected CheckBox mRightCheckBox = null;
    protected ImageView mBackImg = null;
    private TitleBarRightButtonCallback mCallback = null;

    public interface TitleBarRightButtonCallback {
        public void onRightButtonClick(View view);

        public void onRightButtonStatusChange(CompoundButton button, boolean status);
    }

    protected void setCallback(TitleBarRightButtonCallback callback) {
        mCallback = callback;
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            switch (view.getId()) {
                case R.id.back: {
                    onBackClick();
                }
                break;
                case R.id.rightCheckBox: {
                    if (mCallback != null) {
                        mCallback.onRightButtonClick(view);
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton button, boolean status) {
            // TODO Auto-generated method stub
            switch (button.getId()) {
                case R.id.rightCheckBox: {
                    if (mCallback != null) {
                        mCallback.onRightButtonStatusChange(button, status);
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("InflateParams")
    protected void initActionBar() {
        ActionBar actionbar = getSupportActionBar();
        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View v = LayoutInflater.from(this).inflate(R.layout.back_actionbar_layout, null);
        mTitleTextView = (TextView) v.findViewById(R.id.titleTextView);
        mRightCheckBox = (CheckBox) v.findViewById(R.id.rightCheckBox);
        mRightCheckBox.setOnClickListener(mClickListener);
        mRightCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mBackImg = (ImageView) v.findViewById(R.id.back);
        mBackImg.setOnClickListener(mClickListener);
        layout.leftMargin = 0;
        layout.rightMargin = 0;
        actionbar.setCustomView(v, layout);
    }

    protected void setRightButtonChecked(boolean checked) {
        if (mRightCheckBox != null) {
            mRightCheckBox.setChecked(checked);
        }
    }

    protected void setRightButtonTitle(CharSequence title) {
        if (mRightCheckBox != null) {
            mRightCheckBox.setText(title);
        }
        setRightButtonVisable();
    }

    protected void setRightButtonTitle(int resid) {
        if (mRightCheckBox != null) {
            mRightCheckBox.setText(resid);
        }
        setRightButtonVisable();
    }

    private void setRightButtonVisable() {
        if (mRightCheckBox.getVisibility() != View.VISIBLE) {
            mRightCheckBox.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置左按钮是否显示
     *
     * @param visible
     */
    public void setRightButtonVisable(boolean visible) {
        int visibilite = visible ? View.VISIBLE : View.GONE;
        mRightCheckBox.setVisibility(visibilite);
    }

    protected void setRightButtonDrawable(Drawable drawable) {
        if (mRightCheckBox != null) {
            mRightCheckBox.setButtonDrawable(drawable);
        }
        setRightButtonVisable();
    }

    protected void setRightButtonDrawable(int resid) {
        if (mRightCheckBox != null) {
            mRightCheckBox.setButtonDrawable(resid);
        }
        setRightButtonVisable();
    }

    private void setTitleViewVisable() {
        if (mTitleTextView.getVisibility() != View.VISIBLE) {
            mTitleTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
        setTitleViewVisable();
    }

    @Override
    public void setTitle(int titleId) {
        if (mTitleTextView != null) {
            mTitleTextView.setText(titleId);
        }
        setTitleViewVisable();
    }

    public void onBackClick() {
        finish();
    }

    public void setRightButtonEnabled(boolean b) {
        if (mRightCheckBox != null) {
            mRightCheckBox.setEnabled(b);
        }
    }

    protected void hideTitlebar() {
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
