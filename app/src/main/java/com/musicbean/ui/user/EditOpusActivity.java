package com.musicbean.ui.user;

import android.os.Bundle;

import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BaseEditActivity;

/**
 * Created by boyo on 16/7/9.
 */
public class EditOpusActivity extends BaseEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("代表作");

        mEditInput.setLines(7);
    }

    @Override
    protected void commit(String str) {
        UserInfoBean bean = new UserInfoBean();
        bean.opus = str;

        saveUserInfo(bean);
    }

    @Override
    protected void refreshData() {
        LoginManager.getInstance().getUserCookie().userinfo.opus = mEditInput.getText().toString();
        LoginManager.getInstance().saveUserInfo();
    }
}
