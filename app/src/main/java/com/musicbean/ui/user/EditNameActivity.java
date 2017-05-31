package com.musicbean.ui.user;

import android.os.Bundle;
import android.text.InputFilter;

import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BaseEditActivity;

/**
 * Created by boyo on 16/7/9.
 */
public class EditNameActivity extends BaseEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("修改昵称");

        mEditInput.setLines(1);
        mEditInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
    }

    @Override
    protected void commit(String str) {
        UserInfoBean bean = new UserInfoBean();
        bean.nickname = str;

        saveUserInfo(bean);
    }

    @Override
    protected void refreshData() {
        LoginManager.getInstance().getUserCookie().userinfo.nickname = mEditInput.getText().toString();
        LoginManager.getInstance().saveUserInfo();
    }
}
