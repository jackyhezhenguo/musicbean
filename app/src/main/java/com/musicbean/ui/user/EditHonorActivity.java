package com.musicbean.ui.user;

import android.os.Bundle;

import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BaseEditActivity;

/**
 * Created by boyo on 16/7/9.
 */
public class EditHonorActivity extends BaseEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("所获荣誉");

        mEditInput.setLines(7);
    }

    @Override
    protected void commit(String str) {
        UserInfoBean bean = new UserInfoBean();
        bean.honor = str;
        saveUserInfo(bean);
    }

    @Override
    protected void refreshData() {
        LoginManager.getInstance().getUserCookie().userinfo.honor = mEditInput.getText().toString();
        LoginManager.getInstance().saveUserInfo();
    }
}
