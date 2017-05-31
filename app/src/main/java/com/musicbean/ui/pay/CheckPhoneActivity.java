package com.musicbean.ui.pay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.PayApi;
import com.musicbean.ui.BackActivity;
import com.musicbean.ui.settings.BindPhoneActivity;

/**
 * Created by boyo on 16/11/25.
 */
public class CheckPhoneActivity extends BindPhoneActivity implements BackActivity.TitleBarRightButtonCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("手机号验证");
        setRightButtonTitle("提现记录");
        setCallback(this);

    }

    @Override
    public void onRightButtonClick(View view) {
        startActivity(new Intent(this, RecordListActivity.class));
    }

    @Override
    public void onRightButtonStatusChange(CompoundButton button, boolean status) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_getcash_checkphone;
    }

    @Override
    protected void onCommit(String phone, String code) {
        // TODO
        PayApi.checkCaptcha(this, phone, code, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                startActivity(new Intent(CheckPhoneActivity.this, BindAccountActivity.class));
                finish();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {
                Toast.makeText(CheckPhoneActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                hideDialog();
            }
        });

    }
}
