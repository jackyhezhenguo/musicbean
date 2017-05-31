package com.musicbean.ui.live;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.NetworkUtil;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.LiveApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.http.bean.LiveInitInfo;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BaseActivity;
import com.qihoo.share.framework.BaseShareAPI;
import com.qihoo.share.framework.ShareCallBackListener;
import com.qihoo.share.framework.ShareParam;
import com.qihoo.share.framework.ShareResult;
import com.qihoo.share.framework.ShareSdk;

import java.lang.reflect.Type;

public class StartLiveActivity extends BaseActivity implements View.OnClickListener, ShareCallBackListener
        , RadioGroup.OnCheckedChangeListener {

    private EditText mEditTitle;
    private ShareSdk.API_NAME mShareName = null;

    private RadioButton mRbWeibo;
    private RadioButton mRbWeixin;
    private RadioButton mRbFriends;
    private RadioButton mRbQq;

    private RadioGroup mRGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_live);

        findViewById(R.id.close).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);

        mEditTitle = (EditText) findViewById(R.id.edt_title);

        mRGroup = (RadioGroup) findViewById(R.id.share_group);
        mRGroup.setOnCheckedChangeListener(this);

        mRbWeixin = (RadioButton) findViewById(R.id.share_wechat);
        mRbWeixin.setOnClickListener(this);
        mRbWeibo = (RadioButton) findViewById(R.id.share_weibo);
        mRbWeibo.setOnClickListener(this);
        mRbFriends = (RadioButton) findViewById(R.id.share_friends);
        mRbFriends.setOnClickListener(this);
        mRbQq = (RadioButton) findViewById(R.id.share_qq);
        mRbQq.setOnClickListener(this);
    }

    private boolean isJustChecked = false;

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.share_weibo:
                if (mRbWeibo.isChecked()) {
                    isJustChecked = true;
                    mShareName = ShareSdk.API_NAME.Weibo;
                }
                break;
            case R.id.share_friends:
                if (mRbFriends.isChecked()) {
                    isJustChecked = true;
                    mShareName = ShareSdk.API_NAME.WXTimeLine;
                }
                break;
            case R.id.share_wechat:
                if (mRbWeixin.isChecked()) {
                    isJustChecked = true;
                    mShareName = ShareSdk.API_NAME.WXSession;
                }
                break;
            case R.id.share_qq:
                if (mRbQq.isChecked()) {
                    isJustChecked = true;
                    mShareName = ShareSdk.API_NAME.QQ;
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_weibo:
            case R.id.share_friends:
            case R.id.share_qq:
            case R.id.share_wechat:
                if (!isJustChecked) {
                    mRGroup.clearCheck();
                    mShareName = null;
                }
                isJustChecked = false;
                break;
            case R.id.close:
                finish();
                break;
            case R.id.btn_start:
                // TOTO 分享
                if (Build.VERSION.SDK_INT <= 17) {
                    Toast.makeText(this, "您的系统版本过低，不支持开直播～", Toast.LENGTH_LONG).show();
                    return;
                }

                initLive();

                break;
        }
    }

    private LiveInitInfo mLiveInitInfo;

    private void initLive() {
        if (!NetworkUtil.checkNetwork(this)) {
            Toast.makeText(StartLiveActivity.this, "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }

        showDialog();
        String title = mEditTitle.getText().toString();
        LiveApi.init(this, title, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<LiveInitInfo>>() {
                }.getType();
                BaseResponse<LiveInitInfo> ret = new Gson().fromJson(data, objectType);

                if (ret != null) {
                    mLiveInitInfo = ret.data;
                    if (mShareName != null) {
                        BaseShareAPI api = ShareSdk.getShareAPI(mShareName, StartLiveActivity.this);
                        doShare(api);
                    } else {
                        gotoLive();
                    }
                }
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {
                Toast.makeText(StartLiveActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFinish() {
                hideDialog();
            }
        });
    }

    private void gotoLive() {
        Intent intent = new Intent(this, MyLiveActivityRtmp.class);
        intent.putExtra("title", mEditTitle.getText().toString());
        intent.putExtra("info", mLiveInitInfo);
        startActivity(intent);
        finish();
    }

    private ShareParam mParam;


    private void doShare(BaseShareAPI api) {

        if (mParam == null) {
            mParam = ShareWindow.buildParam(this, LoginManager.getInstance().getUserInfo().nickname,
                    LoginManager.getInstance().getUserInfo().id, HotItemInfo.live, null);
        }

        api.setCallBackListener(this);
        api.share(mParam, this);
    }


    @Override
    public void callback(ShareResult shareResult) {
        gotoLive();
    }
}
