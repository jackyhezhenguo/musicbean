package com.musicbean.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.http.bean.UserPageInfo;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.user.BaseUserListActivity;
import com.musicbean.ui.user.UserListAdapter;

import java.lang.reflect.Type;

/**
 * Created by boyo on 16/7/13.
 */
public class NotificationActivity extends BaseUserListActivity implements CompoundButton.OnCheckedChangeListener {

    private CheckBox mCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("消息提醒");

        View v = LayoutInflater.from(this).inflate(R.layout.notification_header, mListView, false);
        ViewGroup root = (ViewGroup) findViewById(R.id.root);
        root.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mCheckbox = (CheckBox) findViewById(R.id.notification_switcher);
        mCheckbox.setChecked(LoginManager.getInstance().getUserInfo().is_remind == 1);
        mLoadMore.setVisibility(mCheckbox.isChecked() ? View.VISIBLE : View.INVISIBLE);
        mCheckbox.setOnCheckedChangeListener(this);
    }

    @Override
    protected UserListAdapter getAdapter() {
        return new NotificationListAdapter();
    }

    @Override
    protected void requestData() {
        UserApi.getMyRemindList(this, start, count, mResponseHandler);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mLoadMore.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);

        UserApi.saveRemind(this, isChecked ? 1 : 0, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<UserInfoBean>>() {
                }.getType();
                BaseResponse<UserInfoBean> res = new Gson().fromJson(data, objectType);

                LoginManager.getInstance().setUserInfo(res.data);
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }
}
