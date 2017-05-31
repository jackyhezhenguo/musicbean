package com.musicbean.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.musicbean.R;
import com.musicbean.http.api.UserApi;
import com.musicbean.ui.BackActivity;
import com.musicbean.ui.search.SearchActivity;

/**
 * Created by boyo on 16/7/8.
 */
public class ConcernListActivity extends BaseUserListActivity implements BackActivity.TitleBarRightButtonCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("关注");

        setRightButtonDrawable(R.drawable.icon_add_friend);
        setCallback(this);
    }

    @Override
    public void onRightButtonStatusChange(CompoundButton button, boolean status) {

    }

    @Override
    public void onRightButtonClick(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    @Override
    protected void requestData() {
        UserApi.getConcernList(this, mUid, start, count, mResponseHandler);
    }

}
