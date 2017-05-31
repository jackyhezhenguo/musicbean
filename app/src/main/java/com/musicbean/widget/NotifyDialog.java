package com.musicbean.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.musicbean.R;

/**
 * Created by boyo on 16/7/23.
 */
public class NotifyDialog extends Dialog {

    private TextView mMessage;

    public NotifyDialog(Context context, int theme) {
        super(context, R.style.Dialog_No_Board);
    }

    public NotifyDialog(Context context) {
        super(context, R.style.Dialog_No_Board);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notify);
        mMessage = (TextView) findViewById(R.id.message);
        if (!TextUtils.isEmpty(mMsg)) {
            mMessage.setText(mMsg);
        }
    }

    private String mMsg;

    public void show(String msg) {
        mMsg = msg;
        if (!TextUtils.isEmpty(mMsg) && mMessage != null) {
            mMessage.setText(mMsg);
        }
        super.show();
    }
}
