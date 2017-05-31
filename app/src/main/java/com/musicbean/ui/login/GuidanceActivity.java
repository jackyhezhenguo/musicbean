package com.musicbean.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.HttpUtil;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.BoardInfo;
import com.musicbean.ui.SplashActivity;
import com.musicbean.util.VolleyLoadPicture;
import com.musicbean.widget.BannerView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GuidanceActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String FIRST_VIEW_PAGE = "FIRST_VIEW_PAGE";
    private static final String FIRST_VISIT = "FIRST_VISIT";

    private SharedPreferences settings;

    private Handler mHandler;
    private Runnable mRunnable;
    private TextView tvTime;
    private ImageView imageView;
    private int time = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidance);

        tvTime = (TextView) findViewById(R.id.time);
        imageView = (ImageView) findViewById(R.id.imageView);
        tvTime.setOnClickListener(this);

        mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                tvTime.setText("跳过（" +  time-- + "）");
                if (time != -2) {
                    mHandler.postDelayed(this, 1000);
                }else {
                    tvTime.setVisibility(View.INVISIBLE);
                    startTheActivity();
                }
            }
        };

        mHandler.postDelayed(mRunnable,0);

        settings = getSharedPreferences(FIRST_VIEW_PAGE, 0);


        HttpUtil.getInstance().get(this, "http://musicbean-app-server-dev.obaymax.com/start/onBoarding", null, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Log.d("11",data);
                Type objectType = new TypeToken<BaseResponse<BoardInfo>>() {
                }.getType();
                BaseResponse<BoardInfo> ret = new Gson().fromJson(data, objectType);
                Log.d("11",ret.data.id);
                Log.d("11",ret.data.img);
                //用Volley加载图片
                VolleyLoadPicture vlp = new VolleyLoadPicture(GuidanceActivity.this, imageView);
                vlp.getmImageLoader().get(ret.data.img, vlp.getOne_listener());

            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time:
                startTheActivity();
                break;
        }
    }

    private void startTheActivity() {
        if (0 == settings.getInt(FIRST_VISIT,0)) {
            GuidanceActivity.this.startActivity(new Intent(GuidanceActivity.this, ViewPageActivity.class));
            GuidanceActivity.this.finish();
        } else {
            GuidanceActivity.this.startActivity(new Intent(GuidanceActivity.this, SplashActivity.class));
            GuidanceActivity.this.finish();
        }
    }


}
