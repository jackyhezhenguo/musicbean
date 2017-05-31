package com.musicbean.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.UserCookie;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BaseActivity;

import java.lang.reflect.Type;

public class PhoneLoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEdtPhone;
    private EditText mEdtCode;
    private Button mBtnGetcode;
    private View mTips;

    private int COUNT_DOWN = 60;
    private int mCountdown;

    private String tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        mEdtCode = (EditText) findViewById(R.id.edt_code);
        mEdtPhone = (EditText) findViewById(R.id.edt_phone);
        mBtnGetcode = (Button) findViewById(R.id.btn_getcode);
        mTips = findViewById(R.id.getcode_tip);

        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        mBtnGetcode.setOnClickListener(this);

        tip = getResources().getString(R.string.click_getcode);

    }

    protected int getLayoutId(){
        return R.layout.activity_phone_login;
    }

    @Override
    public void onClick(View v) {
        String phone = mEdtPhone.getText().toString().trim();
        String code = mEdtCode.getText().toString();
        switch (v.getId()) {
            case R.id.btn_getcode:
                if (TextUtils.isEmpty(phone) || phone.length() != 11) {
                    Toast.makeText(this, "请输入正确手机号码", Toast.LENGTH_SHORT).show();
                } else {
                    //showDialog();

                    mBtnGetcode.setEnabled(false);
                    if (mTips != null) {
                        mTips.setVisibility(View.VISIBLE);
                    }
                    mCountdown = COUNT_DOWN;
                    mBtnGetcode.post(mCountdownRunnable);
                    UserApi.getCaptcha(this, phone, new ResponseHandler() {
                        @Override
                        public void onSuccess(String data) {
                            BaseResponse res = new Gson().fromJson(data, BaseResponse.class);
                            if (res.errorno == 0) {

                            } else {
                                mCountdown = -1;
                                Toast.makeText(PhoneLoginActivity.this, res.errmsg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int responseCode, String errorMsg) {
                            mCountdown = -1;
                        }

                        @Override
                        public void onFinish() {
                            //hideDialog();
                        }
                    });
                }
                break;
            case R.id.btn_ok:
                if (TextUtils.isEmpty(phone) || phone.length() != 11) {
                    Toast.makeText(this, "请输入正确手机号码", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(code)) {
                    Toast.makeText(this, "请输入验证吗", Toast.LENGTH_SHORT).show();
                } else {
                    showDialog();
                    onCommit(phone, code);
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    protected void onCommit(String phone, String code) {
        UserApi.loginByPhone(this, phone, code, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<UserCookie>>() {
                }.getType();
                BaseResponse<UserCookie> res = new Gson().fromJson(data, objectType);

                LoginManager.getInstance().onLogin(res.data);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {
                Toast.makeText(PhoneLoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                hideDialog();
            }
        });
    }

    private Runnable mCountdownRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCountdown > 0) {
                mCountdown--;
                mBtnGetcode.setText(tip + "(" + mCountdown + ")");
                mBtnGetcode.postDelayed(mCountdownRunnable, 1000);
            } else {
                mCountdown = COUNT_DOWN;
                mBtnGetcode.setEnabled(true);
                mBtnGetcode.setText(tip);
            }
        }
    };
}
