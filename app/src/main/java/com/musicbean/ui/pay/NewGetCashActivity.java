package com.musicbean.ui.pay;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.PayApi;
import com.musicbean.manager.LoginManager;
import com.musicbean.preference.GetCashAccount;
import com.musicbean.preference.MySettings;
import com.musicbean.preference.PreferencesManager;
import com.musicbean.ui.BackActivity;

public class NewGetCashActivity extends BackActivity implements BackActivity.TitleBarRightButtonCallback, TextWatcher,
        View.OnClickListener {

    private EditText mEditBeans;
    private TextView mTxtBeans;
    private TextView mTxtMoney;
    private TextView mGetMoney;
    private TextView mTxtAccount;

    private GetCashAccount mAccount;

    private TextView mBtnBind;

    private static final int EXCHANGE_RATE = 75;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getcash);

        setTitle("提现申请");

        setRightButtonTitle("提现记录");
        setCallback(this);

        mEditBeans = (EditText) findViewById(R.id.edt_beans);
        mTxtBeans = (TextView) findViewById(R.id.txt_beans);
        mTxtMoney = (TextView) findViewById(R.id.txt_money);
        mGetMoney = (TextView) findViewById(R.id.get_money);
        mTxtAccount = (TextView) findViewById(R.id.account);

        mBtnBind = (TextView) findViewById(R.id.btn_bind);
        mBtnBind.setOnClickListener(this);

        mEditBeans.addTextChangedListener(this);
        mEditBeans.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkBeans();
                }
            }
        });

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });

        findViewById(R.id.btn_exchange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewGetCashActivity.this, ExchangeDiamondActivity.class));
            }
        });

        int mbeans = LoginManager.getInstance().getUserInfo().bean;
        mTxtBeans.setText(mbeans + "");
        mTxtMoney.setText(exchangeBeans(mbeans) + "");

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_bind) {
            startActivity(new Intent(this, CheckPhoneActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        MySettings set = (MySettings) PreferencesManager.getSettings();
        if (!TextUtils.isEmpty(set.getcashAccount)) {
            Gson gson = new Gson();
            mAccount = gson.fromJson(set.getcashAccount, GetCashAccount.class);

            mTxtAccount.setText("支付宝    " + mAccount.account + "(" + mAccount.name + ")");

            mBtnBind.setText("更改");
        } else {
            mBtnBind.setText("绑定");
        }
    }

    @Override
    public void onRightButtonClick(View view) {
        startActivity(new Intent(this, RecordListActivity.class));
    }

    @Override
    public void onRightButtonStatusChange(CompoundButton button, boolean status) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    private int mCurMoney = 0;

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString())) {
            return;
        }

        try {
            int bnum = Integer.parseInt(s.toString());
            mCurMoney = bnum / EXCHANGE_RATE;

            mGetMoney.setText(mCurMoney + "元");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    private boolean checkBeans() {
        if (TextUtils.isEmpty(mEditBeans.getText())) {
            Toast.makeText(this, "请输入提现的音悦豆数量", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (LoginManager.getInstance().getUserInfo().bean < 300) {
            Toast.makeText(this, "音悦豆小于300，不能提现", Toast.LENGTH_SHORT).show();
            return false;
        }

        int num = Integer.parseInt(mEditBeans.getText().toString());
        int bnum = num / EXCHANGE_RATE;
        if (bnum == 0) {
            Toast.makeText(this, "至少75个豆才能兑换一元!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (num > LoginManager.getInstance().getUserInfo().bean) {
            Toast.makeText(this, "您的音悦豆不足，无法兑换", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (bnum * EXCHANGE_RATE != num) {
            mEditBeans.setText(bnum * EXCHANGE_RATE + "");
            mGetMoney.setText(bnum + "");
        }

        return true;
    }

    private int exchangeBeans(int beans) {
        return beans / EXCHANGE_RATE;
    }

    private void commit() {
        if (mAccount == null) {
            Toast.makeText(this, "请绑定支付宝账户", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkBeans()) {
            return;
        }


        showDialog();
        PayApi.getCash(this, mEditBeans.getText().toString(), mCurMoney + "",
                mAccount.name, mAccount.account,
                new ResponseHandler() {
                    @Override
                    public void onSuccess(String data) {
                        Toast.makeText(NewGetCashActivity.this, "提现申请审核通过后，提现金额将会在一个工作日打入您支付宝账户中", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int responseCode, String errorMsg) {
                        Toast.makeText(NewGetCashActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        hideDialog();
                    }
                });
    }
}
