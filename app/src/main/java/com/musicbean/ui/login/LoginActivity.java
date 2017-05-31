package com.musicbean.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.musicbean.ui.WebViewActivity;
import com.musicbean.ui.main.MainActivity;
import com.musicbean.util.LogUtils;
import com.qihoo.share.framework.BaseShareAPI;
import com.qihoo.share.framework.ShareCallBackListener;
import com.qihoo.share.framework.ShareParam;
import com.qihoo.share.framework.ShareResult;
import com.qihoo.share.framework.ShareSdk;

import java.lang.reflect.Type;

/**
 * Created by boyo on 16/6/20.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, ShareCallBackListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        findViewById(R.id.qq).setOnClickListener(this);
        findViewById(R.id.wechat).setOnClickListener(this);
        findViewById(R.id.phone).setOnClickListener(this);
        findViewById(R.id.weibo).setOnClickListener(this);
        findViewById(R.id.protocal).setOnClickListener(this);
    }


    String mSite;

    @Override
    public void onClick(View v) {
        BaseShareAPI api;
        switch (v.getId()) {
            case R.id.qq:
                mSite = "qq";
                api = ShareSdk.getShareAPI(ShareSdk.API_NAME.QQ, this);
                api.setCallBackListener(this);
                api.share(new ShareParam(), this);
                api.requestOauth(null, this);
                break;

            case R.id.weibo:
                mSite = "weibo";
                api = ShareSdk.getShareAPI(ShareSdk.API_NAME.Weibo, this);
                api.setCallBackListener(this);
                api.requestOauth(null, this);
                break;

            case R.id.wechat:
                mSite = "weixin";
                api = ShareSdk.getShareAPI(ShareSdk.API_NAME.WXSession, this);
                if (api.isSurpport()) {
                    api.setCallBackListener(this);
                    api.requestOauth(null, this);
                } else {
                    Toast.makeText(this, "没有安装微信或微信版本过低", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.phone:
                startActivityForResult(new Intent(this, PhoneLoginActivity.class), 111);
                //Toast.makeText(this, "手机登录", Toast.LENGTH_SHORT).show();
                break;

            case R.id.protocal:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", "http://musicbean-app-server-dev.obaymax.com/help.html");
                intent.putExtra("title", "音悦豆服务条款");
                startActivity(intent);
                break;

        }
    }

    @Override
    public void callback(ShareResult shareResult) {
        LogUtils.e("LoginActivity", shareResult.resultMsg);
        if (shareResult.resultCode == ShareResult.CODE_SUCCESS) {
            String token = shareResult.oauthInfo.accessTocken;
            LogUtils.e("LoginActivity", "token:" + token);
            LogUtils.e("LoginActivity", "code:" + shareResult.oauthInfo.accessCode);
            if (mSite.equals("weixin")) {
                token = shareResult.oauthInfo.accessCode;
            }
            UserApi.loginByOauth(this, mSite, token, new ResponseHandler() {
                @Override
                public void onSuccess(String data) {
                    Type objectType = new TypeToken<BaseResponse<UserCookie>>() {
                    }.getType();
                    BaseResponse<UserCookie> res = new Gson().fromJson(data, objectType);

                    LoginManager.getInstance().onLogin(res.data);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onFailure(int responseCode, String errorMsg) {
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
