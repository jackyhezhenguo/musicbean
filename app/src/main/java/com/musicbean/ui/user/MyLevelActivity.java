package com.musicbean.ui.user;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.LevelInfo;
import com.musicbean.manager.LevelManager;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BackActivity;

import java.lang.reflect.Type;

/**
 * Created by boyo on 16/7/21.
 */
public class MyLevelActivity extends BackActivity {

    private ImageView mLevelImage;
    private TextView mLevelTxt;
    private TextView mExpTxt;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("我的等级");

        setContentView(R.layout.activity_level);

        mLevelImage = (ImageView) findViewById(R.id.level_image);
        mExpTxt = (TextView) findViewById(R.id.experience_txt);
        mLevelTxt = (TextView) findViewById(R.id.level_txt);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        int level = LoginManager.getInstance().getUserInfo().user_level;
        mLevelImage.setImageResource(LevelManager.getInstance().getLevelBgId(level));
        mLevelTxt.setText("LV." + level);

        requestData();
    }

    private void requestData() {
        UserApi.getLevelInfo(this, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<LevelInfo>>() {
                }.getType();
                BaseResponse<LevelInfo> res = new Gson().fromJson(data, objectType);

                SpannableString ss = new SpannableString(res.data.experence + "/" + res.data.next_experence);
                ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.text_green));
                ss.setSpan(fcs, 0, (res.data.experence + "").length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mExpTxt.setText(ss);
                mProgressBar.setMax(res.data.next_experence);
                mProgressBar.setProgress(res.data.experence);
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }
}
