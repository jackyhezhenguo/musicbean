package com.musicbean.ui.user;

import android.os.Bundle;
import android.view.View;

import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BaseEditActivity;

/**
 * Created by boyo on 16/7/9.
 */
public class EditStudioActivity extends BaseEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("设置工作室名称");

        mEditInput.setLines(1);
        mTips.setVisibility(View.VISIBLE);
        mTips.setText("工作室名称是唯一的，只可以设置一次哦!");
    }

    @Override
    protected void commit(String str) {
        UserInfoBean bean = new UserInfoBean();
        bean.studio = str;

        saveUserInfo(bean);
    }

    @Override
    protected void refreshData() {
        LoginManager.getInstance().getUserCookie().userinfo.studio = mEditInput.getText().toString();
        LoginManager.getInstance().saveUserInfo();
    }
}
