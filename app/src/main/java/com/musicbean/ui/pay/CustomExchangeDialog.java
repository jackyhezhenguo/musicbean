package com.musicbean.ui.pay;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.musicbean.R;
import com.musicbean.http.bean.ExchangeInfo;
import com.musicbean.manager.LoginManager;

/**
 * Created by boyo on 16/7/23.
 */
public class CustomExchangeDialog extends Dialog implements View.OnClickListener, TextWatcher {

    public static final int EXCHANGE_RATE = 6;

    private EditText mEdtBeans;
    private TextView mTxtDiamond;

    private TextView mMessage;

    private long mIntBeans;
    private long mIntDiamonds;

    public CustomExchangeDialog(Context context, int theme) {
        super(context, R.style.Dialog_No_Board);
    }

    public CustomExchangeDialog(Context context) {
        super(context, R.style.Dialog_No_Board);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exchange);
        mMessage = (TextView) findViewById(R.id.message);
        if (!TextUtils.isEmpty(mMsg)) {
            mMessage.setText(mMsg);
        }

        mEdtBeans = (EditText) findViewById(R.id.edt_beans);
        mEdtBeans.addTextChangedListener(this);
        mTxtDiamond = (TextView) findViewById(R.id.txt_diamond);

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
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                if (checkBeans()) {
                    if (mListener != null) {
                        if (mIntBeans > LoginManager.getInstance().getUserInfo().bean) {
                            Toast.makeText(getContext(),
                                    "音悦豆不能超过" + LoginManager.getInstance().getUserInfo().bean,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ExchangeInfo info = new ExchangeInfo(mIntBeans, mIntDiamonds);
                        mListener.onExchangeSelected(info);
                    }

                    dismiss();
                }

                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString())) {
            return;
        }

        try {
            int bnum = Integer.parseInt(s.toString());
            int b = bnum / EXCHANGE_RATE;
            mTxtDiamond.setText(b + "钻石");
            mIntDiamonds = b;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    private boolean checkBeans() {
        String str = mEdtBeans.getText().toString();
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        try {
            mIntBeans = Long.parseLong(str);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        long bnum = mIntBeans / EXCHANGE_RATE;
        if (bnum == 0) {
            Toast.makeText(getContext(), "至少6个豆才能兑换1个钻石!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (bnum * EXCHANGE_RATE != mIntBeans) {
            mIntBeans = bnum * EXCHANGE_RATE;
            mEdtBeans.setText(mIntBeans + "");
            mIntDiamonds = bnum;
            mTxtDiamond.setText(bnum + "钻石");
        }

        return true;
    }

    public interface OnExchangeDialogListener {
        void onExchangeSelected(ExchangeInfo info);
    }

    private OnExchangeDialogListener mListener;

    public void setOnExchangeDialogListener(OnExchangeDialogListener l) {
        mListener = l;
    }
}
