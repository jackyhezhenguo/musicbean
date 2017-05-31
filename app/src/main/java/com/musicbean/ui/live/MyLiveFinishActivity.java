package com.musicbean.ui.live;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.musicbean.R;
import com.musicbean.ui.BaseActivity;
import com.musicbean.ui.main.MainActivity;

/**
 * Created by boyo on 16/7/7.
 */
public class MyLiveFinishActivity extends BaseActivity {

    private TextView mFinishInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_finish_live);

        findViewById(R.id.btn_goto_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyLiveFinishActivity.this, MainActivity.class));
            }
        });

        mFinishInfo = (TextView) findViewById(R.id.finish_info);

        String users = getIntent().getStringExtra("onlines");
        String beans = getIntent().getStringExtra("beans");
        setView(users, beans);
    }

    private void setView(String users, String beans) {
        String str = null;
        if (!TextUtils.isEmpty(beans)) {
            str = getResources().getString(R.string.live_finish_info, users, beans);
        } else {
            str = getResources().getString(R.string.live_finish_info_short, users);
        }
        SpannableString ss = new SpannableString(str);
        ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.text_light_green));
        ss.setSpan(fcs, 0, users.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(beans)) {
            fcs = new ForegroundColorSpan(getResources().getColor(R.color.text_light_green));
            ss.setSpan(fcs, 6 + users.length(), 6 + users.length() + beans.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        mFinishInfo.setText(ss);
    }
}
