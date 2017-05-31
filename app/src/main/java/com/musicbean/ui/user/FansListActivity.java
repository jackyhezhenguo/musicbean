package com.musicbean.ui.user;

import android.os.Bundle;

import com.musicbean.http.api.UserApi;

/**
 * Created by boyo on 16/7/8.
 */
public class FansListActivity extends BaseUserListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("粉丝");
    }

    @Override
    protected void requestData() {
        UserApi.getFollowList(this, mUid, start, count, 0, mResponseHandler);
    }
}
