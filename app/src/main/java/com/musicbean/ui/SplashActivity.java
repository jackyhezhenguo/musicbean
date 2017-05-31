package com.musicbean.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.musicbean.R;
import com.musicbean.manager.GiftManager;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.login.LoginActivity;
import com.musicbean.ui.main.MainActivity;

/**
 * Created by boyo on 16/6/14.
 */
public class SplashActivity extends BaseActivity {

    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        mImage = (ImageView) findViewById(R.id.cover);

        mImage.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (LoginManager.getInstance().isLogin()) {
                    Log.d("11","is");
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 0);

    }


}
