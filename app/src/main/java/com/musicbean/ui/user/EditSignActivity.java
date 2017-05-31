package com.musicbean.ui.user;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;

import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BaseEditActivity;

/**
 * Created by boyo on 16/7/9.
 */
public class EditSignActivity extends BaseEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("修改个性签名");

        mEditInput.setLines(3);
        mEditInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        mTips.setVisibility(View.VISIBLE);
        mTips.setText("内容不超过32个字");
    }

    @Override
    protected void commit(String str) {
        UserInfoBean bean = new UserInfoBean();
        bean.spec_sign = str;

        saveUserInfo(bean);
    }

    @Override
    protected void refreshData() {
        LoginManager.getInstance().getUserCookie().userinfo.spec_sign = mEditInput.getText().toString();
        LoginManager.getInstance().saveUserInfo();
    }
}
