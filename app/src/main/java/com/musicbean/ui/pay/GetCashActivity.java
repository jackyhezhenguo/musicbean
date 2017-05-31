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

import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.PayApi;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BackActivity;

import org.w3c.dom.Text;

public class GetCashActivity extends BackActivity implements BackActivity.TitleBarRightButtonCallback, TextWatcher {

    private EditText mEditBeans;
    private TextView mTxtMoney;
    private TextView mAccount;
    private EditText mName;

    private static final int EXCHANGE_RATE = 75;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_cash);

        setTitle("提现申请");

        setRightButtonTitle("提现记录");
        setCallback(this);

        mEditBeans = (EditText) findViewById(R.id.edt_beans);
        mTxtMoney = (TextView) findViewById(R.id.txt_money);
        mAccount = (TextView) findViewById(R.id.account);
        mName = (EditText) findViewById(R.id.name);

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
                startActivity(new Intent(GetCashActivity.this, ExchangeDiamondActivity.class));
            }
        });

        mEditBeans.setText(LoginManager.getInstance().getUserInfo().bean + "");
        mEditBeans.setSelection(mEditBeans.getText().length());
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

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString())) {
            return;
        }

        try {
            int bnum = Integer.parseInt(s.toString());
            int b = bnum / EXCHANGE_RATE;

            mTxtMoney.setText(b + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    private boolean checkBeans() {
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
            mTxtMoney.setText(bnum + "");
        }

        return true;
    }

    private void commit() {
        if (!checkBeans()) {
            return;
        }

        String account = mAccount.getText().toString();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "请输入支付宝账户", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = mName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }

        showDialog();
        PayApi.getCash(this, mEditBeans.getText().toString(), mTxtMoney.getText().toString(),
                mName.getText().toString(), mAccount.getText().toString(),
                new ResponseHandler() {
                    @Override
                    public void onSuccess(String data) {
                        Toast.makeText(GetCashActivity.this, "提现申请审核通过后，提现金额将会在一个工作日打入您支付宝账户中", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int responseCode, String errorMsg) {
                        Toast.makeText(GetCashActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        hideDialog();
                    }
                });
    }
}
