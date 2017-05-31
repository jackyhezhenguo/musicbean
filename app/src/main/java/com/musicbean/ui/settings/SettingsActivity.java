package com.musicbean.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BackActivity;
import com.musicbean.util.AppInfo;

/**
 * Created by boyo on 16/7/3.
 */
public class SettingsActivity extends BackActivity implements View.OnClickListener {
    private TextView mBindPhoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("设置");

        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.feedback_layout).setOnClickListener(this);
        findViewById(R.id.msg_push_layout).setOnClickListener(this);
        findViewById(R.id.bind_phone_layout).setOnClickListener(this);

        TextView tv = (TextView) findViewById(R.id.version);
        tv.setText("V" + AppInfo.getAppVersionName(this));

        mBindPhoneText = (TextView) findViewById(R.id.bind_phone_txt);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (LoginManager.getInstance().isLogin()) {
            String phone = LoginManager.getInstance().getUserCookie().userinfo.phone;
            if (TextUtils.isEmpty(phone)) {
                mBindPhoneText.setText("未绑定手机，安全等级低");
            } else {
                mBindPhoneText.setText(phone);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_phone_layout:
                String phone = LoginManager.getInstance().getUserCookie().userinfo.phone;
                if (!TextUtils.isEmpty(phone)) {
                    return;
                }

                startActivity(new Intent(this, BindPhoneActivity.class));
                break;
            case R.id.feedback_layout:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.msg_push_layout:
                Intent intent = new Intent(this, NotificationActivity.class);
                intent.putExtra("uid", LoginManager.getInstance().getUserInfo().id);
                startActivity(intent);
                break;
            case R.id.btn_logout:
                UserApi.logout(this, new ResponseHandler() {
                    @Override
                    public void onSuccess(String data) {
                        LoginManager.getInstance().onLogout();
                    }

                    @Override
                    public void onFailure(int responseCode, String errorMsg) {

                    }
                });
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
