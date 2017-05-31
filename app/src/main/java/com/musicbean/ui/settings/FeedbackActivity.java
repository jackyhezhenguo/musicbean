package com.musicbean.ui.settings;

import android.os.Bundle;
import android.widget.Toast;

import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.ui.BaseEditActivity;

/**
 * Created by boyo on 16/7/9.
 */
public class FeedbackActivity extends BaseEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("投诉与建议");

        mEditInput.setLines(7);
    }

    @Override
    protected void commit(String str) {
        showDialog();
        UserApi.feedback(this, str, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(FeedbackActivity.this, "感谢您的反馈", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }

            @Override
            public void onFinish() {
                hideDialog();
            }
        });
    }
}
