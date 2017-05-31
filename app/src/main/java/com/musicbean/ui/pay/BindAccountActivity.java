package com.musicbean.ui.pay;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.musicbean.R;
import com.musicbean.preference.GetCashAccount;
import com.musicbean.preference.MySettings;
import com.musicbean.preference.PreferencesManager;
import com.musicbean.ui.BackActivity;

/**
 * Created by boyo on 16/11/26.
 */
public class BindAccountActivity extends BackActivity implements BackActivity.TitleBarRightButtonCallback, View.OnClickListener {

    private EditText mEdtAccount;
    private EditText mEdtName;
    private Button mBtnBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bind_alipay);

        setTitle("提现账户绑定");
        setRightButtonTitle("提现记录");
        setCallback(this);

        mEdtAccount = (EditText) findViewById(R.id.edt_account);
        mEdtName = (EditText) findViewById(R.id.edt_name);
        mBtnBind = (Button) findViewById(R.id.btn_bind);
        mBtnBind.setOnClickListener(this);

        TextView tv = (TextView) findViewById(R.id.pay_warn_txt);
        tv.setText(Html.fromHtml(getString(R.string.pay_warn_msg)));
    }

    @Override
    public void onRightButtonClick(View view) {
        startActivity(new Intent(this, RecordListActivity.class));
    }

    @Override
    public void onRightButtonStatusChange(CompoundButton button, boolean status) {

    }

    @Override
    public void onClick(View v) {
        String account = mEdtAccount.getText().toString();
        String name = mEdtName.getText().toString();

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入账号和姓名", Toast.LENGTH_SHORT).show();
            return;
        }

        GetCashAccount gcAcctount = new GetCashAccount();
        gcAcctount.account = account;
        gcAcctount.name = name;
        MySettings set = (MySettings) PreferencesManager.getSettings();
        set.getcashAccount = new Gson().toJson(gcAcctount);
        set.sync();

        finish();
    }
}
