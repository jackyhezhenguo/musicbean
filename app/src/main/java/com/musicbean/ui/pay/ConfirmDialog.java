package com.musicbean.ui.pay;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.musicbean.R;

/**
 * Created by boyo on 16/7/23.
 */
public class ConfirmDialog extends Dialog implements View.OnClickListener {

    private TextView mMessage;

    public ConfirmDialog(Context context, int theme) {
        super(context, R.style.Dialog_No_Board);
    }

    public ConfirmDialog(Context context) {
        super(context, R.style.Dialog_No_Board);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        mMessage = (TextView) findViewById(R.id.message);
        if (!TextUtils.isEmpty(mMsg)) {
            mMessage.setText(mMsg);
        }

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    private String mMsg;

    public void show(String msg) {
        mMsg = msg;
        if (!TextUtils.isEmpty(mMsg) && mMessage != null) {
            mMessage.setText(mMsg);
        }
        super.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (mListener != null) {
                    mListener.onEnsure();
                }
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    public interface OnDialogListener {
        void onEnsure();
    }

    private OnDialogListener mListener;

    public void setOnDialogListener(OnDialogListener l) {
        mListener = l;
    }
}
