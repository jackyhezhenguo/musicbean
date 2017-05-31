package com.musicbean.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BackActivity;

/**
 * Created by boyo on 16/7/9.
 */
public class EditSexActivity extends BackActivity {

    RadioButton rbMan;
    RadioButton rbWoman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("性别");

        setContentView(R.layout.activity_edit_sex);

        rbMan = (RadioButton) findViewById(R.id.rb_man);
        rbWoman = (RadioButton) findViewById(R.id.rb_woman);

        if (LoginManager.getInstance().getUserInfo().isWoman()) {
            rbWoman.setChecked(true);
        } else {
            rbMan.setChecked(true);
        }

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }

    private void commit() {
        String gender;
        if (rbWoman.isChecked()) {
            gender = "F";
        } else if (rbMan.isChecked()) {
            gender = "M";
        } else {
            Toast.makeText(EditSexActivity.this, "请选择", Toast.LENGTH_SHORT).show();
            return;
        }

        final UserInfoBean bean = new UserInfoBean();
        bean.gender = gender;
        showDialog();
        UserApi.saveInfo(this, bean, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(EditSexActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().getUserCookie().userinfo.gender = bean.gender;
                LoginManager.getInstance().saveUserInfo();
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
